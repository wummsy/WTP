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

public class TpCancelCommand implements CommandExecutor {

    private final WTPUtils plugin;

    public TpCancelCommand(WTPUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigManager config = plugin.getConfigManager();

        if (!(sender instanceof Player player)) {
            MessageUtil.send(sender, config.getPrefixedMessage("player-only"));
            return true;
        }

        TeleportRequest request = plugin.getTeleportRequestManager().cancelBySender(player.getUniqueId());
        if (request == null) {
            MessageUtil.send(player, config.getPrefixedMessage("tpa-none-outgoing"));
            return true;
        }

        Player target = Bukkit.getPlayer(request.getTargetId());
        MessageUtil.send(player, MessageUtil.placeholder(config.getPrefixedMessage("tpa-cancelled-sender"), "%target%", target != null ? target.getName() : "the player"));
        if (target != null) {
            MessageUtil.send(target, MessageUtil.placeholder(config.getPrefixedMessage("tpa-cancelled-target"), "%player%", player.getName()));
        }

        return true;
    }
}
