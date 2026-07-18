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
 * This packet updates the target information for a client.<br>
 * It is used to synchronize the current target state between the server and the player.
 * @author Sweetkr
 */
public class SM_TARGET_UPDATE extends AionServerPacket
{
	private final Player player;
	
	/**
	 * Updates the target information for a specific player.<br>
	 * This method initializes the packet with the provided {@link Player} object.
	 * @param player The {@code Player} who will receive the update.
	 */
	public SM_TARGET_UPDATE(Player player)
	{
		this.player = player;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(player.getObjectId());
		writeD(player.getTarget() == null ? 0 : player.getTarget().getObjectId());
	}
}
