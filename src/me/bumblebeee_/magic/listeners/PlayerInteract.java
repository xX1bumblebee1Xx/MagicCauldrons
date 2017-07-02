package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.HiddenStringUtils;
import me.bumblebeee_.magic.Magic;
import me.bumblebeee_.magic.SpellManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
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
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Collection;
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
                        remove.add(en);
                        items.add(i);
                    }
                }
            }

            String spell = spells.getSpell(items);
            if (spell == null || Magic.getInstance().getConfig().getBoolean("spells." + spell + ".disabled")) {
                p.sendMessage("Could not find a spell for them ingredients!");
                return;
            }

            int spellLevel = spells.getSpellLevel(spell);
            int caulLevel = spells.getCauldronLevel(b);

            if (spellLevel > caulLevel) {
                p.sendMessage("This cauldron is not capable of casting this spell!");
                return;
            }

            if (p.getInventory().getItemInMainHand() == null) {
                p.sendMessage("You must be holding your wand!");
                return;
            }
            if (p.getInventory().getItemInMainHand().getType() != spells.getWandType()) {
                p.sendMessage("You must be holding your wand!");
                return;
            }
            if (!p.getInventory().getItemInMainHand().hasItemMeta()) {
                p.sendMessage("You must be holding your wand!");
                return;
            }
            if (!p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                p.sendMessage("You must be holding your wand!");
                return;
            }
            String display = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
            String wandName = ChatColor.translateAlternateColorCodes('&', Magic.getInstance().getConfig().getString("wandName"));
            if (!display.equalsIgnoreCase(wandName)) {
                p.sendMessage("You must be holding your wand!");
                return;
            }
            wand = p.getInventory().getItemInMainHand();

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
            p.sendMessage("Successfully cast spell!");
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
            String id = spells.getWandID(e.getItem());
            if (!SpellManager.selected.containsKey(id)) {
                p.sendMessage("You do not have any spell selected");
                return;
            }

            String spell = SpellManager.selected.get(id);
            if (spell.equalsIgnoreCase("fireball")) {
                p.launchProjectile(Fireball.class);
            } else if (spell.equalsIgnoreCase("lightning")) {
                p.getWorld().strikeLightning(p.getTargetBlock(((Set<Material>) null), 100).getLocation());
            } else if (spell.equalsIgnoreCase("shoot")) {
                BlockIterator blocksToAdd = new BlockIterator(p.getLocation(), 2, 100);
                Location blockToAdd;
                int buffer = 0;
                while(blocksToAdd.hasNext()) {
                    blockToAdd = blocksToAdd.next().getLocation();
                    Collection<Entity> entities = blockToAdd.getWorld().getNearbyEntities(blockToAdd, 0.5, 0.5, 0.5);
                    if (blockToAdd.getBlock().getType() != Material.AIR) {
                        break;
                    } else if (entities.size() > 0) {
                        buffer++;
                        if (buffer <= 2)
                            continue;
                        for (Entity entity : entities) {
                            if (entity.equals(p))
                                continue;
                            if (entity instanceof LivingEntity) {
                                LivingEntity en = (LivingEntity) entity;
                                    en.damage(2);
                            }
                        }

                    }
                    p.getWorld().playEffect(blockToAdd, Effect.SMOKE, 4);
                }
            } else if (spell.equalsIgnoreCase("poison")) {
                for (Entity en : p.getNearbyEntities(5, 5, 5)) {
                    if (!(en instanceof LivingEntity))
                        continue;
                    ((LivingEntity) en).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
                }
            } else if (spell.equalsIgnoreCase("miner")) {
                BlockIterator blocksToAdd = new BlockIterator(p.getLocation(), 2, 20);
                Location blockToAdd;
                while(blocksToAdd.hasNext()) {
                    blockToAdd = blocksToAdd.next().getLocation();
                    blockToAdd.getBlock().setType(Material.AIR);
                    p.getWorld().playEffect(blockToAdd, Effect.SMOKE, 4);
                }
            }
        }
    }
}