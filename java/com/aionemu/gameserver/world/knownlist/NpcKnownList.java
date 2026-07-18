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
package com.aionemu.gameserver.world.knownlist;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.world.MapRegion;

/**
 * This class manages the list of {@link VisibleObject} entities that a player has encountered.<br>
 * It tracks which NPCs are known to the player to handle visibility and awareness logic.
 * @author ATracer
 */
public class NpcKnownList extends CreatureAwareKnownList
{
	/**
	 * Creates a new {@link NpcKnownList} for a specific object.<br>
	 * This constructor initializes the list using the provided {@code owner}.
	 * @param owner The {@code VisibleObject} that owns this known list.
	 */
	public NpcKnownList(VisibleObject owner)
	{
		super(owner);
	}
	
	/**
	 * Updates the known list for the owner.<br>
	 * This method checks if the current {@link MapRegion} is active.<br>
	 * If it is active, it calls the superclass {@code doUpdate()} method.<br>
	 * Otherwise, it clears all entries in the list.
	 */
	@Override
	public void doUpdate()
	{
		final MapRegion activeRegion = owner.getActiveRegion();
		if ((activeRegion != null) && activeRegion.isMapRegionActive())
		{
			super.doUpdate();
		}
		else
		{
			clear();
		}
	}
}
