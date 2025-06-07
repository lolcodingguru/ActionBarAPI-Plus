package com.codingguru.actionBarAPI.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.codingguru.actionBarAPI.testplugin.Main;

public class logging {
    public static void log(LogLevel level, String message) {
        if (message == null) return;

        switch (level) {
            case ERROR:
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&c&lERROR&r&8] &f" + message));
                break;
            case WARNING:
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6&lWARNING&r&8] &f" + message));
                break;
            case INFO:
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&e&lINFO&r&8] &f" + message));
                break;
            case SUCCESS:
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&a&lSUCCESS&r&8] &f" + message));
                break;
            case OUTLINE:
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + message));
                break;
            case TEST_SUCCESS:
                if(Main.testInProgress) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&a&lTEST-Success&r&8] &f" + message));
                }
                break;
            case TEST_FAIL:
                if(Main.testInProgress) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&c&lTEST-Fail&r&8] &f" + message));
                }
                break;
            case DEBUG:
                if(false) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&b&lDEBUG&r&8] &f" + message));
                }
                break;
        }
    }

    public enum LogLevel { ERROR, WARNING, INFO, SUCCESS, OUTLINE, DEBUG, TEST_SUCCESS, TEST_FAIL, EVENT_LISTENER }
}
