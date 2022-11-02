package me.sonix.checks.impl.killaura;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.ActionData;
import me.sonix.playerdata.data.impl.CombatData;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.processors.Packet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Experimental
public class KillauraB extends Check {
	public KillauraB(Profile profile) {
		super(profile, CheckType.KILLAURA, "B", "Invalid movement when attackiing");
	}

	@Override
	public void handle(Packet packet) {
		MovementData data = profile.getMovementData();
		ActionData adata = profile.getActionData();
		CombatData bdata = profile.getCombatData();
		if (packet.isFlying() && bdata.hitTicks < 2) {
			Entity target = bdata.target;

			double deltaXZ = data.getDeltaXZ();
			double lastDeltaXZ = data.getLastDeltaXZ();

			double baseSpeed = profile.getBaseSpeed(profile.getPlayer(), 0.22F);
			boolean sprinting = adata.isSprinting();

			double acceleration = Math.abs(deltaXZ - lastDeltaXZ);

			boolean exempt = !(target instanceof Player);
			boolean invalid = acceleration < 0.0027 && sprinting && deltaXZ > baseSpeed;

			if (invalid && !exempt) {
				if (increaseBuffer() > 2) {
					fail(" * Accel &c" + acceleration);
				}
			} else {
				decreaseBufferBy(0.05);
			}
		}
	}
}
