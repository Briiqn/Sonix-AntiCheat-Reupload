package me.sonix.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class WrapperPlayClientUseEntity extends PacketWrapper {

    public static final PacketType TYPE = PacketType.Play.Client.USE_ENTITY;

    private final int targetId;
    private final EnumWrappers.EntityUseAction type;
    private Vector targetVector;

    public WrapperPlayClientUseEntity(PacketContainer packet) {
        super(packet);

        this.targetId = handle.getIntegers().read(0);
        EnumWrappers.EntityUseAction action;

        if ((action = handle.getEntityUseActions().read(0)) == EnumWrappers.EntityUseAction.INTERACT_AT) {

            this.targetVector = handle.getVectors().read(0);
        }

        this.type = action;
    }

    /**
     * Retrieve entity ID of the target.
     *
     * @return The current entity ID
     */
    public int getTargetID() {
        return targetId;
    }

    /**
     * Retrieve the entity the player is interacting with.
     * @param world - the world this event occured in.
     * @return The target entity.
     */
    public Entity getTarget(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity the player is interacting with.
     * @param event - the current packet event.
     * @return The target entity.
     */
    public Entity getTarget(PacketEvent event) {
        return getTarget(event.getPlayer().getWorld());
    }

    /**
     * Set the entity ID the player is interacting with.
     * @param value - new value.
     */
    public void setTargetID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the use action.
     * @return The action.
     */
    public EnumWrappers.EntityUseAction getMouse() {
        return handle.getEntityUseActions().read(0);
    }

    /**
     * Set the use action.
     * @param value - new action.
     */
    public void setMouse(EnumWrappers.EntityUseAction value) {
        handle.getEntityUseActions().write(0, value);
    }

    /**
     * Retrieve Type.
     *
     * @return The current Type
     */
    public EnumWrappers.EntityUseAction getType() {
        return type;
    }

    /**
     * Retrieve the target vector.
     * <p>
     * Notes: Only if {@link #getType()} is {@link EnumWrappers.EntityUseAction#INTERACT_AT}, Make sure the {@link #getType()} method
     * Has been called first.
     *
     * @return The target vector or null
     */
    public Vector getTargetVector() {
        return targetVector;
    }

}