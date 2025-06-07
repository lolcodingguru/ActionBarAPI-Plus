// 0 - Executed.
// 1 - Invalid style.
// 2 - Other invalid usage.
// 3 - Exception error

package com.codingguru.actionBarAPI.taskHandlers;

import com.codingguru.actionBarAPI.utils.logging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class reception {

    public static boolean doesSoundExist(Sound sound) {
        if (sound == null) {
            return true; // Null sound is acceptable (no sound)
        }
        try {
            Sound.valueOf(sound.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static int tempTaskRequest(taskType type, Player player, String message, int duration, Styles style, List<ChatColor> colors, Sound sound) {
        return tempTaskRequest(type, player, message, duration, style, colors, sound, false, UUID.randomUUID());
    }

    public static int tempTaskRequest(taskType type, Player player, String message, int duration, Styles style, List<ChatColor> colors, Sound sound, boolean isGlobal) {
        return tempTaskRequest(type, player, message, duration, style, colors, sound, isGlobal, UUID.randomUUID());
    }

    public static int tempTaskRequest(taskType type, Player player, String message, int duration, Styles style, List<ChatColor> colors, Sound sound, boolean isGlobal, UUID taskId) {
        // For permanent action bars (type == permanent), duration should be negative (typically -1)
        // For temporary action bars (type == temporary), duration should be positive
        if (player == null || (type == taskType.temporary && duration < 0) || style == null || !doesSoundExist(sound)) {
            return 2; // Invalid usage
        }

        try {
            ActionBarTask task;

            switch (style) {
                case rolling:
                    task = new rolling(player, message, duration, colors, sound, isGlobal, taskId);
                    break;
                case rainbow:
                    task = new rainbow(player, message, duration, colors, sound, isGlobal, taskId);
                    break;
                case flashing:
                    task = new flashing(player, message, duration, colors, sound, isGlobal, taskId);
                    break;
                case gradient:
                    task = new gradient(player, message, duration, colors, sound, isGlobal, taskId);
                    break;
                case typewriter:
                    task = new typewriter(player, message, duration, colors, sound, isGlobal, taskId);
                    break;
                case wave:
                    task = new wave(player, message, duration, colors, sound, isGlobal, taskId);
                    break;
                default:
                    return 1; // Invalid style
            }

            TaskManager.getInstance().queueTask(task, isGlobal);

            // Log based on task type and global status
            String taskTypeStr = (type == taskType.temporary) ? "Temporary" : "Permanent";
            String globalStr = isGlobal ? "global" : "player-specific";
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] " + taskTypeStr + " " + globalStr + " task queued for player " + player.getName());

            return 0; // Successfully executed
        } catch (Exception e) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Exception while creating task: " + e.getMessage());
            e.printStackTrace();
            return 3; // Exception error
        }
    }

    public static int globalTaskRequest(taskType type, String message, int duration, Styles style, List<ChatColor> colors, Sound sound) {
        String taskTypeStr = (type == taskType.temporary) ? "temporary" : "permanent";
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Global " + taskTypeStr + " task request received: message=\"" + message + 
            "\", duration=" + duration + ", style=" + style);

        if ((type == taskType.temporary && duration < 0) || style == null || !doesSoundExist(sound)) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Global task request validation failed");
            return 2; // Invalid usage
        }

        try {
            // We need a player for the task, but it's global, so we'll use the first online player
            // and then apply it to all players
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Cannot create global action bar with no online players.");
                return 2; // Invalid usage
            }

            Player firstPlayer = Bukkit.getOnlinePlayers().iterator().next();
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Using first player " + firstPlayer.getName() + " as base for global task");

            // Create the task for the first player
            int result = tempTaskRequest(type, firstPlayer, message, duration, style, colors, sound, true);

            if (result != 0) {
                logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Failed to create global task for first player, error code: " + result);
                return result; // Return any error code
            }

            // For all other online players, create individual tasks
            int playerCount = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.equals(firstPlayer)) {
                    continue; // Skip the first player, already handled
                }

                playerCount++;
                logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Creating global task for additional player: " + player.getName());

                // Create a task for this player
                tempTaskRequest(type, player, message, duration, style, colors, sound, true);
            }

            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Global task created for " + (playerCount + 1) + " players in total");

            return 0; // Successfully executed
        } catch (Exception e) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Exception while creating global task: " + e.getMessage());
            e.printStackTrace();
            return 3; // Exception error
        }
    }

    public enum Styles {rolling, rainbow, flashing, gradient, typewriter, fade, wave}
    public enum taskType {permanent, temporary}

}
