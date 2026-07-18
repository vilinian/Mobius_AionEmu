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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * This packet is used to add an item to the exchange window.<br>
 * It handles the request from a player to include a specific {@link Item} in their trade list.
 * @author Avol
 * @author ATracer
 */
public class SM_EXCHANGE_ADD_ITEM extends AionServerPacket
{
	private final Player player;
	private final int action;
	private final Item item;
	
	/**
	 * This packet handles adding an item to an exchange.<br>
	 * It stores the specific {@code Item}, the {@code Player} involved, and the {@code action} type.
	 * @param action The type of action being performed.
	 * @param item The {@link Item} object to be added.
	 * @param player The {@link Player} who is performing the action.
	 */
	public SM_EXCHANGE_ADD_ITEM(int action, Item item, Player player)
	{
		this.player = player;
		this.action = action;
		this.item = item;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final ItemTemplate itemTemplate = item.getItemTemplate();
		
		writeC(action); // 0 -self 1-other
		
		writeD(itemTemplate.getTemplateId());
		writeD(item.getObjectId());
		writeNameId(itemTemplate.getNameId());
		
		final ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
		itemInfoBlob.writeMe(getBuf());
	}
}
