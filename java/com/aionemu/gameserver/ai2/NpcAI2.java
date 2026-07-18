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
package com.aionemu.gameserver.ai2;

import com.aionemu.gameserver.ai2.handler.ActivateEventHandler;
import com.aionemu.gameserver.ai2.handler.DiedEventHandler;
import com.aionemu.gameserver.ai2.handler.ShoutEventHandler;
import com.aionemu.gameserver.ai2.handler.SpawnEventHandler;
import com.aionemu.gameserver.ai2.handler.TargetEventHandler;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.ai2.poll.NpcAIPolls;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.controllers.movement.NpcMoveController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.knownlist.KnownList;

/**
 * This class handles the artificial intelligence logic for {@link Npc} entities.<br>
 * It manages behaviors such as movement, combat, and interaction based on the {@link AIConfig}.<br>
 * It serves as the primary engine for processing NPC actions in the game world.
 * @author ATracer
 */
@AIName("npc")
public class NpcAI2 extends AITemplate
{
	/**
	 * Retrieves the {@link Npc} object that owns this AI instance.<br>
	 * This method returns the underlying NPC entity associated with the AI.
	 * @return The {@code Npc} owner of this AI.
	 */
	@Override
	public Npc getOwner()
	{
		return (Npc) super.getOwner();
	}
	
	/**
	 * Retrieves the {@code NpcTemplate} for this summon.<br>
	 * This method fetches the template from the owner of the summon.
	 * @return The {@code NpcTemplate} associated with the owner.
	 */
	protected NpcTemplate getObjectTemplate()
	{
		return getOwner().getObjectTemplate();
	}
	
	/**
	 * Retrieves the {@code SpawnTemplate} associated with this summon.<br>
	 * It fetches the spawn data from the owner of the summon.
	 * @return the {@code SpawnTemplate} for the current summon.
	 */
	protected SpawnTemplate getSpawnTemplate()
	{
		return getOwner().getSpawn();
	}
	
	/**
	 * Retrieves the life statistics for this NPC.<br>
	 * This method fetches data from the {@link Npc} owner.
	 * @return the {@code NpcLifeStats} object.
	 */
	protected NpcLifeStats getLifeStats()
	{
		return getOwner().getLifeStats();
	}
	
	/**
	 * Retrieves the {@code Race} of the owner.<br>
	 * This method calls {@code getOwner} to find the associated player.<br>
	 * It then returns that player's race information.
	 * @return The {@code Race} object belonging to the summon's owner.
	 */
	protected Race getRace()
	{
		return getOwner().getRace();
	}
	
	/**
	 * Retrieves the {@link TribeClass} of the NPC owner.<br>
	 * This method calls {@code getOwner} to fetch the tribe information.
	 * @return The {@code TribeClass} associated with the NPC.
	 */
	protected TribeClass getTribe()
	{
		return getOwner().getTribe();
	}
	
	/**
	 * Retrieves the {@link EffectController} for this NPC.<br>
	 * This method fetches the controller from the owner object.
	 * @return the {@code EffectController} associated with the NPC.
	 */
	protected EffectController getEffectController()
	{
		return getOwner().getEffectController();
	}
	
	/**
	 * Retrieves the {@link KnownList} associated with this NPC.<br>
	 * This method fetches the list from the owner object.
	 * @return the {@code KnownList} of the owner.
	 */
	protected KnownList getKnownList()
	{
		return getOwner().getKnownList();
	}
	
	/**
	 * Retrieves the list of aggressive targets for this NPC.<br>
	 * This method delegates the request to the {@link Npc} owner.
	 * @return the {@code AggroList} associated with the owner.
	 */
	protected AggroList getAggroList()
	{
		return getOwner().getAggroList();
	}
	
	/**
	 * Retrieves the list of skills for the owner.<br>
	 * This method calls {@code getSkillList} on the current NPC.
	 * @return the {@code NpcSkillList} associated with this NPC.
	 */
	protected NpcSkillList getSkillList()
	{
		return getOwner().getSkillList();
	}
	
	/**
	 * Retrieves the {@link VisibleObject} that created this NPC.<br>
	 * This method delegates the request to the owner's creator.
	 * @return the {@code VisibleObject} responsible for creating the owner.
	 */
	protected VisibleObject getCreator()
	{
		return getOwner().getCreator();
	}
	
	/**
	 * Retrieves the movement controller for this NPC.<br>
	 * This method delegates the request to the {@link Npc} owner.
	 * @return the {@code NpcMoveController} associated with the owner.
	 */
	protected NpcMoveController getMoveController()
	{
		return getOwner().getMoveController();
	}
	
