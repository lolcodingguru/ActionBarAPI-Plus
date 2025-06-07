package com.codingguru.actionBarAPI.taskHandlers;

import com.codingguru.actionBarAPI.utils.logging;
import com.codingguru.actionBarAPI.utils.priorityHandling;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class TaskManager {
    private static TaskManager instance;
    private final Plugin plugin;
    private final Map<UUID, Queue<ActionBarTask>> taskQueues = new HashMap<>();
    private final Set<ActionBarTask> globalTasks = new HashSet<>();

    private TaskManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TaskManager not initialized. Call initialize first.");
        }
        return instance;
    }

    public static synchronized void initialize(Plugin plugin) {
        if (instance == null) {
            instance = new TaskManager(plugin);
            logging.log(logging.LogLevel.SUCCESS, "[ActionBarAPI+] TaskManager initialized successfully for plugin: " + plugin.getName());
        } else {
            logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] TaskManager already initialized by another plugin");
            logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Ignoring initialization request from plugin: " + plugin.getName());
        }
    }

    public void queueTask(ActionBarTask task) {
        queueTask(task, false);
    }

    public void queueTask(ActionBarTask task, boolean isGlobal) {
        UUID playerUUID = task.getPlayerUUID();

        // If it's a global task, store it for tracking
        if (isGlobal) {
            globalTasks.add(task);
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Added global task for player " + Bukkit.getPlayer(playerUUID).getName() + " (UUID: " + playerUUID + ")");
        }

        taskQueues.computeIfAbsent(playerUUID, k -> new LinkedList<>()).offer(task);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Task queued for player " + Bukkit.getPlayer(playerUUID).getName() + " (UUID: " + playerUUID + "), isGlobal: " + isGlobal);
        processQueue(playerUUID, isGlobal);
    }

    public void processQueue(UUID playerUUID, boolean isGlobal) {
        Queue<ActionBarTask> queue = taskQueues.get(playerUUID);
        if (queue == null || queue.isEmpty()) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] No tasks in queue for player UUID: " + playerUUID);
            return;
        }

        ActionBarTask nextTask = queue.poll();
        Player player = Bukkit.getPlayer(playerUUID);
        String playerName = player != null ? player.getName() : "Unknown";

        // Check if it's a permanent or temporary task
        boolean isPermanent = nextTask.getRemainingTicks() < 0;
        String taskType = isPermanent ? "permanent" : "temporary";

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Processing " + taskType + " task for player " + playerName + " (UUID: " + playerUUID + "), isGlobal: " + isGlobal);

        if (isPermanent) {
            priorityHandling.registerPermanentTask(playerUUID, nextTask, isGlobal);
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Registered permanent task for player " + playerName);
        } else {
            priorityHandling.registerTemporaryTask(playerUUID, nextTask, isGlobal);
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Registered temporary task for player " + playerName + " with duration: " + nextTask.getRemainingTicks() + " ticks");
        }

        nextTask.start();
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Started task for player " + playerName);
    }

    public void taskCompleted(ActionBarTask task) {
        UUID playerUUID = task.getPlayerUUID();
        Player player = Bukkit.getPlayer(playerUUID);
        String playerName = player != null ? player.getName() : "Unknown";

        boolean isPermanent = task.getRemainingTicks() < 0;
        String taskType = isPermanent ? "permanent" : "temporary";

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Task completed for player " + playerName + " (UUID: " + playerUUID + "), type: " + taskType);

        if (isPermanent) {
            priorityHandling.unregisterPermanentTask(playerUUID);
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Unregistered permanent task for player " + playerName);
        } else {
            priorityHandling.unregisterTemporaryTask(playerUUID);
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Unregistered temporary task for player " + playerName);
        }

        boolean wasGlobal = globalTasks.remove(task);
        if (wasGlobal) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Removed global task for player " + playerName);
        }

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Processing next task in queue for player " + playerName);
        processQueue(playerUUID, false);
    }

    public void applyGlobalActionBarToPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Checking if player " + playerName + " needs global action bar");

        if (priorityHandling.hasSeenGlobalActionBar(playerUUID)) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Player " + playerName + " has already seen global action bar, skipping");
            return;
        }

        priorityHandling.ActiveTask globalTemp = priorityHandling.getGlobalTemporaryTask();
        if (globalTemp != null) {
            ActionBarTask originalTask = globalTemp.getTask();
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Applying global temporary action bar to newly joined player " + playerName);
            return;
        }

        priorityHandling.ActiveTask globalPerm = priorityHandling.getGlobalPermanentTask();
        if (globalPerm != null) {
            ActionBarTask originalTask = globalPerm.getTask();
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Applying global permanent action bar to newly joined player " + playerName);
            return;
        }

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] No global action bars to apply to player " + playerName);
    }

    public void sendGlobalActionBar(ActionBarTask task) {
        globalTasks.add(task);

        Player taskPlayer = Bukkit.getPlayer(task.getPlayerUUID());
        String taskPlayerName = taskPlayer != null ? taskPlayer.getName() : "Unknown";

        boolean isPermanent = task.getRemainingTicks() < 0;
        String taskType = isPermanent ? "permanent" : "temporary";

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Sending global " + taskType + " action bar from player " + taskPlayerName);

        int playerCount = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(taskPlayer)) {
                continue;
            }

            playerCount++;
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Applying global action bar to player " + player.getName());

        }

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Global action bar sent to " + playerCount + " players");
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
