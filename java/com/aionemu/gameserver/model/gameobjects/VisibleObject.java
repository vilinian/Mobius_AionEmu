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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.VisibleObjectController;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.WorldType;
import com.aionemu.gameserver.world.knownlist.KnownList;

/**
 * This class serves as the base class for all in-game objects that can be spawned at a specific position, such as players or NPCs.<br>
 * It manages visibility between entities by using a {@link KnownList} to track which objects are currently known by this instance.
 * @author -Nemesiss-
 */
public abstract class VisibleObject extends AionObject
{
	protected VisibleObjectTemplate objectTemplate;
	
	// how far player will see visible object
	public static final float VisibilityDistance = 95;
	
	// maxZvisibleDistance
	public static final float maxZvisibleDistance = 95;
	
	/**
	 * Creates a new instance of a {@link VisibleObject}.<br>
	 * This constructor initializes the basic properties required for an object in the game world.
	 * @param objId The unique identifier for this object.
	 * @param controller The {@link VisibleObjectController} that handles the logic for this object.
	 * @param spawnTemplate The template used to define how this object spawns.
	 * @param objectTemplate The template containing the base data for this object type.
	 * @param position The initial {@link WorldPosition} where the object will be placed.
	 */
	public VisibleObject(int objId, VisibleObjectController<? extends VisibleObject> controller, SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate, WorldPosition position)
	{
		super(objId);
		this.controller = controller;
		this.position = position;
		spawn = spawnTemplate;
		this.objectTemplate = objectTemplate;
	}
	
	/**
	 * Position of object in the world.
	 */
	protected WorldPosition position;
	/**
	 * KnownList of this VisibleObject.
	 */
	private KnownList knownlist;
	/**
	 * Controller of this VisibleObject
	 */
	private final VisibleObjectController<? extends VisibleObject> controller;
	/**
	 * Visible object's target
	 */
	private VisibleObject target;
	/**
	 * Spawn template of this visibleObject. .
	 */
	private SpawnTemplate spawn;
	
	/**
	 * Retrieves the current {@link MapRegion} of this object.<br>
	 * This method gets the position and then extracts the region from it.
	 * @return The {@code MapRegion} where the object is currently located.
	 */
	public MapRegion getActiveRegion()
	{
		return getPosition().getMapRegion();
	}
	
	/**
	 * Retrieves the unique identifier for this instance.<br>
	 * This ID was provided during the construction of {@link CollisionResults}.
	 * @return The {@code int} value representing the instance ID.
	 */
	public int getInstanceId()
	{
		return getPosition().getInstanceId();
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return getPosition().getMapId();
	}
	
	/**
	 * Retrieves the type of the world where this object is located.<br>
	 * It uses the {@code getWorldId()} to find the correct map.
	 * @return The {@link WorldType} of the current world.
	 */
	public WorldType getWorldType()
	{
		return World.getInstance().getWorldMap(getWorldId()).getWorldType();
	}
	
	/**
	 * Retrieves the X coordinate of the object.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return getPosition().getX();
	}
	
	/**
	 * Retrieves the vertical coordinate of the object.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return getPosition().getY();
	}
	
	/**
	 * Retrieves the vertical coordinate of the object.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return getPosition().getZ();
	}
	
	/**
	 * Updates the position and heading of this object.<br>
	 * This method calls {@code getPosition} to set new coordinates.
	 * @param x The new X coordinate.
	 * @param y The new Y coordinate.
	 * @param z The new Z coordinate.
	 * @param h The new heading value.
	 */
	public void setXYZH(Float x, Float y, Float z, Byte h)
	{
		getPosition().setXYZH(x, y, z, h);
	}
	
	/**
	 * Retrieves the current rotation direction of the object.<br>
	 * This value is stored as a {@code byte}.
	 * @return The heading value of the object.
	 */
	public byte getHeading()
	{
		return getPosition().getHeading();
	}
	
	/**
	 * Retrieves the current location of this visible object.
	 * @return the {@code WorldPosition} of the object.
	 */
	public WorldPosition getPosition()
	{
		return position;
	}
	
