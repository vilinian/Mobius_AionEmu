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

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /reskin} player command.<br>
 * This class allows players to change their visual appearance by applying a new skin.<br>
 * It interacts with {@link Player} data and manages item requirements for the transformation.
 * @author Chuck
 * @rework Ever'
 */
public class cmd_reskin extends PlayerCommand
{
	/**
	 * Registers the {@code reskin} command.<br>
	 * This allows players to change their visual appearance.<br>
	 * It initializes the command handler for the game server.
	 */
	public cmd_reskin()
	{
		super("reskin");
	}
	
	/**
	 * Executes the command to change an item's appearance.<br>
	 * It parses the provided parameters to identify the old and new item IDs.<br>
	 * The method verifies that both items are of the same type and checks for sufficient Vote Points.
	 * @param player The {@code Player} executing the command.
	 * @param params Variable arguments containing the old item ID and the new item ID.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length != 2)
		{
			PacketSendUtility.sendMessage(player, "syntax .reskin <Old Item> <New Item>");
			return;
		}
		
		@SuppressWarnings("unused")
		Player target = player;
		final VisibleObject creature = player.getTarget();
		if (player.getTarget() instanceof Player)
		{
			target = (Player) creature;
		}
		
		int oldItemId = 0;
		int newItemId = 0;
		
		try
		{
			String item = params[0];
			
			if (item.startsWith("[item:"))
			{
				Pattern id = Pattern.compile("\\[item:(\\d{9})");
				Matcher result = id.matcher(item);
				
				if (result.find())
				{
					oldItemId = Integer.parseInt(result.group(1));
				}
				else
				{
					oldItemId = Integer.parseInt(params[0]);
				}
				
				item = params[1];
				if (item.startsWith("[item:"))
				{
					id = Pattern.compile("\\[item:(\\d{9})");
					result = id.matcher(item);
					
					if (result.find())
					{
						newItemId = Integer.parseInt(result.group(1));
					}
					else
					{
						newItemId = Integer.parseInt(params[0]);
					}
				}
				else
				{
					PacketSendUtility.sendMessage(player, "syntax .reskin <Old Item> <New Item>");
					return;
				}
			}
			else
			{
				PacketSendUtility.sendMessage(player, "syntax .reskin <Old Item> <New Item>");
				return;
			}
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "syntax .reskin <Old Item> <New Item>");
			return;
		}
		
		final Storage storage = player.getInventory();
		final List<Item> oldItems = player.getInventory().getItemsByItemId(oldItemId);
		final List<Item> newItems = player.getInventory().getItemsByItemId(newItemId);
		
		// Iterator Ancien Item
		final Iterator<Item> oldIter = oldItems.iterator();
		final Item oldItem = oldIter.next();
		
		// Iterator Nouveau Item
		final Iterator<Item> newIter = newItems.iterator();
		final Item newItem = newIter.next();
		
		// verification que l'ancien item est dans l'inventaire
		if (oldItems.isEmpty())
		{
			PacketSendUtility.sendMessage(player, "You do not have this item in your inventory.");
			return;
		}
		
		// verification que les items sont du même type.
		if (newItem.getItemTemplate().isWeapon() && oldItem.getItemTemplate().isWeapon())
		{
			if (newItem.getItemTemplate().getWeaponType() != oldItem.getItemTemplate().getWeaponType())
			{
				PacketSendUtility.sendMessage(player, "You can not remodel different types of item.");
				return;
			}
		}
		else if (newItem.getItemTemplate().isArmor() && oldItem.getItemTemplate().isArmor())
		{
			if (newItem.getItemTemplate().getItemSlot() == oldItem.getItemTemplate().getItemSlot())
			{
				if (newItem.getItemTemplate().getArmorType() != oldItem.getItemTemplate().getArmorType())
				{
					PacketSendUtility.sendMessage(player, "You can not remodel different types of item.");
					return;
				}
			}
			else
			{
				PacketSendUtility.sendMessage(player, "You can not remodel different types of item.");
				return;
			}
		}
		
		final int tollPrice = 750;
		final long tolls = player.getClientConnection().getAccount().getToll();
		final RequestResponseHandler responseHandler = new RequestResponseHandler(player)
		{
			@Override
			public void acceptRequest(Creature p2, Player p)
			{
				if (tolls < tollPrice)
				{
					PacketSendUtility.sendMessage(p, "You don't have enought Vote Points (" + tolls + "). You need : " + tollPrice + " Vote Points.");
					return;
				}
				
				p.getClientConnection().getAccount().setToll(tolls - tollPrice);
				
			}
			
			@Override
			public void denyRequest(Creature p2, Player p)
			{
			}
		};
		
		final boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
		if (requested)
		{
			oldItem.setItemSkinTemplate(DataManager.ITEM_DATA.getItemTemplate(newItemId));
			storage.decreaseByItemId(newItemId, storage.getItemCountByItemId(newItemId));
			PacketSendUtility.sendBrightYellowMessage(player, "Your item " + params[0] + " just take the appearance of the item " + params[1] + ".");
			PacketSendUtility.sendMessage(player, "For changing the skin, you have use " + tollPrice + " Vote Points!");
		}
	}
	
	/**
	 * Handles the failure of an {@code execute} command.<br>
	 * It sends a syntax hint to the player.
	 * @param player The {@code Player} who attempted the command.
	 * @param message The error message associated with the failure.
	 */
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "syntax : .reskin <Old Item> <New Item>");
	}
}
