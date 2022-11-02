package me.sonix.utils.versionutils.impl;

import me.sonix.utils.versionutils.VersionInstance;
import me.sonix.utils.versionutils.ClientVersion;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;

public class ViaVersion implements VersionInstance {
    @Override
    public ClientVersion getClientVersion(Player player) {
        return ClientVersion.getClientVersion(Via.getAPI().getPlayerVersion(player));
    }
}