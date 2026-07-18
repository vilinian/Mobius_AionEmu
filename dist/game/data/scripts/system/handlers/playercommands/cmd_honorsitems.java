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
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /honorsitems} player command.<br>
 * This class allows players to manage and view items obtained through honor systems.<br>
 * It interacts with {@link ItemService} to process item-related actions.
 * @author Maestross
 */
public class cmd_honorsitems extends PlayerCommand
{
	/**
	 * Initializes the {@link cmd_honorsitems} command handler.<br>
	 * This constructor sets up the command for retrieving honor items.
	 */
	public cmd_honorsitems()
	{
		super("honoritems");
	}
	
	/**
	 * Executes the honor items command for a player.<br>
	 * It handles requests to view prices, item types, or specific gear cases based on the input parameters.
	 * @param player The {@code Player} executing the command.
	 * @param params Variable arguments used to specify the type of armor, price info, or specific item case.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length < 1)
		{
			PacketSendUtility.sendMessage(player, "Syntax: .honoritems <plate | leather | cloth | chain | weapons>");
			PacketSendUtility.sendMessage(player, "Syntax: .honoritems <pprices | lprices | cprices | ccprices | wprices>");
			return;
		}
		
		if (params[0].equalsIgnoreCase("plate"))
		{
			plate(player);
		}
		
		if (params[0].equalsIgnoreCase("leather"))
		{
			leather(player);
		}
		
		if (params[0].equalsIgnoreCase("cloth"))
		{
			cloth(player);
		}
		
		if (params[0].equalsIgnoreCase("chain"))
		{
			chain(player);
		}
		
		if (params[0].equalsIgnoreCase("weapons"))
		{
			weapons(player);
		}
		
		if (params[0].equalsIgnoreCase("pprices"))
		{
			plateInfo(player);
		}
		
		if (params[0].equalsIgnoreCase("lprices"))
		{
			leatherInfo(player);
		}
		
		if (params[0].equalsIgnoreCase("cprices"))
		{
			clothInfo(player);
		}
		
		if (params[0].equalsIgnoreCase("ccprices"))
		{
			chainInfo(player);
		}
		
		if (params[0].equalsIgnoreCase("wprices"))
		{
			weaponsInfo(player);
		}
		
		if (params[0].equalsIgnoreCase("1"))
		{// Plate Breast
			case1(player);
		}
		
		if (params[0].equalsIgnoreCase("2"))
		{// Plate Hands
			case2(player);
		}
		
		if (params[0].equalsIgnoreCase("3"))
		{// Plate Shoes
			case3(player);
		}
		
		if (params[0].equalsIgnoreCase("4"))
		{// Plate Pants
			case4(player);
		}
		
		if (params[0].equalsIgnoreCase("5"))
		{// Plate Shoulders
			case5(player);
		}
		
		if (params[0].equalsIgnoreCase("6"))
		{// Leather Breast
			case6(player);
		}
		
		if (params[0].equalsIgnoreCase("7"))
		{// Leather Hands
			case7(player);
		}
		
		if (params[0].equalsIgnoreCase("8"))
		{// Leather Shoes
			case8(player);
		}
		
		if (params[0].equalsIgnoreCase("9"))
		{// Leather Pants
			case9(player);
		}
		
		if (params[0].equalsIgnoreCase("10"))
		{// Leather Shoulders
			case10(player);
		}
		
		if (params[0].equalsIgnoreCase("11"))
		{// Cloth Breast
			case11(player);
		}
		
		if (params[0].equalsIgnoreCase("12"))
		{// Cloth Hands
			case12(player);
		}
		
		if (params[0].equalsIgnoreCase("13"))
		{// Cloth Shoes
			case13(player);
		}
		
		if (params[0].equalsIgnoreCase("14"))
		{// Cloth Pants
			case14(player);
		}
		
		if (params[0].equalsIgnoreCase("15"))
		{// Cloth Shoulders
			case15(player);
		}
		
		if (params[0].equalsIgnoreCase("16"))
		{// Chain Breast
			case16(player);
		}
		
		if (params[0].equalsIgnoreCase("17"))
		{// Chain Hands
			case17(player);
		}
		
		if (params[0].equalsIgnoreCase("18"))
		{// Chain Shoes
			case18(player);
		}
		
		if (params[0].equalsIgnoreCase("19"))
		{// Chain Pants
			case19(player);
		}
		
		if (params[0].equalsIgnoreCase("20"))
		{// Chain Shoulders
			case20(player);
		}
		
		if (params[0].equalsIgnoreCase("21"))
		{// Weapon Sword
			case21(player);
		}
		
		if (params[0].equalsIgnoreCase("22"))
		{// Weapon Greatsword
			case22(player);
		}
		
		if (params[0].equalsIgnoreCase("23"))
		{// Weapon Longbow
			case23(player);
		}
		
		if (params[0].equalsIgnoreCase("24"))
		{// Weapon Dagger
			case24(player);
		}
		
		if (params[0].equalsIgnoreCase("25"))
		{// Weapon Orb
			case25(player);
		}
		
		if (params[0].equalsIgnoreCase("26"))
		{// Weapon Tome
			case26(player);
		}
		
		if (params[0].equalsIgnoreCase("27"))
		{// Weapon Staff
			case27(player);
		}
		
		if (params[0].equalsIgnoreCase("28"))
		{// Weapon Mace
			case28(player);
		}
		
		if (params[0].equalsIgnoreCase("29"))
		{// Weapon Shield
			case29(player);
		}
		
		if (params[0].equalsIgnoreCase("30"))
		{// Weapon Spear
			case30(player);
		}
	}
	
	/**
	 * Displays the prices for various plate armor pieces to the player.<br>
	 * It sends a list of items including their AP and Medal costs.
	 * @param player The {@link Player} who will receive the information messages.
	 */
	private void plateInfo(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Armor Plate Prices");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 110601342] AP: 4329504 Medals: 105"); // Plate Breast
		PacketSendUtility.sendMessage(player, "[item: 111601305] AP: 2164752 Medals: 52"); // Plate Hands
		PacketSendUtility.sendMessage(player, "[item: 114601291] AP: 2164752 Medals: 52"); // Plate Shoes
		PacketSendUtility.sendMessage(player, "[item: 113601294] AP: 3247344 Medals: 78"); // Plate Pants
		PacketSendUtility.sendMessage(player, "[item: 112601285] AP: 2164752 Medals: 52"); // Plate Shoulders
		PacketSendUtility.sendMessage(player, "----------------");
	}
	
	/**
	 * Displays the prices for leather armor items to the player.<br>
	 * It sends a list of specific item IDs with their required AP and Medals.
	 * @param player The {@link Player} who will receive the messages.
	 */
	private void leatherInfo(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Leather Armor Prices");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 110301393] AP: 4329504 Medals: 105"); // Leather Breast
		PacketSendUtility.sendMessage(player, "[item: 111301334] AP: 2164752 Medals: 52"); // Leather Hands
		PacketSendUtility.sendMessage(player, "[item: 114301393] AP: 2164752 Medals: 52"); // Leather Shoes
		PacketSendUtility.sendMessage(player, "[item: 113301358] AP: 3247344 Medals: 78"); // Leather Pants
		PacketSendUtility.sendMessage(player, "[item: 112301277] AP: 2164752 Medals: 52"); // Leather Shoulders
		PacketSendUtility.sendMessage(player, "----------------");
	}
	
	/**
	 * Displays the prices for cloth armor items to the player.<br>
	 * It sends a list of specific item IDs along with their AP and Medal costs.
	 * @param player The {@link Player} who will receive the messages.
	 */
	private void clothInfo(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Cloth Armor prices");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 110101485] AP: 4329504 Medals: 105"); // Cloth Breast
		PacketSendUtility.sendMessage(player, "[item: 111101339] AP: 2164752 Medals: 52"); // Cloth Hands
		PacketSendUtility.sendMessage(player, "[item: 114101387] AP: 2164752 Medals: 52"); // Cloth Shoes
		PacketSendUtility.sendMessage(player, "[item: 113101356] AP: 3247344 Medals: 78"); // Cloth Pants
		PacketSendUtility.sendMessage(player, "[item: 112101296] AP: 2164752 Medals: 52"); // Cloth Shoulders
		PacketSendUtility.sendMessage(player, "----------------");
	}
	
	/**
	 * Displays the prices for various chain armor items.<br>
	 * This method sends a list of item IDs and their costs to the player.
	 * @param player The {@link Player} who will receive the messages.
	 */
	private void chainInfo(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Chain Armor Prices");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 110501368] AP: 4329504 Medals: 105"); // Chain Breast
		PacketSendUtility.sendMessage(player, "[item: 111501326] AP: 2164752 Medals: 52"); // Chain Hands
		PacketSendUtility.sendMessage(player, "[item: 114501349] AP: 2164752 Medals: 52"); // Chain Shoes
		PacketSendUtility.sendMessage(player, "[item: 113501341] AP: 3247344 Medals: 78"); // Chain Pants
		PacketSendUtility.sendMessage(player, "[item: 112501266] AP: 2164752 Medals: 52"); // Chain Shoulders
		PacketSendUtility.sendMessage(player, "----------------");
	}
	
	/**
	 * Displays the prices for various weapons to the player.<br>
	 * It sends a list of items with their required AP and Medals.
	 * @param player The {@code Player} who will receive the messages.
	 */
	private void weaponsInfo(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Weapon Pprices");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 100001412] AP: 6494256 Medals: 156"); // Weapon Sword
		PacketSendUtility.sendMessage(player, "[item: 100901105] AP: 6494256 Medals: 156"); // Weapon Greatsword
		PacketSendUtility.sendMessage(player, "[item: 101701134] AP: 6494256 Medals: 156"); // Weapon Longbow
		PacketSendUtility.sendMessage(player, "[item: 100201251] AP: 6494256 Medals: 156"); // Weapon Dagger
		PacketSendUtility.sendMessage(player, "[item: 100501097] AP: 6494256 Medals: 156"); // Weapon Orb
		PacketSendUtility.sendMessage(player, "[item: 100601153] AP: 6494256 Medals: 156"); // Weapon Tome
		PacketSendUtility.sendMessage(player, "[item: 101501123] AP: 6494256 Medals: 156"); // Weapon Staff
		PacketSendUtility.sendMessage(player, "[item: 100101089] AP: 6494256 Medals: 156"); // Weapon Mace
		PacketSendUtility.sendMessage(player, "[item: 115001462] AP: 4329504 Medals: 105"); // Weapon Shield
		PacketSendUtility.sendMessage(player, "[item: 101301042] AP: 6494256 Medals: 156"); // Weapon Spear
		PacketSendUtility.sendMessage(player, "----------------");
	}
	
	/**
	 * Displays information about plate armor items to the player.<br>
	 * It shows a list of specific item IDs for different body parts.<br>
	 * The message also provides instructions on how to use the {@code .honoritems} command.
	 * @param player The {@link Player} who will receive the messages.
	 */
	private void plate(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Plates");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 110601342] (1)"); // Plate Breast
		PacketSendUtility.sendMessage(player, "[item: 111601305] (2)"); // Plate Hands
		PacketSendUtility.sendMessage(player, "[item: 114601291] (3)"); // Plate Shoes
		PacketSendUtility.sendMessage(player, "[item: 113601294] (4)"); // Plate Pants
		PacketSendUtility.sendMessage(player, "[item: 112601285] (5)"); // Plate Shoulders
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendYellowMessageOnCenter(player, "Use now .honoritems and the corresponding ID number (Examplel: .honoritems 1");
	}
	
	/**
	 * Displays information about leather items to the player.<br>
	 * It shows a list of specific item IDs and their quantities.<br>
	 * The message instructs the user on how to use the {@code .honoritems} command.
	 * @param player The {@link Player} who will receive the messages.
	 */
	private void leather(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Leather");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 110301393] (6)"); // Leather Breast
		PacketSendUtility.sendMessage(player, "[item: 111301334] (7)"); // Leather Hands
		PacketSendUtility.sendMessage(player, "[item: 114301393] (8)"); // Leather Shoes
		PacketSendUtility.sendMessage(player, "[item: 113301358] (9)"); // Leather Pants
		PacketSendUtility.sendMessage(player, "[item: 112301277] (10)"); // Leather Shoulders
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendYellowMessageOnCenter(player, "Use now .honoritems and the corresponding ID number (Examplel: .honoritems 6");
	}
	
	/**
	 * Displays information about cloth items to the player.<br>
	 * It lists specific item IDs and their corresponding numbers.<br>
	 * It also provides instructions on how to use the {@code .honoritems} command.
	 * @param player The {@link Player} who will receive the messages.
	 */
	private void cloth(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Cloth");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 110101485] (11)"); // Cloth Breast
		PacketSendUtility.sendMessage(player, "[item: 111101339] (12)"); // Cloth Hands
		PacketSendUtility.sendMessage(player, "[item: 114101387] (13)"); // Cloth Shoes
		PacketSendUtility.sendMessage(player, "[item: 113101356] (14)"); // Cloth Pants
		PacketSendUtility.sendMessage(player, "[item: 112101296] (15)"); // Cloth Shoulders
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendYellowMessageOnCenter(player, "Use now .honoritems and the corresponding ID number (Examplel: .honoritems 11");
	}
	
	/**
	 * Displays information about chain items to the player.<br>
	 * It shows a list of specific item IDs and their corresponding numbers.<br>
	 * The method also provides instructions on how to use the {@code .honoritems} command.
	 * @param player The {@link Player} who will receive the messages.
	 */
	private void chain(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Chain");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 110501368] (16)"); // Chain Breast
		PacketSendUtility.sendMessage(player, "[item: 111501326] (17)"); // Chain Hands
		PacketSendUtility.sendMessage(player, "[item: 114501349] (18)"); // Chain Shoes
		PacketSendUtility.sendMessage(player, "[item: 113501341] (19)"); // Chain Pants
		PacketSendUtility.sendMessage(player, "[item: 112501266] (20)"); // Chain Shoulders
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendYellowMessageOnCenter(player, "Use now .honoritems and the corresponding ID number (Examplel: .honoritems 16");
	}
	
	/**
	 * Displays a list of available weapons to the player.<br>
	 * It shows specific item IDs and their corresponding numbers.<br>
	 * The method also provides instructions on how to use the {@code .honoritems} command.
	 * @param player The {@link Player} who will receive the messages.
	 */
	private void weapons(Player player)
	{
		PacketSendUtility.sendYellowMessageOnCenter(player, "Weapons");
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendMessage(player, "[item: 100001412] (21)"); // Weapon Sword
		PacketSendUtility.sendMessage(player, "[item: 100901105] (22)"); // Weapon Greatsword
		PacketSendUtility.sendMessage(player, "[item: 101701134] (23)"); // Weapon Longbow
		PacketSendUtility.sendMessage(player, "[item: 100201251] (24)"); // Weapon Dagger
		PacketSendUtility.sendMessage(player, "[item: 100501097] (25)"); // Weapon Orb
		PacketSendUtility.sendMessage(player, "[item: 100601153] (26)"); // Weapon Tome
		PacketSendUtility.sendMessage(player, "[item: 101501123] (27)"); // Weapon Staff
		PacketSendUtility.sendMessage(player, "[item: 100101089] (28)"); // Weapon Mace
		PacketSendUtility.sendMessage(player, "[item: 115001462] (29)"); // Weapon Shield
		PacketSendUtility.sendMessage(player, "[item: 101301042] (30)"); // Weapon Spear
		PacketSendUtility.sendMessage(player, "----------------");
		PacketSendUtility.sendYellowMessageOnCenter(player, "Use now .honoritems and the corresponding ID number (Examplel: .honoritems 21");
	}
	
	/**
	 * Handles the exchange of medals for a Plate Breast.<br>
	 * It checks if the {@code player} has enough AP and medals.<br>
	 * If requirements are met, it deducts resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case1(Player player)
	{// Plate Breast
		final Storage bag = player.getInventory();
		final int count = 105;
		final int ap = 4329504;
		final int id = 110601342;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a specific reward.<br>
	 * This method checks if the {@link Player} has enough AP and medals.<br>
	 * It deducts the required resources and grants the new item.
	 * @param player The {@code Player} who is performing the action.
	 */
	private void case2(Player player)
	{// Plate Hands
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 111601305;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Plate Shoes.<br>
	 * Checks if the {@code player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case3(Player player)
	{// Plate Shoes
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 114601291;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Plate Pants.<br>
	 * This method checks if the {@code player} has enough AP and medals.<br>
	 * It deducts the required resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case4(Player player)
	{// Plate Pants
		final Storage bag = player.getInventory();
		final int count = 78;
		final int ap = 3247344;
		final int id = 113601294;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Plate Shoulders.<br>
	 * Checks if the {@code player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case5(Player player)
	{// Plate Shoulders
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 112601285;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a Leather Breast.<br>
	 * Checks if the {@link Player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@code Player} performing the action.
	 */
	private void case6(Player player)
	{// Leather Breast
		final Storage bag = player.getInventory();
		final int count = 105;
		final int ap = 4329504;
		final int id = 110301393;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Leather Hands.<br>
	 * Checks if the {@code player} has enough AP and required medals.<br>
	 * Deducts resources and grants the new item to the {@link Player}.
	 * @param player The {@code Player} performing the action.
	 */
	private void case7(Player player)
	{// Leather Hands
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 111301334;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Leather Shoes.<br>
	 * Checks if the {@code player} has enough AP and required medals.<br>
	 * Deducts resources and grants the new item to the {@link Player}.
	 * @param player The {@code Player} performing the action.
	 */
	private void case8(Player player)
	{// Leather Shoes
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 114301393;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for leather pants.<br>
	 * This method checks if the {@link Player} has enough AP and medals.<br>
	 * It deducts the required resources and grants the new item.
	 * @param player The {@code Player} who is performing the exchange.
	 */
	private void case9(Player player)
	{// Leather Pants
		final Storage bag = player.getInventory();
		final int count = 78;
		final int ap = 3247344;
		final int id = 113301358;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Leather Shoulders.<br>
	 * Checks if the {@code player} has enough AP and required medals.<br>
	 * Deducts resources and grants the new item to the {@link Player}.
	 * @param player The {@code Player} performing the action.
	 */
	private void case10(Player player)
	{// Leather Shoulders
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 112301277;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * This method rewards a {@link Player} with a Cloth Breastplate.<br>
	 * It checks if the player has enough AP and medals in their inventory.<br>
	 * If requirements are met, it deducts the costs and adds the new item.
	 * @param player The {@code Player} who is receiving the reward.
	 */
	private void case11(Player player)
	{// Cloth Breast
		final Storage bag = player.getInventory();
		final int count = 105;
		final int ap = 4329504;
		final int id = 110101485;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for cloth hands.<br>
	 * This method checks if the {@code player} has enough AP and medals.<br>
	 * It deducts the required resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case12(Player player)
	{// Cloth Hands
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 111101339;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Cloth Shoes.<br>
	 * Checks if the {@code player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case13(Player player)
	{// Cloth Shoes
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 114101387;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Cloth Pants.<br>
	 * Checks if the {@code player} has enough AP and required medals.<br>
	 * Deducts resources and grants the new item to the {@link Player}.
	 * @param player The {@code Player} performing the action.
	 */
	private void case14(Player player)
	{// Cloth Pants
		final Storage bag = player.getInventory();
		final int count = 78;
		final int ap = 3247344;
		final int id = 113101356;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Cloth Shoulders.<br>
	 * Checks if the {@code player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@link Player} performing the action.
	 */
	private void case15(Player player)
	{// Cloth Shoulders
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 112101296;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a Chain Breast.<br>
	 * This method checks if the {@code player} has enough AP and medals.<br>
	 * It deducts the required resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case16(Player player)
	{// Chain Breast
		final Storage bag = player.getInventory();
		final int count = 105;
		final int ap = 4329504;
		final int id = 110501368;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Chain Hands.<br>
	 * Checks if the {@code player} has enough Abyss Points and medals.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@link Player} performing the action.
	 */
	private void case17(Player player)
	{// Chain Hands
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 111501326;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Chain Shoes.<br>
	 * Checks if the {@code player} has enough Abyss Points and required medals.<br>
	 * Deducts the costs and grants the new item to the inventory.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case18(Player player)
	{// Chain Shoes
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 114501349;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Chain Pants.<br>
	 * This method checks if the {@code player} has enough Abyss Points and medals.<br>
	 * It deducts the required resources and grants the new item to the {@link Player}.
	 * @param player The {@code Player} who is performing the exchange.
	 */
	private void case19(Player player)
	{// Chain Pants
		final Storage bag = player.getInventory();
		final int count = 78;
		final int ap = 3247344;
		final int id = 113501341;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for Chain Shoulders.<br>
	 * Checks if the {@code player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@link Player} performing the action.
	 */
	private void case20(Player player)
	{// Chain Shoulders
		final Storage bag = player.getInventory();
		final int count = 52;
		final int ap = 2164752;
		final int id = 112501266;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a specific weapon.<br>
	 * Checks if the {@link Player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@code Player} performing the action.
	 */
	private void case21(Player player)
	{// Weapon Sword
		final Storage bag = player.getInventory();
		final int count = 156;
		final int ap = 6494256;
		final int id = 100001412;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a Greatsword.<br>
	 * Checks if the {@link Player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@code Player} performing the action.
	 */
	private void case22(Player player)
	{// Weapon Greatsword
		final Storage bag = player.getInventory();
		final int count = 156;
		final int ap = 6494256;
		final int id = 100901105;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a Longbow.<br>
	 * Checks if the {@link Player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@code Player} performing the action.
	 */
	private void case23(Player player)
	{// Weapon Longbow
		final Storage bag = player.getInventory();
		final int count = 156;
		final int ap = 6494256;
		final int id = 101701134;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a specific weapon item.<br>
	 * Checks if the {@code player} has enough AP and required medals.<br>
	 * Deducts the costs and grants the new item to the inventory.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case24(Player player)
	{// Weapon Dagger
		final Storage bag = player.getInventory();
		final int count = 156;
		final int ap = 6494256;
		final int id = 100201251;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a Weapon Orb.<br>
	 * It checks if the {@code player} has enough AP and medals.<br>
	 * If requirements are met, it deducts resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case25(Player player)
	{// Weapon Orb
		final Storage bag = player.getInventory();
		final int count = 156;
		final int ap = 6494256;
		final int id = 100501097;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a Weapon Tome.<br>
	 * Checks if the {@code player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case26(Player player)
	{// Weapon Tome
		final Storage bag = player.getInventory();
		final int count = 156;
		final int ap = 6494256;
		final int id = 100601153;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a Weapon Staff.<br>
	 * Checks if the {@code player} has enough AP and medals in their inventory.<br>
	 * Deducts the required resources and grants the new item.
	 * @param player The {@link Player} performing the action.
	 */
	private void case27(Player player)
	{// Weapon Staff
		final Storage bag = player.getInventory();
		final int count = 156;
		final int ap = 6494256;
		final int id = 101501123;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a Mace weapon.<br>
	 * It checks if the {@code player} has enough AP and medals in their inventory.<br>
	 * If requirements are met, it deducts the costs and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case28(Player player)
	{// Weapon Mace
		final Storage bag = player.getInventory();
		final int count = 156;
		final int ap = 6494256;
		final int id = 100101089;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a weapon and shield.<br>
	 * It checks if the {@code player} has enough AP and medals.<br>
	 * If requirements are met, it deducts resources and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case29(Player player)
	{// Weapon Shield
		final Storage bag = player.getInventory();
		final int count = 105;
		final int ap = 4329504;
		final int id = 115001462;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
	}
	
	/**
	 * Handles the exchange of medals for a specific weapon spear.<br>
	 * It checks if the {@code player} has enough AP and medals in their inventory.<br>
	 * If requirements are met, it deducts the costs and grants the new item.
	 * @param player The {@link Player} who is performing the exchange.
	 */
	private void case30(Player player)
	{// Weapon Spear
		final Storage bag = player.getInventory();
		final int count = 156;
		final int ap = 6494256;
		final int id = 101301042;
		final long itemsInBag = bag.getItemCountByItemId(186000223);
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough AP, you only have: " + ap);
			return;
		}
		
		if (itemsInBag < count)
		{
			PacketSendUtility.sendYellowMessageOnCenter(player, "You do not have enough medals, you have only: " + count);
			return;
		}
		
		AbyssPointsService.addAp(player, -ap);
		final Item item = bag.getFirstItemByItemId(186000223);
		bag.decreaseByObjectId(item.getObjectId(), count);
		ItemService.addItem(player, id, 1);
		PacketSendUtility.sendMessage(player, "You have successfully received your item!");
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
		PacketSendUtility.sendMessage(player, "Syntax: .honoritems <plate | leather | cloth | chain | weapons>");
	}
}
