package me.bumblebeee_.magic;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbilityManager {

    public void addAbility(ItemStack wand, String mob) {
        String data = HiddenStringUtils.extractHiddenString(wand.getItemMeta().getLore().get(wand.getItemMeta().getLore().size()-1));
        String id = data.split(" ")[1];
        File f = new File(Magic.getInstance().getDataFolder() + File.separator + "storage.yml");
        if (!f.exists())
            return;

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        if (c.getConfigurationSection("wands") == null || !c.getConfigurationSection("wands").getKeys(false).contains(id)) {
            c.set("wands." + id + ".mobs", new ArrayList<>(Arrays.asList(mob)));
        } else {
            List<String> mobs = c.getStringList("wands." + id + ".mobs");
            mobs.add(mob);
            c.set("wands." + id + ".mobs", mobs);
        }

        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeEffects(Player p) {
        p.removePotionEffect(PotionEffectType.SPEED);
        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        p.setAllowFlight(false);
        p.setFlying(false);
    }

    public List<String> getAbilities(ItemStack wand) {
        String data = HiddenStringUtils.extractHiddenString(wand.getItemMeta().getLore().get(wand.getItemMeta().getLore().size()-1));
        String id = data.split(" ")[1];
        File f = new File(Magic.getInstance().getDataFolder() + File.separator + "storage.yml");
        if (!f.exists())
            return new ArrayList<>();

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        if (c.getConfigurationSection("wands") == null || !c.getConfigurationSection("wands").getKeys(false).contains(id))
            return new ArrayList<>();

        List<String> mobs = c.getStringList("wands." + id + ".mobs");
        if (mobs == null)
            return new ArrayList<>();

        return mobs;
    }

}
