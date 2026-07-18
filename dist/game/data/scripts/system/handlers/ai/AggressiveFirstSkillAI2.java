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
 * This class handles the AI behavior for NPCs that prioritize using their first available skill.<br>
 * It extends {@link AggressiveNpcAI2} to provide specific logic for aggressive combat encounters.
 * @author Ritsu
 * @Reworked Majka Ajural
 */
@AIName("aggressive_first_skill")
public class AggressiveFirstSkillAI2 extends AggressiveNpcAI2
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
		setUseInSpawnedSkill();
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
		setUseInSpawnedSkill();
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
		setUseInSpawnedSkill();
	}
	
	/**
	 * Executes a specific skill when the NPC is first spawned.<br>
	 * This method checks if a {@code useInSpawnedSkill} is defined in the skill list.<br>
	 * If it exists, it triggers that skill using the {@link SkillEngine}.
	 */
	private void setUseInSpawnedSkill()
	{
		if (getSkillList().getUseInSpawnedSkill() != null)
		{
			final int spawnedSkillId = getSkillList().getUseInSpawnedSkill().getSkillId();
			final int spawnedSkillLevel = getSkillList().getSkillLevel(spawnedSkillId);
			SkillEngine.getInstance().getSkill(getOwner(), spawnedSkillId, spawnedSkillLevel, getOwner()).useSkill();
		}
	}
}
