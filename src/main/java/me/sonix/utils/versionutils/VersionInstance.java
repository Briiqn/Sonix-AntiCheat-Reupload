package me.sonix.utils.versionutils;

import org.bukkit.entity.Player;

public interface VersionInstance {
    ClientVersion getClientVersion(Player player);
}