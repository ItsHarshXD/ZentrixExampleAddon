package dev.itsharshxd.addon.zentrix.example.listeners;

import dev.itsharshxd.zentrix.api.events.currency.CurrencyChangeEvent;
import dev.itsharshxd.addon.zentrix.example.ExampleAddon;
import java.util.Optional;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Demonstrates listening to all currency-related Zentrix events.
 * <p>
 * Events covered:
 * <ul>
 *   <li>{@link CurrencyChangeEvent} - Fired when a player's currency balance changes (cancellable)</li>
 * </ul>
 * </p>
 */
public class CurrencyEventListener implements Listener {

    private final ExampleAddon addon;
    private final Logger logger;

    public CurrencyEventListener(ExampleAddon addon) {
        this.addon = addon;
        this.logger = addon.getLogger();
    }

    // ==========================================
    // CurrencyChangeEvent (Cancellable)
    // ==========================================

    /**
     * Called when a player's currency balance is about to change.
     * <p>
     * This event is CANCELLABLE - you can prevent balance changes.
     * You can also MODIFY the new balance before it's applied.
     * <p>
     * Use cases:
     * - Logging all currency transactions
     * - Implementing tax systems
     * - Applying bonuses/multipliers
     * - Preventing certain transactions
     * - Tracking economy flow
     * </p>
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCurrencyChange(CurrencyChangeEvent event) {
        logger.info("========== CURRENCY CHANGE EVENT ==========");

        // Basic info
        logger.info("Player: " + event.getPlayerName());
        logger.info("Player UUID: " + event.getPlayerId());
        logger.info("Is Cancelled: " + event.isCancelled());

        // Balance info
        logger.info("--- Balance Details ---");
        logger.info("Old Balance: " + event.getOldBalance());
        logger.info("New Balance: " + event.getNewBalance());
        logger.info("Change Amount: " + event.getChangeAmount());

        // Change type
        logger.info("--- Change Type ---");
        logger.info("Is Gain: " + event.isGain());
        logger.info("Is Loss: " + event.isLoss());
        logger.info("Is No Change: " + event.isNoChange());

        // Reason
        logger.info("--- Change Reason ---");
        logger.info("Reason: " + event.getReason());
        logger.info("Is Event Reward: " + event.isEventReward());
        logger.info("Is Admin Change: " + event.isAdminChange());
        logger.info("Is Addon Change: " + event.isAddonChange());

        // Source
        Optional<String> source = event.getSource();
        logger.info("Has Source: " + source.isPresent());
        source.ifPresent(s -> logger.info("Source: " + s));

        // Player access
        logger.info("--- Player Access ---");
        Optional<Player> player = event.getPlayer();
        logger.info("Is Online: " + player.isPresent());
        player.ifPresent(p -> {
            logger.info("Display Name: " + p.getDisplayName());
            logger.info("World: " + p.getWorld().getName());
        });

        // Reason switch - handle different change reasons
        CurrencyChangeEvent.ChangeReason reason = event.getReason();
        switch (reason) {
            case ADMIN:
                logger.info("Balance was changed by an administrator");
                break;
            case EVENT_REWARD:
                logger.info("Balance was changed due to a game event reward");
                break;
            case ADDON:
                logger.info("Balance was changed by a third-party addon");
                break;
            case PURCHASE:
                logger.info("Balance was changed due to a purchase");
                break;
            case PENALTY:
                logger.info("Balance was changed due to a penalty");
                break;
            case TRANSFER:
                logger.info("Balance was changed due to a transfer");
                break;
            case RESET:
                logger.info("Balance was reset to default");
                break;
            case STORAGE_LOAD:
                logger.info("Balance was loaded from storage");
                break;
            case OTHER:
                logger.info("Balance was changed for unknown reason");
                break;
        }

        logger.info("============================================");

        // ==========================================
        // Example: Modifying the balance
        // ==========================================

        // Example 1: Apply 10% tax on large gains (commented out)
        // if (event.isGain() && event.getChangeAmount() > 100) {
        //     double taxedGain = event.getChangeAmount() * 0.9; // 10% tax
        //     event.setNewBalance(event.getOldBalance() + taxedGain);
        //     event.getPlayer().ifPresent(p ->
        //         p.sendMessage("§e[Tax] §710% tax applied to large gain!"));
        //     logger.info("Applied 10% tax. New balance adjusted to: " + event.getNewBalance());
        // }

        // Example 2: Double rewards for VIP players (commented out)
        // if (event.isEventReward() && event.isGain()) {
        //     event.getPlayer().ifPresent(p -> {
        //         if (p.hasPermission("zentrix.vip.doublerewards")) {
        //             event.multiplyChange(2.0);
        //             p.sendMessage("§6[VIP] §7Your reward has been doubled!");
        //             logger.info("Doubled VIP reward. New balance: " + event.getNewBalance());
        //         }
        //     });
        // }

        // Example 3: Add bonus to rewards (commented out)
        // if (event.isEventReward() && event.isGain()) {
        //     event.addBonus(5.0); // Add 5 bonus currency
        //     logger.info("Added 5 bonus. New balance: " + event.getNewBalance());
        // }

        // Example 4: Prevent balance going below a minimum (commented out)
        // double minimumBalance = 10.0;
        // if (event.getNewBalance() < minimumBalance && event.isLoss()) {
        //     event.setNewBalance(minimumBalance);
        //     event.getPlayer().ifPresent(p ->
        //         p.sendMessage("§c[Economy] §7Your balance cannot go below " + minimumBalance + "!"));
        //     logger.info("Prevented balance from going below minimum. Set to: " + minimumBalance);
        // }

        // ==========================================
        // Example: Cancelling the event
        // ==========================================

        // Example 5: Prevent certain players from receiving rewards (commented out)
        // if (event.isEventReward()) {
        //     event.getPlayer().ifPresent(p -> {
        //         if (p.hasPermission("zentrix.blocked")) {
        //             event.setCancelled(true);
        //             p.sendMessage("§cYou are blocked from receiving currency rewards!");
        //             logger.info("Blocked currency reward for player: " + p.getName());
        //         }
        //     });
        // }

        // Example 6: Block all currency changes during maintenance mode (commented out)
        // boolean maintenanceMode = false;
        // if (maintenanceMode && reason != CurrencyChangeEvent.ChangeReason.ADMIN) {
        //     event.setCancelled(true);
        //     event.getPlayer().ifPresent(p ->
        //         p.sendMessage("§c[Maintenance] §7Currency changes are currently disabled!"));
        //     logger.info("Blocked currency change during maintenance mode");
        // }

        // ==========================================
        // Example: Logging for external systems
        // ==========================================

        // Example: Log all transactions for analytics (always enabled)
        String transactionLog = String.format(
            "[TRANSACTION] Player: %s | Old: %.2f | New: %.2f | Change: %+.2f | Reason: %s | Source: %s",
            event.getPlayerName(),
            event.getOldBalance(),
            event.getNewBalance(),
            event.getChangeAmount(),
            event.getReason().name(),
            event.getSource().orElse("N/A")
        );
        logger.info(transactionLog);

        // Example: Send message to player about balance change
        event
            .getPlayer()
            .ifPresent(p -> {
                if (event.isGain() && !event.isCancelled()) {
                    p.sendMessage(
                        "§a[+] §7You received §a" +
                            String.format("%.1f", event.getChangeAmount()) +
                            " §7currency!"
                    );
                } else if (event.isLoss() && !event.isCancelled()) {
                    p.sendMessage(
                        "§c[-] §7You lost §c" +
                            String.format(
                                "%.1f",
                                Math.abs(event.getChangeAmount())
                            ) +
                            " §7currency!"
                    );
                }
            });
    }

    /**
     * Alternative handler with HIGH priority to modify events before MONITOR handlers see them.
     * <p>
     * Use this for actually modifying events. MONITOR should only observe.
     * </p>
     */
    // @EventHandler(priority = EventPriority.HIGH)
    // public void onCurrencyChangeModify(CurrencyChangeEvent event) {
    //     // Apply modifications here before MONITOR handlers
    //     // Example: Implement VIP double rewards
    //     if (event.isEventReward() && event.isGain()) {
    //         event.getPlayer().ifPresent(p -> {
    //             if (p.hasPermission("zentrix.vip")) {
    //                 event.multiplyChange(1.5); // 50% bonus for VIPs
    //             }
    //         });
    //     }
    // }
}
