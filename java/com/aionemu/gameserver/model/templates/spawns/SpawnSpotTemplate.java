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
package com.aionemu.gameserver.model.templates.spawns;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the configuration data for a specific monster spawn location.<br>
 * This template defines where and how entities appear within the game world.<br>
 * It is used by the {@code SpawnManager} to manage entity distribution.
 * @author xTz
 * @modified Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpawnSpotTemplate")
public class SpawnSpotTemplate
{
	@XmlAttribute(name = "x", required = true)
	private float x;
	@XmlAttribute(name = "y", required = true)
	private float y;
	@XmlAttribute(name = "z", required = true)
	private float z;
	@XmlAttribute(name = "h", required = true)
	private byte h;
	@XmlAttribute(name = "static_id")
	private Integer staticId = 0;
	@XmlAttribute(name = "random_walk")
	private Integer randomWalk = 0;
	@XmlAttribute(name = "walker_id")
	private String walkerId;
	@XmlAttribute(name = "walker_index")
	private Integer walkerIdx;
	@XmlAttribute(name = "fly")
	private Integer fly = 0;
	@XmlAttribute(name = "anchor")
	private String anchor;
	@XmlAttribute(name = "state")
	private Integer state = 0;
	@XmlElement(name = "temporary_spawn")
	private TemporarySpawn temporaySpawn;
	@XmlElement(name = "model")
	private SpawnModel model;
	private static final Integer ZERO = 0;
	@XmlTransient
	private boolean fixed = false;
	
	/**
	 * Creates a new instance of {@link SpawnSpotTemplate}.<br>
	 * This constructor initializes the template with default values.
	 */
	public SpawnSpotTemplate()
	{
	}
	
	/**
	 * Prepares the object data before it is converted to XML.<br>
	 * This method sets certain fields to {@code null} if they contain default values.<br>
	 * It ensures that only relevant data is included in the final output.
	 * @param marshaller The {@link Marshaller} used to convert the object to XML.
	 */
	void beforeMarshal(Marshaller marshaller)
	{
		if (ZERO.equals(staticId))
		{
			staticId = null;
		}
		
		if (ZERO.equals(fly))
		{
			fly = null;
		}
		
		if (ZERO.equals(randomWalk))
		{
			randomWalk = null;
		}
		
		if (ZERO.equals(state))
		{
			state = null;
		}
		
		if (ZERO.equals(walkerIdx))
		{
			walkerIdx = null;
		}
	}
	
	/**
	 * This method is called after the object is marshaled to XML.<br>
	 * It ensures that {@code staticId}, {@code fly}, {@code randomWalk}, {@code state}, and {@code walkerIdx} have default values if they are {@code null}.
	 * @param marshaller The {@link Marshaller} used for the operation.
	 */
	void afterMarshal(Marshaller marshaller)
	{
		if (staticId == null)
		{
			staticId = 0;
		}
		
		if (fly == null)
		{
			fly = 0;
		}
		
		if (randomWalk == null)
		{
			randomWalk = 0;
		}
		
		if (state == null)
		{
			state = 0;
		}
		
		if (walkerIdx == null)
		{
			walkerIdx = 0;
		}
	}
	
