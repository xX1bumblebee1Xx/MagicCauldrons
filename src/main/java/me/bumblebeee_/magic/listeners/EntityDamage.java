package main.java.me.bumblebeee_.magic.listeners;

import main.java.me.bumblebeee_.magic.AbilityManager;
import main.java.me.bumblebeee_.magic.Magic;
import main.java.me.bumblebeee_.magic.SpellManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDamage implements Listener {

    SpellManager spells = new SpellManager();
    AbilityManager abilities = new AbilityManager();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        Player p = (Player) e.getEntity();
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

        if (!abilities.getAbilities(hand).contains("CHICKEN"))
            return;
        e.setCancelled(true);
    }

}