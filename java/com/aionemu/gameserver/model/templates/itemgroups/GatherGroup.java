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
package com.aionemu.gameserver.model.templates.itemgroups;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 *
 */

/**
 * <p/>
 * Java class for GatherGroup complex type.
 * <p/>
 * The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * 
 * <pre>
 * &lt;complexType name="BonusItemGroup">
 *   &lt;complexContent>
 *     &lt;extension base="{}BonusItemGroup">
 *       &lt;sequence>
 *         &lt;element name="item" type="{}IdLevelReward" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GatherGroup")
public class GatherGroup extends BonusItemGroup
{
	@XmlElement(name = "item")
	protected List<ItemRaceEntry> items;
	
	/**
	 * Retrieves the list of {@link ItemRaceEntry} objects.<br>
	 * This method returns a live reference to the internal list.<br>
	 * If the list is null, it initializes a new {@code ArrayList}.
	 * @return A {@code List} containing all items in this group.
	 */
	public List<ItemRaceEntry> getItems()
	{
		if (items == null)
		{
			items = new ArrayList<>();
		}
		
		return items;
	}
	
	/**
	 * Retrieves the list of rewards as an array.<br>
	 * This method converts the internal {@code List} of items into a new {@code ItemRaceEntry[]} array.
	 * @return An array containing all {@link ItemRaceEntry} objects.
	 */
	@Override
	public ItemRaceEntry[] getRewards()
	{
		return getItems().toArray(new ItemRaceEntry[0]);
	}
}