	/**
	 * Checks if the current position of this object is a valid spawn point.<br>
	 * This method delegates the check to the {@code isSpawned} method.
	 * @return {@code true} if the position is spawned, {@code false} otherwise.
	 */
	public boolean isSpawned()
	{
		return getPosition().isSpawned();
	}
	
	/**
	 * Checks if this object currently exists in the game world.<br>
	 * It verifies if the {@code World} instance contains an object with this unique ID.
	 * @return {@code true} if the object is found in the world, {@code false} otherwise.
	 */
	public boolean isInWorld()
	{
		return World.getInstance().findVisibleObject(getObjectId()) != null;
	}
	
	/**
	 * Checks if the object is currently located inside an instance.<br>
	 * This method returns {@code true} if the current position is part of an instance map.
	 * @return {@code true} if the object is in an instance, {@code false} otherwise.
	 */
	public boolean isInInstance()
	{
		return getPosition().isInstanceMap();
	}
	
	/**
	 * Clears all objects from the current {@link KnownList}.<br>
	 * This method resets the list of known entities for this object.
	 */
	public void clearKnownlist()
	{
		getKnownList().clear();
	}
	
	/**
	 * Updates the list of known objects for this entity.<br>
	 * This method calls {@code doUpdate} on the internal {@code KnownList}.
	 */
	public void updateKnownlist()
	{
		getKnownList().doUpdate();
	}
	
	/**
	 * Checks if the provided {@link Creature} is not {@code null}.
	 * @param creature The {@link Creature} to check.
	 * @return {@code true} if the {@code creature} is not {@code null}, {@code false} otherwise.
	 */
	public boolean canSee(Creature creature)
	{
		return creature != null;
	}
	
	/**
	 * Sets the {@link KnownList} for this visible object.<br>
	 * This list tracks which other objects are currently known by this instance.
	 * @param knownlist The new {@code KnownList} to assign.
	 */
	public void setKnownlist(KnownList knownlist)
	{
		this.knownlist = knownlist;
	}
	
	/**
	 * Retrieves the list of objects known by this {@link VisibleObject}.<br>
	 * This list tracks which entities are currently visible to this object.
	 * @return the current {@link KnownList} instance.
	 */
	public KnownList getKnownList()
	{
		return knownlist;
	}
	
	/**
	 * Retrieves the {@link VisibleObjectController} associated with this object.<br>
	 * The controller manages the logic for this specific visible object.
	 * @return the {@code VisibleObjectController} instance.
	 */
	public VisibleObjectController<? extends VisibleObject> getController()
	{
		return controller;
	}
	
	/**
	 * Retrieves the current target of this object.
	 * @return the {@link VisibleObject} that is currently being targeted, or {@code null} if there is no target.
	 */
	public VisibleObject getTarget()
	{
		return target;
	}
	
	/**
	 * Calculates the distance between this object and its current target.<br>
	 * It subtracts the collision radii of both objects from the total distance.<br>
	 * If there is no target, it returns {@code 0}.
	 * @return The calculated distance to the target as a {@code float}.
	 */
	public float getDistanceToTarget()
	{
		final VisibleObject currTarget = target;
		if (currTarget == null)
		{
			return 0;
		}
		
		return (float) MathUtil.getDistance(getX(), getY(), getZ(), currTarget.getX(), currTarget.getY(), currTarget.getZ()) - getObjectTemplate().getBoundRadius().getCollision() - currTarget.getObjectTemplate().getBoundRadius().getCollision();
	}
	
	/**
	 * Sets the target of this object.<br>
	 * The {@code creature} parameter defines which {@link VisibleObject} is now being targeted.
	 * @param creature The {@code VisibleObject} to set as the new target.
	 */
	public void setTarget(VisibleObject creature)
	{
		target = creature;
	}
	
	/**
	 * Checks if this object is currently targeting a specific ID.<br>
	 * It returns {@code true} if the target exists and matches the provided {@code objectId}.<br>
	 * Otherwise, it returns {@code false}.
	 * @param objectId The unique identifier of the object to check against.
	 * @return {@code true} if the current target matches the ID, {@code false} otherwise.
	 */
	public boolean isTargeting(int objectId)
	{
		return (target != null) && (target.getObjectId() == objectId);
	}
	
