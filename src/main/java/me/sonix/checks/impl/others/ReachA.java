package me.sonix.checks.impl.others;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.ActionData;
import me.sonix.playerdata.data.impl.ConnectionData;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;

@Experimental
public class ReachA extends Check {

	public ReachA(Profile profile) {
		super(profile, CheckType.REACH, "A", "Long arms");
	}

	@Override
	public void handle(Packet packet) {
		MovementData data = profile.getMovementData();
		ActionData adata = profile.getActionData();
		VelocityData vdata = profile.getVelocityData();
		ConnectionData cdata = profile.getConnectionData();

	}
}
