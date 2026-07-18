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
package com.aionemu.gameserver.model.drop;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a collection of items that can be dropped as a group.<br>
 * It handles the logic for determining which drops are awarded based on specific criteria.<br>
 * This class implements {@link DropCalculator} to facilitate loot generation.
 * @author MrPoke
 */
public class DropGroup implements DropCalculator
{
	protected List<Drop> drop;
	protected Race race = Race.PC_ALL;
	protected Boolean useCategory = true;
	protected String group_name;
	
	/**
	 * Creates a new {@link DropGroup} instance.<br>
	 * This constructor initializes the drop list, race requirements, and category settings.
	 * @param drop The list of {@code Drop} objects to include in this group.
	 * @param race The {@link Race} type allowed for this group.
	 * @param useCategory A {@code Boolean} indicating if categories should be used.
	 * @param group_name The unique name assigned to this group.
	 */
	public DropGroup(List<Drop> drop, Race race, Boolean useCategory, String group_name)
	{
		this.drop = drop;
		this.race = race;
		this.useCategory = useCategory;
		this.group_name = group_name;
	}
	
	/**
	 * Retrieves the list of drops associated with this {@link DropGroup}.<br>
	 * This method returns the internal {@code drop} list.
	 * @return a {@code List} of {@link Drop} objects.
	 */
	public List<Drop> getDrop()
	{
		return drop;
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
	 * Checks if the category filter is currently enabled.<br>
	 * This determines how drops are grouped during calculation.
	 * @return {@code true} if categories are used, {@code false} otherwise.
	 */
	public Boolean isUseCategory()
	{
		return useCategory;
	}
	
	/**
	 * Retrieves the name of the drop group.<br>
	 * Returns an empty {@code String} if the name is {@code null}.
	 * @return The name of the group as a {@code String}.
	 */
	public String getGroupName()
	{
		if (group_name == null)
		{
			return "";
		}
		
		return group_name;
	}
	
	/**
	 * Calculates and adds items to a drop result set based on random chance.<br>
	 * It considers modifiers, player races, and group membership rules.
	 * @param result The {@code Set<DropItem>} to store the generated drops.
	 * @param index The current position in the drop sequence.
	 * @param dropModifier A multiplier applied to the base drop chance.
	 * @param race The {@link Race} of the player triggering the drop.
	 * @param groupMembers A {@code Collection<Player>} representing the current party.
	 * @return The updated index after adding new items.
	 */
	@Override
	public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, Collection<Player> groupMembers)
	{
		if (useCategory)
		{
			final Drop d = drop.get(Rnd.get(0, drop.size() - 1));
			return d.dropCalculator(result, index, dropModifier, race, groupMembers);
		}
		
		for (int i = 0; i < drop.size(); i++)
		{
			final Drop d = drop.get(i);
			index = d.dropCalculator(result, index, dropModifier, race, groupMembers);
		}
		
		return index;
	}
}
