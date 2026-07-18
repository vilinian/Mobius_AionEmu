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
package com.aionemu.gameserver.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service provides administrative tools and commands for managing the game server.<br>
 * It allows authorized users to perform actions such as modifying {@link Player} data or spawning {@link Item} objects.
 * @author KID
 */
public class AdminService
{
	private static final Logger itemLog = LoggerFactory.getLogger("GMITEMRESTRICTION");
	private final List<Integer> list;
	private static AdminService instance = new AdminService();
	
	/**
	 * Provides access to the singleton instance of {@link AdminService}.<br>
	 * Use this method to get the global service for admin operations.
	 * @return The single shared instance of {@code AdminService}.
	 */
	public static AdminService getInstance()
	{
		return instance;
	}
	
	/**
	 * Initializes a new instance of the {@link AdminService}.<br>
	 * It sets up the internal list for tracking items.<br>
	 * If trade item restrictions are enabled in {@code AdminConfig}, it calls {@code reload}.
	 */
	public AdminService()
	{
		list = new ArrayList<>();
		if (AdminConfig.ENABLE_TRADEITEM_RESTRICTION)
		{
			reload();
		}
	}
	
	/**
	 * Reloads the item restriction configuration.<br>
	 * This method clears the current {@code list}.<br>
	 * It reads new data from the {@code ./config/administration/item.restriction.txt} file.<br>
	 * The updated items are then added to the internal list.
	 */
	public void reload()
	{
		if (list.size() > 0)
		{
			list.clear();
		}
		
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader("./config/administration/item.restriction.txt"));
			String line = null;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith("#") || (line.trim().length() == 0))
				{
					continue;
				}
				
				final String pt = line.split("#")[0].replaceAll(" ", "");
				list.add(Integer.parseInt(pt));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		GameServer.log.info("[AdminService] loaded " + list.size() + " operational items.");
	}
	
	/**
	 * Checks if a {@link Player} has permission to perform an action.<br>
	 * This method validates the operation based on the provided {@code Item} and its {@code type}.<br>
	 * It returns {@code true} if the action is allowed, otherwise it returns {@code false}.
	 * @param player The player attempting to perform the action.
	 * @param target The player who is receiving the action.
	 * @param item The specific {@link Item} involved in the operation.
	 * @param type The category or type of the operation being performed.
	 * @return {@code true} if the operation is permitted, {@code false} otherwise.
	 */
	public boolean canOperate(Player player, Player target, Item item, String type)
	{
		return canOperate(player, target, item.getItemId(), type);
	}
	
	/**
	 * Checks if a {@link Player} is allowed to perform an action on another player.<br>
	 * This method validates permissions based on the item ID and its type.<br>
	 * It handles restrictions for specific GM access levels.
	 * @param player The player attempting to perform the action.
	 * @param target The player who is receiving the action.
	 * @param itemId The unique identifier of the item involved.
	 * @param type The category or name of the operation being performed.
	 * @return {@code true} if the action is permitted, {@code false} otherwise.
	 */
	public boolean canOperate(Player player, Player target, int itemId, String type)
	{
		if (!AdminConfig.ENABLE_TRADEITEM_RESTRICTION || ((target != null) && (target.getAccessLevel() > 0))) // allow between gms
		{
			return true;
		}
		
		if ((player.getAccessLevel() > 0) && (player.getAccessLevel() < 4))
		{
			// run check only for 1-3 level gms
			final boolean value = list.contains(itemId);
			String str = "GM " + player.getName() + "|" + player.getObjectId() + " (" + type + "): " + itemId + "|result=" + value;
			if (target != null)
			{
				str += "|target=" + target.getName() + "|" + target.getObjectId();
			}
			
			itemLog.info(str);
			if (!value)
			{
				PacketSendUtility.sendMessage(player, "You cannot use " + type + " with this item.");
			}
			
			return value;
		}
		
		return true;
	}
}
