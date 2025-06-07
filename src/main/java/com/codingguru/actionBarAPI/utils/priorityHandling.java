// Task management system for action bars
// There is no prioritization anymore. Instead:
// 1. Starting a temporary action bar task will cancel any running temporary task
// 2. Starting a temporary action bar task will pause any permanent task (which will resume after temporary task completes)
// 3. Starting a permanent action bar task will cancel any existing permanent task

package com.codingguru.actionBarAPI.utils;

import com.codingguru.actionBarAPI.taskHandlers.ActionBarTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

public class priorityHandling {
    private static final Map<UUID, ActiveTask> permanentTasks = new HashMap<>();
    private static final Map<UUID, ActiveTask> temporaryTasks = new HashMap<>();

    private static ActiveTask globalPermanentTask = null;
    private static ActiveTask globalTemporaryTask = null;

    private static final Set<UUID> playersWithGlobalActionBar = new HashSet<>();

    public static class ActiveTask {
        private final ActionBarTask task;
        private final boolean isGlobal;

        public ActiveTask(ActionBarTask task, boolean isGlobal) {
            this.task = task;
            this.isGlobal = isGlobal;
        }

        public ActionBarTask getTask() {
            return task;
        }

        public boolean isGlobal() {
            return isGlobal;
        }
    }

    public static void registerPermanentTask(UUID playerUUID, ActionBarTask task, boolean isGlobal) {
        Player player = Bukkit.getPlayer(playerUUID);
        String playerName = player != null ? player.getName() : "Unknown";

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Registering permanent task for player " + playerName + " (UUID: " + playerUUID + "), isGlobal: " + isGlobal);

        ActiveTask existingTask = permanentTasks.get(playerUUID);
        if (existingTask != null) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Cancelling existing permanent task for player " + playerName);
            existingTask.getTask().stop();
        }

        permanentTasks.put(playerUUID, new ActiveTask(task, isGlobal));
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Permanent task registered for player " + playerName);

        if (isGlobal) {
            globalPermanentTask = new ActiveTask(task, true);
            playersWithGlobalActionBar.add(playerUUID);
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Global permanent task registered and player " + playerName + " marked as having seen it");
        }
    }

    public static void registerTemporaryTask(UUID playerUUID, ActionBarTask task, boolean isGlobal) {
        Player player = Bukkit.getPlayer(playerUUID);
        String playerName = player != null ? player.getName() : "Unknown";

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Registering temporary task for player " + playerName + " (UUID: " + playerUUID + "), isGlobal: " + isGlobal);

        ActiveTask existingTempTask = temporaryTasks.get(playerUUID);
        if (existingTempTask != null) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Cancelling existing temporary task for player " + playerName);
            existingTempTask.getTask().stop();
        }

        ActiveTask existingPermTask = permanentTasks.get(playerUUID);
        if (existingPermTask != null) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Pausing permanent task for player " + playerName + " while temporary task runs");
            existingPermTask.getTask().pause();
        }

        temporaryTasks.put(playerUUID, new ActiveTask(task, isGlobal));
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Temporary task registered for player " + playerName);

        if (isGlobal) {
            globalTemporaryTask = new ActiveTask(task, true);
            playersWithGlobalActionBar.add(playerUUID);
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Global temporary task registered and player " + playerName + " marked as having seen it");
        }
    }

    public static void unregisterTemporaryTask(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        String playerName = player != null ? player.getName() : "Unknown";

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Unregistering temporary task for player " + playerName + " (UUID: " + playerUUID + ")");

        temporaryTasks.remove(playerUUID);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Temporary task removed for player " + playerName);

        if (globalTemporaryTask != null && !hasAnyPlayerWithGlobalTemporaryTask()) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] No players have global temporary task anymore, clearing global reference");
            globalTemporaryTask = null;
        }

        ActiveTask permanentTask = permanentTasks.get(playerUUID);
        if (permanentTask != null) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Resuming paused permanent task for player " + playerName);
            permanentTask.getTask().resume();
        }
    }

    public static void unregisterPermanentTask(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        String playerName = player != null ? player.getName() : "Unknown";

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Unregistering permanent task for player " + playerName + " (UUID: " + playerUUID + ")");

        permanentTasks.remove(playerUUID);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Permanent task removed for player " + playerName);

        if (globalPermanentTask != null && !hasAnyPlayerWithGlobalPermanentTask()) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] No players have global permanent task anymore, clearing global reference");
            globalPermanentTask = null;
        }
    }

    private static boolean hasAnyPlayerWithGlobalTemporaryTask() {
        for (ActiveTask task : temporaryTasks.values()) {
            if (task.isGlobal()) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasAnyPlayerWithGlobalPermanentTask() {
        for (ActiveTask task : permanentTasks.values()) {
            if (task.isGlobal()) {
                return true;
            }
        }
        return false;
    }

    public static ActiveTask getActiveTemporaryTask(UUID playerUUID) {
        return temporaryTasks.get(playerUUID);
    }

    public static ActiveTask getActivePermanentTask(UUID playerUUID) {
        return permanentTasks.get(playerUUID);
    }

    public static ActiveTask getActiveTask(UUID playerUUID) {
        ActiveTask tempTask = temporaryTasks.get(playerUUID);
        if (tempTask != null) {
            return tempTask;
        }
        return permanentTasks.get(playerUUID);
    }

    public static boolean hasSeenGlobalActionBar(UUID playerUUID) {
        return playersWithGlobalActionBar.contains(playerUUID);
    }

    public static void markPlayerSeenGlobalActionBar(UUID playerUUID) {
        playersWithGlobalActionBar.add(playerUUID);
    }

    public static ActiveTask getGlobalPermanentTask() {
        return globalPermanentTask;
    }

    public static ActiveTask getGlobalTemporaryTask() {
        return globalTemporaryTask;
    }

    public enum Priority { LOW, MEDIUM, HIGH, OVERRIDE }
}
