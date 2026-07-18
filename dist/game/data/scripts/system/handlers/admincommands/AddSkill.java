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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to grant a specific skill to a player.<br>
 * It allows administrators to modify character abilities for testing or support purposes.
 * @author Phantom
 */
public class AddSkill extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link AddSkill} command.<br>
	 * This class handles the admin command for adding skills to players.
	 */
	public AddSkill()
	{
		super("addskill");
	}
	
	/**
	 * Executes the command to add a skill to a target player.<br>
	 * It parses the provided parameters for the skill ID and level.<br>
	 * The method checks if the current target is a {@link Player}.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the skill ID and skill level as integers.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length != 2)
		{
			PacketSendUtility.sendMessage(player, "syntax //addskill <skillId> <skillLevel>");
			return;
		}
		
		final VisibleObject target = player.getTarget();
		
		int skillId = 0;
		int skillLevel = 0;
		
		try
		{
			skillId = Integer.parseInt(params[0]);
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, skillId, skillLevel);
			PacketSendUtility.sendMessage(player, "You have success add skill");
			PacketSendUtility.sendMessage(targetpl, "You have acquire a new skill");
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
		PacketSendUtility.sendMessage(player, "syntax //addskill <skillId> <skillLevel>");
	}
}
