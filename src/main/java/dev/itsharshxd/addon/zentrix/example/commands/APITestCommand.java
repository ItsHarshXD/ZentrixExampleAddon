package dev.itsharshxd.addon.zentrix.example.commands;

import dev.itsharshxd.zentrix.ZentrixAPI.get().ZentrixAPI;
import dev.itsharshxd.zentrix.ZentrixAPI.get().addon.AddonManager;
import dev.itsharshxd.zentrix.ZentrixAPI.get().classes.ClassService;
import dev.itsharshxd.zentrix.ZentrixAPI.get().classes.PlayerClass;
import dev.itsharshxd.zentrix.ZentrixAPI.get().currency.CurrencyEventType;
import dev.itsharshxd.zentrix.ZentrixAPI.get().currency.CurrencyService;
import dev.itsharshxd.zentrix.ZentrixAPI.get().data.DataService;
import dev.itsharshxd.zentrix.ZentrixAPI.get().game.GameService;
import dev.itsharshxd.zentrix.ZentrixAPI.get().game.ZentrixGame;
import dev.itsharshxd.zentrix.ZentrixAPI.get().phase.GamePhase;
import dev.itsharshxd.zentrix.ZentrixAPI.get().phase.PhaseService;
import dev.itsharshxd.zentrix.ZentrixAPI.get().player.PlayerService;
import dev.itsharshxd.zentrix.ZentrixAPI.get().player.ZentrixPlayer;
import dev.itsharshxd.zentrix.ZentrixAPI.get().profile.ProfileService;
import dev.itsharshxd.zentrix.ZentrixAPI.get().recipe.RecipeBuilder;
import dev.itsharshxd.zentrix.ZentrixAPI.get().recipe.RecipeService;
import dev.itsharshxd.zentrix.ZentrixAPI.get().recipe.ZentrixRecipe;
import dev.itsharshxd.zentrix.ZentrixAPI.get().team.TeamService;
import dev.itsharshxd.zentrix.ZentrixAPI.get().team.ZentrixTeam;
import dev.itsharshxd.addon.zentrix.example.ExampleAddon;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Comprehensive command for testing all Zentrix API features.
 * <p>
 * Usage: /apitest <subcommand> [args]
 * </p>
 * <p>
 * Available subcommands:
 * <ul>
 *   <li>help - Show all available commands</li>
 *   <li>games - Test GameService (list games, game info)</li>
 *   <li>player - Test PlayerService (player info, status)</li>
 *   <li>team - Test TeamService (teams, teammates)</li>
 *   <li>class - Test ClassService (classes, selection)</li>
 *   <li>currency - Test CurrencyService (balance, rewards)</li>
 *   <li>profile - Test ProfileService (stats)</li>
 *   <li>phase - Test PhaseService (phases, timing)</li>
 *   <li>addon - Test AddonManager (registered addons)</li>
 *   <li>all - Run all tests</li>
 * </ul>
 * </p>
 */
public class APITestCommand implements CommandExecutor, TabCompleter {

    private final ExampleAddon addon;

    // Subcommands
    private static final List<String> SUBCOMMANDS = Arrays.asList(
        "help",
        "games",
        "player",
        "team",
        "class",
        "currency",
        "phase",
        "profile",
        "addon",
        "data",
        "recipe",
        "all"
    );

    // Game subcommands
    private static final List<String> GAME_SUBCOMMANDS = Arrays.asList(
        "list",
        "info",
        "count",
        "arenas",
        "bystate",
        "byarena"
    );

    // Player subcommands
    private static final List<String> PLAYER_SUBCOMMANDS = Arrays.asList(
        "info",
        "ingame",
        "alive",
        "spectating",
        "kills",
        "all",
        "spectators"
    );

    // Team subcommands
    private static final List<String> TEAM_SUBCOMMANDS = Arrays.asList(
        "list",
        "myteam",
        "teammates",
        "alive",
        "winning",
        "members"
    );

    // Class subcommands
    private static final List<String> CLASS_SUBCOMMANDS = Arrays.asList(
        "list",
        "myclass",
        "info",
        "default",
        "enabled"
    );

    // Currency subcommands
    private static final List<String> CURRENCY_SUBCOMMANDS = Arrays.asList(
        "balance",
        "info",
        "events",
        "format"
    );

    // Phase subcommands
    private static final List<String> PHASE_SUBCOMMANDS = Arrays.asList(
        "list",
        "current",
        "next",
        "time",
        "info"
    );

    // Data subcommands
    private static final List<String> DATA_SUBCOMMANDS = Arrays.asList(
        "folder",
        "config",
        "zentrix",
        "files",
        "create",
        "read",
        "write"
    );

    // Recipe subcommands
    private static final List<String> RECIPE_SUBCOMMANDS = Arrays.asList(
        "list",
        "count",
        "info",
        "create",
        "remove",
        "cancraft",
        "remaining"
    );

    public APITestCommand(ExampleAddon addon) {
        this.addon = addon;
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    ) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (subcommand) {
            case "help":
                showHelp(sender);
                break;
            case "games":
                testGameService(sender, subArgs);
                break;
            case "player":
                testPlayerService(sender, subArgs);
                break;
            case "team":
                testTeamService(sender, subArgs);
                break;
            case "class":
                testClassService(sender, subArgs);
                break;
            case "currency":
                testCurrencyService(sender, subArgs);
                break;
            case "phase":
                testPhaseService(sender, subArgs);
                break;
            case "profile":
                testProfileService(sender, subArgs);
                break;
            case "addon":
                testAddonManager(sender, subArgs);
                break;
            case "data":
                testDataService(sender, subArgs);
                break;
            case "recipe":
                testRecipeService(sender, subArgs);
                break;
            case "all":
                runAllTests(sender);
                break;
            default:
                sender.sendMessage("§cUnknown subcommand: " + subcommand);
                sender.sendMessage(
                    "§7Use §e/apitest help §7for available commands."
                );
                break;
        }

