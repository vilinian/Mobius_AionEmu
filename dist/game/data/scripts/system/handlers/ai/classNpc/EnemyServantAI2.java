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
package system.handlers.ai.classNpc;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence logic for enemy servant NPCs.<br>
 * This class defines how these specific creatures behave and react within the game world.
 * @author Cheatkiller
 */
@AIName("enemyservant")
public class EnemyServantAI2 extends NpcAI2
{
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if ((getCreator() == null) || (getCreator().getTarget() == null))
			{
				return;
			}
			
			AI2Actions.targetCreature(EnemyServantAI2.this, (Creature) getCreator().getTarget());
			attack();
		}, 2000);
	}
	
	/**
	 * Initiates the attack sequence for the NPC.<br>
	 * This method schedules a recurring skill use task.<br>
	 * It uses {@link ThreadPoolManager} to run the action every 1000 milliseconds.
	 */
	private void attack()
	{
		final Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> getOwner().getController().useSkill(16907, 55), 1000, 6000);
		getOwner().getController().addTask(TaskId.SKILL_USE, task);
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
