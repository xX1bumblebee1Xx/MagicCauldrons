package me.bumblebeee_.magic;

import me.bumblebeee_.magic.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Magic extends JavaPlugin {

    private static Plugin instance = null;
    ManaManager mana = new ManaManager();

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
        manaRunnable();
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OffhandSwap(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DropItem(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new SpellCast(), this);
    }

    public static Plugin getInstance() { return instance; }

    public void manaRunnable() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (!mana.getManaPlayers().containsKey(p.getUniqueId()))
                        continue;
                    if (mana.getMana(p) == 100) {
                        continue;
                    }

                    if (mana.getMana(p) > 0) {
                        p.setAllowFlight(true);
                    } else {
                        p.setAllowFlight(false);
                    }
                    mana.addMana(p, getConfig().getInt("manaRegen"));
                }
            }
        }, 20, 20);
    }
}
