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

import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet updates the client about the status of a gathering action.<br>
 * It is used to synchronize information regarding {@link GatherableTemplate} interactions.
 * @author ATracer
 * @author orz
 * @author Antraxx
 */
public class SM_GATHER_UPDATE extends AionServerPacket
{
	private final GatherableTemplate template;
	private final int action;
	private final int itemId;
	private final int success;
	private final int failure;
	private final int nameId;
	
	/**
	 * Updates the client on a gathering action result.<br>
	 * This packet sends information about the gathered item and the outcome of the attempt.
	 * @param template The {@link GatherableTemplate} for the object being interacted with.
	 * @param material The {@link Material} data containing the item details.
	 * @param success The success status of the gathering action.
	 * @param failure The failure status of the gathering action.
	 * @param action The specific type of action performed by the player.
	 */
	public SM_GATHER_UPDATE(GatherableTemplate template, Material material, int success, int failure, int action)
	{
		this.action = action;
		this.template = template;
		itemId = material.getItemid();
		this.success = success;
		this.failure = failure;
		nameId = material.getNameid();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(template.getHarvestSkill());
		writeC(action);
		writeD(itemId);
		
		switch (action)
		{
			case 0:
			{
				writeQ(3);
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(1200); // timer??
				writeD(1330011); // ??text??skill??
				writeH(0x24); // 0x24
				writeD(nameId);
				writeH(0); // 0x24
				break;
			}
			case 1:
			{
				writeQ(3);
				writeD(success);
				writeD(failure);
				writeD(700); // unk timer??
				writeD(1200); // unk timer??
				writeD(0); // unk timer??writeD(700);
				writeH(0);
				break;
			}
			case 2:
			{
				writeQ(3);
				writeD(success);
				writeD(failure);
				writeD(700); // unk timer??
				writeD(1200); // unk timer??
				writeD(0); // unk timer??writeD(700);
				writeH(0);
				break;
			}
			case 3:
			{
				writeQ(3);
				writeD(success);
				writeD(failure);
				writeD(700); // unk timer??
				writeD(1200); // unk timer??
				writeD(0); // unk timer??writeD(700);
				writeH(0);
				break;
			}
			case 5: // you have stopped gathering
			{
				writeQ(3);
				writeD(0);
				writeD(0);
				writeD(700); // unk timer??
				writeD(1200); // unk timer??
				writeD(1330080); // unk timer??writeD(700);
				writeH(0);
				break;
			}
			case 6:
			{
				writeQ(3);
				writeD(success);
				writeD(failure);
				writeD(700); // unk timer??
				writeD(1200); // unk timer??
				writeD(0); // unk timer??writeD(700);
				writeH(0);
				break;
			}
			case 7:
			{
				writeQ(3);
				writeD(success);
				writeD(failure);
				writeD(0);
				writeD(1200); // timer??
				writeD(1330079); // ??text??skill??
				writeH(0x24); // 0x24
				writeD(nameId);
				writeH(0); // 0x24
				break;
			}
		}
	}
}
