package com.wtputils.config;

import com.wtputils.WTPUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loads and exposes all values from config.yml, including the per-dimension RTP settings.
 */
public class ConfigManager {

    private final WTPUtils plugin;

    private final Map<String, DimensionConfig> dimensions = new LinkedHashMap<>();
    private final List<Material> unsafeMaterials = new ArrayList<>();

    public ConfigManager(WTPUtils plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.reloadConfig();
        dimensions.clear();
        unsafeMaterials.clear();

        ConfigurationSection dimSection = plugin.getConfig().getConfigurationSection("rtp.dimensions");
        if (dimSection != null) {
            Set<String> keys = dimSection.getKeys(false);
            for (String worldName : keys) {
                ConfigurationSection section = dimSection.getConfigurationSection(worldName);
                if (section == null) continue;

                boolean enabled = section.getBoolean("enabled", true);
                String displayName = section.getString("display-name", worldName);
                Material icon = Material.matchMaterial(section.getString("icon", "STONE"));
                if (icon == null) icon = Material.STONE;
                List<String> lore = section.getStringList("lore");
                String permission = section.getString("permission", "wtputils.rtp." + worldName);
                int centerX = section.getInt("center-x", 0);
                int centerZ = section.getInt("center-z", 0);
                int minRadius = section.getInt("min-radius", 100);
                int maxRadius = section.getInt("max-radius", 5000);

                dimensions.put(worldName, new DimensionConfig(worldName, enabled, displayName, icon,
                        lore, permission, centerX, centerZ, minRadius, maxRadius));
            }
        }

        for (String matName : plugin.getConfig().getStringList("rtp.unsafe-materials")) {
            Material mat = Material.matchMaterial(matName);
            if (mat != null) unsafeMaterials.add(mat);
        }
    }

    public Map<String, DimensionConfig> getDimensions() {
        return dimensions;
    }

    public DimensionConfig getDimension(String worldName) {
        return dimensions.get(worldName);
    }

    public List<Material> getUnsafeMaterials() {
        return unsafeMaterials;
    }

    public String getMessage(String path) {
        return plugin.getConfig().getString("messages." + path, "");
    }

    public String getPrefixedMessage(String path) {
        return getMessage("prefix") + getMessage(path);
    }

    public int getTpaTimeoutSeconds() {
        return plugin.getConfig().getInt("tpa.request-timeout-seconds", 60);
    }

    public int getTpaCooldownSeconds() {
        return plugin.getConfig().getInt("tpa.cooldown-seconds", 5);
    }

    public int getTpaWarmupSeconds() {
        return plugin.getConfig().getInt("tpa.warmup-seconds", 3);
    }

    public boolean isTpaCancelOnMove() {
        return plugin.getConfig().getBoolean("tpa.cancel-on-move", true);
    }

    public boolean isTpaCancelOnDamage() {
        return plugin.getConfig().getBoolean("tpa.cancel-on-damage", true);
    }

    public int getRtpCooldownSeconds() {
        return plugin.getConfig().getInt("rtp.cooldown-seconds", 30);
    }

    public int getRtpWarmupSeconds() {
        return plugin.getConfig().getInt("rtp.warmup-seconds", 0);
    }

    public boolean isRtpCancelOnMove() {
        return plugin.getConfig().getBoolean("rtp.cancel-on-move", true);
    }

    public boolean isRtpCancelOnDamage() {
        return plugin.getConfig().getBoolean("rtp.cancel-on-damage", true);
    }

    public int getRtpMaxAttempts() {
        return plugin.getConfig().getInt("rtp.max-attempts", 30);
    }
}
