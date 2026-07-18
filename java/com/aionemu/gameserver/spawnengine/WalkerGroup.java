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
package com.aionemu.gameserver.spawnengine;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.zone.Point2D;

/**
 * Manages a collection of {@link Npc} entities that move together as a group.<br>
 * It coordinates the walking behavior and synchronization for multiple NPCs simultaneously.
 * @author vlog
 * @modified Rolandas
 */
public class WalkerGroup
{
	private static final Logger log = LoggerFactory.getLogger(WalkerGroup.class);
	private final List<ClusteredNpc> members;
	private final WalkerGroupType type;
	private final float walkerXpos;
	private final float walkerYpos;
	private final int[] memberSteps;
	private volatile int groupStep;
	private final String versionId;
	private boolean isSpawned;
	
	/**
	 * Creates a new {@link WalkerGroup} from a list of NPCs.<br>
	 * This constructor sorts the members by their walker index.<br>
	 * It initializes the group's position and walk type based on the first member.
	 * @param members The list of {@link ClusteredNpc} objects to include in this group.
	 */
	public WalkerGroup(List<ClusteredNpc> members)
	{
		this.members = members.stream().sorted(Comparator.comparingInt(ClusteredNpc::getWalkerIndex)).collect(Collectors.toList());
		memberSteps = new int[members.size()];
		walkerXpos = members.get(0).getX();
		walkerYpos = members.get(0).getY();
		type = members.get(0).getWalkTemplate().getType();
		versionId = members.get(0).getWalkTemplate().getVersionId();
	}
	
	/**
	 * Arranges the group members into a specific formation.<br>
	 * This method calculates positions based on the {@code WalkerGroupType}.<br>
	 * It handles {@code SQUARE} formations by calculating row and sagittal distances.<br>
	 * Each member is assigned a position and a {@link WalkerGroupShift}.
	 */
	public void form()
	{
		if (getWalkType() == WalkerGroupType.SQUARE)
		{
			final int[] rows = members.get(0).getWalkTemplate().getRows();
			if (IntStream.of(rows).sum() != members.size())
			{
				log.warn("Invalid row sizes for walk cluster " + members.get(0).getWalkTemplate().getRouteId());
			}
			
			if (rows.length == 1)
			{
				// Line formation: distance 2 meters from each other (divide by 2 and multiple by 2)
				// negative at left hand and positive at the right hand
				final float bounds = (float) members.stream().mapToDouble(m -> m.getNpc().getObjectTemplate().getBoundRadius().getSide()).sum();
				float distance = ((1 - members.size()) / 2f) * (WalkerGroupShift.DISTANCE + bounds);
				final Point2D origin = new Point2D(walkerXpos, walkerYpos);
				final Point2D destination = new Point2D(members.get(0).getWalkTemplate().getRouteStep(2).getX(), members.get(0).getWalkTemplate().getRouteStep(2).getY());
				for (int i = 0; i < members.size(); i++, distance += WalkerGroupShift.DISTANCE)
				{
					final WalkerGroupShift shift = new WalkerGroupShift(distance, 0);
					final Point2D loc = getLinePoint(origin, destination, shift);
					members.get(i).setX(loc.getX());
					members.get(i).setY(loc.getY());
					final Npc member = members.get(i).getNpc();
					member.setWalkerGroup(this);
					member.setWalkerGroupShift(shift);
					// distance += npc.getObjectTemplate().getBoundRadius().getSide();
				}
			}
			else if (rows.length != 0)
			{
				final float rowDistances[] = new float[rows.length - 1];
				float coronalDist = 0;
				for (int i = 0; i < (rows.length - 1); i++)
				{
					if ((rows[i] % 2) != (rows[i + 1] % 2))
					{
						rowDistances[i] = 0.86602540378443864676372317075294f * WalkerGroupShift.DISTANCE;
					}
					else
					{
						rowDistances[i] = WalkerGroupShift.DISTANCE;
					}
					
					coronalDist -= rowDistances[i];
				}
				
				final Point2D origin = new Point2D(walkerXpos, walkerYpos);
				final Point2D destination = new Point2D(members.get(0).getWalkTemplate().getRouteStep(2).getX(), members.get(0).getWalkTemplate().getRouteStep(2).getY());
				int index = 0;
				for (int i = 0; i < rows.length; i++)
				{
					float sagittalDist = ((1 - rows[i]) / 2f) * WalkerGroupShift.DISTANCE;
					for (int j = 0; j < rows[i]; j++, sagittalDist += WalkerGroupShift.DISTANCE)
					{
						if (index > (members.size() - 1))
						{
							break;
						}
						
						final WalkerGroupShift shift = new WalkerGroupShift(sagittalDist, coronalDist);
						final Point2D loc = getLinePoint(origin, destination, shift);
						final ClusteredNpc cnpc = members.get(index++);
						cnpc.setX(loc.getX());
						cnpc.setY(loc.getY());
						cnpc.getNpc().setWalkerGroup(this);
						cnpc.getNpc().setWalkerGroupShift(shift);
					}
					
					if (i < (rows.length - 1))
					{
						coronalDist += rowDistances[i];
					}
				}
			}
		}
		else if (getWalkType() == WalkerGroupType.CIRCLE)
		{
			// TODO: if needed
		}
		else if (getWalkType() == WalkerGroupType.POINT)
		{
			log.warn("No formation specified for walk cluster " + members.get(0).getWalkTemplate().getRouteId());
		}
	}
	
