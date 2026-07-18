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
package com.aionemu.gameserver.model.items;

import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * Represents a {@link ItemStone} specifically used for mana-related mechanics.<br>
 * This class handles the data and properties associated with Mana Stones in the game world.
 * @author ATracer
 */
public class ManaStone extends ItemStone
{
	private List<StatFunction> modifiers;
	
	/**
	 * Creates a new instance of a {@link ManaStone}.<br>
	 * This constructor initializes the stone and loads its modifiers from the data manager.
	 * @param itemObjId The unique object ID for this specific instance.
	 * @param itemId The template ID used to look up the stone's properties.
	 * @param slot The inventory or equipment slot where the stone is located.
	 * @param persistentState The state information for saving and loading the item.
	 */
	public ManaStone(int itemObjId, int itemId, int slot, PersistentState persistentState)
	{
		super(itemObjId, itemId, slot, persistentState);
		
		final ItemTemplate stoneTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if ((stoneTemplate != null) && (stoneTemplate.getModifiers() != null))
		{
			modifiers = stoneTemplate.getModifiers();
		}
	}
	
	/**
	 * Retrieves the list of stat modifiers for this {@link ManaStone}.<br>
	 * This method returns all active effects applied to the item.
	 * @return a {@code List} of {@link StatFunction} objects.
	 */
	public List<StatFunction> getModifiers()
	{
		return modifiers;
	}
	
	/**
	 * Retrieves the first modifier from the list of item modifiers.<br>
	 * This method checks if the {@code modifiers} list is not {@code null} and contains elements.<br>
	 * It returns the first element or {@code null} if no modifiers exist.
	 * @return The first {@link StatFunction} in the list, or {@code null}.
	 */
	public StatFunction getFirstModifier()
	{
		return ((modifiers != null) && (modifiers.size() > 0)) ? modifiers.get(0) : null;
	}
}
