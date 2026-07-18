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
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.manager.AttackManager;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Homing;

/**
 * Handles the artificial intelligence for NPCs that move toward a specific target.<br>
 * This class manages the behavior of {@link com.aionemu.gameserver.model.gameobjects.Homing} objects.<br>
 * It extends {@link GeneralNpcAI2} to provide specialized movement and tracking logic.
 * @author ATracer
 */
@AIName("homing")
public class HomingNpcAI2 extends GeneralNpcAI2
{
	/**
	 * Executes the logic for the NPC's artificial intelligence.<br>
	 * This method triggers {@code onThink} to handle current actions.<br>
	 * The game engine calls this method on a regular basis.
	 */
	@Override
	public void think()
	{
		// homings are not thinking to return :)
	}
	
	/**
	 * Determines the next action for attacking a target.<br>
	 * This method checks if the current target is valid and alive.<br>
	 * It decides whether to switch targets, use a skill, or perform a simple attack.
	 * @return the chosen {@code AttackIntention} for the NPC.
	 */
	@Override
	public AttackIntention chooseAttackIntention()
	{
		// TODO skill type homings
		return AttackIntention.SIMPLE_ATTACK;
	}
	
	/**
	 * This method is called when an attack action finishes.<br>
	 * It triggers the {@code onAttackComplete} logic.
	 */
	@Override
	protected void handleAttackComplete()
	{
		super.handleAttackComplete();
		final Homing owner = (Homing) getOwner();
		if (owner.getActiveSkillId() != 0)
		{
			AttackManager.scheduleNextAttack(this);
		}
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
