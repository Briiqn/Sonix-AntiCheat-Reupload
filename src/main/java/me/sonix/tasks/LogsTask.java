package me.sonix.tasks;

import me.sonix.Main;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A task that we'll be using in order to process our logs.
 */
public class LogsTask extends BukkitRunnable {

    private final Main plugin;

    public LogsTask(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        if (this.plugin.getLogManager().isLogging() || this.plugin.getLogManager().getLogsQueue().isEmpty()) return;

        this.plugin.getLogManager().getLogExporter().logMultiple(this.plugin.getLogManager().getLogsQueue());

        this.plugin.getLogManager().clearQueuedLogs();

        this.plugin.getLogManager().setLogging(false);
    }
}