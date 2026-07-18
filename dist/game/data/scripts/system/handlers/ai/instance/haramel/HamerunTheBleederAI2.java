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
package system.handlers.ai.instance.haramel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldPosition;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * This class handles the specific AI behavior for the NPC {@code hamerun_the_bleeder}.<br>
 * It extends {@link AggressiveNpcAI2} to provide custom logic for this instance.
 * @Author Majka Ajural
 */
@AIName("hamerun_the_bleeder")
public class HamerunTheBleederAI2 extends AggressiveNpcAI2
{
	private final AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> taskAbilityDrain;
	private final int servant1Id = 282041; // Brainwashed Fighter
	private final int servant2Id = 282042; // Brainwashed MuMu Fighter
	protected List<Integer> percents = new ArrayList<>();
	private boolean canThink = true;
	private Phase phase = Phase.ACTIVE;
	
	private enum Phase
	{
		ACTIVE,
		SERVANT,
	}
	
	/**
	 * Checks if the AI is currently allowed to process logic.<br>
	 * This method returns the current state of the {@code canThink} variable.
	 * @return {@code true} if the AI can think, or {@code false} otherwise.
	 */
	@Override
	public boolean canThink()
	{
		return canThink;
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code addPercent()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		addPercent();
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
		super.handleAttack(creature);
		
		if (isHome.compareAndSet(true, false))
		{
			taskAbilityDrainStart();
		}
		
		callForHelp(10);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	/**
	 * Handles the logic for when an NPC returns home.<br>
	 * It updates the internal state and resets various behaviors.<br>
	 * This includes stopping tasks, despawning servants, and clearing effects.
	 */
	@Override
	protected void handleBackHome()
	{
		super.handleBackHome();
		isHome.set(true);
		phase = Phase.ACTIVE;
		canThink = true;
		taskAbilityDrainStop();
		despawnServants();
		getEffectController().removeAllEffects(); // Remove all effects on owner
		addPercent();
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		super.handleDied();
		
		// Cancel owner's tasks
		taskAbilityDrainStop();
		
		// Destroys owner's spawns
		despawnServants();
		
		// Destroys owner
		AI2Actions.deleteOwner(this);
	}
	
	/**
	 * Resets the {@code percents} list.<br>
	 * It clears all existing values.<br>
	 * It adds a default value of {@code 50} to the list.
	 */
	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]
		{
			50
		});
	}
	
	// Spawn both Servants of Hamerun
	/**
	 * Triggers the summoning of servants for the NPC.<br>
	 * This method sends a random shout message to the owner.<br>
	 * It spawns two specific servant NPCs at nearby positions.<br>
	 * The new servants are commanded to attack the most hated target.
	 */
	private void spawnServants()
	{
		// Random shout of Hamerun to call his servants
		final int[] hamerunServantShouts =
		{
			1500102,
			1500103
		};
		NpcShoutsService.getInstance().sendMsg(getOwner(), hamerunServantShouts[Rnd.get(0, 1)], getObjectId(), 0, 0);
		
		// Spawns Brainwashed Fighter [ID: 282041] and Brainwashed MuMu Fighter [ID: 282042]
		final WorldPosition npcPosition = getOwner().getPosition();
		final float npcX = npcPosition.getX();
		final float npcY = npcPosition.getY();
		final float npcZ = npcPosition.getZ();
		
		final Npc servant1spawn = (Npc) spawn(servant1Id, npcX - 2.0f, npcY + 2.0f, npcZ, (byte) 60); // Right
		final Npc servant2spawn = (Npc) spawn(servant2Id, npcX + 2.0f, npcY - 2.0f, npcZ, (byte) 22); // Left
		
		// Sets target against player and attack it
		final Creature player = getAggroList().getMostHated();
		servant1spawn.setTarget(player);
		servant1spawn.getController().attackTarget(player, 0, 0, 0);
		servant2spawn.setTarget(player);
		servant2spawn.getController().attackTarget(player, 0, 0, 0);
		
		// Sets owner's target as itself
		getOwner().setTarget(getOwner());
	}
	
	// Despawn servants; called on return home
	/**
	 * Removes all active servants from the current world map.<br>
	 * This method calls {@code deleteNpcs} for both servant types.<br>
	 * It targets NPCs with IDs {@code 282041} and {@code 282042}.
	 */
	private void despawnServants()
	{
		final WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(servant1Id));
		deleteNpcs(instance.getNpcs(servant2Id));
	}
	
	/**
	 * This method removes a list of {@link Npc} objects from the game.<br>
	 * It iterates through each {@code Npc} in the provided list.<br>
	 * If an {@code Npc} is not {@code null}, it calls {@code onDelete()} on its controller.
	 * @param npcs The list of {@link Npc} objects to be deleted.
	 */
	private void deleteNpcs(List<Npc> npcs)
	{
		for (Npc npc : npcs)
		{
			if (npc != null)
			{
				npc.getController().onDelete();
			}
		}
	}
	
	/**
	 * Checks the current health percentage against a list of thresholds.<br>
	 * It triggers specific actions like spawning servants when certain levels are reached.<br>
	 * This method is synchronized to ensure thread safety during state changes.
	 * @param hpPercentage The current health percentage of the creature.
	 */
	private synchronized void checkPercentage(int hpPercentage)
	{
		for (Integer percent : percents)
		{
			if (hpPercentage <= percent)
			{
				switch (percent)
				{
					case 50:
						// Spawning servants
						if (!isAlreadyDead() && phase.equals(Phase.ACTIVE))
						{
							// Stops Ability Drain task to avoid to stop him without shield as actually two buffs cannot be got together
							taskAbilityDrainStop();
							
							taskHamerunHypnosisStart();
						}
						
						percents.remove(percent);
						break;
				}
			}
			break;
		}
	}
	
	/**
	 * Stops the active ability drain task.<br>
	 * This method checks if {@code taskAbilityDrain} is currently running.<br>
	 * If it is not finished, it cancels the task immediately.
	 */
	private void taskAbilityDrainStop()
	{
		if ((taskAbilityDrain != null) && !taskAbilityDrain.isDone())
		{
			taskAbilityDrain.cancel(true);
		}
	}
	
	/**
	 * Starts a recurring task to use the {@code Ability Drain} skill.<br>
	 * This task runs every {@code 10000} milliseconds.<br>
	 * It checks if the owner is alive and in the {@code ACTIVE} phase before casting.
	 */
	private void taskAbilityDrainStart()
	{
		taskAbilityDrain = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			if (isAlreadyDead())
			{
				taskAbilityDrainStop();
			}
			else
			{
				if (!getOwner().isCasting() && phase.equals(Phase.ACTIVE))
				{
					getOwner().getController().useSkill(19264, 11); // Ability Drain
				}
			}
		}, 0, 10000);
	}
	
	// Starts when hp is below 50%
	/**
	 * Starts the hypnosis sequence for Hamerun.<br>
	 * This method changes the NPC state to {@code SERVANT} and stops current actions.<br>
	 * It casts skill {@code 19210} and schedules a return to {@code ACTIVE} phase after 18000ms.<br>
	 * It also triggers the spawning of servants.
	 */
	private void taskHamerunHypnosisStart()
	{
		// Stops attacks
		phase = Phase.SERVANT;
		canThink = false;
		EmoteManager.emoteStopAttacking(getOwner());
		setStateIfNot(AIState.WALKING);
		
		// Casts the Hamerun's Hypnosis skill (ID: 19210)
		getOwner().getController().abortCast(); // Stops eventual cast skill
		SkillEngine.getInstance().getSkill(getOwner(), 19210, 1, getOwner()).useSkill();
		
		// Restarts fighting after the end of Hamerun's Hypnosis skill
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (!isAlreadyDead() && !isHome.get() && phase.equals(Phase.SERVANT))
			{
				phase = Phase.ACTIVE;
				canThink = true;
				
				final Creature player = getAggroList().getMostHated();
				if ((player == null) || player.getLifeStats().isAlreadyDead() || !getOwner().canSee(player))
				{
					setStateIfNot(AIState.FIGHT);
					think();
				}
				else
				{
					getMoveController().abortMove();
					getOwner().setTarget(player);
					getOwner().getGameStats().renewLastAttackTime();
					getOwner().getGameStats().renewLastAttackedTime();
					getOwner().getGameStats().renewLastChangeTargetTime();
					getOwner().getGameStats().renewLastSkillTime();
					setStateIfNot(AIState.FIGHT);
					handleMoveValidate();
					taskAbilityDrainStart();
				}
			}
		}, 18000);
		
		spawnServants();
	}
}
