package me.sonix.checks.impl.movement;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;

@Experimental
public class SpeedC extends Check {
	public SpeedC(Profile profile) {
		super(profile, CheckType.SPEED, "C", "Invalid acceleration");
	}
	@Override
	public void handle(Packet packet) {
		MovementData data = profile.getMovementData();
		VelocityData vdata = profile.getVelocityData();
		if (profile.isBypassing()) return;
		if(profile.getPlayer().getAllowFlight()
				|| profile.getPlayer().isFlying()) return;

		if (packet.isFlying()) {
			double deltaXZ = data.getDeltaXZ();
			double lastDeltaXZ = data.getLastDeltaXZ();

			double limit = (profile.getBaseSpeed(profile.getPlayer(), 0.34F) + 0.1) + (vdata.isTakingVelocity() ? vdata.velocityXZ + 0.15 : 0.0);

			double acceleration = deltaXZ - lastDeltaXZ;
			if (profile.getPlayer().isFlying() || data.insideVehicle() || data.nearVehicle || data.onClimbable() || data.onSlime() ) return;

			boolean invalid = acceleration > limit;

			if (invalid && data.sinceNearVehicleTicks > 8) fail( " * Acceleration &c" +acceleration + "\n * Limit&c "+limit);
		}
	}
}
