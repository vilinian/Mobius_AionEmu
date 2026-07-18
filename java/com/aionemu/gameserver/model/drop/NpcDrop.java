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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents the loot items that an {@link com.aionemu.gameserver.model.Race} NPC can drop.<br>
 * This class handles the logic for calculating drops based on specific game rules.<br>
 * It implements the {@link DropCalculator} interface to provide consistent drop behavior.
 * @author MrPoke
 */
public class NpcDrop implements DropCalculator
{
	protected List<DropGroup> dropGroup;
	protected int npcId;
	
	/**
	 * Creates a new {@link NpcDrop} instance.<br>
	 * This constructor initializes the loot data for a specific NPC.
	 * @param dropGroup The list of {@code DropGroup} objects to associate with this NPC.
	 * @param npcId The unique identifier for the NPC.
	 */
	public NpcDrop(List<DropGroup> dropGroup, int npcId)
	{
		super();
		this.dropGroup = dropGroup;
		this.npcId = npcId;
	}
	
	/**
	 * Retrieves the list of {@link DropGroup} objects for this NPC.<br>
	 * If no groups exist, it returns an empty {@code List}.
	 * @return a {@code List} of {@link DropGroup} objects.
	 */
	public List<DropGroup> getDropGroup()
	{
		if (dropGroup == null)
		{
			return Collections.emptyList();
		}
		
		return dropGroup;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
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
		if ((dropGroup == null) || dropGroup.isEmpty())
		{
			return index;
		}
		
		for (DropGroup dg : dropGroup)
		{
			if ((dg.getRace() == Race.PC_ALL) || (dg.getRace() == race))
			{
				index = dg.dropCalculator(result, index, dropModifier, race, groupMembers);
			}
		}
		
		return index;
	}
}
