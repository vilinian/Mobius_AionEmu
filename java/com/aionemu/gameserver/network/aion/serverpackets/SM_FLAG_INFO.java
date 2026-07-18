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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the transmission of flag information to the client.<br>
 * It is used to synchronize specific status flags for game objects like {@link Creature} or {@link Npc}.
 */
public class SM_FLAG_INFO extends AionServerPacket
{
	int count;
	private final Creature _npc;
	private final int npcId;
	
	/**
	 * Creates a new {@code SM_FLAG_INFO} packet.<br>
	 * This constructor initializes the packet with a specific count and an {@link Npc}.<br>
	 * It also automatically retrieves the {@code npcId} from the provided {@code Npc} object.
	 * @param count The number of flags to include in the packet.
	 * @param npc The {@code Npc} instance associated with this information.
	 */
	public SM_FLAG_INFO(int count, Npc npc)
	{
		this.count = count;
		_npc = npc;
		npcId = npc.getNpcId();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(count);
		if (_npc != null)
		{
			writeD(npcId);
			writeD(_npc.getObjectId());
			writeD(_npc.getLifeStats().getCurrentHp());
			writeD(_npc.getLifeStats().getMaxHp());
			writeF(_npc.getX());
			writeF(_npc.getY());
			writeF(_npc.getZ());
		}
		else
		{
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeF(0);
			writeF(0);
			writeF(0);
		}
	}
}
