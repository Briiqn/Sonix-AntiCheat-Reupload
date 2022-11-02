package me.sonix;

import com.comphenix.protocol.ProtocolLibrary;
import me.sonix.commands.CommandManager;
import me.sonix.commands.JoinEvent;
import me.sonix.commands.TestserverEvents;
import me.sonix.files.Checks;
import me.sonix.files.Config;
import me.sonix.files.commentedfiles.CommentedFileConfiguration;
import me.sonix.listeners.ClientBrandListener;
import me.sonix.listeners.ProfileListener;
import me.sonix.listeners.ViolationListener;
import me.sonix.managers.AlertManager;
import me.sonix.managers.logs.LogManager;
import me.sonix.managers.profile.ProfileManager;
import me.sonix.managers.themes.ThemeManager;
import me.sonix.managers.threads.ThreadManager;
import me.sonix.nms.NmsManager;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.processors.listeners.BukkitListener;
import me.sonix.processors.listeners.NetworkListener;
import me.sonix.tasks.LogsTask;
import me.sonix.tasks.TickTask;
import me.sonix.tasks.ViolationTask;
import me.sonix.utils.ChatUtils;
import me.sonix.utils.MiscUtils;
import me.sonix.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;

/**
 * A simple and efficient anticheat base
 *
 * @author Nik
 */
public class Main extends JavaPlugin implements Listener {

    private static Main instance;

    private Config configuration;
    private Checks checks;

    private ProfileManager profileManager;
    private final NmsManager nmsManager = new NmsManager();
    private LogManager logManager;
    private ThreadManager threadManager;

    private AlertManager alertManager;
    private ThemeManager themeManager;

    public TestserverEvents testserverEvents;

    @Override
    public void onEnable() {
        registerEvents();
        instance = this;
        testserverEvents = new TestserverEvents();
        //Initialize
        (this.configuration = new Config(this)).initialize();
        (this.checks = new Checks(this)).initialize();
        (this.profileManager = new ProfileManager()).initialize();
        (this.themeManager = new ThemeManager(this)).initialize();
        (this.logManager = new LogManager(this)).initialize();
        (this.threadManager = new ThreadManager(this)).initialize();
        (this.alertManager = new AlertManager()).initialize();
        testserverEvents.testServer = Config.Setting.TESTSERVER.getBoolean();
        //Tasks
        new TickTask(this).runTaskTimerAsynchronously(this, 50L, 0L);

        if (Config.Setting.LOGS_ENABLED.getBoolean()) {
            new LogsTask(this).runTaskTimerAsynchronously(this, 6000L, 6000L);
        }

        new ViolationTask(this).runTaskTimerAsynchronously(this,
                Config.Setting.CHECK_SETTINGS_VIOLATION_RESET_INTERVAL.getLong() * 1200L,
                Config.Setting.CHECK_SETTINGS_VIOLATION_RESET_INTERVAL.getLong() * 1200L);


        //Packet Listeners
        Arrays.asList(
                new NetworkListener(this),
                new ClientBrandListener(this)
        ).forEach(packetListener -> ProtocolLibrary.getProtocolManager().addPacketListener(packetListener));

        //Bukkit Listeners
        Arrays.asList(
                new ProfileListener(this),
                new ViolationListener(this),
                new BukkitListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        //Load Commands
        Objects.requireNonNull(getCommand("sonix")).setExecutor(new CommandManager(this));

        //We're most likely going to be using transactions - ping pongs, So we need to do this for ViaVersion
        System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");

        //Initialize static variables to make sure our threads won't get affected when they run for the first time.
        try {

            MiscUtils.initializeClasses(
                    "me.sonix.utils.fastmath.FastMath",
                    "me.sonix.utils.fastmath.NumbersUtils",
                    "me.sonix.utils.fastmath.FastMathLiteralArrays",
                    "me.sonix.utils.minecraft.MathHelper",
                    "me.sonix.utils.CollisionUtils",
                    "me.sonix.utils.MoveUtils"
            );

        } catch (ClassNotFoundException e) {

            //Impossible unless we made a mistake
            ChatUtils.log("An error was thrown during initialization, Sonix may not work properly.");

            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

        //Shutdown all managers
        this.configuration.shutdown();
        this.checks.shutdown();
        this.profileManager.shutdown();
        this.alertManager.shutdown();
        this.threadManager.shutdown();
        this.themeManager.shutdown();

        //Clear reflection cache
        ReflectionUtils.clear();

        //Unregister any listeners
        HandlerList.unregisterAll((Listener) this);
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);

        //Cancel all tasks
        Bukkit.getScheduler().cancelTasks(this);

        instance = null;
    }
    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new TestserverEvents(), this);
        getServer().getPluginManager().registerEvents(new VelocityData(), this);
    }
    public CommentedFileConfiguration getConfiguration() {
        return this.configuration.getConfig();
    }

    public CommentedFileConfiguration getChecks() {
        return this.checks.getConfig();
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public ThreadManager getThreadManager() {
        return threadManager;
    }

    public AlertManager getAlertManager() {
        return alertManager;
    }

    public NmsManager getNmsManager() {
        return nmsManager;
    }

    public static Main getInstance() {
        return instance;
    }

}