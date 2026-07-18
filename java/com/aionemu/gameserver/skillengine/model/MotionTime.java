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
package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.Race;

/**
 * Represents the timing data for a character's motion during skill execution.<br>
 * This class is used by the {@link com.aionemu.gameserver.skillengine.SkillEngine} to manage animation sequences.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "motion_time", propOrder =
{
	"am",
	"af",
	"em",
	"ef"
})
public class MotionTime
{
	protected Times am;
	protected Times af;
	protected Times em;
	protected Times ef;
	@XmlAttribute(required = true)
	protected String name; // TODO enum
	
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
	 * Retrieves the {@code am} motion time.<br>
	 * This value is used for specific character types.
	 * @return the {@code Times} object representing the {@code am} value.
	 */
	public Times getAm()
	{
		return am;
	}
	
	/**
	 * Sets the {@code am} value for this motion time.<br>
	 * This updates the internal state of the {@link MotionTime} object.
	 * @param am The {@code Times} object to assign.
	 */
	public void setAm(Times am)
	{
		this.am = am;
	}
	
	/**
	 * Retrieves the {@code af} motion time.<br>
	 * This method returns the {@link Times} object associated with the {@code af} field.
	 * @return The {@code af} motion time value.
	 */
	public Times getAf()
	{
		return af;
	}
	
	/**
	 * Sets the {@code af} value for this motion time.<br>
	 * This updates the internal state of the {@link MotionTime} object.
	 * @param af The new {@code Times} value to assign.
	 */
	public void setAf(Times af)
	{
		this.af = af;
	}
	
	/**
	 * Retrieves the motion time for male characters.<br>
	 * This method returns the {@code em} field from this object.
	 * @return the {@link Times} object representing male motion time.
	 */
	public Times getEm()
	{
		return em;
	}
	
	/**
	 * Sets the {@code em} value for this motion time.<br>
	 * This updates the internal state of the {@link MotionTime} object.
	 * @param em The {@code Times} object to set.
	 */
	public void setEm(Times em)
	{
		this.em = em;
	}
	
	/**
	 * Retrieves the motion time for the female elf character type.<br>
	 * This method returns the {@code Times} object associated with the {@code ef} field.
	 * @return The {@code Times} data for female elves.
	 */
	public Times getEf()
	{
		return ef;
	}
	
	/**
	 * Sets the {@code ef} value for this motion time.<br>
	 * This updates the internal state of the {@link MotionTime} object.
	 * @param ef The new {@code Times} value to assign.
	 */
	public void setEf(Times ef)
	{
		this.ef = ef;
	}
	
	/**
	 * Sets the name.
	 * @param name The new name to assign.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Retrieves the specific {@link Times} for a given race and gender.<br>
	 * This method checks the {@code race} and {@code gender} to return the correct motion data.<br>
	 * It returns {@code null} if the combination is not supported.
	 * @param race The {@link Race} of the character.
	 * @param gender The {@link Gender} of the character.
	 * @return The corresponding {@link Times} object or {@code null}.
	 */
	public Times getTimes(Race race, Gender gender)
	{
		switch (race)
		{
			case ASMODIANS:
				if (gender == Gender.MALE)
				{
					return getAm();
				}
				
				return getAf();
			case ELYOS:
				if (gender == Gender.MALE)
				{
					return getEm();
				}
				
				return getEf();
			default:
				break;
			
		}
		
		return null;
	}
	
	/**
	 * Calculates the motion time for a specific weapon.<br>
	 * This method uses the {@link Race}, {@link Gender}, and {@link WeaponTypeWrapper} to find the correct value.<br>
	 * It returns {@code 0} if no matching data is found.
	 * @param race The character's race type.
	 * @param gender The character's gender.
	 * @param weapon The specific weapon being used.
	 * @return The motion time as an integer.
	 */
	public int getTimeForWeapon(Race race, Gender gender, WeaponTypeWrapper weapon)
	{
		switch (race)
		{
			case ASMODIANS:
				if (gender == Gender.MALE)
				{
					return getAm().getTimeForWeapon(weapon);
				}
				
				return getAf().getTimeForWeapon(weapon);
			case ELYOS:
				if (gender == Gender.MALE)
				{
					return getEm().getTimeForWeapon(weapon);
				}
				
				return getEf().getTimeForWeapon(weapon);
			default:
				break;
			
		}
		
		return 0;
	}
}
