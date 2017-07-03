package me.bumblebeee_.magic;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ManaManager {

    public static HashMap<UUID, Double> mana = new HashMap<>();

    public double getMana(Player p) {
        if (!getManaPlayers().containsKey(p.getUniqueId()))
            return -1;
        return getManaPlayers().get(p.getUniqueId());
    }


    public void takeMana(Player p, double amount) {
        if (!getManaPlayers().containsKey(p.getUniqueId()))
            return;

        double ca = getManaPlayers().get(p.getUniqueId());
        getManaPlayers().remove(p.getUniqueId());
        getManaPlayers().put(p.getUniqueId(), ca-amount);
    }

    public void addMana(Player p, double amount) {
        if (!getManaPlayers().containsKey(p.getUniqueId()))
            return;

        double ca = getManaPlayers().get(p.getUniqueId());
        getManaPlayers().remove(p.getUniqueId());
        getManaPlayers().put(p.getUniqueId(), ca+amount);
    }

    public HashMap<UUID, Double> getManaPlayers() { return mana; }

}