	/**
	 * Retrieves the {@code SpawnTemplate} associated with this object.<br>
	 * This template contains information about how the object was spawned in the world.
	 * @return The {@link SpawnTemplate} of this object.
	 */
	public SpawnTemplate getSpawn()
	{
		return spawn;
	}
	
	/**
	 * Sets the {@code spawn} template for this object.<br>
	 * This method updates the internal {@code spawn} field with the provided {@link SpawnTemplate}.
	 * @param spawn The {@link SpawnTemplate} to assign to this object.
	 */
	public void setSpawn(SpawnTemplate spawn)
	{
		this.spawn = spawn;
	}
	
	/**
	 * Retrieves the template associated with this visible object.<br>
	 * This {@link VisibleObjectTemplate} contains the base data for the object.
	 * @return The {@code VisibleObjectTemplate} of this object.
	 */
	public VisibleObjectTemplate getObjectTemplate()
	{
		return objectTemplate;
	}
	
	/**
	 * Sets the template for this visible object.<br>
	 * This method assigns a {@link VisibleObjectTemplate} to the internal field.
	 * @param objectTemplate The template to be used by this object.
	 */
	public void setObjectTemplate(VisibleObjectTemplate objectTemplate)
	{
		this.objectTemplate = objectTemplate;
	}
	
	/**
	 * Updates the current location of this object.<br>
	 * This method sets the {@code position} field to a new {@link WorldPosition}.
	 * @param position The new {@link WorldPosition} to assign to this object.
	 */
	public void setPosition(WorldPosition position)
	{
		this.position = position;
	}
	
	/**
	 * Gets the distance at which this object can be seen by others.<br>
	 * Certain NPC types like monsters or guards return {@code Integer.MAX_VALUE}.<br>
	 * Other objects use the default {@code VisibilityDistance}.
	 * @return The visibility distance as a {@code float}.
	 */
	public float getVisibilityDistance()
	{
		if (this instanceof Npc)
		{
			final NpcTemplate npcTemplate = (NpcTemplate) getObjectTemplate();
			if (npcTemplate.getNpcTemplateType().equals(NpcTemplateType.FLAG) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.GUARD) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.MONSTER) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.HOUSING) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.ABYSS_GUARD) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.RAID_MONSTER))
			{
				return Integer.MAX_VALUE;
			}
		}
		
		return VisibilityDistance;
	}
	
	/**
	 * Gets the maximum vertical distance for visibility.<br>
	 * This method checks if the object is a specific type of {@link Npc}.<br>
	 * If it is a flag, guard, monster, housing, abyss guard, or raid monster, it returns {@code Integer.MAX_VALUE}.<br>
	 * Otherwise, it returns the value of the {@code maxZvisibleDistance} constant.
	 * @return The maximum Z distance for visibility as a {@code float}.
	 */
	public float getMaxZVisibleDistance()
	{
		if (this instanceof Npc)
		{
			final NpcTemplate npcTemplate = (NpcTemplate) getObjectTemplate();
			if (npcTemplate.getNpcTemplateType().equals(NpcTemplateType.FLAG) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.GUARD) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.MONSTER) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.HOUSING) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.ABYSS_GUARD) || npcTemplate.getNpcTemplateType().equals(NpcTemplateType.RAID_MONSTER))
			{
				return Integer.MAX_VALUE;
			}
		}
		
		return maxZvisibleDistance;
	}
	
	/**
	 * Returns a string representation of this visible object.<br>
	 * If the {@code objectTemplate} is null, it returns the base class string.<br>
	 * Otherwise, it returns the name and ID from the template.
	 * @return A formatted string containing the object name and its template ID.
	 */
	@Override
	public String toString()
	{
		if (objectTemplate == null)
		{
			return super.toString();
		}
		
		return objectTemplate.getName() + " (" + objectTemplate.getTemplateId() + ")";
	}
}
