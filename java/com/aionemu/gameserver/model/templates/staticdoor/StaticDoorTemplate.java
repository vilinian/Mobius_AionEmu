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
package com.aionemu.gameserver.model.templates.staticdoor;

import java.util.EnumSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.geoEngine.bounding.BoundingBox;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;

/**
 * Represents the configuration data for a static door object in the game world.<br>
 * This class extends {@link VisibleObjectTemplate} to define specific properties for doors.
 * @author Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StaticDoor")
public class StaticDoorTemplate extends VisibleObjectTemplate
{
	@XmlAttribute
	protected DoorType type = DoorType.DOOR;
	@XmlAttribute
	protected Float x;
	@XmlAttribute
	protected Float y;
	@XmlAttribute
	protected Float z;
	@XmlAttribute(name = "doorid")
	protected int doorId;
	@XmlAttribute(name = "keyid")
	protected int keyId;
	@XmlAttribute(name = "state")
	protected String statesHex;
	@XmlAttribute(name = "mesh")
	private String meshFile;
	@XmlElement(name = "box")
	private StaticDoorBounds box;
	@XmlTransient
	EnumSet<StaticDoorState> states = EnumSet.noneOf(StaticDoorState.class);
	
	/**
	 * Retrieves the X coordinate of the door.<br>
	 * This value represents the horizontal position in the world.
	 * @return the {@code Float} value of the X coordinate.
	 */
	public Float getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the vertical position of the door.<br>
	 * This value represents the {@code y} coordinate in the 3D space.
	 * @return The {@code Float} value of the {@code y} coordinate.
	 */
	public Float getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the vertical coordinate of the door.<br>
	 * This value represents the {@code z} position in the 3D world.
	 * @return The {@code Float} value of the {@code z} coordinate.
	 */
	public Float getZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the unique identifier for this door.<br>
	 * This value is used to identify specific doors in the game world.
	 * @return The {@code int} value of the door ID.
	 */
	public int getDoorId()
	{
		return doorId;
	}
	
	/**
	 * Retrieves the unique identifier for the key required to open this door.<br>
	 * This value is used to check if a player possesses the correct item.
	 * @return The {@code int} value of the {@code keyId}.
	 */
	public int getKeyId()
	{
		return keyId;
	}
	
	/**
	 * Retrieves the unique identifier for this static door template.<br>
	 * This value is used to identify the specific door type in the game.
	 * @return The unique template ID as an {@code int}.
	 */
	@Override
	public int getTemplateId()
	{
		return 300001;
	}
	
	/**
	 * Returns the name of the door.<br>
	 * This method returns the literal value {@code "door"}.
	 * @return The name of the door as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return "door";
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	@Override
	public int getNameId()
	{
		return 0;
	}
	
	/**
	 * Retrieves the initial states for a static door.<br>
	 * This method parses the {@code statesHex} value to determine the active states.<br>
	 * It returns an {@link EnumSet} of {@link StaticDoorState}.
	 * @return An {@link EnumSet} containing the initial door states.
	 */
	public EnumSet<StaticDoorState> getInitialStates()
	{
		if (statesHex != null)
		{
			int radix = 16;
			if (statesHex.startsWith("0x"))
			{
				statesHex = statesHex.replace("0x", "");
			}
			else
			{
				radix = 10;
			}
			
			try
			{
				StaticDoorState.setStates(Integer.parseInt(statesHex, radix), states);
			}
			catch (NumberFormatException ex)
			{
			}
			finally
			{
				statesHex = null;
			}
		}
		
		return states;
	}
	
	/**
	 * Retrieves the file path for the door's 3D model.<br>
	 * This value is stored in the {@code mesh} attribute.
	 * @return The name of the mesh file as a {@code String}.
	 */
	public String getMeshFile()
	{
		return meshFile;
	}
	
	/**
	 * Retrieves the {@link BoundingBox} for this door.<br>
	 * It returns {@code null} if no bounding box is defined.
	 * @return the current {@code BoundingBox} object or {@code null}.
	 */
	public BoundingBox getBoundingBox()
	{
		if (box == null)
		{
			return null;
		}
		
		return box.getBoundingBox();
	}
	
	/**
	 * Retrieves the current door type.<br>
	 * This method returns the {@code DoorType} assigned to this template.
	 * @return The {@code DoorType} of the door.
	 */
	public DoorType getDoorType()
	{
		return type;
	}
}
