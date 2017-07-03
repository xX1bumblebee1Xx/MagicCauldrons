package main.java.me.bumblebeee_.magic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpellManager {

    //Keeps track of which player has which spell selected
    //WandID, SelectedSpell
    public static HashMap<String, String> selected = new HashMap<>();

    public String getSpell(List<ItemStack> itemStacks) {
        for (String spell : getAllSpells()) {
            List<String> recipe = Magic.getInstance().getConfig().getStringList("spells." + spell + ".recipe");
            List<Material> materials = convertToMaterialList(itemStacks);
            int found = 0;
            int size = recipe.size();

            for (String s : recipe) {
                Material m;
                try {
                    m = Material.matchMaterial(s);
                } catch (Exception e) {
                    Bukkit.getServer().getLogger().warning("Failed to find material named " + s);
                    continue;
                }

                if (materials.contains(m)) {
                    found++;
                    if (found == size)
                        return spell;
                }
            }
        }
        return null;
    }

    public List<String> getWandSpells(String id) {
        File f = new File(Magic.getInstance().getDataFolder() + File.separator + "storage.yml");
        if (!f.exists())
            return new ArrayList<>();
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        if (c.getConfigurationSection("wands") == null)
            return new ArrayList<>();
        if (!c.getConfigurationSection("wands").getKeys(false).contains(id))
            return new ArrayList<>();
        return c.getStringList("wands." + id);
    }

    public Set<String> getAllSpells() {
        return Magic.getInstance().getConfig().getConfigurationSection("spells").getKeys(false);
    }

    public void addSpell(String spell, ItemStack wand) {
        String data = HiddenStringUtils.extractHiddenString(wand.getItemMeta().getLore().get(wand.getItemMeta().getLore().size()-1));
        String id = data.split(" ")[1];
        File f = new File(Magic.getInstance().getDataFolder() + File.separator + "storage.yml");
        if (!f.exists())
            return;

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        if (c.getConfigurationSection("wands") == null || !c.getConfigurationSection("wands").getKeys(false).contains(id)) {
            c.set("wands." + id, new ArrayList<>(Arrays.asList(spell)));
        } else {
            List<String> spells = c.getStringList("wands." + id);
            spells.add(spell);
            c.set("wands." + id, spells);
        }

        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Material> convertToMaterialList(List<ItemStack> itemstack) {
        ArrayList<Material> data = new ArrayList<>();
        for (ItemStack is : itemstack) {
            data.add(is.getType());
        }
        return data;
    }

    public boolean checkShape(Location l) {
        l.subtract(0,1,0);
        Location min = l.clone().subtract(1,0,1);
        Location max = l.clone().add(1,0,1);

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Material m = new Location(l.getWorld(), x, y, z).getBlock().getType();
                    if (m != Material.DIAMOND_BLOCK && m != Material.GOLD_BLOCK && m != Material.IRON_BLOCK)
                        return false;
                }
            }
        }
        return true;
    }

    public int getCauldronLevel(Block cauldron) {
        Block check = cauldron.getLocation().subtract(0,1,0).getBlock();
        if (check.getType() == Material.DIAMOND_BLOCK)
            return 3;
        else if (check.getType() == Material.GOLD_BLOCK)
            return 2;
        else if (check.getType() == Material.IRON_BLOCK)
            return 1;
        return 0;
    }

    public int getSpellLevel(String spell) {
        return Magic.getInstance().getConfig().getInt("spells." + spell + ".level");
    }

    public String getWandID(ItemStack i) {
        if (!i.hasItemMeta())
            return null;
        if (!i.getItemMeta().hasLore())
            return null;

        List<String> lore = i.getItemMeta().getLore();
        return HiddenStringUtils.extractHiddenString(lore.get(lore.size()-1)).split(" ")[1];
    }

    public Material getWandType() {
        Material m;
        try {
            m = Material.matchMaterial(Magic.getInstance().getConfig().getString("wandItem"));
        } catch (Exception e) {
            return null;
        }
        return m;
    }

    public void giveWandToPlayer(Player p) {
        Material m;
        try {
            m = Material.matchMaterial(Magic.getInstance().getConfig().getString("wandItem"));
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "An internal error has occurred! Please report this to an admin.");
            Bukkit.getServer().getLogger().warning("Failed to find item called " + Magic.getInstance().getConfig().getString("wandItem"));
            return;
        }


        int wand = getWandAmounts()+1;
        List<String> rawLore = Magic.getInstance().getConfig().getStringList("wandLore");
        List<String> lore = new ArrayList<>();
        for (String l : rawLore) {
            lore.add(ChatColor.translateAlternateColorCodes('&', l));
        }
        lore.add(HiddenStringUtils.encodeString("wand: " + wand));
        ItemStack i = new ItemStack(m);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Magic.getInstance().getConfig().getString("wandName")));
        im.setLore(lore);
        i.setItemMeta(im);

        setWandAmounts(wand);
        p.getInventory().addItem(i);
    }

    public int getWandAmounts() {
        File f = new File(Magic.getInstance().getDataFolder() + File.separator + "storage.yml");
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
            return 0;
        } else {
            YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
            return c.getInt("wands");
        }
    }

    public void setWandAmounts(int amount) {
        File f = new File(Magic.getInstance().getDataFolder() + File.separator + "storage.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().warning("Failed to create storage.yml!");
            } finally {
                YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
                c.set("wands", amount);
                try {
                    c.save(f);
                } catch (IOException e) {
                    Bukkit.getServer().getLogger().warning("Failed to create storage.yml!");
                }
            }
        } else {
            YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
            c.set("wands", amount);
            try {
                c.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
