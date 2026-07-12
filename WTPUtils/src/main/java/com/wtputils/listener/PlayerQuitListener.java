package com.wtputils.listener;

import com.wtputils.WTPUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final WTPUtils plugin;

    public PlayerQuitListener(WTPUtils plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getTeleportRequestManager().clearFor(event.getPlayer().getUniqueId());
        plugin.getWarmupManager().cancel(event.getPlayer().getUniqueId(), null);
    }
}
