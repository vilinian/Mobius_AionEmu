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
 * This packet is sent to the client when a player interacts with an object in the game world.<br>
 * It handles the logic for using items, NPCs, or other interactable entities.
 * @author ATracer
 */
public class SM_USE_OBJECT extends AionServerPacket
{
	private final int playerObjId;
	private final int targetObjId;
	private final int time;
	private final int actionType;
	
	/**
	 * Creates a new {@code SM_USE_OBJECT} packet.<br>
	 * This packet handles interactions between players and objects.
	 * @param playerObjId The unique ID of the player object.
	 * @param targetObjId The unique ID of the target object.
	 * @param time The timestamp for the action.
	 * @param actionType The specific type of action being performed.
	 */
	public SM_USE_OBJECT(int playerObjId, int targetObjId, int time, int actionType)
	{
		super();
		this.playerObjId = playerObjId;
		this.targetObjId = targetObjId;
		this.time = time;
		this.actionType = actionType;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerObjId);
		writeD(targetObjId);
		writeD(time);
		writeC(actionType);
	}
}
