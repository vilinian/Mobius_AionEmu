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
package com.aionemu.gameserver.model.templates.tribe;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.TribeClass;

/**
 * Represents the data model for a tribe within the game world.<br>
 * This class stores configuration details such as its name and associated {@link TribeClass}.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tribe")
public class Tribe
{
	@XmlList
	protected List<TribeClass> aggro;
	@XmlList
	protected List<TribeClass> hostile;
	@XmlList
	protected List<TribeClass> friend;
	@XmlList
	protected List<TribeClass> neutral;
	@XmlList
	protected List<TribeClass> none;
	@XmlList
	protected List<TribeClass> support;
	@XmlAttribute
	protected TribeClass base = TribeClass.NONE;
	@XmlAttribute(required = true)
	protected TribeClass name;
	
	/**
	 * Retrieves the list of classes that are considered aggressive.<br>
	 * This method returns an empty {@code List} if no aggro data exists.
	 * @return a {@code List} of {@link TribeClass} objects.
	 */
	public List<TribeClass> getAggro()
	{
		if (aggro == null)
		{
			aggro = Collections.emptyList();
		}
		
		return aggro;
	}
	
	/**
	 * Retrieves the list of classes that are considered hostile.<br>
	 * This method returns an empty {@code List} if no hostile classes are defined.
	 * @return A {@code List} of {@link TribeClass} objects representing hostile entities.
	 */
	public List<TribeClass> getHostile()
	{
		if (hostile == null)
		{
			hostile = Collections.emptyList();
		}
		
		return hostile;
	}
	
	/**
	 * Retrieves the list of friendly classes for this tribe.<br>
	 * Returns an empty list if no friends are defined.
	 * @return a {@code List} of {@link TribeClass} objects representing friends.
	 */
	public List<TribeClass> getFriend()
	{
		if (friend == null)
		{
			friend = Collections.emptyList();
		}
		
		return friend;
	}
	
	/**
	 * Retrieves the list of neutral classes for this tribe.<br>
	 * This method returns an empty {@code List} if no neutral classes are defined.
	 * @return a {@code List} of {@link TribeClass} objects that are considered neutral.
	 */
	public List<TribeClass> getNeutral()
	{
		if (neutral == null)
		{
			neutral = Collections.emptyList();
		}
		
		return neutral;
	}
	
	/**
	 * Retrieves the list of {@code TribeClass} entries that are categorized as none.<br>
	 * This method ensures a non-null list is returned by providing an empty list if {@code none} is null.
	 * @return A {@code List<TribeClass>} containing the none categories.
	 */
	public List<TribeClass> getNone()
	{
		if (none == null)
		{
			none = Collections.emptyList();
		}
		
		return none;
	}
	
	/**
	 * Retrieves the list of classes that provide support.<br>
	 * This method returns an empty {@code List} if no support is defined.
	 * @return a {@code List} of {@link TribeClass} objects representing supported classes.
	 */
	public List<TribeClass> getSupport()
	{
		if (support == null)
		{
			support = Collections.emptyList();
		}
		
		return support;
	}
	
	/**
	 * Retrieves the primary class of the tribe.<br>
	 * It returns the {@code base} value if it is not {@code TribeClass.NONE}.<br>
	 * If {@code base} is {@code TribeClass.NONE}, it returns the {@code getName} instead.
	 * @return The {@code TribeClass} of the tribe.
	 */
	public TribeClass getBase()
	{
		return base == TribeClass.NONE ? name : base;
	}
	
	/**
	 * Retrieves the name of the tribe.<br>
	 * This returns the {@code TribeClass} associated with this tribe.
	 * @return The {@code TribeClass} representing the tribe name.
	 */
	public TribeClass getName()
	{
		return name;
	}
	
	/**
	 * Checks if this tribe is classified as a guard.<br>
	 * This method delegates the check to the {@link TribeClass} of the tribe name.
	 * @return {@code true} if the tribe is a guard, {@code false} otherwise.
	 */
	public boolean isGuard()
	{
		return name.isGuard();
	}
	
	/**
	 * Checks if the tribe belongs to a basic class.<br>
	 * This method delegates the check to the {@code name} field.
	 * @return {@code true} if it is a basic class, {@code false} otherwise.
	 */
	public boolean isBasic()
	{
		return name.isBasicClass();
	}
	
	/**
	 * Returns a string representation of the tribe.<br>
	 * This includes the {@code getName} and the {@code getBase}.
	 * @return A formatted string containing the tribe name and its base type.
	 */
	@Override
	public String toString()
	{
		return name + " (" + base + ")";
	}
}
