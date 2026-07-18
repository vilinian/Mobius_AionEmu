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
package com.aionemu.gameserver.world.zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.geometry.Area;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.model.templates.zone.ZoneTemplate;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.handler.AdvencedZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;

/**
 * Represents a specific active instance of a {@link ZoneTemplate} within the game world.<br>
 * It manages the entities, such as {@link Player} and {@link Creature}, currently located in that zone.
 * @author ATracer
 */
public class ZoneInstance implements Comparable<ZoneInstance>
{
	private final ZoneInfo template;
	private final int mapId;
	private final Map<Integer, Creature> creatures = new HashMap<>();
	protected List<ZoneHandler> handlers = new ArrayList<>();
	
	/**
	 * Creates a new instance of a zone.<br>
	 * This method initializes the zone with a specific map and template.
	 * @param mapId The unique identifier for the map.
	 * @param template The {@link ZoneInfo} used to define the zone properties.
	 */
	public ZoneInstance(int mapId, ZoneInfo template)
	{
		this.template = template;
		this.mapId = mapId;
	}
	
	/**
	 * Retrieves the {@link Area} associated with this zone's template.<br>
	 * This method provides access to the physical boundaries of the area.
	 * @return The {@code Area} object from the underlying {@code ZoneInfo}.
	 */
	public Area getAreaTemplate()
	{
		return template.getArea();
	}
	
	/**
	 * Retrieves the {@code ZoneTemplate} associated with this zone.<br>
	 * This method returns the template data for the current area.
	 * @return The {@link ZoneTemplate} object.
	 */
	public ZoneTemplate getZoneTemplate()
	{
		return template.getZoneTemplate();
	}
	
	/**
	 * Checks if a {@link Creature} is currently inside this zone.<br>
	 * It verifies the world ID and the 3D coordinates of the creature.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is inside the zone, {@code false} otherwise.
	 */
	public boolean revalidate(Creature creature)
	{
		return ((mapId == creature.getWorldId()) && template.getArea().isInside3D(creature.getX(), creature.getY(), creature.getZ()));
	}
	
	/**
	 * Handles the logic when a {@code Creature} enters this zone.<br>
	 * It checks if the creature can enter using the parent class method.<br>
	 * If successful, it sets the creature's zone type to {@code ZoneType.FLY}.
	 * @param creature The {@code Creature} entering the zone.
	 * @return {@code true} if the creature successfully entered, otherwise {@code false}.
	 */
	public synchronized boolean onEnter(Creature creature)
	{
		if (creatures.containsKey(creature.getObjectId()))
		{
			return false;
		}
		
		creatures.put(creature.getObjectId(), creature);
		if (creature instanceof Player)
		{
			creature.getController().onEnterZone(this);
		}
		
		for (int i = 0; i < handlers.size(); i++)
		{
			handlers.get(i).onEnterZone(creature, this);
		}
		
		return true;
	}
	
	/**
	 * This method is called when a {@link Creature} leaves the zone.<br>
	 * It removes the creature from the internal list of creatures.<br>
	 * It notifies the creature controller and all registered handlers.
	 * @param creature The {@link Creature} that is leaving the zone.
	 * @return {@code true} if the leave action was successful, otherwise {@code false}.
	 */
	public synchronized boolean onLeave(Creature creature)
	{
		if (!creatures.containsKey(creature.getObjectId()))
		{
			return false;
		}
		
		creatures.remove(creature.getObjectId());
		creature.getController().onLeaveZone(this);
		for (int i = 0; i < handlers.size(); i++)
		{
			handlers.get(i).onLeaveZone(creature, this);
		}
		
		return true;
	}
	
