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
 * This class represents an unknown server packet with the ID {@code 127}.<br>
 * It serves as a placeholder for handling specific game data that is not yet fully defined.
 * @author Dummy
 */
public class SM_UNK_127 extends AionServerPacket
{
	/**
	 * This is a default constructor for the {@link SM_UNK_127} class.<br>
	 * It initializes a new instance of this packet type.
	 */
	public SM_UNK_127()
	{
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(0);
		writeC(0);
	}
}
