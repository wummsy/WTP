package com.wtputils.config;

import org.bukkit.Material;

import java.util.List;

/**
 * Holds the per-world RTP configuration loaded from config.yml (rtp.dimensions.<worldName>).
 */
public class DimensionConfig {

    private final String worldName;
    private final boolean enabled;
    private final String displayName;
    private final Material icon;
    private final List<String> lore;
    private final String permission;
    private final int centerX;
    private final int centerZ;
    private final int minRadius;
    private final int maxRadius;

    public DimensionConfig(String worldName, boolean enabled, String displayName, Material icon,
                            List<String> lore, String permission, int centerX, int centerZ,
                            int minRadius, int maxRadius) {
        this.worldName = worldName;
        this.enabled = enabled;
        this.displayName = displayName;
        this.icon = icon;
        this.lore = lore;
        this.permission = permission;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getPermission() {
        return permission;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public int getMinRadius() {
        return minRadius;
    }

    public int getMaxRadius() {
        return maxRadius;
    }
}
