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
package com.aionemu.gameserver.controllers.movement;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.handler.TargetEventHandler;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.walker.RouteStep;
import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.spawnengine.WalkerGroup;
import com.aionemu.gameserver.taskmanager.tasks.MoveTaskManager;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.collections.LastUsedCache;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * Handles the movement logic specifically for {@link Npc} entities.<br>
 * It manages how non-player characters navigate and move through the game world.
 * @author ATracer
 */
public class NpcMoveController extends CreatureMoveController<Npc>
{
	private static final Logger log = LoggerFactory.getLogger(NpcMoveController.class);
	public static final float MOVE_CHECK_OFFSET = 0.1f;
	private static final float MOVE_OFFSET = 0.05f;
	private Destination destination = Destination.TARGET_OBJECT;
	private float pointX;
	private float pointY;
	private float pointZ;
	private LastUsedCache<Byte, Point3D> lastSteps = null;
	private byte stepSequenceNr = 0;
	private float offset = 0.1f;
	
	// walk related
	List<RouteStep> currentRoute;
	int currentPoint;
	int walkPause;
	private float cachedTargetZ;
	
	/**
	 * Creates a new instance of {@code NpcMoveController}.<br>
	 * This constructor initializes the controller for a specific {@link Npc}.<br>
	 * It passes the owner to the parent class.
	 * @param owner The {@link Npc} that this controller will manage.
	 */
	public NpcMoveController(Npc owner)
	{
		super(owner);
	}
	
	private static enum Destination
	{
		TARGET_OBJECT,
		POINT;
	}
	
	/**
	 * Starts the movement process toward a specific target object.<br>
	 * This method sets the destination to {@code Destination.TARGET_OBJECT}.<br>
	 * It also registers the owner with the {@link MoveTaskManager}.
	 */
	public void moveToTargetObject()
	{
		if (started.compareAndSet(false, true))
		{
			if (owner.getAi2().isLogging())
			{
				AI2Logger.moveinfo(owner, "MC: moveToTarget started");
			}
			
			destination = Destination.TARGET_OBJECT;
			updateLastMove();
			MoveTaskManager.getInstance().addCreature(owner);
		}
	}
	
	/**
	 * Moves the NPC to a specific set of coordinates.<br>
	 * This method updates the destination and starts the movement task.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 */
	public void moveToPoint(float x, float y, float z)
	{
		if (started.compareAndSet(false, true))
		{
			if (owner.getAi2().isLogging())
			{
				AI2Logger.moveinfo(owner, "MC: moveToPoint started");
			}
			
			destination = Destination.POINT;
			pointX = x;
			pointY = y;
			pointZ = z;
			updateLastMove();
			MoveTaskManager.getInstance().addCreature(owner);
		}
	}
	
	/**
	 * Moves the {@link Npc} to the next point in its current route.<br>
	 * This method initializes the movement task for the owner.<br>
	 * It updates the last move information and registers the creature with {@code MoveTaskManager}.
	 */
	public void moveToNextPoint()
	{
		if (started.compareAndSet(false, true))
		{
			if (owner.getAi2().isLogging())
			{
				AI2Logger.moveinfo(owner, "MC: moveToNextPoint started");
			}
			
			destination = Destination.POINT;
			updateLastMove();
			MoveTaskManager.getInstance().addCreature(owner);
		}
	}
	
