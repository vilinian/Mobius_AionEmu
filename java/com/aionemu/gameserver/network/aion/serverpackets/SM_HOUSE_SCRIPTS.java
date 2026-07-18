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

import com.aionemu.gameserver.model.gameobjects.player.PlayerScripts;
import com.aionemu.gameserver.model.house.PlayerScript;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the synchronization of house scripts for a player.<br>
 * It sends data related to {@link com.aionemu.gameserver.model.house.PlayerScript} objects to the client.
 * @author Rolandas
 */
public class SM_HOUSE_SCRIPTS extends AionServerPacket
{
	private final int address;
	private final PlayerScripts scripts;
	int from, to;
	
	/**
	 * Creates a new {@code SM_HOUSE_SCRIPTS} packet.<br>
	 * This packet handles house script data between players.
	 * @param address The unique identifier for the house.
	 * @param scripts The {@link PlayerScripts} object containing the script data.
	 * @param from The ID of the player sending the script.
	 * @param to The ID of the player receiving the script.
	 */
	public SM_HOUSE_SCRIPTS(int address, PlayerScripts scripts, int from, int to)
	{
		this.address = address;
		this.scripts = scripts;
		this.from = from;
		this.to = to;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(address);
		writeH((to - from) + 1);
		final Map<Integer, PlayerScript> scriptMap = scripts.getScripts();
		for (int position = from; position <= to; position++)
		{
			writeC(position);
			final PlayerScript script = scriptMap.get(position);
			final byte[] bytes = script.getCompressedBytes();
			if (bytes == null)
			{
				writeH(-1);
			}
			else
			{
				if (bytes.length == 0)
				{
					writeH(0);
					continue;
				}
				
				writeH(bytes.length + 8);
				writeD(bytes.length);
				writeD(script.getUncompressedSize());
				writeB(bytes);
			}
		}
	}
}
