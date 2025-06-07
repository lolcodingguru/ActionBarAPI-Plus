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

/**
 * Event fired when an action bar task is modified
 */
public class ActionBarTaskModifiedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final String oldMessage;
    private final String newMessage;
    private final reception.Styles style;
    private final List<ChatColor> colors;
    private final Sound sound;
    private final boolean isGlobal;
    private final UUID taskId; // Unique identifier for the task

    public ActionBarTaskModifiedEvent(Player player, String oldMessage, String newMessage, 
                                     reception.Styles style, List<ChatColor> colors, 
                                     Sound sound, boolean isGlobal) {
        this(player, oldMessage, newMessage, style, colors, sound, isGlobal, UUID.randomUUID());
    }

    public ActionBarTaskModifiedEvent(Player player, String oldMessage, String newMessage, 
                                     reception.Styles style, List<ChatColor> colors, 
                                     Sound sound, boolean isGlobal, UUID taskId) {
        this.cancelled = false;
        this.player = player;
        this.oldMessage = oldMessage;
        this.newMessage = newMessage;
        this.style = style;
        this.colors = colors;
        this.sound = sound;
        this.isGlobal = isGlobal;
        this.taskId = taskId;
    }

    public Player getPlayer() {
        return player;
    }

    public String getOldMessage() {
        return oldMessage;
    }

    public String getNewMessage() {
        return newMessage;
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

    public boolean isGlobal() {
        return isGlobal;
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
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
