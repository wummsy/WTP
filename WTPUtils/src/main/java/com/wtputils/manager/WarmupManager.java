package com.wtputils.manager;

import com.wtputils.WTPUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles "don't move" teleport countdowns for both TPA and RTP.
 */
public class WarmupManager {

    private final WTPUtils plugin;
    private final Map<UUID, Warmup> activeWarmups = new HashMap<>();

    public WarmupManager(WTPUtils plugin) {
        this.plugin = plugin;
    }

    public boolean isWarmingUp(UUID playerId) {
        return activeWarmups.containsKey(playerId);
    }

    public void start(Player player, int seconds, boolean cancelOnMove, boolean cancelOnDamage, Runnable onComplete) {
        UUID id = player.getUniqueId();
        cancel(id, null);

        if (seconds <= 0) {
            onComplete.run();
            return;
        }

        Location startLoc = player.getLocation();
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            activeWarmups.remove(id);
            onComplete.run();
        }, seconds * 20L);

        activeWarmups.put(id, new Warmup(startLoc, cancelOnMove, cancelOnDamage, task));
    }

    /** Cancels a warmup, optionally sending the player a reason message. */
    public void cancel(UUID playerId, String reasonMessage) {
        Warmup warmup = activeWarmups.remove(playerId);
        if (warmup != null && warmup.task != null && !warmup.task.isCancelled()) {
            warmup.task.cancel();
        }
        if (warmup != null && reasonMessage != null) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage(reasonMessage);
            }
        }
    }

    public Warmup get(UUID playerId) {
        return activeWarmups.get(playerId);
    }

    public static class Warmup {
        public final Location startLocation;
        public final boolean cancelOnMove;
        public final boolean cancelOnDamage;
        public final BukkitTask task;

        public Warmup(Location startLocation, boolean cancelOnMove, boolean cancelOnDamage, BukkitTask task) {
            this.startLocation = startLocation;
            this.cancelOnMove = cancelOnMove;
            this.cancelOnDamage = cancelOnDamage;
            this.task = task;
        }
    }
}