	/**
	 * Starts the movement process for the {@code owner}.<br>
	 * This method updates the internal state to begin moving toward the target coordinates.<br>
	 * It triggers the logic required to transition the creature into a moving state.
	 */
	@Override
	public void moveToDestination()
	{
		if (owner.getAi2().isLogging())
		{
			AI2Logger.moveinfo(owner, "moveToDestination destination: " + destination);
		}
		
		if (CreatureActions.isAlreadyDead(owner))
		{
			abortMove();
			return;
		}
		
		if (!owner.canPerformMove() || (owner.getAi2().getSubState() == AISubState.CAST))
		{
			if (owner.getAi2().isLogging())
			{
				AI2Logger.moveinfo(owner, "moveToDestination can't perform move");
			}
			
			if (started.compareAndSet(true, false))
			{
				setAndSendStopMove(owner);
			}
			
			updateLastMove();
			return;
		}
		else if (started.compareAndSet(false, true))
		{
			movementMask = MovementMask.NPC_STARTMOVE;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
		
		if (!started.get())
		{
			if (owner.getAi2().isLogging())
			{
				AI2Logger.moveinfo(owner, "moveToDestination not started");
			}
		}
		
		switch (destination)
		{
			case TARGET_OBJECT:
				final Npc npc = owner;
				final VisibleObject target = owner.getTarget(); // todo no target
				if ((target == null) || !(target instanceof Creature))
				{
					return;
				}
				
				if (MathUtil.getDistance(target, pointX, pointY, pointZ) > MOVE_CHECK_OFFSET)
				{
					final Creature creature = (Creature) target;
					offset = npc.getController().getAttackDistanceToTarget();
					pointX = target.getX();
					pointY = target.getY();
					pointZ = getTargetZ(npc, creature);
				}
				
				moveToLocation(pointX, pointY, pointZ, offset);
				break;
			case POINT:
				offset = 0.1f;
				moveToLocation(pointX, pointY, pointZ, offset);
				break;
		}
		
		updateLastMove();
	}
	
	/**
	 * Calculates the target Z coordinate for an {@link Npc}.<br>
	 * It determines if a specific height should be used based on flying states.<br>
	 * This method handles special cases for NPCs moving toward flying creatures.
	 * @param npc The {@link Npc} entity performing the movement.
	 * @param creature The {@link Creature} target being moved towards.
	 * @return The calculated Z coordinate as a {@code float}.
	 */
	private float getTargetZ(Npc npc, Creature creature)
	{
		float targetZ = creature.getZ();
		if (GeoDataConfig.GEO_NPC_MOVE && creature.isInFlyingState() && !npc.isInFlyingState())
		{
			if (npc.getGameStats().checkGeoNeedUpdate())
			{
				cachedTargetZ = GeoService.getInstance().getZ(creature);
			}
			
			targetZ = cachedTargetZ;
		}
		
		return targetZ;
	}
	
	/**
	 * Updates the NPC position based on a target coordinate and calculates the next movement step.<br>
	 * This method handles heading updates, distance calculations, and geometry adjustments.<br>
	 * It also broadcasts a {@code SM_MOVE} packet if the movement mask changes.
	 * @param targetX The destination X coordinate.
	 * @param targetY The destination Y coordinate.
	 * @param targetZ The destination Z coordinate.
	 * @param offset A small value used for movement calculations.
	 */
	protected void moveToLocation(float targetX, float targetY, float targetZ, float offset)
	{
		boolean directionChanged = false;
		final float ownerX = owner.getX();
		final float ownerY = owner.getY();
		final float ownerZ = owner.getZ();
		
		directionChanged = (targetX != targetDestX) || (targetY != targetDestY) || (targetZ != targetDestZ);
		
		if (directionChanged)
		{
			heading = (byte) (Math.toDegrees(Math.atan2(targetY - ownerY, targetX - ownerX)) / 3);
		}
		
		if (owner.getAi2().isLogging())
		{
			AI2Logger.moveinfo(owner, "OLD targetDestX: " + targetDestX + " targetDestY: " + targetDestY + " targetDestZ " + targetDestZ);
		}
		
		// to prevent broken walkers in case of activating/deactivating zones
		if ((targetX == 0) && (targetY == 0))
		{
			targetX = owner.getSpawn().getX();
			targetY = owner.getSpawn().getY();
			targetZ = owner.getSpawn().getZ();
		}
		
		targetDestX = targetX;
		targetDestY = targetY;
		targetDestZ = targetZ;
		
		if (owner.getAi2().isLogging())
		{
			AI2Logger.moveinfo(owner, "ownerX=" + ownerX + " ownerY=" + ownerY + " ownerZ=" + ownerZ);
			AI2Logger.moveinfo(owner, "targetDestX: " + targetDestX + " targetDestY: " + targetDestY + " targetDestZ " + targetDestZ);
		}
		
		final float currentSpeed = owner.getGameStats().getMovementSpeedFloat();
		float futureDistPassed = (currentSpeed * (System.currentTimeMillis() - lastMoveUpdate)) / 1000f;
		final float dist = (float) MathUtil.getDistance(ownerX, ownerY, ownerZ, targetX, targetY, targetZ);
		
		if (owner.getAi2().isLogging())
		{
			AI2Logger.moveinfo(owner, "futureDist: " + futureDistPassed + " dist: " + dist);
		}
		
		if (dist == 0)
		{
			if (owner.getAi2().getState() == AIState.RETURNING)
			{
				if (owner.getAi2().isLogging())
				{
					AI2Logger.moveinfo(owner, "State RETURNING: abort move");
				}
				
				TargetEventHandler.onTargetReached((NpcAI2) owner.getAi2());
			}
			return;
		}
		
		if (futureDistPassed > dist)
		{
			futureDistPassed = dist;
		}
		
		final float distFraction = futureDistPassed / dist;
		final float newX = ((targetDestX - ownerX) * distFraction) + ownerX;
		final float newY = ((targetDestY - ownerY) * distFraction) + ownerY;
		float newZ = ((targetDestZ - ownerZ) * distFraction) + ownerZ;
		if ((ownerX == newX) && (ownerY == newY) && (owner.getSpawn().getRandomWalk() > 0))
		{
			return;
		}
		
		if (GeoDataConfig.GEO_NPC_MOVE && GeoDataConfig.GEO_ENABLE && (owner.getAi2().getSubState() != AISubState.WALK_PATH) && (owner.getAi2().getState() != AIState.RETURNING) && (owner.getGameStats().getLastGeoZUpdate() < System.currentTimeMillis()))
		{
			// fix Z if npc doesn't move to spawn point
			if ((owner.getSpawn().getX() != targetDestX) || (owner.getSpawn().getY() != targetDestY) || (owner.getSpawn().getZ() != targetDestZ))
			{
				final float geoZ = GeoService.getInstance().getZ(owner.getWorldId(), newX, newY, newZ, 0, owner.getInstanceId());
				if (Math.abs(newZ - geoZ) > 1)
				{
					directionChanged = true;
				}
				
				// add altitude
				newZ = (geoZ + owner.getObjectTemplate().getBoundRadius().getUpper()) - owner.getObjectTemplate().getHeight();
			}
			
			owner.getGameStats().setLastGeoZUpdate(System.currentTimeMillis() + 1000);
		}
		
		if (owner.getAi2().isLogging())
		{
			AI2Logger.moveinfo(owner, "newX=" + newX + " newY=" + newY + " newZ=" + newZ + " mask=" + movementMask);
		}
		
		World.getInstance().updatePosition(owner, newX, newY, newZ, heading, false);
		
		final byte newMask = getMoveMask(directionChanged);
		if (movementMask != newMask)
		{
			if (owner.getAi2().isLogging())
			{
				AI2Logger.moveinfo(owner, "oldMask=" + movementMask + " newMask=" + newMask);
			}
			
			movementMask = newMask;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
	}
	
	/**
	 * Determines the movement mask for an NPC based on its current state.<br>
	 * This method checks if the direction has changed or evaluates the AI state.<br>
	 * It also considers weapon status and flight capabilities to set the final mask.
	 * @param directionChanged A boolean indicating if the NPC is currently changing direction.
	 * @return The calculated movement mask as a {@code byte}.
	 */
	private byte getMoveMask(boolean directionChanged)
	{
		if (directionChanged)
		{
			return MovementMask.NPC_STARTMOVE;
		}
		else if (owner.getAi2().getState() == AIState.RETURNING)
		{
			return MovementMask.NPC_RUN_FAST;
		}
		else if (owner.getAi2().getState() == AIState.FOLLOWING)
		{
			return MovementMask.NPC_WALK_SLOW;
		}
		
		byte mask = MovementMask.IMMEDIATE;
		final Stat2 stat = owner.getGameStats().getMovementSpeed();
		if (owner.isInState(CreatureState.WEAPON_EQUIPPED))
		{
			mask = stat.getBonus() < 0 ? MovementMask.NPC_RUN_FAST : MovementMask.NPC_RUN_SLOW;
		}
		else if (owner.isInState(CreatureState.WALKING) || owner.isInState(CreatureState.ACTIVE))
		{
			mask = stat.getBonus() < 0 ? MovementMask.NPC_WALK_FAST : MovementMask.NPC_WALK_SLOW;
		}
		
		if (owner.isFlying())
		{
			mask |= MovementMask.GLIDE;
		}
		
		return mask;
	}
	
	/**
	 * Stops the current movement of the creature.<br>
	 * This method cancels any active move request.<br>
	 * It sets the {@code isInMove} flag to {@code false}.
	 */
	@Override
	public void abortMove()
	{
		if (!started.get())
		{
			return;
		}
		
		resetMove();
		setAndSendStopMove(owner);
	}
	
	/**
	 * Resets the current movement state of the NPC.<br>
	 * This method clears all target coordinates and sets the movement status to {@code false}.<br>
	 * It is used to stop an active move or clear previous pathing data.
	 */
	public void resetMove()
	{
		if (owner.getAi2().isLogging())
		{
			AI2Logger.moveinfo(owner, "MC perform stop");
		}
		
		started.set(false);
		targetDestX = 0;
		targetDestY = 0;
		targetDestZ = 0;
		pointX = 0;
		pointY = 0;
		pointZ = 0;
	}
	
	/**
	 * Updates the active route for the NPC.<br>
	 * This method sets the {@code currentRoute} list and resets the {@code currentPoint} to 0.<br>
	 * If the provided list is {@code null}, it clears the existing route.
	 * @param currentRoute The new list of {@link RouteStep} objects to follow.
	 */
	public void setCurrentRoute(List<RouteStep> currentRoute)
	{
		if (currentRoute == null)
		{
			AI2Logger.info(owner.getAi2(), String.format("MC: setCurrentRoute is setting route to null (NPC id: {})!!!", owner.getNpcId()));
		}
		else
		{
			this.currentRoute = currentRoute;
		}
		
		currentPoint = 0;
	}
	
	/**
	 * Updates the current movement destination for an NPC.<br>
	 * This method calculates the next point based on the provided steps.<br>
	 * It also handles group walking logic if applicable.
	 * @param step The new {@link RouteStep} to move towards.
	 * @param prevStep The previous {@link RouteStep} used for path calculation.
	 */
	public void setRouteStep(RouteStep step, RouteStep prevStep)
	{
		Point2D dest = null;
		if (owner.getWalkerGroup() != null)
		{
			dest = WalkerGroup.getLinePoint(new Point2D(prevStep.getX(), prevStep.getY()), new Point2D(step.getX(), step.getY()), owner.getWalkerGroupShift());
			pointZ = prevStep.getZ();
			if (GeoDataConfig.GEO_ENABLE && GeoDataConfig.GEO_NPC_MOVE)
			{
				// TODO: fix Z
			}
			
			owner.getWalkerGroup().setStep(owner, step.getRouteStep());
		}
		else
		{
			pointZ = step.getZ();
		}
		
		currentPoint = step.getRouteStep() - 1;
		pointX = dest == null ? step.getX() : dest.getX();
		pointY = dest == null ? step.getY() : dest.getY();
		destination = Destination.POINT;
		walkPause = step.getRestTime();
	}
	
	/**
	 * Retrieves the index of the current point in the movement route.<br>
	 * This value is used to track progress during navigation.
	 * @return The {@code int} index of the current step.
	 */
	public int getCurrentPoint()
	{
		return currentPoint;
	}
	
	/**
	 * Checks if the NPC has arrived at its current destination.<br>
	 * It compares the distance between the {@code owner} and the target coordinates.<br>
	 * The check returns {@code true} if the distance is less than {@code MOVE_OFFSET}.
	 * @return {@code true} if the point is reached, otherwise {@code false}.
	 */
	public boolean isReachedPoint()
	{
		return MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), pointX, pointY, pointZ) < MOVE_OFFSET;
	}
	
