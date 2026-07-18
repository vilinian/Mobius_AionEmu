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
package com.aionemu.gameserver.model.templates.siegelocation;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.siege.SiegeType;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * This class defines the template data for siege locations within the game world.<br>
 * It stores configuration details used to initialize {@link com.aionemu.gameserver.model.siege.SiegeType} instances.
 * @author Sarynth modified by antness & Source & Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "siegelocation")
public class SiegeLocationTemplate
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "type")
	protected SiegeType type;
	@XmlAttribute(name = "world")
	protected int world;
	@XmlElement(name = "artifact_activation")
	protected ArtifactActivation artifactActivation;
	@XmlElement(name = "door_repair")
	protected DoorRepair doorRepair;
	@XmlElement(name = "siege_reward")
	protected List<SiegeReward> siegeRewards;
	@XmlElement(name = "legion_reward")
	protected List<SiegeLegionReward> siegeLegionRewards;
	@XmlAttribute(name = "name_id")
	protected int nameId = 0;
	@XmlAttribute(name = "buff_id")
	protected int buffId = 0;
	@XmlAttribute(name = "buff_idA")
	protected int buffIdA = 0;
	@XmlAttribute(name = "buff_idE")
	protected int buffIdE = 0;
	@XmlAttribute(name = "owner_gp")
	protected int ownerGp = 0;
	@XmlAttribute(name = "repeat_count")
	protected int repeatCount = 1;
	@XmlAttribute(name = "repeat_interval")
	protected int repeatInterval = 1;
	@XmlAttribute(name = "siege_duration")
	protected int siegeDuration;
	@XmlAttribute(name = "influence")
	protected int influenceValue;
	@XmlAttribute(name = "occupy_count")
	protected int occupyCount = 0;
	@XmlList
	@XmlAttribute(name = "fortress_dependency")
	protected List<Integer> fortressDependency;
	
	// Luna System
	@XmlElement(name = "luna_boost_price")
	protected List<LunaBoostPrice> lunaBoostPrice;
	@XmlElement(name = "luna_teleport_price")
	protected List<LunaTeleportPrice> lunaTeleportPrice;
	@XmlElement(name = "luna_reward")
	protected List<LunaReward> lunaReward;
	@XmlElement(name = "luna_teleport")
	protected List<LunaTeleport> lunaTeleport;
	
	@XmlAttribute(name = "base_id")
	protected int baseId;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the {@link SiegeType} of this location.<br>
	 * This identifies what kind of siege is occurring here.
	 * @return the current {@code SiegeType}
	 */
	public SiegeType getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return world;
	}
	
	/**
	 * Retrieves the activation data for this siege location.<br>
	 * This method returns the {@code ArtifactActivation} object associated with the template.
	 * @return the {@code ArtifactActivation} instance.
	 */
	public ArtifactActivation getActivation()
	{
		return artifactActivation;
	}
	
	/**
	 * Retrieves the list of rewards for a siege.<br>
	 * This method returns the {@code siegeRewards} associated with this location.
	 * @return A {@code List} of {@link SiegeReward} objects.
	 */
	public List<SiegeReward> getSiegeRewards()
	{
		return siegeRewards;
	}
	
	/**
	 * Retrieves the list of rewards for the legion.<br>
	 * This method returns all {@link SiegeLegionReward} objects associated with this location.
	 * @return a {@code List} of {@code SiegeLegionReward} objects.
	 */
	public List<SiegeLegionReward> getSiegeLegionRewards()
	{
		return siegeLegionRewards;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the current number of occupants in this location.<br>
	 * This value represents how many entities are currently occupying the area.
	 * @return The total count of occupants as an {@code int}.
	 */
	public int getOccupyCount()
	{
		return occupyCount;
	}
	
	/**
	 * Retrieves the number of times this challenge can be repeated.<br>
	 * This value is stored in the {@code repeatCount} field.
	 * @return The total count of allowed repetitions as an {@code int}.
	 */
	public int getRepeatCount()
	{
		return repeatCount;
	}
	
	/**
	 * Retrieves the time interval between repeated events.<br>
	 * This value is used to determine how often a siege occurs.
	 * @return The {@code int} value representing the repeat interval.
	 */
	public int getRepeatInterval()
	{
		return repeatInterval;
	}
	
	/**
	 * Retrieves the list of IDs that this location depends on.<br>
	 * It returns an empty {@code List<Integer>} if no dependencies exist.
	 * @return a {@code List<Integer>} containing the dependency IDs.
	 */
	public List<Integer> getFortressDependency()
	{
		if (fortressDependency == null)
		{
			return Collections.emptyList();
		}
		
		return fortressDependency;
	}
	
	/**
	 * Retrieves the total duration of the current siege.<br>
	 * This value is stored in the {@code siegeDuration} field.
	 * @return The length of the siege as an {@code int}.
	 */
	public int getSiegeDuration()
	{
		return siegeDuration;
	}
	
	/**
	 * Retrieves the current influence value of this siege location.<br>
	 * This value represents the amount of control or power held at the site.
	 * @return The current {@code int} influence value.
	 */
	public int getInfluenceValue()
	{
		return influenceValue;
	}
	
	/**
	 * Retrieves the {@code DoorRepair} data for this siege location.<br>
	 * This method returns the repair configuration associated with the template.
	 * @return the {@code DoorRepair} object.
	 */
	public DoorRepair getRepair()
	{
		return doorRepair;
	}
	
	/**
	 * Retrieves the unique buffer identifier.<br>
	 * This value is stored in the {@code buffId} field.
	 * @return The integer ID of the buff.
	 */
	public int getBuffId()
	{
		return buffId;
	}
	
	/**
	 * Retrieves the unique identifier for buff A from the template.<br>
	 * This method updates the local {@code buffIdA} field with the value from {@code getBuffIdA}.
	 * @return The integer ID of buff A.
	 */
	public int getBuffIdA()
	{
		return buffIdA;
	}
	
	/**
	 * Retrieves the special buff ID from the {@link SiegeLocationTemplate}.<br>
	 * This method updates the local {@code buffIdE} field with the value.
	 * @return The integer value of the buff ID.
	 */
	public int getBuffIdE()
	{
		return buffIdE;
	}
	
	/**
	 * Retrieves the owner GP value for this siege location.<br>
	 * This value is stored in the {@code ownerGp} field.
	 * @return The integer value of the owner GP.
	 */
	public int getOwnerGp()
	{
		return ownerGp;
	}
	
	// Luna System
	/**
	 * Retrieves the list of prices for Luna Boosts.<br>
	 * This method returns the {@code lunaBoostPrice} associated with this location.
	 * @return a {@code List} of {@link LunaBoostPrice} objects.
	 */
	public List<LunaBoostPrice> getLunaBoostPrice()
	{
		return lunaBoostPrice;
	}
	
	/**
	 * Retrieves the list of prices for Luna teleports.<br>
	 * This method returns all associated {@code LunaTeleportPrice} objects.
	 * @return a {@code List} of {@code LunaTeleportPrice} objects.
	 */
	public List<LunaTeleportPrice> getLunaTeleportPrice()
	{
		return lunaTeleportPrice;
	}
	
	/**
	 * Retrieves the list of rewards for Luna.<br>
	 * This method returns all {@code LunaReward} objects associated with this location.
	 * @return a {@code List} of {@link LunaReward} objects.
	 */
	public List<LunaReward> getLunaReward()
	{
		return lunaReward;
	}
	
	/**
	 * Retrieves the list of {@link LunaTeleport} objects.<br>
	 * This method returns all teleport data associated with this siege location.
	 * @return a {@code List} of {@code LunaTeleport} objects.
	 */
	public List<LunaTeleport> getLunaTeleport()
	{
		return lunaTeleport;
	}
	
	/**
	 * Retrieves the unique identifier for this siege location.<br>
	 * This value is fetched from the {@link SiegeLocationTemplate}.<br>
	 * It also updates the internal {@code baseId} field.
	 * @return The unique integer ID of the siege location.
	 */
	public int getBaseId()
	{
		return baseId;
	}
}
