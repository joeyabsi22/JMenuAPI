package me.jmang.japi.jmenuapi.actions;

import me.jmang.japi.jmenuapi.Icon;
import me.jmang.japi.jmenuapi.Menu;
import me.jmang.japi.jmenuapi.Page;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuClick {

    private final @NotNull Player player;
    private final @NotNull Page page;
    private final int slot;
    private final @NotNull ClickType click;
    private final @Nullable ItemStack cursor;

    public MenuClick(@NotNull Player player,
                     @NotNull Page page,
                     int slot, @NotNull ClickType click,
                     @Nullable ItemStack cursor) {
        this.player = player;
        this.page = page;
        this.slot = slot;
        this.click = click;
        this.cursor = cursor;
    }

    public @NotNull Player getWhoClicked() {
        return player;
    }

    public @NotNull Page getPage() {
        return page;
    }

    // rename to index???
    public int getSlot() {
        return slot;
    }

    public @Nullable Icon getClickedIcon() {
        return page.getIcon(slot);
    }

    public @NotNull ClickType getClick() {
        return click;
    }

    public boolean isLeftClick() {
        return click.isLeftClick();
    }

    public boolean isRightClick() {
        return click.isRightClick();
    }

    public boolean isShiftClick() {
        return click.isShiftClick();
    }

    public boolean isKeyboardClick() {
        return click.isKeyboardClick();
    }

    public @Nullable ItemStack getCursor() {
        return cursor;
    }

    public boolean cursorHasItem() {
        return cursor != null;
    }
}
