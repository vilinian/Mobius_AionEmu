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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.decomposable.SelectItem;
import com.aionemu.gameserver.model.templates.decomposable.SelectItems;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SELECT_ITEM_ADD;
import com.aionemu.gameserver.services.item.ItemService;

/**
 * Handles the client request to confirm a selection from a decomposable item.<br>
 * This packet processes the user's choice and triggers the corresponding server actions.
 * @author Alcapwnd
 */
public class CM_SELECTITEM_OK extends AionClientPacket
{
	private int uniqueItemId;
	private int index;
	@SuppressWarnings("unused")
	private int unk;
	
	/**
	 * This method initializes a new {@code CM_SELECTITEM_OK} packet.<br>
	 * It sets the required network states for the connection.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the {@link AionConnection}.
	 * @param restStates Additional states associated with the {@link AionConnection}.
	 */
	public CM_SELECTITEM_OK(int opcode, AionConnection.State state, AionConnection.State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		uniqueItemId = readD();
		unk = readD();
		index = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final Item item = player.getInventory().getItemByObjId(uniqueItemId);
		if (item == null)
		{
			return;
		}
		
		sendPacket(new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), player.getObjectId().intValue(), uniqueItemId, item.getItemId(), 0, 1));
		final boolean delete = player.getInventory().decreaseByObjectId(uniqueItemId, 1L);
		if (delete)
		{
			final SelectItems selectitem = DataManager.DECOMPOSABLE_SELECT_ITEM_DATA.getSelectItem(player.getPlayerClass(), player.getRace(), item.getItemId());
			final SelectItem st = selectitem.getItems().get(index);
			ItemService.addItem(player, st.getSelectItemId(), st.getCount());
			sendPacket(new SM_SELECT_ITEM_ADD(uniqueItemId, 0));
		}
		
	}
	
}
