package me.sonix.playerdata.data;

import me.sonix.processors.Packet;

public interface Data {
    void process(Packet packet);
}