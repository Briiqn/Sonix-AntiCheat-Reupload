package me.sonix.playerdata.data.impl;

import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive;
import me.sonix.playerdata.data.Data;
import me.sonix.processors.Packet;

import java.util.Map;
import java.util.Optional;

public class ConnectionData implements Data {

    private long lastKeepAliveSent;
    private long lastKeepAliveReceived;
    private long keepAlivePing;
    private long keepAliveId;

    private final EvictingMap<Long, Long> keepAliveUpdates = new EvictingMap<>(20);
    @Override
    public void process(Packet packet) {
        /*
        Handle the packet
         */
    }

    public void handleIncomingKeepAlive(final WrappedPacketInKeepAlive wrapper) {
        final long now = System.currentTimeMillis();

        keepAliveUpdates.computeIfPresent(wrapper.getId(), (id, time) -> {
            keepAlivePing = now - time;
            lastKeepAliveReceived = now;

            return time;
        });
    }
    public void handleOutgoingKeepAlive(final WrappedPacketOutKeepAlive wrapper) {
        final long now = System.currentTimeMillis();
        final long id = wrapper.getId();

        lastKeepAliveSent = now;

        keepAliveId = id;
        keepAliveUpdates.put(id, System.currentTimeMillis());
    }

    public Optional<Long> getKeepAliveTime(final long identification) {
        final Map<Long, Long> entries = keepAliveUpdates;

        if (entries.containsKey(identification)) return Optional.of(entries.get(identification));

        return Optional.empty();
    }
}