	/**
	 * This method checks if any zone within the region triggers an event when a creature dies.<br>
	 * It iterates through all zones to see if the {@code target} is inside one.<br>
	 * If a zone's {@code Creature)} method returns {@code true}, this method also returns {@code true}.
	 * @param attacker The creature that performed the attack.
	 * @param target The creature that died.
	 * @return {@code true} if any zone triggered an event, otherwise {@code false}.
	 */
	public boolean onDie(Creature attacker, Creature target)
	{
		if (!creatures.containsKey(target.getObjectId()))
		{
			return false;
		}
		
		for (int i = 0; i < handlers.size(); i++)
		{
			final ZoneHandler handler = handlers.get(i);
			if (handler instanceof AdvencedZoneHandler)
			{
				if (((AdvencedZoneHandler) handler).onDie(attacker, target, this))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific {@link Creature} is currently located within this zone.<br>
	 * It verifies the presence of the creature by checking its unique object ID.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is inside, {@code false} otherwise.
	 */
	public boolean isInsideCreature(Creature creature)
	{
		return creatures.containsKey(creature.getObjectId());
	}
	
	/**
	 * Checks if a specific 3D point is within the zone boundaries.<br>
	 * This method uses the {@code Area} data from the zone template.
	 * @param x The X coordinate of the point.
	 * @param y The Y coordinate of the point.
	 * @param z The Z coordinate of the point.
	 * @return {@code true} if the coordinates are inside the area, {@code false} otherwise.
	 */
	public boolean isInsideCordinate(float x, float y, float z)
	{
		return template.getArea().isInside3D(x, y, z);
	}
	
	/**
	 * Compares this {@link ZoneInstance} with another instance.<br>
	 * It first compares the priority of their zone templates.<br>
	 * If priorities are equal, it compares their name IDs.
	 * @param o The other {@link ZoneInstance} to compare against.
	 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(ZoneInstance o)
	{
		final int result = getZoneTemplate().getPriority() - o.getZoneTemplate().getPriority();
		if (result == 0)
		{
			return template.getZoneTemplate().getName().id() - o.template.getZoneTemplate().getName().id();
		}
		
		return result;
	}
	
	/**
	 * Registers a new {@link ZoneHandler} to this zone instance.<br>
	 * This allows the handler to process events within the area.
	 * @param handler The {@code ZoneHandler} to be added.
	 */
	public void addHandler(ZoneHandler handler)
	{
		handlers.add(handler);
	}
	
	/**
	 * Checks if flying is permitted in this zone.<br>
	 * It evaluates the {@link ZoneTemplate} flags and any world map overrides.
	 * @return {@code true} if flight is allowed, {@code false} otherwise.
	 */
	public boolean canFly()
	{
		if ((template.getZoneTemplate().getFlags() == -1) || World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.FLY))
		{
			return World.getInstance().getWorldMap(mapId).isPossibleFly();
		}
		
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.FLY.getId()) != 0;
	}
	
	/**
	 * Checks if gliding is enabled in the current zone.<br>
	 * This method checks the {@code flags} of the {@link ZoneTemplate}.<br>
	 * It also considers any overrides from the world map.
	 * @return {@code true} if gliding is allowed, otherwise {@code false}.
	 */
	public boolean canGlide()
	{
		if ((template.getZoneTemplate().getFlags() == -1) || World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.GLIDE))
		{
			return World.getInstance().getWorldMap(mapId).canGlide();
		}
		
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.GLIDE.getId()) != 0;
	}
	
	/**
	 * Checks if the map allows placing a kisk.<br>
	 * This method evaluates the {@code flags} property against the {@code BIND} ID.
	 * @return {@code true} if the bind flag is set, {@code false} otherwise.
	 */
	public boolean canPutKisk()
	{
		if ((template.getZoneTemplate().getFlags() == -1) || World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.BIND))
		{
			return World.getInstance().getWorldMap(mapId).canPutKisk();
		}
		
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.BIND.getId()) != 0;
	}
	
	/**
	 * Checks if the map allows players to use the recall feature.<br>
	 * This method evaluates the current {@code flags} against the {@code RECALL} ID.
	 * @return {@code true} if recall is enabled, {@code false} otherwise.
	 */
	public boolean canRecall()
	{
		if ((template.getZoneTemplate().getFlags() == -1) || World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.RECALL))
		{
			return World.getInstance().getWorldMap(mapId).canRecall();
		}
		
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.RECALL.getId()) != 0;
	}
	
	/**
	 * Checks if riding is allowed in this zone.<br>
	 * This method evaluates the {@code flags} property of the template.<br>
	 * It also checks for any map-specific overrides from the {@link World}.
	 * @return {@code true} if riding is permitted, {@code false} otherwise.
	 */
	public boolean canRide()
	{
		if ((template.getZoneTemplate().getFlags() == -1) || World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.RIDE))
		{
			return World.getInstance().getWorldMap(mapId).canRide();
		}
		
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.RIDE.getId()) != 0;
	}
	
	/**
	 * Checks if flying while riding is allowed on this map.<br>
	 * This method evaluates the {@code flags} property against the {@code FLY_RIDE} ID.
	 * @return {@code true} if flying while riding is enabled, {@code false} otherwise.
	 */
	public boolean canFlyRide()
	{
		if ((template.getZoneTemplate().getFlags() == -1) || World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.FLY_RIDE))
		{
			return World.getInstance().getWorldMap(mapId).canFlyRide();
		}
		
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.FLY_RIDE.getId()) != 0;
	}
	
	/**
	 * Checks if Player vs Player combat is enabled on this map.<br>
	 * This method evaluates the {@code flags} attribute against the {@code PVP_ENABLED} ID.
	 * @return {@code true} if PVP is allowed, {@code false} otherwise.
	 */
	public boolean isPvpAllowed()
	{
		if ((template.getZoneTemplate().getZoneType() != ZoneClassName.PVP) || World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.PVP_ENABLED))
		{
			return World.getInstance().getWorldMap(mapId).isPvpAllowed();
		}
		
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.PVP_ENABLED.getId()) != 0;
	}
	
	/**
	 * Checks if duels between players of the same race are permitted.<br>
	 * This method evaluates the current {@code flags} for this map.
	 * @return {@code true} if same-race duels are allowed, {@code false} otherwise.
	 */
	public boolean isSameRaceDuelsAllowed()
	{
		if ((template.getZoneTemplate().getZoneType() != ZoneClassName.DUEL) || World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.DUEL_SAME_RACE_ENABLED))
		{
			return World.getInstance().getWorldMap(mapId).isSameRaceDuelsAllowed();
		}
		
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.DUEL_SAME_RACE_ENABLED.getId()) != 0;
	}
	
	/**
	 * Checks if duels between different races are permitted on this map.<br>
	 * This method evaluates the current {@code flags} against the {@code DUEL_OTHER_RACE_ENABLED} bitmask.
	 * @return {@code true} if other race duels are allowed, {@code false} otherwise.
	 */
	public boolean isOtherRaceDuelsAllowed()
	{
		if ((template.getZoneTemplate().getZoneType() != ZoneClassName.DUEL) || World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.DUEL_OTHER_RACE_ENABLED))
		{
			return World.getInstance().getWorldMap(mapId).isOtherRaceDuelsAllowed();
		}
		
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.DUEL_OTHER_RACE_ENABLED.getId()) != 0;
	}
	
	/**
	 * Retrieves the unique identifier for the town associated with this object.<br>
	 * This value is used to determine which town area the entity belongs to.
	 * @return the {@code int} ID of the town.
	 */
	public int getTownId()
	{
		return template.getZoneTemplate().getTownId();
	}
	
	/**
	 * Retrieves all creatures currently located in this zone.<br>
	 * The map uses the creature ID as the key.
	 * @return a {@code Map<Integer, Creature>} containing all creatures.
	 */
	public Map<Integer, Creature> getCreatures()
	{
		return creatures;
	}
}
