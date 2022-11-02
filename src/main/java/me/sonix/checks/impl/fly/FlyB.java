package me.sonix.checks.impl.fly;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.ActionData;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.processors.Packet;
import org.bukkit.GameMode;

@Experimental
public class FlyB extends Check {
	public FlyB(Profile profile) {
		super(profile, CheckType.FLY, "B", "Invalid Air Movement");
	}
	public int airTicks;
	public int airTickLimit = 16;
	@Override
	public void handle(Packet packet) {
		MovementData data = profile.getMovementData();
		ActionData adata = profile.getActionData();
		airTicks = data.airTicks;
		if (packet.isMovement()) {
			if (profile.getPlayer().getAllowFlight()
					|| profile.getPlayer().isFlying()
					|| data.insideVehicle()
					|| adata.getGameMode() == GameMode.CREATIVE) return;

			if (data.isInAir() && airTicks > airTickLimit && !data.nearVehicle && data.getDeltaY() >= 0 && data.sinceSlimeTicks > 20 && data.sinceNearVehicleTicks > 8) {
				if (increaseBuffer() > 5) {
					fail(" * ServerGround &c" + data.isServerGround() + "\n * ClientGround &c" + data.isOnGround() + "\n * in Air &c" + data.isInAir() + "\n * AirTicks &c" + airTicks + "\n * ClientAirTicks &c" + data.clientAirTicks);
				}
			}
			else {
				decreaseBufferBy(0.25);
			}
		}
	}
}