	/**
	 * Calculates the extra side value for a specific range of rows.<br>
	 * This method currently returns a default value of {@code 0}.
	 * @param rows The array of row data to process.
	 * @param startIndex The starting index within the {@code rows} array.
	 * @param endIndex The ending index within the {@code rows} array.
	 * @return The calculated extra side value as a {@code float}.
	 */
	@SuppressWarnings("unused")
	private float getSidesExtra(int[] rows, int startIndex, int endIndex)
	{
		return 0;
	}
	
	/**
	 * Calculates a new position based on an origin and destination point.<br>
	 * This method applies a specific shift to determine the resulting {@code Point2D}.<br>
	 * It handles horizontal, vertical, and diagonal movements using {@link WalkerGroupShift}.
	 * @param origin The starting {@code Point2D} coordinate.
	 * @param destination The target {@code Point2D} coordinate.
	 * @param shift The {@code WalkerGroupShift} values to apply to the calculation.
	 * @return A new {@code Point2D} representing the shifted position.
	 */
	public static Point2D getLinePoint(Point2D origin, Point2D destination, WalkerGroupShift shift)
	{
		// TODO: implement angle shift
		final WalkerGroupShift dir = getShiftSigns(origin, destination);
		Point2D result = null;
		if ((origin.getY() - destination.getY()) == 0)
		{
			return new Point2D(origin.getX() + (dir.getCoronalShift() * shift.getCoronalShift()), origin.getY() - (dir.getSagittalShift() * shift.getSagittalShift()));
		}
		else if ((origin.getX() - destination.getX()) == 0)
		{
			return new Point2D(origin.getX() + (dir.getCoronalShift() * shift.getSagittalShift()), origin.getY() + (dir.getCoronalShift() * shift.getCoronalShift()));
		}
		else
		{
			final double slope = (origin.getX() - destination.getX()) / (origin.getY() - destination.getY());
			final double dx = Math.abs(shift.getSagittalShift()) / Math.sqrt(1 + (slope * slope));
			if ((shift.getSagittalShift() * dir.getCoronalShift()) < 0)
			{
				result = new Point2D((float) (origin.getX() - dx), (float) (origin.getY() + (dx * slope)));
			}
			else
			{
				result = new Point2D((float) (origin.getX() + dx), (float) (origin.getY() - (dx * slope)));
			}
		}
		
		if (shift.getCoronalShift() != 0)
		{
			Point2D rotatedShift = null;
			if (shift.getSagittalShift() != 0)
			{
				rotatedShift = getLinePoint(origin, destination, new WalkerGroupShift(Math.signum(shift.getSagittalShift()) * Math.abs(shift.getCoronalShift()), 0));
			}
			else
			{
				rotatedShift = getLinePoint(origin, destination, new WalkerGroupShift(Math.abs(shift.getCoronalShift()), 0));
			}
			
			// since it's rotated, and perpendicular, dx and dy are reciprocal when not rotated
			final float dx = Math.abs(origin.getX() - rotatedShift.getX());
			final float dy = Math.abs(origin.getY() - rotatedShift.getY());
			if (shift.getCoronalShift() < 0)
			{
				if ((dir.getSagittalShift() < 0) && (dir.getCoronalShift() < 0))
				{
					result = new Point2D(result.getX() + dy, result.getY() + dx);
				}
				else if ((dir.getSagittalShift() > 0) && (dir.getCoronalShift() > 0))
				{
					result = new Point2D(result.getX() - dy, result.getY() - dx);
				}
				else if ((dir.getSagittalShift() < 0) && (dir.getCoronalShift() > 0))
				{
					result = new Point2D(result.getX() + dy, result.getY() - dx);
				}
				else if ((dir.getSagittalShift() > 0) && (dir.getCoronalShift() < 0))
				{
					result = new Point2D(result.getX() - dy, result.getY() + dx);
				}
			}
			else
			{
				if ((dir.getSagittalShift() < 0) && (dir.getCoronalShift() < 0))
				{
					result = new Point2D(result.getX() - dy, result.getY() - dx);
				}
				else if ((dir.getSagittalShift() > 0) && (dir.getCoronalShift() > 0))
				{
					result = new Point2D(result.getX() + dy, result.getY() + dx);
				}
				else if ((dir.getSagittalShift() < 0) && (dir.getCoronalShift() > 0))
				{
					result = new Point2D(result.getX() - dy, result.getY() + dx);
				}
				else if ((dir.getSagittalShift() > 0) && (dir.getCoronalShift() < 0))
				{
					result = new Point2D(result.getX() + dy, result.getY() - dx);
				}
			}
		}
		
		return result;
	}
	
