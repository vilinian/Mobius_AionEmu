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
 * Represents the data template for a {@code Recipe} related to Luna.<br>
 * This class stores configuration details used by the game server to handle specific recipe logic.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LunaTemplate")
public class LunaTemplate
{
	protected List<LunaComponent> luna_component_panel_1;
	protected List<LunaComponent> luna_component_panel_2;
	protected List<LunaComponent> luna_component_panel_3;
	protected List<LunaComponent> luna_component_panel_4;
	protected List<LunaComponent> luna_component_panel_5;
	
	@XmlAttribute(name = "max_production_count")
	protected Integer maxProductionCount;
	
	@XmlAttribute(name = "name")
	protected String name;
	
	@XmlAttribute
	protected int quantity;
	
	@XmlAttribute
	protected int group;
	
	@XmlAttribute(name = "success_rate")
	protected int success_rate;
	
	@XmlAttribute
	protected int productid;
	
	@XmlAttribute
	protected Race race;
	
	@XmlAttribute
	protected int itemid;
	
	@XmlAttribute
	protected int nameid;
	
	@XmlAttribute
	protected int id;
	
	/**
	 * Retrieves the list of components for the first panel.<br>
	 * This method ensures that the {@code luna_component_panel_1} list is initialized.
	 * @return a {@code List} of {@link LunaComponent} objects.
	 */
	public List<LunaComponent> getLunaComponent()
	{
		if (luna_component_panel_1 == null)
		{
			luna_component_panel_1 = new ArrayList<>();
		}
		
		return luna_component_panel_1;
	}
	
	/**
	 * Retrieves the list of components for the second panel.<br>
	 * This method initializes an empty {@code ArrayList} if the list is {@code null}.
	 * @return a {@code List} of {@link LunaComponent} objects.
	 */
	public List<LunaComponent> getLunaComponent2()
	{
		if (luna_component_panel_2 == null)
		{
			luna_component_panel_2 = new ArrayList<>();
		}
		
		return luna_component_panel_2;
	}
	
	/**
	 * Retrieves the list of components for the third panel.<br>
	 * This method initializes an empty {@code ArrayList} if the list is {@code null}.
	 * @return a {@code List} of {@link LunaComponent} objects.
	 */
	public List<LunaComponent> getLunaComponent3()
	{
		if (luna_component_panel_3 == null)
		{
			luna_component_panel_3 = new ArrayList<>();
		}
		
		return luna_component_panel_3;
	}
	
	/**
	 * Retrieves the list of components for panel 4.<br>
	 * This method ensures that a new {@code ArrayList} is created if the list is currently {@code null}.
	 * @return A {@code List} of {@link LunaComponent} objects.
	 */
	public List<LunaComponent> getLunaComponent4()
	{
		if (luna_component_panel_4 == null)
		{
			luna_component_panel_4 = new ArrayList<>();
		}
		
		return luna_component_panel_4;
	}
	
	/**
	 * Retrieves the list of components for panel 5.<br>
	 * This method ensures the list is initialized if it is currently {@code null}.
	 * @return a {@code List} of {@link LunaComponent} objects.
	 */
	public List<LunaComponent> getLunaComponent5()
	{
		if (luna_component_panel_5 == null)
		{
			luna_component_panel_5 = new ArrayList<>();
		}
		
		return luna_component_panel_5;
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
	 * Retrieves the group identifier for this template.<br>
	 * This value is used to categorize different items.
	 * @return the {@code Integer} group value.
	 */
	public Integer getGroup()
	{
		return group;
	}
	
	/**
	 * Retrieves the gathering rate for this material.<br>
	 * This value determines how often an item is produced.
	 * @return The current {@code int} value of the rate.
	 */
	public int getRate()
	{
		return success_rate;
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
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
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
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
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
}
