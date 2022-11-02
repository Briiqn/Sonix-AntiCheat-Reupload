package me.sonix.checks.impl.inventory;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.processors.Packet;
import org.bukkit.inventory.PlayerInventory;

@Experimental
public class InventoryA extends Check {
	public InventoryA(Profile profile) {
		super(profile, CheckType.INVENTORY, "", "Invalid inventory packets");
	}

	@Override
	public void handle(Packet packet) {
		if (packet.isAttack() && profile.getPlayer().getOpenInventory() instanceof PlayerInventory)
			fail( " * Attacking while inside inventory");

		if (packet.isAttack() && packet.is(Packet.Type.WINDOW_CLICK))
			fail( " * Attacking while clicking inventory slots");

		if (packet.isMovement() && packet.is(Packet.Type.WINDOW_CLICK))
			fail( " * Moving while clicking inventory slots");
	}
}
