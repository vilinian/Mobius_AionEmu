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
 * This packet handles the skill fusion process for Daevanion skills.<br>
 * It is used to synchronize the results of a skill fusion attempt between the server and the client.
 * @author Falke_34
 */
public class SM_DAEVANION_SKILL_FUSION extends AionServerPacket
{
	@SuppressWarnings("unused")
	private final int count;
	private final int result;
	
	/**
	 * This method creates a new {@code SM_DAEVANION_SKILL_FUSION} packet.<br>
	 * It initializes the fusion data for the skill system.
	 * @param count The number of items used in the fusion.
	 * @param result The outcome value of the fusion process.
	 */
	public SM_DAEVANION_SKILL_FUSION(int count, int result)
	{
		this.count = count;
		this.result = result;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(1);
		writeD(result); // itemTemplateId
		writeD(result); // itemTemplateId?
		writeD(result); // itemTemplateId?
	}
}
