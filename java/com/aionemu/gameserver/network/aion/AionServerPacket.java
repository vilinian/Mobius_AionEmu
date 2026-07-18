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

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.packet.BaseServerPacket;
import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.Crypt;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;

/**
 * This is the base class for all packets sent from the Game Server to the client.<br>
 * It serves as the foundation for every {@link com.aionemu.gameserver.network.aion.AionServerPacket} type.
 * @author -Nemesiss-
 * @author GiGatR00n
 */
public abstract class AionServerPacket extends BaseServerPacket
{
	// private static final Logger log = LoggerFactory.getLogger(AionServerPacket.class);
	
	/**
	 * Initializes a new instance of an {@link AionServerPacket}.<br>
	 * This constructor sets the correct opcode for the packet class.
	 */
	protected AionServerPacket()
	{
		super();
		setOpcode(ServerPacketsOpcodes.getOpcode(getClass()));
	}
	
	/**
	 * This method obfuscates the packet ID.<br>
	 * It encodes the {@code value} using {@code encodeOpcodec}.<br>
	 * The result is then written to the buffer along with a static server packet code.
	 * @param value The original packet ID to be encoded and written.
	 */
	private void writeOP(int value)
	{
		/**
		 * obfuscate packet id
		 */
		final int op = Crypt.encodeOpcodec(value);
		buf.putShort((short) (op));
		/**
		 * put static server packet code
		 */
		buf.put(Crypt.staticServerPacketCode);
		
		/**
		 * for checksum?
		 */
		buf.putShort((short) (~op));
	}
	
	/**
	 * Sends the packet data to the specified connection.<br>
	 * This method uses the internal buffer to perform the write operation.
	 * @param con The {@link AionConnection} object used to send the data.
	 */
	public void write(AionConnection con)
	{
		write(con, buf);
	}
	
	/**
	 * Performs the internal logic for writing packet data.<br>
	 * This method is called by {@code write}.<br>
	 * It handles the actual serialization of fields to the connection.
	 * @param con The {@code AionConnection} object used to send the data.
	 */
	protected void writeImpl(AionConnection con)
	{
	}
	
	/**
	 * Retrieves the internal data buffer. <br>
	 * This method provides access to the {@code ByteBuffer} used for packet data.
	 * @return The current {@code ByteBuffer}.
	 */
	public ByteBuffer getBuf()
	{
		return buf;
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
		tmpBuffer.position(5 + 2);
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
	 * Writes the packet data to a specific connection and buffer.<br>
	 * This method handles encryption and optional debug logging for developers.
	 * @param con The {@link AionConnection} used to send the packet.
	 * @param buffer The {@link ByteBuffer} where the packet data is stored.
	 */
	public void write(AionConnection con, ByteBuffer buffer)
	{
		if (con.getState().equals(AionConnection.State.IN_GAME) && (con.getActivePlayer().getPlayerAccount().getMembership() == 10))
		{
			if (!getPacketName().equals("SM_MESSAGE"))
			{
				PacketSendUtility.sendMessage(con.getActivePlayer(), "0x" + Integer.toHexString(getOpcode()).toUpperCase() + " : " + getPacketName());
			}
		}
		
		setBuf(buffer);
		buf.putShort((short) 0);
		writeOP(getOpcode());
		writeImpl(con);
		buf.flip();
		/**
		 * Display Packets Name + Hex-Bytes in Chat Window
		 */
		final int BufCurrentPos = buf.position();
		buf.position(5 + 2);
		
		final Player player = con.getActivePlayer();
		
		if (con.getState().equals(State.IN_GAME) && (player != null) && (getOpcode() != 24) && (player.getAccessLevel() >= DeveloperConfig.SHOW_PACKETS_INCHAT_ACCESSLEVEL))
		{
			if (isPacketFilterd(DeveloperConfig.FILTERED_PACKETS_INCHAT, getPacketName()))
			{
				if (DeveloperConfig.SHOW_PACKET_BYTES_INCHAT)
				{
					final String PckName = String.format("0x%04X : %s", getOpcode(), getPacketName());
					PacketSendUtility.sendMessage(player, "********************************************");
					PacketSendUtility.sendMessage(player, PckName);
					PacketSendUtility.sendMessage(player, Util.toHexStream(getByteBuffer(buf, DeveloperConfig.TOTAL_PACKET_BYTES_INCHAT)));
				}
				else if (DeveloperConfig.SHOW_PACKET_NAMES_INCHAT)
				{
					final String PckName = String.format("0x%04X : %s", getOpcode(), getPacketName());
					PacketSendUtility.sendMessage(player, PckName);
				}
			}
		}
		
		buf.position(BufCurrentPos);
		
		buf.putShort((short) buf.limit());
		final ByteBuffer b = buf.slice();
		buf.position(0);
		con.encrypt(b);
	}
	
	/**
	 * Writes a string to the packet buffer with a fixed size.<br>
	 * If {@code text} is {@code null}, it fills the buffer with empty bytes.<br>
	 * Otherwise, it writes each character and pads the remaining space.
	 * @param text The string content to write.
	 * @param size The total number of characters the buffer should hold.
	 */
	protected void writeS(String text, int size)
	{
		if (text == null)
		{
			buf.put(new byte[size]);
		}
		else
		{
			final int len = text.length();
			for (int i = 0; i < len; i++)
			{
				buf.putChar(text.charAt(i));
			}
			
			buf.put(new byte[size - (len * 2)]);
		}
	}
	
	/**
	 * Writes a specific name identifier to the packet buffer.<br>
	 * This method handles the formatting for the {@code nameId} field.
	 * @param nameId The unique identifier for the name.
	 */
	protected void writeNameId(int nameId)
	{
		writeH(0x24);
		writeD(nameId);
		writeH(0x00);
	}
}
