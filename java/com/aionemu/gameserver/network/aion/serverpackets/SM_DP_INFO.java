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
 * This packet handles the delivery of character profile information.<br>
 * It is used to synchronize data between the server and the client.
 * @author Sweetkr
 */
public class SM_DP_INFO extends AionServerPacket
{
	private final int playerObjectId;
	private final int currentDp;
	
	/**
	 * Creates a new {@code SM_DP_INFO} packet.<br>
	 * This packet stores the DP information for a specific player.
	 * @param playerObjectId The unique ID of the player.
	 * @param currentDp The current amount of DP the player has.
	 */
	public SM_DP_INFO(int playerObjectId, int currentDp)
	{
		this.playerObjectId = playerObjectId;
		this.currentDp = currentDp;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerObjectId);
		writeH(currentDp);
	}
}
