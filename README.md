# Zentrix Example Addon

A comprehensive example addon demonstrating the Zentrix Developer API usage. This addon serves as a reference implementation and test harness for developers building addons for the Zentrix Battle Royale plugin.

## Overview

This example addon showcases:
- Extending `ZentrixAddon` for automatic lifecycle management
- Accessing the API via `ZentrixAPI.get()`
- Listening to all Zentrix events
- Creating commands that interact with the API
- Best practices for addon development

## Requirements

- **Java 21** or higher
- **Paper 1.21.9-.1.21.10** or compatible server
- **Zentrix** plugin installed and enabled

## Installation

1. Build the addon JAR:
   ```bash
   ./gradlew :example-addon:build
   ```

2. Copy the JAR from `example-addon/build/libs/` to your server's `plugins/` folder

3. Ensure Zentrix is installed and will load before this addon

4. Start/restart your server

## Features Demonstrated

### 1. ZentrixAddon Base Class

The `ExampleAddon` class extends `ZentrixAddon`, which provides:
- Automatic API availability checks
- Version compatibility verification
- Automatic addon registration/unregistration

```java
public class ExampleAddon extends ZentrixAddon {
    
    @Override
    protected void onAddonEnable() {
        // Access the API
        ZentrixAPI api = ZentrixAPI.get();
        
        // Use services
        api.getGameService().getActiveGames();
        
        // Your initialization code here
    }
    
    @Override
    protected void onAddonDisable() {
        // Cleanup code here
    }
    
    @Override
    protected String getRequiredAPIVersion() {
        return "1.0.0"; // Minimum API version required
    }
}
```

### 2. Event Listeners

The addon includes listeners for all major Zentrix events:

#### Game Events (`GameEventListener`)
- `GameStartEvent` - Fired when a game starts
- `GameEndEvent` - Fired when a game ends
- `GamePhaseChangeEvent` - Fired when game phase changes

#### Player Events (`PlayerEventListener`)
- `PlayerJoinGameEvent` - Fired when player joins (cancellable)
- `PlayerLeaveGameEvent` - Fired when player leaves
- `PlayerKillEvent` - Fired when a player gets a kill
- `PlayerDeathGameEvent` - Fired when a player is eliminated

#### Team Events (`TeamEventListener`)
- `TeamEliminatedEvent` - Fired when a team is eliminated

#### Currency Events (`CurrencyEventListener`)
- `CurrencyChangeEvent` - Fired when currency changes (cancellable)

### 3. API Test Command

The `/apitest` command provides comprehensive testing of all API features:

```
/apitest help        - Show all commands
/apitest games       - Test GameService
/apitest player      - Test PlayerService
/apitest team        - Test TeamService
/apitest class       - Test ClassService
/apitest currency    - Test CurrencyService
/apitest phase       - Test PhaseService
/apitest profile     - Test ProfileService
/apitest addon       - Test AddonManager
/apitest all         - Run all tests
```

#### Game Subcommands
```
/apitest games list      - List all active games
/apitest games info      - Show current game info
/apitest games count     - Show active game count
/apitest games arenas    - List available arenas
/apitest games bystate   - List games by state
/apitest games byarena   - List games by arena
```

#### Player Subcommands
```
/apitest player info       - Show your player info
/apitest player ingame     - Check if in game
/apitest player alive      - Check if alive
/apitest player spectating - Check if spectating
/apitest player kills      - Show kill count
/apitest player all        - List all players
/apitest player spectators - List all spectators
```

#### Team Subcommands
```
/apitest team list      - List all teams in game
/apitest team myteam    - Show your team info
/apitest team teammates - List your teammates
/apitest team alive     - List alive teams
/apitest team winning   - Show winning team
/apitest team members   - List team members
```

#### Class Subcommands
```
/apitest class list    - List all classes
/apitest class myclass - Show your selected class
/apitest class info    - Show class details
/apitest class default - Show default class
/apitest class enabled - Check if class system enabled
```

#### Currency Subcommands
```
/apitest currency balance - Show your balance
/apitest currency info    - Show currency config
/apitest currency events  - List reward events
/apitest currency format  - Format a test amount
```

#### Phase Subcommands
```
/apitest phase list    - List all phases
/apitest phase current - Show current phase
/apitest phase next    - Show next phase
/apitest phase time    - Show time remaining
/apitest phase info    - Show detailed phase info
```

## API Services Reference

### GameService
```java
// Access the API
ZentrixAPI api = ZentrixAPI.get();
GameService gameService = api.getGameService();

// Get all active games
Collection<ZentrixGame> games = gameService.getActiveGames();

// Find game by player
Optional<ZentrixGame> game = gameService.getGameByPlayer(player);

// Get game count
int count = gameService.getActiveGameCount();

// Get available arenas
Collection<String> arenas = gameService.getAvailableArenas();
```

### PlayerService
```java
PlayerService playerService = ZentrixAPI.get().getPlayerService();

// Get player in game
Optional<ZentrixPlayer> zPlayer = playerService.getPlayer(player);

// Get all players in games
Collection<ZentrixPlayer> players = playerService.getAllPlayers();

// Get all spectators
Collection<ZentrixPlayer> spectators = playerService.getAllSpectators();

// Check if player is in game
boolean inGame = playerService.isInGame(player);
```

### TeamService
```java
TeamService teamService = ZentrixAPI.get().getTeamService();

// Get player's team
Optional<ZentrixTeam> team = teamService.getPlayerTeam(player);

// Get all teams in a game
Collection<ZentrixTeam> teams = teamService.getTeams(game);

// Check if players are teammates
boolean areTeammates = teamService.areTeammates(player1, player2);

// Get alive teams
Collection<ZentrixTeam> aliveTeams = teamService.getAliveTeams(game);
```

