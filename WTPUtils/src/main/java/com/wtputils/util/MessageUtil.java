package com.wtputils.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Small helper for colorizing and placeholder-replacing messages loaded from config.yml.
 */
public final class MessageUtil {

    private MessageUtil() {
    }

    public static String color(String input) {
        if (input == null) return "";
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String placeholder(String input, String placeholder, String value) {
        if (input == null) return "";
        return input.replace(placeholder, value);
    }

    public static void send(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(color(message));
    }
}
