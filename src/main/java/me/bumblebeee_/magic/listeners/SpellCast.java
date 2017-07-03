package me.bumblebeee_.magic.listeners;

import me.bumblebeee_.magic.Magic;
import me.bumblebeee_.magic.ManaManager;
import me.bumblebeee_.magic.events.SpellCastEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpellCast implements Listener {

    ManaManager mana = new ManaManager();

    @EventHandler
    public void onSpellCast(SpellCastEvent e) {
        Player p = e.getPlayer();
        if (!mana.getManaPlayers().containsKey(p.getUniqueId()))
            mana.getManaPlayers().put(p.getUniqueId(), 100.0);

        int manaRequired = Magic.getInstance().getConfig().getInt("spells." + e.getSpell() + ".mana");
        if (mana.getManaPlayers().get(p.getUniqueId()) < manaRequired) {
            e.setCancelled(true);
            return;
        }

        mana.takeMana(p, manaRequired);
    }

}