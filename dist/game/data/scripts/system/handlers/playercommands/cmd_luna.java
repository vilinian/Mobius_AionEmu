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
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SYSTEM_INFO;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.utils.i18n.CustomMessageId;
import com.aionemu.gameserver.utils.i18n.LanguageHandler;

/**
 * Handles the {@code /luna} player command.<br>
 * This class allows players to view their current Luna system information.<br>
 * It utilizes {@link SM_LUNA_SYSTEM_INFO} to send data back to the client.
 * @author Falke34
 */
public class cmd_luna extends PlayerCommand
{
	/**
	 * Initializes the {@code luna} command handler.<br>
	 * This constructor sets up the command for use by players.<br>
	 * It calls the superclass constructor with the literal value {@code "luna"}.
	 */
	public cmd_luna()
	{
		super("luna");
	}
	
	// The exchange rate is 10,000 AP or 10,000,000 Kinah for 1 Luna Coin.
	
	/**
	 * Executes the command to exchange Abyss Points or Kinah for Luna Coins.<br>
	 * It checks if the player has enough currency before adding the coins.<br>
	 * The method validates that the amount does not exceed the maximum limit.
	 * @param player The {@code Player} executing the command.
	 * @param params Variable arguments containing the currency type and the amount to exchange.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 2))
		{
			PacketSendUtility.sendMessage(player, ".luna <ap | kinah> <value>" + "\nAp 10,000:1 : Kinah 10,000,000:1");
			return;
		}
		
		int luna;
		try
		{
			luna = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			return;
		}
		
		if (luna > 500)
		{
			// max amount of Luna Coins
			PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.LUNATOBIG));
			return;
		}
		
		if (params[0].equals("ap") && (luna > 0))
		{
			final int PlayerAbyssPoints = player.getAbyssRank().getAp();
			final int pointsLost = (luna * 10000);
			if (PlayerAbyssPoints < pointsLost)
			{
				PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.TOLOWAP));
				return;
			}
			
			AbyssPointsService.addAp(player, -pointsLost);
			addluna(player, luna);
		}
		else if (params[0].equals("kinah") && (luna > 0))
		{
			final int kinahLost = (luna * 10000000);
			if (player.getInventory().getKinah() < kinahLost)
			{
				PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.TOLOWLUNA));
				return;
			}
			
			player.getInventory().decreaseKinah(kinahLost);
			addluna(player, luna);
		}
		else
		{
			PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.WRONGLUNANUM));
			return;
		}
	}
	
	/**
	 * Adds a specific amount of Luna coins to a player's account.<br>
	 * This method updates the {@code Player} data and sends a refresh packet.
	 * @param player The {@link Player} who will receive the coins.
	 * @param luna The number of coins to add.
	 */
	private void addluna(Player player, int luna)
	{
		player.getCommonData().setLunaCoins(player.getCommonData().getLunaCoins() + luna);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0));
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
		// TODO Auto-generated method stub
	}
}
