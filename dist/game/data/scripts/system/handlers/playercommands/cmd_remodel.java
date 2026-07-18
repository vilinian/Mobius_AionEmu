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
package system.handlers.playercommands;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.services.item.ItemRemodelService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the player command for remodeling an item.<br>
 * It uses {@link ItemRemodelService} to process the request and updates the player's appearance.
 * @author Kashim
 */
public class cmd_remodel extends PlayerCommand
{
	/**
	 * Initializes the {@code remodel} command.<br>
	 * This allows administrators to change item appearances.<br>
	 * It registers the command with the system.
	 */
	public cmd_remodel()
	{
		super("remodel");
	}
	
	/**
	 * Executes the preview command for a specific item.<br>
	 * This method validates the input and calls {@code commandPreviewRemodelItem}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params An array of {@code String} containing the item ID.
	 */
	public void executeCommand(Player admin, String[] params)
	{
		if (params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "Syntax: .remodel <itemid>\n");
			return;
		}
		
		if (params.length == 1)
		{
			// Use target
			final int itemId = Integer.parseInt(params[0]);
			if (admin.getInventory().decreaseByItemId(186000202, 1))
			{
				if (remodelItem(admin, itemId))
				{
					PacketSendUtility.sendMessage(admin, "Successfully remodelled an item of the player!");
					PacketSendUtility.broadcastPacket(admin, new SM_UPDATE_PLAYER_APPEARANCE(admin.getObjectId(), admin.getEquipment().getEquippedItemsWithoutStigma()), true);
				}
				else
				{
					PacketSendUtility.sendMessage(admin, "Was not able to remodel an item of the player!");
				}
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "You do not meet the requirements !");
			}
		}
	}
	
	/**
	 * Changes the appearance of an equipped item for a specific player.<br>
	 * It searches for a matching weapon or armor slot based on the provided {@code itemId}.<br>
	 * This method updates the database and sends a packet to the client.
	 * @param player The {@link Player} who will receive the remodel.
	 * @param itemId The unique identifier of the item template to use for remodeling.
	 * @return {@code true} if the item was successfully remodeled, otherwise {@code false}.
	 */
	private boolean remodelItem(Player player, int itemId)
	{
		final ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (template == null)
		{
			return false;
		}
		
		final Equipment equip = player.getEquipment();
		if (equip == null)
		{
			return false;
		}
		
		for (Item item : equip.getEquippedItemsWithoutStigma())
		{
			if (item.getItemTemplate().isWeapon())
			{
				if (item.getItemTemplate().getWeaponType() == template.getWeaponType())
				{
					ItemRemodelService.systemRemodelItem(player, item, template);
					PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
					DAOManager.getDAO(InventoryDAO.class).store(item, player);
					return true;
				}
			}
			else if (item.getItemTemplate().isArmor())
			{
				if (item.getItemTemplate().getItemSlot() == template.getItemSlot())
				{
					ItemRemodelService.systemRemodelItem(player, item, template);
					PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
					DAOManager.getDAO(InventoryDAO.class).store(item, player);
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Executes the command to add an item to a player.<br>
	 * It parses the provided parameters to identify the item ID and quantity.<br>
	 * The method handles both self-giving and giving items to other players.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the target name, item link, and count.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		executeCommand(player, params);
	}
}
