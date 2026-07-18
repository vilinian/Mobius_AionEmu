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
 * This packet updates the current HP and maximum HP values for a character.<br>
 * It is sent to synchronize health statistics between the server and the client.
 * @author Luno
 */
public class SM_STATUPDATE_HP extends AionServerPacket
{
	private final int currentHp;
	private final int maxHp;
	
	/**
	 * Creates a new {@link SM_STATUPDATE_HP} packet.<br>
	 * This updates the health values for a character.
	 * @param currentHp The current health points of the character.
	 * @param maxHp The maximum health points of the character.
	 */
	public SM_STATUPDATE_HP(int currentHp, int maxHp)
	{
		this.currentHp = currentHp;
		this.maxHp = maxHp;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(currentHp);
		writeD(maxHp);
	}
}
