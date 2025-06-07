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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class rainbow extends BaseActionBarTask {
    private BukkitTask task;
    private int currentTick = 0;

    // Rainbow colors (ignoring provided colors)
    private static final List<ChatColor> RAINBOW_COLORS = Arrays.asList(
        ChatColor.RED,
        ChatColor.GOLD,
        ChatColor.YELLOW,
        ChatColor.GREEN,
        ChatColor.AQUA,
        ChatColor.BLUE,
        ChatColor.LIGHT_PURPLE
    );

    protected rainbow(Player player, String message, int duration, List<ChatColor> colors, 
                     Sound sound) {
        this(player, message, duration, colors, sound, false);
    }

    protected rainbow(Player player, String message, int duration, List<ChatColor> colors, 
                     Sound sound, boolean isGlobal) {
        this(player, message, duration, colors, sound, isGlobal, UUID.randomUUID());
    }

    protected rainbow(Player player, String message, int duration, List<ChatColor> colors, 
                     Sound sound, boolean isGlobal, UUID taskId) {
        // Ignore provided colors and use rainbow colors
        super(player, message, duration, RAINBOW_COLORS, sound, isGlobal, taskId);
        this.style = reception.Styles.rainbow;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;
        paused = false;
        playSound();

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Starting rainbow action bar task for player " + player.getName() + 
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
                        new ActionBarTaskEndedEvent(rainbow.this, player, isGlobal);
                    org.bukkit.Bukkit.getPluginManager().callEvent(endedEvent);

                    stop();
                    return;
                }

                if (paused) {
                    return;
                }

                String coloredMessage = applyRainbowColors();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    new TextComponent(coloredMessage));

                if (!isPermanent) {
                    remainingTicks--;
                }
                currentTick = (currentTick + 1) % 6; // Change every 6 ticks
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

        // Fire stopped event
        ActionBarTaskStoppedEvent stoppedEvent =
            new ActionBarTaskStoppedEvent(this, player, isGlobal);
        org.bukkit.Bukkit.getPluginManager().callEvent(stoppedEvent);

        // Notify the task manager that this task has completed
        TaskManager.getInstance().taskCompleted(this);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Rainbow action bar task stopped.");
    }

    private String applyRainbowColors() {
        StringBuilder result = new StringBuilder();
        int messageLength = message.length();

        for (int i = 0; i < messageLength; i++) {
            // Calculate color index based on position and current tick
            int colorIndex = (i + currentTick) % RAINBOW_COLORS.size();

            // Apply the color to this character
            result.append(RAINBOW_COLORS.get(colorIndex)).append(message.charAt(i));
        }

        return result.toString();
    }
}
