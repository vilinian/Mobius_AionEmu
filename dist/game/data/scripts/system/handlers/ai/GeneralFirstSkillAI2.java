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
package system.handlers.ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.skillengine.SkillEngine;

/**
 * This class handles the AI logic for NPCs using the {@code general_first_skill} behavior.<br>
 * It manages how these entities select and execute their primary skills during combat.
 * @author Ritsu
 */
@AIName("general_first_skill")
public class GeneralFirstSkillAI2 extends GeneralNpcAI2
{
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		super.handleBackHome();
		if (getSkillList().getUseInSpawnedSkill() != null)
		{
			final int skillId = getSkillList().getUseInSpawnedSkill().getSkillId();
			final int skillLevel = getSkillList().getSkillLevel(skillId);
			SkillEngine.getInstance().getSkill(getOwner(), skillId, skillLevel, getOwner()).useSkill();
		}
	}
	
	/**
	 * This method is called when the NPC has respawned.<br>
	 * It triggers the logic to enable specific skills for the new spawn.<br>
	 * It calls {@code handleRespawned} from the parent class.
	 */
	@Override
	protected void handleRespawned()
	{
		super.handleRespawned();
		if (getSkillList().getUseInSpawnedSkill() != null)
		{
			final int skillId = getSkillList().getUseInSpawnedSkill().getSkillId();
			final int skillLevel = getSkillList().getSkillLevel(skillId);
			SkillEngine.getInstance().getSkill(getOwner(), skillId, skillLevel, getOwner()).useSkill();
		}
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		if (getSkillList().getUseInSpawnedSkill() != null)
		{
			final int skillId = getSkillList().getUseInSpawnedSkill().getSkillId();
			final int skillLevel = getSkillList().getSkillLevel(skillId);
			SkillEngine.getInstance().getSkill(getOwner(), skillId, skillLevel, getOwner()).useSkill();
		}
	}
}
