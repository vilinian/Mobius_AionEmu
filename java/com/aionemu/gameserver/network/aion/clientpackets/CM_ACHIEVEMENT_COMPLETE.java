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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.player.AchievementService;

/**
 * This packet is sent by the client to notify the server that an achievement has been completed.<br>
 * It triggers the {@link AchievementService} to process the completion logic for the player.
 */
public class CM_ACHIEVEMENT_COMPLETE extends AionClientPacket
{
	private int templateId;
	private long achievementObj;
	private long actionObj;
	
	/**
	 * This constructor initializes a new {@link CM_ACHIEVEMENT_COMPLETE} packet.<br>
	 * It passes the network details to the parent class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The current connection state of the client.
	 * @param restStates Additional states associated with the connection.
	 */
	public CM_ACHIEVEMENT_COMPLETE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		templateId = readD();
		achievementObj = readQ();
		actionObj = readQ();
	}
	
	@Override
	protected void runImpl()
	{
		if (achievementObj == 0)
		{
			AchievementService.getInstance().onRewardAchievement(getConnection().getActivePlayer(), templateId);
		}
		else
		{
			AchievementService.getInstance().onRewardAction(getConnection().getActivePlayer(), (int) actionObj, templateId);
		}
	}
}
