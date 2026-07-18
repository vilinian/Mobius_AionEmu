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
package com.aionemu.gameserver.model.templates.gather;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;

/**
 * Represents the base configuration for objects that can be gathered in the game world.<br>
 * This class defines the properties and behaviors of gatherable items like plants or minerals.<br>
 * It extends {@link VisibleObjectTemplate} to include common visual and spatial attributes.
 * @author ATracer, KID
 */
@XmlRootElement(name = "gatherable_template")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatherableTemplate extends VisibleObjectTemplate
{
	@XmlElement(required = true)
	protected Materials materials;
	@XmlElement(required = true)
	protected ExMaterials exmaterials;
	@XmlAttribute
	protected int id;
	@XmlAttribute
	protected String name;
	@XmlAttribute
	protected int nameId;
	@XmlAttribute
	protected String sourceType;
	@XmlAttribute
	protected int harvestCount;
	@XmlAttribute
	protected int skillLevel;
	@XmlAttribute
	protected int harvestSkill;
	@XmlAttribute
	protected int successAdj;
	@XmlAttribute
	protected int failureAdj;
	@XmlAttribute
	protected int aerialAdj;
	@XmlAttribute
	protected int captcha;
	@XmlAttribute
	protected int lvlLimit;
	@XmlAttribute
	protected int reqItem;
	@XmlAttribute
	protected int reqItemNameId;
	@XmlAttribute
	protected int checkType;
	@XmlAttribute
	protected int eraseValue;
	
	/**
	 * Retrieves the {@link Materials} associated with this template.<br>
	 * This method returns the list of items that can be gathered.
	 * @return the {@code Materials} object.
	 */
	public Materials getMaterials()
	{
		return materials;
	}
	
	/**
	 * Retrieves the extra materials associated with this template.<br>
	 * This method returns the {@code ExMaterials} object.
	 * @return the {@link ExMaterials} instance.
	 */
	public ExMaterials getExtraMaterials()
	{
		return exmaterials;
	}
	
	/**
	 * Retrieves the unique identifier for this gatherable template.<br>
	 * This value corresponds to the {@code id} field.
	 * @return The unique template ID as an {@code int}.
	 */
	@Override
	public int getTemplateId()
	{
		return id;
	}
	
	/**
	 * Retrieves the aerial adjustment value.<br>
	 * This value is used to modify gathering logic for aerial objects.
	 * @return The {@code int} value of the {@code aerialAdj} property.
	 */
	public int getAerialAdj()
	{
		return aerialAdj;
	}
	
	/**
	 * Retrieves the adjustment value for gathering failures.<br>
	 * This value is used to modify the outcome when a harvest fails.
	 * @return the {@code int} value of the {@code failureAdj} property.
	 */
	public int getFailureAdj()
	{
		return failureAdj;
	}
	
	/**
	 * Retrieves the success adjustment value.<br>
	 * This value modifies the success rate for gathering.
	 * @return the {@code int} value of {@code successAdj}
	 */
	public int getSuccessAdj()
	{
		return successAdj;
	}
	
	/**
	 * Retrieves the harvest skill value for this template.<br>
	 * This value is used to determine the success rate of gathering actions.
	 * @return the {@code int} value of the harvest skill.
	 */
	public int getHarvestSkill()
	{
		return harvestSkill;
	}
	
	/**
	 * Retrieves the current level of the skill.<br>
	 * This value is used to determine the power of the action performed by the {@link AbstractAI}.
	 * @return The integer level of the skill.
	 */
	public int getSkillLevel()
	{
		return skillLevel;
	}
	
	/**
	 * Retrieves the total number of items obtained from a harvest.<br>
	 * This value is stored in the {@code harvestCount} field.
	 * @return The amount of materials collected.
	 */
	public int getHarvestCount()
	{
		return harvestCount;
	}
	
	/**
	 * Retrieves the type of the source for this gatherable object.<br>
	 * This value is stored in the {@code sourceType} field.
	 * @return The {@code String} representing the source type.
	 */
	public String getSourceType()
	{
		return sourceType;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	@Override
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the current captcha rate for this template.<br>
	 * This value determines how often a captcha is triggered during gathering.
	 * @return The {@code int} value of the captcha rate.
	 */
	public int getCaptchaRate()
	{
		return captcha;
	}
	
	/**
	 * Retrieves the maximum level required to interact with this object.<br>
	 * This value is stored in the {@code lvlLimit} field.
	 * @return The minimum level limit as an {@code int}.
	 */
	public int getLevelLimit()
	{
		return lvlLimit;
	}
	
	/**
	 * Retrieves the unique identifier for the required item.<br>
	 * This value is used to check if a player has the necessary item.
	 * @return The {@code int} ID of the required item.
	 */
	public int getRequiredItemId()
	{
		return reqItem;
	}
	
	/**
	 * Retrieves the calculated ID for the required item name.<br>
	 * This value is derived from the {@code reqItemNameId} attribute.
	 * @return The calculated integer ID.
	 */
	public int getRequiredItemNameId()
	{
		return (reqItemNameId * 2) + 1;
	}
	
	/**
	 * Retrieves the type of check used for this template.<br>
	 * This value determines how the system validates gathering actions.
	 * @return the {@code int} value representing the check type.
	 */
	public int getCheckType()
	{
		return checkType;
	}
	
	/**
	 * Retrieves the value used for erasing. <br>
	 * This value is stored in the {@code eraseValue} field.
	 * @return The current {@code int} value of the erase property.
	 */
	public int getEraseValue()
	{
		return eraseValue;
	}
}
