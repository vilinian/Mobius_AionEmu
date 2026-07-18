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

import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet updates the client regarding the current crafting progress.<br>
 * It synchronizes the crafting status between the server and the player.
 * @author Mr. Poke
 * @author Antraxx
 */
public class SM_CRAFT_UPDATE extends AionServerPacket
{
	private final int skillId;
	private final int itemId;
	private final int action;
	private final int success;
	private final int failure;
	private final int nameId;
	private final int timerDelay = 700;
	private int timerPeriod = 1200;
	
	/**
	 * Updates the client on the result of a crafting action.<br>
	 * This packet handles both successful and failed craft attempts.<br>
	 * It also manages specific timing for skills like aether morphing.
	 * @param skillId The unique identifier for the skill used.
	 * @param item The {@link ItemTemplate} of the crafted object.
	 * @param success The result code indicating if the craft succeeded.
	 * @param failure The result code indicating the reason for failure.
	 * @param action The specific type of crafting action performed.
	 */
	public SM_CRAFT_UPDATE(int skillId, ItemTemplate item, int success, int failure, int action)
	{
		this.action = action;
		this.skillId = skillId;
		itemId = item.getTemplateId();
		this.success = success;
		this.failure = failure;
		nameId = item.getNameId();
		
		// aether morphing
		if (skillId == 40009)
		{
			timerPeriod = 3000;
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(skillId);
		writeC(action);
		writeD(itemId);
		
		switch (action)
		{
			case 0: // init
			{
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(timerPeriod);
				writeD(1330048);
				writeH(0x24); // 0x24
				writeD(nameId);
				writeH(0);
				break;
			}
			case 1: // update
			case 2: // Bluecrit
			case 5: // sucess
			{
				writeD(success);
				writeD(failure);
				writeD(timerDelay);
				writeD(timerPeriod);
				writeD(0);
				writeH(0);
				break;
			}
			case 3: // purpleCrit or CraftProc
			{
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(timerPeriod);
				writeD(1330048); // message
				writeH(0x24);
				writeD(nameId);
				writeH(0);
				break;
			}
			case 4: // cancel
			{
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(0);
				writeD(1330051);
				writeH(0);
				break;
			}
			case 6: // failed
			{
				writeD(success);
				writeD(failure);
				writeD(timerDelay);
				writeD(timerPeriod);
				writeD(1330050);
				writeH(0x24);
				writeD(nameId);
				writeH(0);
				break;
			}
			case 7:
			{
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(1200);
				writeD(1330050);
				writeH(0x24);
				writeD(nameId);
				writeH(0);
				break;
			}
		}
	}
}
