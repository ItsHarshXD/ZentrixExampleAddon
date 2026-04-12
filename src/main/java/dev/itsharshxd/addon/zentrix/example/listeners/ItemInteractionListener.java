package dev.itsharshxd.addon.zentrix.example.listeners;

import dev.itsharshxd.zentrix.api.ZentrixAPI;
import dev.itsharshxd.zentrix.api.game.ZentrixGame;
import dev.itsharshxd.zentrix.api.item.ItemService;
import dev.itsharshxd.zentrix.api.player.ZentrixPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Listener demonstrating custom item behavior using the ItemService.
 * <p>
 * This listener shows how to:
 * <ul>
 *   <li>Identify custom items using {@link ItemService#identifyItem(ItemStack)}</li>
 *   <li>Implement custom behavior based on item ID</li>
 *   <li>Use cooldowns for item abilities</li>
 *   <li>Integrate with other Zentrix services (GameService, PlayerService)</li>
 * </ul>
 * </p>
 *
 * @author ItsHarshXD
 * @version 1.0.0
 */
public class ItemInteractionListener implements Listener {

    private final JavaPlugin plugin;
    private final Logger logger;

    // Cooldown tracking (player UUID -> last use time in millis)
    private final Map<UUID, Long> trackingCompassCooldowns = new HashMap<>();
    private final Map<UUID, Long> healAppleCooldowns = new HashMap<>();

    // Cooldown durations in milliseconds
    private static final long TRACKING_COMPASS_COOLDOWN = 10_000; // 10 seconds
    private static final long HEAL_APPLE_COOLDOWN = 30_000; // 30 seconds

    /**
     * Creates a new ItemInteractionListener.
     *
     * @param plugin The plugin instance
     */
    public ItemInteractionListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    /**
     * Handles player interactions with custom items.
     * <p>
     * This demonstrates how to use {@link ItemService#identifyItem(ItemStack)}
     * to detect when a player uses a custom registered item.
     * </p>
     *
     * @param event The player interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only handle right-click actions
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
            event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        // Use ItemService to identify the item
        ItemService itemService = ZentrixAPI.get().getItemService();
        Optional<String> itemIdOpt = itemService.identifyItem(item);

        if (itemIdOpt.isEmpty()) {
            return; // Not a registered custom item
        }

        String itemId = itemIdOpt.get();
        Player player = event.getPlayer();

        // Handle based on item ID
        switch (itemId) {
            case "example-tracking-compass" -> handleTrackingCompass(player, event);
            case "example-heal-apple" -> handleHealApple(player, event);
            // Speed boots don't need interaction handling - they're passive
        }
    }

    /**
     * Handles the Tracking Compass item.
     * <p>
     * When right-clicked, this compass points to the nearest enemy player
     * in the same game. Demonstrates integration with PlayerService.
     * </p>
     *
     * @param player The player using the compass
     * @param event  The interact event
     */
    private void handleTrackingCompass(Player player, PlayerInteractEvent event) {
        event.setCancelled(true); // Prevent normal compass behavior

        // Check cooldown
        if (isOnCooldown(player, trackingCompassCooldowns, TRACKING_COMPASS_COOLDOWN)) {
            long remaining = getRemainingCooldown(player, trackingCompassCooldowns, TRACKING_COMPASS_COOLDOWN);
            player.sendMessage("§c§lCOOLDOWN! §7Tracking Compass ready in §f" +
                (remaining / 1000) + "s");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return;
        }

        // Check if player is in a game
        var gameService = ZentrixAPI.get().getGameService();
        Optional<ZentrixGame> gameOpt = gameService.getPlayerGame(player);

        if (gameOpt.isEmpty()) {
            player.sendMessage("§c§lERROR! §7You must be in a game to use this item!");
            return;
        }

        ZentrixGame game = gameOpt.get();
        var playerService = ZentrixAPI.get().getPlayerService();

        // Find nearest enemy (player not on the same team)
        Player nearestEnemy = null;
        double nearestDistance = Double.MAX_VALUE;

        var teamService = ZentrixAPI.get().getTeamService();
        var playerTeamOpt = teamService.getPlayerTeam(player);
        UUID playerTeamId = playerTeamOpt.map(t -> UUID.fromString(t.getTeamId())).orElse(null);

        for (ZentrixPlayer zentrixPlayer : playerService.getPlayersInGame(game)) {
            Player target = Bukkit.getPlayer(zentrixPlayer.getUniqueId());
            if (target == null || target.equals(player)) {
                continue;
            }

            // Check if on different team (or solo mode)
            var targetTeamOpt = teamService.getPlayerTeam(target);
            UUID targetTeamId = targetTeamOpt.map(t -> UUID.fromString(t.getTeamId())).orElse(null);

            // Skip teammates
            if (playerTeamId != null && playerTeamId.equals(targetTeamId)) {
                continue;
            }

            double distance = player.getLocation().distance(target.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestEnemy = target;
            }
        }

        if (nearestEnemy == null) {
            player.sendMessage("§e§lTRACKING §8» §7No enemies found nearby!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            return;
        }

        // Point compass to enemy and notify player
        Location enemyLocation = nearestEnemy.getLocation();
        player.setCompassTarget(enemyLocation);

        // Calculate direction
        String direction = getCardinalDirection(player.getLocation(), enemyLocation);

        player.sendMessage("§6§lTRACKING §8» §7Nearest enemy: §f" + nearestEnemy.getName());
        player.sendMessage("§6§lTRACKING §8» §7Distance: §f" + (int) nearestDistance + " blocks §7(" + direction + ")");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);

        // Set cooldown
        trackingCompassCooldowns.put(player.getUniqueId(), System.currentTimeMillis());

        logger.info("[ItemInteraction] " + player.getName() + " tracked " +
            nearestEnemy.getName() + " (" + (int) nearestDistance + " blocks)");
    }

    /**
     * Handles the Vampire Apple (Heal Apple) item.
     * <p>
     * When right-clicked, this apple heals the player and applies
     * a brief lifesteal effect. Demonstrates custom potion effects.
     * </p>
     *
     * @param player The player using the apple
     * @param event  The interact event
     */
    private void handleHealApple(Player player, PlayerInteractEvent event) {
        // Don't cancel - let them eat it normally, but add extra effects

        // Check cooldown for the special effect
        if (isOnCooldown(player, healAppleCooldowns, HEAL_APPLE_COOLDOWN)) {
            // Let them eat it, but no special effects
            player.sendMessage("§c§lCOOLDOWN! §7Lifesteal effect ready in §f" +
                (getRemainingCooldown(player, healAppleCooldowns, HEAL_APPLE_COOLDOWN) / 1000) + "s");
            return;
        }

        // Check if in game
        var gameService = ZentrixAPI.get().getGameService();
        if (gameService.getPlayerGame(player).isEmpty()) {
            return; // Not in game, don't apply special effects
        }

        // Apply special effects on a slight delay (after eating animation starts)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Extra healing (4 hearts = 8 health)
            double newHealth = Math.min(player.getHealth() + 8.0, player.getMaxHealth());
            player.setHealth(newHealth);

            // Apply "lifesteal" effect (regeneration + strength)
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1)); // 10s Regen II
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 0)); // 10s Strength I

            // Visual and audio feedback
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            player.sendMessage("§c§l❤ LIFESTEAL §8» §7You feel the power of the vampire apple!");

            logger.info("[ItemInteraction] " + player.getName() + " consumed Vampire Apple");
        }, 10L); // 0.5 second delay

        // Set cooldown
        healAppleCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * Checks if a player is on cooldown for an ability.
     *
     * @param player    The player
     * @param cooldowns The cooldown map
     * @param duration  The cooldown duration in milliseconds
     * @return true if still on cooldown
     */
    private boolean isOnCooldown(Player player, Map<UUID, Long> cooldowns, long duration) {
        Long lastUse = cooldowns.get(player.getUniqueId());
        if (lastUse == null) {
            return false;
        }
        return System.currentTimeMillis() - lastUse < duration;
    }

    /**
     * Gets the remaining cooldown time in milliseconds.
     *
     * @param player    The player
     * @param cooldowns The cooldown map
     * @param duration  The cooldown duration in milliseconds
     * @return Remaining time in milliseconds, or 0 if not on cooldown
     */
    private long getRemainingCooldown(Player player, Map<UUID, Long> cooldowns, long duration) {
        Long lastUse = cooldowns.get(player.getUniqueId());
        if (lastUse == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - lastUse;
        return Math.max(0, duration - elapsed);
    }

    /**
     * Gets the cardinal direction from one location to another.
     *
     * @param from The starting location
     * @param to   The target location
     * @return A cardinal direction string (N, NE, E, SE, S, SW, W, NW)
     */
    private String getCardinalDirection(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double angle = Math.toDegrees(Math.atan2(-dx, dz));

        if (angle < 0) {
            angle += 360;
        }

        if (angle >= 337.5 || angle < 22.5) return "N";
        if (angle >= 22.5 && angle < 67.5) return "NE";
        if (angle >= 67.5 && angle < 112.5) return "E";
        if (angle >= 112.5 && angle < 157.5) return "SE";
        if (angle >= 157.5 && angle < 202.5) return "S";
        if (angle >= 202.5 && angle < 247.5) return "SW";
        if (angle >= 247.5 && angle < 292.5) return "W";
        if (angle >= 292.5 && angle < 337.5) return "NW";

        return "?";
    }
}
