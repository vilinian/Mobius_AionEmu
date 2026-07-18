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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.HousingMoveableItem;

/**
 * Represents an object within a house that can be moved by players.<br>
 * This class serves as the base model for all {@link HousingMoveableItem} instances.
 * @author Rolandas
 */
public class MoveableObject extends HouseObject<HousingMoveableItem>
{
	/**
	 * Creates a new instance of a {@link MoveableObject}.<br>
	 * This constructor initializes the object with its owner and unique identifiers.
	 * @param owner The {@link House} that owns this object.
	 * @param objId The unique identifier for this specific object instance.
	 * @param templateId The ID of the {@code HousingMoveableItem} template used to create it.
	 */
	public MoveableObject(House owner, int objId, int templateId)
	{
		super(owner, objId, templateId);
	}
	
	/**
	 * Handles the logic when a {@link Player} interacts with this chair.<br>
	 * This method is triggered by the use action.
	 * @param player The {@code Player} who used the object.
	 */
	@Override
	public void onUse(Player player)
	{
	}
	
	/**
	 * Checks if the chair object is allowed to expire at this moment.<br>
	 * This method currently always returns {@code true}.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		return true;
	}
}
