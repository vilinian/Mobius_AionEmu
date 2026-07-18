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
 * This packet provides information about the current stage of an instance.<br>
 * It is used to synchronize instance progress with the client.
 * @author xTz
 */
public class SM_INSTANCE_STAGE_INFO extends AionServerPacket
{
	private final int type;
	private final int event;
	private final int unk;
	
	/**
	 * Creates a new {@code SM_INSTANCE_STAGE_INFO} packet.<br>
	 * This constructor initializes the stage information for an instance.
	 * @param type The type of the instance stage.
	 * @param event The specific event associated with this stage.
	 * @param unk An unknown value used for additional data.
	 */
	public SM_INSTANCE_STAGE_INFO(int type, int event, int unk)
	{
		this.type = type;
		this.event = event;
		this.unk = unk;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(type);
		writeD(0);
		writeH(event);
		writeH(unk);
	}
}
