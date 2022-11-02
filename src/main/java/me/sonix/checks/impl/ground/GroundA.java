package me.sonix.checks.impl.ground;

import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.ActionData;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.processors.Packet;
import org.bukkit.GameMode;

public class GroundA extends Check {
	public GroundA(Profile profile) {
		super(profile, CheckType.GROUND, "A", "Spoofed ground status");
	}
	@Override
	public void handle(Packet packet) {
		MovementData data = profile.getMovementData();
		ActionData adata = profile.getActionData();

		if (profile.getPlayer().getAllowFlight()
				|| profile.getPlayer().isFlying()
				|| data.insideVehicle()
				|| !packet.isMovement()
				|| adata.getGameMode() == GameMode.CREATIVE) return;


		if (data.isServerGround() != data.isOnGround()) {
			if (!data.isServerGround() && data.isOnGround() && !data.nearVehicle && data.sinceNearVehicleTicks > 5) {
				if (increaseBuffer() > 3) {
					fail(" * ServerGround &c" + data.isServerGround() + "\n * ClientGround &c" + data.isOnGround());
				}
			}
		}
		else {
			decreaseBufferBy(0.25);
		}
	}
}
