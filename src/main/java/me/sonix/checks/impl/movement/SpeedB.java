package me.sonix.checks.impl.movement;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.ActionData;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@Experimental
public class SpeedB extends Check {
	public SpeedB(Profile profile) {
		super(profile, CheckType.SPEED, "B", "Invalid Movement (Rework)");
	}

	@Override
	public void handle(Packet packet) {
		Player p = profile.getPlayer();
		MovementData data = profile.getMovementData();
		VelocityData vdata = profile.getVelocityData();
		if (profile.isBypassing()) return;
		if(p.getAllowFlight()
				|| p.isInsideVehicle()) {
			return;
		}
		float threshold = data.isOnGround() ? 0.31f : .341f;

		float deltaXZ = (float) Math.sqrt(Math.pow(data.getDeltaX() - data.getLastDeltaX(), 2) + Math.pow(data.getDeltaZ() - data.getLastDeltaZ(), 2)), deltaY = (float) (data.getDeltaY() - data.getLastDeltaY());
		float speedLevel = profile.getPotionEffectLevel(p, PotionEffectType.SPEED);

		threshold+= data.groundTicks < 5 ? speedLevel * 0.06f : speedLevel * 0.04f;
		threshold*= data.groundTicks < 5 ? 1.7f : 1.0;

		if(deltaXZ > threshold  && data.sinceNearVehicleTicks > 8 && !data.nearVehicle ) {
			if(increaseBuffer() > 5) {
				fail( " * DeltaXZ &c"+deltaXZ + "\n * Threshold &c" + threshold);
			}
		} else {
			decreaseBufferBy(0.25);
		}
	}

}
