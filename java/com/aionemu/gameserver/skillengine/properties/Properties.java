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
package com.aionemu.gameserver.skillengine.properties;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class holds the configuration properties for a {@link Skill}.<br>
 * It maps XML data to internal fields used by the skill engine.<br>
 * Use this class to define specific attributes and behaviors of skills.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Properties")
public class Properties
{
	@XmlAttribute(name = "first_target", required = true)
	protected FirstTargetAttribute firstTarget;
	@XmlAttribute(name = "first_target_range", required = true)
	protected int firstTargetRange;
	@XmlAttribute(name = "awr")
	protected boolean addWeaponRange;
	@XmlAttribute(name = "target_relation", required = true)
	protected TargetRelationAttribute targetRelation;
	@XmlAttribute(name = "target_type", required = true)
	protected TargetRangeAttribute targetType;
	@XmlAttribute(name = "target_distance")
	protected int targetDistance;
	@XmlAttribute(name = "target_maxcount")
	protected int targetMaxCount;
	@XmlAttribute(name = "target_status")
	private List<String> targetStatus;
	@XmlAttribute(name = "revision_distance")
	protected int revisionDistance;
	@XmlAttribute(name = "effective_range")
	private int effectiveRange;
	@XmlAttribute(name = "effective_altitude")
	private int effectiveAltitude;
	@XmlAttribute(name = "effective_angle")
	private int effectiveAngle;
	@XmlAttribute(name = "effective_dist")
	private int effectiveDist;
	@XmlAttribute(name = "direction")
	protected AreaDirections direction = AreaDirections.NONE;
	@XmlAttribute(name = "target_species")
	protected TargetSpeciesAttribute targetSpecies = TargetSpeciesAttribute.ALL;
	
