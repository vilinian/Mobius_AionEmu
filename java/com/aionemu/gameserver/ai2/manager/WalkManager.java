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
package com.aionemu.gameserver.ai2.manager;

import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.walker.RouteStep;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * Manages the movement and pathfinding logic for {@link Npc} entities.<br>
 * It handles complex navigation tasks such as route following and collision avoidance. This class ensures that NPCs move smoothly across the game world using {@link WalkerTemplate} data.
 * @author ATracer
 */
public class WalkManager
{
	private static final int WALK_RANDOM_RANGE = 5;
	
	/**
	 * Starts the walking behavior for a specific NPC.<br>
	 * It sets the {@link AIState} to {@code WALKING}.<br>
	 * If a walker template exists, it starts route walking.<br>
	 * Otherwise, it starts random walking.
	 * @param npcAI The {@link NpcAI2} instance to start walking.
	 * @return {@code true} if the walking process started successfully, or {@code false} otherwise.
	 */
	public static boolean startWalking(NpcAI2 npcAI)
	{
		npcAI.setStateIfNot(AIState.WALKING);
		final Npc owner = npcAI.getOwner();
		final WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(owner.getSpawn().getWalkerId());
		if (template != null)
		{
			npcAI.setSubStateIfNot(AISubState.WALK_PATH);
			startRouteWalking(npcAI, owner, template);
		}
		else
		{
			return startRandomWalking(npcAI, owner);
		}
		
		return true;
	}
	
	/**
	 * Starts a walking behavior for an {@link NpcAI2} instance.<br>
	 * It uses the provided {@link WalkerTemplate} to define the path.<br>
	 * If the template is {@code null}, it starts random walking instead.
	 * @param npcAI The AI instance that will perform the walking action.
	 * @param template The template containing the route steps for the NPC.
	 * @return {@code true} if the walking behavior started successfully, otherwise {@code false}.
	 */
	public static boolean startRouteWalking(NpcAI2 npcAI, WalkerTemplate template)
	{
		npcAI.setStateIfNot(AIState.WALKING);
		final Npc owner = npcAI.getOwner();
		
		if (template != null)
		{
			npcAI.setSubStateIfNot(AISubState.WALK_PATH);
			startRouteWalking(npcAI, owner, template);
		}
		else
		{
			return startRandomWalking(npcAI, owner);
		}
		
		return true;
	}
	
