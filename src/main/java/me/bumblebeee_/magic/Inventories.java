package main.java.me.bumblebeee_.magic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;
import java.util.Set;

public class Inventories {

    public void openSpells(Player p, String id) {
        id = id.split(" ")[1];
        Inventory inv = Bukkit.getServer().createInventory(null, 27, "Your spells");

        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);

        ItemStack close = new ItemStack(Material.NETHER_STAR);
        ItemMeta cm = close.getItemMeta();
        cm.setDisplayName(ChatColor.RED + "Close");
        close.setItemMeta(cm);

        ItemStack none = new ItemStack(Material.FIREWORK_CHARGE);
        ItemMeta nm = none.getItemMeta();
        nm.setDisplayName(ChatColor.DARK_PURPLE + "Deselect spell");
        none.setItemMeta(nm);

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, filler);
        }
        inv.setItem(22, close);
        inv.setItem(0, none);

        File f = new File(Magic.getInstance().getDataFolder() + File.separator + "storage.yml");
        if (!f.exists()) {
            p.openInventory(inv);
            return;
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        if (c.getConfigurationSection("wands") == null) {
            p.openInventory(inv);
            return;
        }

        Set<String> ids = c.getConfigurationSection("wands").getKeys(false);
        if (!ids.contains(id)) {
            p.openInventory(inv);
            return;
        }

        List<String> spells = c.getStringList("wands." + id);
        for (int i = 0; i < spells.size(); i++) {
            if (i > 26)
                continue;

            ItemStack s = new ItemStack(Material.FIREBALL);
            ItemMeta sm = s.getItemMeta();
            sm.setDisplayName(ChatColor.DARK_PURPLE + "Select " + spells.get(i));
            s.setItemMeta(sm);
            inv.setItem(i+1, s);
        }

        p.openInventory(inv);
    }

}
