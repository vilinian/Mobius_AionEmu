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
 * This packet handles the identification process for Luna characters.<br>
 * It is sent to the client to confirm character identity during login or selection.
 * @author Falke_34
 */
public class SM_LUNA_IDENTIFICATION extends AionServerPacket
{
	private final int itemObjectId;
	
	/**
	 * Creates a new {@code SM_LUNA_IDENTIFICATION} packet.<br>
	 * This packet identifies an item for the player.
	 * @param player The {@link Player} who is receiving the packet.
	 * @param itemObjectId The unique ID of the item being identified.
	 */
	public SM_LUNA_IDENTIFICATION(Player player, int itemObjectId)
	{
		this.itemObjectId = itemObjectId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(itemObjectId);
		writeC(0);
	}
}
