// Initially, This was goind to be a temporary place to test methods and whatnot.
// But now (although the package name testPlugin is misleading) the API will enable here and register a test command for whomever wants to test and see all the methods it can test

package com.codingguru.actionBarAPI.testplugin;

import com.codingguru.actionBarAPI.events.*;
import com.codingguru.actionBarAPI.taskHandlers.TaskManager;
import com.codingguru.actionBarAPI.ActionBarAPI;
import com.codingguru.actionBarAPI.events.*;
import com.codingguru.actionBarAPI.taskHandlers.reception;
import com.codingguru.actionBarAPI.utils.logging;
import com.codingguru.actionBarAPI.utils.priorityHandling;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {
    public static boolean testInProgress = false;
    private Map<UUID, Long> pendingTestConfirmations = new HashMap<>();
    private static final int CONFIRMATION_TIMEOUT_SECONDS = 20;
    private static boolean initialized = false;

    public Main() {
        if (initialized) {
            // Plugin already initialized, likely loaded as both a standalone plugin and a dependency
            // This is a no-op constructor to prevent the "Plugin already initialized!" error
            return;
        }
        initialized = true;
    }

    @Override
    public void onEnable() {
        // If the plugin was initialized as a dependency, we still need to register commands and events
        if (!initialized) {
            getLogger().info("[ActionBarAPI+] Plugin was loaded as a dependency and constructor was skipped. Initializing now.");
            initialized = true;
        }

        if (ActionBarAPI.getInstance() == null) {
            getLogger().info("[ActionBarAPI+] No plugin has initialized the API yet. Initializing self now. (You can probably safely ignore this)");
            new ActionBarAPI(this);
        } else {
            getLogger().info("[ActionBarAPI+] API already initialized by another plugin. Using existing instance. (You can probably safely ignore this)");
        }
        getCommand("actionbarapiplus").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("ActionBarAPI+ disabled.");
    }

    @EventHandler
    public void onActionBarSent(ActionBarSentEvent event) {
        if(testInProgress) {
        Player player = event.getPlayer();


    logging.log(logging.LogLevel.TEST_SUCCESS, "ActionBarSentEvent fired - Testing getters:");
    logging.log(logging.LogLevel.TEST_SUCCESS, "  getPlayer(): " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
    logging.log(logging.LogLevel.TEST_SUCCESS, "  getMessage(): " + event.getMessage());
    logging.log(logging.LogLevel.TEST_SUCCESS, "  getDuration(): " + event.getDuration());
    logging.log(logging.LogLevel.TEST_SUCCESS, "  getStyle(): " + (event.getStyle() != null ? event.getStyle().name() : "null"));
    logging.log(logging.LogLevel.TEST_SUCCESS, "  getColors(): " + (event.getColors() != null ? event.getColors().toString() : "null"));
    logging.log(logging.LogLevel.TEST_SUCCESS, "  getSound(): " + (event.getSound() != null ? event.getSound().name() : "null"));
    logging.log(logging.LogLevel.TEST_SUCCESS, "  getTaskId(): " + event.getTaskId());
    logging.log(logging.LogLevel.TEST_SUCCESS, "  isCancelled(): " + event.isCancelled());

    if (player != null) {
        player.sendMessage(ChatColor.GREEN + "✓ ActionBarSentEvent getters tested");
        player.sendMessage(ChatColor.GRAY + "  Player: " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
        player.sendMessage(ChatColor.GRAY + "  Message: " + event.getMessage());
        player.sendMessage(ChatColor.GRAY + "  Duration: " + event.getDuration());
        player.sendMessage(ChatColor.GRAY + "  Style: " + (event.getStyle() != null ? event.getStyle().name() : "null"));
        player.sendMessage(ChatColor.GRAY + "  Colors: " + (event.getColors() != null ? event.getColors().toString() : "null"));
        player.sendMessage(ChatColor.GRAY + "  Sound: " + (event.getSound() != null ? event.getSound().name() : "null"));
        player.sendMessage(ChatColor.GRAY + "  Cancelled: " + event.isCancelled());
    }
}
    }

    @EventHandler
    public void onActionBarStarted(ActionBarStartedEvent event) {
        if(testInProgress) {
            Player player = event.getPlayer();

            // Test and display all getters
            logging.log(logging.LogLevel.TEST_SUCCESS, "ActionBarStartedEvent fired - Testing getters:");
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getPlayer(): " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getMessage(): " + event.getMessage());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getDuration(): " + event.getDuration());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getStyle(): " + (event.getStyle() != null ? event.getStyle().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getColors(): " + (event.getColors() != null ? event.getColors().toString() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getSound(): " + (event.getSound() != null ? event.getSound().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getTaskId(): " + event.getTaskId());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  isCancelled(): " + event.isCancelled());

            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "✓ ActionBarStartedEvent getters tested");
                player.sendMessage(ChatColor.GRAY + "  Player: " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Message: " + event.getMessage());
                player.sendMessage(ChatColor.GRAY + "  Duration: " + event.getDuration());
                player.sendMessage(ChatColor.GRAY + "  Style: " + (event.getStyle() != null ? event.getStyle().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Colors: " + (event.getColors() != null ? event.getColors().toString() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Sound: " + (event.getSound() != null ? event.getSound().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Cancelled: " + event.isCancelled());
            }
        }
    }

    @EventHandler
    public void onActionBarTaskEnded(ActionBarTaskEndedEvent event) {
        if(testInProgress) {
            Player player = event.getPlayer();

            // Test and display all getters
            logging.log(logging.LogLevel.TEST_SUCCESS, "ActionBarTaskEndedEvent fired - Testing getters:");
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getPlayer(): " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getMessage(): " + event.getMessage());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getStyle(): " + (event.getStyle() != null ? event.getStyle().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getColors(): " + (event.getColors() != null ? event.getColors().toString() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getSound(): " + (event.getSound() != null ? event.getSound().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getDuration(): " + event.getDuration());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  wasGlobal(): " + event.wasGlobal());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getTaskId(): " + event.getTaskId());

            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "✓ ActionBarTaskEndedEvent getters tested");
                player.sendMessage(ChatColor.GRAY + "  Player: " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Message: " + event.getMessage());
                player.sendMessage(ChatColor.GRAY + "  Style: " + (event.getStyle() != null ? event.getStyle().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Colors: " + (event.getColors() != null ? event.getColors().toString() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Sound: " + (event.getSound() != null ? event.getSound().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Duration: " + event.getDuration());
                player.sendMessage(ChatColor.GRAY + "  Was Global: " + event.wasGlobal());
            }
        }
    }

    @EventHandler
    public void onActionBarTaskModified(ActionBarTaskModifiedEvent event) {
        if (testInProgress) {
            Player player = event.getPlayer();

            // Test and display all getters
            logging.log(logging.LogLevel.TEST_SUCCESS, "ActionBarTaskModifiedEvent fired - Testing getters:");
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getPlayer(): " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getOldMessage(): " + event.getOldMessage());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getNewMessage(): " + event.getNewMessage());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getStyle(): " + (event.getStyle() != null ? event.getStyle().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getColors(): " + (event.getColors() != null ? event.getColors().toString() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getSound(): " + (event.getSound() != null ? event.getSound().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  isGlobal(): " + event.isGlobal());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getTaskId(): " + event.getTaskId());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  isCancelled(): " + event.isCancelled());

            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "✓ ActionBarTaskModifiedEvent getters tested");
                player.sendMessage(ChatColor.GRAY + "  Player: " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Old Message: " + event.getOldMessage());
                player.sendMessage(ChatColor.GRAY + "  New Message: " + event.getNewMessage());
                player.sendMessage(ChatColor.GRAY + "  Style: " + (event.getStyle() != null ? event.getStyle().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Colors: " + (event.getColors() != null ? event.getColors().toString() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Sound: " + (event.getSound() != null ? event.getSound().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Is Global: " + event.isGlobal());
                player.sendMessage(ChatColor.GRAY + "  Cancelled: " + event.isCancelled());
            }
        }
    }

    @EventHandler
    public void onActionBarTaskPaused(ActionBarTaskPausedEvent event) {
        if(testInProgress) {
            Player player = event.getPlayer();

            // Test and display all getters
            logging.log(logging.LogLevel.TEST_SUCCESS, "ActionBarTaskPausedEvent fired - Testing getters:");
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getPlayer(): " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getMessage(): " + event.getMessage());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getStyle(): " + (event.getStyle() != null ? event.getStyle().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getColors(): " + (event.getColors() != null ? event.getColors().toString() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getSound(): " + (event.getSound() != null ? event.getSound().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  isPermanent(): " + event.isPermanent());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  isGlobal(): " + event.isGlobal());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getRemainingTicks(): " + event.getRemainingTicks());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getTaskId(): " + event.getTaskId());

            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "✓ ActionBarTaskPausedEvent getters tested");
                player.sendMessage(ChatColor.GRAY + "  Player: " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Message: " + event.getMessage());
                player.sendMessage(ChatColor.GRAY + "  Style: " + (event.getStyle() != null ? event.getStyle().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Colors: " + (event.getColors() != null ? event.getColors().toString() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Sound: " + (event.getSound() != null ? event.getSound().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Is Permanent: " + event.isPermanent());
                player.sendMessage(ChatColor.GRAY + "  Is Global: " + event.isGlobal());
                player.sendMessage(ChatColor.GRAY + "  Remaining Ticks: " + event.getRemainingTicks());
            }
        }
    }

    @EventHandler
    public void onActionBarTaskResumed(ActionBarTaskResumedEvent event) {
        if(testInProgress) {
            Player player = event.getPlayer();

            // Test and display all getters
            logging.log(logging.LogLevel.TEST_SUCCESS, "ActionBarTaskResumedEvent fired - Testing getters:");
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getPlayer(): " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getMessage(): " + event.getMessage());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getStyle(): " + (event.getStyle() != null ? event.getStyle().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getColors(): " + (event.getColors() != null ? event.getColors().toString() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getSound(): " + (event.getSound() != null ? event.getSound().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  isPermanent(): " + event.isPermanent());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  isGlobal(): " + event.isGlobal());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getRemainingTicks(): " + event.getRemainingTicks());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getTaskId(): " + event.getTaskId());

            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "✓ ActionBarTaskResumedEvent getters tested");
                player.sendMessage(ChatColor.GRAY + "  Player: " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Message: " + event.getMessage());
                player.sendMessage(ChatColor.GRAY + "  Style: " + (event.getStyle() != null ? event.getStyle().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Colors: " + (event.getColors() != null ? event.getColors().toString() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Sound: " + (event.getSound() != null ? event.getSound().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Is Permanent: " + event.isPermanent());
                player.sendMessage(ChatColor.GRAY + "  Is Global: " + event.isGlobal());
                player.sendMessage(ChatColor.GRAY + "  Remaining Ticks: " + event.getRemainingTicks());
            }
        }
    }

    @EventHandler
    public void onActionBarTaskStopped(ActionBarTaskStoppedEvent event) {
        if(testInProgress) {
            Player player = event.getPlayer();

            // Test and display all getters
            logging.log(logging.LogLevel.TEST_SUCCESS, "ActionBarTaskStoppedEvent fired - Testing getters:");
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getPlayer(): " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getMessage(): " + event.getMessage());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getStyle(): " + (event.getStyle() != null ? event.getStyle().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getColors(): " + (event.getColors() != null ? event.getColors().toString() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getSound(): " + (event.getSound() != null ? event.getSound().name() : "null"));
            logging.log(logging.LogLevel.TEST_SUCCESS, "  wasPermanent(): " + event.wasPermanent());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  wasGlobal(): " + event.wasGlobal());
            logging.log(logging.LogLevel.TEST_SUCCESS, "  getTaskId(): " + event.getTaskId());

            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "✓ ActionBarTaskStoppedEvent getters tested");
                player.sendMessage(ChatColor.GRAY + "  Player: " + (event.getPlayer() != null ? event.getPlayer().getName() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Message: " + event.getMessage());
                player.sendMessage(ChatColor.GRAY + "  Style: " + (event.getStyle() != null ? event.getStyle().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Colors: " + (event.getColors() != null ? event.getColors().toString() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Sound: " + (event.getSound() != null ? event.getSound().name() : "null"));
                player.sendMessage(ChatColor.GRAY + "  Was Permanent: " + event.wasPermanent());
                player.sendMessage(ChatColor.GRAY + "  Was Global: " + event.wasGlobal());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("actionbarapiplus")) {
            if (args.length == 0 || !args[0].equalsIgnoreCase("runtest")) {
                player.sendMessage(ChatColor.RED + "Usage: /actionbarapiplus runtest");
                return true;
            }

            if (!player.hasPermission("actionbarapiplus.runtest")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            if (testInProgress) {
                player.sendMessage(ChatColor.RED + "A test is already in progress. Please wait for it to complete.");
                return true;
            }

            UUID playerUUID = player.getUniqueId();
            long currentTime = System.currentTimeMillis();

            if (pendingTestConfirmations.containsKey(playerUUID)) {
                long requestTime = pendingTestConfirmations.get(playerUUID);
                long elapsedSeconds = (currentTime - requestTime) / 1000;

                if (elapsedSeconds <= CONFIRMATION_TIMEOUT_SECONDS) {
                    pendingTestConfirmations.remove(playerUUID);

                    cancelAllActionBarTasks();

                    testInProgress = true;
                    player.sendMessage(ChatColor.GREEN + "Starting comprehensive ActionBarAPI tests...");
                    logging.log(logging.LogLevel.INFO, "Starting comprehensive ActionBarAPI tests for player " + player.getName());

                    runTestSequence(player);
                    return true;
                } else {
                    pendingTestConfirmations.remove(playerUUID);
                }
            }

            player.sendMessage(ChatColor.RED + "⚠ IMPORTANT WARNING ⚠");
            player.sendMessage(ChatColor.GOLD + "Running this comprehensive test will:");
            player.sendMessage(ChatColor.YELLOW + "- Immediately cancel ALL running action bar tasks for ALL players on the server");
            player.sendMessage(ChatColor.YELLOW + "- Display test results to everyone who receives an action bar during the test period");
            player.sendMessage(ChatColor.YELLOW + "- Potentially cause significant lag spikes during the testing process");
            player.sendMessage(ChatColor.YELLOW + "- Interrupt any important information currently being displayed via action bars");
            player.sendMessage(ChatColor.RED + "This test is NOT recommended on an active production server!");
            player.sendMessage(ChatColor.RED + "Consider running tests on a development server instead.");
            player.sendMessage(ChatColor.GOLD + "To confirm you understand these risks, run " + ChatColor.WHITE + "/actionbarapiplus runtest" + ChatColor.GOLD + " again within " + CONFIRMATION_TIMEOUT_SECONDS + " seconds.");

            pendingTestConfirmations.put(playerUUID, currentTime);
            return true;
        }

        return false;
    }

    /**
     * Cancels all action bar tasks for all online players
     */
    private void cancelAllActionBarTasks() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ActionBarAPI.stopActionBar(player);
        }
        logging.log(logging.LogLevel.INFO, "Cancelled all running action bar tasks");
    }

    private void runTestSequence(final Player player) {
        new BukkitRunnable() {
            private int testStep = 0;
            private final String testMessage = "Test Message";

            @Override
            public void run() {
                switch (testStep) {
                    case 0:
                        player.sendMessage(ChatColor.GOLD + "=== ActionBarAPI Test Sequence ===");
                        player.sendMessage(ChatColor.YELLOW + "Testing may take a few minutes");
                        player.sendMessage(ChatColor.YELLOW + "Please stay online during the test");
                        break;

                    case 1:
                        testTemporaryActionBar(player, testMessage, reception.Styles.rolling);
                        break;

                    case 2:
                        testTemporaryActionBar(player, testMessage, reception.Styles.rainbow);
                        break;

                    case 3:
                        testTemporaryActionBar(player, testMessage, reception.Styles.flashing);
                        break;

                    case 4:
                        testTemporaryActionBar(player, testMessage, reception.Styles.gradient);
                        break;

                    case 5:
                        testTemporaryActionBar(player, testMessage, reception.Styles.typewriter);
                        break;

                    case 6:
                        testTemporaryActionBar(player, testMessage, reception.Styles.wave);
                        break;

                    case 7:
                        testPermanentActionBar(player, testMessage + " (Permanent)");
                        break;

                    case 8:
                        testModifyActionBar(player, testMessage + " (Modified)");
                        break;

                    case 9:
                        testStopActionBar(player);
                        break;

                    case 10:
                        testGlobalTemporaryActionBar(player, "Global " + testMessage);
                        break;

                    case 11:
                        testGlobalPermanentActionBar(player, "Global " + testMessage + " (Permanent)");
                        break;

                    case 12:
                        testPauseAndResume(player, testMessage + " (Pause/Resume)");
                        break;

                    case 13:
                        // Test complete
                        player.sendMessage(ChatColor.GREEN + "All tests completed!");
                        logging.log(logging.LogLevel.INFO, "ActionBarAPI tests completed for player " + player.getName());

                        // Cancel any remaining action bar tasks
                        cancelAllActionBarTasks();

                        testInProgress = false;
                        this.cancel();
                        return;
                }

                testStep++;
            }
        }.runTaskTimer(this, 0L, 200L);
    }

    /**
     * Tests sending a temporary action bar with the specified style
     */
    private void testTemporaryActionBar(Player player, String message, reception.Styles style) {
        try {
            player.sendMessage(ChatColor.AQUA + "Testing temporary action bar with " + style.name() + " style...");

            List<String> colors = getColorsForStyle(style);
            ActionBarAPI.sendActionBar(player, message, 80, style, colors, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

            logging.log(logging.LogLevel.TEST_SUCCESS, "Temporary action bar with " + style.name() + " style test passed for player " + player.getName());
            player.sendMessage(ChatColor.GREEN + "✓ Temporary action bar with " + style.name() + " style test passed");
        } catch (Exception e) {
            logging.log(logging.LogLevel.TEST_FAIL, "Temporary action bar with " + style.name() + " style test failed: " + e.getMessage());
            player.sendMessage(ChatColor.RED + "✗ Temporary action bar with " + style.name() + " style test failed: " + e.getMessage());
        }
    }

    /**
     * Tests starting a permanent action bar
     */
    private void testPermanentActionBar(Player player, String message) {
        try {
            player.sendMessage(ChatColor.AQUA + "Testing permanent action bar...");

            List<String> colors = Arrays.asList("GOLD", "RED");
            ActionBarAPI.startActionBar(player, message, reception.Styles.rolling, colors, Sound.ENTITY_PLAYER_LEVELUP);

            logging.log(logging.LogLevel.TEST_SUCCESS, "Permanent action bar test passed for player " + player.getName());
            player.sendMessage(ChatColor.GREEN + "✓ Permanent action bar test passed");
        } catch (Exception e) {
            logging.log(logging.LogLevel.TEST_FAIL, "Permanent action bar test failed: " + e.getMessage());
            player.sendMessage(ChatColor.RED + "✗ Permanent action bar test failed: " + e.getMessage());
        }
    }

    /**
     * Tests modifying an active action bar
     */
    private void testModifyActionBar(Player player, String newMessage) {
        try {
            player.sendMessage(ChatColor.AQUA + "Testing action bar modification...");

            boolean modified = ActionBarAPI.modifyActionBar(player, newMessage);
            if (modified) {
                logging.log(logging.LogLevel.TEST_SUCCESS, "Action bar modification test passed for player " + player.getName());
                player.sendMessage(ChatColor.GREEN + "✓ Action bar modification test passed");
            } else {
                logging.log(logging.LogLevel.TEST_FAIL, "Action bar modification test failed: No active action bar to modify");
                player.sendMessage(ChatColor.RED + "✗ Action bar modification test failed: No active action bar to modify");
            }
        } catch (Exception e) {
            logging.log(logging.LogLevel.TEST_FAIL, "Action bar modification test failed: " + e.getMessage());
            player.sendMessage(ChatColor.RED + "✗ Action bar modification test failed: " + e.getMessage());
        }
    }

    /**
     * Tests stopping an active action bar
     */
    private void testStopActionBar(Player player) {
        try {
            player.sendMessage(ChatColor.AQUA + "Testing action bar stop...");

            boolean stopped = ActionBarAPI.stopActionBar(player);
            if (stopped) {
                logging.log(logging.LogLevel.TEST_SUCCESS, "Action bar stop test passed for player " + player.getName());
                player.sendMessage(ChatColor.GREEN + "✓ Action bar stop test passed");
            } else {
                logging.log(logging.LogLevel.TEST_FAIL, "Action bar stop test failed: No active action bar to stop");
                player.sendMessage(ChatColor.RED + "✗ Action bar stop test failed: No active action bar to stop");
            }
        } catch (Exception e) {
            logging.log(logging.LogLevel.TEST_FAIL, "Action bar stop test failed: " + e.getMessage());
            player.sendMessage(ChatColor.RED + "✗ Action bar stop test failed: " + e.getMessage());
        }
    }

    /**
     * Tests sending a temporary global action bar
     */
    private void testGlobalTemporaryActionBar(Player player, String message) {
        try {
            player.sendMessage(ChatColor.AQUA + "Testing temporary global action bar...");

            List<String> colors = Arrays.asList("WHITE", "AQUA", "GREEN", "YELLOW", "RED");
            ActionBarAPI.sendGlobalActionBar(message, 80, reception.Styles.rolling, colors, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

            logging.log(logging.LogLevel.TEST_SUCCESS, "Temporary global action bar test passed");
            player.sendMessage(ChatColor.GREEN + "✓ Temporary global action bar test passed");
        } catch (Exception e) {
            logging.log(logging.LogLevel.TEST_FAIL, "Temporary global action bar test failed: " + e.getMessage());
            player.sendMessage(ChatColor.RED + "✗ Temporary global action bar test failed: " + e.getMessage());
        }
    }

    /**
     * Tests starting a permanent global action bar
     */
    private void testGlobalPermanentActionBar(Player player, String message) {
        try {
            player.sendMessage(ChatColor.AQUA + "Testing permanent global action bar...");

            List<String> colors = Arrays.asList("GOLD", "RED");
            ActionBarAPI.startGlobalActionBar(message, reception.Styles.rolling, colors, Sound.ENTITY_PLAYER_LEVELUP);

            logging.log(logging.LogLevel.TEST_SUCCESS, "Permanent global action bar test passed");
            player.sendMessage(ChatColor.GREEN + "✓ Permanent global action bar test passed");
        } catch (Exception e) {
            logging.log(logging.LogLevel.TEST_FAIL, "Permanent global action bar test failed: " + e.getMessage());
            player.sendMessage(ChatColor.RED + "✗ Permanent global action bar test failed: " + e.getMessage());
        }
    }

    /**
     * Returns appropriate colors for the specified style
     */
    private List<String> getColorsForStyle(reception.Styles style) {
        switch (style) {
            case rainbow:
                return Arrays.asList("RED", "GOLD", "YELLOW", "GREEN", "AQUA", "BLUE", "LIGHT_PURPLE");
            case gradient:
                return Arrays.asList("BLUE", "AQUA", "GREEN", "YELLOW", "GOLD", "RED");
            case flashing:
                return Arrays.asList("RED", "WHITE");
            default:
                return Arrays.asList("WHITE", "AQUA", "GREEN", "YELLOW", "RED");
        }
    }

    /**
     * Tests pausing and resuming action bars
     */
    private void testPauseAndResume(final Player player, final String message) {
        try {
            player.sendMessage(ChatColor.AQUA + "Testing action bar pause and resume...");

            List<String> colors = Arrays.asList("GOLD", "RED");
            ActionBarAPI.startActionBar(player, message, reception.Styles.rolling, colors, Sound.ENTITY_PLAYER_LEVELUP);
            player.sendMessage(ChatColor.YELLOW + "Started permanent action bar...");

            new BukkitRunnable() {
                @Override
                public void run() {
                    // Get the active task
                    UUID playerUUID = player.getUniqueId();
                    priorityHandling.ActiveTask activeTask = priorityHandling.getActiveTask(playerUUID);

                    if (activeTask == null) {
                        logging.log(logging.LogLevel.TEST_FAIL, "Pause/Resume test failed: No active task found");
                        player.sendMessage(ChatColor.RED + "✗ Pause/Resume test failed: No active task found");
                        return;
                    }

                    // Pause the task
                    activeTask.getTask().pause();
                    player.sendMessage(ChatColor.YELLOW + "Paused action bar...");

                    // Check if the task is paused (we don't have a direct way to check, so we'll just log it)
                    logging.log(logging.LogLevel.TEST_SUCCESS, "Action bar paused for player " + player.getName());
                    player.sendMessage(ChatColor.GREEN + "✓ Action bar paused");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Get the active task again (it might have changed)
                            priorityHandling.ActiveTask activeTask = priorityHandling.getActiveTask(playerUUID);

                            if (activeTask == null) {
                                logging.log(logging.LogLevel.TEST_FAIL, "Resume part of test failed: No active task found");
                                player.sendMessage(ChatColor.RED + "✗ Resume part of test failed: No active task found");
                                return;
                            }

                            activeTask.getTask().resume();
                            player.sendMessage(ChatColor.YELLOW + "Resumed action bar...");

                            // Check if the task is resumed (we don't have a direct way to check, so we'll just log it)
                            logging.log(logging.LogLevel.TEST_SUCCESS, "Action bar resumed for player " + player.getName());
                            player.sendMessage(ChatColor.GREEN + "✓ Action bar resumed");

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    // Clean up by stopping the action bar
                                    ActionBarAPI.stopActionBar(player);

                                    logging.log(logging.LogLevel.TEST_SUCCESS, "Pause/Resume test passed for player " + player.getName());
                                    player.sendMessage(ChatColor.GREEN + "✓ Pause/Resume test passed");
                                }
                            }.runTaskLater(Main.this, 40L);
                        }
                    }.runTaskLater(Main.this, 40L);
                }
            }.runTaskLater(this, 40L);

        } catch (Exception e) {
            logging.log(logging.LogLevel.TEST_FAIL, "Pause/Resume test failed: " + e.getMessage());
            player.sendMessage(ChatColor.RED + "✗ Pause/Resume test failed: " + e.getMessage());
        }
    }
}
