package me.jmang.japi.jmenuapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class JMenuAPI extends JavaPlugin implements Listener {

    static JMenuAPI instance;

    @Override
    public void onEnable() {
        instance = this;
        closeAllInventories();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        closeAllInventories();
    }

    private void closeAllInventories() {
        for (Player player : getServer().getOnlinePlayers()) player.closeInventory();
    }

    public static @NotNull JMenuAPI getInstance() {
        return instance;
    }
}