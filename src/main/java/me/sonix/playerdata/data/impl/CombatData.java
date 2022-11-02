package me.sonix.playerdata.data.impl;

import com.avaje.ebean.validation.NotNull;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.Data;
import me.sonix.processors.Packet;
import me.sonix.wrappers.WrapperPlayClientUseEntity;
import org.bukkit.entity.Entity;

public class CombatData implements Data {
    private int lastAttackTicks;

    private int killauraTicks;

    private Profile profile;


    @Override
    public void process(Packet packet) {
        if (packet.isAttack()){
            handleUseEntity(packet.getUseEntityWrapper());

        }
        if (packet.is(Packet.Type.ARM_ANIMATION))
            handleArmAnimation();
        if (packet.is(Packet.Type.USE_ENTITY))
            handleFlying();
    }

    public int hitTicks, swings;
    public double distance;
    public Entity target;


    public @NotNull void handleUseEntity(WrapperPlayClientUseEntity wrapper) {
//        if (wrapper.getType() != WrapperPlayClientUseEntity.TYPE.name().)
        target = wrapper.getTarget(profile.getPlayer().getWorld());

        distance = profile.getPlayer().getLocation().toVector().setY(0).distance(target.getLocation().toVector().setY(0)) - .42;
//        ++hits;
//
//        hitTicks = 0;
//
//        if (target != lastTarget) {
//            ++currentTargets;
//        }
    }

    public void handleArmAnimation() {
        ++swings;
    }

    public void handleFlying() {
        ++hitTicks;
//        currentTargets = 0;
//
//        if (swings > 1) {
//            hitMissRatio = ((double) hits / (double) swings) * 100;
//        }
//        if (hits > 100 || swings > 100) {
//            hits = swings = 0;
//        }
    }
}