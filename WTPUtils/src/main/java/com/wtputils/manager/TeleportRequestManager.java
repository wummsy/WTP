package com.wtputils.manager;

import com.wtputils.WTPUtils;
import com.wtputils.config.ConfigManager;
import com.wtputils.model.RequestType;
import com.wtputils.model.TeleportRequest;
import com.wtputils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks pending TPA / TPAHERE requests. A player may only have ONE pending
 * outgoing request and can only be the target of ONE incoming request at a time.
 */
public class TeleportRequestManager {

    private final WTPUtils plugin;
    private final ConfigManager config;

    // target uuid -> request
    private final Map<UUID, TeleportRequest> requestsByTarget = new HashMap<>();
    // sender uuid -> target uuid (for lookup/cancel by sender)
    private final Map<UUID, UUID> senderToTarget = new HashMap<>();

    public TeleportRequestManager(WTPUtils plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    public boolean hasOutgoingRequest(UUID senderId) {
        return senderToTarget.containsKey(senderId);
    }

    public boolean hasIncomingRequest(UUID targetId) {
        return requestsByTarget.containsKey(targetId);
    }

    public TeleportRequest getIncomingRequest(UUID targetId) {
        return requestsByTarget.get(targetId);
    }

    public void createRequest(Player sender, Player target, RequestType type) {
        TeleportRequest request = new TeleportRequest(sender.getUniqueId(), target.getUniqueId(), type);
        requestsByTarget.put(target.getUniqueId(), request);
        senderToTarget.put(sender.getUniqueId(), target.getUniqueId());

        int timeout = config.getTpaTimeoutSeconds();
        request.setExpiryTask(Bukkit.getScheduler().runTaskLater(plugin, () -> expireRequest(target.getUniqueId()), timeout * 20L));
    }

    private void expireRequest(UUID targetId) {
        TeleportRequest request = requestsByTarget.get(targetId);
        if (request == null) return;

        removeRequest(request);

        Player target = Bukkit.getPlayer(targetId);
        Player sender = Bukkit.getPlayer(request.getSenderId());

        if (target != null) {
            MessageUtil.send(target, MessageUtil.placeholder(config.getPrefixedMessage("tpa-expired-target"),
                    "%player%", sender != null ? sender.getName() : "The player"));
        }
        if (sender != null) {
            MessageUtil.send(sender, MessageUtil.placeholder(config.getPrefixedMessage("tpa-expired-sender"),
                    "%target%", target != null ? target.getName() : "The player"));
        }
    }

    public void removeRequest(TeleportRequest request) {
        request.cancelExpiryTask();
        requestsByTarget.remove(request.getTargetId());
        senderToTarget.remove(request.getSenderId());
    }

    public TeleportRequest cancelBySender(UUID senderId) {
        UUID targetId = senderToTarget.get(senderId);
        if (targetId == null) return null;
        TeleportRequest request = requestsByTarget.get(targetId);
        if (request == null) return null;
        removeRequest(request);
        return request;
    }

    /** Cleans up any pending requests involving a player who disconnected. */
    public void clearFor(UUID playerId) {
        TeleportRequest incoming = requestsByTarget.get(playerId);
        if (incoming != null) {
            removeRequest(incoming);
        }
        UUID targetId = senderToTarget.get(playerId);
        if (targetId != null) {
            TeleportRequest outgoing = requestsByTarget.get(targetId);
            if (outgoing != null) {
                removeRequest(outgoing);
            }
        }
    }
}
