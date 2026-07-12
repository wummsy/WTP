package com.wtputils;

import com.wtputils.command.RtpCommand;
import com.wtputils.command.TpAcceptCommand;
import com.wtputils.command.TpCancelCommand;
import com.wtputils.command.TpDenyCommand;
import com.wtputils.command.TpaCommand;
import com.wtputils.command.TpaHereCommand;
import com.wtputils.command.WtpUtilsCommand;
import com.wtputils.config.ConfigManager;
import com.wtputils.listener.GuiListener;
import com.wtputils.listener.PlayerQuitListener;
import com.wtputils.listener.WarmupListener;
import com.wtputils.manager.CooldownManager;
import com.wtputils.manager.RtpManager;
import com.wtputils.manager.TeleportRequestManager;
import com.wtputils.manager.WarmupManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WTPUtils extends JavaPlugin {

    private ConfigManager configManager;
    private TeleportRequestManager teleportRequestManager;
    private CooldownManager tpaCooldownManager;
    private CooldownManager rtpCooldownManager;
    private WarmupManager warmupManager;
    private RtpManager rtpManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.teleportRequestManager = new TeleportRequestManager(this);
        this.tpaCooldownManager = new CooldownManager();
        this.rtpCooldownManager = new CooldownManager();
        this.warmupManager = new WarmupManager(this);
        this.rtpManager = new RtpManager(this, rtpCooldownManager, warmupManager);

        registerCommands();
        registerListeners();

        getLogger().info("WTP Utils has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("WTP Utils has been disabled.");
    }

    private void registerCommands() {
        getCommand("tpa").setExecutor(new TpaCommand(this));
        getCommand("tpahere").setExecutor(new TpaHereCommand(this));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(this));
        getCommand("tpcancel").setExecutor(new TpCancelCommand(this));

        RtpCommand rtpCommand = new RtpCommand(this);
        getCommand("rtp").setExecutor(rtpCommand);
        getCommand("rtp").setTabCompleter(rtpCommand);

        getCommand("wtputils").setExecutor(new WtpUtilsCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new WarmupListener(this), this);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TeleportRequestManager getTeleportRequestManager() {
        return teleportRequestManager;
    }

    public CooldownManager getTpaCooldownManager() {
        return tpaCooldownManager;
    }

    public CooldownManager getRtpCooldownManager() {
        return rtpCooldownManager;
    }

    public WarmupManager getWarmupManager() {
        return warmupManager;
    }

    public RtpManager getRtpManager() {
        return rtpManager;
    }
}
