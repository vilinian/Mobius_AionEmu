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

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This task checks if a player is currently following a specific {@link Npc}.<br>
 * It validates the proximity and status of the NPC to progress quest objectives.
 * @author ATracer
 */
public class FollowingNpcCheckTask implements Runnable
{
	private final QuestEnv env;
	private final DestinationChecker destinationChecker;
	
	/**
	 * Initializes a new task to monitor an NPC following behavior.<br>
	 * This constructor sets up the required environment and checking logic.
	 * @param env The {@link QuestEnv} containing the current quest context.
	 * @param destinationChecker The {@code DestinationChecker} used to verify movement.
	 */
	FollowingNpcCheckTask(QuestEnv env, DestinationChecker destinationChecker)
	{
		this.env = env;
		this.destinationChecker = destinationChecker;
	}
	
	@Override
	public void run()
	{
		final Player player = env.getPlayer();
		final Npc npc = (Npc) destinationChecker.follower;
		if (player.getLifeStats().isAlreadyDead() || npc.getLifeStats().isAlreadyDead())
		{
			onFail(env);
		}
		
		if (!MathUtil.isIn3dRange(player, npc, 50))
		{
			onFail(env);
		}
		
		if (destinationChecker.check())
		{
			onSuccess(env);
		}
	}
	
	/**
	 * This method is called when the NPC successfully reaches its target.<br>
	 * It stops the following behavior and notifies the {@link QuestEngine}.
	 * @param env The current quest environment context.
	 */
	private void onSuccess(QuestEnv env)
	{
		stopFollowing(env);
		QuestEngine.getInstance().onNpcReachTarget(env);
	}
	
	/**
	 * This method is called when the NPC fails to follow its target.<br>
	 * It stops the following behavior and notifies the {@link QuestEngine}.
	 * @param env The current quest environment context.
	 */
	protected void onFail(QuestEnv env)
	{
		stopFollowing(env);
		QuestEngine.getInstance().onNpcLostTarget(env);
	}
	
	/**
	 * Stops the NPC from following the player.<br>
	 * This method cancels the quest task and sends a stop event to the NPC.<br>
	 * It also removes the NPC if it is not a temporary follower.
	 * @param env The current {@code QuestEnv} context.
	 */
	private void stopFollowing(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final Npc npc = (Npc) destinationChecker.follower;
		player.getController().cancelTask(TaskId.QUEST_FOLLOW);
		npc.getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, player);
		if (!npc.getAi2().getName().equals("following"))
		{
			npc.getController().onDelete();
		}
	}
}

abstract class DestinationChecker
{
	protected Creature follower;
	
	abstract boolean check();
}

final class TargetDestinationChecker extends DestinationChecker
{
	private final Creature target;
	
	/**
	 * Creates a new checker to verify if a {@link Creature} is moving toward another {@link Creature}.<br>
	 * This method initializes the follower and the target for the check.
	 * @param follower The creature that is performing the movement.
	 * @param target The creature that the follower should be heading towards.
	 */
	TargetDestinationChecker(Creature follower, Creature target)
	{
		this.follower = follower;
		this.target = target;
	}
	
	/**
	 * Verifies if the follower is within a specific distance of the target.<br>
	 * It checks if the distance is less than or equal to {@code 10}.
	 * @return {@code true} if the follower is in range, otherwise {@code false}.
	 */
	@Override
	boolean check()
	{
		return MathUtil.isIn3dRange(target, follower, 10);
	}
}

final class CoordinateDestinationChecker extends DestinationChecker
{
	private final float x;
	private final float y;
	private final float z;
	
	/**
	 * Creates a new checker to verify if a creature is at specific coordinates.<br>
	 * This task uses the {@code x}, {@code y}, and {@code z} values to define the destination.<br>
	 * It helps determine if a {@link Creature} has reached its goal.
	 * @param follower The {@link Creature} that is moving toward the target.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 */
	CoordinateDestinationChecker(Creature follower, float x, float y, float z)
	{
		this.follower = follower;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Verifies if the follower is near specific coordinates.<br>
	 * It checks if the distance to {@code x}, {@code y}, and {@code z} is within 10 units.
	 * @return {@code true} if the follower is close enough, otherwise {@code false}.
	 */
	@Override
	boolean check()
	{
		return MathUtil.isNearCoordinates(follower, x, y, z, 10);
	}
}

final class ZoneChecker extends DestinationChecker
{
	private final ZoneName zoneName;
	
	/**
	 * Checks if a {@link Creature} is inside a specific {@link ZoneName}.<br>
	 * This method initializes the checker with the required location data.
	 * @param follower The {@code Creature} being tracked.
	 * @param zoneName The {@link ZoneName} to check against.
	 */
	ZoneChecker(Creature follower, ZoneName zoneName)
	{
		this.follower = follower;
		this.zoneName = zoneName;
	}
	
	/**
	 * Verifies if the follower is inside a specific zone.<br>
	 * It checks the location against {@code zoneName}.
	 * @return {@code true} if the follower is in the zone, otherwise {@code false}.
	 */
	@Override
	boolean check()
	{
		return follower.isInsideZone(zoneName);
	}
}

final class ZoneChecker2 extends DestinationChecker
{
	private final ZoneName zone1, zone2;
	
	/**
	 * Initializes a new {@code ZoneChecker2} instance.<br>
	 * This method sets the required zones for the check.
	 * @param follower The {@code Creature} that is being tracked.
	 * @param zone1 The first required {@link ZoneName}.
	 * @param zone2 The second required {@link ZoneName}.
	 */
	ZoneChecker2(Creature follower, ZoneName zone1, ZoneName zone2)
	{
		this.follower = follower;
		this.zone1 = zone1;
		this.zone2 = zone2;
	}
	
	/**
	 * Verifies if the follower is in a specific area.<br>
	 * It checks both {@code zone1} and {@code zone2}.
	 * @return {@code true} if the follower is inside either zone, otherwise {@code false}.
	 */
	@Override
	boolean check()
	{
		return follower.isInsideZone(zone1) || follower.isInsideZone(zone2);
	}
}
