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
 * This packet updates the current divine points {@code dp} value for a character.<br>
 * It is sent from the server to synchronize the player's statistics.
 * @author Luno
 */
public class SM_STATUPDATE_DP extends AionServerPacket
{
	private final int currentDp;
	
	/**
	 * Creates a new {@link SM_STATUPDATE_DP} packet.<br>
	 * This updates the divine points value for the player.
	 * @param currentDp The new amount of divine points to set.
	 */
	public SM_STATUPDATE_DP(int currentDp)
	{
		this.currentDp = currentDp;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(currentDp);
	}
}
