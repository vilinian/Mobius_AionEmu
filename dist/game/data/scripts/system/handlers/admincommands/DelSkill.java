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
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command to remove a specific skill from a player.<br>
 * It interacts with {@link PlayerSkillList} to delete the requested entry.
 * @author xTz
 */
public class DelSkill extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link DelSkill} command.<br>
	 * This class handles the removal of skills from players via admin commands.
	 */
	public DelSkill()
	{
		super("delskill");
	}
	
	/**
	 * Executes the command to remove skills from a player.<br>
	 * It allows removing all skills or a specific skill by ID.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings containing the target name and the skill identifier.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params.length < 1) || (params.length > 2))
		{
			PacketSendUtility.sendMessage(admin, "No parameters detected.\n" + "Please use //delskill <Player name> <all | skillId>\n" + "or use //delskill [target] <all | skillId>");
			return;
		}
		
		Player player;
		PlayerSkillList playerSkillList = null;
		String recipient = null;
		recipient = Util.convertName(params[0]);
		int skillId = 0;
		if (params.length == 2)
		{
			player = World.getInstance().findPlayer(recipient);
			if (player == null)
			{
				PacketSendUtility.sendMessage(admin, "The specified player is not online.");
				return;
			}
			
			if ("all".startsWith(params[1]))
			{
				playerSkillList = player.getSkillList();
			}
			else
			{
				try
				{
					skillId = Integer.parseInt(params[1]);
				}
				catch (NumberFormatException e)
				{
					PacketSendUtility.sendMessage(admin, "Param 1 must be an integer or <all>.");
					return;
				}
				
				if (!check(admin, player, skillId))
				{
					return;
				}
			}
			
			apply(admin, player, skillId, playerSkillList);
			
		}
		
		if (params.length == 1)
		{
			final VisibleObject target = admin.getTarget();
			if (target == null)
			{
				PacketSendUtility.sendMessage(admin, "You should select a target first!");
				return;
			}
			
			if (target instanceof Player)
			{
				player = (Player) target;
				
				if ("all".startsWith(params[0]))
				{
					playerSkillList = player.getSkillList();
				}
				else
				{
					try
					{
						skillId = Integer.parseInt(params[0]);
					}
					catch (NumberFormatException e)
					{
						PacketSendUtility.sendMessage(admin, "Param 0 must be an integer or <all>.");
						return;
					}
					
					if (!check(admin, player, skillId))
					{
						return;
					}
				}
				
				if (target instanceof Player)
				{
					apply(admin, player, skillId, playerSkillList);
				}
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "This command can only be used on a player !");
			}
		}
	}
	
	/**
	 * Validates if a player can have a specific skill removed.<br>
	 * It checks if the {@code skillId} exists and is not a stigma.
	 * @param admin The {@link Player} who is executing the command.
	 * @param player The {@link Player} whose skill will be checked.
	 * @param skillId The unique identifier of the skill to remove.
	 * @return {@code true} if the removal is allowed, {@code false} otherwise.
	 */
	private static boolean check(Player admin, Player player, int skillId)
	{
		if ((skillId != 0) && !player.getSkillList().isSkillPresent(skillId))
		{
			PacketSendUtility.sendMessage(admin, "Player dont have this skill.");
			return false;
		}
		
		if (player.getSkillList().getSkillEntry(skillId).isStigma())
		{
			PacketSendUtility.sendMessage(admin, "You can't remove stigma skill.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Removes a specific skill or all non-stigma skills from a player.<br>
	 * This method is called by the {@link DelSkill} command.<br>
	 * It notifies the {@code admin} of the result.
	 * @param admin The administrator executing the command.
	 * @param player The target player whose skills will be modified.
	 * @param skillId The unique ID of the skill to remove. Use {@code 0} to remove all non-stigma skills.
	 * @param playerSkillList The list of skills currently owned by the target player.
	 */
	public void apply(Player admin, Player player, int skillId, PlayerSkillList playerSkillList)
	{
		if (skillId != 0)
		{
			SkillLearnService.removeSkill(player, skillId);
			PacketSendUtility.sendMessage(admin, "You have successfully deleted the specified skill.");
		}
		else
		{
			for (PlayerSkillEntry skillEntry : playerSkillList.getAllSkills())
			{
				if (!skillEntry.isStigma())
				{
					SkillLearnService.removeSkill(player, skillEntry.getSkillId());
				}
			}
			
			PacketSendUtility.sendMessage(admin, "You have success delete All skills.");
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
		PacketSendUtility.sendMessage(player, "No parameters detected.\n" + "Please use //delskill <Player name> <all | skillId>\n" + "or use //delskill [target] <all | skillId>");
	}
}
