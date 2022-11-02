package me.sonix.playerdata.data.impl;

import me.sonix.processors.Packet;
import me.sonix.playerdata.data.Data;

public class TeleportData implements Data {

    private int teleportTicks;


    @Override
    public void process(Packet packet) {
    }

    public int getTeleportTicks() {
        return teleportTicks;
    }


}