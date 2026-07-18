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
package com.aionemu.gameserver.utils.chathandlers;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class serves as the base for all commands that can be executed by a {@link Player}.<br>
 * It handles the logic for processing player-initiated chat commands within the game server.
 * @author synchro2
 */
public abstract class PlayerCommand extends ChatCommand
{
	/**
	 * Creates a new instance of a {@link PlayerCommand}.<br>
	 * This constructor initializes the command with a specific name.
	 * @param alias The unique text identifier for this command.
	 */
	public PlayerCommand(String alias)
	{
		super(alias);
	}
	
	/**
	 * Checks if a {@link Player} has enough permissions to run this command.<br>
	 * It compares the player's access level against the required level.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player meets the requirement, {@code false} otherwise.
	 */
	@Override
	public boolean checkLevel(Player player)
	{
		return player.havePermission(getLevel());
	}
	
	/**
	 * Executes an admin command for a specific player.<br>
	 * This method checks if the {@code player} has the required permissions.<br>
	 * It parses the {@code text} input and runs the corresponding logic.
	 * @param player The {@link Player} who is attempting to run the command.
	 * @param text The raw string input provided by the player.
	 * @return {@code true} if the command failed or was blocked, and {@code false} if it succeeded.
	 */
	@Override
	boolean process(Player player, String text)
	{
		if (!checkLevel(player))
		{
			PacketSendUtility.sendMessage(player, "You not have permission for use this command.");
			return true;
		}
		
		boolean success = false;
		if (text.length() == getAlias().length())
		{
			success = run(player, EMPTY_PARAMS);
		}
		else
		{
			success = run(player, text.substring(getAlias().length() + 1).split(" "));
		}
		
		return success;
	}
}
