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
package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.restriction.ItemCleanupTemplate;

/**
 * This class holds the data for cleaning up item restrictions.<br>
 * It maps XML configuration to {@link ItemCleanupTemplate} objects.<br>
 * Use this class to manage how specific items are handled during cleanup processes.
 * @author KID
 */
@XmlRootElement(name = "item_restriction_cleanups")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemRestrictionCleanupData
{
	@XmlElement(name = "cleanup")
	private List<ItemCleanupTemplate> bplist;
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return bplist.size();
	}
	
	/**
	 * Retrieves the list of cleanup templates.<br>
	 * This method returns all {@link ItemCleanupTemplate} objects stored in this data holder.
	 * @return a {@code List} containing the {@code ItemCleanupTemplate} objects.
	 */
	public List<ItemCleanupTemplate> getList()
	{
		return bplist;
	}
}
