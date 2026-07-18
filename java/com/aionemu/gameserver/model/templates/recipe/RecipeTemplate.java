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
package com.aionemu.gameserver.model.templates.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.mail.MailPart;

/**
 * Represents the data structure for a crafting recipe template.<br>
 * It defines the requirements and results for creating items within the game world.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecipeTemplate")
public class RecipeTemplate
{
	protected List<Component> component;
	protected List<ComboProduct> comboproduct;
	@XmlAttribute(name = "max_production_count")
	protected Integer maxProductionCount;
	@XmlAttribute(name = "craft_delay_time")
	protected Integer craftDelayTime;
	@XmlAttribute(name = "craft_delay_id")
	protected Integer craftDelayId;
	@XmlAttribute
	protected int quantity;
	@XmlAttribute
	protected int productid;
	@XmlAttribute
	protected int autolearn;
	@XmlAttribute
	protected int dp;
	@XmlAttribute
	protected int skillpoint;
	@XmlAttribute
	protected Race race;
	@XmlAttribute
	protected int skillid;
	@XmlAttribute
	protected int itemid;
	@XmlAttribute
	protected int nameid;
	@XmlAttribute
	protected int id;
	
	/**
	 * Retrieves the list of components required for this recipe.<br>
	 * If the list is {@code null}, a new {@code ArrayList} is created.<br>
	 * This method returns a reference to the live list.
	 * @return A {@code List} of {@link Component} objects.
	 */
	public List<Component> getComponent()
	{
		if (component == null)
		{
			component = new ArrayList<>();
		}
		
		return component;
	}
	
	/**
	 * Retrieves the item ID of a specific combo product.<br>
	 * This method uses a 1-based index to access the list.
	 * @param num The position of the product in the list.
	 * @return The {@code Integer} item ID, or {@code null} if not found.
	 */
	public Integer getComboProduct(int num)
	{
		if ((comboproduct == null) || (comboproduct.get(num - 1) == null))
		{
			return null;
		}
		
		return comboproduct.get(num - 1).getItemid();
	}
	
	/**
	 * Retrieves the total number of combo products.<br>
	 * This method returns {@code 0} if the {@code comboproduct} list is {@code null}.
	 * @return The size of the {@code comboproduct} list as an {@code Integer}.
	 */
	public Integer getComboProductSize()
	{
		if (comboproduct == null)
		{
			return 0;
		}
		
		return comboproduct.size();
	}
	
	/**
	 * Retrieves the total amount of this component.<br>
	 * This value is stored in the {@code quantity} field.
	 * @return The number of items as an {@code Integer}.
	 */
	public Integer getQuantity()
	{
		return quantity;
	}
	
	/**
	 * Retrieves the unique identifier for the product.<br>
	 * This value is stored in the {@code productid} field.
	 * @return the {@code Integer} ID of the product.
	 */
	public Integer getProductid()
	{
		return productid;
	}
	
	/**
	 * Retrieves the auto-learn status for this recipe.<br>
	 * This value determines if a skill is learned automatically.
	 * @return The {@code int} value representing the auto-learn state.
	 */
	public int getAutoLearn()
	{
		return autolearn;
	}
	
	/**
	 * Retrieves the DP value for this recipe.<br>
	 * This represents the skill points required or gained during production.
	 * @return The {@code Integer} value of the DP property.
	 */
	public Integer getDp()
	{
		return dp;
	}
	
	/**
	 * Retrieves the skill point value for this recipe.<br>
	 * This method returns the {@code int} value stored in the template.
	 * @return The number of skill points required.
	 */
	public Integer getSkillpoint()
	{
		return skillpoint;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this recipe.<br>
	 * This value is mapped from the {@code skillid} attribute.
	 * @return The {@code Integer} ID of the skill.
	 */
	public Integer getSkillid()
	{
		return skillid;
	}
	
	/**
	 * Retrieves the unique identifier for this component.<br>
	 * This value corresponds to the {@code itemid} field.
	 * @return The {@code Integer} ID of the item.
	 */
	public Integer getItemid()
	{
		return itemid;
	}
	
	/**
	 * Retrieves the unique identifier associated with the name.<br>
	 * This value is stored in the {@code nameid} field.
	 * @return The integer ID of the name.
	 */
	public int getNameid()
	{
		return nameid;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link MailPart}.<br>
	 * This value is used to distinguish different parts of a mail.
	 * @return The {@code Integer} ID of the part, or {@code null} if not set.
	 */
	public Integer getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the maximum number of items that can be produced.<br>
	 * This value is stored in the {@code maxProductionCount} field.
	 * @return the maximum production count as an {@code Integer}.
	 */
	public Integer getMaxProductionCount()
	{
		return maxProductionCount;
	}
	
	/**
	 * Retrieves the crafting delay time for this recipe.<br>
	 * This value represents how long it takes to complete a craft.
	 * @return The {@code Integer} value of the crafting delay time.
	 */
	public Integer getCraftDelayTime()
	{
		return craftDelayTime;
	}
	
	/**
	 * Retrieves the unique identifier for the crafting delay.<br>
	 * This value is used to determine specific animation or timing behaviors.
	 * @return the {@code craftDelayId} as an {@code Integer}.
	 */
	public Integer getCraftDelayId()
	{
		return craftDelayId;
	}
}
