package com.wtputils.command;

import com.wtputils.WTPUtils;
import com.wtputils.config.ConfigManager;
import com.wtputils.gui.RtpGuiHolder;
import com.wtputils.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class RtpCommand implements CommandExecutor, TabCompleter {

    private final WTPUtils plugin;

    public RtpCommand(WTPUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigManager config = plugin.getConfigManager();

        if (!(sender instanceof Player player)) {
            MessageUtil.send(sender, config.getPrefixedMessage("player-only"));
            return true;
        }

        if (!player.hasPermission("wtputils.rtp")) {
            MessageUtil.send(player, config.getPrefixedMessage("no-permission"));
            return true;
        }

        if (args.length >= 1) {
            plugin.getRtpManager().attemptRtp(player, args[0]);
            return true;
        }

        Inventory gui = RtpGuiHolder.build(plugin, player);
        player.openInventory(gui);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) return new ArrayList<>();
        List<String> options = new ArrayList<>();
        String partial = args[0].toLowerCase();
        for (String worldName : plugin.getConfigManager().getDimensions().keySet()) {
            if (worldName.toLowerCase().startsWith(partial)) {
                options.add(worldName);
            }
        }
        return options;
    }
}
