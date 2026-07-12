package com.wtputils.command;

import com.wtputils.WTPUtils;
import com.wtputils.config.ConfigManager;
import com.wtputils.manager.CooldownManager;
import com.wtputils.manager.TeleportRequestManager;
import com.wtputils.model.RequestType;
import com.wtputils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Shared logic between /tpa and /tpahere so both commands stay tiny and consistent.
 */
final class TpaRequestHelper {

    private TpaRequestHelper() {
    }

    static void send(WTPUtils plugin, CommandSender sender, String[] args, RequestType type) {
        ConfigManager config = plugin.getConfigManager();

        if (!(sender instanceof Player player)) {
            MessageUtil.send(sender, config.getPrefixedMessage("player-only"));
            return;
        }

        if (!player.hasPermission("wtputils.tpa")) {
            MessageUtil.send(player, config.getPrefixedMessage("no-permission"));
            return;
        }

        if (args.length < 1) {
            MessageUtil.send(player, "&cUsage: /" + (type == RequestType.TPA ? "tpa" : "tpahere") + " <player>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            MessageUtil.send(player, MessageUtil.placeholder(config.getPrefixedMessage("player-not-found"), "%player%", args[0]));
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            MessageUtil.send(player, config.getPrefixedMessage("cannot-target-self"));
            return;
        }

        TeleportRequestManager requestManager = plugin.getTeleportRequestManager();
        CooldownManager cooldownManager = plugin.getTpaCooldownManager();

        if (!player.hasPermission("wtputils.bypass.cooldown")) {
            long remaining = cooldownManager.getRemainingSeconds(player.getUniqueId());
            if (remaining > 0) {
                MessageUtil.send(player, MessageUtil.placeholder(config.getPrefixedMessage("tpa-cooldown"), "%seconds%", String.valueOf(remaining)));
                return;
            }
        }

        if (requestManager.hasOutgoingRequest(player.getUniqueId())) {
            MessageUtil.send(player, MessageUtil.placeholder(config.getPrefixedMessage("tpa-no-pending-target"), "%target%", target.getName()));
            return;
        }

        if (requestManager.hasIncomingRequest(target.getUniqueId())) {
            MessageUtil.send(player, config.getPrefixedMessage("tpa-already-incoming"));
            return;
        }

        requestManager.createRequest(player, target, type);
        if (!player.hasPermission("wtputils.bypass.cooldown")) {
            cooldownManager.applyCooldown(player.getUniqueId(), config.getTpaCooldownSeconds());
        }

        int timeout = config.getTpaTimeoutSeconds();

        MessageUtil.send(player, MessageUtil.placeholder(
                MessageUtil.placeholder(config.getPrefixedMessage("tpa-sent"), "%target%", target.getName()),
                "%seconds%", String.valueOf(timeout)));

        String receivedKey = type == RequestType.TPA ? "tpa-received" : "tpahere-received";
        MessageUtil.send(target, MessageUtil.placeholder(
                MessageUtil.placeholder(config.getPrefixedMessage(receivedKey), "%player%", player.getName()),
                "%seconds%", String.valueOf(timeout)));
    }
}
