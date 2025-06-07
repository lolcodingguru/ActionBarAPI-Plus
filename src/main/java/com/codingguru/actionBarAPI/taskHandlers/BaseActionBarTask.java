package com.codingguru.actionBarAPI.taskHandlers;

import com.codingguru.actionBarAPI.events.ActionBarTaskPausedEvent;
import com.codingguru.actionBarAPI.events.ActionBarTaskResumedEvent;
import com.codingguru.actionBarAPI.utils.logging;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public abstract class BaseActionBarTask implements ActionBarTask {
    protected final Player player;
    protected final String message;
    protected final int duration;
    protected final List<ChatColor> colors;
    protected final Sound sound;
    protected reception.Styles style; // Added style field
    protected boolean running;
    protected boolean paused;
    protected int remainingTicks;
    protected boolean isPermanent;
    protected boolean isGlobal; // Added isGlobal field
    protected final UUID taskId; // Unique identifier for the task

    public BaseActionBarTask(Player player, String message, int duration, 
                           List<ChatColor> colors, Sound sound) {
        this(player, message, duration, colors, sound, false);
    }

    public BaseActionBarTask(Player player, String message, int duration, 
                           List<ChatColor> colors, Sound sound, boolean isGlobal) {
        this(player, message, duration, colors, sound, isGlobal, UUID.randomUUID());
    }

    public BaseActionBarTask(Player player, String message, int duration, 
                           List<ChatColor> colors, Sound sound, boolean isGlobal, UUID taskId) {
        this.player = player;
        this.message = message;
        this.duration = duration;
        this.colors = colors;
        this.sound = sound;
        this.remainingTicks = duration;
        this.running = false;
        this.paused = false;
        this.isPermanent = (duration < 0); // If duration is negative, it's a permanent task
        this.isGlobal = isGlobal; // Initialize isGlobal field
        this.taskId = taskId; // Use the provided task ID
        // Note: style will be set by the subclass
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public reception.Styles getStyle() {
        return style;
    }

    @Override
    public List<ChatColor> getColors() {
        return colors;
    }

    @Override
    public Sound getSound() {
        return sound;
    }

    @Override
    public void pause() {
        if (running && !paused) {
            paused = true;
            logging.log(logging.LogLevel.DEBUG,
                "[ActionBarAPI+] Task paused for player " + player.getName() + ", type: " + (isPermanent ? "permanent" : "temporary"));

            // Fire pause event
            ActionBarTaskPausedEvent event =
                new ActionBarTaskPausedEvent(this, player, this.isGlobal);
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
        }
    }

    @Override
    public void resume() {
        if (running && paused) {
            paused = false;
            logging.log(logging.LogLevel.DEBUG,
                "[ActionBarAPI+] Task resumed for player " + player.getName() + ", type: " + (isPermanent ? "permanent" : "temporary"));

            // Fire resume event
            ActionBarTaskResumedEvent event =
                new ActionBarTaskResumedEvent(this, player, this.isGlobal);
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getRemainingTicks() {
        return remainingTicks;
    }

    @Override
    public UUID getPlayerUUID() {
        return player.getUniqueId();
    }

    @Override
    public UUID getTaskId() {
        return taskId;
    }

    protected void playSound() {
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }
}
