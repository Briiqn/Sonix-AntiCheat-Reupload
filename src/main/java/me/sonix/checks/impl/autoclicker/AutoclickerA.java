package me.sonix.checks.impl.autoclicker;

import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.EvictingList;
import me.sonix.processors.Packet;

import java.util.Collection;

@Experimental
public class AutoclickerA extends Check {
	public AutoclickerA(Profile profile) {
		super(profile, CheckType.AUTOCLICKER, "A", "High CPS");
	}

	private long lastSwing = -1;
	private long delay;
	private int movements;
	private double cps, rate;

	@Override
	public void handle(Packet packet) {
		if (packet.is(Packet.Type.ARM_ANIMATION)) {
			handleArmAnimation();
			handleFlying();
			final double cps = getCps();

			final boolean invalid = cps > 25 && !Double.isInfinite(cps);

			if (invalid) {
				fail("CPS=" + cps);
			}
		}
	}

	private final EvictingList<Integer> clicks = new EvictingList<>(10);


	public void handleArmAnimation() {
		if (lastSwing > 0) {
			delay = System.currentTimeMillis() - lastSwing;
		}
		lastSwing = System.currentTimeMillis();


		click: {
			if (movements > 5) break click;

			clicks.add(movements);
		}

		if (clicks.size() > 5) {
			final double cps = getCps(clicks);
			final double rate = cps * movements;

			this.cps = cps;
			this.rate = rate;
		}

		movements = 0;
	}

	public void handleFlying() {
		movements++;
	}

	public double getCps(final Collection<? extends Number> data) {
		return 20 / getAverage(data);
	}
	public double getAverage(final Collection<? extends Number> data) {
		double sum = 0.0;

		for (final Number number : data) {
			sum += number.doubleValue();
		}

		return sum / data.size();
	}

	public double getCps() {
		return cps;
	}

	public double getRate() {
		return rate;
	}

	public long getDelay() {
		return delay;
	}
}
