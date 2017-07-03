package me.bumblebeee_.magic;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Runnables {

    ManaManager mana = new ManaManager();
    SpellManager spells = new SpellManager();
    AbilityManager abilities = new AbilityManager();

    public void manaRunnable() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask( Magic.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (!mana.getManaPlayers().containsKey(p.getUniqueId()))
                        continue;
                    if (mana.getMana(p) == 100) {
                        continue;
                    }

                    if (mana.getMana(p) > 0) {
                        p.setAllowFlight(true);
                    } else {
                        p.setAllowFlight(false);
                    }
                    mana.addMana(p, Magic.getInstance().getConfig().getInt("manaRegen"));
                }
            }
        }, 20, 20);
    }


    public void abilities() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask( Magic.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (p.getInventory().getItemInMainHand() == null) {
                        abilities.removeEffects(p);
                        return;
                    }
                    ItemStack hand = p.getInventory().getItemInMainHand();
                    if (hand.getType() != spells.getWandType()) {
                        abilities.removeEffects(p);
                        return;
                    }
                    if (!hand.hasItemMeta()) {
                        abilities.removeEffects(p);
                        return;
                    }
                    if (!hand.getItemMeta().hasDisplayName()) {
                        abilities.removeEffects(p);
                        return;
                    }
                    String display = ChatColor.translateAlternateColorCodes('&', Magic.getInstance().getConfig().getString("wandName"));
                    if (!hand.getItemMeta().getDisplayName().equalsIgnoreCase(display)) {
                        abilities.removeEffects(p);
                        return;
                    }

                    for (String ab : abilities.getAbilities(hand)) {
                        PotionEffect speed = PotionEffectType.SPEED.createEffect(999999, 6);
                        PotionEffect nightVision = PotionEffectType.NIGHT_VISION.createEffect(199999980, 6);
                        if (ab.equalsIgnoreCase("BAT")) {
                            p.addPotionEffect(nightVision);
                            p.setAllowFlight(true);
                        }

                        if (ab.equalsIgnoreCase("OCELOT")) {
                            p.addPotionEffect(speed);
                        }
                    }
                }
            }
        }, 20, 20);
    }

}
