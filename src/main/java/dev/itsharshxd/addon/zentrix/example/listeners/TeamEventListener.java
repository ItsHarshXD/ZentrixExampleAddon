package dev.itsharshxd.addon.zentrix.example.listeners;

import dev.itsharshxd.zentrix.api.events.team.TeamEliminatedEvent;
import dev.itsharshxd.zentrix.api.game.ZentrixGame;
import dev.itsharshxd.zentrix.api.player.ZentrixPlayer;
import dev.itsharshxd.zentrix.api.team.ZentrixTeam;
import dev.itsharshxd.addon.zentrix.example.ExampleAddon;
import java.util.Collection;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Demonstrates listening to all team-related Zentrix events.
 * <p>
 * Events covered:
 * <ul>
 *   <li>{@link TeamEliminatedEvent} - Fired when a team is eliminated from the game</li>
 * </ul>
 * </p>
 */
public class TeamEventListener implements Listener {

    private final ExampleAddon addon;
    private final Logger logger;

    public TeamEventListener(ExampleAddon addon) {
        this.addon = addon;
        this.logger = addon.getLogger();
    }

    // ==========================================
    // TeamEliminatedEvent
    // ==========================================

    /**
     * Called when a team is eliminated from the game.
     * <p>
     * A team is eliminated when all its members have been killed or left.
     * This event provides:
     * - The eliminated team details
     * - The last member to be eliminated
     * - The killer (if killed by another player)
     * - Placement information
     * </p>
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeamEliminated(TeamEliminatedEvent event) {
        ZentrixGame game = event.getGame();
        ZentrixTeam team = event.getTeam();

        logger.info("========== TEAM ELIMINATED EVENT ==========");
        logger.info("Game ID: " + event.getGameId());
        logger.info("Arena: " + event.getArenaName());
        logger.info("Game Type: " + event.getGameTypeName());

        // Eliminated team info
        logger.info("--- Eliminated Team ---");
        logger.info("Team ID: " + team.getTeamId());
        logger.info("Team Number: " + team.getTeamNumber());
        logger.info("Display Name: " + team.getDisplayName());
        logger.info("Color: " + team.getColor());
        logger.info("Symbol: " + team.getSymbol());
        logger.info("Chat Format: " + team.getChatFormat());
        logger.info("Nametag Format: " + team.getNametagFormat());
        logger.info("Friendly Fire Enabled: " + team.isFriendlyFireEnabled());
        logger.info("Total Members: " + team.getMemberCount());
        logger.info("Alive Members: " + team.getAliveMemberCount());
        logger.info("Is Eliminated: " + team.isEliminated());
        logger.info("Is Empty: " + team.isEmpty());

        // List team member UUIDs
        Collection<java.util.UUID> memberIds = team.getMemberIds();
        logger.info("--- Team Members (UUIDs) ---");
        for (java.util.UUID memberId : memberIds) {
            logger.info("  - " + memberId);
        }

        // Last eliminated member
        logger.info("--- Last Eliminated Member ---");
        ZentrixPlayer lastMember = event.getLastMemberEliminated();
        logger.info("Name: " + lastMember.getName());
        logger.info("UUID: " + lastMember.getUniqueId());
        logger.info("Final Kills: " + lastMember.getGameKills());
        logger.info(
            "Highest Kill Streak: " + lastMember.getHighestKillStreak()
        );
        logger.info("Damage Dealt: " + lastMember.getDamageDealt());
        logger.info("Damage Taken: " + lastMember.getDamageTaken());
        logger.info(
            "Survival Time: " + lastMember.getSurvivalTimeSeconds() + "s"
        );
        logger.info("Is Online: " + lastMember.isOnline());
        logger.info("Is Alive: " + lastMember.isAlive());
        logger.info("Is Spectating: " + lastMember.isSpectating());

        lastMember
            .getSelectedClass()
            .ifPresent(playerClass -> {
                logger.info("Selected Class: " + playerClass.getDisplayName());
            });

        // Killer info (if applicable)
        logger.info("--- Final Killer ---");
        logger.info("Has Final Killer: " + event.hasFinalKiller());
        event
            .getFinalKiller()
            .ifPresentOrElse(
                killer -> {
                    logger.info("Killer Name: " + killer.getName());
                    logger.info("Killer UUID: " + killer.getUniqueId());
                    logger.info("Killer Kills: " + killer.getGameKills());
                    logger.info(
                        "Killer Kill Streak: " + killer.getKillStreak()
                    );
                    killer
                        .getTeam()
                        .ifPresent(killerTeam -> {
                            logger.info(
                                "Killer Team: " + killerTeam.getDisplayName()
                            );
                            logger.info(
                                "Killer Team Alive: " +
                                    killerTeam.getAliveMemberCount() +
                                    " members"
                            );
                        });
                    killer
                        .getSelectedClass()
                        .ifPresent(playerClass -> {
                            logger.info(
                                "Killer Class: " + playerClass.getDisplayName()
                            );
                        });
                },
                () ->
                    logger.info(
                        "Team was eliminated without a killer (environmental death or disconnect)"
                    )
            );

        // Placement info
        logger.info("--- Placement ---");
        logger.info("Final Placement: " + event.getPlacementOrdinal());
        logger.info("Remaining Teams: " + event.getRemainingTeamCount());
        logger.info("Total Team Kills: " + event.getTotalTeamKills());
        logger.info("Was Runner Up: " + event.wasRunnerUp());

        // Top killer on the team
        event
            .getTopKiller()
            .ifPresent(topKiller -> {
                logger.info(
                    "Team's Top Killer: " +
                        topKiller.getName() +
                        " (" +
                        topKiller.getGameKills() +
                        " kills)"
                );
            });

        // Check win condition
        logger.info("--- Win Condition ---");
        logger.info("Triggers Win: " + event.triggersWin());
        logger.info(
            "Is Final Team Remaining: " + (event.getRemainingTeamCount() == 1)
        );

        // If this triggers a win, show winning team
        event
            .getWinningTeam()
            .ifPresent(winningTeam -> {
                logger.info("Winning Team: " + winningTeam.getDisplayName());
            });

        // Game status
        logger.info("--- Game Status ---");
        logger.info("Game State: " + game.getState());
        logger.info("Players Alive: " + game.getPlayerCount());
        logger.info("Teams Alive: " + game.getAliveTeamCount());
        logger.info("Spectators: " + game.getSpectatorCount());

        // List remaining alive teams
        Collection<ZentrixTeam> aliveTeams = game.getAliveTeams();
        logger.info(
            "--- Remaining Alive Teams (" + aliveTeams.size() + ") ---"
        );
        for (ZentrixTeam aliveTeam : aliveTeams) {
            logger.info(
                "  " +
                    aliveTeam.getDisplayName() +
                    " | Members: " +
                    aliveTeam.getAliveMemberCount() +
                    "/" +
                    aliveTeam.getMemberCount()
            );
        }

        // Current phase info
        game
            .getCurrentPhase()
            .ifPresent(phase -> {
                logger.info("--- Current Phase ---");
                logger.info("Phase: " + phase.getDisplayName());
                logger.info(
                    "Time Remaining: " + phase.getTimeRemaining() + "s"
                );
            });

        logger.info("============================================");

        // Example: Broadcast team elimination with placement
        game.broadcast(
            "&c&l☠ &e" +
                team.getDisplayName() +
                " &7finished in &c" +
                event.getPlacementOrdinal() +
                " &7place! " +
                "&8(" +
                event.getRemainingTeamCount() +
                " teams remain)"
        );

        // Example: Announce if this triggers a win
        if (event.triggersWin()) {
            event
                .getWinningTeam()
                .ifPresent(winningTeam -> {
                    game.broadcast(
                        "&6&l★ &e" +
                            winningTeam.getDisplayName() +
                            " &7is the last team standing!"
                    );
                });
        }
    }
}
