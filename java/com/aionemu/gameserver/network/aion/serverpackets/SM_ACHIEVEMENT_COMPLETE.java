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
 * This packet is sent to the client when a player completes an achievement.<br>
 * It notifies the user of their success and triggers any associated rewards or UI updates.
 */
public class SM_ACHIEVEMENT_COMPLETE extends AionServerPacket
{
	private final int actionId;
	private final int achievementObj;
	private final int actionobj;
	
	/**
	 * This constructor initializes a new {@code SM_ACHIEVEMENT_COMPLETE} packet.<br>
	 * It sets the required data for completing an achievement.
	 * @param actionId The unique identifier for the action performed.
	 * @param achievementObj The object associated with the achievement.
	 * @param actionobj The specific object related to the action.
	 */
	public SM_ACHIEVEMENT_COMPLETE(int actionId, int achievementObj, int actionobj)
	{
		this.actionId = actionId;
		this.achievementObj = achievementObj;
		this.actionobj = actionobj;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(1);
		writeD(actionId);
		writeQ(achievementObj);
		writeQ(actionobj);
	}
}
