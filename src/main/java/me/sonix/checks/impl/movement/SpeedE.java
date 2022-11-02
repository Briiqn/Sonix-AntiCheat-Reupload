package me.sonix.checks.impl.movement;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

@Experimental
public class SpeedE extends Check {
	public SpeedE(Profile profile) {
		super(profile, CheckType.SPEED, "E", "Invalid Friction");
	}
	private double blockSlipperiness = 0.91;
	private double lastHorizontalDistance = 0.0;
	@Override
	public void handle(Packet packet) {
//		if (profile.isBypassing()) return;
		MovementData data = profile.getMovementData();
		VelocityData vdata = profile.getVelocityData();
		if (packet.isFlying()) {

			double deltaY = data.getDeltaY();

			double blockSlipperiness = this.blockSlipperiness;
			double attributeSpeed = 1.d;

			boolean lastOnGround = data.isLastOnGround();

			attributeSpeed += profile.getPotionEffectLevel(profile.getPlayer(), PotionEffectType.SPEED) * (float) 0.2 * attributeSpeed;
			attributeSpeed += profile.getPotionEffectLevel(profile.getPlayer(), PotionEffectType.SLOW) * (float) -.15 * attributeSpeed;

			if (lastOnGround) {
				blockSlipperiness *= 0.91f;

				attributeSpeed *= 1.3;
				attributeSpeed *= 0.16277136 / Math.pow(blockSlipperiness, 3);

				if (deltaY > 0.0) {
					attributeSpeed += 0.2;
				}
			} else {
				attributeSpeed = 0.026f;
				blockSlipperiness = 0.91f;
			}

			double horizontalDistance = data.getDeltaXZ();
			double movementSpeed = (horizontalDistance - lastHorizontalDistance) / attributeSpeed;

			if (movementSpeed > 1.0 && !data.onClimbable() && !data.nearVehicle && !data.insideLiquid() && !profile.getPlayer().isFlying() && !profile.getPlayer().getAllowFlight() && !data.onWeb() && data.sinceNearVehicleTicks > 8) {
				increaseBufferBy(10);

				if (getBuffer() > 20) {
					fail(" * MovementSpeed &c "+movementSpeed);
				}
			} else {
				decreaseBufferBy(1);
			}

			double x = profile.getPlayer().getLocation().clone().getX();
			double y = profile.getPlayer().getLocation().clone().getY();
			double z = profile.getPlayer().getLocation().clone().getZ();

			Location blockLocation = new Location(profile.getPlayer().getWorld(), x, Math.floor(y) - 1, z);

			this.blockSlipperiness = getBlockFriction(blockLocation);
			this.lastHorizontalDistance = horizontalDistance * blockSlipperiness;
		}

		if (packet.isFlying()) {
			int airTicks = data.airTicks;

			double deltaXZ = data.getDeltaXZ();
			double lastDeltaXZ = data.getLastDeltaXZ();

			double predicted = (lastDeltaXZ * 0.91F) + 0.026F;
			double difference = deltaXZ - predicted;

			boolean invalid = difference > 1E-5 && predicted > 0.075 && airTicks > 2;

			if (invalid && !data.nearVehicle && !data.insideVehicle() && !data.onClimbable() && !data.insideLiquid() && !profile.getPlayer().isFlying() && !profile.getPlayer().getAllowFlight() && !data.onWeb() && data.sinceNearVehicleTicks > 8) {
				if (increaseBuffer() > 5) {
					fail(" * difference &c" + difference + "\n * predicted &c"+predicted);
				}
			} else {
				decreaseBufferBy(0.5);
			}
		}
	}

	public double getBlockFriction(final Location to) {
		try {
			return (getBlockAsync(to).getType()) == Material.PACKED_ICE
					|| getBlockAsync(to).getType() == Material.ICE ? 0.9800000190734863
					: (getBlockAsync(to).getType()).toString().toLowerCase().contains("slime") ? 0.800000011920929
					: 0.6000000238418579;
		} catch (final Exception ignored) {
			return 0.6000000238418579;
		}
	}
	public Block getBlockAsync(final Location location) {
		if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
			return location.getWorld().getBlockAt(location);
		} else {
			return null;
		}
	}
}
