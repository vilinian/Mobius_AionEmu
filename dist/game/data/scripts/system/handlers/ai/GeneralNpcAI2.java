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
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.AggroEventHandler;
import com.aionemu.gameserver.ai2.handler.AttackEventHandler;
import com.aionemu.gameserver.ai2.handler.CreatureEventHandler;
import com.aionemu.gameserver.ai2.handler.DiedEventHandler;
import com.aionemu.gameserver.ai2.handler.MoveEventHandler;
import com.aionemu.gameserver.ai2.handler.ReturningEventHandler;
import com.aionemu.gameserver.ai2.handler.TalkEventHandler;
import com.aionemu.gameserver.ai2.handler.TargetEventHandler;
import com.aionemu.gameserver.ai2.handler.ThinkEventHandler;
import com.aionemu.gameserver.ai2.manager.SkillAttackManager;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;

/**
 * Provides the default artificial intelligence behavior for general NPCs.<br>
 * This class handles standard actions such as movement, interaction, and combat logic for {@link Creature} entities.
 * @author ATracer
 */
@AIName("general")
public class GeneralNpcAI2 extends NpcAI2
{
	/**
	 * Triggers the main logic for the NPC's artificial intelligence.<br>
	 * This method calls {@code onThink} to process current actions.<br>
	 * It should be called regularly by the game engine.
	 */
	@Override
	public void think()
	{
		ThinkEventHandler.onThink(this);
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		DiedEventHandler.onDie(this);
	}
	
	/**
	 * Processes the logic for when this AI is attacked by a {@code Creature}.<br>
	 * It checks if the attacker is within 40 units of the owner.<br>
	 * If close enough, it calculates a path to move away from the attacker.<br>
	 * The owner will then move toward the nearest valid collision point.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		AttackEventHandler.onAttack(this, creature);
	}
	
	/**
	 * Checks if the {@code creature} requires support from this NPC.<br>
	 * This method delegates the logic to the {@link AggroEventHandler}.
	 * @param creature The {@code Creature} that might need assistance.
	 * @return {@code true} if the NPC handles the request, otherwise {@code false}.
	 */
	@Override
	protected boolean handleCreatureNeedsSupport(Creature creature)
	{
		return AggroEventHandler.onCreatureNeedsSupport(this, creature);
	}
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		TalkEventHandler.onTalk(this, player);
	}
	
	/**
	 * This method is called when a conversation with a player ends.<br>
	 * It triggers the {@code onFinishTalk} logic for the given entity.
	 * @param creature The {@code Player} who finished the dialog.
	 */
	@Override
	protected void handleDialogFinish(Player creature)
	{
		TalkEventHandler.onFinishTalk(this, creature);
	}
	
	/**
	 * This method is called when an attack action finishes.<br>
	 * It triggers the {@code onFinishAttack} logic.
	 */
	@Override
	protected void handleFinishAttack()
	{
		AttackEventHandler.onFinishAttack(this);
	}
	
	/**
	 * This method is called when an attack action finishes.<br>
	 * It triggers the {@code onAttackComplete} logic.
	 */
	@Override
	protected void handleAttackComplete()
	{
		AttackEventHandler.onAttackComplete(this);
	}
	
	/**
	 * This method is called when the NPC successfully reaches its target.<br>
	 * It triggers the {@code onTargetReached} logic.
	 */
	@Override
	protected void handleTargetReached()
	{
		TargetEventHandler.onTargetReached(this);
	}
	
	/**
	 * This method is called when the NPC is not at its home location.<br>
	 * It triggers the logic defined in {@code onNotAtHome}.
	 */
	@Override
	protected void handleNotAtHome()
	{
		ReturningEventHandler.onNotAtHome(this);
	}
	
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		ReturningEventHandler.onBackHome(this);
	}
	
	/**
	 * This method is called when the current target moves too far away.<br>
	 * It triggers the {@code onTargetTooFar} logic.
	 */
	@Override
	protected void handleTargetTooFar()
	{
		TargetEventHandler.onTargetTooFar(this);
	}
	
	/**
	 * This method is called when the AI gives up on its current target.<br>
	 * It triggers the {@code onTargetGiveup} logic.
	 */
	@Override
	protected void handleTargetGiveup()
	{
		TargetEventHandler.onTargetGiveup(this);
	}
	
	/**
	 * This method is called when the target of a {@link Creature} changes.<br>
	 * It notifies the base class and the {@link TargetEventHandler}.
	 * @param creature The {@code Creature} whose target has changed.
	 */
	@Override
	protected void handleTargetChanged(Creature creature)
	{
		super.handleTargetChanged(creature);
		TargetEventHandler.onTargetChange(this, creature);
	}
	
	/**
	 * Validates the current movement of the NPC.<br>
	 * This method calls {@code onMoveValidate} to check if the move is legal.
	 */
	@Override
	protected void handleMoveValidate()
	{
		MoveEventHandler.onMoveValidate(this);
	}
	
	/**
	 * This method is called when an NPC reaches its destination.<br>
	 * It triggers the logic for completing a movement action.<br>
	 * It calls {@code onMoveArrived} to process the event.
	 */
	@Override
	protected void handleMoveArrived()
	{
		super.handleMoveArrived();
		MoveEventHandler.onMoveArrived(this);
	}
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		CreatureEventHandler.onCreatureMoved(this, creature);
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It broadcasts a system message to the {@link Player} owner.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		super.handleDespawned();
	}
	
	/**
	 * Determines if this AI handler is capable of processing a specific event.<br>
	 * It checks the base logic from {@code canHandleEvent} and applies additional rules for certain events.
	 * @param eventType The type of event to check.
	 * @return {@code true} if the event can be handled, {@code false} otherwise.
	 */
	@Override
	protected boolean canHandleEvent(AIEventType eventType)
	{
		final boolean canHandle = super.canHandleEvent(eventType);
		
		switch (eventType)
		{
			case CREATURE_MOVED:
				return canHandle || DataManager.NPC_SHOUT_DATA.hasAnyShout(getOwner().getWorldId(), getOwner().getNpcId(), ShoutEventType.SEE);
			case CREATURE_NEEDS_SUPPORT:
				return canHandle && isNonFightingState() && DataManager.TRIBE_RELATIONS_DATA.hasSupportRelations(getOwner().getTribe());
			default:
				break;
		}
		
		return canHandle;
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
		final VisibleObject currentTarget = getTarget();
		final Creature mostHated = getAggroList().getMostHated();
		
		if ((mostHated == null) || mostHated.getLifeStats().isAlreadyDead())
		{
			return AttackIntention.FINISH_ATTACK;
		}
		
		if ((currentTarget == null) || !currentTarget.getObjectId().equals(mostHated.getObjectId()))
		{
			onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
			return AttackIntention.SWITCH_TARGET;
		}
		
		final NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
		if (skill != null)
		{
			skillId = skill.getSkillId();
			skillLevel = skill.getSkillLevel();
			return AttackIntention.SKILL_ATTACK;
		}
		
		return AttackIntention.SIMPLE_ATTACK;
	}
}
