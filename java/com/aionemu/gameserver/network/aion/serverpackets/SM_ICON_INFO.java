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
 * This packet handles the transmission of icon information to the client.<br>
 * It provides details about specific icons used within the game interface.
 * @author xTz
 */
public class SM_ICON_INFO extends AionServerPacket
{
	private final int buffId;
	private final boolean display;
	
	/**
	 * Creates a new {@link SM_ICON_INFO} packet.<br>
	 * This packet sends information about a specific buff icon to the client.
	 * @param buffId The unique identifier for the buff.
	 * @param display Whether the icon should be shown on the screen.
	 */
	public SM_ICON_INFO(int buffId, boolean display)
	{
		this.buffId = buffId;
		this.display = display;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(buffId);
		writeD(7); // Value from PS 5.4
		writeC(display ? 1 : 0);
	}
}
