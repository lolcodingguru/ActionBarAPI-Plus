package com.me.actionBarAPI.events;

import com.me.actionBarAPI.taskHandlers.reception;
import com.me.actionBarAPI.utils.priorityHandling;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class ActionBarStartedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final String message;
    private final int duration;
    private final reception.Styles style;
    private final List<ChatColor> colors;
    private final priorityHandling.Priority priority;
    private final Sound sound;

    public ActionBarStartedEvent(Player player, String message, int duration, reception.Styles style, List<ChatColor> colors, priorityHandling.Priority priority, Sound sound) {
        this.cancelled=false;
        this.player=player;
        this.message=message;
        this.duration=duration;
        this.style=style;
        this.colors=colors;
        this.priority=priority;
        this.sound=sound;
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

    public reception.Styles getStyle() {
        return style;
    }

    public List<ChatColor> getColors() {
        return colors;
    }

    public priorityHandling.Priority getPriority() {
        return priority;
    }

    public Sound getSound() {
        return sound;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled=b;

    }
}
