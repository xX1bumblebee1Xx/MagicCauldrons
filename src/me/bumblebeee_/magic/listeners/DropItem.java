package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.HiddenStringUtils;
import me.bumblebeee_.magic.Inventories;
import me.bumblebeee_.magic.Magic;
import me.bumblebeee_.magic.SpellManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.List;

public class DropItem implements Listener {

    Inventories inv = new Inventories();
    SpellManager spells = new SpellManager();

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() != spells.getWandType())
            return;
        if (!e.getItemDrop().getItemStack().hasItemMeta())
            return;
        if (!e.getItemDrop().getItemStack().getItemMeta().hasDisplayName())
            return;
        String dis = e.getItemDrop().getItemStack().getItemMeta().getDisplayName();
        if (!dis.equals(ChatColor.translateAlternateColorCodes('&', Magic.getInstance().getConfig().getString("wandName"))))
            return;

        Player p = e.getPlayer();
        List<String> lore = e.getItemDrop().getItemStack().getItemMeta().getLore();
        String id = HiddenStringUtils.extractHiddenString(lore.get(lore.size()-1));

        inv.openSpells(p, id);
        e.setCancelled(true);
    }

}