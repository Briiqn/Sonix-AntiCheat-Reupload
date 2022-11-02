package me.sonix.playerdata.data.impl;

import me.sonix.playerdata.data.Data;
import me.sonix.processors.Packet;
import me.sonix.wrappers.WrapperPlayServerEntityVelocity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class VelocityData implements Data, Listener {
    public long lastVelocityTaken, lastAttack;
    public int speedThreshold;
    public LivingEntity lastHitEntity;



    public int airTicks, groundTicks, iceTicks, liquidTicks, blockTicks, slimeTicks, velXTicks, velYTicks, velZTicks;

    public double walkSpeed = 0.2, lastWalkSpeed = 0.2, lastDeltaXZ;
    @Override
    public void process(Packet packet) {
        if (packet.getType() == Packet.Type.SERVER_ENTITY_VELOCITY){
            final WrapperPlayServerEntityVelocity vel = packet.getVelocityWrapper();
            handle(vel.getVelocityX(), vel.getVelocityY(), vel.getVelocityX());
            handleTransaction();
        }
    }

    public double velocityX, velocityY, velocityZ, velocityXZ;
    public double lastVelocityX, lastVelocityY, lastVelocityZ, lastVelocityXZ;

    public int maxVelocityTicks, velocityTicks, ticksSinceVelocity, takingVelocityTicks;
    private short velocityID;

    private final Velocity transactionVelocity = new Velocity(0, 0, 0, 0);

    public final Map<Short, Vector> pendingVelocities = new HashMap<>();
    private int flyingTicks;

    public void handle(final double velocityX, final double velocityY, final double velocityZ) {
        lastVelocityX = this.velocityX;
        lastVelocityY = this.velocityY;
        lastVelocityZ = this.velocityZ;
        lastVelocityXZ = this.velocityXZ;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.velocityXZ =  Math.hypot(velocityX, velocityZ);

        this.velocityID = (short) ThreadLocalRandom.current().nextInt(Short.MAX_VALUE);

        pendingVelocities.put(velocityID, new Vector(velocityX, velocityY, velocityZ));
        handleFlying();
    }

    public void handleFlying() {
        ++ticksSinceVelocity;
        ++flyingTicks;

        if (isTakingVelocity()) {
            ++takingVelocityTicks;
            ticksSinceVelocity = 0;
        } else {
            takingVelocityTicks = 0;
        }
    }

    public void handleTransaction() {
        pendingVelocities.computeIfPresent(velocityID, (id, vector) -> {
            this.ticksSinceVelocity = 0;

            transactionVelocity.setVelocityX(vector.getX());
            transactionVelocity.setVelocityY(vector.getY());
            transactionVelocity.setVelocityZ(vector.getZ());

            transactionVelocity.setIndex(transactionVelocity.getIndex() + 1);

            this.velocityTicks = flyingTicks;
            this.maxVelocityTicks = (int) (((vector.getX() + vector.getZ()) / 2 + 2) * 15);

            pendingVelocities.remove(velocityID);

            return vector;
        });
    }

    public boolean isTakingVelocity() {
        return Math.abs(flyingTicks - this.velocityTicks) < this.maxVelocityTicks;
    }
}