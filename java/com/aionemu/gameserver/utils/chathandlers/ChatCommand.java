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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class serves as the base structure for all chat commands in the game server.<br>
 * It defines how specific text commands are parsed and executed by {@link Player} objects.
 * @author KID
 */
public abstract class ChatCommand
{
	private final String alias;
	private Byte level;
	static final String[] EMPTY_PARAMS = new String[] {};
	static final Logger log = LoggerFactory.getLogger(ChatCommand.class);
	
	/**
	 * Creates a new instance of a {@link ChatCommand}.<br>
	 * This constructor sets the unique name for the command.
	 * @param alias The string used to identify this command.
	 */
	public ChatCommand(String alias)
	{
		this.alias = alias;
	}
	
	/**
	 * Executes the command logic for a specific {@link Player}.<br>
	 * It handles errors by logging them and calling {@code String)}.
	 * @param player The {@code Player} who is running the command.
	 * @param params A variable number of {@code String} arguments provided by the user.
	 * @return {@code true} if the command finished successfully.
	 */
	public boolean run(Player player, String... params)
	{
		try
		{
			execute(player, params);
			return true;
		}
		catch (Exception e)
		{
			log.error("", e);
			onFail(player, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Retrieves the unique name for this chat command.<br>
	 * This is used to identify the command in the game.
	 * @return The {@code String} representation of the command alias.
	 */
	public String getAlias()
	{
		return alias;
	}
	
	/**
	 * Sets the required access level for this {@link ChatCommand}.<br>
	 * This determines which players can execute the command.
	 * @param level The new access level to assign.
	 */
	public void setAccessLevel(Byte level)
	{
		this.level = level;
	}
	
	/**
	 * Retrieves the access level required for this command.<br>
	 * This value is set using {@code setAccessLevel}.
	 * @return The current level as a {@code Byte}.
	 */
	public Byte getLevel()
	{
		return level;
	}
	
	abstract boolean checkLevel(Player player);
	
	abstract boolean process(Player player, String text);
	
	public abstract void execute(Player player, String... params);
	
	/**
	 * Handles the failure of an {@code execute} command.<br>
	 * It sends a syntax hint to the player.
	 * @param player The {@code Player} who attempted the command.
	 * @param message The error message associated with the failure.
	 */
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, message);
	}
}
