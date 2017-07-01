package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.HiddenStringUtils;
import me.bumblebeee_.magic.Magic;
import me.bumblebeee_.magic.SpellManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerInteract implements Listener {

    SpellManager spells = new SpellManager();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND)
            return;
        Player p = e.getPlayer();
        if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.CAULDRON) {
            if (e.getItem() != null && e.getItem().getType() == Material.WATER_BUCKET)
                return;

            e.setCancelled(true);
            Block b = e.getClickedBlock();
            List<ItemStack> items = new ArrayList<>();
            List<Entity> remove = new ArrayList<>();
            ItemStack wand = null;

            boolean shape = spells.checkShape(b.getLocation());
            if (!shape)
                return;

            Cauldron c = (Cauldron) b.getState().getData();
            if (c.isEmpty()) {
                p.sendMessage("Cauldron is empty!");
                return;
            }

            for (Entity en : b.getWorld().getEntities()) {
                if (en.getLocation().distance(b.getLocation()) <= 1.5 && en.getLocation().getBlock().getType() == Material.CAULDRON) {
                    if (en instanceof Item) {
                        ItemStack i = ((Item) en).getItemStack();
                        if (i.getType() == spells.getWandType()) {
                            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                                String dis = ChatColor.translateAlternateColorCodes('&', Magic.getInstance().getConfig().getString("wandName"));
                                if (i.getItemMeta().getDisplayName().equals(dis)) {
                                    wand = i;
                                } else {
                                    remove.add(en);
                                    items.add(i);
                                }
                            } else {
                                remove.add(en);
                                items.add(i);
                            }
                        } else {
                            remove.add(en);
                            items.add(i);
                        }
                    }
                }
            }

            String spell = spells.getSpell(items);
            if (spell == null || Magic.getInstance().getConfig().getBoolean("spells." + spell + ".disabled")) {
                p.sendMessage("Could not find a spell for them ingredients!");
                return;
            }

            //TODO level

            if (wand == null) {
                p.sendMessage("You must include you wand as well!");
                return;
            }

            List<String> lore = wand.getItemMeta().getLore();
            String id = HiddenStringUtils.extractHiddenString(lore.get(lore.size()-1)).split(" ")[1];

            if (spells.getWandSpells(id).contains(spell)) {
                p.sendMessage("You already have that spell");
                return;
            }

            for (Entity i : remove) {
                i.remove();
            }

            spells.addSpell(spell, wand);
            BlockState bs = b.getState();
            bs.getData().setData((byte) (c.getData() - 1));
            bs.update();
        } else {
            if (e.getAction() != Action.LEFT_CLICK_AIR) {
                if (e.getAction() != Action.LEFT_CLICK_BLOCK)
                    return;
            }
            if (e.getItem() == null)
                return;
            if (e.getItem().getType() != spells.getWandType())
                return;

            e.setCancelled(true);
            if (!SpellManager.selected.containsKey(p.getUniqueId())) {
                p.sendMessage("You do not have any spell selected");
                return;
            }

            String spell = SpellManager.selected.get(p.getUniqueId());
            if (spell.equalsIgnoreCase("fireball")) {
                p.launchProjectile(Fireball.class);
            } else if (spell.equalsIgnoreCase("lightning")) {
                p.getWorld().strikeLightning(p.getTargetBlock(((Set<Material>) null), 100).getLocation());
            } else if (spell.equalsIgnoreCase("shoot")) {

            } else if (spell.equalsIgnoreCase("poison")) {
                for (Entity en : p.getNearbyEntities(5, 5, 5)) {
                    if (!(en instanceof LivingEntity))
                        continue;
                    ((LivingEntity) en).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
                }
            }
        }
    }
}