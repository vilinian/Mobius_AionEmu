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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command to teleport a {@link Player} to a specific {@link VisibleObject}.<br>
 * This allows administrators to move characters to designated world locations instantly.
 * @author Rolandas
 */
public class MoveToObject extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link MoveToObject} command.<br>
	 * This class handles moving a player to a specific object via an admin command.
	 */
	public MoveToObject()
	{
		super("movetoobj");
	}
	
	/**
	 * Teleports the administrator to a specific object based on its ID.<br>
	 * The method validates that the provided parameter is a valid number.<br>
	 * It also stops any active protection tasks for the {@code Player}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be the object ID.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length != 1))
		{
			PacketSendUtility.sendMessage(admin, "Syntax : //movetoobj <object id>");
			return;
		}
		
		int objectId = 0;
		
		try
		{
			objectId = Integer.valueOf(params[0]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "Only numbers please!!!");
		}
		
		final VisibleObject object = World.getInstance().findVisibleObject(objectId);
		if (object == null)
		{
			PacketSendUtility.sendMessage(admin, "Cannot find object for spawn #" + objectId);
			return;
		}
		
		final VisibleObject spawn = object;
		
		TeleportService2.teleportTo(admin, spawn.getWorldId(), spawn.getSpawn().getX(), spawn.getSpawn().getY(), spawn.getSpawn().getZ());
		admin.getController().stopProtectionActiveTask();
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
		PacketSendUtility.sendMessage(player, "Syntax : //movetoobj <object id>");
	}
}
