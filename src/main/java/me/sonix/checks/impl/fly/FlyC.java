package me.sonix.checks.impl.fly;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;
import org.bukkit.entity.Player;

@Experimental
public class FlyC extends Check {
	public FlyC(Profile profile) {
		super(profile, CheckType.FLY, "C", "Goofy ahh");
	}

	@Override
	public void handle(Packet packet) {
		Player p = profile.getPlayer();

		MovementData data = profile.getMovementData();
		VelocityData vData = profile.getVelocityData();
//		if (profile.isBypassing()) return;
		if(data == null
				|| p.getAllowFlight()
				|| data.insideLiquid()
				|| data.insideVehicle()
				|| data.onClimbable()) return;

		float deltaY = (float) (data.getDeltaY() - data.getLastDeltaY());

		if(data.getFlyTicks() > 7 && deltaY > 0 && !data.insideLiquid() && !data.isOnGround() && !data.onWeb() && data.sinceWebTicks > 20 && data.sinceFlyingTicks > 20 && data.sinceNearVehicleTicks > 3) {
			if(data.flyThreshold++ > 2) {
				fail(" * To &c" + deltaY + "\n * From &c" + data.getLastDeltaY());
			}
		} else data.flyThreshold-= data.flyThreshold > 0 ? 0.1f : 0;

		float accel = (float) (deltaY - data.getLastDeltaY());

		if(data.getFlyTicks() > 1 && Math.abs(accel) == 0 && !data.onWeb() && !data.onSlime() && !data.nearVehicle && data.sinceWebTicks > 20 && data.sinceFlyingTicks > 20 && data.sinceLiquidTicks > 4 && !data.nearWall && data.sinceNearVehicleTicks > 8) {
			if(data.flyThreshold++ > 3) {
				fail( " * Accel &c" + accel);
			}
		} else data.flyThreshold-= data.flyThreshold > 0 ? 0.25f : 0;

	}
}
