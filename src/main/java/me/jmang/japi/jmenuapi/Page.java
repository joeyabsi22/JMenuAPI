package me.jmang.japi.jmenuapi;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/** A page which represents an ingame inventory to be opened through a menu.
 *
 */
public abstract class Page implements Iterable<@Nullable Icon> {

    private static final InventoryHolder defaultHolder = null;
    static final int maxStackSize = 127;

    private final @NotNull Inventory inventory;
    private @Nullable Icon[] contents;
    private volatile @NotNull String title = "";

    private Page(@NotNull InventoryType type) {
        this.inventory = Bukkit.createInventory(defaultHolder, type, title);
        initContents();
    }

    private Page(@NotNull InventoryType type, @NotNull String title) {
        this.inventory = Bukkit.createInventory(defaultHolder, type, title);
        this.title = title;
        initContents();
    }

    private Page(int rows) throws IllegalArgumentException {
        this.inventory = Bukkit.createInventory(defaultHolder, rows * 9, title);
        initContents();
    }

    private Page(int rows, @NotNull String title) throws IllegalArgumentException {
        this.inventory = Bukkit.createInventory(defaultHolder, rows * 9, title);
        this.title = title;
        initContents();
    }

    private void initContents() {
        contents = new Icon[inventory.getSize()];
    }

    final @NotNull Inventory getInventory() {
        return this.inventory;
    }

    /** Returns the title of the page.
     *
     * @return The title
     */
    public @NotNull String getTitle() {
        return this.title;
    }

    /** Returns the raw title of the page with no chat colors.
     *
     * @return The title
     */
    public @NotNull String getTitleWithoutColor() {
        return ChatColor.RESET + this.title;
    }

    /** Sets the title of the page.
     *
     * @param title
     */
    public void setTitle(@NotNull String title) {
        this.title = title;
        updateTitle();
    }

    final void updateTitle() {
        for (HumanEntity viewer : this.inventory.getViewers()) updateTitle(viewer);
    }

    final void updateTitle(@NotNull HumanEntity viewer) {

        EntityPlayer player = ((CraftPlayer) viewer).getHandle();
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(player.bV.j,
                player.bV.getType(),
                new ChatMessage(title));
        player.b.sendPacket(packet);
        player.initMenu(player.bV);
    }

    /** Iterates over the contents of the page.
     *
     */
    @Override
    public @NotNull Iterator<@Nullable Icon> iterator() {
        return Arrays.stream(contents.clone()).iterator();
    }

    /** Returns the contents of the page.
     *
     * @return The contents
     */
    public @Nullable Icon[] getContents() {
        return contents.clone();
    }

    /** Replaces the contents of the page. The new contents provided must be an array with the same size as the number
     * of slots in the page.
     * @param contents The new contents to replace the current contents
     * @throws IllegalArgumentException If the length of the contents provided are not the same as the number of
     * slots in the page.
     */
    public void setContents(@Nullable Icon[] contents) throws IllegalArgumentException {
        if (contents.length == this.contents.length) {
            this.contents = contents;
            updateContents();
        } else throw new IllegalArgumentException(); //add message
    }

    /** Clears the contents of the page.
     *
     */
    public void clearContents() {
        setContents(new Icon[contents.length]);
    }

    /** Returns the number of slots in the page.
     *
     * @return The number of slots
     */
    public int getSlots() {
        return contents.length;
    }

    /** Returns the icon at the given index in the contents of the page.
     *
     * @param index The index in the contents
     * @return The icon
     * @throws IndexOutOfBoundsException
     */
    public @Nullable Icon getIcon(int index) {
        return contents[index % contents.length];
    }

    /** Sets the icon at the given index in the contents of the page.
     *
     * @param index The index in the contents
     * @param icon The icon
     * @throws IndexOutOfBoundsException
     */
    public void setIcon(int index, @Nullable Icon icon) {
        index = index % contents.length;
        contents[index] = icon;
        updateIndex(index);
    }

    /** Clears the icon at the given index in the contents of the page.
     * This is the same as {@code setIcon(index, null)}.
     * @param index The index in the contents
     * @throws IndexOutOfBoundsException
     */
    public void clearIcon(int index) {
        index = index % contents.length;
        contents[index] = null;
        updateIndex(index);
    }