	/**
	 * Retrieves the unique identifier for the {@link Npc} owner.<br>
	 * This method calls {@code getOwner} to fetch the ID.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return getOwner().getNpcId();
	}
	
	/**
	 * Retrieves the unique identifier of the creator.<br>
	 * This method calls {@code getOwner} to fetch the owner object.<br>
	 * It then returns the ID from that owner.
	 * @return the integer ID of the creator.
	 */
	protected int getCreatorId()
	{
		return getOwner().getCreatorId();
	}
	
	/**
	 * Checks if a specific {@code VisibleObject} is within a certain distance.<br>
	 * This method uses the owner of this AI to calculate the 3D distance.
	 * @param object The target object to check.
	 * @param range The maximum distance allowed.
	 * @return {@code true} if the object is within the range, otherwise {@code false}.
	 */
	protected boolean isInRange(VisibleObject object, int range)
	{
		return MathUtil.isIn3dRange(getOwner(), object, range);
	}
	
	/**
	 * This method is called when the AI becomes active.<br>
	 * It handles the initial logic for starting an AI behavior.<br>
	 * You can override this to perform custom actions upon activation.
	 */
	@Override
	protected void handleActivate()
	{
		ActivateEventHandler.onActivate(this);
	}
	
	/**
	 * Handles the logic for when an AI becomes inactive.<br>
	 * This method is called to clean up states or stop actions.<br>
	 * It works in tandem with {@code handleActivate}.
	 */
	@Override
	protected void handleDeactivate()
	{
		ActivateEventHandler.onDeactivate(this);
	}
	
	/**
	 * Executes logic when an NPC is spawned into the world.<br>
	 * This method triggers the {@code onSpawn} event.
	 */
	@Override
	protected void handleSpawned()
	{
		SpawnEventHandler.onSpawn(this);
	}
	
	/**
	 * This method is called when the NPC has respawned.<br>
	 * It triggers the logic to enable specific skills for the new spawn.<br>
	 * It calls {@code handleRespawned} from the parent class.
	 */
	@Override
	protected void handleRespawned()
	{
		SpawnEventHandler.onRespawn(this);
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It triggers specific events for shouting and spawning handlers.<br>
	 * This method ensures that {@link ShoutEventHandler} and {@link SpawnEventHandler} are notified.
	 */
	@Override
	protected void handleDespawned()
	{
		if (poll(AIQuestion.CAN_SHOUT))
		{
			ShoutEventHandler.onBeforeDespawn(this);
		}
		
		SpawnEventHandler.onDespawn(this);
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onSimpleDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		DiedEventHandler.onSimpleDie(this);
	}
	
	/**
	 * This method is called when an NPC reaches its destination.<br>
	 * It triggers the logic for completing a movement action.<br>
	 * It calls {@code onReachedWalkPoint} to process the event.
	 */
	@Override
	protected void handleMoveArrived()
	{
		if (!poll(AIQuestion.CAN_SHOUT) || (getSpawnTemplate().getWalkerId() == null))
		{
			return;
		}
		
		ShoutEventHandler.onReachedWalkPoint(this);
	}
	
	/**
	 * This method is called when the target of a {@link Creature} changes.<br>
	 * It notifies the base class and the {@link TargetEventHandler}.
	 * @param creature The {@code Creature} whose target has changed.
	 */
	@Override
	protected void handleTargetChanged(Creature creature)
	{
		super.handleMoveArrived();
		if (!poll(AIQuestion.CAN_SHOUT))
		{
			return;
		}
		
		ShoutEventHandler.onSwitchedTarget(this, creature);
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
				return NpcAIPolls.shouldDecay(this);
			case SHOULD_RESPAWN:
				return NpcAIPolls.shouldRespawn(this);
			case SHOULD_REWARD:
				return AIAnswers.POSITIVE;
			case CAN_SHOUT:
				return isMayShout() ? AIAnswers.POSITIVE : AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}
	
	/**
	 * Checks if the AI is allowed to perform a shout action.<br>
	 * This method returns {@code false} by default.
	 * @return {@code true} if shouting is permitted, otherwise {@code false}.
	 */
	@Override
	public boolean isMayShout()
	{
		// temp fix, we shouldn't rely on it because of inheritance
		if (AIConfig.SHOUTS_ENABLE)
		{
			return getOwner().mayShout(0);
		}
		
		return false;
	}
	
	/**
	 * Checks if the NPC is currently able to move.<br>
	 * It verifies that the movement speed is greater than {@code 0}.<br>
	 * It also ensures the NPC is not in a {@code FREEZE} state.
	 * @return {@code true} if the NPC can move, otherwise {@code false}.
	 */
	public boolean isMoveSupported()
	{
		return (getOwner().getGameStats().getMovementSpeedFloat() > 0) && !isInSubState(AISubState.FREEZE);
	}
}
