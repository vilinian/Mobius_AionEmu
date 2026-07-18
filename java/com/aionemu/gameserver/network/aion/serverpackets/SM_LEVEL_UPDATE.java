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
 * This packet is used to notify the client about a character's level update.<br>
 * It synchronizes the current level status between the server and the player.
 * @author ATracer
 */
public class SM_LEVEL_UPDATE extends AionServerPacket
{
	private final int targetObjectId;
	private final int effect;
	private final int level;
	
	/**
	 * Updates the level of a specific object.<br>
	 * This packet sends information about an object's new level and its visual effect.
	 * @param targetObjectId The unique identifier for the object to update.
	 * @param effect The visual effect ID associated with the update.
	 * @param level The new level value for the object.
	 */
	public SM_LEVEL_UPDATE(int targetObjectId, int effect, int level)
	{
		this.targetObjectId = targetObjectId;
		this.effect = effect;
		this.level = level;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(targetObjectId);
		writeH(effect); // unk
		writeH(level);
		writeH(0x00); // unk
	}
}
