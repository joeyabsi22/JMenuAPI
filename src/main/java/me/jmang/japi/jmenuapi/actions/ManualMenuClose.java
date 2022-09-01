package me.jmang.japi.jmenuapi.actions;

import me.jmang.japi.jmenuapi.Menu;
import me.jmang.japi.jmenuapi.Page;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ManualMenuClose {

    private final @NotNull Player player;
    private final @NotNull Menu menu;
    private final @NotNull Page page;
    private final boolean isDisconnect;

    public ManualMenuClose(@NotNull Player player, @NotNull Menu menu, @NotNull Page page, boolean isDisconnect) {
        this.player = player;
        this.menu = menu;
        this.page = page;
        this.isDisconnect = isDisconnect;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Page getPage() {
        return page;
    }

    public boolean isDisconnect() {
        return isDisconnect;
    }

    public boolean reopenLastPage() {
        if (isDisconnect) return false;
        menu.addPlayer(player, page);
        return true;
    }
}
