package me.bumblebeee_.magic;

import me.bumblebeee_.magic.listeners.InventoryClick;
import me.bumblebeee_.magic.listeners.OffhandSwap;
import me.bumblebeee_.magic.listeners.PlayerInteract;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Magic extends JavaPlugin {

    private static Plugin instance = null;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        registerEvents();
        Bukkit.getServer().getPluginCommand("wand").setExecutor(new Commands());

        File f = new File(getDataFolder() + File.separator + "storage.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().warning("Failed to create storage.yml!");
            } finally {
                YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
                c.set("wands", 0);
                try {
                    c.save(f);
                } catch (IOException e) {
                    Bukkit.getServer().getLogger().warning("Failed to create storage.yml!");
                }
            }
        }
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OffhandSwap(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(), this);
    }

    public static Plugin getInstance() { return instance; }
}
