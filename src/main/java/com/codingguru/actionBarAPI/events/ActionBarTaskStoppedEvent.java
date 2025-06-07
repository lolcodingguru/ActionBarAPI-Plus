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
 * Event fired when an action bar task is stopped
 */
public class ActionBarTaskStoppedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String message;
    private final reception.Styles style;
    private final List<ChatColor> colors;
    private final Sound sound;
    private final boolean wasPermanent;
    private final boolean wasGlobal;
    private final UUID taskId; // Unique identifier for the task

    public ActionBarTaskStoppedEvent(Player player, String message, reception.Styles style, 
                                    List<ChatColor> colors, Sound sound, 
                                    boolean wasPermanent, boolean wasGlobal) {
        this(player, message, style, colors, sound, wasPermanent, wasGlobal, UUID.randomUUID());
    }

    public ActionBarTaskStoppedEvent(Player player, String message, reception.Styles style, 
                                    List<ChatColor> colors, Sound sound, 
                                    boolean wasPermanent, boolean wasGlobal, UUID taskId) {
        this.player = player;
        this.message = message;
        this.style = style;
        this.colors = colors;
        this.sound = sound;
        this.wasPermanent = wasPermanent;
        this.wasGlobal = wasGlobal;
        this.taskId = taskId;
    }

    /**
     * Construct the event from an ActionBarTask
     */
    public ActionBarTaskStoppedEvent(ActionBarTask task, Player player, boolean wasGlobal) {
        this.player = player;
        this.message = task.getMessage();
        this.style = task.getStyle();
        this.colors = task.getColors();
        this.sound = task.getSound();
        this.wasPermanent = task.getRemainingTicks() < 0;
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

    public boolean wasPermanent() {
        return wasPermanent;
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
