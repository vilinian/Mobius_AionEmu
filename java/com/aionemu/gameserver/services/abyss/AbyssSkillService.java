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
package com.aionemu.gameserver.services.abyss;

import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * This service manages the logic for {@link Player} skills related to the Abyss system.<br>
 * It handles skill execution and updates based on the player's {@code AbyssRank}.
 * @author ATracer
 */
public class AbyssSkillService
{
	/**
	 * Updates the skills for a specific {@link Player}.<br>
	 * This method removes old abyss skills based on the player's race.<br>
	 * It then adds new skills if the player meets the required rank.
	 * @param player The {@code Player} object to update.
	 */
	public static void updateSkills(Player player)
	{
		final AbyssRank abyssRank = player.getAbyssRank();
		if (abyssRank == null)
		{
			return;
		}
		
		final AbyssRankEnum rankEnum = abyssRank.getRank();
		
		// remove all abyss skills first
		for (AbyssSkills abyssSkill : AbyssSkills.values())
		{
			if (abyssSkill.getRace() == player.getRace())
			{
				for (int skillId : abyssSkill.getSkills())
				{
					player.getSkillList().removeSkill(skillId);
				}
			}
		}
		
		// add new skills
		if (abyssRank.getRank().getId() >= AbyssRankEnum.STAR5_OFFICER.getId())
		{
			for (int skillId : AbyssSkills.getSkills(player.getRace(), rankEnum))
			{
				player.getSkillList().addAbyssSkill(player, skillId, 1);
			}
		}
	}
	
	/**
	 * This method is called when a {@link Player} enters the world.<br>
	 * It triggers an update for the player's skills.<br>
	 * It calls the {@code updateSkills} method.
	 * @param player The {@code Player} object that just entered the world.
	 */
	public static void onEnterWorld(Player player)
	{
		updateSkills(player);
	}
}
