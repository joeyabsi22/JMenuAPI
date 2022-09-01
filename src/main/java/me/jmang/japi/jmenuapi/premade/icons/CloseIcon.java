package me.jmang.japi.jmenuapi.premade.icons;

import me.jmang.japi.jmenuapi.FIcon;
import me.jmang.japi.jmenuapi.Menu;
import me.jmang.japi.jmenuapi.actions.MenuClick;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class CloseIcon extends FIcon {

    private static final Material defaultMaterial = Material.BARRIER;
    private static final String defaultName = ChatColor.RED + "CLOSE";

    private final @NotNull Menu menu;

    public CloseIcon(@NotNull Menu menu) {
        super(defaultMaterial);
        this.menu = menu;
        setName(defaultName);
    }

    public CloseIcon(@NotNull Menu menu, @NotNull Material material) {
        super(material);
        this.menu = menu;
        setName(defaultName);
    }


    @Override
    public void onClick(MenuClick click) {
        menu.removePlayer(click.getWhoClicked());
        menu.closeIfNoOpenPages();
    }
}
