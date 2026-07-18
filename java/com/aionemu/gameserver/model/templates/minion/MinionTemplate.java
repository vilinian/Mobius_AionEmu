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
package com.aionemu.gameserver.model.templates.minion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.TitleTemplate;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * Represents the base configuration and data for a minion entity.<br>
 * This template defines the attributes, stats, and behaviors used to create minions in the game world.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(namespace = "", name = "MinionTemplate")
public class MinionTemplate
{
	@XmlAttribute(name = "id", required = true)
	private int id;
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "nameid")
	private int name_id;
	@XmlAttribute(name = "grade")
	private String grade;
	@XmlAttribute(name = "grade_id")
	private int gradeId;
	@XmlAttribute(name = "level")
	private int level;
	@XmlAttribute(name = "growthPoints")
	private int growthPoints;
	@XmlAttribute(name = "growthMax")
	private int growthMax;
	@XmlAttribute(name = "growthCost")
	private int growthCost;
	@XmlElement(name = "modifiers", required = false)
	private ModifiersTemplate modifiers;
	@XmlElement(name = "actions")
	private MinionActions actions;
	@XmlElement(name = "minionstats")
	private MinionStatsTemplate statsTemplate;
	@XmlElement(name = "bound")
	private BoundRadius bound;
	@XmlElement(name = "evolved")
	private MinionEvolved evolved;
	@XmlElement(name = "nameId")
	private int nameId;
	@XmlElement(name = "physical_attr")
	protected List<MinionAttr> physicalAttr;
	@XmlElement(name = "magical_attr")
	protected List<MinionAttr> magicalAttr;
	
	/**
	 * Retrieves the list of physical attributes for this minion.<br>
	 * If no attributes exist, it returns an empty {@code ArrayList}.
	 * @return a {@code List} of {@link MinionAttr} objects.
	 */
	public List<MinionAttr> getPhysicalAttr()
	{
		if (physicalAttr == null)
		{
			physicalAttr = new ArrayList<>();
		}
		
		return physicalAttr;
	}
	
	/**
	 * Retrieves the list of magical attributes for this minion.<br>
	 * If no attributes exist, it returns an empty {@code ArrayList}.
	 * @return a {@code List} of {@link MinionAttr} objects.
	 */
	public List<MinionAttr> getMagicalAttr()
	{
		if (magicalAttr == null)
		{
			magicalAttr = new ArrayList<>();
		}
		
		return magicalAttr;
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
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the grade of the minion.<br>
	 * This value is stored as a {@code String}.
	 * @return The grade of the minion.
	 */
	public String getGrade()
	{
		return grade;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the current number of growth points for this minion.<br>
	 * This value represents the progress made toward leveling up.
	 * @return The total amount of {@code growthPoints}.
	 */
	public int getGrowthPoints()
	{
		return growthPoints;
	}
	
	/**
	 * Retrieves the maximum growth points for this minion.<br>
	 * This value represents the upper limit of growth a minion can achieve.
	 * @return the maximum number of {@code growthPoints}.
	 */
	public int getGrowthMax()
	{
		return growthMax;
	}
	
	/**
	 * Retrieves the cost required to grow a minion.<br>
	 * This value is stored in the {@code growthCost} field.
	 * @return The integer value representing the growth cost.
	 */
	public int getGrowthCost()
	{
		return growthCost;
	}
	
	/**
	 * Retrieves the {@link BoundRadius} associated with this minion.<br>
	 * This object defines the area where certain actions are restricted.
	 * @return The {@code BoundRadius} template for this minion.
	 */
	public BoundRadius getBoundRadius()
	{
		return bound;
	}
	
	/**
	 * Retrieves the evolution data for this minion.<br>
	 * This method returns the {@code MinionEvolved} object associated with the template.
	 * @return the {@code MinionEvolved} instance or {@code null} if no evolution exists.
	 */
	public MinionEvolved getEvolved()
	{
		return evolved;
	}
	
	/**
	 * Retrieves the statistics template for this minion.<br>
	 * This provides access to the base stats defined in the {@code statsTemplate} field.
	 * @return the {@link MinionStatsTemplate} associated with this minion.
	 */
	public MinionStatsTemplate getStatsTemplate()
	{
		return statsTemplate;
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
	 * Retrieves the unique identifier for the minion's grade.<br>
	 * This value corresponds to the {@code grade_id} attribute.
	 * @return the {@code int} value of the grade ID.
	 */
	public int getGradeId()
	{
		return gradeId;
	}
	
	/**
	 * Retrieves the list of stat modifiers for this {@link TitleTemplate}.<br>
	 * This method returns all active effects applied to the title.
	 * @return a {@code List} of {@link StatFunction} objects or {@code null} if no modifiers exist.
	 */
	public List<StatFunction> getModifiers()
	{
		if (modifiers != null)
		{
			return modifiers.getModifiers();
		}
		
		return null;
	}
	
	/**
	 * Retrieves the action configuration for this minion.<br>
	 * This method returns the {@code actions} object associated with the template.
	 * @return the {@link MinionActions} object containing minion behaviors.
	 */
	public MinionActions getAction()
	{
		return actions;
	}
}
