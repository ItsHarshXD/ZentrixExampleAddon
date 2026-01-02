package dev.itsharshxd.addon.zentrix.example.listeners;

import dev.itsharshxd.zentrix.api.events.game.GameEndEvent;
import dev.itsharshxd.zentrix.api.events.game.GamePhaseChangeEvent;
import dev.itsharshxd.zentrix.api.events.game.GameStartEvent;
import dev.itsharshxd.zentrix.api.game.ZentrixGame;
import dev.itsharshxd.zentrix.api.phase.GamePhase;
import dev.itsharshxd.zentrix.api.player.ZentrixPlayer;
import dev.itsharshxd.zentrix.api.team.ZentrixTeam;
import dev.itsharshxd.addon.zentrix.example.ExampleAddon;
import java.util.Collection;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Demonstrates listening to all game-related Zentrix events.
 * <p>
 * Events covered:
 * <ul>
 *   <li>{@link GameStartEvent} - Fired when a game transitions to PLAYING state</li>
 *   <li>{@link GameEndEvent} - Fired when a game ends (winner determined or force-ended)</li>
 *   <li>{@link GamePhaseChangeEvent} - Fired when the game phase changes</li>
 * </ul>
 * </p>
 */
public class GameEventListener implements Listener {

    private final ExampleAddon addon;
    private final Logger logger;

    public GameEventListener(ExampleAddon addon) {
        this.addon = addon;
        this.logger = addon.getLogger();
    }

    // ==========================================
    // GameStartEvent
    // ==========================================

