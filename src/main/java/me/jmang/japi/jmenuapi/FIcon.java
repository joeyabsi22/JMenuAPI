package me.jmang.japi.jmenuapi;

import me.jmang.japi.jmenuapi.actions.MenuClick;
import org.bukkit.Material;

public abstract class FIcon extends Icon {

    public FIcon(Material material) {
        super(material);
    }

    public abstract void onClick(MenuClick click);

    void onClickInternal(MenuClick click) {
        onClick(click);
    }
}
