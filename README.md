https://www.spigotmc.org/resources/125822/

# ActionBarAPI+

## Introduction
ActionBarAPI+ is a comprehensive library for Minecraft servers that provides control over Minecraft's built-in action bar system. This API allows developers to create, manage, and customize action bars with various styles, colors, and behaviors.

I created this API for myself and for my own projects, but I'm sharing it with the community for those who might find it useful. While it works on Minecraft versions 1.10-1.21, please note that not every feature has been thoroughly tested across all versions. As an open-source project, contributions are welcome to improve and extend its functionality.

I created this assuming that I will optimize it and fix any issues I encounter along the way while using it to develop other plugins, I have not tested everything. Read the Disclaimer for more info.

## Features
- Send temporary or permanent action bars to players
- Apply various visual styles (rolling, rainbow, flashing, gradient, typewriter, wave)
- Customize colors and sounds
- Global action bars for all players
- Modify existing action bars
- Pause and resume action bars
- Listen to action bar events
- Compatible with Minecraft versions 1.10-1.21

## Installation

### Adding as a dependency
There is no Maven or Gradle Deployment at the moment.

### Manual Installation
1. Download the latest release from the spigot page.
2. Add the JAR file to your project's dependencies/libraries in your IDE. 
3. Initialize like shown below

## API Documentation

### Initialization
To use the API in your plugin, you need to initialize it in your `onEnable` method:

```java
import com.codingguru.actionBarAPI.ActionBarAPI;

@Override
public void onEnable() {
    // Initialize ActionBarAPI
    new ActionBarAPI(this);
}
```

### Methods

#### Temporary Action Bars
Send an action bar that disappears after a specified duration.

```java
/**
 * Sends a temporary action bar to a player
 * @param player The player to send the action bar to
 * @param message The message to display
 * @param duration The duration in ticks (20 ticks = 1 second)
 * @param style The style of the action bar
 * @param colors List of color names to use
 * @param sound The sound to play (can be null)
 */
ActionBarAPI.sendActionBar(Player player, String message, int duration, reception.Styles style, List<String> colors, Sound sound);
```

#### Permanent Action Bars
Start an action bar that remains until explicitly stopped or replaced.

```java
/**
 * Starts a permanent action bar for a player
 * @param player The player to send the action bar to
 * @param message The message to display
 * @param style The style of the action bar
 * @param colors List of color names to use
 * @param sound The sound to play (can be null)
 */
ActionBarAPI.startActionBar(Player player, String message, reception.Styles style, List<String> colors, Sound sound);
```

#### Global Action Bars
Send an action bar to all online players.

```java
/**
 * Sends a temporary global action bar to all online players
 * @param message The message to display
 * @param duration The duration in ticks (20 ticks = 1 second)
 * @param style The style of the action bar
 * @param colors List of color names to use
 * @param sound The sound to play (can be null)
 */
ActionBarAPI.sendGlobalActionBar(String message, int duration, reception.Styles style, List<String> colors, Sound sound);

/**
 * Starts a permanent global action bar for all online players
 * @param message The message to display
 * @param style The style of the action bar
 * @param colors List of color names to use
 * @param sound The sound to play (can be null)
 */
ActionBarAPI.startGlobalActionBar(String message, reception.Styles style, List<String> colors, Sound sound);
```

#### Stopping Action Bars
Stop an active action bar for a player.

```java
/**
 * Stops an active action bar for the specified player
 * @param player The player whose action bar should be stopped
 * @return true if an action bar was stopped, false if no action bar was active
 */
ActionBarAPI.stopActionBar(Player player);
```

#### Modifying Action Bars
Change the message of an active permanent action bar.

```java
/**
 * Modifies an active action bar for the specified player
 * @param player The player whose action bar should be modified
 * @param newMessage The new message to display
 * @return true if an action bar was modified, false if no action bar was active
 */
ActionBarAPI.modifyActionBar(Player player, String newMessage);
```

### Styles
The API provides several predefined styles for action bars:

```java
reception.Styles.rolling    // Text rolls in from the right
reception.Styles.rainbow    // Text cycles through rainbow colors
reception.Styles.flashing   // Text flashes between colors
reception.Styles.gradient   // Text displays a color gradient
reception.Styles.typewriter // Text appears character by character
reception.Styles.wave       // Text displays a wave effect
```

### Events
The API fires various events that you can listen to:

```java
// Fired when an action bar is sent to a player
ActionBarSentEvent

// Fired when a permanent action bar is started
ActionBarStartedEvent

// Fired when an action bar task ends naturally (duration expires)
ActionBarTaskEndedEvent

// Fired when an action bar is modified
ActionBarTaskModifiedEvent

// Fired when an action bar is paused
ActionBarTaskPausedEvent

// Fired when a paused action bar is resumed
ActionBarTaskResumedEvent

// Fired when an action bar is stopped manually
ActionBarTaskStoppedEvent
```

## Usage Examples

### Basic Usage Example
There is a video showcasing the result of this usage example.


