package me.sonix.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPacketInTransaction extends PacketWrapper{

	public static final PacketType TYPE = PacketType.Play.Client.TRANSACTION;

	public WrapperPacketInTransaction(PacketContainer packet) {
		super(packet);
	}
}
