package dev.itsharshxd.zentrix.example.listeners;

import dev.itsharshxd.zentrix.api.events.player.PlayerDeathGameEvent;
import dev.itsharshxd.zentrix.api.events.player.PlayerJoinGameEvent;
import dev.itsharshxd.zentrix.api.events.player.PlayerKillEvent;
import dev.itsharshxd.zentrix.api.events.player.PlayerLeaveGameEvent;
import dev.itsharshxd.zentrix.api.game.ZentrixGame;
import dev.itsharshxd.zentrix.api.player.ZentrixPlayer;
import dev.itsharshxd.zentrix.example.ExampleAddon;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Demonstrates listening to all player-related Zentrix events.
 * <p>
 * Events covered:
 * <ul>
 *   <li>{@link PlayerJoinGameEvent} - Fired when a player joins a game (cancellable)</li>
 *   <li>{@link PlayerLeaveGameEvent} - Fired when a player leaves a game</li>
 *   <li>{@link PlayerKillEvent} - Fired when a player kills another player</li>
 *   <li>{@link PlayerDeathGameEvent} - Fired when a player is eliminated</li>
 * </ul>
 * </p>
 */
public class PlayerEventListener implements Listener {

    private final ExampleAddon addon;
    private final Logger logger;

    public PlayerEventListener(ExampleAddon addon) {
        this.addon = addon;
        this.logger = addon.getLogger();
    }

    // ==========================================
    // PlayerJoinGameEvent (Cancellable)
    // ==========================================