```java
// Import necessary classes

import com.codingguru.actionBarAPI.ActionBarAPI;
import com.codingguru.actionBarAPI.events.ActionBarSentEvent;
import com.codingguru.actionBarAPI.events.ActionBarTaskEndedEvent;
import com.codingguru.actionBarAPI.taskHandlers.reception;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.UUID;

@Override
public void onEnable() {

    Bukkit.getPluginManager().registerEvents(this, this);
    new ActionBarAPI(this);


}

// !!!
// This is just a Usage example, it assumes player full health initially
// and does not handle regen event
// and does not handle player death either.
// Although those could be implemented similarly to this.
// !!!


@EventHandler
public void onPlayerJoin(PlayerJoinEvent event){
    Player player = event.getPlayer();
    ActionBarAPI.sendActionBar(player,
            "Welcome to the server",
            100, reception.Styles.rainbow,
            Arrays.asList(/*Leave empty because rainbow style ignores provided colors and just uses rainbow*/),
            Sound.ENTITY_PLAYER_LEVELUP
    );
}

public UUID welcomeEventID;
String health ="Health: ❤❤❤❤❤❤❤❤❤❤";

@EventHandler
public void ActionBarSentEvent(ActionBarSentEvent event){
    // Confirm this is the event sent on player join
    if (event.getMessage().equals("Welcome to the server")) {
        welcomeEventID = event.getTaskId(); // store ID of event
    }
}

@EventHandler
public void ActionBarTaskEndedEvent(ActionBarTaskEndedEvent event){
    // check if what has ended is the welcome message task
    if(event.getTaskId()==welcomeEventID) {
        // we confirmed the welcome message has ended, we can start health bar task.
        ActionBarAPI.startActionBar(
                event.getPlayer(),
                health,
                reception.Styles.flashing,
                Arrays.asList("RED", "WHITE"),
                Sound.ENTITY_PLAYER_LEVELUP);
    }
}

@EventHandler
public void playerHurt(EntityDamageEvent event){
    if(!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();

    // calculate remaining health after damage (in heart units) one heart = 2 health points
    double currentHealth = player.getHealth();
    double damageAmount = event.getFinalDamage();
    double healthAfterDamage = Math.max(0, currentHealth - damageAmount); // make sure result isnt negative (otherwise player is dead)
    int heartsAfterDamage = (int) Math.floor(healthAfterDamage / 2.0);
    // I do not have a half heart emoji so in case of half heart, we just truncate to no heart

    Bukkit.getScheduler().runTask(this, () -> {
        // build until heartsafterdamage is fulfilled
        StringBuilder newhealth = new StringBuilder();
        for (int i = 0; i < heartsAfterDamage; i++) {
            newhealth.append("❤");
        }

        // modify already started action bar
        ActionBarAPI.modifyActionBar(player, "Health: " + newhealth.toString());
    });
}

@Override
public void onDisable() {
    // Plugin shutdown logic
}
```

### Event Listening

```java
import com.codingguru.actionBarAPI.events.ActionBarSentEvent;
import com.codingguru.actionBarAPI.events.ActionBarTaskEndedEvent;
import com.codingguru.actionBarAPI.events.ActionBarTaskModifiedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ActionBarListener implements Listener {

    @EventHandler
    public void onActionBarSent(ActionBarSentEvent event) {
        // This event is fired when an action bar is sent to a player
        System.out.println("Action bar sent to " + event.getPlayer().getName() + ": " + event.getMessage());
    }

    @EventHandler
    public void onActionBarTaskEnded(ActionBarTaskEndedEvent event) {
        // This event is fired when an action bar task ends naturally (duration expires)
        System.out.println("Action bar ended for " + event.getPlayer().getName());
    }

    @EventHandler
    public void onActionBarTaskModified(ActionBarTaskModifiedEvent event) {
        // This event is fired when an action bar is modified
        System.out.println("Action bar modified for " + event.getPlayer().getName() +
                " from \"" + event.getOldMessage() + "\" to \"" + event.getNewMessage() + "\"");
    }
}
```

## Testing

The plugin includes a test command that demonstrates all the features of the API:

```
/actionbarapiplus runtest
```

This command requires the permission `actionbarapiplus.runtest` and will run a comprehensive test of all API features.

**Warning**: This test will cancel all running action bar tasks for all players on the server and may cause lag spikes. It is not recommended to run this on a production server.

## Compatibility

ActionBarAPI+ is designed to work with Minecraft versions 1.10-1.21. However, not all features have been thoroughly tested on all versions. If you encounter any issues, please report them on the GitHub repository.

## Known Limitations

- The API may conflict with other plugins that use the action bar at the same time. If multiple plugins try to display action bars simultaneously, it may flicker and cause a visual glitch.
- Global action bars can impact server performance if there are many players online (especially on weaker servers).

## Contributing

Contributions are welcome! If you'd like to contribute to the project, please:

1. Submit a pull request
2. Give feedback or suggestions
3. Report any bugs or issues

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Disclaimer

I created this API for my own use and am sharing it for others who might find it helpful. While I've made efforts to ensure it works correctly, you may encounter bugs or issues. The API is provided "as is" without warranty of any kind. Use at your own risk.

I would only recommend this for custom plugins on your own server, not plugins for public release (since it may interefere with others that require or use the action bar.)

If you find the API useful or have suggestions for improvements, feel free to contribute to the project or reach out with feedback.
