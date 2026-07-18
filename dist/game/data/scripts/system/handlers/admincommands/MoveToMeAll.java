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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command to teleport all players to the executor's current location.<br>
 * This class uses {@link TeleportService2} to move every player in the world to the sender.<br>
 * It provides a quick way for administrators to gather everyone at one spot.
 * @author Shepper Helped by
 * @alfa24t
 */
public class MoveToMeAll extends AdminCommand
{
	/**
	 * Initializes the {@code MoveToMeAll} command.<br>
	 * This allows an administrator to teleport all players to their location.<br>
	 * It registers the command with the name {@code movetomeall}.
	 */
	public MoveToMeAll()
	{
		super("movetomeall");
	}
	
	/**
	 * Teleports all players or specific races to the administrator's location.<br>
	 * It supports targets like {@code all}, {@code elyos}, and {@code asmos}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element specifies the target group.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(admin, "syntax //movetomeall < all | elyos | asmos >");
			return;
		}
		
		if (params[0].equals("all"))
		{
			for (Player p : World.getInstance().getAllPlayers())
			{
				if (!p.equals(admin))
				{
					TeleportService2.teleportTo(p, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading());
					PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));
					
					PacketSendUtility.sendMessage(admin, "Player " + p.getName() + " teleported.");
					PacketSendUtility.sendMessage(p, "Teleportd by " + admin.getName() + ".");
				}
			}
		}
		
		if (params[0].equals("elyos"))
		{
			for (Player p : World.getInstance().getAllPlayers())
			{
				if (!p.equals(admin))
				{
					if (p.getRace() == Race.ELYOS)
					{
						TeleportService2.teleportTo(p, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading());
						PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));
						
						PacketSendUtility.sendMessage(admin, "Player " + p.getName() + " teleported.");
						PacketSendUtility.sendMessage(p, "Teleportd by " + admin.getName() + ".");
					}
				}
			}
		}
		
		if (params[0].equals("asmos"))
		{
			for (Player p : World.getInstance().getAllPlayers())
			{
				if (!p.equals(admin))
				{
					if (p.getRace() == Race.ASMODIANS)
					{
						TeleportService2.teleportTo(p, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading());
						PacketSendUtility.sendPacket(p, new SM_PLAYER_SPAWN(p));
						
						PacketSendUtility.sendMessage(admin, "Player " + p.getName() + " teleported.");
						PacketSendUtility.sendMessage(p, "Teleportd by " + admin.getName() + ".");
					}
				}
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
		PacketSendUtility.sendMessage(player, "syntax //movetomeall < all | elyos | asmos >");
	}
}
