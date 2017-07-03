package main.java.me.bumblebeee_.magic.listeners;

import main.java.me.bumblebeee_.magic.Messages;
import main.java.me.bumblebeee_.magic.SpellManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {

    SpellManager spells = new SpellManager();
    Messages msgs = new Messages();

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
                p.sendMessage(msgs.getMessage("deselectedSpell"));
            } else {
                p.sendMessage(msgs.getMessage("noSpellSelected"));
            }
            p.closeInventory();
        } else if (dis.startsWith("Select")) {
            String spell = dis.split(" ")[1];
            if (SpellManager.selected.containsKey(id) && SpellManager.selected.get(id).equals(spell)) {
                p.sendMessage(msgs.getMessage("alreadySelected"));
            } else {
                p.sendMessage(msgs.getMessage("selectedSpell").replace("<spell>", spell));
                SpellManager.selected.put(id, spell.toLowerCase());
            }
            p.closeInventory();
        }
    }

}