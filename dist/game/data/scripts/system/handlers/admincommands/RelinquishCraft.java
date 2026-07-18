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
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.services.craft.RelinquishCraftStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command to relinquish a craft for a specific player.<br>
 * This allows administrators to manually update or reset {@link CraftSkillUpdateService} statuses.
 * @author synchro2
 */
public class RelinquishCraft extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link RelinquishCraft} command.<br>
	 * This class handles the admin command for relinquishing craft status.
	 */
	public RelinquishCraft()
	{
		super("relinquishcraft");
	}
	
	/**
	 * Executes the command to relinquish a crafting skill for a specific player.<br>
	 * It resets the skill level to the minimum required value and removes associated recipes.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the target name, the second is the {@code skillId}, and the third is "Expert" or "Master".
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		Player player;
		int skillId;
		boolean isExpert = false;
		String skillIdParam = "";
		String isExpertParam = "";
		
		if ((params.length < 2) || (params.length > 3))
		{
			PacketSendUtility.sendMessage(admin, "syntax //relinquishcraft <character_name | target> <skillId> <expert | master>");
			return;
		}
		
		if (params.length == 2)
		{
			final VisibleObject target = admin.getTarget();
			if ((target == null) || !(target instanceof Player))
			{
				PacketSendUtility.sendMessage(admin, "Select target first.");
				return;
			}
			
			player = (Player) target;
			skillIdParam = params[0];
			isExpertParam = params[1];
			
		}
		else
		{
			player = World.getInstance().findPlayer(Util.convertName(params[0]));
			skillIdParam = params[1];
			isExpertParam = params[2];
			
			if (player == null)
			{
				PacketSendUtility.sendMessage(admin, "The specified player is not online.");
				return;
			}
		}
		
		try
		{
			skillId = Integer.parseInt(skillIdParam);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "You must using only numbers in skillId.");
			return;
		}
		
		if (!isExpertParam.equalsIgnoreCase("Expert") && !isExpertParam.equalsIgnoreCase("Master"))
		{
			PacketSendUtility.sendMessage(admin, "Only master or expert.");
			return;
		}
		
		if (isExpertParam.equalsIgnoreCase("Expert"))
		{
			isExpert = true;
		}
		
		if (isExpertParam.equalsIgnoreCase("Master"))
		{
			isExpert = false;
		}
		
		final PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		final int minValue = isExpert ? RelinquishCraftStatus.getExpertMinValue() : RelinquishCraftStatus.getMasterMinValue();
		final int maxValue = isExpert ? RelinquishCraftStatus.getExpertMaxValue() : RelinquishCraftStatus.getMasterMaxValue();
		final int skillMessageId = RelinquishCraftStatus.getSkillMessageId();
		
		if (!CraftSkillUpdateService.isCraftingSkill(skillId))
		{
			PacketSendUtility.sendMessage(admin, "It's not skillId.");
			return;
		}
		
		if ((skill == null) || (skill.getSkillLevel() < minValue) || (skill.getSkillLevel() > maxValue))
		{
			PacketSendUtility.sendMessage(admin, "Wrong skill level.");
			return;
		}
		
		skill.setSkillLvl(minValue);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, skillMessageId, false));
		RelinquishCraftStatus.removeRecipesAbove(player, skillId, minValue);
		RelinquishCraftStatus.deleteCraftStatusQuests(skillId, player, false);
		PacketSendUtility.sendMessage(admin, "Craft status successfull relinquished.");
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
		PacketSendUtility.sendMessage(player, "syntax //relinquishcraft <character_name | target> <skillId> <expert | master>");
	}
}
