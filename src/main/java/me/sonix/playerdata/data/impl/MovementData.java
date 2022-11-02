package me.sonix.playerdata.data.impl;

import me.sonix.Main;
import me.sonix.managers.profile.Profile;
import me.sonix.nms.NmsInstance;
import me.sonix.playerdata.data.Data;
import me.sonix.playerdata.processors.impl.GhostblockProcessor;
import me.sonix.processors.Packet;
import me.sonix.utils.CollisionUtils;
import me.sonix.utils.MoveUtils;
import me.sonix.utils.custom.BoundingBox;
import me.sonix.utils.custom.CustomLocation;
import me.sonix.utils.custom.Equipment;
import me.sonix.utils.fastmath.FastMath;
import me.sonix.wrappers.WrapperPlayClientLook;
import me.sonix.wrappers.WrapperPlayClientPosition;
import me.sonix.wrappers.WrapperPlayClientPositionLook;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.*;

public class MovementData implements Data {

    private final Profile profile;
    public long lastFlying;
    private final Equipment equipment;

    private double x, y, z, lastX, lastY, lastZ;
    private double deltaX, lastDeltaX, deltaZ, lastDeltaZ, deltaY, lastDeltaY, deltaXZ, lastDeltaXZ,
            accelXZ, lastAccelXZ, accelY, lastAccelY;

    private float fallDistance, lastFallDistance,
            baseGroundSpeed, baseAirSpeed,
            frictionFactor = MoveUtils.FRICTION_FACTOR, lastFrictionFactor = MoveUtils.FRICTION_FACTOR;

    private CustomLocation location, lastLocation;

    private final List<Material> nearbyBlocks = new ArrayList<>();

    private boolean onGround;
    private boolean lastOnGround;
    private boolean serverGround;
    private boolean lastServerGround;

    private boolean inVehicle;

    private boolean isFlying, inAir, inAirOneBlock;
    public GhostblockProcessor ghostblockProcessor;
    private boolean inWeb;
    public int speedThreshold;
    public float flyThreshold;
    private int flyTicks, serverGroundTicks, lastServerGroundTicks, nearGroundTicks, lastNearGroundTicks,
            lastUnloadedChunkTicks = 100,
            clientGroundTicks, lastNearWallTicks,
            lastFrictionFactorUpdateTicks, lastNearEdgeTicks;

    public boolean flying, inLiquid, inWater, inLava,  fullySubmergedInLiquidStat,
            blockNearHead, nearWall, onClimbable, onSolidGround, nearVehicle, onSlime,
            onIce, nearPiston, nearStair, pos, lastPos;

    private final List<Block> blocks = new ArrayList<>();
    private final List<Block> blocksNear = new ArrayList<>();
    private final List<Block> blocksBelow = new ArrayList<>();
    private final List<Block> blocksAbove = new ArrayList<>();

    private final Deque<Vector> teleportList = new ArrayDeque<>();

    public int airTicks, clientAirTicks, sinceVehicleTicks, sinceFlyingTicks,
            liquidTicks, sinceLiquidTicks, climbableTicks, sinceClimbableTicks,
            webTicks, sinceWebTicks, ticks, sinceSpeedTicks,
            groundTicks, sinceTeleportTicks, sinceSlimeTicks, solidGroundTicks,
            iceTicks, sinceIceTicks, sinceBlockNearHeadTicks, sinceNearVehicleTicks;


    public MovementData(Profile profile) {
        this.profile = profile;

        this.equipment = new Equipment();

        this.ghostblockProcessor = new GhostblockProcessor(profile);
        this.location = this.lastLocation = new CustomLocation(profile.getPlayer().getLocation());
    }

