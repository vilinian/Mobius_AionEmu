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
 * This packet updates the current {@code mp} and maximum {@code mp} values for a character.<br>
 * It is sent from the server to synchronize mana statistics with the client.
 * @author Luno
 */
public class SM_STATUPDATE_MP extends AionServerPacket
{
	private final int currentMp;
	private final int maxMp;
	
	/**
	 * Creates a new {@code SM_STATUPDATE_MP} packet.<br>
	 * This updates the player's mana points.
	 * @param currentMp The current amount of mana.
	 * @param maxMp The maximum capacity of mana.
	 */
	public SM_STATUPDATE_MP(int currentMp, int maxMp)
	{
		this.currentMp = currentMp;
		this.maxMp = maxMp;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(currentMp);
		writeD(maxMp);
	}
}