	/**
	 * Starts a random walking behavior for an NPC.<br>
	 * This method checks if movement is enabled and if the owner has a valid random walk count.<br>
	 * It sets the {@code WALK_RANDOM} state and picks the first destination.
	 * @param npcAI The AI instance to update.
	 * @param owner The NPC object associated with the AI.
	 * @return {@code true} if the walking behavior started successfully, otherwise {@code false}.
	 */
	private static boolean startRandomWalking(NpcAI2 npcAI, Npc owner)
	{
		if (!AIConfig.ACTIVE_NPC_MOVEMENT)
		{
			return false;
		}
		
		final int randomWalkNr = owner.getSpawn().getRandomWalk();
		if (randomWalkNr == 0)
		{
			return false;
		}
		
		if (npcAI.setSubStateIfNot(AISubState.WALK_RANDOM))
		{
			EmoteManager.emoteStartWalking(npcAI.getOwner());
			chooseNextRandomPoint(npcAI);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Starts the movement of an NPC along a specific path.<br>
	 * This method sets up the route from a {@code WalkerTemplate}.<br>
	 * It also triggers the start walking emote for the owner.
	 * @param npcAI The AI controller for the NPC.
	 * @param owner The actual NPC entity being moved.
	 * @param template The template containing the path steps.
	 */
	protected static void startRouteWalking(NpcAI2 npcAI, Npc owner, WalkerTemplate template)
	{
		if (!AIConfig.ACTIVE_NPC_MOVEMENT)
		{
			return;
		}
		
		final List<RouteStep> route = template.getRouteSteps();
		final int currentPoint = owner.getMoveController().getCurrentPoint();
		final RouteStep nextStep = findNextRoutStep(owner, route);
		owner.getMoveController().setCurrentRoute(route);
		owner.getMoveController().setRouteStep(nextStep, route.get(currentPoint));
		EmoteManager.emoteStartWalking(npcAI.getOwner());
		npcAI.getOwner().getMoveController().moveToNextPoint();
	}
	
	/**
	 * Determines the next {@link RouteStep} for an {@link Npc}.<br>
	 * It checks the current movement point of the owner.<br>
	 * If a point exists, it uses {@code List, int)}.<br>
	 * Otherwise, it finds the closest step using {@code List, RouteStep)}.
	 * @param owner The {@link Npc} currently moving along the route.
	 * @param route The list of {@link RouteStep} objects defining the path.
	 * @return The next {@link RouteStep} to move towards or {@code null}.
	 */
	protected static RouteStep findNextRoutStep(Npc owner, List<RouteStep> route)
	{
		final int currentPoint = owner.getMoveController().getCurrentPoint();
		RouteStep nextStep = null;
		if (currentPoint != 0)
		{
			nextStep = findNextRouteStepAfterPause(owner, route, currentPoint);
		}
		else
		{
			nextStep = findClosestRouteStep(owner, route, nextStep);
		}
		
		return nextStep;
	}
	
	/**
	 * Finds the nearest {@link RouteStep} from a list for a specific NPC.<br>
	 * It checks if the {@code owner} belongs to a walker group first.<br>
	 * If no group exists, it calculates the distance to every step in the route.
	 * @param owner The {@link Npc} who is moving along the path.
	 * @param route The list of {@link RouteStep} objects representing the full path.
	 * @param nextStep The current target step used as a reference point.
	 * @return The closest {@link RouteStep} found in the provided list.
	 */
	protected static RouteStep findClosestRouteStep(Npc owner, List<RouteStep> route, RouteStep nextStep)
	{
		double closestDist = 0;
		final float x = owner.getX();
		final float y = owner.getY();
		final float z = owner.getZ();
		
		if (owner.getWalkerGroup() != null)
		{
			// always choose the 1st step, not the last which is close enough
			if (owner.getWalkerGroup().getGroupStep() < 2)
			{
				nextStep = route.get(0);
			}
			else
			{
				nextStep = route.get(owner.getWalkerGroup().getGroupStep() - 1);
			}
		}
		else
		{
			for (RouteStep step : route)
			{
				final double stepDist = MathUtil.getDistance(x, y, z, step.getX(), step.getY(), step.getZ());
				if ((closestDist == 0) || (stepDist < closestDist))
				{
					closestDist = stepDist;
					nextStep = step;
				}
			}
		}
		
		return nextStep;
	}
	
	/**
	 * Finds the next {@link RouteStep} for an {@link Npc} after a pause.<br>
	 * It checks if the current point is close enough to move to the next one.
	 * @param owner The {@link Npc} currently moving along the route.
	 * @param route The list of {@link RouteStep} objects defining the path.
	 * @param currentPoint The index of the current step in the {@code route}.
	 * @return The next {@link RouteStep} to follow.
	 */
	protected static RouteStep findNextRouteStepAfterPause(Npc owner, List<RouteStep> route, int currentPoint)
	{
		RouteStep nextStep = route.get(currentPoint);
		final double stepDist = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), nextStep.getX(), nextStep.getY(), nextStep.getZ());
		if (stepDist < 1)
		{
			nextStep = nextStep.getNextStep();
		}
		
		return nextStep;
	}
	
	/**
	 * Checks if the NPC is currently in a walking state.<br>
	 * It verifies if movement is supported and if the NPC has routes or is attackable.
	 * @param npcAI The {@link NpcAI2} instance to check.
	 * @return {@code true} if the NPC is walking, {@code false} otherwise.
	 */
	public static boolean isWalking(NpcAI2 npcAI)
	{
		return npcAI.isMoveSupported() && (hasWalkRoutes(npcAI) || npcAI.getOwner().isAttackableNpc());
	}
	
	/**
	 * Checks if the NPC has any defined walking routes.<br>
	 * This method retrieves the route status from the {@link Npc} owner.
	 * @param npcAI The {@code NpcAI2} instance to check.
	 * @return {@code true} if routes exist, otherwise {@code false}.
	 */
	public static boolean hasWalkRoutes(NpcAI2 npcAI)
	{
		return npcAI.getOwner().hasWalkRoutes();
	}
	
