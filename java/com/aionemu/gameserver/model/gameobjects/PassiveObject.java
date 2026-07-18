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

import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.HousingPassiveItem;

/**
 * Represents a passive object within a {@link House}.<br>
 * This class handles objects that do not have active interactions but exist as part of the environment.<br>
 * It extends {@link HouseObject} using a {@code HousingPassiveItem} template.
 * @author Rolandas
 */
public class PassiveObject extends HouseObject<HousingPassiveItem>
{
	/**
	 * Creates a new instance of a {@link PassiveObject}.<br>
	 * This constructor initializes the object with its owner and unique identifiers.
	 * @param owner The {@link House} that owns this object.
	 * @param objId The unique identifier for the specific object instance.
	 * @param templateId The ID of the template used to define this object.
	 */
	public PassiveObject(House owner, int objId, int templateId)
	{
		super(owner, objId, templateId);
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
