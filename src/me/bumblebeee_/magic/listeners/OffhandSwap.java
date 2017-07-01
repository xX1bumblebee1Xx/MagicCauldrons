package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.HiddenStringUtils;
import me.bumblebeee_.magic.Inventories;
import me.bumblebeee_.magic.Magic;
import me.bumblebeee_.magic.SpellManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Set;

public class OffhandSwap implements Listener {

    SpellManager spells = new SpellManager();
    Inventories inv = new Inventories();

    @EventHandler
    public void onOffhandSwap(PlayerSwapHandItemsEvent e) {
        if (e.getOffHandItem().getType() != spells.getWandType())
            return;
        if (!e.getOffHandItem().hasItemMeta())
            return;
        if (!e.getOffHandItem().getItemMeta().hasDisplayName())
            return;
        String dis = e.getOffHandItem().getItemMeta().getDisplayName();
        if (!dis.equals(ChatColor.translateAlternateColorCodes('&', Magic.getInstance().getConfig().getString("wandName"))))
            return;

        Player p = e.getPlayer();
        List<String> lore = e.getOffHandItem().getItemMeta().getLore();
        String id = HiddenStringUtils.extractHiddenString(lore.get(lore.size()-1));

        inv.openSpells(p, id);
        e.setCancelled(true);
    }

}