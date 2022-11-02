package me.sonix.playerdata.processors.impl;

import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.RotationData;
import me.sonix.playerdata.processors.Processor;
import me.sonix.utils.custom.CustomLocation;

public class GhostblockProcessor implements Processor {
	public boolean onGhostBlock;
	public int ghostTicks;
	private final Profile profile;

	public GhostblockProcessor(Profile profile) {
		this.profile = profile;
	}
	public CustomLocation lastGroundLocation;

	public void process() {
		MovementData data = profile.getMovementData();
		RotationData rdata = profile.getRotationData();
		final boolean onGhostBlock = data.isOnGround()
				&& profile.getPlayer().getLocation().clone().getY() % 0.015625 < 0.03
				&& data.inAirOneblock()
				&& data.airTicks > 2;

		final double deltaY = data.getDeltaY();
		final double lastDeltaY = data.getLastDeltaY();

		double predictedY = (lastDeltaY - 0.08) * 0.98F;
		if (Math.abs(predictedY) < 0.005) predictedY = 0.0;

		final boolean underGhostBlock = data.getSinceBlockNearHeadTicks() > 3
				&& Math.abs(deltaY - ((-0.08) * 0.98F)) < 1E-5
				&& Math.abs(deltaY - predictedY) > 1E-5;

		this.onGhostBlock = onGhostBlock || underGhostBlock;

		if (this.onGhostBlock) ++ghostTicks;
		else ghostTicks = 0;


		if (!data.inAirOneblock()
				&& data.isOnGround()) {
			final CustomLocation location = data.getLocation().clone();

			location.setYaw(rdata.getYaw());
			location.setPitch(rdata.getPitch());

			lastGroundLocation = location;
		}
	}
}
