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
package system.handlers.ai;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.chest.ChestTemplate;
import com.aionemu.gameserver.model.templates.chest.KeyItem;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Handles the artificial intelligence logic for {@link ChestTemplate} objects.<br>
 * This class manages how chests interact with players and handle item drops.
 * @author ATracer, xTz
 */
@AIName("chest")
public class ChestAI2 extends ActionItemNpcAI2
{
	private ChestTemplate chestTemplate;
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		chestTemplate = DataManager.CHEST_DATA.getChestTemplate(getNpcId());
		
		if (chestTemplate == null)
		{
			return;
		}
		
		super.handleDialogStart(player);
	}
	
	/**
	 * Handles the completion of an item usage action.<br>
	 * This method is called when a {@link Player} finishes using an item.<br>
	 * It checks if the owner is in an instance before processing.
	 * @param player The {@code Player} who finished using the item.
	 */
	@Override
	protected void handleUseItemFinish(Player player)
	{
		if (analyzeOpening(player))
		{
			if (getOwner().isInState(CreatureState.DEAD))
			{
				AuditLogger.info(player, "Attempted multiple Chest looting!");
				return;
			}
			
			AI2Actions.dieSilently(this, player);
			final Collection<Player> players = new HashSet<>();
			if (player.isInGroup2())
			{
				for (Player member : player.getPlayerGroup2().getOnlineMembers())
				{
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE))
					{
						players.add(member);
					}
				}
			}
			else if (player.isInAlliance2())
			{
				for (Player member : player.getPlayerAlliance2().getOnlineMembers())
				{
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE))
					{
						players.add(member);
					}
				}
			}
			else
			{
				players.add(player);
			}
			
			DropRegistrationService.getInstance().registerDrop(getOwner(), player, players.stream().mapToInt(Player::getLevel).max().orElse(0), players);
			DropService.getInstance().requestDropList(player, getObjectId());
			super.handleUseItemFinish(player);
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1111301));
		}
	}
	
	/**
	 * Checks if the {@code player} has all required items to open the chest.<br>
	 * It verifies that every {@link KeyItem} is present in the inventory with the correct quantity.<br>
	 * If requirements are met, it removes the items from the inventory and returns {@code true}.<br>
	 * Returns {@code false} if any item is missing or insufficient.
	 * @param player The {@code Player} attempting to open the chest.
	 * @return {@code true} if the opening is successful, otherwise {@code false}.
	 */
	private boolean analyzeOpening(Player player)
	{
		final List<KeyItem> keyItems = chestTemplate.getKeyItem();
		int i = 0;
		for (KeyItem keyItem : keyItems)
		{
			if (keyItem.getItemId() == 0)
			{
				return true;
			}
			
			final Item item = player.getInventory().getFirstItemByItemId(keyItem.getItemId());
			if (item != null)
			{
				if (item.getItemCount() != keyItem.getQuantity())
				{
					int _i = 0;
					for (Item findedItem : player.getInventory().getItemsByItemId(keyItem.getItemId()))
					{
						_i += findedItem.getItemCount();
					}
					
					if (_i < keyItem.getQuantity())
					{
						return false;
					}
				}
				
				i++;
				continue;
			}
			
			return false;
		}
		
		if (i == keyItems.size())
		{
			for (KeyItem keyItem : keyItems)
			{
				player.getInventory().decreaseByItemId(keyItem.getItemId(), keyItem.getQuantity());
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method handles the logic after a dialog ends.<br>
	 * It is called when a {@link Player} finishes interacting with an NPC.
	 * @param player The {@code Player} who finished the dialog.
	 */
	@Override
	protected void handleDialogFinish(Player player)
	{
	}
}
