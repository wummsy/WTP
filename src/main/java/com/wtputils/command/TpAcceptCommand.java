package com.wtputils.command;

import com.wtputils.WTPUtils;
import com.wtputils.config.ConfigManager;
import com.wtputils.model.RequestType;
import com.wtputils.model.TeleportRequest;
import com.wtputils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAcceptCommand implements CommandExecutor {

    private final WTPUtils plugin;

    public TpAcceptCommand(WTPUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigManager config = plugin.getConfigManager();

        if (!(sender instanceof Player accepter)) {
            MessageUtil.send(sender, config.getPrefixedMessage("player-only"));
            return true;
        }

        TeleportRequest request = plugin.getTeleportRequestManager().getIncomingRequest(accepter.getUniqueId());
        if (request == null) {
            MessageUtil.send(accepter, config.getPrefixedMessage("tpa-none-pending"));
            return true;
        }

        Player requester = Bukkit.getPlayer(request.getSenderId());
        plugin.getTeleportRequestManager().removeRequest(request);

        if (requester == null || !requester.isOnline()) {
            MessageUtil.send(accepter, MessageUtil.placeholder(config.getPrefixedMessage("player-not-found"), "%player%", "That player"));
            return true;
        }

        // Determine who physically moves.
        Player traveler = request.getType() == RequestType.TPA ? requester : accepter;
        Player destinationHolder = request.getType() == RequestType.TPA ? accepter : requester;
        Location destination = destinationHolder.getLocation().clone();

        MessageUtil.send(requester, MessageUtil.placeholder(config.getPrefixedMessage("tpa-accepted-sender"), "%target%", accepter.getName()));
        MessageUtil.send(accepter, MessageUtil.placeholder(config.getPrefixedMessage("tpa-accepted-target"), "%player%", requester.getName()));

        int warmupSeconds = config.getTpaWarmupSeconds();
        Runnable teleportAction = () -> {
            traveler.teleport(destination);
            MessageUtil.send(traveler, config.getPrefixedMessage("warmup-complete"));
        };

        if (warmupSeconds > 0) {
            MessageUtil.send(traveler, MessageUtil.placeholder(config.getPrefixedMessage("warmup-start"), "%seconds%", String.valueOf(warmupSeconds)));
            plugin.getWarmupManager().start(traveler, warmupSeconds, config.isTpaCancelOnMove(), config.isTpaCancelOnDamage(), teleportAction);
        } else {
            teleportAction.run();
        }

        return true;
    }
}
