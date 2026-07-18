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
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /exchange} player command.<br>
 * This class manages the logic for exchanging items between players.<br>
 * It interacts with {@link ItemService} to process trade requests.
 * @author Maestross
 */
public class cmd_exchange extends PlayerCommand
{
	/**
	 * Registers the {@code exchange} command.<br>
	 * This allows players to use the exchange system via chat.<br>
	 * It initializes the command handler for player interactions.
	 */
	public cmd_exchange()
	{
		super("exchange");
	}
	
	/**
	 * Executes the command to exchange Abyss Points for a specific item.<br>
	 * It checks if the {@link Player} has enough AP before granting the reward.<br>
	 * The method subtracts the required amount from the player's balance and adds the item.
	 * @param player The {@link Player} who is executing the command.
	 * @param params Variable arguments for additional command configuration.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final int ap = 15000;
		final int derived = 186000147;
		final int derived_q = 5;
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendMessage(player, "You don't have enough AP, you only have: " + ap);
			return;
		}
		
		ItemService.addItem(player, derived, derived_q);
		AbyssPointsService.addAp(player, -ap);
	}
}
