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
package com.aionemu.gameserver.questEngine.task;

import java.util.concurrent.Future;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This class manages the execution of asynchronous tasks related to the quest system.<br>
 * It handles background processing for various {@link com.aionemu.gameserver.questEngine.model.QuestEnv} operations.<br>
 * Use this class to offload heavy quest logic from the main game thread.
 * @author ATracer
 */
public class QuestTasks
{
	/**
	 * Creates a background task to check if an {@link Npc} is following a specific target.<br>
	 * This task runs repeatedly every 1000 milliseconds.
	 * @param env The current quest environment context.
	 * @param npc The NPC that needs to be checked.
	 * @param target The destination NPC that the first NPC should follow.
	 * @return A {@code Future} representing the scheduled task.
	 */
	public static Future<?> newFollowingToTargetCheckTask(QuestEnv env, Npc npc, Npc target)
	{
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpcCheckTask(env, new TargetDestinationChecker(npc, target)), 1000, 1000);
	}
	
	/**
	 * Creates a background task to check if an {@link Npc} is following a specific target.<br>
	 * This method uses the {@code npcTargetId} to find the correct spawn location.<br>
	 * It schedules a repeating check every 1000 milliseconds.
	 * @param env The current quest environment context.
	 * @param npc The NPC that is performing the following action.
	 * @param npcTargetId The unique ID of the target NPC to follow.
	 * @return A {@code Future} representing the scheduled background task.
	 */
	public static Future<?> newFollowingToTargetCheckTask(QuestEnv env, Npc npc, int npcTargetId)
	{
		final SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(npc.getWorldId(), npcTargetId);
		if (searchResult == null)
		{
			throw new IllegalArgumentException("Supplied npc doesn't exist: " + npcTargetId);
		}
		
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpcCheckTask(env, new CoordinateDestinationChecker(npc, searchResult.getSpot().getX(), searchResult.getSpot().getY(), searchResult.getSpot().getZ())), 1000, 1000);
	}
	
	/**
	 * Creates a background task to check if an {@link Npc} is following a specific location.<br>
	 * This task runs repeatedly every 1000 milliseconds.
	 * @param env The current quest environment context.
	 * @param npc The NPC that needs to be monitored.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 * @return A {@code Future} representing the scheduled task.
	 */
	public static Future<?> newFollowingToTargetCheckTask(QuestEnv env, Npc npc, float x, float y, float z)
	{
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpcCheckTask(env, new CoordinateDestinationChecker(npc, x, y, z)), 1000, 1000);
	}
	
	/**
	 * Creates a background task to check if an {@link Npc} is following a target.<br>
	 * This task runs repeatedly every 1000 milliseconds.<br>
	 * It uses the provided {@link ZoneName} to limit the search area.
	 * @param env The current quest environment context.
	 * @param npc The NPC that is performing the action.
	 * @param zoneName The specific zone where the check should occur.
	 * @return A {@code Future} representing the scheduled task.
	 */
	public static Future<?> newFollowingToTargetCheckTask(QuestEnv env, Npc npc, ZoneName zoneName)
	{
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpcCheckTask(env, new ZoneChecker(npc, zoneName)), 1000, 1000);
	}
	
	/**
	 * Creates a periodic task to check if an {@link Npc} is following a target.<br>
	 * This task runs every 1000 milliseconds.<br>
	 * It validates the movement between two specific zones.
	 * @param env The current quest environment context.
	 * @param npc The NPC that needs to be monitored.
	 * @param zoneName1 The first zone boundary for the check.
	 * @param zoneName2 The second zone boundary for the check.
	 * @return A {@code Future} representing the scheduled task.
	 */
	public static Future<?> newFollowingToTargetCheckTask(QuestEnv env, Npc npc, ZoneName zoneName1, ZoneName zoneName2)
	{
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpcCheckTask(env, new ZoneChecker2(npc, zoneName1, zoneName2)), 1000, 1000);
	}
}
