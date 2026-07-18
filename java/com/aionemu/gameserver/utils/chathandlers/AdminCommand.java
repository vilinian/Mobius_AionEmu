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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class serves as a base for handling administrative commands within the game chat.<br>
 * It provides common functionality for {@link ChatCommand} to execute privileged actions.
 * @author synchro2
 */
public abstract class AdminCommand extends ChatCommand
{
	static final Logger log = LoggerFactory.getLogger("ADMINAUDIT_LOG");
	
	/**
	 * Creates a new instance of an {@link AdminCommand}.<br>
	 * This constructor initializes the command with a specific shortcut.
	 * @param alias The unique text string used to trigger this command.
	 */
	public AdminCommand(String alias)
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
		return player.getAccessLevel() >= getLevel();
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
			if (LoggingConfig.LOG_GMAUDIT)
			{
				log.info("[ADMIN COMMAND] > [Player: " + player.getName() + "] has tried to use the command " + getAlias() + " without having the rights");
			}
			
			if (player.isGM())
			{
				PacketSendUtility.sendMessage(player, "[WARN] You need to have access level " + getLevel() + " or more to use " + getAlias());
				return true;
			}
			
			return false;
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
		
		if (LoggingConfig.LOG_GMAUDIT)
		{
			if ((player.getTarget() != null) && (player.getTarget() instanceof Creature))
			{
				final Creature target = (Creature) player.getTarget();
				log.info("[ADMIN COMMAND] > [Name: " + player.getName() + "][Target : " + target.getName() + "]: " + text);
			}
			else
			{
				log.info("[ADMIN COMMAND] > [Name: " + player.getName() + "]: " + text);
			}
		}
		
		if (!success)
		{
			PacketSendUtility.sendMessage(player, "<You have failed to execute " + text + ">");
			return true;
		}
		
		return success;
	}
}
