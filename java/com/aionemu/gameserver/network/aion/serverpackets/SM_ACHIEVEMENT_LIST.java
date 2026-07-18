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
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementAction;
import com.aionemu.gameserver.model.gameobjects.player.achievement.PlayerAchievement;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.player.AchievementService;

/**
 * This packet is sent to the client to provide a list of achievements.<br>
 * It contains data regarding {@link PlayerAchievement} objects for the player.<br>
 * Use this class when updating the user interface with available rewards or progress.
 */
public class SM_ACHIEVEMENT_LIST extends AionServerPacket
{
	private final Player player;
	
	/**
	 * Creates a new achievement list packet for a specific player.<br>
	 * This packet is used to send the player's achievements over the network.
	 * @param player The {@link Player} object associated with this packet.
	 */
	public SM_ACHIEVEMENT_LIST(Player player)
	{
		this.player = player;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeQ(AchievementService.getInstance().getLastUpdate().getTime() / 1000);
		writeH(player.getPlayerAchievements().size());
		for (PlayerAchievement achievement : player.getPlayerAchievements().values())
		{
			writeC(1);
			writeQ(achievement.getObjectId());
			writeD(achievement.getId());
			writeD(0);
			writeD(0);
			writeC(achievement.getType().getValue());
			writeC(0);
			writeC(achievement.getState().getValue());
			writeD(achievement.getStep());
			writeC(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeQ(achievement.getStartDate().getTime() / 1000);
			writeQ(achievement.getEndateDate().getTime() / 1000);
			writeH(achievement.getActionMap().size());
			for (AchievementAction action : achievement.getActionMap().values())
			{
				writeQ(action.getObjectId());
				writeD(action.getId());
				writeQ(action.getAchievementObjectId());
				writeC(0);
				writeC(0);
				writeC(action.getState().getValue());
				writeD(action.getStep());
				writeC(0);
				writeD(0);
				writeD(0);
				writeD(0);
				writeD(0);
				writeD(0);
				writeQ(action.getStartDate().getTime() / 1000);
				writeQ(action.getEndateDate().getTime() / 1000);
			}
		}
	}
}
