package me.sonix.utils.versionutils.impl;

import me.sonix.utils.versionutils.ClientVersion;
import me.sonix.utils.versionutils.VersionInstance;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

public class ProtocolSupport implements VersionInstance {
    @Override
    public ClientVersion getClientVersion(Player player) {
        return ClientVersion.getClientVersion(ProtocolSupportAPI.getProtocolVersion(player).getId());
    }
}