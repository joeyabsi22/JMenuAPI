package me.jmang.japi.jmenuapi;

import me.jmang.japi.jmenuapi.actions.ManualMenuClose;
import me.jmang.japi.jmenuapi.actions.MenuClick;
import me.jmang.japi.jmenuapi.events.MenuCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/** A menu which can be opened and viewed by players ingame.
 *
 */
public abstract class Menu implements Listener {

    private static final String badPlayerMessage = "The player provided does not have a connection to this menu.";
    private static final String badPageMessage = "The page provided does not belong to this menu.";

    private static final List<@NotNull MenuOption> defaultOptions = new ArrayList<>(Arrays.asList(
        MenuOption.CLOSE_ON_NO_PLAYERS_CONNECTED
    ));

    private final ArrayList<@NotNull Page> pages = new ArrayList<>();
    private final HashMap<@NotNull MenuSession, @Nullable Page> sessions = new HashMap<>();
    private final HashSet<@NotNull MenuOption> options = new HashSet<>();

    /** Creates a menu with the default options.
     * The menu is automatically registered as a listener to the server.
     */
    public Menu() {
        init();
        this.options.addAll(defaultOptions);
    }

    /** Creates a menu which overrides the default options with the provided options.
     * The menu is automatically registered as a listener to the server.
     * @param options The options to be applied
     */
    public Menu(@NotNull MenuOption... options) {
        init();
        this.options.addAll(Arrays.asList(options));
    }

    /** Creates a menu which gives the option to override the default options with the provided options.
     * The menu is automatically registered as a listener to the server.
     * @param overrideDefaultOptions If {@code true}, the default options will be overridden. If {@code false}, the
     *                               default options will be kept along with the provided ones.
     * @param options The options to be applied
     */
    public Menu(boolean overrideDefaultOptions, @NotNull MenuOption... options) {
        init();
        if (!overrideDefaultOptions) this.options.addAll(defaultOptions);
        this.options.addAll(Arrays.asList(options));
    }

