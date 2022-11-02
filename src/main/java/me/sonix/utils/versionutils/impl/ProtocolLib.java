package me.sonix.utils.versionutils.impl;

import com.comphenix.protocol.ProtocolLibrary;
import me.sonix.utils.versionutils.ClientVersion;
import me.sonix.utils.versionutils.VersionInstance;
import org.bukkit.entity.Player;

public class ProtocolLib implements VersionInstance {
    @Override
    public ClientVersion getClientVersion(Player player) {
        return ClientVersion.getClientVersion(ProtocolLibrary.getProtocolManager().getProtocolVersion(player));
    }
}