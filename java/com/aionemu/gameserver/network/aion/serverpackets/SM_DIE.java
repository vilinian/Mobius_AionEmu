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
 * This packet is sent to the client when a character dies.<br>
 * It handles the notification of death events within the game world.
 * @author orz
 * @author Sarynth thx Rhys2002 for packets. :)
 */
public class SM_DIE extends AionServerPacket
{
	private final boolean hasRebirth;
	private final boolean hasItem;
	private final int remainingKiskTime;
	private int type = 0;
	private final boolean invasion;
	
	/**
	 * Creates a new {@code SM_DIE} packet.<br>
	 * This constructor sets the default invasion status to {@code false}.
	 * @param hasRebirth Indicates if the character has rebirth.
	 * @param hasItem Indicates if the character has an item.
	 * @param remainingKiskTime The amount of time left for the Kisk.
	 * @param type The packet type identifier.
	 */
	public SM_DIE(boolean hasRebirth, boolean hasItem, int remainingKiskTime, int type)
	{
		this(hasRebirth, hasItem, remainingKiskTime, type, false);
	}
	
	/**
	 * Creates a new {@code SM_DIE} packet.<br>
	 * This packet handles character death information.
	 * @param hasRebirth Indicates if the character has undergone rebirth.
	 * @param hasItem Indicates if the character currently holds an item.
	 * @param remainingKiskTime The amount of time left for the Kisk effect.
	 * @param type The specific type of death event.
	 * @param invasion Indicates if the death occurred during an invasion.
	 */
	public SM_DIE(boolean hasRebirth, boolean hasItem, int remainingKiskTime, int type, boolean invasion)
	{
		this.hasRebirth = hasRebirth;
		this.hasItem = hasItem;
		this.remainingKiskTime = remainingKiskTime;
		this.type = type;
		this.invasion = invasion;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC((hasRebirth ? 1 : 0)); // skillRevive
		writeC((hasItem ? 1 : 0)); // itemRevive
		writeD(remainingKiskTime);
		writeC(type);
		writeC(invasion ? 0x80 : 0x00);
		writeC(4);
		writeC(0); // TODO Trainings Arena Revive
	}
}
