package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.HiddenStringUtils;
import me.bumblebeee_.magic.Inventories;
import me.bumblebeee_.magic.Magic;
import me.bumblebeee_.magic.SpellManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Set;

public class OffhandSwap implements Listener {

    SpellManager spells = new SpellManager();
    Inventories inv = new Inventories();

    @EventHandler
    public void onOffhandSwap(PlayerSwapHandItemsEvent e) {

    }

}