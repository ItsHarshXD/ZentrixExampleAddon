package dev.itsharshxd.addon.zentrix.example;

import dev.itsharshxd.zentrix.api.ZentrixAPI;
import dev.itsharshxd.zentrix.api.addon.ZentrixAddon;
import dev.itsharshxd.addon.zentrix.example.commands.APITestCommand;
import dev.itsharshxd.addon.zentrix.example.listeners.CurrencyEventListener;
import dev.itsharshxd.addon.zentrix.example.listeners.GameEventListener;
import dev.itsharshxd.addon.zentrix.example.listeners.ItemInteractionListener;
import dev.itsharshxd.addon.zentrix.example.listeners.PlayerEventListener;
import dev.itsharshxd.addon.zentrix.example.listeners.TeamEventListener;
import java.io.File;

import dev.itsharshxd.zentrix.api.broadcast.BroadcastBuilder;
import dev.itsharshxd.zentrix.api.broadcast.BroadcastService;
import dev.itsharshxd.zentrix.api.broadcast.BroadcastType;
import dev.itsharshxd.zentrix.api.broadcast.GameState;
import dev.itsharshxd.zentrix.api.classes.ClassService;
import dev.itsharshxd.zentrix.api.currency.CurrencyService;
import dev.itsharshxd.zentrix.api.data.DataService;
import dev.itsharshxd.zentrix.api.game.GameService;
import dev.itsharshxd.zentrix.api.gametype.GameTypeBuilder;
import dev.itsharshxd.zentrix.api.gametype.GameTypeService;
import dev.itsharshxd.zentrix.api.gametype.ZentrixGameType;
import dev.itsharshxd.zentrix.api.item.ItemBuilder;
import dev.itsharshxd.zentrix.api.item.ItemService;
import dev.itsharshxd.zentrix.api.phase.PhaseBuilder;
import dev.itsharshxd.zentrix.api.phase.PhaseService;
import dev.itsharshxd.zentrix.api.player.PlayerService;
import dev.itsharshxd.zentrix.api.profile.ProfileService;
import dev.itsharshxd.zentrix.api.team.TeamService;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Example addon demonstrating comprehensive usage of the Zentrix Developer API.
 * <p>
 * This addon shows how to:
 * <ul>
 *   <li>Extend ZentrixAddon for automatic lifecycle management</li>
 *   <li>Access the API via {@link ZentrixAPI#get()}</li>
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
    private ItemInteractionListener itemInteractionListener;

    // Track registered dynamic content for cleanup
    private boolean registeredDynamicContent = false;

    /**
     * Called when the addon is enabled after Zentrix API is available.
     */
    @Override
    protected void onAddonEnable() {
        instance = this;

        // Log API information
        ZentrixAPI api = ZentrixAPI.get();
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

        // Demonstrate Dynamic Registration API (v1.1.0+)
        demonstrateDynamicRegistration();

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
        // Cleanup dynamic registrations
        if (registeredDynamicContent) {
            cleanupDynamicRegistrations();
        }

        getLogger().info("Example Addon disabled. Goodbye!");
        instance = null;
    }

    /**
     * Cleans up all dynamically registered content.
     */
    private void cleanupDynamicRegistrations() {
        getLogger().info("Cleaning up dynamic registrations...");

        ZentrixAPI api = ZentrixAPI.get();

        // Unregister custom items
        ItemService itemService = api.getItemService();
        itemService.unregisterItem("example-tracking-compass");
        itemService.unregisterItem("example-speed-boots");
        itemService.unregisterItem("example-heal-apple");

        // Unregister custom broadcasts
        BroadcastService broadcastService = api.getBroadcastService();
        broadcastService.unregisterBroadcast("example-tips");
        broadcastService.unregisterBroadcast("example-welcome");

        // Unregister custom game types
        GameTypeService gameTypeService = api.getGameTypeService();
        gameTypeService.unregisterGameType("mega-squads");

        // Unregister custom phases
        PhaseService phaseService = api.getPhaseService();
        phaseService.unregisterPhase("blood_moon");

        getLogger().info("Dynamic registrations cleaned up!");
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
        itemInteractionListener = new ItemInteractionListener(this);

        getServer().getPluginManager().registerEvents(gameEventListener, this);
        getServer().getPluginManager().registerEvents(playerEventListener, this);
        getServer().getPluginManager().registerEvents(teamEventListener, this);
        getServer().getPluginManager().registerEvents(currencyEventListener, this);
        getServer().getPluginManager().registerEvents(itemInteractionListener, this);

        getLogger().info("Registered 5 event listeners");
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
        ZentrixAPI api = ZentrixAPI.get();

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
        var dataService = ZentrixAPI.get().getDataService();

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
     * Demonstrates the Dynamic Registration API (v1.1.0+).
     * <p>
     * This method shows how to dynamically register:
     * <ul>
     *   <li>Custom Items with ItemService</li>
     *   <li>Custom Broadcasts with BroadcastService</li>
     *   <li>Custom Game Types with GameTypeService</li>
     *   <li>Custom Phases with PhaseService</li>
     * </ul>
     * </p>
     */
    private void demonstrateDynamicRegistration() {
        getLogger().info("--- Dynamic Registration Demo (v1.1.0+) ---");

        ZentrixAPI api = ZentrixAPI.get();

        // ========================================
        // 1. ITEM REGISTRATION
        // ========================================
        registerCustomItems(api.getItemService());

        // ========================================
        // 2. BROADCAST REGISTRATION
        // ========================================
        registerCustomBroadcasts(api.getBroadcastService());

        // ========================================
        // 3. GAME TYPE REGISTRATION
        // ========================================
        registerCustomGameTypes(api.getGameTypeService());

        // ========================================
        // 4. PHASE REGISTRATION
        // ========================================
        registerCustomPhases(api.getPhaseService());

        registeredDynamicContent = true;
        getLogger().info("Dynamic registration complete!");
    }

    /**
     * Registers custom items using the ItemService.
     * <p>
     * Demonstrates:
     * <ul>
     *   <li>Creating items with display names, lore, and enchantments</li>
     *   <li>Using glow effect without visible enchantments</li>
     *   <li>Setting slot positions and enabled status</li>
     *   <li>Using addon ID for ownership tracking</li>
     * </ul>
     * </p>
     *
     * @param itemService The item service
     */
    private void registerCustomItems(ItemService itemService) {
        getLogger().info("Registering custom items...");

        // Item 1: Tracking Compass
        // A glowing compass that could be used to track players
        ItemBuilder trackingCompass = new ItemBuilder()
            .id("example-tracking-compass")
            .material(Material.COMPASS)
            .displayName("&#FFD700&lTracking Compass")
            .lore(
                "",
                "&#AAAAAA Right-click to track",
                "&#AAAAAA the nearest enemy!",
                "",
                "&#777777Cooldown: &#FFFFFF10s"
            )
            .enchant(Enchantment.UNBREAKING, 1)
            .flag(ItemFlag.HIDE_ENCHANTS)
            .glow(true)
            .slot(4)  // Middle hotbar slot
            .enabled(true)
            .addonId(getAddonId());

        if (itemService.registerItem(trackingCompass)) {
            getLogger().info("  ✓ Registered 'example-tracking-compass'");
        } else {
            getLogger().warning("  ✗ Failed to register 'example-tracking-compass' (may already exist)");
        }

        // Item 2: Speed Boots
        // Enchanted boots that give speed
        ItemBuilder speedBoots = new ItemBuilder()
            .id("example-speed-boots")
            .material(Material.LEATHER_BOOTS)
            .displayName("&#55FF55&lSwift Boots")
            .lore(
                "",
                "&#AAAAAA These enchanted boots",
                "&#AAAAAA make you run faster!",
                "",
                "&#55FF55+20% Movement Speed"
            )
            .enchant(Enchantment.SWIFT_SNEAK, 3)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .customModelData(1001)
            .unbreakable(true)
            .slot(36)  // Boots slot
            .enabled(true)
            .addonId(getAddonId());

        if (itemService.registerItem(speedBoots)) {
            getLogger().info("  ✓ Registered 'example-speed-boots'");
        } else {
            getLogger().warning("  ✗ Failed to register 'example-speed-boots' (may already exist)");
        }

        // Item 3: Healing Apple
        // A special golden apple variant
        ItemBuilder healApple = new ItemBuilder()
            .id("example-heal-apple")
            .material(Material.GOLDEN_APPLE)
            .displayName("&#FF5555&lVampire Apple")
            .lore(
                "",
                "&#AAAAAA Consume to heal and",
                "&#AAAAAA steal life from enemies!",
                "",
                "&#FF5555❤ +4 Hearts",
                "&#FFAA00✦ Lifesteal for 10s"
            )
            .amount(1)
            .glow(true)
            .slot(8)  // Last hotbar slot
            .enabled(true)
            .addonId(getAddonId());

        if (itemService.registerItem(healApple)) {
            getLogger().info("  ✓ Registered 'example-heal-apple'");
        } else {
            getLogger().warning("  ✗ Failed to register 'example-heal-apple' (may already exist)");
        }

        // Log total items
        getLogger().info("  Total custom items from this addon: " +
            itemService.getItemsByAddon(getAddonId()).size());
    }

    /**
     * Registers custom broadcasts using the BroadcastService.
     * <p>
     * Demonstrates:
     * <ul>
     *   <li>Chat broadcasts with multiple messages</li>
     *   <li>Title broadcasts with timing configuration</li>
     *   <li>State-specific broadcasts (LOBBY, WAITING, PLAYING)</li>
     *   <li>Interval-based automatic broadcasting</li>
     * </ul>
     * </p>
     *
     * @param broadcastService The broadcast service
     */
    private void registerCustomBroadcasts(BroadcastService broadcastService) {
        getLogger().info("Registering custom broadcasts...");

        // Broadcast 1: Gameplay Tips (Chat)
        // Rotating tips shown during gameplay
        BroadcastBuilder tipsBroadcast = new BroadcastBuilder()
            .id("example-tips")
            .type(BroadcastType.CHAT)
            .enabled(true)
            .interval(120)  // Every 2 minutes
            .showIn(GameState.PLAYING)
            .messages(
                "&#FFD700[TIP] &#FFFFFF Use sneak to reduce knockback when hit!",
                "&#FFD700[TIP] &#FFFFFF Golden apples give absorption hearts!",
                "&#FFD700[TIP] &#FFFFFF Stay inside the border - it deals damage!",
                "&#FFD700[TIP] &#FFFFFF Team up with your squad to survive longer!"
            )
            .addonId(getAddonId());

        if (broadcastService.registerBroadcast(tipsBroadcast)) {
            getLogger().info("  ✓ Registered 'example-tips' broadcast");
        } else {
            getLogger().warning("  ✗ Failed to register 'example-tips' (may already exist)");
        }

        // Broadcast 2: Welcome Title (Title)
        // Shown to players in the lobby
        BroadcastBuilder welcomeBroadcast = new BroadcastBuilder()
            .id("example-welcome")
            .type(BroadcastType.TITLE)
            .enabled(true)
            .interval(300)  // Every 5 minutes
            .showIn(GameState.LOBBY)
            .title("&#FFD700&lWELCOME!")
            .subtitle("&#AAAAAA Use /play to join a game")
            .titleTiming(10, 60, 20)  // fadeIn, stay, fadeOut (in ticks)
            .addonId(getAddonId());

        if (broadcastService.registerBroadcast(welcomeBroadcast)) {
            getLogger().info("  ✓ Registered 'example-welcome' broadcast");
        } else {
            getLogger().warning("  ✗ Failed to register 'example-welcome' (may already exist)");
        }

        // Log total broadcasts
        getLogger().info("  Total custom broadcasts from this addon: " +
            broadcastService.getBroadcastsByAddon(getAddonId()).size());
    }

    /**
     * Registers custom game types using the GameTypeService.
     * <p>
     * Demonstrates:
     * <ul>
     *   <li>Creating new team-based game modes</li>
     *   <li>Configuring team sizes and player limits</li>
     *   <li>Custom scoreboards for different game states</li>
     * </ul>
     * </p>
     *
     * @param gameTypeService The game type service
     */
    private void registerCustomGameTypes(GameTypeService gameTypeService) {
        getLogger().info("Registering custom game types...");

        // Game Type: Mega Squads (8-player teams)
        GameTypeBuilder megaSquads = new GameTypeBuilder()
            .name("mega-squads")
            .teamSize(8)
            .minimumPlayers(16)  // At least 2 teams
            .maximumPlayers(64)  // Up to 8 teams
            .startTime(60)  // 60 second countdown
            .scoreboard(ZentrixGameType.GameTypeState.WAITING, sb -> sb
                .title("&#FFD700&lMEGA SQUADS")
                .line("&#AAAAAA Map: &#FFFFFF%arena%")
                .line("")
                .line("&#AAAAAA Players: &#55FF55%players%&#AAAAAA/&#FFFFFF%max%")
                .line("&#AAAAAA Teams: &#55FF55%teams%")
                .line("")
                .line("&#777777Waiting for players...")
            )
            .scoreboard(ZentrixGameType.GameTypeState.PLAYING, sb -> sb
                .title("&#FFD700&lMEGA SQUADS")
                .line("&#AAAAAA Phase: &#FFFFFF%phase%")
                .line("&#AAAAAA Time: &#FFFFFF%time%")
                .line("")
                .line("&#AAAAAA Alive: &#55FF55%alive%")
                .line("&#AAAAAA Teams: &#55FF55%teams_alive%")
                .line("")
                .line("&#AAAAAA Kills: &#FF5555%kills%")
            )
            .addonId(getAddonId());

        if (gameTypeService.registerGameType(megaSquads)) {
            getLogger().info("  ✓ Registered 'mega-squads' game type");
        } else {
            getLogger().warning("  ✗ Failed to register 'mega-squads' (may already exist)");
        }

        // Log total game types
        getLogger().info("  Total custom game types from this addon: " +
            gameTypeService.getGameTypesByAddon(getAddonId()).size());
    }

    /**
     * Registers custom phases using the PhaseService.
     * <p>
     * Demonstrates:
     * <ul>
     *   <li>Creating custom game phases with duration</li>
     *   <li>Border configuration (shrink, damage)</li>
     *   <li>On-start actions (announcements, titles, effects)</li>
     *   <li>Fluent consumer pattern for complex configuration</li>
     * </ul>
     * </p>
     *
     * @param phaseService The phase service
     */
    private void registerCustomPhases(PhaseService phaseService) {
        getLogger().info("Registering custom phases...");

        // Phase: Blood Moon
        // A dramatic phase with increased danger
        PhaseBuilder bloodMoon = new PhaseBuilder()
            .name("blood_moon")
            .displayName("&#990000&lBLOOD MOON")
            .duration(90)  // 90 seconds
            .border(border -> border
                .shrinkTo(75)  // Shrink to 75 blocks
                .shrinkDuration(90)  // Over the entire phase
                .damagePerBlock(2.0)  // Double border damage
                .warningTime(10)  // 10 second warning
            )
            .onStart(actions -> actions
                .announce("&#990000☠ &#FFFFFFThe &#990000Blood Moon &#FFFFFFrises! Damage is increased!")
                .title("&#990000&lBLOOD MOON", "&#FF6666Damage increased by 50%!", 10, 40, 10)
                .sound(Sound.ENTITY_WITHER_SPAWN, 0.8f, 0.5f)
                .giveEffect(PotionEffectType.STRENGTH, 1800, 0)  // Strength I for 90s
            )
            .addonId(getAddonId());

        // Note: registerPhaseAt() adds at a specific index in the phase list
        // Use registerPhase() to add at the end
        if (phaseService.registerPhase(bloodMoon)) {
            getLogger().info("  ✓ Registered 'blood_moon' phase");
        } else {
            getLogger().warning("  ✗ Failed to register 'blood_moon' (may already exist)");
        }

        // Log total phases
        getLogger().info("  Total phases (including custom): " + phaseService.getPhaseCount());
        getLogger().info("  Custom phases from this addon: " +
            phaseService.getPhasesByAddon(getAddonId()).size());
    }

    /**
     * Gets the singleton instance of this addon.
     *
     * @return The addon instance
     */
    public static ExampleAddon getInstance() {
        return instance;
    }
}
