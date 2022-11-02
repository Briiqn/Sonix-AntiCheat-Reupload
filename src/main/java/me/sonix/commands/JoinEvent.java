package me.sonix.commands;

import me.sonix.Main;
import me.sonix.managers.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.Bukkit.getServer;

public class JoinEvent implements Listener {
	Profile profile;
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
//		profile.bypass = true;
//
//		getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Sonix"), () -> {
//			profile.bypass = false;
//		}, (40));
		getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Sonix"), () -> {
			if (Main.getInstance().testserverEvents.testServer) event.getPlayer().sendMessage(Util.translate("&7Test server mode is &cENABLED&7. You have been warned."));
			Bukkit.broadcastMessage(Util.translate(event.getPlayer().getDisplayName() +" &7has joined using &c"+ profile.getClient()));
		}, (15));

	}
}
