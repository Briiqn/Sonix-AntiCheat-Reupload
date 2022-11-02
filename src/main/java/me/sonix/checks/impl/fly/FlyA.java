package me.sonix.checks.impl.fly;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.ActionData;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.Packet;
import net.minecraft.server.v1_8_R3.BlockSlime;
import org.bukkit.GameMode;
import org.bukkit.block.Block;

@Experimental
public class FlyA extends Check {
	public FlyA(Profile profile) {
		super(profile, CheckType.FLY, "A", "Impossible upwards acceleration");
	}

	@Override
	public void handle(Packet packet) {
		MovementData data = profile.getMovementData();
		ActionData adata = profile.getActionData();
		VelocityData vdata = profile.getVelocityData();
		if (profile.isBypassing()) return;
		if (adata.getGameMode() == GameMode.CREATIVE
				|| profile.getPlayer().getAllowFlight()
				|| profile.getPlayer().isFlying()) return;


		if (!data.insideVehicle() && !data.insideLiquid() && !data.onClimbable() && !data.onSlime() && !data.onWeb() && !data.nearVehicle && data.sinceFlyingTicks > 20 && data.sinceNearVehicleTicks > 8) {
			for(int i =0; i < profile.getPlayer().getFallDistance(); i++){
				if (data.getDeltaY() > .41999998688697815f && !(getBlock2((int) profile.getPlayer().getLocation().getX()-i, (int) profile.getPlayer().getLocation().getY()-i, (int) profile.getPlayer().getLocation().getZ()-i) instanceof BlockSlime)) {
					fail(" * DeltaY &c" + data.getDeltaY());
				}
			}
		}
	}

	public Block getBlock2(int x, int y, int z) {
		return profile.getPlayer().getWorld().getBlockAt(x,y,z);
	}
}
