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
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command to teleport a specific group of players to the administrator's current location.<br>
 * It utilizes {@link TeleportService2} to move the target players and sends confirmation packets via {@link PacketSendUtility}.
 * @author Source
 */
public class GroupToMe extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link GroupToMe} command.<br>
	 * This class handles the admin command for grouping players to the administrator.
	 */
	public GroupToMe()
	{
		super("grouptome");
	}
	
	/**
	 * Executes the command to teleport all members of a specific player's group to the admin.<br>
	 * It verifies that the target player is online and currently in a group.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be the target player name.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			onFail(admin, null);
			return;
		}
		
		final Player groupToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (groupToMove == null)
		{
			PacketSendUtility.sendMessage(admin, "The player is not online.");
			return;
		}
		
		if (!groupToMove.isInGroup2())
		{
			PacketSendUtility.sendMessage(admin, groupToMove.getName() + " is not in group.");
			return;
		}
		
		for (Player target : groupToMove.getPlayerGroup2().getMembers())
		{
			if (target != admin)
			{
				TeleportService2.teleportTo(target, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading());
				PacketSendUtility.sendMessage(target, "You have been summoned by " + admin.getName() + ".");
				PacketSendUtility.sendMessage(admin, "You summon " + target.getName() + ".");
			}
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
		PacketSendUtility.sendMessage(player, "syntax //grouptome <player>");
	}
}
