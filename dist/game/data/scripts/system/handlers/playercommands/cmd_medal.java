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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.utils.i18n.CustomMessageId;
import com.aionemu.gameserver.utils.i18n.LanguageHandler;

/**
 * Handles the {@code /medal} player command.<br>
 * This class allows players to interact with and manage medals within the game.<br>
 * It processes input to provide specific medal-related actions or information.
 * @author Maestross
 */
public class cmd_medal extends PlayerCommand
{
	/**
	 * Initializes the {@code medal} command.<br>
	 * This method sets up the command for players to receive medals.<br>
	 * It uses the parent class {@link PlayerCommand}.
	 */
	public cmd_medal()
	{
		super("medal");
	}
	
	/**
	 * Executes the command to award a specific medal to a player.<br>
	 * It checks the first parameter to determine which medal type to give.<br>
	 * The available types are silver, gold, platinum, and mithril.
	 * @param player The {@code Player} executing the command.
	 * @param params Variable arguments containing the medal type name.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length < 1)
		{
			PacketSendUtility.sendMessage(player, "Syntax: .medal <silver | gold | platinum | mithril>" + LanguageHandler.translate(CustomMessageId.EX_SILVER_INFO) + LanguageHandler.translate(CustomMessageId.EX_GOLD_INFO) + LanguageHandler.translate(CustomMessageId.EX_PLATIN_INFO) + LanguageHandler.translate(CustomMessageId.EX_MITHRIL_INFO));
			return;
		}
		
		if (params[0].equalsIgnoreCase("silver"))
		{
			silver_medal(player);
		}
		
		if (params[0].equalsIgnoreCase("gold"))
		{
			gold_medal(player);
		}
		
		if (params[0].equalsIgnoreCase("platinum"))
		{
			platinum_medal(player);
		}
		
		if (params[0].equalsIgnoreCase("mithril"))
		{
			mithril_medal(player);
		}
	}
	
	/**
	 * Handles the exchange of silver medals for gold medals.<br>
	 * It checks if the {@code player} has enough items in their inventory.<br>
	 * The required amount depends on the account membership status.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void silver_medal(Player player)
	{
		final Storage bag = player.getInventory();
		
		final long itemsInBag = bag.getItemCountByItemId(186000031);
		final int ss = player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3;
		if ((itemsInBag <= 0) || (itemsInBag >= 1000))
		{
			return;
		}
		
		if (itemsInBag < ss)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, LanguageHandler.translate(CustomMessageId.NOT_ENOUGH_SILVER));
			return;
		}
		
		final Item item = bag.getFirstItemByItemId(186000031);
		bag.decreaseByObjectId(item.getObjectId(), player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3);
		ItemService.addItem(player, 186000030, 5);
		PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.EXCHANGE_SILVER));
	}
	
	/**
	 * This method handles the exchange of gold medals for other items.<br>
	 * It checks if the {@code player} has enough gold medals in their inventory.<br>
	 * The required amount depends on the account membership status.<br>
	 * If successful, it removes gold medals and adds new items to the {@link Player}.
	 * @param player The {@code Player} who is performing the exchange.
	 */
	private void gold_medal(Player player)
	{
		final Storage bag = player.getInventory();
		
		final long itemsInBag = bag.getItemCountByItemId(186000030);
		final int ss = player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3;
		if ((itemsInBag <= 0) || (itemsInBag >= 1000))
		{
			return;
		}
		
		if (itemsInBag < ss)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, LanguageHandler.translate(CustomMessageId.NOT_ENOUGH_GOLD));
			return;
		}
		
		final Item item = bag.getFirstItemByItemId(186000030);
		bag.decreaseByObjectId(item.getObjectId(), player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3);
		ItemService.addItem(player, 186000096, 5);
		PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.EXCHANGE_GOLD));
	}
	
	/**
	 * This method handles the exchange of platinum medals.<br>
	 * It checks if the {@code player} has enough items in their inventory.<br>
	 * The required amount depends on the account membership status.<br>
	 * It removes the old item and gives new rewards to the {@link Player}.
	 * @param player The {@code Player} who is performing the exchange.
	 */
	private void platinum_medal(Player player)
	{
		final Storage bag = player.getInventory();
		
		final long itemsInBag = bag.getItemCountByItemId(186000096);
		final int ss = player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3;
		if ((itemsInBag <= 0) || (itemsInBag >= 1000))
		{
			return;
		}
		
		if (itemsInBag < ss)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, LanguageHandler.translate(CustomMessageId.NOT_ENOUGH_PLATIN));
			return;
		}
		
		final Item item = bag.getFirstItemByItemId(186000096);
		bag.decreaseByObjectId(item.getObjectId(), player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3);
		ItemService.addItem(player, 186000147, 3);
		PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.EXCHANGE_PLATIN));
	}
	
	/**
	 * This method handles the exchange of Mithril medals.<br>
	 * It checks if the {@code player} has enough items in their inventory.<br>
	 * The required amount depends on the account membership status.<br>
	 * If successful, it removes the old item and adds new ones.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void mithril_medal(Player player)
	{
		final Storage bag = player.getInventory();
		
		final long itemsInBag = bag.getItemCountByItemId(186000147);
		final int ss = player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3;
		if ((itemsInBag <= 0) || (itemsInBag >= 1000))
		{
			return;
		}
		
		if (itemsInBag < ss)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, LanguageHandler.translate(CustomMessageId.NOT_ENOUGH_MITHRIL));
			return;
		}
		
		final Item item = bag.getFirstItemByItemId(186000147);
		bag.decreaseByObjectId(item.getObjectId(), player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3);
		ItemService.addItem(player, 186000223, 2);
		PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.EXCHANGE_MITHRIL));
	}
}
