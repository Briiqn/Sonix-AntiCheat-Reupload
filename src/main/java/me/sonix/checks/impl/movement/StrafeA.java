package me.sonix.checks.impl.movement;

import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;

public class StrafeA extends Check {
	public StrafeA(Profile profile) {
		super(profile, CheckType.STRAFE, "A", "Basic Strafe Check");
	}

	@Override
	public void handle(Packet packet) {
		MovementData data = profile.getMovementData();
		VelocityData vdata = profile.getVelocityData();
		if (profile.isBypassing()) return;
		if (packet.isMovement()) {
			double deltaX = data.getDeltaX();
			double deltaZ = data.getDeltaZ();

			double deltaXZ = data.getDeltaXZ();

			double lastDeltaX = data.getLastDeltaX();
			double lastDeltaZ = data.getLastDeltaZ();

			int airTicks = data.clientAirTicks;

			double blockSlipperiness = 0.91F;
			double attributeSpeed = 0.026;

			double predictedDeltaX = lastDeltaX * blockSlipperiness;
			double predictedDeltaZ = lastDeltaZ * blockSlipperiness;

			double diffX = Math.abs(deltaX - predictedDeltaX);
			double diffZ = Math.abs(deltaZ - predictedDeltaZ);
			if (profile.getPlayer().isFlying() || profile.getPlayer().getAllowFlight() || data.insideVehicle() || data.onClimbable() || data.insideLiquid() || data.nearWall) return;
			boolean invalid = (diffX > attributeSpeed || diffZ > attributeSpeed) && deltaXZ > .05 && airTicks > 2;

			if (invalid) {
				if (increaseBuffer() > 2) {
					fail(" * diffX &c"+diffX + "\n * diffZ &c"+diffZ);
				}
			} else {
				decreaseBufferBy(0.1);
			}
		}
	}
}
