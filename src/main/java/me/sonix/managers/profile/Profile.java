package me.sonix.managers.profile;

import me.sonix.Main;
import me.sonix.enums.Permissions;
import me.sonix.files.Config;
import me.sonix.managers.threads.ProfileThread;
import me.sonix.processors.Packet;
import me.sonix.utils.ChatUtils;
import me.sonix.utils.TaskUtils;
import me.sonix.utils.custom.Exempt;
import me.sonix.playerdata.data.impl.ActionData;
import me.sonix.playerdata.data.impl.CombatData;
import me.sonix.playerdata.data.impl.ConnectionData;
import me.sonix.playerdata.data.impl.MovementData;
import me.sonix.playerdata.data.impl.RotationData;
import me.sonix.playerdata.data.impl.TeleportData;
import me.sonix.playerdata.data.impl.VehicleData;
import me.sonix.playerdata.data.impl.VelocityData;
import me.sonix.utils.custom.CheckHolder;
import me.sonix.utils.versionutils.ClientVersion;
import me.sonix.utils.versionutils.VersionUtils;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * A profile class containing every single information we need
 */
public class Profile {

    //-------------------------------------------
    private final ActionData actionData;
    private final CombatData combatData;
    private final ConnectionData connectionData;
    private final MovementData movementData;
    private final RotationData rotationData;
    private final TeleportData teleportData;
    private final VelocityData velocityData;
    private final VehicleData vehicleData;
    //-------------------------------------------

    //--------------------------------------
    private final CheckHolder checkHolder;
    //--------------------------------------

    //--------------------------------------
    private final ClientVersion version;
    private String client = "Unknown";
    public boolean bypass;
    //--------------------------------------

    //------------------------------------------
    private final ProfileThread profileThread;
    private final Player player;
    private final UUID uuid;

    private int playerPing;

    public int speedLevel;
    public int slowLevel;
    //------------------------------------------

    //---------------------------
    private final Exempt exempt;
    //---------------------------

    public Profile(Player player) {

        //Player Object
        this.player = player;

        //UUID
        this.uuid = player.getUniqueId();

        //Version
        this.version = VersionUtils.getClientVersion(player);


        //Data
        this.actionData = new ActionData(this);
        this.combatData = new CombatData();
        this.connectionData = new ConnectionData();
        this.movementData = new MovementData(this);
        this.rotationData = new RotationData(this);
        this.teleportData = new TeleportData();
        this.velocityData = new VelocityData();
        this.vehicleData = new VehicleData();

        //Check Holder
        this.checkHolder = new CheckHolder(this);

        //Exempt
        this.exempt = new Exempt(this);

        //Thread
        this.profileThread = Main.getInstance().getThreadManager().getAvailableProfileThread();

        //Initialize Checks
        reloadChecks();
    }


    public float getPotionEffectLevel(Player player, PotionEffectType pet) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (!pe.getType().getName().equals(pet.getName())) continue;
            return pe.getAmplifier() + 1;
        }
        return 0;
    }

    public float getBaseSpeed(final Player player, final float base) {
        return base + (getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }


    public boolean isBypassing() {
        return bypass;
    }

    public void handle(Packet packet) {

        if (this.player == null) return;

        this.connectionData.process(packet);
        this.actionData.process(packet);
        this.combatData.process(packet);
        this.movementData.process(packet);
        this.rotationData.process(packet);
        this.teleportData.process(packet);
        this.velocityData.process(packet);
        this.vehicleData.process(packet);

        if (skip(packet.getTimeStamp())) return;

        this.exempt.handleExempts(packet.getTimeStamp());

        this.checkHolder.runChecks(packet);
        this.speedLevel = (int) getPotionEffectLevel(player, PotionEffectType.SPEED);
        this.slowLevel = (int) getPotionEffectLevel(player, PotionEffectType.SLOW);
        this.playerPing = ((CraftPlayer) player).getHandle().ping;
    }

    public void kick(String reason) {

        if (this.player == null) return;

        TaskUtils.task(() -> this.player.kickPlayer(ChatUtils.format(reason)));
    }

    public ClientVersion getVersion() {
        return version;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Player getPlayer() {
        return this.player;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void reloadChecks() {
        this.checkHolder.registerAll();
    }

    private boolean skip(long currentTime) {

        //You can add more conditions here
        return this.bypass;
    }

    public void handleTick(long currentTime) {
        //Handle the tick here
    }

    public TeleportData getTeleportData() {
        return teleportData;
    }

    public ActionData getActionData() {
        return actionData;
    }

    public CombatData getCombatData() {
        return combatData;
    }

    public ConnectionData getConnectionData() {
        return connectionData;
    }

    public MovementData getMovementData() {
        return movementData;
    }

    public RotationData getRotationData() {
        return rotationData;
    }

    public VelocityData getVelocityData() {
        return velocityData;
    }

    public VehicleData getVehicleData() {
        return vehicleData;
    }

    public CheckHolder getCheckHolder() {
        return checkHolder;
    }

    public Exempt isExempt() {
        return exempt;
    }

    public int getPing(){
        return this.playerPing;
    }


    public ProfileThread getProfileThread() {
        return profileThread;
    }
}