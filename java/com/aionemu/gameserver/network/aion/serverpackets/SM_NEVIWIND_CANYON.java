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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the data for the Neviwind Canyon area.<br>
 * It is used to synchronize information between the server and the client regarding this specific location.
 * @author Falke_34
 */
public class SM_NEVIWIND_CANYON extends AionServerPacket
{
	private final int action;
	
	/**
	 * Creates a new instance of the {@code SM_NEVIWIND_CANYON} packet.<br>
	 * This constructor initializes the packet with a specific action type.
	 * @param action The integer value representing the action to perform.
	 */
	public SM_NEVIWIND_CANYON(int action)
	{
		this.action = action;
	}
	
	@Override
	protected void writeImpl(AionConnection aionConnection)
	{
		writeC(action);
	}
}
