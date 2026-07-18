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
 * This packet handles the {@code CUBIC} data structure sent from the server to the client.<br>
 * It is used to synchronize specific game world information or state updates.
 * @author Falke_34
 */
public class SM_CUBIC extends AionServerPacket
{
	private final int cubicId;
	private final int rank;
	private int level;
	private final int register;
	
	/**
	 * Creates a new instance of the {@code SM_CUBIC} packet.<br>
	 * This constructor initializes all required fields for the cubic data.
	 * @param cubicId The unique identifier for the cubic.
	 * @param rank The current rank of the cubic.
	 * @param level The current level of the cubic.
	 * @param register The registration value associated with the cubic.
	 */
	public SM_CUBIC(int cubicId, int rank, int level, int register)
	{
		this.cubicId = cubicId;
		this.rank = rank;
		this.level = level;
		this.level = level;
		this.register = register;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(cubicId); // Cube
		writeD(rank); // Rank
		writeD(level); // Level
		writeD(register); // Count of Cubic to Register
	}
}
