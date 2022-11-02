package me.sonix.commands;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class TestserverEvents implements Listener {
	public boolean testServer;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			if (testServer){
				e.setDamage(0.0);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void entityDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
			if (testServer) {
				e.setDamage(0.0);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void Damage(EntityDamageEvent e) {
		if (e.getEntity().getType() == EntityType.PLAYER){
			if (testServer){
				e.setDamage(0.0);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void FallDamage(EntityDamageEvent e) {
		try {
			if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL)
				if (testServer){
					e.setDamage(0.0);
					e.getEntity().sendMessage(Util.translate("&7You would have taken &c"+e.getDamage()+" &7fall damage."));
				}
		}
		catch (NullPointerException ee) {ee.printStackTrace();}
	}
	public void antiCheatban(Player player){
		player.getWorld().strikeLightningEffect(player.getLocation());
	}
}
