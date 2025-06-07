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
 * Gradient style creates a smooth color transition across the message
 */
public class gradient extends BaseActionBarTask {
    private BukkitTask task;
    private int currentTick = 0;
    private static final int TICKS_PER_SHIFT = 3; // Shift gradient every 3 ticks

    protected gradient(Player player, String message, int duration, List<ChatColor> colors,
                     Sound sound) {
        this(player, message, duration, colors, sound, false);
    }

    protected gradient(Player player, String message, int duration, List<ChatColor> colors,
                     Sound sound, boolean isGlobal) {
        this(player, message, duration, colors, sound, isGlobal, UUID.randomUUID());
    }

    protected gradient(Player player, String message, int duration, List<ChatColor> colors,
                     Sound sound, boolean isGlobal, UUID taskId) {
        super(player, message, duration, colors, sound, isGlobal, taskId);
        this.style = reception.Styles.gradient;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;
        paused = false;
        playSound();

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Starting gradient action bar task for player " + player.getName() + 
            ", duration: " + (isPermanent ? "permanent" : remainingTicks + " ticks"));

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!running) {
                    stop();
                    return;
                }

                if (!isPermanent && remainingTicks <= 0) {
                     boolean wasGlobal = false; // We don't know if it's global from here
                    ActionBarTaskEndedEvent endedEvent =
                        new ActionBarTaskEndedEvent(gradient.this, player, wasGlobal);
                    org.bukkit.Bukkit.getPluginManager().callEvent(endedEvent);

                    stop();
                    return;
                }

                if (paused) {
                    return;
                }

                String coloredMessage = applyGradientColors();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    new TextComponent(coloredMessage));

                if (!isPermanent) {
                    remainingTicks--;
                }

                if (++currentTick >= TICKS_PER_SHIFT) {
                    currentTick = 0;
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

        boolean wasGlobal = false; // We don't know if it's global from here
        ActionBarTaskStoppedEvent stoppedEvent =
            new ActionBarTaskStoppedEvent(this, player, wasGlobal);
        org.bukkit.Bukkit.getPluginManager().callEvent(stoppedEvent);

        TaskManager.getInstance().taskCompleted(this);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Gradient action bar task stopped.");
    }

    private String applyGradientColors() {
        if (colors.isEmpty()) {
            return message;
        }

        // Need at least 2 colors for a gradient
        if (colors.size() < 2) {
            return colors.get(0) + message;
        }

        StringBuilder result = new StringBuilder();
        int messageLength = message.length();

        int segmentLength = Math.max(1, messageLength / (colors.size() - 1));

        for (int i = 0; i < messageLength; i++) {
            int segmentIndex = Math.min(i / segmentLength, colors.size() - 2);

            float segmentPosition = (i % segmentLength) / (float)segmentLength;

            int colorIndex = (segmentIndex + currentTick / TICKS_PER_SHIFT) % (colors.size() - 1);
            ChatColor color = colors.get(colorIndex);

            result.append(color).append(message.charAt(i));
        }

        return result.toString();
    }
}
