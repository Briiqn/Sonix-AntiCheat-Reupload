package me.sonix.managers.themes.impl;

import me.sonix.Main;
import me.sonix.managers.themes.BaseTheme;

import java.util.Arrays;

public class DefaultTheme extends BaseTheme {
    public DefaultTheme(Main plugin, String themeName) {
        super(plugin, themeName);
    }

    @Override
    public void create() {
        get().addDefault("prefix", "&8[&c&l⚠&8] &c");
        get().addDefault("no_perm", "&cYou do not have permission to do that!");
        get().addDefault("console_commands", "&c&lYou cannot run this command through the console :(");
        get().addDefault("alert_message", "&c%player% &7failed &c%check% &8[&7x&c%vl%&8, &7Ping: &c%ping%&8, &7TPS: &c%tps%&8]");
        get().addDefault("alert_hover",
                Arrays.asList(
                        "&c* %description%",
                        "%information%",
                        "(Ping: %ping% TPS: %tps%) &7(Click to teleport)"
                ));
    }
}