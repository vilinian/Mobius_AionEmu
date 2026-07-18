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
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence logic for NPCs that function as traps.<br>
 * This class manages how these entities behave and interact with players in the game world.
 * @author ATracer
 * @modified Kashim
 * @Reworked Kill3r
 * @Reworked Phantom_KNA
 */
@AIName("trap")
public class TrapNpcAI2 extends NpcAI2
{
	public static int EVENT_SET_TRAP_RANGE = 1;
	@SuppressWarnings("unused")
	private final int trapRange = 0;
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		tryActivateTrap(creature);
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It updates the known list of objects.<br>
	 * It attempts to activate traps for all nearby {@code Creature} objects.<br>
	 * It calls {@code handleSpawned} from the parent class.
	 */
	@Override
	protected void handleSpawned()
	{
		getKnownList().doUpdate();
		getKnownList().doOnAllObjects(object ->
		{
			if (!(object instanceof Creature))
			{
				return;
			}
			
			final Creature creature = (Creature) object;
			tryActivateTrap(creature);
		});
		super.handleSpawned();
	}
	
	/**
	 * Attempts to trigger a trap on a specific creature.<br>
	 * Checks if the {@code creature} is within range and an enemy.<br>
	 * Executes skills and schedules a trap deletion if successful.
	 * @param creature The {@code Creature} to check for trap activation.
	 */
	private void tryActivateTrap(Creature creature)
	{
		final int npcId = getNpcId();
		int time = 1000;
		if ((getNpcId() == 833190) || (getNpcId() == 833189) || (getNpcId() == 855429))
		{
			// Fix for Skill 1058 - 1059
			if (setStateIfNot(AIState.FIGHT))
			{
				AI2Actions.targetCreature(this, creature);
				AI2Actions.useSkill(this, getSkillList().getRandomSkill().getSkillId());
				ThreadPoolManager.getInstance().schedule(new TrapDelete(this), 4500);
			}
		}
		
		if (!creature.getLifeStats().isAlreadyDead() && isInRange(creature, getOwner().getAggroRange() + 2))
		{
			final Creature creator = (Creature) getCreator();
			if (!creator.isEnemy(creature))
			{
				return;
			}
			
			if ((npcId == 833190) || (npcId == 833189) || (npcId == 749250) || (npcId == 749251) || (npcId == 855429))
			{
				// Fix for Skill 1058 - 1059
				time = 5000;
			}
			
			if (setStateIfNot(AIState.FIGHT))
			{
				AI2Actions.targetCreature(this, creature);
				AI2Actions.useSkill(this, getSkillList().getRandomSkill().getSkillId());
				ThreadPoolManager.getInstance().schedule(new TrapDelete(this), time);
			}
		}
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
	
	private static final class TrapDelete implements Runnable
	{
		private TrapNpcAI2 ai;
		
		TrapDelete(TrapNpcAI2 ai)
		{
			this.ai = ai;
		}
		
		@Override
		public void run()
		{
			AI2Actions.deleteOwner(ai);
			ai = null;
		}
	}
}
