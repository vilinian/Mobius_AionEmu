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
 * This packet handles the movement of a player character within the game world.<br>
 * It is sent from the server to update the client on a player's new position and orientation.
 * @author cura
 */
public class SM_PLAYER_MOVE extends AionServerPacket
{
	private final float x;
	private final float y;
	private final float z;
	private final byte heading;
	
	/**
	 * Creates a new {@code SM_PLAYER_MOVE} packet.<br>
	 * This packet updates the player's position and rotation in the game world.
	 * @param x The horizontal coordinate of the player.
	 * @param y The vertical coordinate of the player.
	 * @param z The depth coordinate of the player.
	 * @param heading The direction the player is facing.
	 */
	public SM_PLAYER_MOVE(float x, float y, float z, byte heading)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeF(x);
		writeF(y);
		writeF(z);
		writeC(heading);
	}
}