    private void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, JMenuAPI.instance);
        Bukkit.getServer().getPluginManager().callEvent(new MenuCreateEvent(this));
    }

    /** Disconnects all players and removes all pages from the menu.
     * The menu is also removed from the server as a listener, so it will no longer listen for events.
     */
    public void close() {
        for (MenuSession session : sessions.keySet()) session.removeMenu(this);
        sessions.clear();
        pages.clear();
        HandlerList.unregisterAll(this);
    }

    /** Calls {@code close()} if the players in the menu all have {@code null} as their current page.
     * @return {@code true} if the menu was closed
     */
    public boolean closeIfNoOpenPages() {
        for (Page page : sessions.values()) if (page != null) return false;
        close();
        return true;
    }

    /** Calls {@code close()} if no players are connected to the menu.
     * @return {@code true} if the menu was closed
     */
    public boolean closeIfNoPlayersConnected() {
        if (sessions.size() != 0) return false;
        close();
        return true;
    }

    /** Called when the menu is clicked by a player.
     * This should not be called manually.
     * @param click The click from a player
     */
    public abstract void onClick(MenuClick click);

    /** Called when the menu is manually closed by a player, either by pressing "e" or "esc" or by disconnecting.
     * @param close The close from a player
     */
    public abstract void onManualClose(ManualMenuClose close);

    void onClickInternal(MenuClick click) {
        onClick(click);
    }

    void onManualCloseInternal(ManualMenuClose close) {
        onManualClose(close);
        if (options.contains(MenuOption.CLOSE_ON_NO_CONNECTED_PLAYER_HAS_OPEN_PAGE)) closeIfNoOpenPages();
    }

    /** Returns an immutable list of pages which belong to the menu.
     * @return The list pages
     */
    public @NotNull ArrayList<@NotNull Page> getPages() {
        return new ArrayList<>(pages);
    }

    /** Adds a page to the menu.
     * @param pages The pages to be added
     */
    public void addPage(@NotNull Page... pages) {
        this.pages.addAll(Arrays.asList(pages));
    }

    /** Removes a page from the menu.
     *
     * @param page The page to be removed
     * @return {@code true} if the page was removed
     */
    public boolean removePage(@NotNull Page page) {
        if (pages.contains(page)) {
            if (sessions.containsValue(page)) for (MenuSession session : sessions.keySet()) {
                if (sessions.get(session) == page) sessions.replace(session, null);
                session.update();
            }
            while (pages.contains(page)) pages.remove(page);
            return true;
        } else return false;
    }

    /** Returns an immutable list of players which are connected to the menu.
     * @return The list of players
     */
    public @NotNull ArrayList<@NotNull Player> getPlayers() {
        final ArrayList<Player> players = new ArrayList<>();
        for (MenuSession session : sessions.keySet()) players.add(session.getPlayer());
        return players;
    }

    /** Connects a player to the menu <b>without setting it as their current menu</b>.
     *
     * @param players The players to be added
     */
    public void addPlayer(@NotNull Player... players) {
        for (Player player : players) {
            sessions.put(MenuSession.getSession(player), null);
            MenuSession.getSession(player).addMenu(this);
        }
    }

    /** Connects a player to the menu <b>and simultaneously sets the menu as their current menu</b>.
     *
     * @param player The player to be added
     * @param startingPage
     */
    public void addPlayer(@NotNull Player player, @Nullable Page startingPage) {
        if (startingPage != null && !pages.contains(startingPage)) pages.add(startingPage);
        sessions.put(MenuSession.getSession(player), startingPage);
        MenuSession.getSession(player).setCurrentMenu(this);
    }

    /** Disconnects a player to the menu.
     * If the menu is the player's current menu, their current menu becomes {@code null}.
     * @param player The player to be removed
     * @return {@code true} if the player was removed
     */
    public boolean removePlayer(@NotNull Player player) {
        if (sessions.containsKey(MenuSession.getSession(player))) {
            sessions.remove(MenuSession.getSession(player));
            MenuSession.getSession(player).removeMenu(this);
            if (options.contains(MenuOption.CLOSE_ON_NO_PLAYERS_CONNECTED) && sessions.size() == 0) close();
            return true;
        } else return false;
    }

    /** Returns the current page of a player connected to the menu.
     * @param player The player
     * @return The current page the player has open
     * @throws IllegalArgumentException If the player has no connection to the menu
     */
    public @Nullable Page getCurrentPage(@NotNull Player player) throws IllegalArgumentException {
        if (sessions.containsKey(MenuSession.getSession(player)))
            return this.sessions.get(MenuSession.getSession(player));
        else throw new IllegalArgumentException(badPlayerMessage);
    }

    /** Sets the current page of all players connected to the menu.
     * @param page The page
     */
    public void setAllCurrentPages(@Nullable Page page) {
        if (page != null && !pages.contains(page)) pages.add(page);
        for (MenuSession session : sessions.keySet()) {
            sessions.replace(session, page);
            session.update();
        }
    }

    /** Sets the current page of a player connected to this menu.
     * If the player is not connected to the menu, they are automatically connected.
     * @param player The player
     * @param page The page
     */
    public void setCurrentPage(@NotNull Player player, @Nullable Page page) {
        if (page != null && !pages.contains(page)) pages.add(page);
        MenuSession targetSession = MenuSession.getSession(player);
        if (!sessions.containsKey(targetSession)) addPlayer(player, page);
        else {
            sessions.replace(targetSession, page);
            targetSession.update();
        }
    }

    /**
     * @param players
     * @throws IllegalArgumentException
     */
    public void nextPage(@NotNull Player... players) throws IllegalArgumentException {
        boolean badPlayerProvided = false;
        for (Player player : players) try {flipPage(player, true);}
        catch (IllegalArgumentException e) {badPlayerProvided = true;}
        if (badPlayerProvided) throw new IllegalArgumentException(badPlayerMessage);
    }

    public void previousPage(@NotNull Player... players) throws IllegalArgumentException {
        boolean badPlayerProvided = false;
        for (Player player : players) try {flipPage(player, false);}
        catch (IllegalArgumentException e) {badPlayerProvided = true;}
        if (badPlayerProvided) throw new IllegalArgumentException(badPlayerMessage);
    }

    private void flipPage(@NotNull Player player, boolean fob) throws IllegalArgumentException {
        MenuSession targetSession = MenuSession.getSession(player);
        if (sessions.containsKey(targetSession)) {
            if (sessions.get(targetSession) == null) return;
            Page newPage;
            try {
                if (fob) newPage = pages.get(pages.indexOf(sessions.get(targetSession)) + 1);
                else newPage = pages.get(pages.indexOf(sessions.get(targetSession)) - 1);
            } catch (IndexOutOfBoundsException e) {
                if (options.contains(MenuOption.WRAP_ON_FLIP_PAGE)){
                    if (fob) newPage = pages.get(0);
                    else newPage = pages.get(pages.size() - 1);
                }
                else newPage = sessions.get(targetSession);
            }
            sessions.replace(targetSession, newPage);
            targetSession.update();
        } else throw new IllegalArgumentException(badPlayerMessage);
    }

    @NotNull HashSet<@NotNull MenuOption> getOptions() {
        return options;
    }
}
