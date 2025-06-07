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

public class flashing extends BaseActionBarTask {
    private BukkitTask task;
    private int currentColorIndex = 0;
    private int tickCounter = 0;
    private static final int TICKS_PER_FLASH = 6; // Change color every 6 ticks

    protected flashing(Player player, String message, int duration, List<ChatColor> colors,
                  Sound sound) {
        this(player, message, duration, colors, sound, false);
    }

    protected flashing(Player player, String message, int duration, List<ChatColor> colors,
                  Sound sound, boolean isGlobal) {
        this(player, message, duration, colors, sound, isGlobal, UUID.randomUUID());
    }

    protected flashing(Player player, String message, int duration, List<ChatColor> colors,
                  Sound sound, boolean isGlobal, UUID taskId) {
        super(player, message, duration, colors, sound, isGlobal, taskId);
        this.style = reception.Styles.flashing;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;
        paused = false;
        playSound();

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Starting flashing action bar task for player " + player.getName() + 
            ", duration: " + (isPermanent ? "permanent" : remainingTicks + " ticks"));

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!running) {
                    stop();
                    return;
                }

                if (!isPermanent && remainingTicks <= 0) {
                    // Fire ended event for temporary tasks that naturally complete
                    ActionBarTaskEndedEvent endedEvent =
                        new ActionBarTaskEndedEvent(flashing.this, player, isGlobal);
                    org.bukkit.Bukkit.getPluginManager().callEvent(endedEvent);

                    stop();
                    return;
                }

                if (paused) {
                    return;
                }

                // Apply flashing effect
                String coloredMessage = applyFlashingColor();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    new TextComponent(coloredMessage));

                if (!isPermanent) {
                    remainingTicks--;
                }
                tickCounter++;

                // Change color every TICKS_PER_FLASH ticks
                if (tickCounter >= TICKS_PER_FLASH) {
                    tickCounter = 0;
                    currentColorIndex = (currentColorIndex + 1) % colors.size();
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

        // Fire stopped event
        ActionBarTaskStoppedEvent stoppedEvent =
            new ActionBarTaskStoppedEvent(this, player, isGlobal);
        org.bukkit.Bukkit.getPluginManager().callEvent(stoppedEvent);

        // Notify the task manager that this task has completed
        TaskManager.getInstance().taskCompleted(this);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Flashing action bar task stopped.");
    }

    private String applyFlashingColor() {
        if (colors.isEmpty()) {
            return message;
        }

        // Apply the current color to the entire message
        ChatColor currentColor = colors.get(currentColorIndex);
        return currentColor + message;
    }
}