    public @NotNull ArrayList<@NotNull Integer> fillEmptySlots(@NotNull Icon icon) {
        ArrayList<Integer> filledSlots = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) if (contents[i] == null) {
            contents[i] = icon;
            filledSlots.add(i);
            updateIndex(i);
        }
        return filledSlots;
    }

    public @NotNull HashMap<@NotNull Icon, @Nullable Integer> addIcon(@NotNull Icon... icons) {
        HashMap<@NotNull Icon, @Nullable Integer> filledSlots = new HashMap<>();
        for (Icon icon : icons) {
            filledSlots.put(icon, null);
            for (int i = 0; i < contents.length; i++) if (contents[i] == null) {
                contents[i] = icon;
                filledSlots.replace(icon, i);
                updateIndex(i);
                break;
            }
        }
        return filledSlots;
    }

    private void updateContents() {
        for (int i = 0; i < contents.length; i++) updateIndex(i);
    }

    private void updateIndex(int index) {
        inventory.setMaxStackSize(maxStackSize);
        if (contents[index] == null) inventory.setItem(index, null);
        else inventory.setItem(index, contents[index].getItemStack());
    }

    public static class Chest extends GriddablePage {

        private static final int cols = 9;
        private static final int defaultRows = 3;
        private static final int maxRows = 6;

        public Chest() {
            super(defaultRows, cols);
        }

        public Chest(@NotNull String title) {
            super(defaultRows, cols, title);
        }

        public Chest(int rows) throws IllegalArgumentException {
            super(rows, cols);
        }

        public Chest(int rows, @NotNull String title) throws IllegalArgumentException {
            super(rows, cols, title);
        }

        public Chest(@NotNull Icon... icons) {
            super(defaultRows, cols);
            addIcon(icons);
        }

        public Chest(@NotNull String title, @NotNull Icon... icons) {
            super(defaultRows, cols, title);
            addIcon(icons);
        }

        public Chest(int rows, @NotNull Icon... icons) {
            super(rows, cols);
            addIcon(icons);
        }

        public Chest(int rows, @NotNull String title, @NotNull Icon... icons) {
            super(rows, cols, title);
            addIcon(icons);
        }
    }

    public static class Dispenser extends GriddablePage {

        private static final int rows = 3;
        private static final InventoryType type = InventoryType.DISPENSER;

        public Dispenser() {
            super(type, rows, rows);
        }

        public Dispenser(@NotNull String title) {
            super(type, rows, rows, title);
        }

        public Dispenser(@NotNull Icon... icons) {
            super(type, rows, rows);
            addIcon(icons);
        }

        public Dispenser(@NotNull String title, @NotNull Icon... icons) {
            super(type, rows, rows, title);
            addIcon(icons);
        }
    }

    public static class Hopper extends Page {

        private static final InventoryType type = InventoryType.HOPPER;

        public Hopper() {
            super(type);
        }

        public Hopper(@NotNull String title) {
            super(type, title);
        }

        public Hopper(@NotNull Icon... icons) {
            super(type);
            addIcon(icons);
        }

        public Hopper(@NotNull String title, @NotNull Icon... icons) {
            super(type, title);
            addIcon(icons);
        }
    }

    private static abstract class GriddablePage extends Page {

        private final int rows;
        private final int cols;

        private GriddablePage(int rows, int cols) throws IllegalArgumentException{
            super(rows);
            this.rows = rows;
            this.cols = cols;
        }

        private GriddablePage(int rows, int cols, @NotNull String title) throws IllegalArgumentException {
            super(rows, title);
            this.rows = rows;
            this.cols = cols;
        }

        private GriddablePage(@NotNull InventoryType type, int rows, int cols) throws IllegalArgumentException {
            super(type);
            this.rows = rows;
            this.cols = cols;
        }

        private GriddablePage(@NotNull InventoryType type, int rows, int cols, @NotNull String title)
                throws IllegalArgumentException {
            super(type, title);
            this.rows = rows;
            this.cols = cols;
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        public @Nullable Icon getIcon(int row, int col) {
            return getIcon(getIndexFromRowCol(row, col));
        }

        public void setIcon(int row, int col, @Nullable Icon icon) {
            setIcon(getIndexFromRowCol(row, col), icon);
        }

        public void setIcon(PageLocation location, @Nullable Icon icon) {
            setIcon(location.getIndex(rows, cols), icon);
        }

        public void clearIcon(int row, int col) {
            clearIcon(getIndexFromRowCol(row, col));
        }

        private int getIndexFromRowCol(int row, int col) {
            row  = row % rows;
            col = col % cols;
            return row * cols + col;
        }
    }
}
