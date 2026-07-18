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
package system.handlers.ai.instance.tallocsHollow;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldPosition;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence behavior for the {@code celestius} NPC.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific combat logic for this entity.
 * @author xTz
 */
@AIName("celestius")
public class CelestiusAI2 extends AggressiveNpcAI2
{
	private final AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> helpersTask;
	
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
			startHelpersCall();
			
		}
	}
	
	/**
	 * Stops the background task for helpers.<br>
	 * This method checks if {@code helpersTask} is currently running.<br>
	 * If it is active, it cancels the task immediately.
	 */
	private void cancelHelpersTask()
	{
		if ((helpersTask != null) && !helpersTask.isDone())
		{
			helpersTask.cancel(true);
		}
	}
	
	/**
	 * Starts a recurring task to manage helper NPCs.<br>
	 * This method schedules a background thread via {@link ThreadPoolManager}.<br>
	 * It checks the NPC status and triggers skills or spawns helpers periodically.
	 */
	private void startHelpersCall()
	{
		helpersTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			if (isAlreadyDead() && (getLifeStats().getHpPercentage() < 90))
			{
				deleteHelpers();
				cancelHelpersTask();
			}
			else
			{
				deleteHelpers();
				SkillEngine.getInstance().getSkill(getOwner(), 18981, 44, getOwner()).useNoAnimationSkill();
				startRun((Npc) spawn(281514, 518, 813, 1378, (byte) 0), "3001900001");
				startRun((Npc) spawn(281514, 551, 795, 1376, (byte) 0), "3001900002");
				startRun((Npc) spawn(281514, 574, 854, 1375, (byte) 0), "3001900003");
			}
		}, 1000, 25000);
	}
	
	/**
	 * Initiates the walking behavior for a specific {@link Npc}.<br>
	 * It sets the walker ID and triggers the movement logic.<br>
	 * The NPC state is updated to start the action.<br>
	 * A broadcast packet is sent to show the starting emotion.
	 * @param npc The {@link Npc} instance that will perform the walk.
	 * @param walkId The unique identifier for the walking animation or path.
	 */
	private void startRun(Npc npc, String walkId)
	{
		npc.getSpawn().setWalkerId(walkId);
		WalkManager.startWalking((NpcAI2) npc.getAi2());
		npc.setState(1);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
	}
	
	/**
	 * Removes specific helper NPCs from the current world map.<br>
	 * This method checks for NPCs with ID {@code 281514}.<br>
	 * It deletes them if they are at specific X coordinates.
	 */
	private void deleteHelpers()
	{
		final WorldPosition p = getPosition();
		if (p != null)
		{
			final WorldMapInstance instance = p.getWorldMapInstance();
			if (instance != null)
			{
				final List<Npc> npcs = instance.getNpcs(281514);
				for (Npc npc : npcs)
				{
					final SpawnTemplate template = npc.getSpawn();
					if ((template.getX() == 518) || (template.getX() == 551) || (template.getX() == 574))
					{
						npc.getController().onDelete();
					}
				}
			}
		}
	}
	
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		cancelHelpersTask();
		deleteHelpers();
		isHome.set(true);
		super.handleBackHome();
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It cancels any active helper tasks and removes existing helpers.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		cancelHelpersTask();
		deleteHelpers();
		super.handleDespawned();
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It cancels any active helper tasks and removes existing helpers.<br>
	 * It then calls {@code handleDied} to finish processing.
	 */
	@Override
	protected void handleDied()
	{
		cancelHelpersTask();
		deleteHelpers();
		super.handleDied();
	}
}
