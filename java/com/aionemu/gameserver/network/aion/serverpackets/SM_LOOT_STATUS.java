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
 * This packet handles the status of loot items in the game world.<br>
 * It informs the client about whether an item is currently being looted or is available.
 * @author alexa026
 */
public class SM_LOOT_STATUS extends AionServerPacket
{
	private final int targetObjectId;
	private final int state;
	
	/**
	 * Creates a new {@code SM_LOOT_STATUS} packet.<br>
	 * This packet updates the loot status for a specific object.
	 * @param targetObjectId The unique ID of the target object.
	 * @param state The current status of the loot.
	 */
	public SM_LOOT_STATUS(int targetObjectId, int state)
	{
		this.targetObjectId = targetObjectId;
		this.state = state;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(targetObjectId);
		writeC(state);
		writeD(0);
	}
}
