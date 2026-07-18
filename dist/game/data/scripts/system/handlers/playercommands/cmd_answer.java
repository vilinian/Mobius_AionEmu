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

import com.aionemu.gameserver.model.Wedding;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.WeddingService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the command for a player to accept a wedding proposal.<br>
 * It interacts with {@link WeddingService} to update the status of a pending request.
 * @author synchro2
 */
public class cmd_answer extends PlayerCommand
{
	/**
	 * Registers the {@code answer} command.<br>
	 * This allows players to respond to wedding invitations.<br>
	 * It initializes the command within the {@link PlayerCommand} system.
	 */
	public cmd_answer()
	{
		super("answer");
	}
	
	/**
	 * Executes the command to accept or decline a wedding.<br>
	 * It checks if the {@link Player} is currently in a valid wedding state.<br>
	 * The method processes the first parameter to determine the player's choice.
	 * @param player The {@code Player} who is attempting to respond to the wedding.
	 * @param params Variable arguments containing either "yes" or "no".
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final Wedding wedding = WeddingService.getInstance().getWedding(player);
		
		if ((params == null) || (params.length != 1))
		{
			PacketSendUtility.sendMessage(player, "syntax .answer yes/no.");
			return;
		}
		
		if ((player.getWorldId() == 510010000) || (player.getWorldId() == 520010000))
		{
			PacketSendUtility.sendMessage(player, "You can't use this command on prison.");
			return;
		}
		
		if (wedding == null)
		{
			PacketSendUtility.sendMessage(player, "Wedding not started.");
		}
		
		if (params[0].toLowerCase().equals("yes"))
		{
			PacketSendUtility.sendMessage(player, "You accept.");
			WeddingService.getInstance().acceptWedding(player);
		}
		
		if (params[0].toLowerCase().equals("no"))
		{
			PacketSendUtility.sendMessage(player, "You decide.");
			WeddingService.getInstance().cancelWedding(player);
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
		PacketSendUtility.sendMessage(player, "syntax .answer yes/no.");
	}
}
