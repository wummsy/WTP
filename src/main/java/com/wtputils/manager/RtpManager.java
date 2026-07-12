package com.wtputils.manager;

import com.wtputils.WTPUtils;
import com.wtputils.config.ConfigManager;
import com.wtputils.config.DimensionConfig;
import com.wtputils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Finds safe random locations within a configured radius/center for a given world
 * and teleports players there, respecting the configured warmup.
 */
public class RtpManager {

    private final WTPUtils plugin;
    private final ConfigManager config;
    private final CooldownManager cooldownManager;
    private final WarmupManager warmupManager;
    private final Random random = new Random();

    public RtpManager(WTPUtils plugin, CooldownManager cooldownManager, WarmupManager warmupManager) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.cooldownManager = cooldownManager;
        this.warmupManager = warmupManager;
    }

    public void attemptRtp(Player player, String worldName) {
        DimensionConfig cfg = config.getDimension(worldName);
        if (cfg == null) {
            MessageUtil.send(player, MessageUtil.placeholder(config.getPrefixedMessage("rtp-unknown-world"), "%world%", worldName));
            return;
        }
        if (!cfg.isEnabled()) {
            MessageUtil.send(player, config.getPrefixedMessage("rtp-dimension-disabled"));
            return;
        }
        if (cfg.getPermission() != null && !cfg.getPermission().isEmpty()
                && !player.hasPermission(cfg.getPermission()) && !player.hasPermission("wtputils.rtp.*")) {
            MessageUtil.send(player, config.getPrefixedMessage("rtp-dimension-no-permission"));
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            MessageUtil.send(player, MessageUtil.placeholder(config.getPrefixedMessage("rtp-unknown-world"), "%world%", worldName));
            return;
        }

        UUID id = player.getUniqueId();
        if (!player.hasPermission("wtputils.bypass.cooldown")) {
            long remaining = cooldownManager.getRemainingSeconds(id);
            if (remaining > 0) {
                MessageUtil.send(player, MessageUtil.placeholder(config.getPrefixedMessage("rtp-cooldown"), "%seconds%", String.valueOf(remaining)));
                return;
            }
        }

        MessageUtil.send(player, config.getPrefixedMessage("rtp-searching"));

        int maxAttempts = config.getRtpMaxAttempts();
        Optional<Location> safeLocation = findSafeLocation(world, cfg, maxAttempts);

        if (safeLocation.isEmpty()) {
            MessageUtil.send(player, MessageUtil.placeholder(config.getPrefixedMessage("rtp-failed"), "%attempts%", String.valueOf(maxAttempts)));
            return;
        }

        Location destination = safeLocation.get();

        if (!player.hasPermission("wtputils.bypass.cooldown")) {
            cooldownManager.applyCooldown(id, config.getRtpCooldownSeconds());
        }

        int warmupSeconds = config.getRtpWarmupSeconds();
        Runnable teleportAction = () -> {
            player.teleport(destination);
            String msg = config.getPrefixedMessage("rtp-success");
            msg = MessageUtil.placeholder(msg, "%x%", String.valueOf(destination.getBlockX()));
            msg = MessageUtil.placeholder(msg, "%y%", String.valueOf(destination.getBlockY()));
            msg = MessageUtil.placeholder(msg, "%z%", String.valueOf(destination.getBlockZ()));
            msg = MessageUtil.placeholder(msg, "%world%", cfg.getDisplayName());
            MessageUtil.send(player, msg);
        };

        if (warmupSeconds > 0) {
            MessageUtil.send(player, MessageUtil.placeholder(config.getPrefixedMessage("warmup-start"), "%seconds%", String.valueOf(warmupSeconds)));
            warmupManager.start(player, warmupSeconds, config.isRtpCancelOnMove(), config.isRtpCancelOnDamage(), teleportAction);
        } else {
            teleportAction.run();
        }
    }

    private Optional<Location> findSafeLocation(World world, DimensionConfig cfg, int maxAttempts) {
        List<Material> unsafe = config.getUnsafeMaterials();
        World.Environment env = world.getEnvironment();

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = cfg.getMinRadius() + random.nextDouble() * Math.max(1, (cfg.getMaxRadius() - cfg.getMinRadius()));
            int x = (int) Math.round(cfg.getCenterX() + dist * Math.cos(angle));
            int z = (int) Math.round(cfg.getCenterZ() + dist * Math.sin(angle));

            if (!world.getWorldBorder().isInside(new Location(world, x, 64, z))) {
                continue;
            }

            int scanTop = env == World.Environment.NETHER ? Math.min(122, world.getMaxHeight() - 2) : world.getMaxHeight() - 2;
            int scanBottom = world.getMinHeight() + 1;

            for (int y = scanTop; y > scanBottom; y--) {
                Block ground = world.getBlockAt(x, y, z);
                Block feet = world.getBlockAt(x, y + 1, z);
                Block head = world.getBlockAt(x, y + 2, z);

                if (isSafeGround(ground, feet, head, unsafe)) {
                    Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
                    return Optional.of(loc);
                }
            }
        }

        return Optional.empty();
    }

    private boolean isSafeGround(Block ground, Block feet, Block head, List<Material> unsafe) {
        Material groundType = ground.getType();
        Material feetType = feet.getType();
        Material headType = head.getType();

        if (!ground.getType().isSolid()) return false;
        if (unsafe.contains(groundType) || unsafe.contains(feetType) || unsafe.contains(headType)) return false;
        if (!isPassable(feetType) || !isPassable(headType)) return false;
        if (groundType == Material.LAVA || groundType == Material.WATER) return false;
        if (groundType.name().contains("LEAVES")) return false;

        return true;
    }

    private boolean isPassable(Material material) {
        return !material.isSolid() && material != Material.LAVA && material != Material.WATER;
    }
}
