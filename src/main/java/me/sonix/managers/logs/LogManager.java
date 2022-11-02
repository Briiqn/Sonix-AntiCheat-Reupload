package me.sonix.managers.logs;

import me.sonix.Main;
import me.sonix.files.Config;
import me.sonix.managers.logs.impl.FileExporter;
import me.sonix.managers.Initializer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogManager implements Initializer {

    private final Queue<PlayerLog> logsQueue = new ConcurrentLinkedQueue<>();

    private final LogExporter logExporter;

    private boolean logging;

    public LogManager(Main plugin) {

        switch (Config.Setting.LOGS_TYPE.getString().toLowerCase()) {
            /*case "mysql":

                this.logExporter = new MySQLExporter(plugin);

                break;

            case "sqlite":

                this.logExporter = new SQLiteExporter(plugin);

                break;*/

            default:

                this.logExporter = new FileExporter(plugin);

                break;
        }
    }

    @Override
    public void initialize() {
        this.logExporter.initialize();
    }

    public Queue<PlayerLog> getLogsQueue() {
        return this.logsQueue;
    }

    public void addLogToQueue(PlayerLog playerLog) {

        if (!Config.Setting.LOGS_ENABLED.getBoolean()) return;

        this.logsQueue.add(playerLog);
    }

    public void clearQueuedLogs() {
        this.logsQueue.clear();
    }

    public LogExporter getLogExporter() {
        return this.logExporter;
    }

    public boolean isLogging() {
        return this.logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    @Override
    public void shutdown() {
        this.logsQueue.clear();
        this.logExporter.shutdown();
    }
}