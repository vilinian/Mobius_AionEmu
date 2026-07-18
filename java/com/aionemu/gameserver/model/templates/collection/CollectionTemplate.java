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
package com.aionemu.gameserver.model.templates.collection;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * Represents the base configuration data for a collection item.<br>
 * This template defines the properties and attributes used to initialize collection objects in the game world.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "CollectionTemplate")
public class CollectionTemplate
{
	@XmlElement(name = "modifiers", required = false)
	private ModifiersTemplate modifiers;
	@XmlElement(name = "material")
	private List<MaterialCollectionTemplate> materials;
	@XmlElement(name = "reward")
	private List<RewardCollectionTemplate> rewards;
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "active")
	protected boolean active;
	@XmlAttribute(name = "grade")
	protected CollectionType grade;
	
	/**
	 * Retrieves the {@code ModifiersTemplate} associated with this collection.<br>
	 * This method returns the modifier data used for experience calculations.
	 * @return the {@link ModifiersTemplate} object or {@code null} if no modifiers exist.
	 */
	public ModifiersTemplate getModifiers()
	{
		return modifiers;
	}
	
	/**
	 * Retrieves the list of materials for this collection.<br>
	 * This method returns all {@code MaterialCollectionTemplate} objects associated with the template.
	 * @return a {@code List} of {@link MaterialCollectionTemplate} objects.
	 */
	public List<MaterialCollectionTemplate> getMaterials()
	{
		return materials;
	}
	
	/**
	 * Retrieves the list of rewards for this collection.<br>
	 * This method returns all {@link RewardCollectionTemplate} objects associated with the template.
	 * @return a {@code List} of {@code RewardCollectionTemplate} objects.
	 */
	public List<RewardCollectionTemplate> getRewards()
	{
		return rewards;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Checks if the motion is currently active.<br>
	 * This method returns the current state of the {@code active} field.
	 * @return {@code true} if the motion is active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Retrieves the current grade of the collection.<br>
	 * This value is stored in the {@code grade} field.
	 * @return The {@link CollectionType} representing the grade.
	 */
	public CollectionType getGrade()
	{
		return grade;
	}
}
