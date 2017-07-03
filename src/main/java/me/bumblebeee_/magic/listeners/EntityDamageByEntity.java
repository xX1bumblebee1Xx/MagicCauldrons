package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.Magic;
import me.bumblebeee_.magic.SpellManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDamageByEntity implements Listener {

    SpellManager spells = new SpellManager();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player))
            return;

        Player p = (Player) e.getDamager();
        if (p.getInventory().getItemInMainHand() == null)
            return;
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() != spells.getWandType())
            return;
        if (!hand.hasItemMeta())
            return;
        if (!hand.getItemMeta().hasDisplayName())
            return;
        String display = ChatColor.translateAlternateColorCodes('&', Magic.getInstance().getConfig().getString("wandName"));
        if (!hand.getItemMeta().getDisplayName().equalsIgnoreCase(display))
            return;

        e.setDamage(5);
    }

}