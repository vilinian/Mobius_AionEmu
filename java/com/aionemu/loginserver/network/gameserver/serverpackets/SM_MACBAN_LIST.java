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
package com.aionemu.loginserver.network.gameserver.serverpackets;

import java.util.Map;

import com.aionemu.loginserver.controller.BannedMacManager;
import com.aionemu.loginserver.model.base.BannedMacEntry;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

/**
 * This packet is sent to the game server to provide a list of banned MAC addresses.<br>
 * It contains data managed by the {@link BannedMacManager} for security purposes.
 * @author KID
 */
public class SM_MACBAN_LIST extends GsServerPacket
{
	private final Map<String, BannedMacEntry> bannedList;
	
	/**
	 * Creates a new instance of the {@code SM_MACBAN_LIST} packet.<br>
	 * This constructor initializes the banned list from the {@link BannedMacManager}.
	 */
	public SM_MACBAN_LIST()
	{
		bannedList = BannedMacManager.getInstance().getMap();
	}
	
	@Override
	protected void writeImpl(GsConnection con)
	{
		writeC(9);
		writeD(bannedList.size());
		
		for (BannedMacEntry entry : bannedList.values())
		{
			writeS(entry.getMac());
			writeQ(entry.getTime().getTime());
			writeS(entry.getDetails());
		}
	}
}