	/**
	 * Updates the NPC to its next movement point in the route.<br>
	 * This method increments the {@code currentPoint} index.<br>
	 * It resets the index to {@code 0} if the end of the route is reached.<br>
	 * It then calls {@code RouteStep)} to update the position.
	 */
	public void chooseNextStep()
	{
		final int oldPoint = currentPoint;
		if (currentRoute == null)
		{
			WalkManager.stopWalking((NpcAI2) owner.getAi2());
			log.warn("Bad Walker Id: " + owner.getNpcId() + " - point: " + oldPoint);
			return;
		}
		
		if (currentPoint < (currentRoute.size() - 1))
		{
			currentPoint++;
		}
		else
		{
			currentPoint = 0;
		}
		
		setRouteStep(currentRoute.get(currentPoint), currentRoute.get(oldPoint));
	}
	
	/**
	 * Retrieves the current pause value for walking.<br>
	 * This value is used to determine if an NPC should stop during movement.
	 * @return The current {@code int} value of the walk pause.
	 */
	public int getWalkPause()
	{
		return walkPause;
	}
	
	/**
	 * Checks if the NPC is currently in the process of changing its movement direction.<br>
	 * This method returns {@code true} if the {@code currentPoint} is equal to {@code 0}.
	 * @return {@code true} if the direction is changing, otherwise {@code false}.
	 */
	public boolean isChangingDirection()
	{
		return currentPoint == 0;
	}
	