    @Override
    public void process(Packet packet) {

        final World world = profile.getPlayer().getWorld();
        final long currentTime = packet.getTimeStamp();
        this.inVehicle = Main.getInstance().getNmsManager().getNmsInstance().isInsideVehicle(profile.getPlayer());
        this.isFlying = Main.getInstance().getNmsManager().getNmsInstance().isFlying(profile.getPlayer());
        switch (packet.getType()) {

            case POSITION:

                final WrapperPlayClientPosition move = packet.getPositionWrapper();

                this.lastOnGround = this.onGround;
                this.onGround = move.getOnGround();

                this.flyTicks = this.onGround ? 0 : this.flyTicks + 1;
                this.clientGroundTicks = this.onGround ? this.clientGroundTicks + 1 : 0;

                this.lastLocation = this.location;
                this.location = new CustomLocation(
                        world,
                        move.getX(), move.getY(), move.getZ(),
                        this.location.getYaw(), this.location.getPitch(),
                        currentTime
                );

                this.inLiquid = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.STATIONARY_WATER || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.WATER || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.LAVA || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.STATIONARY_LAVA;
                this.onClimbable = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.LADDER || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.VINE;
                this.onSlime = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.SLIME_BLOCK;
                this.inWeb = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.WEB;


                processLocationData();


                break;

            case POSITION_LOOK:

                //1.17+
                if (profile.getActionData().getLastDuplicateOnePointSeventeenPacketTicks() == 0) break;

                final WrapperPlayClientPositionLook posLook = packet.getPositionLookWrapper();

                this.lastOnGround = this.onGround;
                this.onGround = posLook.getOnGround();

                this.flyTicks = this.onGround ? 0 : this.flyTicks + 1;
                this.clientGroundTicks = this.onGround ? this.clientGroundTicks + 1 : 0;

                this.lastLocation = this.location;
                this.location = new CustomLocation(
                        world,
                        posLook.getX(), posLook.getY(), posLook.getZ(),
                        posLook.getYaw(), posLook.getPitch(),
                        currentTime
                );

                this.inLiquid = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.STATIONARY_WATER || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.WATER || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.LAVA || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.STATIONARY_LAVA;
                this.onClimbable = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.LADDER || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.VINE;
                this.onSlime = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.SLIME_BLOCK;
                this.inWeb = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.WEB;

                processLocationData();

                break;

            case LOOK:

                final WrapperPlayClientLook look = packet.getLookWrapper();

                this.lastOnGround = this.onGround;
                this.onGround = look.getOnGround();

                this.flyTicks = this.onGround ? 0 : this.flyTicks + 1;
                this.clientGroundTicks = this.onGround ? this.clientGroundTicks + 1 : 0;

                this.lastLocation = this.location;
                this.location = new CustomLocation(
                        world,
                        this.location.getX(), this.location.getY(), this.location.getZ(),
                        look.getYaw(), look.getPitch(),
                        currentTime
                );

                this.inLiquid = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.STATIONARY_WATER || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.WATER || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.LAVA || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.STATIONARY_LAVA;
                this.onClimbable = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.LADDER || profile.getPlayer().getLocation().clone().getBlock().getType() == Material.VINE;
                this.onSlime = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.SLIME_BLOCK;
                this.inWeb = profile.getPlayer().getLocation().clone().getBlock().getType() == Material.WEB;

                processLocationData();

                break;
        }
    }

    public void handleCollisions(final int type) {
        blocks.clear();
        blocksNear.clear();

        final BoundingBox boundingBox = new BoundingBox(profile);

        switch (type) {
            case 0:
                boundingBox.expand( 0, 0, 0.55, 0.6, 0, 0);
                break;
            case 1:
                boundingBox.expand(0.1, 0.1, 0.55, 0.6, 0.1, 0.1);
                break;
        }

        final double minX = boundingBox.getMinX();
        final double minY = boundingBox.getMinY();
        final double minZ = boundingBox.getMinZ();
        final double maxX = boundingBox.getMaxX();
        final double maxY = boundingBox.getMaxY();
        final double maxZ = boundingBox.getMaxZ();

        for (double x = minX; x <= maxX; x += (maxX - minX)) {
            for (double y = minY; y <= maxY + 0.01; y += (maxY - minY) / 4) { //Expand max by 0.01 to compensate shortly for precision issues due to FP.
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    final CustomLocation location = new CustomLocation(profile.getPlayer().getWorld(), x, y, z);
                    final Block block = CollisionUtils.getBlock(location, false);

                    if (block != null) {
                        switch (type) {
                            case 0:
                                blocks.add(block);
                                break;
                            case 1:
                                blocksNear.add(block);
                                break;
                        }
                    }
                }
            }
        }

