package me.sonix.checks.impl.aim;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.RotationData;
import me.sonix.processors.Packet;

@Experimental
public class AimA extends Check {
	public AimA(Profile profile) {
		super(profile, CheckType.AIM, "A", "Consistent rotations");
	}
	private float suspiciousYaw;
	@Override
	public void handle(Packet packet) {

		RotationData data = profile.getRotationData();
		if (!packet.isAttack()) return;

		float diff = Math.abs(data.getYaw() - data.getLastYaw()) % 360.0F;
		if (diff > 1.0F && Math.round(diff) == diff) {
			if (diff == this.suspiciousYaw)
				fail(" * Yaw&c " + data.getDeltaYaw() + "\n * Last Yaw&c " + data.getLastDeltaYaw());
			this.suspiciousYaw = Math.round(diff);
		} else {
			this.suspiciousYaw = 0.0F;
		}

		float f1 = Math.abs(data.getYaw() - data.getLastYaw());
		float f2 = Math.abs(data.getPitch() - data.getLastPitch());

		if (data.getLastRotationTicks() < 3 && f1 > 0.0F && f1 < 0.8D && f2 > 0.279D && f2 < 0.28090858D) {
			if (increaseBuffer() > 2)
				fail(" * Yaw&c " + data.getYaw() + "\n * Last Yaw&c " + data.getLastYaw() + "\n * Pitch&c " + data.getPitch() + "\n * Last Pitch&c " + data.getLastPitch());
		} else decreaseBufferBy(0.2);
	}
}