        return true;
    }

    // ==========================================
    // Help Command
    // ==========================================

    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== Zentrix API Test Commands ===");
        sender.sendMessage("");
        sender.sendMessage("§e/apitest games §7- Test GameService");
        sender.sendMessage("  §8list, info, count, arenas, bystate, byarena");
        sender.sendMessage("");
        sender.sendMessage("§e/apitest player §7- Test PlayerService");
        sender.sendMessage(
            "  §8info, ingame, alive, spectating, kills, all, spectators"
        );
        sender.sendMessage("");
        sender.sendMessage("§e/apitest team §7- Test TeamService");
        sender.sendMessage(
            "  §8list, myteam, teammates, alive, winning, members"
        );
        sender.sendMessage("");
        sender.sendMessage("§e/apitest class §7- Test ClassService");
        sender.sendMessage("  §8list, myclass, info, default, enabled");
        sender.sendMessage("");
        sender.sendMessage("§e/apitest currency §7- Test CurrencyService");
        sender.sendMessage("  §8balance, info, events, format");
        sender.sendMessage("");
        sender.sendMessage("§e/apitest phase §7- Test PhaseService");
        sender.sendMessage("  §8list, current, next, time, info");
        sender.sendMessage("");
        sender.sendMessage("§e/apitest profile §7- Test ProfileService");
        sender.sendMessage("");
        sender.sendMessage("§e/apitest addon §7- Test AddonManager");
        sender.sendMessage("");
        sender.sendMessage("§e/apitest data §7- Test DataService");
        sender.sendMessage(
            "  §8folder, config, zentrix, files, create, read, write"
        );
        sender.sendMessage("");
        sender.sendMessage("§e/apitest recipe §7- Test RecipeService");
        sender.sendMessage(
            "  §8list, count, info, create, remove, cancraft, remaining"
        );
        sender.sendMessage("");
        sender.sendMessage("§e/apitest all §7- Run all tests");
        sender.sendMessage("§6§l================================");
    }

    // ==========================================
    // GameService Tests
    // ==========================================

    private void testGameService(CommandSender sender, String[] args) {
        GameService gameService = ZentrixAPI.get().getGameService();
        String subCmd = args.length > 0 ? args[0].toLowerCase() : "list";

        sender.sendMessage("§6§l=== GameService Test ===");

        switch (subCmd) {
            case "list":
                Collection<ZentrixGame> games = gameService.getActiveGames();
                sender.sendMessage("§7Active Games: §e" + games.size());
                for (ZentrixGame game : games) {
                    sender.sendMessage(
                        "  §f- " +
                            game.getGameId() +
                            " §8| §7Arena: §e" +
                            game.getArenaName() +
                            " §8| §7State: §e" +
                            game.getState() +
                            " §8| §7Players: §e" +
                            game.getPlayerCount()
                    );
                }
                break;
            case "info":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(
                        "§cYou must be a player to use this command."
                    );
                    return;
                }
                Player player = (Player) sender;
                Optional<ZentrixGame> gameOpt = gameService.getPlayerGame(
                    player
                );
                if (gameOpt.isPresent()) {
                    ZentrixGame game = gameOpt.get();
                    sender.sendMessage("§7Your Game:");
                    sender.sendMessage("  §7ID: §e" + game.getGameId());
                    sender.sendMessage("  §7Arena: §e" + game.getArenaName());
                    sender.sendMessage("  §7State: §e" + game.getState());
                    sender.sendMessage("  §7Type: §e" + game.getGameTypeName());
                    sender.sendMessage(
                        "  §7Players: §e" +
                            game.getPlayerCount() +
                            "/" +
                            game.getMaxPlayers()
                    );
                    sender.sendMessage(
                        "  §7Teams: §e" + game.getAliveTeamCount()
                    );
                    sender.sendMessage(
                        "  §7Spectators: §e" + game.getSpectatorCount()
                    );
                    sender.sendMessage(
                        "  §7Team Size: §e" + game.getTeamSize()
                    );
                    sender.sendMessage(
                        "  §7Duration: §e" + game.getGameDuration() + "s"
                    );
                    sender.sendMessage(
                        "  §7Border Size: §e" + (int) game.getWorldBorderSize()
                    );
                    sender.sendMessage(
                        "  §7Phase Time: §e" +
                            game.getPhaseTimeRemaining() +
                            "s"
                    );
                    game
                        .getCurrentPhase()
                        .ifPresent(phase ->
                            sender.sendMessage(
                                "  §7Current Phase: §e" + phase.getDisplayName()
                            )
                        );
                } else {
                    sender.sendMessage("§cYou are not in any game.");
                }
                break;
            case "count":
                sender.sendMessage(
                    "§7Active Game Count: §e" + gameService.getActiveGameCount()
                );
                sender.sendMessage(
                    "§7Total Players in Games: §e" +
                        gameService.getTotalPlayerCount()
                );
                break;
            case "arenas":
                Collection<String> arenas = gameService.getAvailableArenas();
                sender.sendMessage("§7Available Arenas: §e" + arenas.size());
                for (String arena : arenas) {
                    int gamesOnArena = gameService
                        .getGamesForArena(arena)
                        .size();
                    sender.sendMessage(
                        "  §f- " +
                            arena +
                            " §8(§7" +
                            gamesOnArena +
                            " active games§8)"
                    );
                }
                break;
            case "bystate":
                for (ZentrixGame.GameState state : ZentrixGame.GameState.values()) {
                    Collection<ZentrixGame> gamesInState =
                        gameService.getGamesByState(state);
                    sender.sendMessage(
                        "§7" +
                            state.name() +
                            ": §e" +
                            gamesInState.size() +
                            " games"
                    );
                }
                break;
            case "byarena":
                if (args.length < 2) {
                    sender.sendMessage(
                        "§cUsage: /apitest games byarena <arena>"
                    );
                    return;
                }
                String arenaName = args[1];
                Collection<ZentrixGame> arenaGames =
                    gameService.getGamesForArena(arenaName);
                sender.sendMessage(
                    "§7Games on " + arenaName + ": §e" + arenaGames.size()
                );
                for (ZentrixGame game : arenaGames) {
                    sender.sendMessage(
                        "  §f- " +
                            game.getGameId() +
                            " §8| §7" +
                            game.getState()
                    );
                }
                break;
            default:
                sender.sendMessage(
                    "§cUnknown games subcommand. Use: list, info, count, arenas, bystate, byarena"
                );
        }
    }

    // ==========================================
    // PlayerService Tests
    // ==========================================

    private void testPlayerService(CommandSender sender, String[] args) {
        PlayerService playerService = ZentrixAPI.get().getPlayerService();
        String subCmd = args.length > 0 ? args[0].toLowerCase() : "info";

        sender.sendMessage("§6§l=== PlayerService Test ===");

        switch (subCmd) {
            case "info":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(
                        "§cYou must be a player to use this command."
                    );
                    return;
                }
                Player player = (Player) sender;
                Optional<ZentrixPlayer> zPlayerOpt = playerService.getPlayer(
                    player
                );
                if (zPlayerOpt.isPresent()) {
                    ZentrixPlayer zPlayer = zPlayerOpt.get();
                    sender.sendMessage("§7Your Player Info:");
                    sender.sendMessage("  §7Name: §e" + zPlayer.getName());
                    sender.sendMessage("  §7UUID: §e" + zPlayer.getUniqueId());
                    sender.sendMessage("  §7Online: §e" + zPlayer.isOnline());
                    sender.sendMessage("  §7Alive: §e" + zPlayer.isAlive());
                    sender.sendMessage(
                        "  §7Spectating: §e" + zPlayer.isSpectating()
                    );
                    sender.sendMessage(
                        "  §7Kills: §e" + zPlayer.getGameKills()
                    );
                    sender.sendMessage(
                        "  §7Kill Streak: §e" + zPlayer.getKillStreak()
                    );
                    sender.sendMessage(
                        "  §7Highest Streak: §e" +
                            zPlayer.getHighestKillStreak()
                    );
                    sender.sendMessage(
                        "  §7Damage Dealt: §e" +
                            String.format("%.1f", zPlayer.getDamageDealt())
                    );
                    sender.sendMessage(
                        "  §7Damage Taken: §e" +
                            String.format("%.1f", zPlayer.getDamageTaken())
                    );
                    sender.sendMessage(
                        "  §7Survival Time: §e" +
                            zPlayer.getSurvivalTimeSeconds() +
                            "s"
                    );
                    sender.sendMessage(
                        "  §7Has Stats: §e" + zPlayer.hasGameStats()
                    );
                    zPlayer
                        .getTeamId()
                        .ifPresent(teamId ->
                            sender.sendMessage("  §7Team: §e" + teamId)
                        );
                    zPlayer
                        .getSelectedClass()
                        .ifPresent(playerClass ->
                            sender.sendMessage(
                                "  §7Class: §e" + playerClass.getDisplayName()
                            )
                        );
                } else {
                    sender.sendMessage("§cYou are not in any game.");
                }
                break;
            case "ingame":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Player p = (Player) sender;
                sender.sendMessage(
                    "§7In Game: §e" + ZentrixAPI.get().getGameService().isInGame(p)
                );
                sender.sendMessage(
                    "§7Playing: §e" + ZentrixAPI.get().getGameService().isPlaying(p)
                );
                sender.sendMessage(
                    "§7Spectating: §e" + ZentrixAPI.get().getGameService().isSpectating(p)
                );
                break;
            case "alive":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                sender.sendMessage(
                    "§7Is Alive: §e" + playerService.isAlive((Player) sender)
                );
                break;
            case "spectating":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                sender.sendMessage(
                    "§7Is Spectating: §e" +
                        playerService.isSpectating((Player) sender)
                );
                break;
            case "kills":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                sender.sendMessage(
                    "§7Your Kills: §e" +
                        playerService.getGameKills((Player) sender)
                );
                break;
            case "all":
                Collection<ZentrixPlayer> allPlayers =
                    playerService.getAllPlayers();
                sender.sendMessage(
                    "§7All Players in Games: §e" + allPlayers.size()
                );
                for (ZentrixPlayer zp : allPlayers) {
                    sender.sendMessage(
                        "  §f- " +
                            zp.getName() +
                            " §8| §7Kills: §e" +
                            zp.getGameKills() +
                            " §8| §7Alive: §e" +
                            zp.isAlive()
                    );
                }
                break;
            case "spectators":
                Collection<ZentrixPlayer> spectators =
                    playerService.getAllSpectators();
                sender.sendMessage("§7All Spectators: §e" + spectators.size());
                for (ZentrixPlayer sp : spectators) {
                    sender.sendMessage("  §f- " + sp.getName());
                }
                break;
            default:
                sender.sendMessage(
                    "§cUnknown player subcommand. Use: info, ingame, alive, spectating, kills, all, spectators"
                );
        }
    }

    // ==========================================
    // TeamService Tests
    // ==========================================

    private void testTeamService(CommandSender sender, String[] args) {
        TeamService teamService = ZentrixAPI.get().getTeamService();
        String subCmd = args.length > 0 ? args[0].toLowerCase() : "myteam";

        sender.sendMessage("§6§l=== TeamService Test ===");

        switch (subCmd) {
            case "list":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Optional<ZentrixGame> gameOpt = api
                    .getGameService()
                    .getPlayerGame((Player) sender);
                if (gameOpt.isEmpty()) {
                    sender.sendMessage("§cYou are not in any game.");
                    return;
                }
                Collection<ZentrixTeam> teams = teamService.getTeams(
                    gameOpt.get()
                );
                sender.sendMessage("§7Teams in Game: §e" + teams.size());
                for (ZentrixTeam team : teams) {
                    sender.sendMessage(
                        "  §f- " +
                            team.getDisplayName() +
                            " §8| §7Alive: §e" +
                            team.getAliveMemberCount() +
                            "/" +
                            team.getMemberCount() +
                            " §8| §7Eliminated: §e" +
                            team.isEliminated()
                    );
                }
                break;
            case "myteam":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Optional<ZentrixTeam> teamOpt = teamService.getPlayerTeam(
                    (Player) sender
                );
                if (teamOpt.isPresent()) {
                    ZentrixTeam team = teamOpt.get();
                    sender.sendMessage("§7Your Team:");
                    sender.sendMessage("  §7ID: §e" + team.getTeamId());
                    sender.sendMessage("  §7Number: §e" + team.getTeamNumber());
                    sender.sendMessage("  §7Name: §e" + team.getDisplayName());
                    sender.sendMessage("  §7Color: §e" + team.getColor());
                    sender.sendMessage("  §7Symbol: §e" + team.getSymbol());
                    sender.sendMessage(
                        "  §7Members: §e" +
                            team.getAliveMemberCount() +
                            "/" +
                            team.getMemberCount()
                    );
                    sender.sendMessage(
                        "  §7Eliminated: §e" + team.isEliminated()
                    );
                    sender.sendMessage(
                        "  §7Friendly Fire: §e" + team.isFriendlyFireEnabled()
                    );
                } else {
                    sender.sendMessage("§cYou are not on a team.");
                }
                break;
            case "teammates":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Set<UUID> teammates = teamService.getTeammates((Player) sender);
                sender.sendMessage("§7Your Teammates: §e" + teammates.size());
                for (UUID tmId : teammates) {
                    Player tm = Bukkit.getPlayer(tmId);
                    sender.sendMessage(
                        "  §f- " + (tm != null ? tm.getName() : tmId.toString())
                    );
                }
                Set<UUID> aliveTeammates = teamService.getAliveTeammates(
                    (Player) sender
                );
                sender.sendMessage(
                    "§7Alive Teammates: §e" + aliveTeammates.size()
                );
                break;
            case "alive":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Optional<ZentrixGame> gOpt = api
                    .getGameService()
                    .getPlayerGame((Player) sender);
                if (gOpt.isEmpty()) {
                    sender.sendMessage("§cYou are not in any game.");
                    return;
                }
                Collection<ZentrixTeam> aliveTeams = teamService.getAliveTeams(
                    gOpt.get()
                );
                sender.sendMessage("§7Alive Teams: §e" + aliveTeams.size());
                for (ZentrixTeam t : aliveTeams) {
                    sender.sendMessage(
                        "  §f- " +
                            t.getDisplayName() +
                            " §8(§7" +
                            t.getAliveMemberCount() +
                            " alive§8)"
                    );
                }
                break;
            case "winning":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Optional<ZentrixGame> g2Opt = api
                    .getGameService()
                    .getPlayerGame((Player) sender);
                if (g2Opt.isEmpty()) {
                    sender.sendMessage("§cYou are not in any game.");
                    return;
                }
                Optional<ZentrixTeam> winningOpt = teamService.getWinningTeam(
                    g2Opt.get()
                );
                winningOpt.ifPresentOrElse(
                    t ->
                        sender.sendMessage(
                            "§7Winning Team: §e" + t.getDisplayName()
                        ),
                    () ->
                        sender.sendMessage(
                            "§7No winning team yet (multiple teams alive)"
                        )
                );
                break;
            case "members":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Optional<ZentrixTeam> myTeamOpt = teamService.getPlayerTeam(
                    (Player) sender
                );
                if (myTeamOpt.isEmpty()) {
                    sender.sendMessage("§cYou are not on a team.");
                    return;
                }
                Collection<ZentrixPlayer> members = teamService.getTeamMembers(
                    myTeamOpt.get()
                );
                sender.sendMessage("§7Team Members: §e" + members.size());
                for (ZentrixPlayer m : members) {
                    sender.sendMessage(
                        "  §f- " +
                            m.getName() +
                            " §8| §7Alive: §e" +
                            m.isAlive() +
                            " §8| §7Kills: §e" +
                            m.getGameKills()
                    );
                }
                break;
            default:
                sender.sendMessage(
                    "§cUnknown team subcommand. Use: list, myteam, teammates, alive, winning, members"
                );
        }
    }

    // ==========================================
    // ClassService Tests
    // ==========================================

    private void testClassService(CommandSender sender, String[] args) {
        ClassService classService = ZentrixAPI.get().getClassService();
        String subCmd = args.length > 0 ? args[0].toLowerCase() : "list";

        sender.sendMessage("§6§l=== ClassService Test ===");

        switch (subCmd) {
            case "list":
                Collection<PlayerClass> classes =
                    classService.getAvailableClasses();
                sender.sendMessage("§7Available Classes: §e" + classes.size());
                for (PlayerClass pc : classes) {
                    sender.sendMessage(
                        "  §f- " +
                            pc.getDisplayName() +
                            " §8| §7Type: §e" +
                            pc.getType()
                    );
                }
                break;
            case "myclass":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Optional<PlayerClass> myClassOpt =
                    classService.getSelectedClass((Player) sender);
                myClassOpt.ifPresentOrElse(
                    pc -> {
                        sender.sendMessage("§7Your Selected Class:");
                        sender.sendMessage("  §7Type: §e" + pc.getType());
                        sender.sendMessage(
                            "  §7Name: §e" + pc.getDisplayName()
                        );
                        sender.sendMessage(
                            "  §7Description: §e" + pc.getDescription()
                        );
                        sender.sendMessage(
                            "  §7Has Ability: §e" + pc.hasAbility()
                        );
                        String abilityType = pc.getAbilityType();
                        if (abilityType != null) {
                            sender.sendMessage(
                                "  §7Ability Type: §e" + abilityType
                            );
                        }
                    },
                    () -> sender.sendMessage("§cYou have not selected a class.")
                );
                break;
            case "info":
                if (args.length < 2) {
                    sender.sendMessage(
                        "§cUsage: /apitest class info <classType>"
                    );
                    return;
                }
                String classType = args[1].toUpperCase();
                Optional<PlayerClass> classOpt = classService.getClass(
                    classType
                );
                classOpt.ifPresentOrElse(
                    pc -> {
                        sender.sendMessage(
                            "§7Class Info: §e" + pc.getDisplayName()
                        );
                        sender.sendMessage("  §7Type: §e" + pc.getType());
                        sender.sendMessage(
                            "  §7Description: §e" + pc.getDescription()
                        );
                        sender.sendMessage(
                            "  §7Has Ability: §e" + pc.hasAbility()
                        );
                        sender.sendMessage(
                            "  §7Kit Items: §e" + pc.getKitItems().size()
                        );
                    },
                    () -> sender.sendMessage("§cClass not found: " + classType)
                );
                break;
            case "default":
                Optional<PlayerClass> defaultClass =
                    classService.getDefaultClass();
                defaultClass.ifPresentOrElse(
                    pc ->
                        sender.sendMessage(
                            "§7Default Class: §e" + pc.getDisplayName()
                        ),
                    () -> sender.sendMessage("§7No default class configured")
                );
                break;
            case "enabled":
                sender.sendMessage(
                    "§7Class System Enabled: §e" +
                        classService.isClassSystemEnabled()
                );
                sender.sendMessage(
                    "§7Class Count: §e" + classService.getClassCount()
                );
                break;
            default:
                sender.sendMessage(
                    "§cUnknown class subcommand. Use: list, myclass, info, default, enabled"
                );
        }
    }

    // ==========================================
    // CurrencyService Tests
    // ==========================================

    private void testCurrencyService(CommandSender sender, String[] args) {
        CurrencyService currencyService = ZentrixAPI.get().getCurrencyService();
        String subCmd = args.length > 0 ? args[0].toLowerCase() : "info";

        sender.sendMessage("§6§l=== CurrencyService Test ===");

        switch (subCmd) {
            case "balance":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Player player = (Player) sender;
                double cached = currencyService.getCachedBalance(player);
                sender.sendMessage(
                    "§7Cached Balance: §e" +
                        currencyService.formatBalance(cached)
                );
                // Async balance fetch
                currencyService
                    .getBalance(player)
                    .thenAccept(balance -> {
                        player.sendMessage(
                            "§7Async Balance: §e" +
                                currencyService.formatBalance(balance)
                        );
                    });
                break;
            case "info":
                sender.sendMessage(
                    "§7Currency Display Name: §e" +
                        currencyService.getDisplayName()
                );
                sender.sendMessage(
                    "§7Currency Symbol: §e" + currencyService.getSymbol()
                );
                sender.sendMessage(
                    "§7Starting Balance: §e" +
                        currencyService.getStartingBalance()
                );
                sender.sendMessage(
                    "§7Formatted 100: §e" + currencyService.formatBalance(100)
                );
                sender.sendMessage(
                    "§7Formatted 1234.5: §e" +
                        currencyService.formatBalance(1234.5)
                );
                break;
            case "events":
                sender.sendMessage("§7Currency Event Rewards:");
                for (CurrencyEventType eventType : CurrencyEventType.values()) {
                    boolean enabled = currencyService.isEventEnabled(eventType);
                    double reward = currencyService.getEventReward(eventType);
                    String status = enabled ? "§a✓" : "§c✗";
                    sender.sendMessage(
                        "  " +
                            status +
                            " §7" +
                            eventType.getConfigKey() +
                            ": §e" +
                            reward
                    );
                }
                break;
            case "format":
                sender.sendMessage("§7Format Tests:");
                sender.sendMessage(
                    "  §7formatAmount(100): §e" +
                        currencyService.formatAmount(100)
                );
                sender.sendMessage(
                    "  §7formatAmount(1000.5): §e" +
                        currencyService.formatAmount(1000.5)
                );
                sender.sendMessage(
                    "  §7formatBalance(500): §e" +
                        currencyService.formatBalance(500)
                );
                sender.sendMessage(
                    "  §7formatBalance(0): §e" +
                        currencyService.formatBalance(0)
                );
                break;
            default:
                sender.sendMessage(
                    "§cUnknown currency subcommand. Use: balance, info, events, format"
                );
        }
    }

    // ==========================================
    // PhaseService Tests
    // ==========================================

    private void testPhaseService(CommandSender sender, String[] args) {
        PhaseService phaseService = ZentrixAPI.get().getPhaseService();
        String subCmd = args.length > 0 ? args[0].toLowerCase() : "list";

        sender.sendMessage("§6§l=== PhaseService Test ===");

        switch (subCmd) {
            case "list":
                Collection<GamePhase> phases = phaseService.getAllPhases();
                sender.sendMessage("§7Configured Phases: §e" + phases.size());
                sender.sendMessage(
                    "§7Total Duration: §e" +
                        phaseService.getTotalPhaseDuration() +
                        "s"
                );
                int index = 0;
                for (GamePhase phase : phases) {
                    sender.sendMessage(
                        "  §7" +
                            index +
                            ". §e" +
                            phase.getDisplayName() +
                            " §8| §7Duration: §e" +
                            phase.getDuration() +
                            "s" +
                            " §8| §7Border: §e" +
                            phase.hasBorderShrinkage()
                    );
                    index++;
                }
                break;
            case "current":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Optional<ZentrixGame> gameOpt = api
                    .getGameService()
                    .getPlayerGame((Player) sender);
                if (gameOpt.isEmpty()) {
                    sender.sendMessage("§cYou are not in any game.");
                    return;
                }
                ZentrixGame game = gameOpt.get();
                Optional<GamePhase> currentOpt = phaseService.getCurrentPhase(
                    game
                );
                currentOpt.ifPresentOrElse(
                    phase -> {
                        sender.sendMessage("§7Current Phase:");
                        sender.sendMessage("  §7Name: §e" + phase.getName());
                        sender.sendMessage(
                            "  §7Display: §e" + phase.getDisplayName()
                        );
                        sender.sendMessage(
                            "  §7Duration: §e" + phase.getDuration() + "s"
                        );
                        sender.sendMessage(
                            "  §7Time Remaining: §e" +
                                phaseService.getTimeRemaining(game) +
                                "s"
                        );
                        sender.sendMessage(
                            "  §7Time Elapsed: §e" +
                                phaseService.getTimeElapsed(game) +
                                "s"
                        );
                        sender.sendMessage(
                            "  §7Has Border: §e" + phase.hasBorderShrinkage()
                        );
                        if (phase.hasBorderShrinkage()) {
                            sender.sendMessage(
                                "  §7Target Size: §e" +
                                    phase.getBorderTargetSize()
                            );
                        }
                    },
                    () -> sender.sendMessage("§cNo active phase.")
                );
                sender.sendMessage(
                    "§7Phase Index: §e" +
                        phaseService.getCurrentPhaseIndex(game)
                );
                sender.sendMessage(
                    "§7Is Paused: §e" + phaseService.isPaused(game)
                );
                break;
            case "next":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Optional<ZentrixGame> g2Opt = api
                    .getGameService()
                    .getPlayerGame((Player) sender);
                if (g2Opt.isEmpty()) {
                    sender.sendMessage("§cYou are not in any game.");
                    return;
                }
                Optional<GamePhase> nextOpt = phaseService.getNextPhase(
                    g2Opt.get()
                );
                nextOpt.ifPresentOrElse(
                    phase ->
                        sender.sendMessage(
                            "§7Next Phase: §e" +
                                phase.getDisplayName() +
                                " §8(§7" +
                                phase.getDuration() +
                                "s§8)"
                        ),
                    () ->
                        sender.sendMessage(
                            "§7No next phase (this is the last phase)"
                        )
                );
                break;
            case "time":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player.");
                    return;
                }
                Optional<ZentrixGame> g3Opt = api
                    .getGameService()
                    .getPlayerGame((Player) sender);
                if (g3Opt.isEmpty()) {
                    sender.sendMessage("§cYou are not in any game.");
                    return;
                }
                ZentrixGame g3 = g3Opt.get();
                sender.sendMessage(
                    "§7Time Remaining: §e" +
                        phaseService.getTimeRemaining(g3) +
                        "s"
                );
                sender.sendMessage(
                    "§7Time Elapsed: §e" + phaseService.getTimeElapsed(g3) + "s"
                );
                sender.sendMessage(
                    "§7Has Border Shrinkage: §e" +
                        phaseService.hasBorderShrinkage(g3)
                );
                sender.sendMessage(
                    "§7Target Border Size: §e" +
                        phaseService.getTargetBorderSize(g3)
                );
                sender.sendMessage(
                    "§7Completed All Phases: §e" +
                        phaseService.hasCompletedAllPhases(g3)
                );
                break;
            case "info":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /apitest phase info <index>");
                    return;
                }
                try {
                    int phaseIndex = Integer.parseInt(args[1]);
                    Optional<GamePhase> phaseOpt = phaseService.getPhaseByIndex(
                        phaseIndex
                    );
                    phaseOpt.ifPresentOrElse(
                        phase -> {
                            sender.sendMessage(
                                "§7Phase #" + phaseIndex + " Info:"
                            );
                            sender.sendMessage(
                                "  §7Name: §e" + phase.getName()
                            );
                            sender.sendMessage(
                                "  §7Display: §e" + phase.getDisplayName()
                            );
                            sender.sendMessage(
                                "  §7Duration: §e" + phase.getDuration() + "s"
                            );
                            sender.sendMessage(
                                "  §7Has Border Config: §e" +
                                    phase.hasBorderConfig()
                            );
                            sender.sendMessage(
                                "  §7Has Border Shrinkage: §e" +
                                    phase.hasBorderShrinkage()
                            );
                            if (phase.hasBorderShrinkage()) {
                                sender.sendMessage(
                                    "  §7Border Target: §e" +
                                        phase.getBorderTargetSize()
                                );
                                sender.sendMessage(
                                    "  §7Shrink Duration: §e" +
                                        phase.getBorderShrinkDuration() +
                                        "s"
                                );
                                sender.sendMessage(
                                    "  §7Damage/Block: §e" +
                                        phase.getBorderDamagePerBlock()
                                );
                            }
                            sender.sendMessage(
                                "  §7Has Warning: §e" + phase.hasWarning()
                            );
                            sender.sendMessage(
                                "  §7Warning Time: §e" +
                                    phase.getWarningTime() +
                                    "s"
                            );
                            sender.sendMessage(
                                "  §7OnStart Actions: §e" +
                                    phase.getOnStartActionCount()
                            );
                            sender.sendMessage(
                                "  §7Starts Deathmatch: §e" +
                                    phase.startsDeathmatch()
                            );
                            phase
                                .getTogglePvP()
                                .ifPresent(pvp ->
                                    sender.sendMessage(
                                        "  §7Toggles PvP: §e" + pvp
                                    )
                                );
                        },
                        () ->
                            sender.sendMessage(
                                "§cPhase not found at index: " + phaseIndex
                            )
                    );
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid phase index: " + args[1]);
                }
                break;
            default:
                sender.sendMessage(
                    "§cUnknown phase subcommand. Use: list, current, next, time, info"
                );
        }
    }

    // ==========================================
    // ProfileService Tests
    // ==========================================

    private void testProfileService(CommandSender sender, String[] args) {
        @SuppressWarnings("unused")
        ProfileService profileService = ZentrixAPI.get().getProfileService();

        sender.sendMessage("§6§l=== ProfileService Test ===");

        if (!(sender instanceof Player)) {
            sender.sendMessage(
                "§cYou must be a player to test profile service."
            );
            return;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        sender.sendMessage(
            "§7Profile service provides access to player statistics."
        );
        sender.sendMessage(
            "§7Note: Profile stats are for lifetime statistics across all games."
        );
        sender.sendMessage("");
        sender.sendMessage("§7Available methods:");
        sender.sendMessage("  §e- getPlayerStats(UUID)");
        sender.sendMessage("  §e- getTotalKills(UUID)");
        sender.sendMessage("  §e- getTotalDeaths(UUID)");
        sender.sendMessage("  §e- getTotalWins(UUID)");
        sender.sendMessage("  §e- getGamesPlayed(UUID)");
        sender.sendMessage("  §e- getKDRatio(UUID)");
        sender.sendMessage("  §e- getWinRate(UUID)");
        sender.sendMessage("");
        sender.sendMessage("§7Use the profile GUI in-game to view your stats!");
    }

    // ==========================================
    // DataService Tests
    // ==========================================

    private void testDataService(CommandSender sender, String[] args) {
        DataService dataService = ZentrixAPI.get().getDataService();

        if (args.length == 0) {
            sender.sendMessage("§6§l=== DataService Test ===");
            sender.sendMessage("§7Available subcommands:");
            sender.sendMessage(
                "  §e/apitest data folder §7- Show data folders"
            );
            sender.sendMessage(
                "  §e/apitest data config §7- Test config creation"
            );
            sender.sendMessage(
                "  §e/apitest data zentrix §7- Read Zentrix configs"
            );
            sender.sendMessage("  §e/apitest data files §7- List addon files");
            sender.sendMessage(
                "  §e/apitest data create <key> <value> §7- Create config entry"
            );
            sender.sendMessage(
                "  §e/apitest data read <key> §7- Read config entry"
            );
            sender.sendMessage(
                "  §e/apitest data write <key> <value> §7- Write config entry"
            );
            return;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "folder":
                sender.sendMessage("§6§l=== Data Folders ===");
                sender.sendMessage(
                    "§7Plugin folder: §e" +
                        dataService.getPluginDataFolder().getPath()
                );
                sender.sendMessage(
                    "§7Addons folder: §e" +
                        dataService.getAddonsFolder().getPath()
                );
                File addonFolder = dataService.getAddonDataFolder(
                    addon.getAddonId()
                );
                sender.sendMessage(
                    "§7This addon's folder: §e" + addonFolder.getPath()
                );
                sender.sendMessage(
                    "§7Folder exists: §e" + addonFolder.exists()
                );
                break;
            case "config":
                sender.sendMessage("§6§l=== Config Test ===");
                YamlConfiguration config = dataService.getOrCreateConfig(
                    addon.getAddonId(),
                    "test-config.yml"
                );
                sender.sendMessage("§7Config loaded/created successfully!");

                // Set some test values
                config.set("test.timestamp", System.currentTimeMillis());
                config.set("test.tester", sender.getName());
                config.set("test.random", new Random().nextInt(1000));

                boolean saved = dataService.saveConfig(
                    addon.getAddonId(),
                    "test-config.yml",
                    config
                );
                sender.sendMessage("§7Config saved: §e" + saved);
                sender.sendMessage("§7Values set:");
                sender.sendMessage(
                    "  §7test.timestamp: §e" + config.get("test.timestamp")
                );
                sender.sendMessage(
                    "  §7test.tester: §e" + config.get("test.tester")
                );
                sender.sendMessage(
                    "  §7test.random: §e" + config.get("test.random")
                );
                break;
            case "zentrix":
                sender.sendMessage(
                    "§6§l=== Zentrix Config Access (Read-Only) ==="
                );

                // Read currency config
                String currencySymbol = dataService.getZentrixConfigString(
                    "currency",
                    "currency.symbol",
                    "⛃"
                );
                String currencyName = dataService.getZentrixConfigString(
                    "currency",
                    "currency.display-name",
                    "Coins"
                );
                sender.sendMessage("§7Currency symbol: §e" + currencySymbol);
                sender.sendMessage("§7Currency name: §e" + currencyName);

                // Read settings config
                int freezeDuration = dataService.getZentrixConfigInt(
                    "settings",
                    "deathmatch-settings.freeze-duration",
                    5
                );
                sender.sendMessage(
                    "§7Deathmatch freeze duration: §e" + freezeDuration + "s"
                );

                // List available configs
                sender.sendMessage("");
                sender.sendMessage("§7Available Zentrix configs:");
                sender.sendMessage(
                    "  §8settings, phases, game-types, teams, classes, currency, broadcasts"
                );
                break;
            case "files":
                sender.sendMessage("§6§l=== Addon Files ===");
                String[] files = dataService.listAddonFiles(addon.getAddonId());
                sender.sendMessage(
                    "§7Files in addon folder: §e" + files.length
                );
                for (String file : files) {
                    boolean isFile = dataService
                        .getAddonFile(addon.getAddonId(), file)
                        .isFile();
                    sender.sendMessage(
                        "  §f" + file + (isFile ? "" : " §8(folder)")
                    );
                }
                break;
            case "create":
                if (args.length < 3) {
                    sender.sendMessage(
                        "§cUsage: /apitest data create <key> <value>"
                    );
                    return;
                }
                String createKey = args[1];
                String createValue = String.join(
                    " ",
                    Arrays.copyOfRange(args, 2, args.length)
                );

                YamlConfiguration createConfig = dataService.getOrCreateConfig(
                    addon.getAddonId(),
                    "custom-data.yml"
                );
                createConfig.set(createKey, createValue);
                dataService.saveConfig(
                    addon.getAddonId(),
                    "custom-data.yml",
                    createConfig
                );
                sender.sendMessage(
                    "§aCreated: §e" + createKey + " §7= §f" + createValue
                );
                break;
            case "read":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /apitest data read <key>");
                    return;
                }
                String readKey = args[1];

                YamlConfiguration readConfig = dataService.getOrCreateConfig(
                    addon.getAddonId(),
                    "custom-data.yml"
                );
                Object value = readConfig.get(readKey);
                if (value != null) {
                    sender.sendMessage(
                        "§aValue: §e" + readKey + " §7= §f" + value
                    );
                } else {
                    sender.sendMessage("§cKey not found: " + readKey);
                }
                break;
            case "write":
                if (args.length < 3) {
                    sender.sendMessage(
                        "§cUsage: /apitest data write <key> <value>"
                    );
                    return;
                }
                String writeKey = args[1];
                String writeValue = String.join(
                    " ",
                    Arrays.copyOfRange(args, 2, args.length)
                );

                YamlConfiguration writeConfig = dataService.getOrCreateConfig(
                    addon.getAddonId(),
                    "custom-data.yml"
                );
                writeConfig.set(writeKey, writeValue);
                dataService.saveConfig(
                    addon.getAddonId(),
                    "custom-data.yml",
                    writeConfig
                );
                sender.sendMessage(
                    "§aWritten: §e" + writeKey + " §7= §f" + writeValue
                );
                break;
            default:
                sender.sendMessage(
                    "§cUnknown data subcommand. Use: folder, config, zentrix, files, create, read, write"
                );
                break;
        }
    }

    // ==========================================
    // RecipeService Tests
    // ==========================================

    private void testRecipeService(CommandSender sender, String[] args) {
        RecipeService recipeService = ZentrixAPI.get().getRecipeService();

        if (args.length == 0) {
            sender.sendMessage("§6§l=== RecipeService Test ===");
            sender.sendMessage("§7Available subcommands:");
            sender.sendMessage("  §e/apitest recipe list §7- List all recipes");
            sender.sendMessage(
                "  §e/apitest recipe count §7- Get recipe count"
            );
            sender.sendMessage(
                "  §e/apitest recipe info <id> §7- Get recipe details"
            );
            sender.sendMessage(
                "  §e/apitest recipe create §7- Create a test recipe"
            );
            sender.sendMessage(
                "  §e/apitest recipe remove <id> §7- Remove a recipe"
            );
            sender.sendMessage(
                "  §e/apitest recipe cancraft <id> §7- Check if you can craft"
            );
            sender.sendMessage(
                "  §e/apitest recipe remaining <id> §7- Check remaining crafts"
            );
            return;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "list":
                sender.sendMessage("§6§l=== All Recipes ===");
                Collection<ZentrixRecipe> recipes =
                    recipeService.getAllRecipes();
                sender.sendMessage("§7Total recipes: §e" + recipes.size());
                for (ZentrixRecipe recipe : recipes) {
                    String type = recipe.isShaped() ? "Shaped" : "Shapeless";
                    String oneTime = recipe.isOneTime() ? " §c[ONE-TIME]" : "";
                    String limit = recipe.hasCraftLimit()
                        ? " §e[Limit: " + recipe.getCraftLimit() + "]"
                        : "";
                    sender.sendMessage(
                        "  §f" +
                            recipe.getId() +
                            " §8(" +
                            type +
                            ")" +
                            oneTime +
                            limit
                    );
                }
                break;
            case "count":
                sender.sendMessage("§6§l=== Recipe Count ===");
                sender.sendMessage(
                    "§7Registered recipes: §e" + recipeService.getRecipeCount()
                );
                sender.sendMessage(
                    "§7One-time recipes: §e" +
                        recipeService.getOneTimeRecipes().size()
                );
                sender.sendMessage(
                    "§7Limited recipes: §e" +
                        recipeService.getLimitedRecipes().size()
                );
                break;
            case "info":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /apitest recipe info <id>");
                    return;
                }
                String infoId = args[1].toLowerCase();
                Optional<ZentrixRecipe> recipeOpt = recipeService.getRecipe(
                    infoId
                );
                if (recipeOpt.isEmpty()) {
                    sender.sendMessage("§cRecipe not found: " + infoId);
                    return;
                }
                ZentrixRecipe recipe = recipeOpt.get();
                sender.sendMessage(
                    "§6§l=== Recipe: " + recipe.getId() + " ==="
                );
                sender.sendMessage(
                    "§7Type: §e" + (recipe.isShaped() ? "Shaped" : "Shapeless")
                );
                sender.sendMessage(
                    "§7Result: §e" +
                        recipe.getResult().getType() +
                        " x" +
                        recipe.getResultAmount()
                );
                sender.sendMessage(
                    "§7One-time: §e" + (recipe.isOneTime() ? "Yes" : "No")
                );
                sender.sendMessage(
                    "§7Craft limit: §e" +
                        (recipe.hasCraftLimit()
                            ? recipe.getCraftLimit()
                            : "Unlimited")
                );
                recipe
                    .getCreator()
                    .ifPresent(c -> sender.sendMessage("§7Creator: §e" + c));
                recipe
                    .getCreationTime()
                    .ifPresent(t -> sender.sendMessage("§7Created: §e" + t));
                break;
            case "create":
                sender.sendMessage("§6§l=== Creating Test Recipe ===");
                String testId =
                    "api-test-recipe-" + (System.currentTimeMillis() % 10000);

                RecipeBuilder builder = new RecipeBuilder()
                    .id(testId)
                    .shapeless()
                    .result(
                        new org.bukkit.inventory.ItemStack(Material.DIAMOND)
                    )
                    .addIngredient(Material.COAL, 4)
                    .addIngredient(Material.IRON_INGOT, 2)
                    .craftLimit(3)
                    .customField("addon_id", addon.getAddonId());

                boolean created = recipeService.registerRecipe(builder);
                if (created) {
                    sender.sendMessage("§aRecipe created: §e" + testId);
                    sender.sendMessage("§7Craft 4 Coal + 2 Iron = 1 Diamond");
                    sender.sendMessage("§7Craft limit: 3 per game");
                } else {
                    sender.sendMessage("§cFailed to create recipe");
                }
                break;
            case "remove":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /apitest recipe remove <id>");
                    return;
                }
                String removeId = args[1].toLowerCase();
                boolean removed = recipeService.unregisterRecipe(removeId);
                if (removed) {
                    sender.sendMessage("§aRecipe removed: §e" + removeId);
                } else {
                    sender.sendMessage(
                        "§cRecipe not found or could not remove: " + removeId
                    );
                }
                break;
            case "cancraft":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(
                        "§cYou must be a player to use this command."
                    );
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(
                        "§cUsage: /apitest recipe cancraft <id>"
                    );
                    return;
                }
                Player craftPlayer = (Player) sender;
                String craftId = args[1].toLowerCase();
                boolean canCraft = recipeService.canPlayerCraft(
                    craftPlayer,
                    craftId
                );
                sender.sendMessage(
                    "§7Can craft §e" +
                        craftId +
                        "§7: " +
                        (canCraft ? "§aYes" : "§cNo")
                );
                break;
            case "remaining":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(
                        "§cYou must be a player to use this command."
                    );
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(
                        "§cUsage: /apitest recipe remaining <id>"
                    );
                    return;
                }
                Player remainingPlayer = (Player) sender;
                String remainingId = args[1].toLowerCase();
                int remaining = recipeService.getRemainingCrafts(
                    remainingPlayer,
                    remainingId
                );
                int craftCount = recipeService.getPlayerCraftCount(
                    remainingPlayer,
                    remainingId
                );
                if (remaining == -1) {
                    sender.sendMessage(
                        "§7Recipe §e" +
                            remainingId +
                            "§7 has no limit (unlimited)"
                    );
                } else {
                    sender.sendMessage(
                        "§7Remaining crafts for §e" +
                            remainingId +
                            "§7: §a" +
                            remaining
                    );
                }
                sender.sendMessage(
                    "§7Times crafted this game: §e" + craftCount
                );
                break;
            default:
                sender.sendMessage(
                    "§cUnknown recipe subcommand. Use: list, count, info, create, remove, cancraft, remaining"
                );
                break;
        }
    }

    // ==========================================
    // AddonManager Tests
    // ==========================================

    private void testAddonManager(CommandSender sender, String[] args) {
        AddonManager addonManager = ZentrixAPI.get().getAddonManager();

        sender.sendMessage("§6§l=== AddonManager Test ===");
        sender.sendMessage(
            "§7Registered Addons: §e" + addonManager.getAddonCount()
        );
        sender.sendMessage("");

        Collection<AddonManager.AddonInfo> addons =
            addonManager.getRegisteredAddons();
        if (addons.isEmpty()) {
            sender.sendMessage(
                "§7No addons registered (besides this example addon)."
            );
        } else {
            for (AddonManager.AddonInfo addon : addons) {
                sender.sendMessage(
                    "§e" + addon.getName() + " §7v" + addon.getVersion()
                );
                sender.sendMessage("  §7ID: §f" + addon.getId());
                sender.sendMessage(
                    "  §7Description: §f" + addon.getDescription()
                );
                sender.sendMessage(
                    "  §7Authors: §f" + String.join(", ", addon.getAuthors())
                );
                sender.sendMessage("  §7Enabled: §f" + addon.isEnabled());
                sender.sendMessage(
                    "  §7Plugin Class: §f" +
                        addon.getPlugin().getClass().getSimpleName()
                );
                sender.sendMessage("");
            }
        }

        // Check this addon
        sender.sendMessage("§7--- Self Check ---");
        boolean isRegistered = addonManager.isAddonRegistered(addon);
        sender.sendMessage("§7This addon registered: §e" + isRegistered);

        Optional<AddonManager.AddonInfo> selfInfo = addonManager.getAddon(
            addon
        );
        selfInfo.ifPresent(info -> {
            sender.sendMessage("§7Self lookup successful: §e" + info.getName());
        });

        // Check by ID
        boolean byId = addonManager.isAddonRegistered("zentrix-example-addon");
        sender.sendMessage("§7Registered by ID: §e" + byId);
    }

    // ==========================================
    // Run All Tests
    // ==========================================

    private void runAllTests(CommandSender sender) {
        sender.sendMessage("§6§l=== Running All API Tests ===");
        sender.sendMessage("");

        testGameService(sender, new String[] { "count" });
        sender.sendMessage("");

        testPlayerService(sender, new String[] { "all" });
        sender.sendMessage("");

        testClassService(sender, new String[] { "enabled" });
        sender.sendMessage("");

        testCurrencyService(sender, new String[] { "info" });
        sender.sendMessage("");

        testPhaseService(sender, new String[] { "list" });
        sender.sendMessage("");

        testAddonManager(sender, new String[] {});
        sender.sendMessage("");

        testDataService(sender, new String[] { "folder" });
        sender.sendMessage("");

        sender.sendMessage("§6§l=== All Tests Complete ===");
        sender.sendMessage(
            "§7Check console for detailed event logs when games are active."
        );
    }

    // ==========================================
    // Tab Completion
    // ==========================================

    @Override
    public @Nullable List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String alias,
        @NotNull String[] args
    ) {
        if (args.length == 1) {
            return filterCompletions(SUBCOMMANDS, args[0]);
        }

        if (args.length == 2) {
            String subCmd = args[0].toLowerCase();
            switch (subCmd) {
                case "games":
                    return filterCompletions(GAME_SUBCOMMANDS, args[1]);
                case "player":
                    return filterCompletions(PLAYER_SUBCOMMANDS, args[1]);
                case "team":
                    return filterCompletions(TEAM_SUBCOMMANDS, args[1]);
                case "class":
                    return filterCompletions(CLASS_SUBCOMMANDS, args[1]);
                case "currency":
                    return filterCompletions(CURRENCY_SUBCOMMANDS, args[1]);
                case "phase":
                    return filterCompletions(PHASE_SUBCOMMANDS, args[1]);
                case "data":
                    return filterCompletions(DATA_SUBCOMMANDS, args[1]);
                case "recipe":
                    return filterCompletions(RECIPE_SUBCOMMANDS, args[1]);
            }
        }

        if (args.length == 3) {
            String subCmd = args[0].toLowerCase();
            String subSubCmd = args[1].toLowerCase();

            // Recipe ID completions
            if (subCmd.equals("recipe")) {
                if (
                    subSubCmd.equals("info") ||
                    subSubCmd.equals("remove") ||
                    subSubCmd.equals("cancraft") ||
                    subSubCmd.equals("remaining")
                ) {
                    return filterCompletions(
                        new ArrayList<>(ZentrixAPI.get().getRecipeService().getRecipeIds()),
                        args[2]
                    );
                }
            }
        }

        if (args.length == 3) {
            String subCmd = args[0].toLowerCase();
            String subSubCmd = args[1].toLowerCase();

            // Arena completions for games byarena
            if (subCmd.equals("games") && subSubCmd.equals("byarena")) {
                return filterCompletions(
                    new ArrayList<>(ZentrixAPI.get().getGameService().getAvailableArenas()),
                    args[2]
                );
            }

            // Class type completions
            if (subCmd.equals("class") && subSubCmd.equals("info")) {
                return filterCompletions(
                    api
                        .getClassService()
                        .getAvailableClasses()
                        .stream()
                        .map(pc -> pc.getType())
                        .collect(Collectors.toList()),
                    args[2]
                );
            }

            // Phase index completions
            if (subCmd.equals("phase") && subSubCmd.equals("info")) {
                List<String> indices = new ArrayList<>();
                for (
                    int i = 0;
                    i < ZentrixAPI.get().getPhaseService().getPhaseCount();
                    i++
                ) {
                    indices.add(String.valueOf(i));
                }
                return filterCompletions(indices, args[2]);
            }
        }

        return Collections.emptyList();
    }

    private List<String> filterCompletions(List<String> options, String input) {
        String lower = input.toLowerCase();
        return options
            .stream()
            .filter(opt -> opt.toLowerCase().startsWith(lower))
            .collect(Collectors.toList());
    }
}
