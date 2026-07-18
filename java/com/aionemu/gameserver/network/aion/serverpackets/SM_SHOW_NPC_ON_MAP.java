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
 * This packet is used to display an NPC on the player's map.<br>
 * It informs the client to render a specific NPC entity at its current location.
 * @author Lyahim
 */
public class SM_SHOW_NPC_ON_MAP extends AionServerPacket
{
	private final int npcid, worldid;
	private final float x, y, z;
	
	/**
	 * This method creates a packet to display an NPC on the map.<br>
	 * It sets the position and identity of the NPC for the client.
	 * @param npcid The unique ID of the NPC.
	 * @param worldid The ID of the world where the NPC is located.
	 * @param x The X coordinate of the NPC.
	 * @param y The Y coordinate of the NPC.
	 * @param z The Z coordinate of the NPC.
	 */
	public SM_SHOW_NPC_ON_MAP(int npcid, int worldid, float x, float y, float z)
	{
		this.npcid = npcid;
		this.worldid = worldid;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(npcid);
		writeD(worldid);
		writeD(worldid);
		writeF(x);
		writeF(y);
		writeF(z);
	}
}
