package com.wtputils.command;

import com.wtputils.WTPUtils;
import com.wtputils.model.RequestType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TpaCommand implements CommandExecutor {

    private final WTPUtils plugin;

    public TpaCommand(WTPUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TpaRequestHelper.send(plugin, sender, args, RequestType.TPA);
        return true;
    }
}
