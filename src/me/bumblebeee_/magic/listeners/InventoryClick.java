package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.SpellManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {

    SpellManager spells = new SpellManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().getName().equals("Your spells"))
            return;
        if (e.getCurrentItem() == null)
            return;
        if (!e.getCurrentItem().hasItemMeta())
            return;
        if (!e.getCurrentItem().getItemMeta().hasDisplayName())
            return;

        e.setCancelled(true);
        String dis = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
        Player p = (Player) e.getWhoClicked();
        String id = spells.getWandID(p.getInventory().getItemInMainHand());
        if (dis.equals("Close")) {
            p.closeInventory();
        } else if (dis.equals("Deselect spell")) {
            if (SpellManager.selected.containsKey(id)) {
                SpellManager.selected.remove(id);
                p.sendMessage("Deselected spell");
            } else {
                p.sendMessage("You do not have any spell selected");
            }
            p.closeInventory();
        } else if (dis.startsWith("Select")) {
            String spell = dis.split(" ")[1];
            if (SpellManager.selected.containsKey(id) && SpellManager.selected.get(id).equals(spell)) {
                p.sendMessage("You already have that spell selected");
            } else {
                p.sendMessage("Selected " + spell);
                SpellManager.selected.put(id, spell.toLowerCase());
            }
            p.closeInventory();
        }
    }

}