package com.wtputils.gui;

import com.wtputils.WTPUtils;
import com.wtputils.config.ConfigManager;
import com.wtputils.config.DimensionConfig;
import com.wtputils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom InventoryHolder so the click listener can reliably identify the RTP GUI
 * (rather than matching on title, which is fragile).
 */
public class RtpGuiHolder implements InventoryHolder {

    public static final NamespacedKey WORLD_KEY = new NamespacedKey("wtputils", "rtp_world");

    private Inventory inventory;

    public static Inventory build(WTPUtils plugin, Player viewer) {
        ConfigManager config = plugin.getConfigManager();
        RtpGuiHolder holder = new RtpGuiHolder();

        List<DimensionConfig> enabledDimensions = new ArrayList<>();
        for (DimensionConfig dim : config.getDimensions().values()) {
            if (dim.isEnabled()) {
                enabledDimensions.add(dim);
            }
        }

        int size = Math.max(9, ((enabledDimensions.size() - 1) / 9 + 1) * 9);
        Inventory inventory = Bukkit.createInventory(holder, size, MessageUtil.color(config.getMessage("rtp-gui-title")));
        holder.inventory = inventory;

        for (DimensionConfig dim : enabledDimensions) {
            ItemStack item = new ItemStack(dim.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(MessageUtil.color(dim.getDisplayName()));

                List<String> lore = new ArrayList<>();
                for (String line : dim.getLore()) {
                    lore.add(MessageUtil.color(line));
                }
                boolean hasPerm = dim.getPermission() == null || dim.getPermission().isEmpty()
                        || viewer.hasPermission(dim.getPermission()) || viewer.hasPermission("wtputils.rtp.*");
                if (!hasPerm) {
                    lore.add(MessageUtil.color("&cYou do not have permission for this."));
                } else {
                    lore.add(MessageUtil.color("&eClick to randomly teleport here!"));
                }
                meta.setLore(lore);

                meta.getPersistentDataContainer().set(WORLD_KEY, PersistentDataType.STRING, dim.getWorldName());
                item.setItemMeta(meta);
            }
            inventory.addItem(item);
        }

        return inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
