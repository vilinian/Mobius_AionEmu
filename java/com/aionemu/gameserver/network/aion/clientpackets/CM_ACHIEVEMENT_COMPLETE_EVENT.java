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
 * This packet handles the event sent by the client when an achievement is completed.<br>
 * It notifies the server to process the completion logic via {@link AchievementService}.
 */
public class CM_ACHIEVEMENT_COMPLETE_EVENT extends AionClientPacket
{
	private int templateId;
	@SuppressWarnings("unused")
	private long ObjectId;
	
	/**
	 * Creates a new instance of the {@code CM_ACHIEVEMENT_COMPLETE_EVENT} packet.<br>
	 * This constructor initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_ACHIEVEMENT_COMPLETE_EVENT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		templateId = readD();
		ObjectId = readQ();
		readC();
	}
	
	@Override
	protected void runImpl()
	{
		AchievementService.getInstance().completeAchievementEvent(getConnection().getActivePlayer(), templateId);
	}
}
