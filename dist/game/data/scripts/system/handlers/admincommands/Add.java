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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.AdminService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command to add items or currency to a player.<br>
 * It validates the input and uses {@link ItemService} to grant the specified objects.
 * @author Phantom, ATracer, Source
 */
public class Add extends AdminCommand
{
	/**
	 * Initializes the {@code Add} command.<br>
	 * This class handles adding items or objects to players.<br>
	 * It extends the base {@link AdminCommand} class.
	 */
	public Add()
	{
		super("add");
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
		if ((params.length < 0) || (params.length < 1))
		{
			onFail(player, null);
			return;
		}
		
		int itemId = 0;
		long itemCount = 1;
		Player receiver;
		
		try
		{
			String item = params[0];
			
			// Some item links have space before Id
			if (item.contains("[item:"))
			{
				item = params[1];
				final Pattern id = Pattern.compile("(\\d{9})");
				final Matcher result = id.matcher(item);
				if (result.find())
				{
					itemId = Integer.parseInt(result.group(1));
				}
				
				if (params.length == 3)
				{
					itemCount = Long.parseLong(params[2]);
				}
			}
			else if (item.contains("[@item:"))
			{
				final Pattern id = Pattern.compile("\\[@item:(\\d{9})");
				final Matcher result = id.matcher(item);
				
				if (result.find())
				{
					itemId = Integer.parseInt(result.group(1));
				}
				else
				{
					itemId = Integer.parseInt(params[0]);
				}
				
				if (params.length == 2)
				{
					itemCount = Long.parseLong(params[1]);
				}
			}
			else
			{
				final Pattern id = Pattern.compile("\\[item:(\\d{9})");
				final Matcher result = id.matcher(item);
				
				if (result.find())
				{
					itemId = Integer.parseInt(result.group(1));
				}
				else
				{
					itemId = Integer.parseInt(params[0]);
				}
				
				if (params.length == 2)
				{
					itemCount = Long.parseLong(params[1]);
				}
			}
			
			receiver = player;
		}
		catch (NumberFormatException e)
		{
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
			if (receiver == null)
			{
				PacketSendUtility.sendMessage(player, "Could not find a player by that name.");
				return;
			}
			
			try
			{
				String item = params[1];
				
				// Some item links have space before Id
				if (item.contains("[item:"))
				{
					item = params[2];
					final Pattern id = Pattern.compile("(\\d{9})");
					final Matcher result = id.matcher(item);
					if (result.find())
					{
						itemId = Integer.parseInt(result.group(1));
					}
					
					if (params.length == 4)
					{
						itemCount = Long.parseLong(params[3]);
					}
				}
				else if (item.contains("[@item:"))
				{
					final Pattern id = Pattern.compile("\\[@item:(\\d{9})");
					final Matcher result = id.matcher(item);
					
					if (result.find())
					{
						itemId = Integer.parseInt(result.group(1));
					}
					else
					{
						itemId = Integer.parseInt(params[1]);
					}
					
					if (params.length == 3)
					{
						itemCount = Long.parseLong(params[2]);
					}
				}
				else
				{
					final Pattern id = Pattern.compile("\\[item:(\\d{9})");
					final Matcher result = id.matcher(item);
					
					if (result.find())
					{
						itemId = Integer.parseInt(result.group(1));
					}
					else
					{
						itemId = Integer.parseInt(params[1]);
					}
					
					if (params.length == 3)
					{
						itemCount = Long.parseLong(params[2]);
					}
				}
			}
			catch (NumberFormatException ex)
			{
				PacketSendUtility.sendMessage(player, "You must give number to itemid.");
				return;
			}
			catch (Exception ex2)
			{
				PacketSendUtility.sendMessage(player, "Occurs an error.");
				return;
			}
		}
		
		if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null)
		{
			PacketSendUtility.sendMessage(player, "Item id is incorrect: " + itemId);
			return;
		}
		
		if (!AdminService.getInstance().canOperate(player, receiver, itemId, "command //add"))
		{
			return;
		}
		
		final long count = ItemService.addItem(receiver, itemId, itemCount);
		
		if (count == 0)
		{
			if (player != receiver)
			{
				PacketSendUtility.sendMessage(player, "You successfully gave " + itemCount + " x [item:" + itemId + "] to " + receiver.getName() + ".");
				PacketSendUtility.sendMessage(receiver, "You successfully received " + itemCount + " x [item:" + itemId + "] from " + player.getName() + ".");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "You successfully received " + itemCount + " x [item:" + itemId + "] ID: " + itemId);
			}
		}
		else
		{
			PacketSendUtility.sendMessage(player, "Item couldn't be added");
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
		PacketSendUtility.sendMessage(player, "syntax //add <player> <item Id | link> <quantity>");
		PacketSendUtility.sendMessage(player, "syntax //add <item Id | link> <quantity>");
		PacketSendUtility.sendMessage(player, "syntax //add <item Id | link>");
	}
}
