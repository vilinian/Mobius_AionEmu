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

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /rgb} player command.<br>
 * This class allows players to change their character's color or visual effects.<br>
 * It extends {@link PlayerCommand} to process user input for RGB modifications.
 * @author Phenom
 * @Rewroked yayaya
 */
public class cmd_rgb extends PlayerCommand
{
	/**
	 * Initializes the {@code rgb} command.<br>
	 * This constructor registers the command name with the parent class.<br>
	 * It allows players to change their text color using RGB codes.
	 */
	public cmd_rgb()
	{
		super("rgb");
	}
	
	/**
	 * Converts a color name into its corresponding RGB code.<br>
	 * This method checks the {@code color} string against known names.<br>
	 * It returns a formatted string of three numbers or {@code null}.
	 * @param color The name of the color to convert.
	 * @return A string containing the RGB values or {@code null} if not found.
	 */
	private String getColorCode(String color)
	{
		if (color.equalsIgnoreCase("red"))
		{
			return "1 0 0";
		}
		
		if (color.equalsIgnoreCase("orange"))
		{
			return "1 0.5 0";
		}
		
		if (color.equalsIgnoreCase("yellow"))
		{
			return "1 1 0";
		}
		
		if (color.equalsIgnoreCase("lime"))
		{
			return "0.5 1 0";
		}
		
		if (color.equalsIgnoreCase("green"))
		{
			return "0 1 0";
		}
		
		if (color.equalsIgnoreCase("cyan"))
		{
			return "0 1 1";
		}
		
		if (color.equalsIgnoreCase("blue"))
		{
			return "0 0 1";
		}
		
		if (color.equalsIgnoreCase("violet"))
		{
			return "0.5 0 1";
		}
		
		if (color.equalsIgnoreCase("pink"))
		{
			return "1 0 0.5";
		}
		
		if (color.equalsIgnoreCase("black"))
		{
			return "0 0 0";
		}
		
		return null;
	}
	
	/**
	 * Executes the command to broadcast a colored message.<br>
	 * It checks if the {@code player} has the required VIP membership level.<br>
	 * The method parses the color and text from the provided {@code params}.
	 * @param player The player executing the command.
	 * @param params Variable arguments containing the color name and the message text.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 2))
		{
			onFail(player, null);
			return;
		}
		
		// Only for VIP levels 3-9, where membership level 2 is considered VIP.
		// 1 - Premium (membership 1)
		// 0 - All players
		if (player.getClientConnection().getAccount().getMembership() < 2)
		{
			PacketSendUtility.sendMessage(player, "This command is available only to VIP!");
			return;
		}
		
		final String color = getColorCode(params[0]);
		if (color == null)
		{
			PacketSendUtility.sendMessage(player, "Unknown color!" + "  Colors: Red|Orange|Yellow|Lime|Green|Cyan|Blue|Violet|Pink|Black");
			return;
		}
		
		String source = "";
		try
		{
			for (int i = 1; i < params.length; i++)
			{
				source += (i == 1 ? "" : " ") + params[i];
			}
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters should be text or number!");
			return;
		}
		
		final int length = source.length();
		int index = 0;
		String message = player.getName() + ": ";
		while (index < length)
		{
			final int next = index + 5;
			message += "[color:" + source.substring(index, next < length ? next : length) + ";" + color + "]";
			index = next;
		}
		
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_MESSAGE(player, message, ChatType.YELLOW));
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
		PacketSendUtility.sendMessage(player, "Usage: .rgb <color> <message>");
	}
}