	/*
	 * Return a normalized direction vector
	 */
	/**
	 * Calculates the direction of movement between two points.<br>
	 * It determines the horizontal and vertical signs based on the difference in coordinates.
	 * @param origin The starting {@code Point2D}.
	 * @param destination The target {@code Point2D}.
	 * @return A new {@link WalkerGroupShift} containing the direction signs.
	 */
	private static WalkerGroupShift getShiftSigns(Point2D origin, Point2D destination)
	{
		final float dx = Math.signum(destination.getX() - origin.getX());
		final float dy = Math.signum(destination.getY() - origin.getY());
		return new WalkerGroupShift(dx, dy);
	}
	
	/**
	 * Updates the movement step for a specific {@link Npc} within this group.<br>
	 * This method also updates the overall {@code groupStep} if the new value is higher.
	 * @param member The {@link Npc} object to update.
	 * @param step The new integer step value to assign.
	 */
	public void setStep(Npc member, int step)
	{
		int currentStep = 0;
		for (int i = 0; i < members.size(); i++)
		{
			if (memberSteps[i] > currentStep)
			{
				currentStep = memberSteps[i];
			}
			
			if (members.get(i).getNpc().equals(member))
			{
				AI2Logger.info(members.get(i).getNpc().getAi2(), "Setting step to " + step);
				memberSteps[i] = step;
			}
		}
		
		if ((step > currentStep) || (step == 1))
		{
			groupStep = step;
		}
	}
	
	/**
	 * Updates the state of NPCs when a target destination is reached.<br>
	 * This method checks if all members in the group have arrived at their positions.<br>
	 * It handles movement transitions and notifies the {@link WalkManager}.
	 * @param npcAI The {@code NpcAI2} instance to update.
	 */
	public void targetReached(NpcAI2 npcAI)
	{
		synchronized (members)
		{
			npcAI.setSubStateIfNot(AISubState.WALK_WAIT_GROUP);
			boolean allArrived = true;
			for (ClusteredNpc snpc : members)
			{
				allArrived &= snpc.getNpc().getAi2().getSubState() == AISubState.WALK_WAIT_GROUP;
				if (!allArrived)
				{
					break;
				}
			}
			
			for (int i = 0; i < members.size(); i++)
			{
				final ClusteredNpc snpc = members.get(i);
				if ((memberSteps[i] == groupStep) && !allArrived)
				{
					npcAI.getOwner().getMoveController().abortMove();
					npcAI.setStateIfNot(AIState.WALKING);
					npcAI.setSubStateIfNot(AISubState.WALK_WAIT_GROUP);
					continue;
				}
				
				npcAI = (NpcAI2) (snpc.getNpc().getAi2());
				if (npcAI.getSubState() == AISubState.WALK_WAIT_GROUP)
				{
					WalkManager.targetReached(npcAI);
				}
			}
		}
	}
	
	/**
	 * Checks if the current position of this object is a valid spawn point.<br>
	 * This method delegates the check to the {@code isSpawned} method.
	 * @return {@code true} if the position is spawned, {@code false} otherwise.
	 */
	public boolean isSpawned()
	{
		return isSpawned;
	}
	
