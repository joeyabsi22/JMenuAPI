package me.jmang.japi.jmenuapi;

/** Options which can be applied to a menu.
 */
public enum MenuOption {

    /** Allows players to interact with their personal inventory while having the menu open.
     */
    ALLOW_INVENTORY_PICKUP,

    /** Clears the personal inventories of players while having the menu open.
     */
    TEMPORARILY_CLEAR_INVENTORY,

    /** Closes the menu if no players are connected after disconnecting a player with {@code removePlayer()}.
     */
    CLOSE_ON_NO_PLAYERS_CONNECTED,

    /** Closes the menu if every connected player has {@code null} as their current page
     * after a {@code ManualMenuClose} happens.
     */
    CLOSE_ON_NO_CONNECTED_PLAYER_HAS_OPEN_PAGE,

    /** Wraps the pages when {@code nextPage()} or {@code previousPage()} is called on the menu.
     *
     */
    WRAP_ON_FLIP_PAGE,
}
