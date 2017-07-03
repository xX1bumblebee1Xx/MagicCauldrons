package me.bumblebeee_.magic.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpellCastEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    boolean cancelled = false;
    private String spell;
    private Player p;

    public SpellCastEvent(Player p, String spell) {
        this.spell = spell;
        this.p = p;
    }

    public String getSpell() {
        return spell;
    }

    public Player getPlayer() {
        return p;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
