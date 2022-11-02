package me.sonix.commands.subcommands;

import me.sonix.Main;
import me.sonix.commands.SubCommand;
import me.sonix.enums.MsgType;
import me.sonix.enums.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AlertsCommand extends SubCommand {

    private final Main plugin;

    public AlertsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "alerts";
    }

    @Override
    protected String getDescription() {
        return "Toggle the alerts";
    }

    @Override
    protected String getSyntax() {
        return "alerts";
    }

    @Override
    protected String getPermission() {
        return Permissions.COMMAND_ALERTS.getPermission();
    }

    @Override
    protected int maxArguments() {
        return 1;
    }

    @Override
    protected boolean canConsoleExecute() {
        return false;
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {

        final UUID uuid = ((Player) sender).getUniqueId();

        if (this.plugin.getAlertManager().hasAlerts(uuid)) {

            this.plugin.getAlertManager().removePlayerFromAlerts(uuid);

            sender.sendMessage(MsgType.PREFIX.getMessage() + "You have disabled the Alerts");

        } else {

            this.plugin.getAlertManager().addPlayerToAlerts(uuid);

            sender.sendMessage(MsgType.PREFIX.getMessage() + "You have enabled the Alerts");
        }
    }
}