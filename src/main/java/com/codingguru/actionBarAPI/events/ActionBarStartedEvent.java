package com.codingguru.actionBarAPI.events;

import com.codingguru.actionBarAPI.taskHandlers.reception;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

public class ActionBarStartedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final String message;
    private final int duration;
    private final reception.Styles style;
    private final List<ChatColor> colors;
    private final Sound sound;
    private final UUID taskId; // Unique identifier for the task

    public ActionBarStartedEvent(Player player, String message, int duration, reception.Styles style, List<ChatColor> colors, Sound sound) {
        this(player, message, duration, style, colors, sound, UUID.randomUUID());
    }

    public ActionBarStartedEvent(Player player, String message, int duration, reception.Styles style, List<ChatColor> colors, Sound sound, UUID taskId) {
        this.cancelled=false;
        this.player=player;
        this.message=message;
        this.duration=duration;
        this.style=style;
        this.colors=colors;
        this.sound=sound;
        this.taskId=taskId;
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

    public Sound getSound() {
        return sound;
    }

    public UUID getTaskId() {
        return taskId;
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
        this.cancelled=b;
    }
}
