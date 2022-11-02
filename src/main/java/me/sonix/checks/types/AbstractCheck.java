package me.sonix.checks.types;

import me.sonix.Main;
import me.sonix.api.events.AnticheatViolationEvent;
import me.sonix.checks.annotations.Development;
import me.sonix.checks.annotations.Experimental;
import me.sonix.checks.enums.CheckCategory;
import me.sonix.checks.enums.CheckType;
import me.sonix.commands.Util;
import me.sonix.files.Config;
import me.sonix.files.commentedfiles.CommentedFileConfiguration;
import me.sonix.managers.profile.Profile;
import me.sonix.utils.BetterStream;
import me.sonix.utils.MiscUtils;
import me.sonix.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractCheck {

    protected final Profile profile;

    private final boolean enabled;

    private final Set<String> commands = new LinkedHashSet<>();

    private final String checkName, checkType, fullCheckName, description;
    private final boolean experimental;
    private final CheckCategory checkCategory;
    private final boolean development;
    private int vl;
    private int maxVl;
    private float buffer;
    private String verbose; //TODO: USE STRINGBUILDER

    public AbstractCheck(Profile profile, CheckType check, String type, String description) {

        this.profile = profile;
        this.checkName = check.getCheckName();
        this.checkType = type;
        this.description = description;

        final CommentedFileConfiguration config = Main.getInstance().getChecks();
        final String checkName = this.checkName.toLowerCase();
        final String checkType = type.toLowerCase().replace(" ", "_");

        Main.getInstance().testserverEvents.testServer = Config.Setting.TESTSERVER.getBoolean();

        this.enabled = type.isEmpty()
                ? config.getBoolean(checkName + ".enabled")
                : config.getBoolean(checkName + "." + checkType + ".enabled", config.getBoolean(checkName + "." + checkType));

        this.maxVl = config.getInt(checkName + ".max_vl");

        /*
        This is null inside GUI's
         */
        if (profile != null) {
            this.commands.addAll(
                    BetterStream.applyAndGet(config.getStringList(checkName + ".commands"),
                            command -> command.replace("%player%", profile.getPlayer().getName())
                    )
            );

        }

        Class<? extends AbstractCheck> clazz = this.getClass();

        this.experimental = clazz.isAnnotationPresent(Experimental.class);

        this.development = clazz.isAnnotationPresent(Development.class);

        this.checkCategory = check.getCheckCategory();

        this.fullCheckName = this.checkName + (type.isEmpty() ? "" : (" (" + type + ")"));
    }

    public String getVerbose() {
        return verbose;
    }

    public String getFullCheckName() {
        return fullCheckName;
    }

    protected void debug(Object info) {
        Bukkit.broadcastMessage(String.valueOf(info));
    }

    public void fail(String verbose) {

        this.verbose = verbose;

        fail();
    }

    public void fail() {

        //Development
        if (this.development) return;

        //Just to make sure
        if (this.vl < 1) this.vl = 1;

        final Player p = profile.getPlayer();

        if (p == null) return;

        AnticheatViolationEvent violationEvent = new AnticheatViolationEvent(
                p,
                this.checkName,
                this.description,
                this.checkType,
                verbose,
                //Increase the violations here
                this.vl++,
                this.maxVl,
                this.experimental);

        Bukkit.getPluginManager().callEvent(violationEvent);

        if (violationEvent.isCancelled()) {

            this.vl--;

            return;
        }
        if (Main.getInstance().testserverEvents.testServer){
            maxVl = 50;
        }
        else {
            final CommentedFileConfiguration config = Main.getInstance().getChecks();
            this.maxVl = config.getInt(checkName + ".max_vl");
        }
        if (this.vl > this.maxVl) {
            this.vl = 1;
            this.buffer = 0;
            if (Main.getInstance().testserverEvents.testServer) {
                Main.getInstance().testserverEvents.antiCheatban(p);
                TaskUtils.task(() -> p.kickPlayer("Detected L"));
                Bukkit.broadcastMessage(Util.translate("&7&m----------------------------------------------------\n&c&l✗ &b&l"+p.getName()+" &7&lhas been removed by &c&lSonix &7&lfor &c&lCheating&7&l.\n&m------------------------------------------------"));
            }
            else {
                MiscUtils.consoleCommand(this.commands);
            }
        }
    }

    public CheckCategory getCategory() {
        return checkCategory;
    }

    public void resetVl() {
        this.vl = 1;
    }

    public int getVl() {
        return this.vl;
    }

    public void setVl(int vl) {
        this.vl = vl;
    }

    protected float increaseBuffer() {
        return this.buffer++;
    }

    protected float increaseBufferBy(double amount) {
        return this.buffer += amount;
    }

    protected float decreaseBuffer() {
        return this.buffer == 0 ? 0 : (this.buffer = Math.max(0, this.buffer - 1));
    }

    protected float decreaseBufferBy(double amount) {
        return this.buffer == 0 ? 0 : (this.buffer = (float) Math.max(0, this.buffer - amount));
    }

    public void resetBuffer() {
        this.buffer = 0;
    }

    protected float getBuffer() {
        return this.buffer;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getCheckName() {
        return this.checkName;
    }

    public String getCheckType() {
        return this.checkType;
    }

    public String getDescription() {
        return this.description;
    }
}