package me.sonix.checks.impl.movement;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.ActionData;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;
import org.bukkit.potion.PotionEffectType;

@Experimental
public class SpeedA extends Check {
	public SpeedA(Profile profile) {
		super(profile, CheckType.SPEED, "A", "Moving faster than possible");
	}

	@Override
	public void handle(Packet packet) {
		MovementData data = profile.getMovementData();
		VelocityData vdata = profile.getVelocityData();
		ActionData adata = profile.getActionData();
//		if (profile.isBypassing()) return;
		if (packet.isFlying()) {
			double deltaXZ = data.getDeltaXZ();
			double deltaY = data.getDeltaY();

			int groundTicks = data.groundTicks;
			int airTicks = data.clientAirTicks;

			float modifierJump = profile.getPotionEffectLevel(profile.getPlayer(), PotionEffectType.JUMP) * 0.1F;
			float jumpMotion = 0.42F + modifierJump;

			double groundLimit = data.getBaseGroundSpeed();
			double airLimit = data.getBaseAirSpeed();

			if (Math.abs(deltaY - jumpMotion) < 1.0E-4
					&& airTicks == 1) {
				groundLimit = data.getBaseGroundSpeed();
				airLimit = getAfterJumpSpeed();
			}

			if (data.nearStair) {
				airLimit += 0.91F;
				groundLimit += 0.91F;
			}

			if (data.sinceSlimeTicks < 20) {
				airLimit += 0.34F;
				groundLimit += 0.34F;
			}

			if (data.getSinceBlockNearHeadTicks() < 6) {
				airLimit += 0.91F / Math.max(1, data.getSinceBlockNearHeadTicks());
				groundLimit += 0.91F / Math.max(1, data.getSinceBlockNearHeadTicks());
			}

			if (groundTicks < 7) {
				groundLimit += (0.25F / groundTicks);
			}

//			if (vdata.isTakingVelocity()) {
//				groundLimit += vdata.velocityXZ + 0.05;
//				airLimit += vdata.velocityXZ + 0.05;
//			}

			// Problematic way of fixing it but good enough.
			if (data.sinceTeleportTicks < 15) {
				airLimit += 0.1;
				groundLimit += 0.1;
			}
			if (data.insideVehicle() || adata.allowFly() || profile.getPlayer().getAllowFlight() || profile.getPlayer().isFlying()) return;

			if (airTicks > 0 && data.sinceSlimeTicks > 25 && data.sinceIceTicks > 20 && !data.nearVehicle && data.sinceFlyingTicks > 20 && data.sinceNearVehicleTicks > 4) {
				if (deltaXZ > airLimit) {
					if (increaseBuffer() > 3) {
						fail( " * GroundLimit &c " +groundLimit + "\n * AirLimit&c " + airLimit + "\n * DeltaXZ &c" + deltaXZ);
					}
				} else {
					decreaseBufferBy(0.15);
				}
			} else {
				if (deltaXZ > groundLimit) {
					if (increaseBuffer() > 3) {
						fail( " * GroundLimit &c " +groundLimit + "\n * AirLimit &c" + airLimit + "\n * DeltaXZ &c" + deltaXZ);
					}
				} else {
					decreaseBufferBy(0.15);
				}
			}
		}
		if (packet.isFlying()) {
			double deltaXZ = data.getDeltaXZ();

			int iceTicks = data.sinceIceTicks;
			int slimeTicks = data.sinceSlimeTicks;
			int collidedVTicks = data.getSinceBlockNearHeadTicks();

//			boolean takingVelocity = vdata.isTakingVelocity();
//			double velocityXZ = vdata.velocityXZ;

			double limit = profile.getBaseSpeed(profile.getPlayer(), 0.34F);

			if (iceTicks < 40 || slimeTicks < 40) limit += 0.34;
			if (collidedVTicks < 40) limit += 0.91;
//			if (takingVelocity) limit += (velocityXZ + 0.15);

			if (data.insideVehicle() || profile.getPlayer().isFlying() || profile.getPlayer().getAllowFlight()) return;
			boolean invalid = deltaXZ > limit && data.airTicks > 2;

			if (invalid && data.sinceSlimeTicks > 12 && data.sinceIceTicks > 12) {
				if (increaseBuffer() > 8) {
					fail( " * deltaXZ &c" +deltaXZ);
				}
			} else {
				decreaseBufferBy(0.75);
			}
		}
	}



	private double getAfterJumpSpeed() {
		return 0.62 + 0.033 * (double) (profile.getPotionEffectLevel(profile.getPlayer(), PotionEffectType.SPEED));
	}
}
