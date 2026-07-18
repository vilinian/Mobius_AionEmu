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
 * This packet handles the animation for magic crafting.<br>
 * It is sent to clients to trigger the visual effects of a craft.
 * @author Falke_34
 */
public class SM_MAGIC_CRAFT_ANIMATION extends AionServerPacket
{
	private final int senderObjectId;
	private final int action;
	
	/**
	 * This method creates a new {@code SM_MAGIC_CRAFT_ANIMATION} packet.<br>
	 * It is used to trigger magic crafting animations for players.
	 * @param senderObjectId The unique ID of the object performing the action.
	 * @param action The specific animation type or action code.
	 */
	public SM_MAGIC_CRAFT_ANIMATION(int senderObjectId, int action)
	{
		this.senderObjectId = senderObjectId;
		this.action = action;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(senderObjectId);
		writeC(action);
	}
}
