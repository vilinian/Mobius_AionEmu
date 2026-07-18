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

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence logic for servant type NPCs.<br>
 * This class extends {@link GeneralNpcAI2} to provide specific behaviors for servants.
 * @author ATracer
 */
@AIName("servant")
public class ServantNpcAI2 extends GeneralNpcAI2
{
	/**
	 * Performs the main logic for the NPC's artificial intelligence.<br>
	 * This method calls {@code onThink} to process current actions.<br>
	 * It should be called regularly by the game engine.
	 */
	@Override
	public void think()
	{
		// servants are not thinking
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
		if (getCreator() != null)
		{
			ThreadPoolManager.getInstance().schedule(() ->
			{
				if (getOwner().getNpcObjectType() != NpcObjectType.TOTEM)
				{
					AI2Actions.targetCreature(ServantNpcAI2.this, (Creature) getCreator().getTarget());
				}
				
				healOrAttack();
			}, 200);
		}
	}
	
	/**
	 * Decides whether the NPC should heal or attack.<br>
	 * It selects a random skill if {@code skillId} is 0.<br>
	 * This method schedules a recurring task to use the chosen skill.
	 */
	private void healOrAttack()
	{
		if (skillId == 0)
		{
			skillId = getSkillList().getRandomSkill().getSkillId();
		}
		
		final int duration = getOwner().getNpcObjectType() == NpcObjectType.TOTEM ? 3000 : 5000;
		final Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> getOwner().getController().useSkill(skillId, 1), 1000, duration);
		getOwner().getController().addTask(TaskId.SKILL_USE, task);
	}
	
	/**
	 * Checks if the NPC can perform movement actions.<br>
	 * This method currently returns {@code false}.
	 * @return {@code true} if movement is supported, otherwise {@code false}.
	 */
	@Override
	public boolean isMoveSupported()
	{
		return false;
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
}
