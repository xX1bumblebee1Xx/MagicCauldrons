package main.java.me.bumblebeee_.magic.listeners;

import main.java.me.bumblebeee_.magic.*;
import main.java.me.bumblebeee_.magic.events.SpellCastEvent;
import org.bukkit.*;
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
    AbilityManager abilities = new AbilityManager();
    Util utils = new Util();
    Messages msgs = new Messages();

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
                p.sendMessage(msgs.getMessage("emptyCauldron"));
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
                p.sendMessage(msgs.getMessage("spellNotFound"));
                return;
            }

            int spellLevel = spells.getSpellLevel(spell);
            int caulLevel = spells.getCauldronLevel(b);

            if (spellLevel > caulLevel) {
                p.sendMessage(msgs.getMessage("levelToLow"));
                return;
            }

            if (p.getInventory().getItemInMainHand() == null) {
                p.sendMessage(msgs.getMessage("notHoldingWand"));
                return;
            }
            if (p.getInventory().getItemInMainHand().getType() != spells.getWandType()) {
                p.sendMessage(msgs.getMessage("notHoldingWand"));
                return;
            }
            if (!p.getInventory().getItemInMainHand().hasItemMeta()) {
                p.sendMessage(msgs.getMessage("notHoldingWand"));
                return;
            }
            if (!p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                p.sendMessage(msgs.getMessage("notHoldingWand"));
                return;
            }
            String display = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
            String wandName = ChatColor.translateAlternateColorCodes('&', Magic.getInstance().getConfig().getString("wandName"));
            if (!display.equalsIgnoreCase(wandName)) {
                p.sendMessage(msgs.getMessage("notHoldingWand"));
                return;
            }
            wand = p.getInventory().getItemInMainHand();

            List<String> lore = wand.getItemMeta().getLore();
            String id = HiddenStringUtils.extractHiddenString(lore.get(lore.size()-1)).split(" ")[1];

            if (spells.getWandSpells(id).contains(spell)) {
                p.sendMessage(msgs.getMessage("alreadyHaveSpell"));
                return;
            }

            for (Entity i : remove) {
                i.remove();
            }

            spells.addSpell(spell, wand);
            BlockState bs = b.getState();
            bs.getData().setData((byte) (c.getData() - 1));
            bs.update();
            p.sendMessage(msgs.getMessage("successAddedSpell"));
        } else {
            if (e.getAction() != Action.LEFT_CLICK_AIR) {
                if (e.getAction() != Action.LEFT_CLICK_BLOCK)
                    return;
            }
            if (e.getItem() == null)
                return;
            if (e.getItem().getType() != spells.getWandType())
                return;
            if (e.getItem().getType() != spells.getWandType())
                return;
            if (!e.getItem().hasItemMeta())
                return;
            if (!e.getItem().getItemMeta().hasDisplayName())
                return;
            String display = ChatColor.translateAlternateColorCodes('&', Magic.getInstance().getConfig().getString("wandName"));
            if (!e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(display))
                return;

            e.setCancelled(true);
            String id = spells.getWandID(e.getItem());

            if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.GRASS) {
                if (abilities.getAbilities(e.getItem()).contains("SHEEP")) {
                    e.getClickedBlock().setType(Material.DIRT);
                    int flevel = p.getFoodLevel();
                    p.setFoodLevel(flevel + 2);
                    return;
                }
            }

            if (!SpellManager.selected.containsKey(id)) {
                p.sendMessage(msgs.getMessage("noSpellSelected"));
                return;
            }

            String spell = SpellManager.selected.get(id);
            SpellCastEvent spellCast = new SpellCastEvent(p, spell);
            Bukkit.getServer().getPluginManager().callEvent(spellCast);
            if (spellCast.isCancelled()) {
                p.sendMessage(msgs.getMessage("notEnoughMana"));
                return;
            }

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
            } else if (spell.equalsIgnoreCase("throwBlocks")) {
                Location l = p.getTargetBlock(((Set<Material>) null), 10).getLocation();
                Location typeLoc = l.clone();
                Material m = typeLoc.subtract(0,1,0).getBlock().getType();
                for (int i = 0; i < 3; i++) {
                    if (m == Material.AIR) {
                        m = typeLoc.subtract(0, 1, 0).getBlock().getType();
                    }
                }

                for (int i = 0; i <= 20; i++) {
                    utils.throwb(m, typeLoc);
                }
            }
        }
    }
}