package me.sonix.checks.impl.fly;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;

@Experimental
public class FlyD extends Check {
	int can003;

	public FlyD(Profile profile) {
		super(profile, CheckType.FLY, "D", "Gravity modification");
	}

	@Override
	public void handle(Packet packet) {
		MovementData data = profile.getMovementData();
		VelocityData vdata = profile.getVelocityData();

		if (profile.isBypassing()) return;

		if(profile.getPlayer().getAllowFlight()
				|| profile.getPlayer().isFlying()
				|| data.insideVehicle()) return;

		if (packet.isFlying()) {
			double detlaY = data.getDeltaY();
			double lastDeltaY = data.getLastDeltaY();
			double gravity = (lastDeltaY - 0.08) * 0.98F;
			double gravityDiff = Math.abs(detlaY - gravity);

			if ((Math.abs(lastDeltaY - 0.08) < 0.005)) {
				can003 = 3;
			}

			can003--;

			if (can003 == 0 && data.getFlyTicks() > 7 && gravityDiff > .05 && !data.onClimbable() && !data.insideLiquid() && !data.onWeb() && !data.onSlime() && !data.nearVehicle && data.sinceWebTicks > 12 && data.sinceLiquidTicks > 10 && data.sinceFlyingTicks > 20 && data.sinceNearVehicleTicks > 6) {
				fail(" * g &c"+gravity + "\n * gd &c"+gravityDiff + "\n * DeltaY &c"+detlaY + "\n * LastDeltaY &c"+lastDeltaY);
			}
		}
	}
}
