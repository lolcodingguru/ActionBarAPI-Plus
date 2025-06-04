// 0 - Executed.
// 1 - Invalid style.
// 2 - Other invalid usage.
// 3 - Exception error

package com.me.actionBarAPI.tempTasks;

import com.me.actionBarAPI.utils.priorityHandling;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class reception {

    public boolean doesSoundExist(Sound sound) {
        try {
            Sound.valueOf(sound.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static int tempTaskRequest(Player player, String message, int duration, Styles style, priorityHandling.Priority priority, Sound sound) {
        if (player != null && duration >= 0 && style != null && doesSoundExist(sound)) {
            try {
                switch (style) {
                    case rolling:
                        return 0;
                    case rainbow:
                        return 0;
                    case flashing:
                        return 0;
                    default:
                        return 1;
                }
            } catch (Exception e) {
                return 3;
            }
        } else{
            return 2;
        }
    }

public enum Styles {rolling, rainbow, flashing}

}