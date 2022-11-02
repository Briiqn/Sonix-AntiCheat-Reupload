package me.sonix.playerdata.data.impl;

import me.sonix.Main;
import me.sonix.managers.profile.Profile;
import me.sonix.playerdata.data.Data;
import me.sonix.processors.Packet;
import me.sonix.utils.MiscUtils;
import me.sonix.utils.custom.PlacedBlock;
import me.sonix.utils.custom.desync.Desync;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ActionData implements Data {

    private final GameMode gameMode;

    private final boolean allowFlight;
    private boolean sneaking;
    private final boolean sprinting;

    private final Desync desync;

    private PlacedBlock placedBlock;
    private boolean isSwinging;
    private final ItemStack itemInMainHand = MiscUtils.EMPTY_ITEM;
    private final ItemStack itemInOffHand = MiscUtils.EMPTY_ITEM;

    private final int lastAllowFlightTicks;
    private int lastSleepingTicks;
    private int lastRidingTicks;


    /*
     * 1.9+
     */
    private int lastDuplicateOnePointSeventeenPacketTicks = 100;

    public ActionData(Profile profile) {

        this.desync = new Desync(profile);

        //Initialize

        Player player = profile.getPlayer();

        this.gameMode = player.getGameMode();

        this.allowFlight = Main.getInstance().getNmsManager().getNmsInstance().getAllowFlight(player);

        this.sprinting = player.isSprinting();

        this.lastAllowFlightTicks = this.allowFlight ? 0 : 100;


    }

    @Override
    public void process(Packet packet) {
        /*
        Handle the packet
         */
    }

    public int getLastRidingTicks() {
        return lastRidingTicks;
    }

    public PlacedBlock getPlacedBlock() {
        return placedBlock;
    }

    public boolean isSneaking() {
        return sneaking;
    }
    public boolean isSprinting() {
        return sprinting;
    }


    public ItemStack getItemInMainHand() {
        return itemInMainHand;
    }

    public ItemStack getItemInOffHand() {
        return itemInOffHand;
    }

    public Desync getDesync() {
        return desync;
    }

    public int getLastSleepingTicks() {
        return lastSleepingTicks;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int lastAllowFly() {
        return lastAllowFlightTicks;
    }

    public boolean allowFly() {
        return allowFlight;
    }

    public int getLastDuplicateOnePointSeventeenPacketTicks() {
        return lastDuplicateOnePointSeventeenPacketTicks;
    }


}