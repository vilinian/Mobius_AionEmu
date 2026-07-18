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
 * This packet handles the update of a player's rank within the Abyss.<br>
 * It synchronizes the current ranking information from the server to the client.
 * @author Nemiroff Date: 17.02.2010
 */
public class SM_ABYSS_RANK_UPDATE extends AionServerPacket
{
	private final Player player;
	private final int action;
	
	/**
	 * Updates the abyss rank for a specific player.<br>
	 * This packet handles different actions based on the provided {@code action} value.
	 * @param action The type of update action to perform.
	 * @param player The {@link Player} object receiving the update.
	 */
	public SM_ABYSS_RANK_UPDATE(int action, Player player)
	{
		this.action = action;
		this.player = player;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(action);
		writeD(player.getObjectId());
		switch (action)
		{
			case 0:
				writeD(player.getAbyssRank().getRank().getId());
				break;
			case 1:
				if (player.isMentor())
				{
					writeD(1);
				}
				else
				{
					writeD(0);
				}
				break;
			case 2:
				if (player.isMentor())
				{
					writeD(1);
				}
				else
				{
					writeD(0);
				}
		}
	}
}
