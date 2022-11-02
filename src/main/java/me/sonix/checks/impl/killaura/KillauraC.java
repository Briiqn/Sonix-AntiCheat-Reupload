package me.sonix.checks.impl.killaura;

import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.processors.Packet;

import static me.sonix.processors.Packet.Type.ARM_ANIMATION;

public class KillauraC extends Check {
	public KillauraC(Profile profile) {
		super(profile, CheckType.KILLAURA, "C", "Illegal packet");
	}

	private boolean swung;
	@Override
	public void handle(Packet packet) {

		//No Swing v1

		if (packet.isAttack()) {
			if (increaseBuffer() > 2) {
				fail(" * No Swing");
			}
		} else if (packet.is(ARM_ANIMATION)) {
			resetBuffer();
		}

		//No Swing v2

		if (packet.isAttack())
			if (!swung) fail(" * No Swing");


		else if (packet.is(Packet.Type.ARM_ANIMATION))
			swung = true;


		else if (packet.isFlying())
			swung = false;

		//Attacking while block hitting
		if (profile.getPlayer().isBlocking() && packet.isAttack())
			fail( " * Illegal block order");
	}
}
