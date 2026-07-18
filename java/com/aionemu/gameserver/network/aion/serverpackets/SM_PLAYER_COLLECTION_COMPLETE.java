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

import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionEntry;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to notify that a player has completed a collection.<br>
 * It informs the game client to update the UI or state for the finished activity.
 */
public class SM_PLAYER_COLLECTION_COMPLETE extends AionServerPacket
{
	private final PlayerCollectionEntry entry;
	
	/**
	 * This constructor initializes a new {@code SM_PLAYER_COLLECTION_COMPLETE} packet.<br>
	 * It stores the specific collection data provided in the {@link PlayerCollectionEntry} object.
	 * @param entry The {@code PlayerCollectionEntry} to be included in this packet.
	 */
	public SM_PLAYER_COLLECTION_COMPLETE(PlayerCollectionEntry entry)
	{
		this.entry = entry;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(entry.getId());
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
	}
}
