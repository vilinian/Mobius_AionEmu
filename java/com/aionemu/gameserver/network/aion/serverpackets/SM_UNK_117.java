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

import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This class handles the {@code SM_UNK_117} server packet.<br>
 * It is used to process specific unknown game data sent from the server to the client.
 * @author Falke_34
 */
public class SM_UNK_117 extends AionServerPacket
{
	private final int objId;
	private final int remainingLifetime;
	
	/**
	 * Creates a new {@code SM_UNK_117} packet instance.<br>
	 * This constructor initializes the packet using data from a {@link Kisk} object.
	 * @param kisk The {@code Kisk} object used to populate the packet fields.
	 */
	public SM_UNK_117(Kisk kisk)
	{
		objId = kisk.getObjectId();
		remainingLifetime = kisk.getRemainingLifetime();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(objId);
		writeD(0);
		writeD(0);
		writeD(remainingLifetime);
	}
}
