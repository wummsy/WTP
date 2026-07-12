package com.wtputils.listener;

import com.wtputils.WTPUtils;
import com.wtputils.config.ConfigManager;
import com.wtputils.manager.WarmupManager;
import com.wtputils.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class WarmupListener implements Listener {

    private final WTPUtils plugin;

    public WarmupListener(WTPUtils plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        WarmupManager warmupManager = plugin.getWarmupManager();
        Player player = event.getPlayer();
        if (!warmupManager.isWarmingUp(player.getUniqueId())) return;

        WarmupManager.Warmup warmup = warmupManager.get(player.getUniqueId());
        if (warmup == null || !warmup.cancelOnMove) return;

        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return; // Only head rotation, ignore.
        }

        ConfigManager config = plugin.getConfigManager();
        warmupManager.cancel(player.getUniqueId(), MessageUtil.color(config.getPrefixedMessage("warmup-cancelled-move")));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        WarmupManager warmupManager = plugin.getWarmupManager();
        if (!warmupManager.isWarmingUp(player.getUniqueId())) return;

        WarmupManager.Warmup warmup = warmupManager.get(player.getUniqueId());
        if (warmup == null || !warmup.cancelOnDamage) return;

        ConfigManager config = plugin.getConfigManager();
        warmupManager.cancel(player.getUniqueId(), MessageUtil.color(config.getPrefixedMessage("warmup-cancelled-damage")));
    }
}
