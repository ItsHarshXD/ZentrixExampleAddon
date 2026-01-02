package dev.itsharshxd.zentrix.example;

import dev.itsharshxd.zentrix.api.ZentrixAPI;
import dev.itsharshxd.zentrix.api.addon.ZentrixAddon;
import dev.itsharshxd.zentrix.api.classes.ClassService;
import dev.itsharshxd.zentrix.api.currency.CurrencyService;
import dev.itsharshxd.zentrix.api.data.DataService;
import dev.itsharshxd.zentrix.api.game.GameService;
import dev.itsharshxd.zentrix.api.phase.PhaseService;
import dev.itsharshxd.zentrix.api.player.PlayerService;
import dev.itsharshxd.zentrix.api.profile.ProfileService;
import dev.itsharshxd.zentrix.api.team.TeamService;
import dev.itsharshxd.zentrix.example.commands.APITestCommand;
import dev.itsharshxd.zentrix.example.listeners.CurrencyEventListener;
import dev.itsharshxd.zentrix.example.listeners.GameEventListener;
import dev.itsharshxd.zentrix.example.listeners.PlayerEventListener;
import dev.itsharshxd.zentrix.example.listeners.TeamEventListener;
import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Example addon demonstrating comprehensive usage of the Zentrix Developer API.
 * <p>
 * This addon shows how to:
 * <ul>
 *   <li>Extend ZentrixAddon for automatic lifecycle management</li>
 *   <li>Access all API services (Game, Player, Team, Class, Currency, Profile, Phase)</li>
 *   <li>Listen to all Zentrix events</li>
 *   <li>Create commands that interact with the API</li>
 * </ul>
 * </p>
 *
 * @author ItsHarshXD
 * @version 1.0.0
 */
public class ExampleAddon extends ZentrixAddon {

    private static ExampleAddon instance;

    // Event listeners
    private GameEventListener gameEventListener;
    private PlayerEventListener playerEventListener;
    private TeamEventListener teamEventListener;
    private CurrencyEventListener currencyEventListener;

    /**
     * Called when the addon is enabled after Zentrix API is available.
     */
    @Override
    protected void onAddonEnable() {
        instance = this;

        // Log API information
        ZentrixAPI api = getAPI();
        getLogger().info("===========================================");
        getLogger().info("  Zentrix Example Addon - API Test Suite");
        getLogger().info("===========================================");
        getLogger().info("API Version: " + api.getAPIVersion());
        getLogger().info("Addon ID: " + getAddonId());

        // Initialize and register event listeners
        registerListeners();

        // Register commands
        registerCommands();

        // Log available services
        logServiceStatus();

        // Demonstrate DataService usage
        demonstrateDataService();

        getLogger().info("===========================================");
        getLogger().info("  Example Addon enabled successfully!");
        getLogger().info("  Use /apitest for testing commands");
        getLogger().info("===========================================");
    }

    /**
     * Called when the addon is disabled.
     */
    @Override
    protected void onAddonDisable() {
        getLogger().info("Example Addon disabled. Goodbye!");
        instance = null;
    }

    /**
     * Gets the minimum required API version.
     * This addon requires API version 1.0.0 or higher.
     */
    @NotNull
    @Override
    protected String getRequiredAPIVersion() {
        return "1.0.0";
    }

    /**
     * Gets the addon's unique identifier.
     */
    @NotNull
    @Override
    public String getAddonId() {
        return "zentrix-example-addon";
    }

    /**
     * Registers all event listeners.
     */
    private void registerListeners() {
        gameEventListener = new GameEventListener(this);
        playerEventListener = new PlayerEventListener(this);
        teamEventListener = new TeamEventListener(this);
        currencyEventListener = new CurrencyEventListener(this);

        getServer().getPluginManager().registerEvents(gameEventListener, this);
        getServer()
            .getPluginManager()
            .registerEvents(playerEventListener, this);
        getServer().getPluginManager().registerEvents(teamEventListener, this);
        getServer()
            .getPluginManager()
            .registerEvents(currencyEventListener, this);

        getLogger().info("Registered 4 event listeners");
    }

    /**
     * Registers all commands.
     */
    private void registerCommands() {
        APITestCommand testCommand = new APITestCommand(this);
        getCommand("apitest").setExecutor(testCommand);
        getCommand("apitest").setTabCompleter(testCommand);
        getLogger().info("Registered /apitest command");
    }

