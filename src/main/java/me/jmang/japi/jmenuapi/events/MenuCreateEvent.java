package me.jmang.japi.jmenuapi.events;

import me.jmang.japi.jmenuapi.Menu;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MenuCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final @NotNull Menu menu;

    public MenuCreateEvent(@NotNull Menu menu) {
        this.menu = menu;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public @NotNull Menu getMenu() {
        return menu;
    }
}
