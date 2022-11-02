package me.sonix.checks.impl.badpackets;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.processors.Packet;

@Experimental
public class BadpacketsB extends Check {
	public BadpacketsB(Profile profile) {
		super(profile, CheckType.BADPACKETS, "B", "Sent 2 same held slot items");
	}
	private int lastSlot = -1;
	private boolean server;
	@Override
	public void handle(Packet packet) {
//		if (packet.isClientHeltItemSLot()) {
//			int slot = profile.getPlayer().getInventory().getHeldItemSlot();
//
//			boolean invalid = slot == lastSlot;
//			boolean exempt = server;
//
//			if (invalid && !exempt) {
//				fail(" * No Debug");
//			}
//
//			lastSlot = slot;
//			server = false;
//		}
//		else if (packet.isServerHeltItemSLot())
//			server = true;
	}
}