	/**
	 * Validates the properties of a {@link Skill} against the current configuration.<br>
	 * This method checks various attributes like target range, relation, and species.<br>
	 * It returns {@code false} if any property fails to be set correctly.
	 * @param skill The {@code Skill} object to be validated.
	 * @return {@code true} if all properties are valid, otherwise {@code false}.
	 */
	public boolean validate(Skill skill)
	{
		if (firstTarget != null)
		{
			if (!FirstTargetProperty.set(skill, this))
			{
				return false;
			}
		}
		
		if ((firstTargetRange != 0) || addWeaponRange)
		{
			if (!FirstTargetRangeProperty.set(skill, this, CastState.CAST_START))
			{
				return false;
			}
		}
		
		if (targetType != null)
		{
			if (!TargetRangeProperty.set(skill, this))
			{
				return false;
			}
		}
		
		if (targetRelation != null)
		{
			if (!TargetRelationProperty.set(skill, this))
			{
				return false;
			}
		}
		
		if (targetType != null)
		{
			if (!MaxCountProperty.set(skill, this))
			{
				return false;
			}
		}
		
		if (targetStatus != null)
		{
			if (!TargetStatusProperty.set(skill, this))
			{
				return false;
			}
		}
		
		if (targetSpecies != TargetSpeciesAttribute.ALL)
		{
			if (!TargetSpeciesProperty.set(skill, this))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Validates the skill properties when a cast is finished.<br>
	 * This method checks if all target requirements are met for the {@code Skill}.<br>
	 * It returns {@code false} if any property validation fails.
	 * @param skill The {@link Skill} object to validate.
	 * @return {@code true} if the skill is valid, or {@code false} otherwise.
	 */
	public boolean endCastValidate(Skill skill)
	{
		final Creature firstTarget = skill.getFirstTarget();
		skill.getEffectedList().clear();
		skill.getEffectedList().add(firstTarget);
		
		if (firstTargetRange != 0)
		{
			if (!FirstTargetRangeProperty.set(skill, this, CastState.CAST_END))
			{
				return false;
			}
		}
		
		if (targetType != null)
		{
			if (!TargetRangeProperty.set(skill, this))
			{
				return false;
			}
		}
		
		if (targetRelation != null)
		{
			if (!TargetRelationProperty.set(skill, this))
			{
				return false;
			}
		}
		
		if (targetType != null)
		{
			if (!MaxCountProperty.set(skill, this))
			{
				return false;
			}
		}
		
		if (targetStatus != null)
		{
			if (!TargetStatusProperty.set(skill, this))
			{
				return false;
			}
		}
		
		if (targetSpecies != TargetSpeciesAttribute.ALL)
		{
			if (!TargetSpeciesProperty.set(skill, this))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Retrieves the primary target attribute.<br>
	 * This method returns the {@code FirstTargetAttribute} associated with these properties.
	 * @return the {@code FirstTargetAttribute} object.
	 */
	public FirstTargetAttribute getFirstTarget()
	{
		return firstTarget;
	}
	
	/**
	 * Retrieves the range for the first target.<br>
	 * This value is used to determine how far the skill can reach its primary target.
	 * @return the {@code int} value of the first target range.
	 */
	public int getFirstTargetRange()
	{
		return firstTargetRange;
	}
	
	/**
	 * Checks if the weapon range should be added to the skill.<br>
	 * This property determines if the base weapon reach is included in the calculation.
	 * @return {@code true} if the weapon range is added, {@code false} otherwise.
	 */
	public boolean isAddWeaponRange()
	{
		return addWeaponRange;
	}
	
	/**
	 * Retrieves the target relation attribute.<br>
	 * This method returns the {@code targetRelation} property.
	 * @return the {@link TargetRelationAttribute} object.
	 */
	public TargetRelationAttribute getTargetRelation()
	{
		return targetRelation;
	}
	
	/**
	 * Retrieves the target type for this property.<br>
	 * This value determines how targets are selected based on range.
	 * @return the {@code TargetRangeAttribute} object.
	 */
	public TargetRangeAttribute getTargetType()
	{
		return targetType;
	}
	
	/**
	 * Retrieves the configured target distance.<br>
	 * This value is used to determine how far a skill can reach.
	 * @return the {@code int} value of the target distance.
	 */
	public int getTargetDistance()
	{
		return targetDistance;
	}
	
	/**
	 * Retrieves the maximum number of targets allowed.<br>
	 * This value is used to limit how many entities can be affected by a skill.
	 * @return The maximum count of targets as an {@code int}.
	 */
	public int getTargetMaxCount()
	{
		return targetMaxCount;
	}
	
	/**
	 * Retrieves the list of allowed target statuses.<br>
	 * This method returns the {@code targetStatus} field from the properties.
	 * @return a {@code List<String>} containing the status values.
	 */
	public List<String> getTargetStatus()
	{
		return targetStatus;
	}
	
	/**
	 * Retrieves the revision distance value.<br>
	 * This value is used to determine the range for skill revisions.
	 * @return The current {@code revision_distance} as an {@code int}.
	 */
	public int getRevisionDistance()
	{
		return revisionDistance;
	}
	
	/**
	 * Retrieves the calculated range for a skill.<br>
	 * This value is stored in the {@code effective_range} attribute.
	 * @return The integer value of the effective range.
	 */
	public int getEffectiveRange()
	{
		return effectiveRange;
	}
	
	/**
	 * Retrieves the altitude value for the skill effect.<br>
	 * This value is used to determine the vertical reach of an ability.
	 * @return The current {@code effectiveAltitude} as an {@code int}.
	 */
	public int getEffectiveAltitude()
	{
		return effectiveAltitude;
	}
	
	/**
	 * Retrieves the effective distance value.<br>
	 * This value is used to determine the reach of a skill effect.
	 * @return The {@code int} value of the effective distance.
	 */
	public int getEffectiveDist()
	{
		return effectiveDist;
	}
	
	/**
	 * Retrieves the current effective angle.<br>
	 * This value is used to determine the scope of a skill effect.
	 * @return The {@code int} value of the effective angle.
	 */
	public int getEffectiveAngle()
	{
		return effectiveAngle;
	}
	
	/**
	 * Retrieves the current area direction.<br>
	 * This value determines how the skill effect is oriented in the game world.
	 * @return The {@code AreaDirections} value for this property.
	 */
	public AreaDirections getDirection()
	{
		return direction;
	}
	
	/**
	 * Retrieves the species of targets affected by this property.<br>
	 * This method returns the {@code targetSpecies} attribute.
	 * @return the {@link TargetSpeciesAttribute} value.
	 */
	public TargetSpeciesAttribute getTargetSpecies()
	{
		return targetSpecies;
	}
	
	public enum CastState
	{
		CAST_START(true),
		CAST_END(false);
		
		private final boolean isCastStart;
		
		CastState(boolean isCastStart)
		{
			this.isCastStart = isCastStart;
		}
		
		public boolean isCastStart()
		{
			return isCastStart;
		}
	}
}
