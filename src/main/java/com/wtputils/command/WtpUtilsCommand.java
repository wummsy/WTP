package com.wtputils.command;

import com.wtputils.WTPUtils;
import com.wtputils.config.ConfigManager;
import com.wtputils.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WtpUtilsCommand implements CommandExecutor {

    private final WTPUtils plugin;

    public WtpUtilsCommand(WTPUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigManager config = plugin.getConfigManager();

        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("wtputils.reload")) {
                MessageUtil.send(sender, config.getPrefixedMessage("no-permission"));
                return true;
            }
            config.load();
            MessageUtil.send(sender, config.getPrefixedMessage("reload-success"));
            return true;
        }

        MessageUtil.send(sender, "&bWTP Utils &7v" + plugin.getDescription().getVersion());
        MessageUtil.send(sender, "&7/wtputils reload &f- Reload the configuration");
        return true;
    }
}
