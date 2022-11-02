package me.sonix.checks.impl.others;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.processors.Packet;

@Experimental
public class FastPlaceA extends Check {
	public FastPlaceA(Profile profile) {
		super(profile, CheckType.INTERACT, "A", "Fast Place");
	}

	private long lastTime, lastDelta;
	@Override
	public void handle(Packet packet) {
//		if(packet.isBlockPlace()){
//			final long time = packet.getTimeStamp();
//
//			final long lastTime= this.lastTime;
//
//			this.lastTime = time;
//			final  long delta = time - lastTime;
//
//			final long lastDelta = this.lastDelta;
//
//			this.lastDelta = delta;
//
//			fail(" * delta &c"+delta + "\n * ld&c " + lastDelta);
//
//		}
	}
}
