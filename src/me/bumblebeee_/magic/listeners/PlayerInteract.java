package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.Magic;
import me.bumblebeee_.magic.SpellManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;

import java.util.ArrayList;
import java.util.List;

public class PlayerInteract implements Listener {

    SpellManager spells = new SpellManager();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (e.getClickedBlock() == null)
            return;
        if (e.getClickedBlock().getType() != Material.CAULDRON)
            return;
        if (e.getItem() != null && e.getItem().getType() == Material.WATER_BUCKET)
            return;

        e.setCancelled(true);
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        List<ItemStack> items = new ArrayList<>();
        String wandItem = Magic.getInstance().getConfig().getString("wandItem");

        boolean shape = spells.checkShape(b.getLocation());
        if (!shape)
            return;

        Cauldron c = (Cauldron) b.getState().getData();
        if (c.isEmpty()) {
            p.sendMessage("Cauldron is empty!");
            return;
        }

        for (Entity en : b.getWorld().getEntities()) {
            if (en.getLocation().distance(b.getLocation()) <= 1.5) {
                if (en.getLocation().getBlock().getType() == Material.CAULDRON) {
                    if (en instanceof Item) {
                        items.add(((Item) en).getItemStack());
                    }
                }
            }
        }

        //TODO check if wand is there


        //TODO check spells
        String spell = spells.getSpell(items);
        if (spell == null) {
            p.sendMessage("Could not find a spell for them ingredients!");
            return;
        }
        System.out.println("Spell: " + spell);

        BlockState bs = b.getState();
        bs.getData().setData((byte) (c.getData()-1));
        bs.update();
    }
}