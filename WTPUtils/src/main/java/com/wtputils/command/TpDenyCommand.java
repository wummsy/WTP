package com.wtputils.command;

import com.wtputils.WTPUtils;
import com.wtputils.config.ConfigManager;
import com.wtputils.model.TeleportRequest;
import com.wtputils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDenyCommand implements CommandExecutor {

    private final WTPUtils plugin;

    public TpDenyCommand(WTPUtils plugin) {
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

        plugin.getTeleportRequestManager().removeRequest(request);

        Player requester = Bukkit.getPlayer(request.getSenderId());
        MessageUtil.send(accepter, MessageUtil.placeholder(config.getPrefixedMessage("tpa-denied-target"), "%player%", requester != null ? requester.getName() : "The player"));
        if (requester != null) {
            MessageUtil.send(requester, MessageUtil.placeholder(config.getPrefixedMessage("tpa-denied-sender"), "%target%", accepter.getName()));
        }

        return true;
    }
}