	/**
	 * Creates a new {@link SpawnSpotTemplate} with specific coordinates.<br>
	 * This constructor sets the basic position and rotation for a spawn point.
	 * @param x The horizontal coordinate of the spawn spot.
	 * @param y The vertical coordinate of the spawn spot.
	 * @param z The depth coordinate of the spawn spot.
	 * @param h The heading value representing the rotation.
	 */
	public SpawnSpotTemplate(float x, float y, float z, byte h)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.h = h;
	}
	
	/**
	 * Creates a new {@link SpawnSpotTemplate} with specific coordinates and walking behavior.<br>
	 * This constructor initializes the position, heading, and random walk settings for an NPC spawn.
	 * @param x The X coordinate of the spawn point.
	 * @param y The Y coordinate of the spawn point.
	 * @param z The Z coordinate of the spawn point.
	 * @param h The heading value of the spawn point.
	 * @param randomWalk The walk pattern ID, which is only set if it is greater than {@code 0}.
	 * @param walkerId The unique identifier for the walker entity.
	 * @param walkerIndex The index used to identify the specific walker.
	 */
	public SpawnSpotTemplate(float x, float y, float z, byte h, int randomWalk, String walkerId, Integer walkerIndex)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.h = h;
		if (randomWalk > 0)
		{
			this.randomWalk = randomWalk;
		}
		
		this.walkerId = walkerId;
		walkerIdx = walkerIndex;
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the current rotation direction of the object.<br>
	 * This value is stored as a {@code byte}.
	 * @return The heading value of the object.
	 */
	public byte getHeading()
	{
		return h;
	}
	
	/**
	 * Retrieves the unique static identifier for this spawn.<br>
	 * This value is used to identify specific objects in the game world.
	 * @return The {@code int} representing the static ID.
	 */
	public int getStaticId()
	{
		return staticId;
	}
	
	/**
	 * Sets the unique identifier for this spawn.<br>
	 * This value is used to identify a specific object in the game world.
	 * @param staticId The {@code int} value to assign as the new ID.
	 */
	public void setStaticId(int staticId)
	{
		this.staticId = staticId;
	}
	
	/**
	 * Retrieves the unique identifier for the walker.<br>
	 * This value is used to identify specific walking behaviors.
	 * @return The {@code String} representing the walker ID.
	 */
	public String getWalkerId()
	{
		return walkerId;
	}
	
	/**
	 * Sets the unique identifier for the walker.<br>
	 * This value is used to identify which walking path to use.
	 * @param walkerId The {@code String} ID of the walker.
	 */
	public void setWalkerId(String walkerId)
	{
		this.walkerId = walkerId;
	}
	
	/**
	 * Retrieves the index of the walker for this spawn spot.<br>
	 * It returns {@code 0} if no index is defined.
	 * @return The current {@code walkerIndex} as an {@code int}.
	 */
	public int getWalkerIndex()
	{
		if (walkerIdx == null)
		{
			return 0;
		}
		
		return walkerIdx;
	}
	
	/**
	 * Retrieves the current value of the {@code randomWalk} property.<br>
	 * This value determines the specific path for a walking entity.
	 * @return The integer value of the {@code randomWalk} attribute.
	 */
	public int getRandomWalk()
	{
		return randomWalk;
	}
	
	/**
	 * Retrieves the flying status of the spawn spot.<br>
	 * This value indicates whether the entity can fly.
	 * @return the {@code int} value representing the fly state.
	 */
	public int getFly()
	{
		return fly;
	}
	
	/**
	 * Retrieves the anchor identifier for this spawn spot.<br>
	 * This value is used to link the spawn to a specific point.
	 * @return The {@code String} representing the anchor.
	 */
	public String getAnchor()
	{
		return anchor;
	}
	
	/**
	 * Retrieves the {@link SpawnModel} associated with this spawn spot.<br>
	 * This method returns the visual model used for spawning.
	 * @return The {@code SpawnModel} object.
	 */
	public SpawnModel getModel()
	{
		return model;
	}
	
	/**
	 * Retrieves the current state of the spawn spot.<br>
	 * This value represents the internal status of the object.
	 * @return The current state as an {@code int}.
	 */
	public int getState()
	{
		if (state == null)
		{
			return 0;
		}
		
		return state;
	}
	
	/**
	 * Retrieves the temporary spawn data for this {@link Spawn}.<br>
	 * This may return {@code null} if no temporary spawn is defined.
	 * @return The {@code TemporarySpawn} object associated with this spawn.
	 */
	public TemporarySpawn getTemporarySpawn()
	{
		return temporaySpawn;
	}
	
	/**
	 * Updates the vertical position of the object.<br>
	 * This method sets the {@code z} coordinate to a new value.<br>
	 * It also updates the persistent state and the current position if available.
	 * @param z The new height value for the object.
	 */
	public void setZ(float z)
	{
		if ((this.z > z) && ((this.z - z) > 0.5))
		{
			fixed = true;
		}
		
		this.z = z;
	}
	
	/**
	 * Checks if the spawn spot is fixed in position.<br>
	 * Returns {@code true} if it is fixed.<br>
	 * Returns {@code false} otherwise.
	 * @return The current status of the {@code fixed} property.
	 */
	public boolean isFixed()
	{
		return fixed;
	}
}