	/**
	 * Retrieves the X coordinate of the destination.<br>
	 * This value is stored in the {@code targetDestX} field.
	 * @return The current X coordinate as a {@code float}.
	 */
	@Override
	public float getTargetX2()
	{
		return started.get() ? targetDestX : owner.getX();
	}
	
	/**
	 * Retrieves the Y coordinate of the destination.<br>
	 * This value is used to determine where the creature is moving.
	 * @return The {@code float} value of {@code targetDestY}.
	 */
	@Override
	public float getTargetY2()
	{
		return started.get() ? targetDestY : owner.getY();
	}
	
	/**
	 * Retrieves the Z-coordinate of the target destination.<br>
	 * This value is used to determine the height of the movement goal.
	 * @return The {@code float} value of {@code targetDestZ}.
	 */
	@Override
	public float getTargetZ2()
	{
		return started.get() ? targetDestZ : owner.getZ();
	}
	
	/**
	 * Checks if the NPC is currently moving toward a specific target object.<br>
	 * This method returns {@code true} if the current destination is set to {@code TARGET_OBJECT}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if following an object, {@code false} otherwise.
	 */
	public boolean isFollowingTarget()
	{
		return destination == Destination.TARGET_OBJECT;
	}
	
	/**
	 * Saves the current position of the {@code owner} as a back step.<br>
	 * This method updates the {@code lastSteps} cache for navigation purposes.<br>
	 * It only records a new step if the distance from the previous step is at least 10 units.
	 */
	public void storeStep()
	{
		if (owner.getAi2().getState() == AIState.RETURNING)
		{
			return;
		}
		
		if (lastSteps == null)
		{
			lastSteps = new LastUsedCache<>(10);
		}
		
		final Point3D currentStep = new Point3D(owner.getX(), owner.getY(), owner.getZ());
		if (owner.getAi2().isLogging())
		{
			AI2Logger.moveinfo(owner, "store back step: X=" + owner.getX() + " Y=" + owner.getY() + " Z=" + owner.getZ());
		}
		
		if ((stepSequenceNr == 0) || (MathUtil.getDistance(lastSteps.get(stepSequenceNr), currentStep) >= 10))
		{
			lastSteps.put(++stepSequenceNr, currentStep);
		}
	}
	
