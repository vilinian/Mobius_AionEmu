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

import com.aionemu.gameserver.model.Support;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.SupportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /support} command for players.<br>
 * It allows users to request assistance from the game support team.<br>
 * This class extends {@link PlayerCommand} to process and route these requests.
 * @author paranaix
 */
public class cmd_support extends PlayerCommand
{
	/**
	 * Initializes the {@code support} command handler.<br>
	 * This class handles requests from players seeking assistance.<br>
	 * It extends the base {@link PlayerCommand} class.
	 */
	public cmd_support()
	{
		super("support");
	}
	
	/**
	 * Executes the command to create a new support ticket.<br>
	 * It checks if the player already has an active ticket before creating one.<br>
	 * The method sends a notification message to the {@link Player} upon success.
	 * @param player The player who is submitting the support request.
	 * @param params Variable arguments containing the description of the issue.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length == 0)
		{
			PacketSendUtility.sendMessage(player, "Syntax: .ticket \"Short description here\" -- will notify GM's of your issue");
			return;
		}
		
		if (SupportService.getInstance().hasTicket(player))
		{
			PacketSendUtility.sendMessage(player, "You already have an open support!");
			return;
		}
		
		Support support;
		support = new Support(player, params.toString(), "");
		SupportService.getInstance().addTicket(support);
		PacketSendUtility.sendMessage(player, "Your support was sended successfully.");
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