	/**
	 * Spawns all members of the group into the game world.<br>
	 * This method calculates the height for each {@link ClusteredNpc} and calls its spawn method.<br>
	 * It sets the {@code isSpawned} flag to {@code true} after completion.
	 */
	public void spawn()
	{
		for (ClusteredNpc snpc : members)
		{
			final float height = getHeight(snpc.getX(), snpc.getY(), snpc.getNpc().getSpawn());
			snpc.spawn(height);
		}
		
		isSpawned = true;
	}
	
	/**
	 * Updates a member of the group with a new {@link Npc} instance.<br>
	 * This method replaces an old NPC in the internal list with the provided one.<br>
	 * It resets the step count for that specific member to {@code 1}.
	 * @param npc The new {@link Npc} object to assign to the group.
	 */
	public void respawn(Npc npc)
	{
		for (int index = 0; index < members.size(); index++)
		{
			final ClusteredNpc snpc = members.get(index);
			if ((snpc.getWalkerIndex() == npc.getSpawn().getWalkerIndex()) && (snpc.getNpc().getNpcId() == npc.getNpcId()))
			{
				synchronized (members)
				{
					snpc.setNpc(npc);
					memberSteps[index] = 1;
				}
				break;
			}
		}
	}
	
	/**
	 * Removes all members of this group from the game world.<br>
	 * This method calls {@code despawn()} on every {@link ClusteredNpc} in the group.<br>
	 * It resets the group positions and sets {@code isSpawned} to {@code false}.
	 */
	public void despawn()
	{
		for (ClusteredNpc snpc : members)
		{
			snpc.despawn();
			
			// reset positions
			form();
			for (int index = 0; index < memberSteps.length; index++)
			{
				memberSteps[index] = 1;
			}
			
			groupStep = 1;
		}
		
		isSpawned = false;
	}
	
	/**
	 * Finds the {@link ClusteredNpc} data for a specific NPC.<br>
	 * It searches through all members of this group.<br>
	 * Returns {@code null} if the NPC is not found.
	 * @param npc The {@code Npc} object to search for.
	 * @return The matching {@code ClusteredNpc} or {@code null}.
	 */
	public ClusteredNpc getClusterData(Npc npc)
	{
		for (ClusteredNpc snpc : members)
		{
			if (snpc.getNpc().equals(npc))
			{
				return snpc;
			}
		}
		
		return null;
	}
	
	/**
	 * Calculates the height value for a specific location.<br>
	 * This method uses the {@code z} coordinate from the provided {@link SpawnTemplate}.
	 * @param x The horizontal position.
	 * @param y The vertical position.
	 * @param template The {@code SpawnTemplate} containing height data.
	 * @return The height value as a {@code float}.
	 */
	private float getHeight(float x, float y, SpawnTemplate template)
	{
		/*
		 * if (GeoService.getInstance().isGeoOn()) { return GeoService.getInstance().getZ(template.getWorldId(), x, y, z, ); }
		 */
		return template.getZ();
	}
	
	/**
	 * Retrieves the total number of members in this group.<br>
	 * This value represents the size of the {@code members} list.
	 * @return The integer count of NPCs in the pool.
	 */
	public int getPool()
	{
		return members.size();
	}
	
	/**
	 * Retrieves the movement type of this group.<br>
	 * This tells you how the members in the {@code WalkerGroup} should walk.
	 * @return The {@code WalkerGroupType} assigned to this group.
	 */
	public WalkerGroupType getWalkType()
	{
		return type;
	}
	
	/**
	 * Checks if an {@link Npc} is positioned in a linear row.<br>
	 * This only applies to groups of type {@code WalkerGroupType.SQUARE}.<br>
	 * It returns {@code true} if the NPC belongs to a template with exactly 1 row.
	 * @param npc The {@link Npc} to check.
	 * @return {@code true} if the NPC is linearly positioned, otherwise {@code false}.
	 */
	public boolean isLinearlyPositioned(Npc npc)
	{
		if (type != WalkerGroupType.SQUARE)
		{
			return false;
		}
		
		for (ClusteredNpc snpc : members)
		{
			if (snpc.getNpc().equals(npc))
			{
				return snpc.getWalkTemplate().getRows().length == 1;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the current step of the group.<br>
	 * This value tracks the progress of the {@link WalkerGroup}.
	 * @return The current integer step of the group.
	 */
	public int getGroupStep()
	{
		return groupStep;
	}
	
	/**
	 * Retrieves the unique identifier for this group's version.<br>
	 * This ID is used to distinguish between different configurations of the same group.
	 * @return The {@code String} representing the version ID.
	 */
	public String getVersionId()
	{
		return versionId;
	}
}