    /**
     * Called when a player is about to join a Zentrix game.
     * <p>
     * This event is CANCELLABLE - you can prevent players from joining.
     * At this point:
     * - Player has not been added to the game yet
     * - You can check conditions and cancel if needed
     * </p>
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinGame(PlayerJoinGameEvent event) {
        ZentrixGame game = event.getGame();
        Player player = event.getPlayer();

        logger.info("========== PLAYER JOIN GAME EVENT ==========");
        logger.info("Player: " + event.getPlayerName());
        logger.info("Player UUID: " + player.getUniqueId());
        logger.info("Game ID: " + event.getGameId());
        logger.info("Arena: " + event.getArenaName());
        logger.info("Game State: " + game.getState());
        logger.info("Is Spectator Join: " + event.isSpectator());
        logger.info("Is Active Player Join: " + event.isActivePlayer());
        logger.info("Is Cancelled: " + event.isCancelled());

        // Game information
        logger.info("--- Game Info ---");
        logger.info("Current Players: " + event.getCurrentPlayerCount());
        logger.info("Players After Join: " + event.getPlayerCountAfterJoin());
        logger.info("Max Players: " + game.getMaxPlayers());
        logger.info("Min Players: " + game.getMinPlayers());
        logger.info("Team Size: " + game.getTeamSize());
        logger.info("Game Type: " + game.getGameTypeName());
        logger.info("Will Be Full: " + event.willBeFull());

        // Bukkit player info
        logger.info("--- Bukkit Player ---");
        logger.info("Display Name: " + player.getName());
        logger.info("Health: " + player.getHealth());
        logger.info("World: " + player.getWorld().getName());

        logger.info("=============================================");

        // Example: Prevent players from joining if a condition is met
        // Uncomment to test cancellation:
        // if (someCondition) {
        //     event.cancel("You cannot join this game!");
        // }
    }

    // ==========================================
    // PlayerLeaveGameEvent
    // ==========================================

    /**
     * Called when a player leaves a Zentrix game.
     * <p>
     * This includes:
     * - Player disconnects
     * - Player uses /leave command
     * - Player is kicked
     * - Player dies and becomes spectator (if configured)
     * </p>
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeaveGame(PlayerLeaveGameEvent event) {
        ZentrixGame game = event.getGame();
        ZentrixPlayer player = event.getPlayer();

        logger.info("========== PLAYER LEAVE GAME EVENT ==========");
        logger.info("Player: " + event.getPlayerName());
        logger.info("Player UUID: " + player.getUniqueId());
        logger.info("Game ID: " + event.getGameId());
        logger.info("Arena: " + event.getArenaName());
        logger.info("Leave Reason: " + event.getReason());
        logger.info("Final Kills: " + event.getPlayerKills());
        logger.info("Survival Time: " + event.getSurvivalTime() + "s");
        logger.info("Had Kills: " + event.hadKills());

        // Leave reason details using convenience methods
        logger.info("--- Leave Type Checks ---");
        logger.info("Was Voluntary (command): " + event.wasVoluntary());
        logger.info("Was Death: " + event.wasDeath());
        logger.info("Was Disconnect: " + event.wasDisconnect());
        logger.info("Was Kicked: " + event.wasKicked());

        // Leave reason switch
        PlayerLeaveGameEvent.LeaveReason reason = event.getReason();
        switch (reason) {
            case QUIT:
                logger.info("Player disconnected from the server");
                break;
            case COMMAND:
                logger.info("Player used /leave command");
                break;
            case KICK:
                logger.info("Player was kicked from the server");
                break;
            case DEATH:
                logger.info("Player died and left the game");
                break;
            case GAME_END:
                logger.info("Game ended");
                break;
            case OTHER:
                logger.info("Player left for unknown reason");
                break;
        }

        // Player's team info
        event
            .getTeam()
            .ifPresent(team -> {
                logger.info("--- Player's Team ---");
                logger.info("Team ID: " + team.getTeamId());
                logger.info("Team Members: " + team.getMemberCount());
                logger.info("Alive Members: " + team.getAliveMemberCount());
            });

        // Game status after leave
        logger.info("--- Game Status After Leave ---");
        logger.info("Remaining Players: " + event.getRemainingPlayers());
        logger.info("Remaining Teams: " + event.getRemainingTeams());
        logger.info("Could Trigger Win: " + event.couldTriggerWin());

        logger.info("==============================================");
    }

    // ==========================================
    // PlayerKillEvent
    // ==========================================

    /**
     * Called when a player kills another player.
     * <p>
     * This event provides:
     * - Killer and victim information
     * - Currency reward information
     * - Kill streak tracking
     * </p>
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKill(PlayerKillEvent event) {
        ZentrixGame game = event.getGame();
        ZentrixPlayer killer = event.getKiller();
        ZentrixPlayer victim = event.getVictim();

        logger.info("========== PLAYER KILL EVENT ==========");
        logger.info("Game ID: " + event.getGameId());
        logger.info("Arena: " + event.getArenaName());

        // Killer info
        logger.info("--- Killer ---");
        logger.info("Name: " + event.getKillerName());
        logger.info("UUID: " + killer.getUniqueId());
        logger.info("Total Kills: " + killer.getGameKills());
        logger.info("Kill Streak: " + killer.getKillStreak());
        logger.info("Highest Kill Streak: " + killer.getHighestKillStreak());
        logger.info("Damage Dealt: " + killer.getDamageDealt());
        killer
            .getTeam()
            .ifPresent(team -> logger.info("Team: " + team.getDisplayName()));
        killer
            .getSelectedClass()
            .ifPresent(playerClass ->
                logger.info("Class: " + playerClass.getDisplayName())
            );

        // Victim info
        logger.info("--- Victim ---");
        logger.info("Name: " + event.getVictimName());
        logger.info("UUID: " + victim.getUniqueId());
        logger.info("Kills Before Death: " + victim.getGameKills());
        logger.info("Damage Dealt: " + victim.getDamageDealt());
        logger.info("Damage Taken: " + victim.getDamageTaken());
        logger.info("Survival Time: " + victim.getSurvivalTimeSeconds() + "s");
        victim
            .getTeam()
            .ifPresent(team -> logger.info("Team: " + team.getDisplayName()));

        // Kill details
        logger.info("--- Kill Details ---");
        logger.info("Is First Blood: " + event.isFirstBlood());
        logger.info("Currency Reward: " + event.getCurrencyReward());
        logger.info("Was Team Kill: " + event.wasTeamKill());

        // Game status
        logger.info("--- Game Status ---");
        logger.info("Players Remaining: " + game.getPlayerCount());
        logger.info("Teams Remaining: " + game.getAliveTeamCount());

        logger.info("========================================");

        // Example: Announce first blood
        if (event.isFirstBlood()) {
            game.broadcast(
                "&c&l[FIRST BLOOD] &e" +
                    event.getKillerName() +
                    " &7drew first blood by killing &e" +
                    event.getVictimName() +
                    "&7!"
            );
        }
    }

    // ==========================================
    // PlayerDeathGameEvent
    // ==========================================

    /**
     * Called when a player is eliminated (dies) in a game.
     * <p>
     * This event provides comprehensive death information including:
     * - Death cause and location
     * - Killer information (if killed by player)
     * - Team elimination status
     * - Win condition trigger status
     * </p>
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathGameEvent event) {
        ZentrixGame game = event.getGame();
        ZentrixPlayer victim = event.getVictim();

        logger.info("========== PLAYER DEATH GAME EVENT ==========");
        logger.info("Game ID: " + event.getGameId());
        logger.info("Arena: " + event.getArenaName());

        // Victim info
        logger.info("--- Victim ---");
        logger.info("Name: " + event.getVictimName());
        logger.info("UUID: " + victim.getUniqueId());
        logger.info("Final Kills: " + event.getVictimKills());
        logger.info("Survival Time: " + event.getSurvivalTime() + "s");
        event
            .getVictimTeam()
            .ifPresent(team -> {
                logger.info("Team: " + team.getDisplayName());
                logger.info("Team Eliminated: " + event.eliminatesTeam());
            });

        // Death details
        logger.info("--- Death Details ---");
        logger.info("Death Cause: " + event.getDeathCause());
        logger.info("Has Killer: " + event.hasKiller());
        logger.info("Was Player Kill: " + event.wasPlayerKill());
        logger.info("Was Border Death: " + event.wasBorderDeath());
        logger.info(
            "Was Environmental Death: " + event.wasEnvironmentalDeath()
        );
        logger.info("Was Team Kill: " + event.wasTeamKill());

        // Death location
        Location deathLoc = event.getDeathLocation();
        logger.info("--- Death Location ---");
        logger.info("World: " + deathLoc.getWorld().getName());
        logger.info("X: " + deathLoc.getBlockX());
        logger.info("Y: " + deathLoc.getBlockY());
        logger.info("Z: " + deathLoc.getBlockZ());

        // Killer info (if applicable)
        event
            .getKiller()
            .ifPresent(killer -> {
                logger.info("--- Killer ---");
                logger.info("Name: " + event.getKillerName().orElse("Unknown"));
                logger.info("Kills: " + killer.getGameKills());
                killer
                    .getTeam()
                    .ifPresent(team ->
                        logger.info("Team: " + team.getDisplayName())
                    );
            });

        // Bukkit player access
        event
            .getVictimBukkit()
            .ifPresent(bukkitPlayer -> {
                logger.info("--- Bukkit Victim ---");
                logger.info("Display Name: " + bukkitPlayer.getName());
            });

        event
            .getKillerBukkit()
            .ifPresent(bukkitKiller -> {
                logger.info("--- Bukkit Killer ---");
                logger.info("Display Name: " + bukkitKiller.getName());
            });

        // Game status
        logger.info("--- Game Status After Death ---");
        logger.info("Remaining Players: " + event.getRemainingPlayers());
        logger.info("Remaining Teams: " + event.getRemainingTeams());
        logger.info("Team Eliminated: " + event.eliminatesTeam());
        logger.info("Could Trigger Win: " + event.couldTriggerWin());

        // Death cause switch
        PlayerDeathGameEvent.DeathCause cause = event.getDeathCause();
        switch (cause) {
            case PLAYER:
                logger.info("Killed by another player");
                break;
            case BORDER:
                logger.info("Killed by world border");
                break;
            case FALL:
                logger.info("Killed by fall damage");
                break;
            case FIRE:
                logger.info("Killed by fire/lava");
                break;
            case DROWNING:
                logger.info("Killed by drowning");
                break;
            case EXPLOSION:
                logger.info("Killed by explosion");
                break;
            case MOB:
                logger.info("Killed by a mob");
                break;
            case STARVATION:
                logger.info("Killed by starvation");
                break;
            case SUFFOCATION:
                logger.info("Killed by suffocation");
                break;
            case VOID:
                logger.info("Killed by void");
                break;
            case OTHER:
                logger.info("Killed by other cause");
                break;
        }

        logger.info("==============================================");

        // Example: Team elimination announcement
        if (event.eliminatesTeam()) {
            event
                .getVictimTeam()
                .ifPresent(team ->
                    game.broadcast(
                        "&c&l[ELIMINATED] &7Team &e" +
                            team.getDisplayName() +
                            " &7has been eliminated! &8(" +
                            event.getRemainingTeams() +
                            " teams remain)"
                    )
                );
        }
    }
}
