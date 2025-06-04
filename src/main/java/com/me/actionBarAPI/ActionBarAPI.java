//This is a library for various and many flexible functions for Minecraft's built-in action bar.
//It also features the ability to listen to actionbar events such as action bar sent, or action bar duration ended, etc.
//It provides developers extensive customizablity and power over the action bars.

// What is a permanent action bar task?
// It is a task that will run indefinetly until stopped, canceled or overriden. Application example: Player health, Stats, Server IP, etc.
//
// What is a temporary action bar task?
// It is a task that will run for a certain amount of time, and then will stop. Application example: A broadcast.
//
// Events are raised when:
// A temporary action bar is sent, or when the duration of a temporary action bar is ended.
// As well as when a permanent action bar is started, stopped or modified.
// When a temporary action bar task is paused due to a higher priority action bar task, and then again once it resumes.
// When a temporary action bar task is interrupted and force-stopped due to a temporary action bar task with priority OVERRIDE.
// When a temporary action bar task has been called but will wait a certain time for a currently ongoing higher priority task to complete.
//
// Permanent action bars have different modes of operation:
// Normal:
    // The default mode, Permanent action bar tasks can be modified using certain functions, and,
    // temporary action bar tasks will temporarily stop a permanent action bar until it is finished.
// noInterruption - Will not allow temporary action bar tasks to be run while in this mode.
// noModification - Will not allow the permanent action bar task to be modified while in this mode.
// noModNoInterrupt - noModification + noInterruption
//
// (This is all player specific of course)


package com.me.actionBarAPI;

import com.me.actionBarAPI.events.ActionBarSentEvent;
import com.me.actionBarAPI.tempTasks.reception;
import com.me.actionBarAPI.utils.logging;
import com.me.actionBarAPI.utils.priorityHandling;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import com.me.actionBarAPI.tempTasks.reception;

import java.util.*;

public final class ActionBarAPI extends JavaPlugin {

    private final Map<UUID, BukkitTask> temporaryTasks = new HashMap<>();
    private final Map<UUID, BukkitTask> permanentTasks = new HashMap<>();

    public void unregisterActionBarAPI(Plugin plugin) {
        if (plugin == null) {
            logging.log(logging.LogLevel.ERROR, "[ActionBarAPI+] Cannot unregister null plugin!");
            return;
        }
    }


    public List<ChatColor> colorListBuild(List<String> colors) {
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

    public void sendActionBar(Player player, String message, int duration, reception.Styles style, List<String> colors, priorityHandling.Priority priority, Sound sound) {

        ActionBarSentEvent event = new ActionBarSentEvent(player, message, duration, priority);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
        } else {
            colorListBuild(colors);
            if (reception.tempTaskRequest(player, message, duration, style, priority, sound)==1) {
                logging.log(logging.LogLevel.WARNING, "[ActionBarAPI+] Temporary task was called with invalid style option. ");
            }
        }
    }
}
