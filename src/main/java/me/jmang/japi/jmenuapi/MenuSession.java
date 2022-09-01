package me.jmang.japi.jmenuapi;

import me.jmang.japi.jmenuapi.actions.ManualMenuClose;
import me.jmang.japi.jmenuapi.actions.MenuClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

final class MenuSession implements Listener {

    private static final int joinUpdateDelay = 3;

    private static final HashMap<@NotNull Player, @NotNull MenuSession> sessions = new HashMap<>();
    private static final ItemStack[] idleTemporaryInventory = new ItemStack[0];

    private @NotNull final Player player;
    private @Nullable ItemStack[] temporaryInventory = idleTemporaryInventory;
    private final ArrayList<@NotNull Menu> menus = new ArrayList<>();
    private @Nullable Menu currentMenu;

    private MenuSession(@NotNull Player player) {
        Bukkit.getServer().getPluginManager().registerEvents(this, JMenuAPI.instance);
        this.player = player;
    }

    static @NotNull MenuSession getSession(@NotNull Player player) {
        if (!sessions.containsKey(player)) sessions.put(player, new MenuSession(player));
        return sessions.get(player);
    }

    @NotNull Player getPlayer() {return player;}

    @NotNull ArrayList<Menu> getMenus() {return menus;}

    void addMenu(@NotNull Menu... menus) {
        for (Menu menu : menus) if (!this.menus.contains(menu)) this.menus.add(menu);
    }

    void removeMenu(@NotNull Menu menu) {
        if (menu == currentMenu) currentMenu = null;
        menus.remove(menu);
        update();
        if (menus.size() == 0 && currentMenu == null) {
            sessions.remove(player);
            HandlerList.unregisterAll(this);
        }
    }

    @Nullable Menu getCurrentMenu() {
        return currentMenu;
    }

    void setCurrentMenu(@Nullable Menu menu) {
        if (menu != null && !menus.contains(menu)) menus.add(menu);
        currentMenu = menu;
        update();
    }

    void closeCurrentMenu() {
        currentMenu = null;
        update();
    }

    void update() {
        if (!player.isOnline()) return;
        if (checkCurrentMenu()) {
            player.closeInventory();
            updateTemporaryInventory(false);
        }
        else {
            updateTemporaryInventory(true);
            assert currentMenu != null;
            Page currentPage = currentMenu.getCurrentPage(player);
            assert currentPage != null;
            player.openInventory(currentPage.getInventory());
        }
    }

    private boolean checkCurrentMenu() {
        return currentMenu == null || currentMenu.getCurrentPage(player) == null;
    }

    private boolean checkInventoryEvent(@NotNull InventoryEvent event) {
        assert currentMenu != null;
        Page currentPage = currentMenu.getCurrentPage(player);
        assert currentPage != null;
        return !event.getView().getTopInventory().equals(currentPage.getInventory());
    }

    private void updateTemporaryInventory(boolean isOpen) {
        assert !isOpen || currentMenu != null;

        if (isOpen && temporaryInventory.length == 0) {
            if (!currentMenu.getOptions().contains(MenuOption.TEMPORARILY_CLEAR_INVENTORY)) return;
            temporaryInventory = player.getInventory().getContents();
            player.getInventory().clear();
        }
        else if (temporaryInventory.length != 0) {
            player.getInventory().setContents(temporaryInventory);
            temporaryInventory = idleTemporaryInventory;
        }
    }

    @EventHandler
    private void onJoin(@NotNull PlayerJoinEvent event) {
        if (event.getPlayer().equals(player))
            Bukkit.getScheduler().runTaskLater(JMenuAPI.getInstance(), this::update, joinUpdateDelay);
    }

    @EventHandler
    private void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!event.getWhoClicked().equals(player)) return;
        if (checkCurrentMenu()) return;
        if (checkInventoryEvent(event)) return;

        assert currentMenu != null;
        Page currentPage = currentMenu.getCurrentPage(player);
        assert currentPage != null;

        if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
            event.setCancelled(true);
            MenuClick menuClick = new MenuClick(
                    player,
                    currentPage,
                    event.getSlot(),
                    event.getClick(),
                    event.getCursor());
            Icon clickedIcon = currentPage.getIcon(event.getSlot());
            if (clickedIcon instanceof FIcon) ((FIcon) clickedIcon).onClick(menuClick);
            else currentMenu.onClick(menuClick);
        } else if (event.getView().getBottomInventory().equals(event.getClickedInventory())
                && !currentMenu.getOptions().contains(MenuOption.ALLOW_INVENTORY_PICKUP))
            event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        if (!event.getPlayer().equals(player)) return;

        Bukkit.getScheduler().runTask(JMenuAPI.getInstance(), () -> {
            if (checkCurrentMenu()) return;
            if (checkInventoryEvent(event)) return;

            assert currentMenu != null;
            Page currentPage = currentMenu.getCurrentPage(player);
            assert currentPage != null;
            currentPage.updateTitle(player);
        });
    }

    @EventHandler
    private void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (!event.getPlayer().equals(player)) return;
        if (checkCurrentMenu()) return;
        if (checkInventoryEvent(event)) return;

        Bukkit.getScheduler().runTask(JMenuAPI.getInstance(), () -> {
            assert currentMenu != null;
            Page closedPage = currentMenu.getCurrentPage(player);
            currentMenu.setCurrentPage(player, null);
            assert closedPage != null;
            currentMenu.onManualCloseInternal(new ManualMenuClose(player, currentMenu, closedPage, !player.isOnline()));
        });
    }
}
