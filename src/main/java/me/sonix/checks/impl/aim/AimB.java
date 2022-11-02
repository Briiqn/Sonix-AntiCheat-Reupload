package me.sonix.checks.impl.aim;

import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.RotationData;
import me.sonix.playerdata.processors.impl.CinematicProcessor;
import me.sonix.processors.Packet;
import me.sonix.utils.MathUtils;

public class AimB extends Check {
	public AimB(Profile profile) {super(profile, CheckType.AIM, "B", "Invalid rotation constant");}
	@Override
	public void handle(Packet packet) {
		RotationData rdata = profile.getRotationData();
		CinematicProcessor cpro = new CinematicProcessor(profile);
		if (packet.isRotation()) {
			float deltaYaw = rdata.getDeltaYaw();
			float deltaPitch = rdata.getDeltaPitch();

			float lastDeltaYaw = rdata.getLastDeltaYaw();
			float lastDeltaPitch = rdata.getLastDeltaPitch();

			double divisorYaw = MathUtils.gcd((long) (deltaYaw * MathUtils.EXPANDER), (long) (lastDeltaYaw * MathUtils.EXPANDER));
			double divisorPitch = MathUtils.gcd((long) (deltaPitch * MathUtils.EXPANDER), (long) (lastDeltaPitch * MathUtils.EXPANDER));

			double constantYaw = divisorYaw / MathUtils.EXPANDER;
			double constantPitch = divisorPitch / MathUtils.EXPANDER;

			double currentX = deltaYaw / constantYaw;
			double currentY = deltaPitch / constantPitch;

			double previousX = lastDeltaYaw / constantYaw;
			double previousY = lastDeltaPitch / constantPitch;

			if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f) {
				double moduloX = currentX % previousX;
				double moduloY = currentY % previousY;

				double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
				double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

				boolean invalidX = moduloX > 90.d && floorModuloX > 0.1;
				boolean invalidY = moduloY > 90.d && floorModuloY > 0.1;

				if (cpro.isCinematic()) return;

				if (invalidX && invalidY) {
					if (increaseBuffer() > 6) {
						fail(" * deltaYaw &c"+deltaYaw+"\n * lastDeltaYaw &c"+lastDeltaYaw+"\n * deltaPitch &c"+deltaPitch+"\n * lastDeltaPitch &c"+lastDeltaPitch);
					}
				} else {
					decreaseBufferBy(0.25);
				}
			}
		}
	}
}
