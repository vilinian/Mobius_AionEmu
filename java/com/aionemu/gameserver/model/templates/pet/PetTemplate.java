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
package com.aionemu.gameserver.model.templates.pet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.stats.PetStatsTemplate;

/**
 * This class serves as a data template for pet entities in the game.<br>
 * It stores static configuration data such as names, descriptions, and {@link PetStatsTemplate} information.
 * @author IlBuono
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "pet")
public class PetTemplate
{
	@XmlAttribute(name = "id", required = true)
	private int id;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "nameid", required = true)
	private int nameId;
	@XmlAttribute(name = "condition_reward")
	private int conditionReward;
	@XmlElement(name = "petfunction")
	private List<PetFunction> petFunctions;
	@XmlElement(name = "petstats")
	private PetStatsTemplate petStats;
	@XmlTransient
	Boolean hasPlayerFuncs = null;
	
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
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the list of functions associated with this pet.<br>
	 * This method ensures that a default empty function is present if no player functions exist.
	 * @return A {@code List} of {@link PetFunction} objects.
	 */
	public List<PetFunction> getPetFunctions()
	{
		if (hasPlayerFuncs == null)
		{
			hasPlayerFuncs = false;
			if (petFunctions == null)
			{
				final List<PetFunction> result = new ArrayList<>();
				result.add(PetFunction.CreateEmpty());
				petFunctions = result;
			}
			else
			{
				for (PetFunction func : petFunctions)
				{
					if (func.getPetFunctionType().isPlayerFunction())
					{
						hasPlayerFuncs = true;
						break;
					}
				}
				
				if (!hasPlayerFuncs)
				{
					petFunctions.add(PetFunction.CreateEmpty());
				}
			}
		}
		
		return petFunctions;
	}
	
	/**
	 * Retrieves the warehouse function for this pet.<br>
	 * It searches through the list of {@link PetFunction} objects.<br>
	 * Returns {@code null} if no warehouse function is found.
	 * @return the {@code PetFunction} associated with the warehouse type or {@code null}.
	 */
	public PetFunction getWarehouseFunction()
	{
		if (petFunctions == null)
		{
			return null;
		}
		
		for (PetFunction pf : petFunctions)
		{
			if (pf.getPetFunctionType() == PetFunctionType.WAREHOUSE)
			{
				return pf;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if the pet has a specific function.<br>
	 * It searches through all available functions for this template.
	 * @param type The {@code PetFunctionType} to look for.
	 * @return {@code true} if the function exists, otherwise {@code false}.
	 */
	public boolean ContainsFunction(PetFunctionType type)
	{
		if (type.getId() < 0)
		{
			return false;
		}
		
		for (PetFunction t : getPetFunctions())
		{
			if (t.getPetFunctionType() == type)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves a specific {@link PetFunction} based on its type.<br>
	 * This method searches through the list of available functions.<br>
	 * It returns the first matching function found.
	 * @param type The {@code PetFunctionType} to search for.
	 * @return The matching {@code PetFunction} object, or {@code null} if no match is found.
	 */
	public PetFunction getPetFunction(PetFunctionType type)
	{
		for (PetFunction t : getPetFunctions())
		{
			if (t.getPetFunctionType() == type)
			{
				return t;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the statistics template for this pet.<br>
	 * This method returns the {@code PetStatsTemplate} object associated with the pet.
	 * @return the {@link PetStatsTemplate} of the pet.
	 */
	public PetStatsTemplate getPetStats()
	{
		return petStats;
	}
	
	/**
	 * Retrieves the reward value for a pet's condition.<br>
	 * This value is defined in the {@code condition_reward} attribute.
	 * @return The integer reward value.
	 */
	public int getConditionReward()
	{
		return conditionReward;
	}
}
