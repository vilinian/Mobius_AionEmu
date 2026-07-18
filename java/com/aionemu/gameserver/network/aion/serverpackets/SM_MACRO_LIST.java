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

import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet contains the list of macros for a player.<br>
 * It is sent from the server to the client to synchronize macro data.
 * @author -Nemesiss-
 */
public class SM_MACRO_LIST extends AionServerPacket
{
	private final Player player;
	
	/**
	 * Creates a new {@code SM_MACRO_LIST} packet.<br>
	 * This packet contains the macro list for a specific player.
	 * @param player The {@link Player} object associated with this packet.
	 */
	public SM_MACRO_LIST(Player player)
	{
		this.player = player;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(player.getObjectId()); // player id
		
		final int size = player.getMacroList().getSize();
		
		writeC(0x01);
		writeH(-size);
		
		if (size != 0)
		{
			for (Map.Entry<Integer, String> entry : player.getMacroList().getMacrosses().entrySet())
			{
				writeC(entry.getKey()); // order
				writeS(entry.getValue()); // xml
			}
		}
	}
}
