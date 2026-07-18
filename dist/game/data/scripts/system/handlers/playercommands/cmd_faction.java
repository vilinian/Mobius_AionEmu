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

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.player.PlayerChatService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the {@code /faction} player command.<br>
 * This class manages faction-related actions and messages for players.<br>
 * It extends {@link PlayerCommand} to process user input.
 * @author Shepper, modified: bobobear
 */
public class cmd_faction extends PlayerCommand
{
	/**
	 * Initializes the faction command handler.<br>
	 * This class handles the {@code faction} player command.<br>
	 * It extends the base {@link PlayerCommand} class.
	 */
	public cmd_faction()
	{
		super("faction");
	}
	
	/**
	 * Executes the faction chat command.<br>
	 * It checks for permissions and costs before broadcasting a message to players of the same race.<br>
	 * The method handles different display formats based on the server configuration.
	 * @param player The {@code Player} executing the command.
	 * @param params Variable arguments containing the message text.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final Storage sender = player.getInventory();
		
		if (!CustomConfig.FACTION_CMD_CHANNEL)
		{
			PacketSendUtility.sendMessage(player, "The command is disabled.");
			return;
		}
		
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "syntax .faction <message>");
			return;
		}
		
		if ((player.getWorldId() == 510010000) || (player.getWorldId() == 520010000))
		{
			PacketSendUtility.sendMessage(player, "You can't talk in Prison.");
			return;
		}
		else if (player.isGagged())
		{
			PacketSendUtility.sendMessage(player, "You are gaged, you can't talk.");
			return;
		}
		
		if (!CustomConfig.FACTION_FREE_USE)
		{
			if (sender.getKinah() > CustomConfig.FACTION_USE_PRICE)
			{
				sender.decreaseKinah(CustomConfig.FACTION_USE_PRICE);
			}
			else
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
				return;
			}
		}
		
		if (PlayerChatService.isFlooding(player))
		{
			return;
		}
		
		String message = String.join(" ", params);
		final String LogMessage = message;
		
		if (CustomConfig.FACTION_CHAT_CHANNEL)
		{
			final ChatType channel = ChatType.CH1;
			
			for (Player listener : World.getInstance().getAllPlayers())
			{
				if (listener.getAccessLevel() > 1)
				{
					PacketSendUtility.sendPacket(listener, new SM_MESSAGE(player.getObjectId(), (player.getRace() == Race.ASMODIANS ? "(A) " : "(E) ") + player.getName(), message, channel));
				}
				else if (listener.getRace() == player.getRace())
				{
					PacketSendUtility.sendPacket(listener, new SM_MESSAGE(player.getObjectId(), player.getName(), message, channel));
				}
			}
		}
		else
		{
			message = player.getName() + ": " + message;
			for (Player a : World.getInstance().getAllPlayers())
			{
				if (a.getAccessLevel() > 1)
				{
					PacketSendUtility.sendMessage(a, (player.getRace() == Race.ASMODIANS ? "[A] " : "[E] ") + message);
				}
				else if (a.getRace() == player.getRace())
				{
					PacketSendUtility.sendMessage(a, message);
				}
			}
		}
		
		if (LoggingConfig.LOG_FACTION)
		{
			PlayerChatService.chatLogging(player, ChatType.NORMAL, "[Faction Msg] " + LogMessage);
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
		// TODO Auto-generated method stub
	}
}
