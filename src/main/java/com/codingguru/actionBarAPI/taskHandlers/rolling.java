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

public class rolling extends BaseActionBarTask {
    private BukkitTask task;
    private int currentColorIndex = 0;

    protected rolling(Player player, String message, int duration, List<ChatColor> colors,
                  Sound sound) {
        this(player, message, duration, colors, sound, false);
    }

    protected rolling(Player player, String message, int duration, List<ChatColor> colors,
                  Sound sound, boolean isGlobal) {
        this(player, message, duration, colors, sound, isGlobal, UUID.randomUUID());
    }

    protected rolling(Player player, String message, int duration, List<ChatColor> colors,
                  Sound sound, boolean isGlobal, UUID taskId) {
        super(player, message, duration, colors, sound, isGlobal, taskId);
        this.style = reception.Styles.rolling;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;
        paused = false;
        playSound();

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Starting rolling action bar task for player " + player.getName() + 
            ", duration: " + (isPermanent ? "permanent" : remainingTicks + " ticks"));

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!running) {
                    stop();
                    return;
                }

                if (!isPermanent && remainingTicks <= 0) {
                    ActionBarTaskEndedEvent endedEvent =
                        new ActionBarTaskEndedEvent(rolling.this, player, isGlobal);
                    org.bukkit.Bukkit.getPluginManager().callEvent(endedEvent);

                    stop();
                    return;
                }

                if (paused) {
                    return;
                }

                String coloredMessage = applyRollingColor();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    new TextComponent(coloredMessage));

                if (!isPermanent) {
                    remainingTicks--;
                }
                // Increment the color index without modulo to allow rolling through the entire text
                currentColorIndex++;
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

        ActionBarTaskStoppedEvent stoppedEvent =
            new ActionBarTaskStoppedEvent(this, player, isGlobal);
        org.bukkit.Bukkit.getPluginManager().callEvent(stoppedEvent);

        TaskManager.getInstance().taskCompleted(this);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Rolling action bar task stopped.");
    }

    private String applyRollingColor() {
        if (colors.isEmpty()) {
            return message;
        }

        // First color in the list is the main text color
        ChatColor mainColor = colors.get(0);

        // If there's only one color, apply it to the entire message
        if (colors.size() == 1) {
            return mainColor + message;
        }

        StringBuilder result = new StringBuilder();
        int messageLength = message.length();

        // For permanent action bars, we want to roll through the entire text
        // before resetting, so we don't use modulo with messageLength
        int rollPosition = currentColorIndex;

        // If rollPosition gets too large, reset it to avoid potential overflow
        if (rollPosition > messageLength * 2) {
            rollPosition = 0;
            currentColorIndex = 0;
        }

        // Apply colors to each character
        for (int i = 0; i < messageLength; i++) {
            // Determine which color to use for this character
            int colorIndex = 0; // Default to main color

            // Calculate distance from roll position (considering wrap-around)
            int distance = (i - rollPosition + messageLength * 2) % messageLength;

            // Apply rolling colors based on distance from roll position
            if (distance < colors.size() - 1) {
                // Use one of the rolling colors (skip the first color which is the main color)
                colorIndex = distance + 1;
            }

            // Apply the selected color to this character
            result.append(colors.get(colorIndex)).append(message.charAt(i));
        }

        return result.toString();
    }
}