        switch (type) {
            case 0:
                MovementData data = profile.getMovementData();
                handleClimbableCollision();
                handleNearbyEntities();

                inLiquid = fullySubmergedInLiquidStat = inWater = inLava = inWeb = onIce =
                        onSolidGround = nearStair = blockNearHead = nearWall = onSlime = nearPiston = false;

                fullySubmergedInLiquidStat = true;
                inAir = true;

                blocksAbove.clear();
                blocksBelow.clear();

                for (final Block block : blocks) {
                    final Material material = block.getType();

                    inLiquid |= block.isLiquid();
                    inWater |= material == Material.WATER ||  material == Material.STATIONARY_WATER;
                    inLava |= material == Material.LAVA || material == Material.STATIONARY_LAVA;
                    inWeb |= material == Material.WEB;
                    onIce |= material == Material.ICE || material == Material.PACKED_ICE;
                    onSolidGround |= material.isSolid();
                    nearStair |= material.toString().contains("STAIR");
                    blockNearHead |= block.getLocation().getBlockY() - data.getDeltaY() >= 0.9 && material != Material.AIR;
                    onSlime |= material == Material.SLIME_BLOCK;
                    nearPiston |= material == Material.PISTON_BASE
                            || material == Material.PISTON_EXTENSION
                            || material == Material.PISTON_MOVING_PIECE
                            || material == Material.PISTON_STICKY_BASE;

                    if (block.getLocation().getY() - data.getDeltaY() >= 1.0) blocksAbove.add(block);
                    if (block.getLocation().getY() - data.getDeltaY() < 0.0) blocksBelow.add(block);

                    if (material != Material.STATIONARY_WATER && material != Material.STATIONARY_LAVA) fullySubmergedInLiquidStat = false;
                    if (material != Material.AIR) inAir = false;
                }

                break;
            case 1:
                nearWall = false;

                for (final Block block : blocksNear) {
                    nearWall |= block.getType().isSolid();
                }

                break;
        }

    }
    private List<Entity> nearbyEntities = new ArrayList<>();

    public void handleNearbyEntities() {
        try {
            nearbyEntities = getEntitiesWithinRadius(profile.getPlayer().getLocation(), 2);

            if (nearbyEntities == null) {
                nearVehicle = false;
                return;
            }

            nearVehicle = false;

            for (final Entity nearbyEntity : nearbyEntities) {
                nearVehicle |= nearbyEntity instanceof Vehicle;
            }
        } catch (final Throwable t) {
            // I know stfu
        }
    }

    public List<Entity> getEntitiesWithinRadius(final Location location, final double radius) {
        try {
            final double expander = 16.0D;

            final double x = location.getX();
            final double z = location.getZ();

            final int minX = (int) Math.floor((x - radius) / expander);
            final int maxX = (int) Math.floor((x + radius) / expander);

            final int minZ = (int) Math.floor((z - radius) / expander);
            final int maxZ = (int) Math.floor((z + radius) / expander);

            final World world = location.getWorld();

            final List<Entity> entities = new LinkedList<>();

            for (int xVal = minX; xVal <= maxX; xVal++) {

                for (int zVal = minZ; zVal <= maxZ; zVal++) {

                    if (!world.isChunkLoaded(xVal, zVal)) continue;

                    for (final Entity entity : world.getChunkAt(xVal, zVal).getEntities()) {
                        //We have to do this due to stupidness
                        if (entity == null) break;

                        //Make sure the entity is within the radius specified
                        if (entity.getLocation().distanceSquared(location) > radius * radius) continue;

                        entities.add(entity);
                    }
                }
            }

            return entities;
        } catch (final Throwable t) {
            // I know stfu
        }

        return null;
    }


    public void handleClimbableCollision() {
        final int var1 = NumberConversions.floor(this.x);
        final int var2 = NumberConversions.floor(this.y);
        final int var3 = NumberConversions.floor(this.z);

        final Block var4 = this.getBlock(new Location(profile.getPlayer().getWorld(), var1, var2, var3));

        if (var4 != null) {
            this.onClimbable = var4.getType() == Material.LADDER || var4.getType() == Material.VINE;
        }
    }
    public Block getBlock(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getWorld().getBlockAt(location);
        } else {
            return null;
        }
    }


    public void handleTeleport(final WrapperPlayClientPosition wrapper) {
        final Vector requestedLocation = new Vector(
                wrapper.getX(),
                wrapper.getY(),
                wrapper.getZ()
        );

        teleportList.add(requestedLocation);
    }

    public void handleTicks() {
        ++ticks;

        if (onGround) ++groundTicks;
        else groundTicks = 0;

        if (profile.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
            sinceSpeedTicks = 0;
        } else {
            ++sinceSpeedTicks;
        }

        if (inAir) {
            ++airTicks;
        } else {
            airTicks = 0;
        }

        if (!onGround) {
            ++clientAirTicks;
        } else {
            clientAirTicks = 0;
        }

        ++sinceTeleportTicks;

        if (profile.getPlayer().getVehicle() != null) {
            sinceVehicleTicks = 0;
            inVehicle = true;
        } else {
            ++sinceVehicleTicks;
            inVehicle = false;
        }

        if (onIce) {
            ++iceTicks;
            sinceIceTicks = 0;
        } else {
            iceTicks = 0;
            ++sinceIceTicks;
        }

        if (onSolidGround) {
            ++solidGroundTicks;
        } else {
            solidGroundTicks = 0;
        }

        if (profile.getPlayer().isFlying()) {
            flying = true;
            sinceFlyingTicks = 0;
        } else {
            ++sinceFlyingTicks;
            flying = false;
        }

        if (onSlime) {
            sinceSlimeTicks = 0;
        } else {
            ++sinceSlimeTicks;
        }

        if (blockNearHead) {
            sinceBlockNearHeadTicks = 0;
        } else {
            ++sinceBlockNearHeadTicks;
        }

        if (inLiquid) {
            ++liquidTicks;
            sinceLiquidTicks = 0;
        } else {
            liquidTicks = 0;
            ++sinceLiquidTicks;
        }

        if (onClimbable) {
            ++climbableTicks;
            sinceClimbableTicks = 0;
        } else {
            climbableTicks = 0;
            ++sinceClimbableTicks;
        }

        if (inWeb) {
            ++webTicks;
            sinceWebTicks = 0;
        } else {
            webTicks = 0;
            ++sinceWebTicks;
        }

        if (nearVehicle) {
            sinceNearVehicleTicks = 0;
        } else {
            ++sinceNearVehicleTicks;
        }
    }

    private void processLocationData() {

        final double lastDeltaX = this.deltaX;
        final double deltaX = this.location.getX() - this.lastLocation.getX();

        this.lastDeltaX = lastDeltaX;
        this.deltaX = deltaX;

        final double lastDeltaY = this.deltaY;
        final double deltaY = this.location.getY() - this.lastLocation.getY();

        this.lastDeltaY = lastDeltaY;
        this.deltaY = deltaY;

        final double lastAccelY = this.accelY;
        final double accelY = Math.abs(lastDeltaY - deltaY);

        this.lastAccelY = lastAccelY;
        this.accelY = accelY;

        final double lastDeltaZ = this.deltaZ;
        final double deltaZ = this.location.getZ() - this.lastLocation.getZ();

        this.lastDeltaZ = lastDeltaZ;
        this.deltaZ = deltaZ;

        final double lastDeltaXZ = this.deltaXZ;
        final double deltaXZ = FastMath.hypot(deltaX, deltaZ);

        this.lastDeltaXZ = lastDeltaXZ;
        this.deltaXZ = deltaXZ;

        final double lastAccelXZ = this.accelXZ;
        final double accelXZ = Math.abs(lastDeltaXZ - deltaXZ);

        this.lastAccelXZ = lastAccelXZ;
        this.accelXZ = accelXZ;

        this.inAir = profile.getPlayer().getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR && profile.getPlayer().getLocation().clone().subtract(0, 1, 1).getBlock().getType() == Material.AIR && profile.getPlayer().getLocation().clone().subtract(1, 1, 0).getBlock().getType() == Material.AIR && profile.getPlayer().getLocation().clone().subtract(1, 1, 1).getBlock().getType() == Material.AIR && profile.getPlayer().getLocation().clone().subtract(0, 1, -1).getBlock().getType() == Material.AIR && profile.getPlayer().getLocation().clone().subtract(-1, 1, 0).getBlock().getType() == Material.AIR && profile.getPlayer().getLocation().clone().subtract(-1, 1, -1).getBlock().getType() == Material.AIR && profile.getPlayer().getLocation().clone().subtract(-1, 1, 1).getBlock().getType() == Material.AIR && profile.getPlayer().getLocation().clone().subtract(1, 1, -1).getBlock().getType() == Material.AIR;

        this.inAirOneBlock = profile.getPlayer().getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR;
        handleTicks();
//        handleCollisions(0);
//        handleCollisions(1);

        //Process data
        processPlayerData();
    }

    private void handleNearbyBlocks() {

        /*
        Get the nearby block result from the current location.
         */
        final CollisionUtils.NearbyBlocksResult nearbyBlocksResult = CollisionUtils.getNearbyBlocks(this.location, false);

        /*
        Handle collisions
        NOTE: You should ALWAYS use NMS if you plan on supporting 1.9+
        For a production server, DO NOT use spigot's api. It's slow. (Especially for Blocks, Chunks, Materials)
         */
    }

    private void processPlayerData() {
            final Player p = profile.getPlayer();

            NmsInstance nms = Main.getInstance().getNmsManager().getNmsInstance();

            //Chunk

            if ((this.lastUnloadedChunkTicks = nms.isChunkLoaded(
                    this.location.getWorld(), this.location.getBlockX(), this.location.getBlockZ())
                    ? this.lastUnloadedChunkTicks + 1 : 0) > 10) {

                //Nearby Entities

                handleNearbyEntities();

                //Nearby Blocks

                handleNearbyBlocks();

                //Friction Factor

                this.frictionFactor = CollisionUtils.getBlockSlipperiness(nms.getType(this.location.clone().subtract(0D, .825D, 0D).getBlock()));

                this.lastFrictionFactorUpdateTicks = this.frictionFactor != this.lastFrictionFactor ? 0 : this.lastFrictionFactorUpdateTicks + 1;

                this.lastFrictionFactor = this.frictionFactor;
            }

            this.lastNearWallTicks = CollisionUtils.isNearWall(this.location) ? 0 : this.lastNearWallTicks + 1;

            //Near Edge

            this.lastNearEdgeTicks = this.lastNearGroundTicks == 0 && CollisionUtils.isNearEdge(this.location) ? 0 : this.lastNearEdgeTicks + 1;

            //Server Ground

            final boolean lastServerGround = this.serverGround;

            final boolean serverGround = CollisionUtils.isServerGround(this.location.getY());
            this.lastServerGround = lastServerGround;

            this.serverGround = serverGround;

            this.serverGroundTicks = serverGround ? this.serverGroundTicks + 1 : 0;

            this.lastServerGroundTicks = serverGround ? 0 : this.lastServerGroundTicks + 1;

            //Equipment

            this.equipment.handle(p);

            //Fall Distance

            this.lastFallDistance = this.fallDistance;

            this.fallDistance = nms.getFallDistance(p);

            //Base Speed

            this.baseGroundSpeed = MoveUtils.getBaseGroundSpeed(profile);

            this.baseAirSpeed = MoveUtils.getBaseAirSpeed(profile);

            //Ghost Blocks
            this.ghostblockProcessor.process();
//            this.ghostblockProcessor = new GhostblockProcessor();

    }

    public int getLastNearEdgeTicks() {
        return lastNearEdgeTicks;
    }

    public int getLastFrictionFactorUpdateTicks() {
        return lastFrictionFactorUpdateTicks;
    }

    public float getFrictionFactor() {
        return frictionFactor;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public float getBaseAirSpeed() {
        return baseAirSpeed;
    }

    public float getBaseGroundSpeed() {
        return baseGroundSpeed;
    }

    public int getLastNearWallTicks() {
        return lastNearWallTicks;
    }

    public int getClientGroundTicks() {
        return clientGroundTicks;
    }

    public int getLastUnloadedChunkTicks() {
        return lastUnloadedChunkTicks;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getLastDeltaX() {
        return lastDeltaX;
    }

    public double getDeltaZ() {
        return deltaZ;
    }

    public double getLastDeltaZ() {
        return lastDeltaZ;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public double getLastDeltaY() {
        return lastDeltaY;
    }

    public double getDeltaXZ() {
        return deltaXZ;
    }

    public double getLastDeltaXZ() {
        return lastDeltaXZ;
    }

    public double getAccelXZ() {
        return accelXZ;
    }

    public double getLastAccelXZ() {
        return lastAccelXZ;
    }

    public double getAccelY() {
        return accelY;
    }

    public double getLastAccelY() {
        return lastAccelY;
    }

    public float getFallDistance() {
        return fallDistance;
    }

    public float getLastFallDistance() {
        return lastFallDistance;
    }

    public CustomLocation getLocation() {
        return location;
    }

    public CustomLocation getLastLocation() {
        return lastLocation;
    }



    public boolean isOnGround() {
        return onGround;
    }

    public boolean isServerGround() {
        return serverGround;
    }

    public int getFlyTicks() {
        return flyTicks;
    }

    public int getLastServerGroundTicks() {
        return lastServerGroundTicks;
    }

    public int getServerGroundTicks() {
        return serverGroundTicks;
    }

    public boolean isLastOnGround() {
        return lastOnGround;
    }

    public boolean isLastServerGround() {
        return lastServerGround;
    }

    public int getNearGroundTicks() {
        return nearGroundTicks;
    }

    public int getLastNearGroundTicks() {
        return lastNearGroundTicks;
    }

    public List<Material> getNearbyBlocks() {
        return nearbyBlocks;
    }

    public boolean insideLiquid() {
        return inLiquid;
    }

    public boolean insideVehicle() {
        return inVehicle;
    }
    public boolean onClimbable() {
        return onClimbable;
    }

    public boolean onSlime() {
        return onSlime;
    }

    public boolean onWeb() {
        return inWeb;
    }

    public int getSinceBlockNearHeadTicks() {
        return sinceBlockNearHeadTicks;
    }

    public boolean isInAir() {
        return inAir;
    }

    public boolean inAirOneblock(){
        return inAirOneBlock;
    }

    public boolean Flying() { return isFlying; }

}