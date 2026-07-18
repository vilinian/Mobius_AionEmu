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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the data for the Tower of Challenge content.<br>
 * It is sent to the client to synchronize tower-related information.
 * @author Falke_34
 */
public class SM_TOWER_OF_CHALLENGE extends AionServerPacket
{
	private final int instanceId;
	private final int value;
	private final String variable;
	
	/**
	 * Creates a new {@code SM_TOWER_OF_CHALLENGE} packet.<br>
	 * This method initializes the packet with data from a {@link Player}.<br>
	 * It sets the instance ID, variable name, and value.
	 * @param player The {@code Player} object used to retrieve the instance ID.
	 * @param variable The name of the variable to be updated.
	 * @param value The integer value to assign to the variable.
	 */
	public SM_TOWER_OF_CHALLENGE(final Player player, String variable, int value)
	{
		instanceId = player.getInstanceId();
		this.variable = variable;
		this.value = value;
	}
	
	@Override
	protected void writeImpl(AionConnection aionConnection)
	{
		writeD(instanceId); // mapId
		writeS(variable); // variable
		writeD(value); // value
	}
}
