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
 * This packet handles the animation for crafting items.<br>
 * It is sent to the client to trigger the visual craft sequence.
 * @author Mr. Poke
 */
public class SM_CRAFT_ANIMATION extends AionServerPacket
{
	private final int senderObjectId;
	private final int targetObjectId;
	private final int skillId;
	private final int action;
	
	/**
	 * Creates a new {@code SM_CRAFT_ANIMATION} packet.<br>
	 * This packet handles the animation for crafting actions.
	 * @param senderObjectId The ID of the object performing the craft.
	 * @param targetObjectId The ID of the object being affected by the craft.
	 * @param skillId The unique identifier for the craft skill used.
	 * @param action The specific type of action performed during crafting.
	 */
	public SM_CRAFT_ANIMATION(int senderObjectId, int targetObjectId, int skillId, int action)
	{
		this.senderObjectId = senderObjectId;
		this.targetObjectId = targetObjectId;
		this.skillId = skillId;
		this.action = action;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(senderObjectId);
		writeD(targetObjectId);
		writeH(skillId);
		writeC(action);
	}
}
