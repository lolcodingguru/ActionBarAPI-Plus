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
 * Event fired when an action bar task is resumed after being paused
 */
public class ActionBarTaskResumedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String message;
    private final reception.Styles style;
    private final List<ChatColor> colors;
    private final Sound sound;
    private final boolean isPermanent;
    private final boolean isGlobal;
    private final int remainingTicks;
    private final UUID taskId; // Unique identifier for the task

    public ActionBarTaskResumedEvent(Player player, String message, reception.Styles style, 
                                    List<ChatColor> colors, Sound sound, 
                                    boolean isPermanent, boolean isGlobal, int remainingTicks) {
        this(player, message, style, colors, sound, isPermanent, isGlobal, remainingTicks, UUID.randomUUID());
    }

    public ActionBarTaskResumedEvent(Player player, String message, reception.Styles style, 
                                    List<ChatColor> colors, Sound sound, 
                                    boolean isPermanent, boolean isGlobal, int remainingTicks, UUID taskId) {
        this.player = player;
        this.message = message;
        this.style = style;
        this.colors = colors;
        this.sound = sound;
        this.isPermanent = isPermanent;
        this.isGlobal = isGlobal;
        this.remainingTicks = remainingTicks;
        this.taskId = taskId;
    }

    /**
     * Construct the event from an ActionBarTask
     */
    public ActionBarTaskResumedEvent(ActionBarTask task, Player player, boolean isGlobal) {
        this.player = player;
        this.message = task.getMessage();
        this.style = task.getStyle();
        this.colors = task.getColors();
        this.sound = task.getSound();
        this.isPermanent = task.getRemainingTicks() < 0;
        this.isGlobal = isGlobal;
        this.remainingTicks = task.getRemainingTicks();
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

    public boolean isPermanent() {
        return isPermanent;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public int getRemainingTicks() {
        return remainingTicks;
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
