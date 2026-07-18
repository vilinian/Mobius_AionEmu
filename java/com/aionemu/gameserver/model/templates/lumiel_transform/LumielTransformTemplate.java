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
package com.aionemu.gameserver.model.templates.lumiel_transform;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the data template for the {@code LumielTransform}.<br>
 * It stores configuration settings used to initialize transform properties in the game server.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "LumielTransformTemplate")
public class LumielTransformTemplate
{
	@XmlElement(name = "reward")
	private List<LumielTransformReward> lumielTransformRewards;
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "need_point")
	protected int needPoints;
	@XmlAttribute(name = "activate")
	protected boolean activate;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the number of points required for this transformation.<br>
	 * This value is stored in the {@code need_point} attribute.
	 * @return The total points needed as an {@code int}.
	 */
	public int getNeedPoints()
	{
		return needPoints;
	}
	
	/**
	 * Checks if the transformation is currently active.<br>
	 * This method returns the value of the {@code activate} field.
	 * @return {@code true} if it is active, {@code false} otherwise.
	 */
	public boolean isActivate()
	{
		return activate;
	}
	
	/**
	 * Retrieves the list of rewards for a Lumiel transformation.<br>
	 * This method returns all {@link LumielTransformReward} objects associated with this template.
	 * @return A {@code List} of {@code LumielTransformReward} objects.
	 */
	public List<LumielTransformReward> getLumielTransformRewards()
	{
		return lumielTransformRewards;
	}
}
