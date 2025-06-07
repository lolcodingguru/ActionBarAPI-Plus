//This is a library for various and many flexible functions for Minecraft's built-in action bar.
//It also features the ability to listen to actionbar events such as action bar sent, or action bar duration ended, etc.
//It provides developers extensive customizablity and power over the action bars.

// What is a permanent action bar task?
// It is a task that will run indefinetly until stopped, canceled or overriden. Application example: Player health, Stats, Server IP, etc.
//
// What is a temporary action bar task?
// It is a task that will run for a certain amount of time, and then will stop. Application example: A broadcast.
//
// All tasks are player-specific (Events are fired for each player separately)
// Global tasks are server-wide simply by sending a task to ALL online players through a loop.
//
//


package com.codingguru.actionBarAPI;

import com.codingguru.actionBarAPI.events.ActionBarSentEvent;
import com.codingguru.actionBarAPI.events.ActionBarStartedEvent;
import com.codingguru.actionBarAPI.events.ActionBarTaskModifiedEvent;
import com.codingguru.actionBarAPI.taskHandlers.TaskManager;
import com.codingguru.actionBarAPI.taskHandlers.reception;
import com.codingguru.actionBarAPI.utils.logging;
import com.codingguru.actionBarAPI.utils.priorityHandling;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class ActionBarAPI implements Listener {
    private static ActionBarAPI instance;
    private JavaPlugin plugin;

    public ActionBarAPI(JavaPlugin plugin) {
        if (instance != null) {
            logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] API already initialized by another plugin (You can probably safely ignore this)");
            logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Ignoring initialization request from plugin: " + plugin.getName() + " (You can probably safely ignore this)");
            return;
        }

        this.plugin = plugin;
        instance = this;
        logging.log(logging.LogLevel.SUCCESS, "[ActionBarAPI+] API initialized successfully for plugin: " + plugin.getName());
        TaskManager.initialize(plugin);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static ActionBarAPI getInstance() {
        return instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Player " + player.getName() + " joined, checking for global action bars to apply");

        TaskManager.getInstance().applyGlobalActionBarToPlayer(player);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Finished processing global action bars for player " + player.getName());
    }

    public void unregisterActionBarAPI(Plugin plugin) {
        if (plugin == null) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Cannot unregister null plugin!");
            return;
        }
    }

    public static List<ChatColor> colorListBuild(List<String> colors) {
        List<String> failedColors = new ArrayList<>();
        List<ChatColor> colorList = new ArrayList<>();
        for (String color : colors) {
            try {
                colorList.add(ChatColor.valueOf(color));
            } catch (IllegalArgumentException e) {
                failedColors.add(color);
            }
        }
        if (!failedColors.isEmpty()) {
            logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] The following requested colors were invalid: " + Arrays.toString(failedColors.toArray()));
        }
        return colorList;
    }

    public static void sendActionBar(Player player, String message, int duration, reception.Styles style, List<String> colors, Sound sound) {
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] sendActionBar called for player " + (player != null ? player.getName() : "null") + 
            ", message=\"" + message + "\", duration=" + duration + ", style=" + style);

        if (player == null || message == null) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Cannot send action bar with null player or message!");
            return;
        }

        List<ChatColor> finalColors = colorListBuild(colors);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Processed " + (colors != null ? colors.size() : 0) + 
            " colors into " + finalColors.size() + " valid colors");

        if (sound != null && !reception.doesSoundExist(sound)) {
            logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] The requested sound does not exist. Sound cancelled.");
            sound = null;
        }

        // Create the task first
        int result = reception.tempTaskRequest(reception.taskType.temporary, player, message, duration, style, finalColors, sound);

        // Get the task ID from the active task
        UUID playerUUID = player.getUniqueId();
        priorityHandling.ActiveTask activeTask = priorityHandling.getActiveTask(playerUUID);
        UUID taskId = (activeTask != null) ? activeTask.getTask().getTaskId() : UUID.randomUUID();

        // Fire the event with the task ID
        ActionBarSentEvent event = new ActionBarSentEvent(player, message, duration, style, finalColors, sound, taskId);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Firing ActionBarSentEvent for player " + player.getName());
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Action bar event was cancelled by a listener.");
            // If the event is cancelled, stop the task
            if (activeTask != null) {
                activeTask.getTask().stop();
            }
            return;
        }

        switch (result) {
            case 0:
                logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Temporary task was called successfully.");
                break;
            case 1:
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Temporary action bar task was called with invalid style option. Cancelled.");
                break;
            case 2:
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Temporary action bar task was called with invalid usage. Cancelled.");
                break;
            case 3:
                logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Temporary action bar call task resulted in a exception error. Cancelled.");
                break;
        }
    }

    /**
     * Sends a temporary global action bar to all online players.
     * @param message The message to display
     * @param duration The duration in ticks
     * @param style The style of the action bar
     * @param colors The colors to use
     * @param sound The sound to play
     */
    public static void sendGlobalActionBar(String message, int duration, reception.Styles style, List<String> colors, Sound sound) {
        if (message == null) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Cannot send global action bar with null message!");
            return;
        }

        List<ChatColor> finalColors = colorListBuild(colors);
        if (sound != null && !reception.doesSoundExist(sound)) {
            logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] The requested sound does not exist. Sound cancelled.");
            sound = null;
        }

        // Process the request
        int result = reception.globalTaskRequest(reception.taskType.temporary, message, duration, style, finalColors, sound);
        switch (result) {
            case 0:
                logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Global temporary task was called successfully.");
                break;
            case 1:
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Global temporary action bar task was called with invalid style option. Cancelled.");
                break;
            case 2:
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Global temporary action bar task was called with invalid usage. Cancelled.");
                break;
            case 3:
                logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Global temporary action bar call task resulted in a exception error. Cancelled.");
                break;
        }
    }

    public static void startActionBar(Player player, String message, reception.Styles style, List<String> colors, Sound sound) {
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] startActionBar called for player " + (player != null ? player.getName() : "null") + 
            ", message=\"" + message + "\", style=" + style);

        if (player == null || message == null) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Cannot start action bar with null player or message!");
            return;
        }

        List<ChatColor> finalColors = colorListBuild(colors);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Processed " + (colors != null ? colors.size() : 0) + 
            " colors into " + finalColors.size() + " valid colors");

        if (sound != null && !reception.doesSoundExist(sound)) {
            logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] The requested sound does not exist. Sound cancelled.");
            sound = null;
        }

        // Create the task first
        int result = reception.tempTaskRequest(reception.taskType.permanent, player, message, -1, style, finalColors, sound);

        // Get the task ID from the active task
        UUID playerUUID = player.getUniqueId();
        priorityHandling.ActiveTask activeTask = priorityHandling.getActivePermanentTask(playerUUID);
        UUID taskId = (activeTask != null) ? activeTask.getTask().getTaskId() : UUID.randomUUID();

        // Fire the event with the task ID
        ActionBarStartedEvent event = new ActionBarStartedEvent(player, message, -1, style, finalColors, sound, taskId);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Firing ActionBarStartedEvent for player " + player.getName());
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Action bar start event was cancelled by a listener.");
            // If the event is cancelled, stop the task
            if (activeTask != null) {
                activeTask.getTask().stop();
            }
            return;
        }

        switch (result) {
            case 0:
                logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Permanent task was called successfully.");
                break;
            case 1:
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Permanent action bar task was called with invalid style option. Cancelled.");
                break;
            case 2:
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Permanent action bar task was called with invalid usage. Cancelled.");
                break;
            case 3:
                logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Permanent action bar call task resulted in a exception error. Cancelled.");
                break;
        }
    }

    /**
     * Starts a permanent global action bar for all online players.
     * @param message The message to display
     * @param style The style of the action bar
     * @param colors The colors to use
     * @param sound The sound to play
     */
    public static void startGlobalActionBar(String message, reception.Styles style, List<String> colors, Sound sound) {
        if (message == null) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Cannot start global action bar with null message!");
            return;
        }


        List<ChatColor> finalColors = colorListBuild(colors);
        if (sound != null && !reception.doesSoundExist(sound)) {
            logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] The requested sound does not exist. Sound cancelled.");
            sound = null;
        }

        int result = reception.globalTaskRequest(reception.taskType.permanent, message, -1, style, finalColors, sound);
        switch (result) {
            case 0:
                logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Global permanent task was called successfully.");
                break;
            case 1:
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Global permanent action bar task was called with invalid style option. Cancelled.");
                break;
            case 2:
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Global permanent action bar task was called with invalid usage. Cancelled.");
                break;
            case 3:
                logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Global permanent action bar call task resulted in a exception error. Cancelled.");
                break;
        }
    }

    /**
     * Stops an active action bar for the specified player.
     * @param player The player whose action bar should be stopped
     * @return true if an action bar was stopped, false if no action bar was active
     */
    public static boolean stopActionBar(Player player) {
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] stopActionBar called for player " + (player != null ? player.getName() : "null"));

        if (player == null) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Cannot stop action bar with null player!");
            return false;
        }

        UUID playerUUID = player.getUniqueId();
        priorityHandling.ActiveTask activeTask = priorityHandling.getActiveTask(playerUUID);

        if (activeTask == null) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] No active task found for player " + player.getName() + ", nothing to stop");
            return false;
        }

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Stopping active task for player " + player.getName() + 
            ", task type: " + (activeTask.getTask().getRemainingTicks() < 0 ? "permanent" : "temporary"));
        activeTask.getTask().stop();
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Task stopped successfully for player " + player.getName());
        return true;
    }

    /**
     * Modifies an active action bar for the specified player.
     * @param player The player whose action bar should be modified
     * @param newMessage The new message to display
     * @return true if an action bar was modified, false if no action bar was active
     */
    public static boolean modifyActionBar(Player player, String newMessage) {
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] modifyActionBar called for player " + 
            (player != null ? player.getName() : "null") + ", new message=\"" + newMessage + "\"");

        if (player == null || newMessage == null) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Cannot modify action bar with null player or message!");
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        priorityHandling.ActiveTask activeTask = priorityHandling.getActivePermanentTask(playerUUID);

        if (activeTask == null) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] No permanent task found for player " + player.getName() + ", nothing to modify");
            return false;
        }

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Found permanent task for player " + player.getName() + ", stopping it before modification");

        String oldMessage = activeTask.getTask().getMessage();
        UUID taskId = activeTask.getTask().getTaskId(); // Get the task ID to reuse it

        reception.Styles style = activeTask.getTask().getStyle();
        List<ChatColor> colors = activeTask.getTask().getColors();
        Sound sound = activeTask.getTask().getSound();
        boolean isGlobal = activeTask.isGlobal();

        ActionBarTaskModifiedEvent event = new ActionBarTaskModifiedEvent(
            player, oldMessage, newMessage, style, colors, sound, isGlobal, taskId);
        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Firing ActionBarTaskModifiedEvent for player " + player.getName());
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Action bar modification was cancelled by a listener.");
            return false;
        }

        activeTask.getTask().stop();

        List<String> colorStrings = Arrays.asList("WHITE"); // Default color

        logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Starting new action bar with modified message for player " + player.getName());

        // Create the task first
        int result = reception.tempTaskRequest(reception.taskType.permanent, player, newMessage, -1, style, colors, sound);

        // Get the new task
        priorityHandling.ActiveTask newActiveTask = priorityHandling.getActivePermanentTask(playerUUID);

        // If the new task was created successfully, log it
        if (newActiveTask != null) {
            logging.log(logging.LogLevel.DEBUG, "[ActionBarAPI+] Action bar successfully modified for player " + player.getName());
            return true;
        } else {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Failed to create new action bar for player " + player.getName());
            return false;
        }
    }
}
