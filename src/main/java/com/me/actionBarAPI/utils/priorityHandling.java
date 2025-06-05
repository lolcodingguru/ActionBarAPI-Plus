// Priority meanings:
// Low to High will be handled in a queue basis.
// if you are in a lower priority of another task, you will wait until the other task is completed.
// If you are in a higher priority, lower priority task will stop until your task is completed.
// OVERRIDE - Overrides all other priorities, and interrupts any running action bar tasks (other tasks will not continue even after requested task is completed).

// Meaning of returned integers:
// 0 - No interference with other action bar tasks found, permission to continue granted.
// 1 - Lower priority task is running, permission to continue granted.
// 2 - Higher priority task is running, WAIT until higher priority task is completed. (Check again every tick)
// 3 - Interference with other tasks detected, permission to continue granted with OVERRIDE priority.
// 4 - Non-interruptible task detected - permission to continue declined.
// maybe ill make it return how many ticks to delay the runnable or smthn , i think theres a better way to do this
package com.me.actionBarAPI.utils;

public class priorityHandling {
    public int getPermission(Priority priority) {

    }

    public enum Priority { LOW, MEDIUM, HIGH, OVERRIDE }

}
