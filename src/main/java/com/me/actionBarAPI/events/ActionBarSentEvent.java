package com.me.actionBarAPI.events;

import com.me.actionBarAPI.utils.priorityHandling;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ActionBarSentEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    private final Player player;
    private final String message;
    private final int duration;
    private final priorityHandling.Priority priority;

    public ActionBarSentEvent(Player player, String message, int duration, priorityHandling.Priority priority) {
        cancelled=false;
        this.player = player;
        this.message = message;
        this.duration = duration;
        this.priority = priority;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public int getDuration() {
        return duration;
    }

    public priorityHandling.Priority getPriority() {
        return priority;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
