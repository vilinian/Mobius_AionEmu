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

import com.aionemu.gameserver.model.team.legion.LegionEmblemType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to provide information about a legion emblem.<br>
 * It handles the synchronization of {@link LegionEmblemType} data for the player.
 * @author Simple modified cura
 */
public class SM_LEGION_SEND_EMBLEM extends AionServerPacket
{
	/**
	 * Legion information *
	 */
	private final int legionId;
	private final int emblemId;
	private final int color_r;
	private final int color_g;
	private final int color_b;
	private final String legionName;
	private final LegionEmblemType emblemType;
	private final int emblemDataSize;
	
	/**
	 * This constructor initializes a new {@code SM_LEGION_SEND_EMBLEM} packet.<br>
	 * It sets all the necessary data for sending legion emblem information to the client.
	 * @param legionId The unique identifier for the legion.
	 * @param emblemId The unique identifier for the specific emblem.
	 * @param color_r The red component of the emblem color.
	 * @param color_g The green component of the emblem color.
	 * @param color_b The blue component of the emblem color.
	 * @param legionName The display name of the legion.
	 * @param emblemType The category or type of the emblem.
	 * @param emblemDataSize The size of the emblem data in bytes.
	 */
	public SM_LEGION_SEND_EMBLEM(int legionId, int emblemId, int color_r, int color_g, int color_b, String legionName, LegionEmblemType emblemType, int emblemDataSize)
	{
		this.legionId = legionId;
		this.emblemId = emblemId;
		this.color_r = color_r;
		this.color_g = color_g;
		this.color_b = color_b;
		this.legionName = legionName;
		this.emblemType = emblemType;
		this.emblemDataSize = emblemDataSize;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(legionId);
		writeC(emblemId);
		writeC(emblemType.getValue());
		writeD(emblemDataSize);
		writeC(emblemType.equals(LegionEmblemType.DEFAULT) ? 0x00 : 0xFF);
		writeC(color_r);
		writeC(color_g);
		writeC(color_b);
		writeS(legionName);
		writeC(0x01);
	}
}