    /**
     * Logs the status of all available services.
     */
    private void logServiceStatus() {
        ZentrixAPI api = getAPI();

        getLogger().info("--- Service Status ---");

        // GameService
        GameService gameService = api.getGameService();
        getLogger().info(
            "GameService: " +
                (gameService != null ? "✓ Available" : "✗ Unavailable")
        );
        if (gameService != null) {
            getLogger().info(
                "  - Active games: " + gameService.getActiveGameCount()
            );
            getLogger().info(
                "  - Available arenas: " +
                    gameService.getAvailableArenas().size()
            );
        }

        // PlayerService
        PlayerService playerService = api.getPlayerService();
        getLogger().info(
            "PlayerService: " +
                (playerService != null ? "✓ Available" : "✗ Unavailable")
        );
        if (playerService != null) {
            getLogger().info(
                "  - Players in games: " + playerService.getAllPlayers().size()
            );
            getLogger().info(
                "  - Spectators: " + playerService.getAllSpectators().size()
            );
        }

        // TeamService
        TeamService teamService = api.getTeamService();
        getLogger().info(
            "TeamService: " +
                (teamService != null ? "✓ Available" : "✗ Unavailable")
        );

        // ClassService
        ClassService classService = api.getClassService();
        getLogger().info(
            "ClassService: " +
                (classService != null ? "✓ Available" : "✗ Unavailable")
        );
        if (classService != null) {
            getLogger().info(
                "  - Class system enabled: " +
                    classService.isClassSystemEnabled()
            );
            getLogger().info(
                "  - Available classes: " + classService.getClassCount()
            );
        }

        // CurrencyService
        CurrencyService currencyService = api.getCurrencyService();
        getLogger().info(
            "CurrencyService: " +
                (currencyService != null ? "✓ Available" : "✗ Unavailable")
        );
        if (currencyService != null) {
            getLogger().info(
                "  - Currency: " + currencyService.getDisplayName()
            );
            getLogger().info("  - Symbol: " + currencyService.getSymbol());
            getLogger().info(
                "  - Starting balance: " + currencyService.getStartingBalance()
            );
        }

        // ProfileService
        ProfileService profileService = api.getProfileService();
        getLogger().info(
            "ProfileService: " +
                (profileService != null ? "✓ Available" : "✗ Unavailable")
        );

        // PhaseService
        PhaseService phaseService = api.getPhaseService();
        getLogger().info(
            "PhaseService: " +
                (phaseService != null ? "✓ Available" : "✗ Unavailable")
        );
        if (phaseService != null) {
            getLogger().info(
                "  - Configured phases: " + phaseService.getPhaseCount()
            );
            getLogger().info(
                "  - Total phase duration: " +
                    phaseService.getTotalPhaseDuration() +
                    "s"
            );
        }

        // AddonManager
        getLogger().info("AddonManager: ✓ Available");
        getLogger().info(
            "  - Registered addons: " + api.getAddonManager().getAddonCount()
        );

        // DataService
        DataService dataService = api.getDataService();
        getLogger().info(
            "DataService: " +
                (dataService != null ? "✓ Available" : "✗ Unavailable")
        );
        if (dataService != null) {
            getLogger().info(
                "  - Plugin folder: " +
                    dataService.getPluginDataFolder().getPath()
            );
            getLogger().info(
                "  - Addons folder: " + dataService.getAddonsFolder().getPath()
            );
        }
    }

    /**
     * Demonstrates DataService usage for addon configuration.
     */
    private void demonstrateDataService() {
        DataService dataService = getAPI().getDataService();

        getLogger().info("--- DataService Demo ---");

        // Get addon's data folder
        File addonFolder = dataService.getAddonDataFolder(getAddonId());
        getLogger().info("Addon data folder: " + addonFolder.getPath());

        // Create or load a config file
        YamlConfiguration config = dataService.getOrCreateConfig(
            getAddonId(),
            "config.yml"
        );

        // Check if this is first run
        boolean firstRun = !config.contains("initialized");
        if (firstRun) {
            getLogger().info("First run detected! Creating default config...");

            // Set some default values
            config.set("initialized", true);
            config.set("addon-name", "Zentrix Example Addon");
            config.set("version", "1.0.0");
            config.set("settings.debug-mode", false);
            config.set("settings.max-retries", 3);
            config.set("features.custom-rewards", true);
            config.set("features.enhanced-logging", false);

            // Save the config
            dataService.saveConfig(getAddonId(), "config.yml", config);
            getLogger().info("Default config saved!");
        } else {
            getLogger().info(
                "Config loaded! Debug mode: " +
                    config.getBoolean("settings.debug-mode")
            );
        }

        // Demonstrate reading Zentrix config (read-only)
        String currencySymbol = dataService.getZentrixConfigString(
            "currency",
            "currency.symbol",
            "⛃"
        );
        getLogger().info("Zentrix currency symbol: " + currencySymbol);

        // List files in addon folder
        String[] files = dataService.listAddonFiles(getAddonId());
        getLogger().info("Files in addon folder: " + files.length);
        for (String file : files) {
            getLogger().info("  - " + file);
        }
    }

    /**
     * Gets the singleton instance of this addon.
     *
     * @return The addon instance
     */
    public static ExampleAddon getInstance() {
        return instance;
    }

    /**
     * Gets the GameService from the API.
     */
    public GameService getGameService() {
        return getAPI().getGameService();
    }

    /**
     * Gets the PlayerService from the API.
     */
    public PlayerService getPlayerService() {
        return getAPI().getPlayerService();
    }

    /**
     * Gets the TeamService from the API.
     */
    public TeamService getTeamService() {
        return getAPI().getTeamService();
    }

    /**
     * Gets the ClassService from the API.
     */
    public ClassService getClassService() {
        return getAPI().getClassService();
    }

    /**
     * Gets the CurrencyService from the API.
     */
    public CurrencyService getCurrencyService() {
        return getAPI().getCurrencyService();
    }

    /**
     * Gets the ProfileService from the API.
     */
    public ProfileService getProfileService() {
        return getAPI().getProfileService();
    }

    /**
     * Gets the PhaseService from the API.
     */
    public PhaseService getPhaseService() {
        return getAPI().getPhaseService();
    }

    /**
     * Gets the Zentrix API instance.
     * <p>
     * This is a public wrapper around the protected {@link #getAPI()} method,
     * allowing commands and other classes to access the API.
     * </p>
     *
     * @return The Zentrix API instance (never null)
     */
    @NotNull
    public ZentrixAPI getZentrixAPI() {
        return getAPI();
    }

    /**
     * Gets the AddonManager from the API.
     */
    public dev.itsharshxd.zentrix.api.addon.AddonManager getAddonManager() {
        return getAPI().getAddonManager();
    }

    /**
     * Gets the DataService from the API.
     */
    public DataService getDataService() {
        return getAPI().getDataService();
    }
}
