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
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Handles the admin command to change a player's race.<br>
 * It allows administrators to modify the {@code Race} of a target {@link Player}.
 * @author Centisgood(Barahime)
 * @reworked FrozenKiller
 */
public class SetRace extends AdminCommand
{
	/**
	 * Initializes the {@link SetRace} command.<br>
	 * This constructor registers the {@code setrace} admin command.
	 */
	public SetRace()
	{
		super("setrace");
	}
	
	/**
	 * Changes the race of a selected player.<br>
	 * The target must be selected before running this command.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be either "elyos" or "asmodians".
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		Player target = null;
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(admin, "syntax: //setrace <elyos | asmodians>");
			return;
		}
		
		final VisibleObject creature = admin.getTarget();
		
		if (admin.getTarget() instanceof Player)
		{
			target = (Player) creature;
		}
		else // if (target == null)
		{
			PacketSendUtility.sendMessage(admin, "You should select a target first!");
			return;
		}
		
		if (params[0].equalsIgnoreCase("elyos"))
		{
			target.getCommonData().setRace(Race.ELYOS);
			TeleportService2.teleportTo(target, WorldMapType.SANCTUM.getId(), 1322, 1511, 568);
			PacketSendUtility.sendMessage(target, "Has been moved to Sanctum.");
		}
		else if (params[0].equalsIgnoreCase("asmodians"))
		{
			target.getCommonData().setRace(Race.ASMODIANS);
			TeleportService2.teleportTo(target, WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195);
			PacketSendUtility.sendMessage(target, "Has been moved to Pandaemonium");
		}
		
		PacketSendUtility.sendMessage(admin, target.getName() + " race has been changed to " + params[0] + ".\n" + target.getName() + " has been moved to town.");
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
		PacketSendUtility.sendMessage(player, "syntax: //setrace <elyos | asmodians>");
	}
}
