package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.AbilityManager;
import me.bumblebeee_.magic.Magic;
import me.bumblebeee_.magic.SpellManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDeath implements Listener {

    SpellManager spells = new SpellManager();
    AbilityManager ability = new AbilityManager();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null)
            return;

        Player p = e.getEntity().getKiller();
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

        String name = e.getEntity().getType().name();
        if (name.equalsIgnoreCase("SHEEP")) {
            ability.addAbility(hand, "SHEEP");
        } else if (name.equalsIgnoreCase("BAT")) {
            ability.addAbility(hand, "BAT");
        } else if (name.equalsIgnoreCase("CHICKEN")) {
            ability.addAbility(hand, "CHICKEN");
        } else if (name.equalsIgnoreCase("OCELOT")) {
            ability.addAbility(hand, "OCELOT");
        }
    }

}