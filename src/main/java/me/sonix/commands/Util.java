package me.sonix.commands;

import org.bukkit.ChatColor;

public class Util {
	public static String translate(String source) {
		return ChatColor.translateAlternateColorCodes('&', source);
	}
}
