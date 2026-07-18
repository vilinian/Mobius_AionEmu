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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code goevent} player command.<br>
 * This class processes requests to trigger specific in-game events via the chat system.<br>
 * It extends {@link PlayerCommand} to manage command execution logic.
 */
public class cmd_goevent extends PlayerCommand
{
	/**
	 * Registers the {@code goevent} command.<br>
	 * This method initializes the command handler for players.<br>
	 * It calls the constructor of the {@link PlayerCommand} class.
	 */
	public cmd_goevent()
	{
		super("goevent");
	}
	
	/**
	 * Toggles the player's status on the event waiting list.<br>
	 * It updates the {@code isLookingForEvent} state and sends a confirmation message.
	 * @param player The {@link Player} who is executing the command.
	 * @param params Additional arguments provided with the command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (player.isLookingForEvent())
		{
			player.setLookingForEvent(false);
			PacketSendUtility.sendMessage(player, "You have leave the event waiting list.");
		}
		else
		{
			player.setLookingForEvent(true);
			PacketSendUtility.sendMessage(player, "You are in event waiting list.");
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
		PacketSendUtility.sendMessage(player, "Syntax: .goevent ");
	}
}
