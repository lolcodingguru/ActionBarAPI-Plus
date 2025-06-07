package com.codingguru.actionBarAPI.taskHandlers;

import com.codingguru.actionBarAPI.utils.logging;
import com.codingguru.actionBarAPI.utils.priorityHandling;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class defaultStyle extends BaseActionBarTask {
    private BukkitTask task;

    public defaultStyle(Player player, String message, int duration, 
                      List<ChatColor> colors, priorityHandling.Priority priority, Sound sound) {
        super(player, message, duration, colors, sound);
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;
        paused = false;
        playSound();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!running || remainingTicks <= 0) {
                    stop();
                    return;
                }

                if (paused) {
                    return;
                }

                // Apply default formatting
                String formattedMessage = applyDefaultFormatting();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    new TextComponent(formattedMessage));

                remainingTicks--;
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

        // Notify the task manager that this task has completed
        TaskManager.getInstance().taskCompleted(this);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Default style action bar task stopped.");
    }

    private String applyDefaultFormatting() {
        if (colors.isEmpty()) {
            return message;
        }

        // Use the first color in the list
        ChatColor color = colors.get(0);
        return color + message;
    }
}
