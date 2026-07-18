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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the {@code ID} admin command.<br>
 * This class allows administrators to identify specific entities in the game world.
 */
public class ID extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@code ID} command.<br>
	 * This command allows administrators to check identification information.<br>
	 * It registers the command name as {@code id} in the system.
	 */
	public ID()
	{
		super("id");
	}
	
	/**
	 * Displays information about the object currently targeted by the admin.<br>
	 * It checks if the target is an {@link Npc} and prints its details to the chat.<br>
	 * The command requires a minimum access level of 1.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings for additional arguments.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		final VisibleObject target = admin.getTarget();
		
		if (admin.getAccessLevel() < 1)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to use this command!");
			return;
		}
		
		if (target instanceof Npc)
		{
			final Npc npc = (Npc) admin.getTarget();
			PacketSendUtility.sendMessage(admin, "[Info about target]" + "\nName: " + npc.getName() + "\nId: " + npc.getNpcId());
		}
	}
}
