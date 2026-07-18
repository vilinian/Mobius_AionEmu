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

import java.util.EnumSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.packet.BaseClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * This is the base class for all packets sent from the {@code AionClient} to the game server.<br>
 * It serves as the foundation for every {@code Aion -> LS Client Packet}.
 * @author -Nemesiss-
 */
public abstract class AionClientPacket extends BaseClientPacket<AionConnection> implements Cloneable
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(AionClientPacket.class);
	private final Set<State> validStates;
	
	/**
	 * Creates a new instance of an {@link AionClientPacket}.<br>
	 * This constructor initializes the packet with its unique opcode.<br>
	 * It also defines which connection states are valid for this packet.
	 * @param opcode The unique identifier for the packet type.
	 * @param state The primary required connection state.
	 * @param restStates Additional optional connection states that may be valid.
	 */
	protected AionClientPacket(int opcode, State state, State... restStates)
	{
		super(opcode);
		validStates = EnumSet.of(state, restStates);
	}
	
	@Override
	public void run()
	{
		try
		{
			// run only if packet is still valid (connection state didn't changed)
			if (isValid())
			{
				runImpl();
			}
		}
		catch (Throwable e)
		{
			String name = getConnection().getAccount().getName();
			if (name == null)
			{
				name = getConnection().getIP();
			}
			
			log.error("Error handling client (" + name + ") message :" + this, e);
		}
	}
	
	/**
	 * Sends a server packet to the connected client.<br>
	 * This method uses the {@link AionConnection} to transmit the data.
	 * @param msg The {@code AionServerPacket} object to be sent.
	 */
	public void sendPacket(AionServerPacket msg)
	{
		getConnection().sendPacket(msg);
	}
	
	/**
	 * Creates a copy of the current {@code AionClientPacket}.<br>
	 * This is useful when you need to duplicate a packet without modifying the original.
	 * @return A new instance of {@code AionClientPacket} or {@code null} if cloning fails.
	 */
	public AionClientPacket clonePacket()
	{
		try
		{
			return (AionClientPacket) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	/**
	 * Reads a {@code String} from the buffer.<br>
	 * It automatically adjusts the remaining bytes to read based on the string length.
	 * @param size The total number of bytes to read for this operation.
	 * @return The resulting {@code String} value or {@code null}.
	 */
	protected String readS(int size)
	{
		final String string = readS();
		if (string != null)
		{
			readB(size - ((string.length() * 2) + 2));
		}
		else
		{
			readB(size);
		}
		
		return string;
	}
	
	/**
	 * Checks if the packet is allowed for the current connection state.<br>
	 * It compares the current {@link State} against the required states.<br>
	 * If the state does not match, it logs an information message.
	 * @return {@code true} if the state is valid, {@code false} otherwise.
	 */
	public boolean isValid()
	{
		final State state = getConnection().getState();
		final boolean valid = validStates.contains(state);
		
		if (!valid)
		{
			log.info(this + " wont be processed cuz its valid state don't match current connection state: " + state);
		}
		
		return valid;
	}
}
