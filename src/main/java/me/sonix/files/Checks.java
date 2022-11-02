package me.sonix.files;

import me.sonix.files.commentedfiles.CommentedFileConfiguration;
import me.sonix.Main;
import me.sonix.managers.Initializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Checks implements Initializer {

    private static final String[] HEADER = new String[]{
            "+----------------------------------------------------------------------------------------------+",
            "|                                                                                              |",
            "|                                            Sonix                                             |",
            "|                                                                                              |",
            "|                          Website: https://www.youranticheatwebsite.com                       |",
            "|                                                                                              |",
            "|                                         Author: Stelios                                      |",
            "|                                                                                              |",
            "+----------------------------------------------------------------------------------------------+"
    };

    private final JavaPlugin plugin;
    private CommentedFileConfiguration configuration;
    private static boolean exists;

    public Checks(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @return the config.yml as a CommentedFileConfiguration
     */
    public CommentedFileConfiguration getConfig() {
        return this.configuration;
    }

    @Override
    public void initialize() {

        File configFile = new File(this.plugin.getDataFolder(), "checks.yml");

        exists = configFile.exists();

        boolean setHeaderFooter = !exists;

        boolean changed = setHeaderFooter;

        this.configuration = CommentedFileConfiguration.loadConfiguration(this.plugin, configFile);

        if (setHeaderFooter) this.configuration.addComments(HEADER);

        for (Setting setting : Setting.values()) {

            setting.reset();

            changed |= setting.setIfNotExists(this.configuration);
        }

        if (changed) this.configuration.save();
    }

    @Override
    public void shutdown() {
        for (Setting setting : Setting.values()) setting.reset();
    }

    public enum Setting {
        SPEED("speed", "", "Movement Checks"),
        SPEED_A("speed.a", true, "Should we enable this module?"),
        SPEED_B("speed.b", true, "Should we enable this module?"),
        SPEED_C("speed.c", true, "Should we enable this module?"),
        SPEED_D("speed.d", true, "Should we enable this module?"),
        SPEED_E("speed.e", true, "Should we enable this module?"),
        SPEED_MAX_VL("speed.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        SPEED_COMMANDS("speed.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        AIM("aim", "", "Aim Checks"),
        AIM_A("aim.a", true, "Should we enable this module?"),
        AIM_B("aim.b", true, "Should we enable this module?"),
        AIM_MAX_VL("aim.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        AIM_COMMANDS("aim.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        KILLAURA("killaura", "", "Killaura Checks"),
        KILLAURA_A("killaura.a", true, "Should we enable this module?"),
        KILLAURA_B("killaura.b", true, "Should we enable this module?"),
        KILLAURA_C("killaura.c", true, "Should we enable this module?"),
        KILLAURA_MAX_VL("killaura.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        KILLAURA_COMMANDS("killaura.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        FLY("fly", "", "Fly Checks"),
        FLY_A("fly.a", true, "Should we enable this module?"),
        FLY_B("fly.b", true, "Should we enable this module?"),
        FLY_C("fly.c", true, "Should we enable this module?"),
        FLY_D("fly.d", true, "Should we enable this module?"),
        FLY_MAX_VL("fly.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        FLY_COMMANDS("fly.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        TIMER("timer", "", "Timer Checks"),
        TIMER_A("timer.a", true, "Should we enable this module?"),
        TIMER_MAX_VL("timer.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        TIMER_COMMANDS("timer.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        BADPACKETS("badpackets", "", "Badpackets Checks"),
        BADPACKETS_A("badpackets.a", true, "Should we enable this module?"),
        BADPACKETS_B("badpackets.b", true, "Should we enable this module?"),
        BADPACKETS_C("badpackets.c", true, "Should we enable this module?"),
        BADPACKETS_MAX_VL("badpackets.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        BADPACKETS_COMMANDS("badpackets.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        REACH("reach", "", "Reach Checks"),
        REACH_A("reach.a", true, "Should we enable this module?"),
        REACH_MAX_VL("reach.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        REACH_COMMANDS("reach.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        FASTPLACE("fastplace", "", "Fastplace Checks"),
        FASTPLACE_A("fastplace.a", true, "Should we enable this module?"),
        FASTPLACE_MAX_VL("fastplace.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        FASTPLACE_COMMANDS("fastplace.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        INVENTORY("inventory", "", "Inventory Checks"),
        INVENTORY_A("inventory.a", true, "Should we enable this module?"),
        INVENTORY_MAX_VL("inventory.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        INVENTORY_COMMANDS("inventory.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        AUTOCLICKER("autoclicker", "", "Autoclicker Checks"),
        AUTOCLICKER_A("autoclicker.a", true, "Should we enable this module?"),
        AUTOCLICKER_MAX_VL("autoclicker.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        AUTOCLICKER_COMMANDS("autoclicker.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        GROUND("ground", "", "Ground Checks"),
        GROUND_A("ground.a", true, "Should we enable this module?"),
        GROUND_B("ground.b", true, "Should we enable this module?"),
        GROUND_MAX_VL("ground.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        GROUND_COMMANDS("ground.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        NOSLOW("noslow", "", "NoSlow Checks"),
        NOSLOW_A("noslow.a", true, "Should we enable this module?"),
        NOSLOW_MAX_VL("noslow.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        NOSLOW_COMMANDS("noslow.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount"),
        STRAFE("strafe", "", "Strafe Checks"),
        STRAFE_A("strafe.a", true, "Should we enable this module?"),
        STRAFE_MAX_VL("strafe.max_vl", 10, "The maximum violation amount a player needs to reach in order to get punished"),
        STRAFE_COMMANDS("strafe.commands", Collections.singletonList("ban %player% Unfair Advantage"), "The commands that will get executed once a player reaches the maximum violation amount");

        private final String key;
        private final Object defaultValue;
        private boolean excluded;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        Setting(String key, Object defaultValue, boolean excluded, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
            this.excluded = excluded;
        }

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            this.loadValue();
            return (boolean) this.value;
        }

        public String getKey() {
            return this.key;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            this.loadValue();
            return (int) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            this.loadValue();
            return (long) this.getNumber();
        }

        /**
         * @return the setting as a double
         */
        public double getDouble() {
            this.loadValue();
            return this.getNumber();
        }

        /**
         * @return the setting as a float
         */
        public float getFloat() {
            this.loadValue();
            return (float) this.getNumber();
        }

        /**
         * @return the setting as a String
         */
        public String getString() {
            this.loadValue();
            return String.valueOf(this.value);
        }

        private double getNumber() {
            if (this.value instanceof Integer) {
                return (int) this.value;
            } else if (this.value instanceof Short) {
                return (short) this.value;
            } else if (this.value instanceof Byte) {
                return (byte) this.value;
            } else if (this.value instanceof Float) {
                return (float) this.value;
            }

            return (double) this.value;
        }

        /**
         * @return the setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            this.loadValue();
            return (List<String>) this.value;
        }

        private boolean setIfNotExists(CommentedFileConfiguration fileConfiguration) {
            this.loadValue();

            if (exists && this.excluded) return false;

            if (fileConfiguration.get(this.key) == null) {
                List<String> comments = Stream.of(this.comments).collect(Collectors.toList());
                if (this.defaultValue != null) {
                    fileConfiguration.set(this.key, this.defaultValue, comments.toArray(new String[0]));
                } else {
                    fileConfiguration.addComments(comments.toArray(new String[0]));
                }

                return true;
            }

            return false;
        }

        /**
         * Resets the cached value
         */
        public void reset() {
            this.value = null;
        }

        /**
         * @return true if this setting is only a section and doesn't contain an actual value
         */
        public boolean isSection() {
            return this.defaultValue == null;
        }

        /**
         * Loads the value from the config and caches it if it isn't set yet
         */
        private void loadValue() {
            if (this.value != null) return;
            this.value = Main.getInstance().getConfiguration().get(this.key);
        }
    }
}