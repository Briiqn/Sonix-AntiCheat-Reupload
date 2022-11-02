package me.sonix.checks.impl.movement;

import me.sonix.checks.enums.CheckType;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.processors.Packet;

public class SpeedD extends Check {
    public SpeedD(Profile profile) {
        super(profile, CheckType.SPEED, "D", "Wrong Deceleration in packet");
    }

    @Override
    public void handle(Packet packet) {
        if (!packet.isRotation()) return;

        //Basic Deceleration check

        MovementData data = profile.getMovementData();

        final float deltaYaw  = profile.getRotationData().getDeltaYaw();

        final double deltaXZ = data.getDeltaXZ();
        final double lastDeltaXZ = data.getLastDeltaXZ();

        final double accel = Math.abs(deltaXZ - lastDeltaXZ);

        final double squaredAccel = accel * 100;

        if (deltaYaw > 1.5f && deltaXZ > .15D && squaredAccel < 1.0E-5 && !data.onClimbable() && squaredAccel != 0) {
            if(increaseBuffer() > 5)
                fail(" * DY&c " + deltaYaw + "\n * accel&c " + squaredAccel + "\n * deltaXZ&c " +deltaXZ);
        }
        else decreaseBufferBy(0.25);
    }
}