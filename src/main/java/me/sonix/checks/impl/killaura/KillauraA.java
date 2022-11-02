package me.sonix.checks.impl.killaura;

import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.processors.Packet;

import static me.sonix.processors.Packet.Type.USE_ENTITY;

public class KillauraA extends Check {
	public KillauraA(Profile profile) {
		super(profile, CheckType.KILLAURA, "A", "Post Killaura");
	}

	private boolean sent;
	public long lastFlying, lastPacket;

	@Override
	public void handle(Packet packet) {
		if (packet.isFlying()) {
			final long now = System.currentTimeMillis();
			final long delay = now - lastPacket;

			if (sent) {
				if (delay > 40L && delay < 100L) {
					increaseBufferBy(0.25);

					if (getBuffer() > 0.75) {
						fail(" * Late Flying packet");
					}
				} else {
					decreaseBufferBy(0.025);
				}

				sent = false;
			}

			this.lastFlying = now;
		} else if (packet.is(USE_ENTITY)){
			final long now = System.currentTimeMillis();
			final long delay = now - lastFlying;

			if (delay < 10L) {
				lastPacket = now;
				sent = true;
			} else {
				decreaseBufferBy(0.0025);
			}
		}
	}
}

