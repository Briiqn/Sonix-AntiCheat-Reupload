package me.sonix.checks.impl.others;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.ActionData;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;
import org.bukkit.entity.Player;

@Experimental
public class NoSlowdown extends Check {
	public NoSlowdown(Profile profile) {
		super(profile, CheckType.NOSLOW, "A", "Not slowing down");
	}

	@Override
	public void handle(Packet packet) {
		Player p = profile.getPlayer();
		MovementData data = profile.getMovementData();
		ActionData adata = profile.getActionData();
		VelocityData vdata = profile.getVelocityData();
		if (packet.isFlying()){
			if (adata.isSprinting() && p.isBlocking()){
				fail(" * Sprinting while blocking with a sword");
			}
		}
	}
}
