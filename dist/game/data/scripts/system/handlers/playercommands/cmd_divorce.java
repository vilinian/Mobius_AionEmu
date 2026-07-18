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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.WeddingService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the player command for initiating a divorce from a spouse.<br>
 * It interacts with {@link WeddingService} to process the dissolution of a marriage.
 * @author SheppeR
 * @rework Eloann
 */
public class cmd_divorce extends PlayerCommand
{
	/**
	 * Handles the divorce command for players.<br>
	 * This method registers the {@code divorce} command in the system.<br>
	 * It allows users to initiate a divorce process via the chat interface.
	 */
	public cmd_divorce()
	{
		super("divorce");
	}
	
	/**
	 * Executes the command to cancel a marriage between two players.<br>
	 * It checks if both players are online and have enough kinah.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the first player name and the second is the partner name.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length != 2))
		{
			PacketSendUtility.sendMessage(admin, "syntax .divorce <characterName1> <characterName2>");
			return;
		}
		
		final Player partner1 = World.getInstance().findPlayer(Util.convertName(params[0]));
		final Player partner2 = World.getInstance().findPlayer(Util.convertName(params[1]));
		
		if ((partner1 == null) || (partner2 == null))
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		
		final int ap1 = partner1.getAbyssRank().getAp();
		final int ap2 = partner2.getAbyssRank().getAp();
		
		if (partner1.equals(partner2))
		{
			PacketSendUtility.sendMessage(admin, "You can't cancel marry player on himself.");
			return;
		}
		
		if ((partner1.getWorldId() == 510010000) || (partner1.getWorldId() == 520010000) || (partner2.getWorldId() == 510010000) || (partner2.getWorldId() == 520010000))
		{
			PacketSendUtility.sendMessage(admin, "One of the players is in prison.");
			return;
		}
		
		if (!hasItem(partner1, 182400001))
		{
			PacketSendUtility.sendMessage(admin, "You don't have enough kinah.");
			return;
		}
		
		if (!hasItem(partner2, 182400001))
		{
			PacketSendUtility.sendMessage(admin, "Your partner don't have enough kinah.");
			return;
		}
		
		WeddingService.getInstance().unDoWedding(partner1, partner2);
		ItemService.addItem(partner1, 182400001, -75000000);
		ItemService.addItem(partner2, 182400001, -75000000);
		AbyssPointsService.addAp(partner1, -((ap1 * 20) / 100));
		AbyssPointsService.addAp(partner2, -((ap2 * 20) / 100));
		PacketSendUtility.sendMessage(admin, "Married canceled.");
	}
	
	/**
	 * Checks if a {@link Player} has enough of a specific item.<br>
	 * It verifies if the count for {@code itemId} is greater than {@code 75000000}.
	 * @param player The {@code Player} to check.
	 * @param itemId The unique ID of the item to look for.
	 * @return {@code true} if the player has enough items, otherwise {@code false}.
	 */
	private boolean hasItem(Player player, int itemId)
	{
		return player.getInventory().getItemCountByItemId(itemId) > 75000000;
	}
	
	/**
	 * This method is called when an {@code execute} command fails.<br>
	 * It sends a failure notification to the administrator.
	 * @param admin The {@code Player} who attempted the command.
	 * @param message The error message to display.
	 */
	@Override
	public void onFail(Player admin, String message)
	{
		PacketSendUtility.sendMessage(admin, "syntax .divorce <characterName1> <characterName2>");
	}
}