    /**
     * Called when a Zentrix game starts (enters PLAYING state).
     * <p>
     * At this point:
     * - All players are teleported to the arena
     * - Teams are assigned
     * - First phase is about to begin
     * </p>
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameStart(GameStartEvent event) {
        ZentrixGame game = event.getGame();

        logger.info("========== GAME START EVENT ==========");
        logger.info("Game ID: " + event.getGameId());
        logger.info("Arena: " + event.getArenaName());
        logger.info("Game Type: " + event.getGameTypeName());
        logger.info("Starting Players: " + event.getStartingPlayerCount());
        logger.info("Starting Teams: " + event.getStartingTeamCount());
        logger.info("Max Players: " + event.getMaxPlayers());
        logger.info("Team Size: " + event.getTeamSize());
        logger.info("Is Solo Game: " + event.isSoloGame());
        logger.info("Is Team Game: " + event.isTeamGame());

        // Access game object for more details
        logger.info("--- Game Details ---");
        logger.info("Game State: " + game.getState());
        logger.info("Min Players: " + game.getMinPlayers());

        // List all teams
        Collection<ZentrixTeam> teams = event.getTeams();
        logger.info("--- Teams (" + teams.size() + ") ---");
        for (ZentrixTeam team : teams) {
            logger.info(
                "  Team: " +
                    team.getTeamId() +
                    " | Members: " +
                    team.getMemberCount() +
                    " | Color: " +
                    team.getColor()
            );
        }

        // List all players
        Collection<ZentrixPlayer> players = game.getPlayers();
        logger.info("--- Players (" + players.size() + ") ---");
        for (ZentrixPlayer player : players) {
            String teamId = player.getTeamId().orElse("none");
            String className = player
                .getSelectedClass()
                .map(c -> c.getDisplayName())
                .orElse("none");
            logger.info(
                "  " +
                    player.getName() +
                    " | Team: " +
                    teamId +
                    " | Class: " +
                    className
            );
        }

        logger.info("======================================");

        // Example: Broadcast a custom message to all players
        game.broadcast(
            "&a&l[ExampleAddon] &7Game started! Good luck everyone!"
        );
    }

    // ==========================================
    // GameEndEvent
    // ==========================================

    /**
     * Called when a Zentrix game ends.
     * <p>
     * This event provides information about:
     * - The winning team/players
     * - The reason the game ended
     * - Game duration
     * </p>
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameEnd(GameEndEvent event) {
        ZentrixGame game = event.getGame();

        logger.info("========== GAME END EVENT ==========");
        logger.info("Game ID: " + event.getGameId());
        logger.info("Arena: " + event.getArenaName());
        logger.info("End Reason: " + event.getEndReason());
        logger.info("Game Duration: " + event.getGameDuration() + "s");
        logger.info("Formatted Duration: " + event.getFormattedDuration());
        logger.info("Has Winner: " + event.hasWinner());
        logger.info("Is Normal End: " + event.isNormalEnd());
        logger.info("Is Forced End: " + event.isForcedEnd());
        logger.info("Winner Count: " + event.getWinnerCount());

        // Check winning team
        event
            .getWinningTeam()
            .ifPresent(team -> {
                logger.info("--- Winning Team ---");
                logger.info("  Team ID: " + team.getTeamId());
                logger.info("  Display Name: " + team.getDisplayName());
                logger.info("  Members: " + team.getMemberCount());
            });

        // List all winners
        Collection<ZentrixPlayer> winners = event.getWinners();
        if (!winners.isEmpty()) {
            logger.info("--- Winners (" + winners.size() + ") ---");
            for (ZentrixPlayer winner : winners) {
                logger.info(
                    "  " +
                        winner.getName() +
                        " | Kills: " +
                        winner.getGameKills() +
                        " | Survival Time: " +
                        winner.getSurvivalTimeSeconds() +
                        "s"
                );
            }
        }

        // Log end reason details
        GameEndEvent.EndReason reason = event.getEndReason();
        switch (reason) {
            case WINNER_DETERMINED:
                logger.info("Game ended normally with a winner!");
                break;
            case ALL_PLAYERS_LEFT:
                logger.info(
                    "All players left before winner could be determined"
                );
                break;
            case FORCE_ENDED:
                logger.info("Game was forcibly ended by an administrator");
                break;
            case PLUGIN_DISABLED:
                logger.info("Game ended due to plugin being disabled");
                break;
            case ERROR:
                logger.info("Game ended due to an error");
                break;
        }

        logger.info("====================================");
    }

    // ==========================================
    // GamePhaseChangeEvent
    // ==========================================

    /**
     * Called when a game phase changes.
     * <p>
     * Phase changes include:
     * - Transition from one phase to another
     * - Border shrinkage updates
     * </p>
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPhaseChange(GamePhaseChangeEvent event) {
        ZentrixGame game = event.getGame();

        logger.info("========== PHASE CHANGE EVENT ==========");
        logger.info("Game ID: " + event.getGameId());
        logger.info("Arena: " + event.getArenaName());
        logger.info("Change Type: " + event.getChangeType());

        // Previous phase info
        event
            .getOldPhase()
            .ifPresentOrElse(
                phase -> {
                    logger.info("--- Previous Phase ---");
                    logPhaseDetails(phase);
                },
                () -> logger.info("Previous Phase: None (first phase)")
            );

        // New phase info
        GamePhase newPhase = event.getNewPhase();
        logger.info("--- New Phase ---");
        logPhaseDetails(newPhase);

        // Current game status
        logger.info("--- Current Game Status ---");
        logger.info("Players Alive: " + game.getPlayerCount());
        logger.info("Teams Alive: " + game.getAliveTeamCount());
        logger.info("Border Size: " + game.getWorldBorderSize());
        logger.info(
            "Phase Time Remaining: " + game.getPhaseTimeRemaining() + "s"
        );

        // Check phase type and handle accordingly
        GamePhaseChangeEvent.PhaseChangeType changeType = event.getChangeType();
        switch (changeType) {
            case PHASE_START:
                logger.info("A new phase has started!");
                // Announce phase to players
                GamePhase phase = event.getNewPhase();
                game.broadcast(
                    "&e&l[Phase] &7" + phase.getDisplayName() + " &7has begun!"
                );
                if (phase.hasBorderShrinkage()) {
                    game.broadcast(
                        "&c&l[Warning] &7Border will shrink to &c" +
                            (int) phase.getBorderTargetSize() +
                            " &7blocks!"
                    );
                }
                break;
            case PHASE_PAUSED:
                logger.info("Phase system has been paused!");
                break;
            case PHASE_RESUMED:
                logger.info("Phase system has been resumed!");
                break;
            case PHASE_STOPPED:
                logger.info("Phase system has been stopped!");
                break;
        }

        logger.info("========================================");
    }

    /**
     * Helper method to log phase details.
     */
    private void logPhaseDetails(GamePhase phase) {
        logger.info("  Name: " + phase.getName());
        logger.info("  Display Name: " + phase.getDisplayName());
        logger.info("  Duration: " + phase.getDuration() + "s");
        logger.info("  Has Border Config: " + phase.hasBorderConfig());
        logger.info("  Has Border Shrinkage: " + phase.hasBorderShrinkage());

        if (phase.hasBorderShrinkage()) {
            logger.info("  Border Target Size: " + phase.getBorderTargetSize());
            logger.info(
                "  Border Shrink Duration: " +
                    phase.getBorderShrinkDuration() +
                    "s"
            );
            logger.info(
                "  Border Damage/Block: " + phase.getBorderDamagePerBlock()
            );
        }

        logger.info("  Has Warning: " + phase.hasWarning());
        logger.info("  Warning Time: " + phase.getWarningTime() + "s");
        logger.info("  Has OnStart Actions: " + phase.hasOnStartActions());
        logger.info("  OnStart Action Count: " + phase.getOnStartActionCount());
        logger.info("  Starts Deathmatch: " + phase.startsDeathmatch());

        phase
            .getTogglePvP()
            .ifPresent(pvp -> logger.info("  Toggles PvP: " + pvp));

        logger.info("  Time Remaining: " + phase.getTimeRemaining() + "s");
        logger.info("  Time Elapsed: " + phase.getTimeElapsed() + "s");
    }
}
