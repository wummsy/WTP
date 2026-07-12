package com.wtputils.listener;

import com.wtputils.WTPUtils;
import com.wtputils.gui.RtpGuiHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class GuiListener implements Listener {

    private final WTPUtils plugin;

    public GuiListener(WTPUtils plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof RtpGuiHolder)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null
                || !(event.getClickedInventory().getHolder() instanceof RtpGuiHolder)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) return;

        String worldName = meta.getPersistentDataContainer().get(RtpGuiHolder.WORLD_KEY, PersistentDataType.STRING);
        if (worldName == null) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        player.closeInventory();
        plugin.getRtpManager().attemptRtp(player, worldName);
    }
}
