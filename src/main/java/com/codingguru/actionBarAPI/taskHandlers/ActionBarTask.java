package com.codingguru.actionBarAPI.taskHandlers;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import java.util.List;
import java.util.UUID;

public interface ActionBarTask {
    void start();
    void stop();
    void pause();
    void resume();
    boolean isRunning();
    int getRemainingTicks();
    UUID getPlayerUUID();
    UUID getTaskId();

    String getMessage();
    reception.Styles getStyle();
    List<ChatColor> getColors();
    Sound getSound();
}
