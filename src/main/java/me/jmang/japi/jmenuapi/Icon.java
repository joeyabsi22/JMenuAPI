package me.jmang.japi.jmenuapi;

import me.jmang.japi.jmenuapi.actions.ManualMenuClose;
import me.jmang.japi.jmenuapi.actions.MenuClick;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class Icon {

    private static final int defaultStackSize = 1;

    private static final String badStackSizeMessage = "The amount provided is not valid.";

    private final @NotNull ItemStack itemStack;
    private final ArrayList<@NotNull String> tags = new ArrayList<>();

    public Icon(@NotNull Material material) {
        this.itemStack = new ItemStack(material, defaultStackSize);
    }

    public Icon(@NotNull Material material, int amount) throws IllegalArgumentException {
        this.itemStack = new ItemStack(material, defaultStackSize);
        setAmount(amount);
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    public @NotNull String getName() {
        return getMeta().getDisplayName();
    }

    public void setName(@NotNull String name) {
        ItemMeta meta = getMeta();
        meta.setDisplayName(name);
        setMeta(meta);
    }

    public @NotNull Material getMaterial() {
        return itemStack.getType();
    }

    public void setMaterial(@NotNull Material material) {
        itemStack.setType(material);
    }

    public int getAmount() {
        return itemStack.getAmount();
    }

    public void setAmount(int amount) throws IllegalArgumentException {
        if (amount >= 0 && amount <= Page.maxStackSize) itemStack.setAmount(amount);
        else throw new IllegalArgumentException(badStackSizeMessage);
    }

    private @NotNull ItemMeta getMeta() {
        assert itemStack.getItemMeta() != null;
        return itemStack.getItemMeta();
    }

    private void setMeta(@NotNull ItemMeta meta) {
        itemStack.setItemMeta(meta);
    }
}
