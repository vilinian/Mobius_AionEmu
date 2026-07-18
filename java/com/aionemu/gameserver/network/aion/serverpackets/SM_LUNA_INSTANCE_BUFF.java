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
 * This packet handles the application of buffs within a {@code LUNA} instance.<br>
 * It is sent from the server to the client to update character status effects.
 * @author Falke_34
 */
public class SM_LUNA_INSTANCE_BUFF extends AionServerPacket
{
	private final int buffId;
	private final boolean active;
	
	/**
	 * Creates a new {@code SM_LUNA_INSTANCE_BUFF} packet.<br>
	 * This packet handles the status of a specific buff in an instance.
	 * @param buffId The unique identifier for the buff.
	 * @param active The current state of the buff, either {@code true} or {@code false}.
	 */
	public SM_LUNA_INSTANCE_BUFF(int buffId, boolean active)
	{
		this.buffId = buffId;
		this.active = active;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(buffId);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
		writeC(active ? 1 : 0);
	}
}
