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
package com.aionemu.gameserver.model.templates.itemset;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.stats.calc.StatOwner;

/**
 * Represents a template for an item set in the game database.<br>
 * It defines the collection of items that provide specific bonuses to a character.<br>
 * This class implements {@link StatOwner} to manage associated statistics.
 * @author ATracer, modified by Antivirus
 */
@XmlRootElement(name = "itemset")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemSetTemplate implements StatOwner
{
	@XmlElement(required = true)
	protected List<ItemPart> itempart;
	@XmlElement(required = true)
	protected List<PartBonus> partbonus;
	protected FullBonus fullbonus;
	@XmlAttribute
	protected String name;
	@XmlAttribute
	protected int id;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It updates the {@code fullbonus} object based on the size of the {@code itempart} list.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (fullbonus != null)
		{
			// Set number of items to apply the full bonus
			fullbonus.setNumberOfItems(itempart.size());
		}
	}
	
	/**
	 * Retrieves the list of {@link ItemPart} objects associated with this template.<br>
	 * This method returns all parts that make up the current item set.
	 * @return a {@code List} of {@code ItemPart} objects.
	 */
	public List<ItemPart> getItempart()
	{
		return itempart;
	}
	
	/**
	 * Retrieves the list of bonuses for this item set.<br>
	 * This method returns all {@link PartBonus} objects associated with the template.
	 * @return a {@code List} of {@code PartBonus} objects.
	 */
	public List<PartBonus> getPartbonus()
	{
		return partbonus;
	}
	
	/**
	 * Retrieves the {@code FullBonus} associated with this item set.<br>
	 * This method returns the bonus data for the complete set.
	 * @return the {@link FullBonus} object.
	 */
	public FullBonus getFullbonus()
	{
		return fullbonus;
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
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
