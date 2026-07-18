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

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.ai.BombTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence for bomb-related NPCs.<br>
 * This class defines specific behaviors and actions for entities using the {@code bomb} AI type.
 * @author xTz
 */
@AIName("bomb")
public class BombAi2 extends AggressiveNpcAI2
{
	private BombTemplate template;
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It retrieves the {@code BombTemplate} from {@link DataManager}.<br>
	 * It schedules the {@code doUseSkill} method to run after 2000 milliseconds.
	 */
	@Override
	protected void handleSpawned()
	{
		template = DataManager.AI_DATA.getAiTemplate().get(getNpcId()).getBombs().getBombTemplate();
		ThreadPoolManager.getInstance().schedule(() -> doUseSkill(), 2000);
	}
	
	/**
	 * Schedules the execution of a skill.<br>
	 * It uses {@link ThreadPoolManager} to run the task after a delay.<br>
	 * The delay is based on the cooldown from the {@code template}.
	 */
	private void doUseSkill()
	{
		ThreadPoolManager.getInstance().schedule(() -> useSkill(template.getSkillId()), template.getCd());
	}
	
	/**
	 * This method handles specific logic for the {@code bomb} NPC.<br>
	 * It checks the type of {@code question} provided by the system.<br>
	 * It returns a predefined {@link AIAnswer} based on the question type.
	 * @param question The {@code AIQuestion} being asked to this instance.
	 * @return The corresponding {@code AIAnswer} or {@code null}.
	 */
	@Override
	protected AIAnswer pollInstance(AIQuestion question)
	{
		switch (question)
		{
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}
	
	/**
	 * Executes a specific skill for the NPC.<br>
	 * This method targets the self and triggers the {@code AI2Actions.useSkill} action.<br>
	 * It also schedules the removal of the owner based on the skill duration.
	 * @param skill The unique identifier for the skill to be used.
	 */
	private void useSkill(int skill)
	{
		AI2Actions.targetSelf(this);
		AI2Actions.useSkill(this, skill);
		final int duration = DataManager.SKILL_DATA.getSkillTemplate(skill).getDuration();
		ThreadPoolManager.getInstance().schedule(() -> AI2Actions.deleteOwner(BombAi2.this), duration != 0 ? duration + 4000 : 0);
	}
}
