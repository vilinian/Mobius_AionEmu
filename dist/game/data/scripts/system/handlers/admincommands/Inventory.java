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
package system.handlers.admincommands;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles administrative commands related to player inventories.<br>
 * This class allows administrators to manage {@link Item} objects for a {@link Player}.<br>
 * It provides functionality to view, add, or remove items from a character's inventory.
 * @author FrozenKiller
 */
public class Inventory extends AdminCommand
{
	private final List<Item> deleteItems = new ArrayList<>();
	
	/**
	 * Initializes a new instance of the {@link Inventory} class.<br>
	 * This constructor sets up the default command name for the admin system.
	 */
	public Inventory()
	{
		super("inventory");
	}
	
	/**
	 * Executes the command to clean a player's inventory.<br>
	 * It removes all items that are not currently equipped from the target player.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be "clean".
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length < 1)
		{
			onFail(admin, null);
			return;
		}
		
		final Player receiver = (Player) admin.getTarget();
		
		if (receiver == null)
		{
			PacketSendUtility.sendMessage(admin, "You should Target an Player or your self first !");
			return;
		}
		
		if (params[0].equalsIgnoreCase("clean"))
		{
			final List<Item> list = receiver.getInventory().getItems();
			for (Item item : list)
			{
				if (!item.isEquipped())
				{
					deleteItems.add(item);
				}
			}
			
			for (Item delItem : deleteItems)
			{
				receiver.getInventory().delete(delItem);
			}
			
			if (admin != receiver)
			{
				PacketSendUtility.sendMessage(receiver, "An Admin deleted all your Unequiped Items");
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "You deleted all your Unequiped Items");
			}
			
			deleteItems.clear();
			list.clear();
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
		PacketSendUtility.sendMessage(player, "This command deletes all Unequiped Items from Inventory");
		PacketSendUtility.sendMessage(player, "<usage //inventory | clean");
	}
}
