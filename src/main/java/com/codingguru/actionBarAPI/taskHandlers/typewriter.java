package com.codingguru.actionBarAPI.taskHandlers;

import com.codingguru.actionBarAPI.events.ActionBarTaskEndedEvent;
import com.codingguru.actionBarAPI.events.ActionBarTaskStoppedEvent;
import com.codingguru.actionBarAPI.utils.logging;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

/**
 * Typewriter style makes characters appear one by one, like typing
 */
public class typewriter extends BaseActionBarTask {
    private BukkitTask task;
    private int currentCharIndex = 0;
    private static final int CHARS_PER_TICK = 1; // How many characters to add per tick
    private static final int TICKS_PER_RESET = 60; // Reset and start over after 3 seconds (60 ticks)
    private int tickCounter = 0;

    protected typewriter(Player player, String message, int duration, List<ChatColor> colors,
                         Sound sound) {
        this(player, message, duration, colors, sound, false);
    }

    protected typewriter(Player player, String message, int duration, List<ChatColor> colors,
                         Sound sound, boolean isGlobal) {
        this(player, message, duration, colors, sound, isGlobal, UUID.randomUUID());
    }

    protected typewriter(Player player, String message, int duration, List<ChatColor> colors,
                         Sound sound, boolean isGlobal, UUID taskId) {
        super(player, message, duration, colors, sound, isGlobal, taskId);
        this.style = reception.Styles.typewriter;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;
        paused = false;
        playSound();

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Starting typewriter action bar task for player " + player.getName() + 
            ", duration: " + (isPermanent ? "permanent" : remainingTicks + " ticks"));

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!running) {
                    stop();
                    return;
                }

                if (!isPermanent && remainingTicks <= 0) {
                    boolean wasGlobal = false;
                    ActionBarTaskEndedEvent endedEvent =
                        new ActionBarTaskEndedEvent(typewriter.this, player, wasGlobal);
                    org.bukkit.Bukkit.getPluginManager().callEvent(endedEvent);

                    stop();
                    return;
                }

                if (paused) {
                    return;
                }

                String coloredMessage = applyTypewriterEffect();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    new TextComponent(coloredMessage));

                if (!isPermanent) {
                    remainingTicks--;
                }

                tickCounter++;
                if (tickCounter % 2 == 0) { // Add a character every 2 ticks for a more natural typing speed
                    currentCharIndex = Math.min(currentCharIndex + CHARS_PER_TICK, message.length());
                }

                // For permanent action bars, don't reset after showing the full message
                if (!isPermanent && currentCharIndex >= message.length() && tickCounter >= TICKS_PER_RESET) {
                    currentCharIndex = 0;
                    tickCounter = 0;
                }
            }
        }.runTaskTimer(TaskManager.getInstance().getPlugin(), 0L, 1L);
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }

        running = false;
        if (task != null) {
            task.cancel();
        }

        boolean wasGlobal = false;
        ActionBarTaskStoppedEvent stoppedEvent =
            new ActionBarTaskStoppedEvent(this, player, wasGlobal);
        org.bukkit.Bukkit.getPluginManager().callEvent(stoppedEvent);

        TaskManager.getInstance().taskCompleted(this);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Typewriter action bar task stopped.");
    }

    private String applyTypewriterEffect() {
        if (colors.isEmpty()) {
            return message.substring(0, currentCharIndex);
        }

        // Use the first color for the text
        ChatColor textColor = colors.get(0);

        // Get the visible part of the message based on current character index
        String visiblePart = message.substring(0, currentCharIndex);

        // Only add a blinking cursor if we haven't typed the full message yet
        String cursor = "";
        if (currentCharIndex < message.length()) {
            cursor = (tickCounter % 2 == 0) ? "_" : " ";
        }

        return textColor + visiblePart + cursor;
    }
}