	/**
	 * Handles the logic when an {@link NpcAI2} reaches its current destination.<br>
	 * This method updates the NPC state based on whether it is following a path or moving randomly.<br>
	 * It also notifies the walker group if one exists.
	 * @param npcAI The {@code NpcAI2} instance to update.
	 */
	public static void targetReached(NpcAI2 npcAI)
	{
		if (npcAI.isInState(AIState.WALKING))
		{
			switch (npcAI.getSubState())
			{
				case WALK_PATH:
					npcAI.getOwner().updateKnownlist();
					if (npcAI.getOwner().getWalkerGroup() != null)
					{
						npcAI.getOwner().getWalkerGroup().targetReached(npcAI);
					}
					else
					{
						chooseNextRouteStep(npcAI);
					}
					break;
				case WALK_WAIT_GROUP:
					npcAI.setSubStateIfNot(AISubState.WALK_PATH);
					chooseNextRouteStep(npcAI);
					break;
				case WALK_RANDOM:
					chooseNextRandomPoint(npcAI);
					break;
				case TALK:
					npcAI.getOwner().getMoveController().abortMove();
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Determines and executes the next movement step for an NPC.<br>
	 * This method checks if the NPC should move immediately or after a pause.<br>
	 * It interacts with the {@code MoveController} to update the pathing.
	 * @param npcAI The {@link NpcAI2} instance to update.
	 */
	protected static void chooseNextRouteStep(NpcAI2 npcAI)
	{
		final int walkPause = npcAI.getOwner().getMoveController().getWalkPause();
		if (walkPause == 0)
		{
			npcAI.getOwner().getMoveController().resetMove();
			npcAI.getOwner().getMoveController().chooseNextStep();
			npcAI.getOwner().getMoveController().moveToNextPoint();
		}
		else
		{
			npcAI.getOwner().getMoveController().abortMove();
			npcAI.getOwner().getMoveController().chooseNextStep();
			ThreadPoolManager.getInstance().schedule(() ->
			{
				if (npcAI.isInState(AIState.WALKING))
				{
					npcAI.getOwner().getMoveController().moveToNextPoint();
				}
			}, walkPause);
		}
	}
	
	/**
	 * Selects a new random destination for an NPC to walk towards.<br>
	 * This method calculates a target point based on the spawn location and allowed range.<br>
	 * It schedules a move command using {@link ThreadPoolManager}.
	 * @param npcAI The {@code NpcAI2} instance of the NPC being moved.
	 */
	private static void chooseNextRandomPoint(NpcAI2 npcAI)
	{
		final Npc owner = npcAI.getOwner();
		owner.getMoveController().abortMove();
		final int randomWalkNr = owner.getSpawn().getRandomWalk();
		final int walkRange = Math.max(randomWalkNr, WALK_RANDOM_RANGE);
		
		final float distToSpawn = (float) owner.getDistanceToSpawnLocation();
		
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (npcAI.isInState(AIState.WALKING))
			{
				if (distToSpawn > walkRange)
				{
					owner.getMoveController().moveToPoint(owner.getSpawn().getX(), owner.getSpawn().getY(), owner.getSpawn().getZ());
				}
				else
				{
					final int nextX = Rnd.nextInt(walkRange * 2) - walkRange;
					final int nextY = Rnd.nextInt(walkRange * 2) - walkRange;
					if (GeoDataConfig.GEO_ENABLE && GeoDataConfig.GEO_NPC_MOVE)
					{
						final byte flags = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId() | CollisionIntention.WALK.getId());
						final Vector3f loc = GeoService.getInstance().getClosestCollision(owner, owner.getX() + nextX, owner.getY() + nextY, owner.getZ(), true, flags);
						owner.getMoveController().moveToPoint(loc.x, loc.y, loc.z);
					}
					else
					{
						owner.getMoveController().moveToPoint(owner.getX() + nextX, owner.getY() + nextY, owner.getZ());
					}
				}
			}
		}, Rnd.get(AIConfig.MINIMIMUM_DELAY, AIConfig.MAXIMUM_DELAY) * 1000);
	}
	
	/**
	 * Stops the movement of a specific NPC.<br>
	 * This method cancels any active move commands and resets the AI state to {@code IDLE}.<br>
	 * It also clears any sub-states and triggers the stop walking emote.
	 * @param npcAI The {@link NpcAI2} instance to stop.
	 */
	public static void stopWalking(NpcAI2 npcAI)
	{
		npcAI.getOwner().getMoveController().abortMove();
		npcAI.setStateIfNot(AIState.IDLE);
		npcAI.setSubStateIfNot(AISubState.NONE);
		EmoteManager.emoteStopWalking(npcAI.getOwner());
	}
	
	/**
	 * Checks if the NPC has reached its current destination.<br>
	 * This method queries the {@code MoveController} of the owner.
	 * @param npcAI The {@link NpcAI2} instance to check.
	 * @return {@code true} if the point is reached, {@code false} otherwise.
	 */
	public static boolean isArrivedAtPoint(NpcAI2 npcAI)
	{
		return npcAI.getOwner().getMoveController().isReachedPoint();
	}
}
