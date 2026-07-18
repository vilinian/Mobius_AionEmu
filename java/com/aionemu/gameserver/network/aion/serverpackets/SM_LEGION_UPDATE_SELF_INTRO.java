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
 * This packet updates the self-introduction information for a legion.<br>
 * It is used to synchronize character details within the legion system.
 * @author Simple
 */
public class SM_LEGION_UPDATE_SELF_INTRO extends AionServerPacket
{
	private final String selfintro;
	private final int playerObjId;
	
	/**
	 * Updates the self-introduction for a specific legion member.<br>
	 * This method initializes the packet with the provided data.
	 * @param playerObjId The unique identifier of the player object.
	 * @param selfintro The new introduction text to be displayed.
	 */
	public SM_LEGION_UPDATE_SELF_INTRO(int playerObjId, String selfintro)
	{
		this.selfintro = selfintro;
		this.playerObjId = playerObjId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerObjId);
		writeS(selfintro);
	}
}
