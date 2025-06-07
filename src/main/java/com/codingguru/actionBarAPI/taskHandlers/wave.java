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
 * Wave style creates a wave effect by applying different formatting to characters
 */
public class wave extends BaseActionBarTask {
    private BukkitTask task;
    private int currentTick = 0;
    private static final int WAVE_PERIOD = 20; // Ticks for a complete wave cycle

    protected wave(Player player, String message, int duration, List<ChatColor> colors,
                   Sound sound) {
        this(player, message, duration, colors, sound, false);
    }

    protected wave(Player player, String message, int duration, List<ChatColor> colors,
                   Sound sound, boolean isGlobal) {
        this(player, message, duration, colors, sound, isGlobal, UUID.randomUUID());
    }

    protected wave(Player player, String message, int duration, List<ChatColor> colors,
                   Sound sound, boolean isGlobal, UUID taskId) {
        super(player, message, duration, colors, sound, isGlobal, taskId);
        this.style = reception.Styles.wave;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;
        paused = false;
        playSound();

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Starting wave action bar task for player " + player.getName() + 
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
                        new ActionBarTaskEndedEvent(wave.this, player, wasGlobal);
                    org.bukkit.Bukkit.getPluginManager().callEvent(endedEvent);

                    stop();
                    return;
                }

                if (paused) {
                    return;
                }

                // Apply wave effect
                String coloredMessage = applyWaveEffect();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    new TextComponent(coloredMessage));

                if (!isPermanent) {
                    remainingTicks--;
                }

                // Update wave position
                currentTick = (currentTick + 1) % WAVE_PERIOD;
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
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Wave action bar task stopped.");
    }

    private String applyWaveEffect() {
        if (colors.isEmpty() || message.isEmpty()) {
            return message;
        }

        // Use the first color as the base color
        ChatColor baseColor = colors.get(0);

        StringBuilder result = new StringBuilder();
        int messageLength = message.length();

        for (int i = 0; i < messageLength; i++) {
            // Calculate wave position for this character
            double wavePos = (double) i / messageLength * 2 * Math.PI + (double) currentTick / WAVE_PERIOD * 2 * Math.PI;
            double sinValue = Math.sin(wavePos);

            // Determine which color to use based on the wave position
            ChatColor charColor;
            if (colors.size() > 1) {
                // Map sin value (-1 to 1) to color index (0 to colors.size()-1)
                int colorIndex = (int) ((sinValue + 1) / 2 * (colors.size() - 1));
                charColor = colors.get(colorIndex);
            } else {
                charColor = baseColor;
            }

            // Apply formatting based on wave position
            if (sinValue > 0.7) {
                // Peak of wave - make it bold
                result.append(charColor).append(ChatColor.BOLD).append(message.charAt(i));
            } else if (sinValue < -0.7) {
                // Trough of wave - make it italic
                result.append(charColor).append(ChatColor.ITALIC).append(message.charAt(i));
            } else {
                // Middle of wave - normal
                result.append(charColor).append(message.charAt(i));
            }
        }

        return result.toString();
    }
}
