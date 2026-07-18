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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.List;

/**
 * This packet updates the visual appearance of a player character.<br>
 * It synchronizes changes such as equipment, costumes, or other cosmetic modifications to the client.
 * @author Avol modified by ATracer
 */
public class SM_UPDATE_PLAYER_APPEARANCE extends AionServerPacket
{
	public int playerId;
	public int size;
	public List<Item> items;
	
	/**
	 * Updates the visual appearance of a specific player.<br>
	 * This method sets the list of {@code Item} objects to be displayed.<br>
	 * It also calculates the total size of the provided list.
	 * @param playerId The unique identifier for the player.
	 * @param items A {@link List} containing the items to update.
	 */
	public SM_UPDATE_PLAYER_APPEARANCE(int playerId, List<Item> items)
	{
		this.playerId = playerId;
		this.items = items;
		size = items.size();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerId);
		
		int mask = 0;
		for (Item item : items)
		{
			if (item.getItemTemplate().isTwoHandWeapon())
			{
				final ItemSlot[] slots = ItemSlot.getSlotsFor(item.getEquipmentSlot());
				mask |= slots[0].getSlotIdMask();
			}
			else
			{
				mask |= item.getEquipmentSlot();
			}
		}
		
		writeD(mask); // item size DBS
		
		for (Item item : items)
		{
			writeD(item.getItemSkinTemplate().getTemplateId());
			final GodStone godStone = item.getGodStone();
			writeD(godStone != null ? godStone.getItemId() : 0);
			writeD(item.getItemColor());
			if (item.getItemTemplate().isAccessory())
			{
				if (item.getItemTemplate().isPlume())
				{
					float authorize = item.getEnchantOrAuthorizeLevel() / 5;
					if (item.getEnchantOrAuthorizeLevel() >= 5)
					{
						authorize = authorize > 2.0F ? 2.0F : authorize;
						writeD((int) authorize << 3);
					}
					else
					{
						writeD(0);
					}
				}
				else if (item.getItemTemplate().isBracelet())
				{
					if ((item.getEnchantOrAuthorizeLevel() >= 5) && (item.getEnchantOrAuthorizeLevel() < 10))
					{
						writeD(96);
					}
					else if (item.getEnchantOrAuthorizeLevel() >= 10)
					{
						writeD(160);
					}
					else
					{
						writeD(32);
					}
				}
				else
				{
					writeD(item.getEnchantOrAuthorizeLevel() >= 5 ? 2 : 0);
				}
			}
			else if ((item.getItemTemplate().isWeapon()) || (item.getItemTemplate().isTwoHandWeapon()))
			{
				writeD(item.getEnchantOrAuthorizeLevel() == 15 ? 2 : item.getEnchantOrAuthorizeLevel() >= 20 ? 4 : 0);
			}
			else
			{
				writeD(0);
			}
		}
	}
}
