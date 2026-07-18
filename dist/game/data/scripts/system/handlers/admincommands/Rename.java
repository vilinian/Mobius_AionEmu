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

import java.util.Iterator;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dao.OldNamesDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.NameRestrictionService;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to rename a player's character name.<br>
 * It validates the new name against {@link NameRestrictionService} and updates the database via {@link PlayerDAO}.<br>
 * This class ensures that only authorized administrators can modify player identities.
 * @author xTz
 */
public class Rename extends AdminCommand
{
	/**
	 * Initializes the {@link Rename} command.<br>
	 * This constructor registers the command with the name {@code rename}.
	 */
	public Rename()
	{
		super("rename");
	}
	
	/**
	 * Executes the command to change a player's name.<br>
	 * It allows renaming a targeted player or a specific player by name.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where parameters depend on whether a target is selected.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params.length < 1) || (params.length > 2))
		{
			PacketSendUtility.sendMessage(admin, "No parameters detected.\n" + "Please use //rename <Player name> <rename>\n" + "or use //rename [target] <rename>");
			return;
		}
		
		Player player = null;
		String recipient = null;
		String rename = null;
		
		if (params.length == 2)
		{
			recipient = Util.convertName(params[0]);
			rename = Util.convertName(params[1]);
			
			if (!DAOManager.getDAO(PlayerDAO.class).isNameUsed(recipient))
			{
				PacketSendUtility.sendMessage(admin, "Could not find a Player by that name.");
				return;
			}
			
			final PlayerCommonData recipientCommonData = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(recipient);
			player = recipientCommonData.getPlayer();
			
			if (!check(admin, rename))
			{
				return;
			}
			
			if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED)
			{
				DAOManager.getDAO(OldNamesDAO.class).insertNames(player.getObjectId(), player.getName(), rename);
			}
			
			recipientCommonData.setName(rename);
			DAOManager.getDAO(PlayerDAO.class).storePlayerName(recipientCommonData);
			if (recipientCommonData.isOnline())
			{
				PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
				PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
				sendPacket(admin, player, rename, recipient);
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "Player " + recipient + " has been renamed to " + rename);
			}
		}
		
		if (params.length == 1)
		{
			rename = Util.convertName(params[0]);
			
			final VisibleObject target = admin.getTarget();
			if (target == null)
			{
				PacketSendUtility.sendMessage(admin, "You should select a target first!");
				return;
			}
			
			if (target instanceof Player)
			{
				player = (Player) target;
				if (!check(admin, rename))
				{
					return;
				}
				
				if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED)
				{
					DAOManager.getDAO(OldNamesDAO.class).insertNames(player.getObjectId(), player.getName(), rename);
				}
				
				player.getCommonData().setName(rename);
				PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
				DAOManager.getDAO(PlayerDAO.class).storePlayerName(player.getCommonData());
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "The command can be applied only on the player.");
			}
			
			recipient = target.getName();
			sendPacket(admin, player, rename, recipient);
		}
	}
	
	/**
	 * Validates if a name can be used for renaming.<br>
	 * It checks the name against restrictions and availability.
	 * @param admin The {@code Player} who is performing the action.
	 * @param rename The new name string to validate.
	 * @return {@code true} if the name is valid, otherwise {@code false}.
	 */
	private static boolean check(Player admin, String rename)
	{
		if (!NameRestrictionService.isValidName(rename))
		{
			PacketSendUtility.sendPacket(admin, new SM_SYSTEM_MESSAGE(1400151));
			return false;
		}
		
		if (!PlayerService.isFreeName(rename) || (!CustomConfig.OLD_NAMES_COMMAND_DISABLED && PlayerService.isOldName(rename)))
		{
			PacketSendUtility.sendPacket(admin, new SM_SYSTEM_MESSAGE(1400155));
			return false;
		}
		
		return true;
	}
	
	/**
	 * This method updates a player's name and notifies others.<br>
	 * It sends the new information to all online friends of the target.<br>
	 * It also broadcasts the update to the player's legion if they belong to one.
	 * @param admin The {@code Player} who executed the command.
	 * @param player The {@code Player} whose name is being changed.
	 * @param rename The new name string for the target player.
	 * @param recipient The original name of the target player used in the confirmation message.
	 */
	public void sendPacket(Player admin, Player player, String rename, String recipient)
	{
		final Iterator<Friend> knownFriends = player.getFriendList().iterator();
		
		while (knownFriends.hasNext())
		{
			final Friend nextObject = knownFriends.next();
			if ((nextObject.getPlayer() != null) && nextObject.getPlayer().isOnline())
			{
				PacketSendUtility.sendPacket(nextObject.getPlayer(), new SM_PLAYER_INFO(player, false));
			}
		}
		
		if (player.isLegionMember())
		{
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
		
		PacketSendUtility.sendMessage(player, "You have been renamed to " + rename);
		PacketSendUtility.sendMessage(admin, "Player " + recipient + " has been renamed to " + rename);
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
		PacketSendUtility.sendMessage(player, "No parameters detected.\n" + "Please use //rename <Player name> <rename>\n" + "or use //rename [target] <rename>");
	}
}
