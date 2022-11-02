package me.sonix.tasks;

import me.sonix.Main;
import me.sonix.checks.types.Check;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A task that we'll be using in order to clear the profile violations.
 */
public class ViolationTask extends BukkitRunnable {

    private final Main plugin;

    public ViolationTask(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getProfileManager().getProfileMap().values().forEach(profile -> {
            for (Check check : profile.getCheckHolder().getChecks()) check.resetVl();
        });
    }
}