	/**
	 * Retrieves the previous movement coordinate from the history.<br>
	 * It decrements the step sequence to find a stored {@link Point3D}.<br>
	 * If no history exists, it defaults to the spawn location.
	 * @return The retrieved {@link Point3D} or null if at the start of the sequence.
	 */
	public Point3D recallPreviousStep()
	{
		if (lastSteps == null)
		{
			lastSteps = new LastUsedCache<>(10);
		}
		
		Point3D result = stepSequenceNr == 0 ? null : lastSteps.get(stepSequenceNr--);
		
		if (result == null)
		{
			if (owner.getAi2().isLogging())
			{
				AI2Logger.moveinfo(owner, "recall back step: spawn point");
			}
			
			targetDestX = owner.getSpawn().getX();
			targetDestY = owner.getSpawn().getY();
			targetDestZ = owner.getSpawn().getZ();
			result = new Point3D(targetDestX, targetDestY, targetDestZ);
		}
		else
		{
			if (owner.getAi2().isLogging())
			{
				AI2Logger.moveinfo(owner, "recall back step: X=" + result.getX() + " Y=" + result.getY() + " Z=" + result.getZ());
			}
			
			targetDestX = result.getX();
			targetDestY = result.getY();
			targetDestZ = result.getZ();
		}
		
		return result;
	}
	
	/**
	 * Resets the movement history for this NPC.<br>
	 * This method clears the {@code lastSteps} list and resets the {@code stepSequenceNr}.<br>
	 * It also sets the {@code movementMask} to {@code MovementMask.IMMEDIATE}.
	 */
	public void clearBackSteps()
	{
		stepSequenceNr = 0;
		lastSteps = null;
		movementMask = MovementMask.IMMEDIATE;
	}
	
	/**
	 * Updates the movement behavior of a {@code Minion}.<br>
	 * This method sets the {@code movementMask} to {@code MovementMask.IMMEDIATE}.
	 */
	@Override
	public void skillMovement()
	{
		movementMask = MovementMask.IMMEDIATE;
	}
}
