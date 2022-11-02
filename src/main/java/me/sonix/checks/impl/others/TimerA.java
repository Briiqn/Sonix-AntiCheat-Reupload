package me.sonix.checks.impl.others;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.processors.Packet;

@Experimental
public class TimerA extends Check {
	public TimerA(Profile profile) {super(profile, CheckType.TIMER, "A", "Speeding up time");}
	private long balance = 0L;
	private long lastFlying = 0L;

	@Override
	public void handle(Packet packet) {
		if (packet.isFlying()) {
			final long now = packet.getTimeStamp();

			final long delay = now - lastFlying;

			balance += 50L - delay;

			if (balance > 5L) {
				if (increaseBuffer() > 5)
					fail(" * Balance &c" + balance);

				balance = 0;
			}
			else
				decreaseBufferBy(0.001);

			this.lastFlying = now;
		}
		else if (packet.isTeleport()){
			if (lastFlying == 0L) return;
			balance -= 50L;
		}
	}
}
