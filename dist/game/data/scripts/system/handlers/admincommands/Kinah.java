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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command for adding {@code Kinah} to a player.<br>
 * This class allows administrators to grant currency to themselves, a named player, or a target.<br>
 * It utilizes {@code KINAH} to identify the correct item type.
 * @author Sarynth Simple admin assistance command for adding kinah to self, named player or target player. Based on //add command. Kinah Item Id - 182400001 (Using ItemId.KINAH.value())
 */
public class Kinah extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Kinah} command.<br>
	 * This constructor registers the command with the name {@code kinah}.<br>
	 * It allows administrators to add kinah to players.
	 */
	public Kinah()
	{
		super("kinah");
	}
	
	/**
	 * Executes the command to give kinah to a player.<br>
	 * It allows giving kinah to either the {@code admin} or a specific target player.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the amount or the target name, and the second is the amount if a name was provided.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		long kinahCount;
		Player receiver;
		
		if (params.length == 1)
		{
			receiver = admin;
			try
			{
				kinahCount = Long.parseLong(params[0]);
			}
			catch (NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "Kinah value must be an integer.");
				return;
			}
		}
		else
		{
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
			
			if (receiver == null)
			{
				PacketSendUtility.sendMessage(admin, "Could not find a player by that name.");
				return;
			}
			
			try
			{
				kinahCount = Long.parseLong(params[1]);
			}
			catch (NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "Kinah value must be an integer.");
				return;
			}
		}
		
		final long count = ItemService.addItem(receiver, ItemId.KINAH.value(), kinahCount);
		
		if (count == 0)
		{
			PacketSendUtility.sendMessage(admin, "Kinah given successfully.");
			PacketSendUtility.sendMessage(receiver, "An admin gives you some kinah.");
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "Kinah couldn't be given.");
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
		PacketSendUtility.sendMessage(player, "syntax //kinah [player] <quantity>");
	}
}