### ClassService
```java
ClassService classService = ZentrixAPI.get().getClassService();

// Get all classes
Collection<PlayerClass> classes = classService.getClasses();

// Get player's selected class
Optional<PlayerClass> playerClass = classService.getPlayerClass(game, player);

// Check if class system is enabled
boolean enabled = classService.isClassSystemEnabled();

// Get class count
int count = classService.getClassCount();
```

### CurrencyService
```java
CurrencyService currencyService = ZentrixAPI.get().getCurrencyService();

// Get player balance (async)
currencyService.getBalance(uuid).thenAccept(balance -> {
    // Handle balance
});

// Get cached balance (sync, for scoreboards)
double balance = currencyService.getCachedBalance(uuid);

// Get currency display info
String displayName = currencyService.getDisplayName();
String symbol = currencyService.getSymbol();

// Format amount
String formatted = currencyService.formatBalance(100.0);
```

### PhaseService
```java
PhaseService phaseService = ZentrixAPI.get().getPhaseService();

// Get all phases
Collection<GamePhase> phases = phaseService.getPhases();

// Get current phase for a game
Optional<GamePhase> currentPhase = phaseService.getCurrentPhase(game);

// Get next phase
Optional<GamePhase> nextPhase = phaseService.getNextPhase(game);

// Get phase count
int count = phaseService.getPhaseCount();
```

### ProfileService
```java
ProfileService profileService = ZentrixAPI.get().getProfileService();

// Get player stats (async)
profileService.getStats(uuid).thenAccept(stats -> {
    int wins = stats.getWins();
    int kills = stats.getKills();
    int deaths = stats.getDeaths();
    double kdr = stats.getKDR();
});
```

### AddonManager
```java
AddonManager addonManager = ZentrixAPI.get().getAddonManager();

// Get registered addons
Collection<AddonInfo> addons = addonManager.getRegisteredAddons();

// Check if addon is registered
boolean registered = addonManager.isAddonRegistered("my-addon");

// Get addon count
int count = addonManager.getAddonCount();
```

## Event Handling Examples

### Cancelling Player Join
```java
@EventHandler
public void onPlayerJoin(PlayerJoinGameEvent event) {
    if (someCondition) {
        event.setCancelled(true);
        event.getBukkitPlayer().ifPresent(p ->
            p.sendMessage("§cYou cannot join this game!"));
    }
}
```

### Modifying Currency Changes
```java
@EventHandler(priority = EventPriority.HIGH)
public void onCurrencyChange(CurrencyChangeEvent event) {
    // Double VIP rewards
    if (event.isEventReward() && event.isGain()) {
        event.getPlayer().ifPresent(p -> {
            if (p.hasPermission("vip.doublerewards")) {
                event.multiplyChange(2.0);
            }
        });
    }
}
```

### Handling Game End
```java
@EventHandler
public void onGameEnd(GameEndEvent event) {
    if (event.hasWinner()) {
        event.getWinners().forEach(winner -> {
            // Reward winners
            Bukkit.broadcastMessage("§a" + winner.getName() + " won!");
        });
    }
}
```

## Project Structure

```
example-addon/
├── build.gradle.kts          # Gradle build configuration
├── README.md                  # This file
└── src/main/
    ├── java/dev/itsharshxd/zentrix/example/
    │   ├── ExampleAddon.java          # Main addon class
    │   ├── commands/
    │   │   └── APITestCommand.java    # /apitest command
    │   └── listeners/
    │       ├── GameEventListener.java     # Game events
    │       ├── PlayerEventListener.java   # Player events
    │       ├── TeamEventListener.java     # Team events
    │       └── CurrencyEventListener.java # Currency events
    └── resources/
        └── plugin.yml         # Plugin configuration
```

## Building

From the project root:

```bash
# Build just the example addon
./gradlew :example-addon:build

# Build everything including the addon
./gradlew build
```

The JAR will be in `example-addon/build/libs/`.

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `zentrix.example.test` | Use /apitest command | op |

## Command Aliases

- `/apitest` - Main command
- `/ztest` - Alias
- `/exampletest` - Alias

## Best Practices Demonstrated

1. **API Access**: Use `ZentrixAPI.get()` to access the API from anywhere
2. **Lifecycle Management**: Use `ZentrixAddon` base class for automatic lifecycle handling
3. **Version Compatibility**: Specify required API version via `getRequiredAPIVersion()`
4. **Event Priority**: Use `MONITOR` for logging, `HIGH`/`NORMAL` for modifications
5. **Async Operations**: Use `CompletableFuture` properly for async API calls
6. **Null Safety**: Use `Optional` returns properly with `ifPresent`/`orElse`
7. **Player Caching**: Use cached balance for scoreboards, async for accurate balance

## Troubleshooting

### Addon Not Loading
- Ensure Zentrix is installed and enabled
- Check that `depend: [Zentrix]` is in plugin.yml
- Verify Java version (21+)

### API Not Available
- Zentrix must be enabled before your addon
- Check server logs for Zentrix initialization errors

### Events Not Firing
- Verify listeners are registered in `onAddonEnable()`
- Check event priorities
- Ensure you're not accidentally cancelling events

## Support

For issues with this example addon or the Zentrix Developer API:
- GitHub: https://github.com/ItsHarshXD/ZentrixAPI

## License

This example addon is provided as part of the Zentrix plugin distribution.