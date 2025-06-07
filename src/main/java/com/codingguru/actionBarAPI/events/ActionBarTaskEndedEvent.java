package com.codingguru.actionBarAPI.events;

import com.codingguru.actionBarAPI.taskHandlers.ActionBarTask;
import com.codingguru.actionBarAPI.taskHandlers.reception;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

/**
 * Event fired when a temporary action bar task naturally ends (completes its duration)
 */
public class ActionBarTaskEndedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String message;
    private final reception.Styles style;
    private final List<ChatColor> colors;
    private final Sound sound;
    private final int duration;
    private final boolean wasGlobal;
    private final UUID taskId; // Unique identifier for the task

    public ActionBarTaskEndedEvent(Player player, String message, reception.Styles style, 
                                  List<ChatColor> colors, Sound sound, 
                                  int duration, boolean wasGlobal) {
        this(player, message, style, colors, sound, duration, wasGlobal, UUID.randomUUID());
    }

    public ActionBarTaskEndedEvent(Player player, String message, reception.Styles style, 
                                  List<ChatColor> colors, Sound sound, 
                                  int duration, boolean wasGlobal, UUID taskId) {
        this.player = player;
        this.message = message;
        this.style = style;
        this.colors = colors;
        this.sound = sound;
        this.duration = duration;
        this.wasGlobal = wasGlobal;
        this.taskId = taskId;
    }

    /**
     * Construct the event from an ActionBarTask
     */
    public ActionBarTaskEndedEvent(ActionBarTask task, Player player, boolean wasGlobal) {
        this.player = player;
        this.message = task.getMessage();
        this.style = task.getStyle();
        this.colors = task.getColors();
        this.sound = task.getSound();
        this.duration = task.getRemainingTicks(); // This will be 0 or negative
        this.wasGlobal = wasGlobal;
        this.taskId = task.getTaskId(); // Get the task ID from the task
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
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

    public int getDuration() {
        return duration;
    }

    public boolean wasGlobal() {
        return wasGlobal;
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
}
