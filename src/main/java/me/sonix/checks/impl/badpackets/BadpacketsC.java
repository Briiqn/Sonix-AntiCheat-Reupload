package me.sonix.checks.impl.badpackets;

import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.processors.Packet;

import static me.sonix.processors.Packet.Type.*;

public class BadpacketsC extends Check {
	public BadpacketsC(Profile profile) {
		super(profile, CheckType.BADPACKETS, "C", "Did not send position packet");
	}
	private int streak;
	private boolean teleported;
	@Override
	public void handle(final Packet packet) {
		if (packet.isFlying()) {
			if (teleported) {
				teleported = false;
				return;
			}

			if (packet.is(POSITION_LOOK) ||packet.is(POSITION) || profile.getPlayer().isInsideVehicle()) {
				streak = 0;
				return;
			}

			if (++streak > 30) fail(" * No Debug");
		}

		else if (packet.is(STEER_VEHICLE)) {
			streak = 0;
		}

		else if (packet.isTeleport()) {
			teleported = true;
		}
	}
}
