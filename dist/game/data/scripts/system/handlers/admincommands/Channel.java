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
package system.handlers.admincommands;

import java.lang.reflect.Field;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles administrative commands related to game channels.<br>
 * This class allows administrators to manage and interact with different {@link com.aionemu.gameserver.model.gameobjects.player.Player} channels.
 * @author SheppeR
 */
public class Channel extends AdminCommand
{
	/**
	 * Creates a new instance of the {@code Channel} class.<br>
	 * This constructor initializes the command for use in the game server.
	 */
	public Channel()
	{
		super("channel");
	}
	
	/**
	 * Toggles the status of the faction command.<br>
	 * It updates the {@code FACTION_CMD_CHANNEL} field in {@link CustomConfig}.<br>
	 * Use "on" or "off" as the first parameter to change the state.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the toggle status (e.g., "on" or "off").
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final Class<?> classToMofify = CustomConfig.class;
		Field someField;
		try
		{
			someField = classToMofify.getDeclaredField("FACTION_CMD_CHANNEL");
			if (params[0].equalsIgnoreCase("on") && !CustomConfig.FACTION_CMD_CHANNEL)
			{
				someField.set(null, Boolean.valueOf(true));
				PacketSendUtility.sendMessage(player, "The command .faction is ON.");
			}
			else if (params[0].equalsIgnoreCase("off") && CustomConfig.FACTION_CMD_CHANNEL)
			{
				someField.set(null, Boolean.valueOf(false));
				PacketSendUtility.sendMessage(player, "The command .faction is OFF.");
			}
		}
		catch (Exception e)
		{
			PacketSendUtility.sendMessage(player, "Error! Wrong property or value.");
			return;
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
		PacketSendUtility.sendMessage(player, "syntax //channel <On | Off>");
	}
}
