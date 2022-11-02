package me.sonix.checks.impl.badpackets;

import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.RotationData;
import me.sonix.processors.Packet;

public class BadpacketsA extends Check {
	public BadpacketsA(Profile profile) {
		super(profile, CheckType.BADPACKETS, "A", "Invalid pitch");
	}

	@Override
	public void handle(Packet packet) {
		RotationData data = profile.getRotationData();

		if (data.getPitch() > 90f || data.getPitch() < -90f)
			fail(" * Pitch &c" + data.getPitch());
	}
}
