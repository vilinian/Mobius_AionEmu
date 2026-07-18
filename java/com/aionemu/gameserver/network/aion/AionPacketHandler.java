/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;

/**
 * This class manages the processing and routing of incoming network packets.<br>
 * It identifies packet types and dispatches them to the appropriate handlers.<br>
 * It serves as the primary entry point for handling {@code ByteBuffer} data from clients.
 * @author -Nemesiss-
 * @author Luno
 * @author GiGatR00n
 */
public class AionPacketHandler
{
	/**
	 * logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(AionPacketHandler.class);
	private final Map<Integer, AionClientPacket> packetsPrototypes = new HashMap<>();
	
	/**
	 * Processes an incoming network packet from a client.<br>
	 * It extracts the packet ID and state to determine the correct {@link AionClientPacket}.
	 * @param data The {@code ByteBuffer} containing the raw packet data.
	 * @param client The {@link AionConnection} representing the current client connection.
	 * @return The parsed {@code AionClientPacket} object.
	 */
	public AionClientPacket handle(ByteBuffer data, AionConnection client)
	{
		final State state = client.getState();
		final int id = data.getShort() & 0xffff;
		/* Second opcodec. */
		data.position(data.position() + 3);
		
		return getPacket(state, id, data, client);
	}
	
	/**
	 * Registers a new packet prototype into the system.<br>
	 * This method maps the opcode to its corresponding {@link AionClientPacket}.<br>
	 * It allows the {@code AionConnection)} method to identify packets.
	 * @param packetPrototype The {@code AionClientPacket} object to be registered.
	 */
	public void addPacketPrototype(AionClientPacket packetPrototype)
	{
		packetsPrototypes.put(packetPrototype.getOpcode(), packetPrototype);
	}
	
	/**
	 * Retrieves a packet prototype based on its unique identifier.<br>
	 * It handles logging for developers and clones the packet data.<br>
	 * If no prototype exists, it triggers an unknown packet event.
	 * @param state The current connection {@link State}.
	 * @param id The unique integer ID of the packet.
	 * @param buf The {@code ByteBuffer} containing raw data.
	 * @param con The active {@link AionConnection} object.
	 * @return The cloned {@link AionClientPacket} or {@code null} if not found.
	 */
	private AionClientPacket getPacket(State state, int id, ByteBuffer buf, AionConnection con)
	{
		final AionClientPacket prototype = packetsPrototypes.get(id);
		
		if (prototype == null)
		{
			unknownPacket(state, id, buf);
			return null;
		}
		
		/**
		 * Display Packets Name + Hex-Bytes in Chat Window
		 */
		final Player player = con.getActivePlayer();
		
		if (con.getState().equals(State.IN_GAME) && (player != null) && (player.getAccessLevel() >= DeveloperConfig.SHOW_PACKETS_INCHAT_ACCESSLEVEL))
		{
			if (isPacketFilterd(DeveloperConfig.FILTERED_PACKETS_INCHAT, prototype.getPacketName()))
			{
				if (DeveloperConfig.SHOW_PACKET_BYTES_INCHAT)
				{
					final String PckName = String.format("0x%04X : %s", id, prototype.getPacketName());
					PacketSendUtility.sendMessage(player, "********************************************");
					PacketSendUtility.sendMessage(player, PckName);
					PacketSendUtility.sendMessage(player, Util.toHexStream(getByteBuffer(buf, DeveloperConfig.TOTAL_PACKET_BYTES_INCHAT)));
					buf.position(5);
				}
				else if (DeveloperConfig.SHOW_PACKET_NAMES_INCHAT)
				{
					final String PckName = String.format("0x%04X : %s", id, prototype.getPacketName());
					PacketSendUtility.sendMessage(player, PckName);
				}
			}
		}
		
		final AionClientPacket res = prototype.clonePacket();
		res.setBuffer(buf);
		res.setConnection(con);
		
		if (con.getState().equals(State.IN_GAME) && (con.getActivePlayer().getPlayerAccount().getMembership() == 10))
		{
			PacketSendUtility.sendMessage(con.getActivePlayer(), "0x" + Integer.toHexString(res.getOpcode()).toUpperCase() + " : " + res.getPacketName());
		}
		
		return res;
	}
	
	/**
	 * Checks if a specific packet should be shown based on a filter list.<br>
	 * It returns {@code true} if the packet name matches any entry in the list.<br>
	 * If the filter list is {@code null} or contains only {@code *}, it returns {@code true}.
	 * @param filterlist A comma-separated string of allowed packet names.
	 * @param PacketName The name of the packet to check.
	 * @return {@code true} if the packet is not filtered, otherwise {@code false}.
	 */
	private boolean isPacketFilterd(String filterlist, String PacketName)
	{
		// If FilterList was empty, all packets will be shown.
		if ((filterlist == null) || filterlist.equalsIgnoreCase("*"))
		{
			return true;
		}
		
		String[] Parts = null;
		Parts = filterlist.trim().split(",");
		
		for (String p : Parts)
		{
			if (p.trim().equalsIgnoreCase(PacketName))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Extracts a specific portion of data from a {@code ByteBuffer}.<br>
	 * It skips the first 5 bytes and copies the remaining data up to the specified count.<br>
	 * This method returns a new, independent {@code ByteBuffer} containing the extracted data.
	 * @param buf The source {@code ByteBuffer} to read from.
	 * @param count The maximum number of bytes to copy into the new buffer.
	 * @return A new {@code ByteBuffer} containing the sliced data.
	 */
	private ByteBuffer getByteBuffer(ByteBuffer buf, int count)
	{
		count = (count <= buf.capacity()) ? count : buf.capacity();
		final ByteBuffer tmpBuffer = buf.asReadOnlyBuffer();
		tmpBuffer.position(5);
		tmpBuffer.limit(count);
		
		// Create an empty ByteBuffer with a Requested Capacity.
		final ByteBuffer PckBuffer = ByteBuffer.allocate(count);
		try
		{
			do
			{
				PckBuffer.put(tmpBuffer.get());
			}
			while (tmpBuffer.remaining() > 0);
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		
		PckBuffer.position(0);
		return PckBuffer;
	}
	
	/**
	 * Handles packets that do not match any known prototype.<br>
	 * It logs a warning if the configuration allows it.
	 * @param state The current {@link State} of the connection.
	 * @param id The unique identifier for the packet.
	 * @param data The raw {@code ByteBuffer} containing the packet content.
	 */
	private void unknownPacket(State state, int id, ByteBuffer data)
	{
		if (NetworkConfig.DISPLAY_UNKNOWNPACKETS)
		{
			log.warn(String.format("Unknown packet received from Aion client: 0x%04X, state=%s %n%s", id, state.toString(), Util.toHex(data)));
		}
	}
}
