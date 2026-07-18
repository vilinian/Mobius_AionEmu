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
import com.aionemu.gameserver.model.templates.housing.HousingJukeBox;

/**
 * Represents a jukebox object within a house.<br>
 * This class handles the logic for interacting with {@link HousingJukeBox} templates.<br>
 * It extends {@link HouseObject} to provide specific functionality for this furniture item.
 * @author Rolandas
 */
public class JukeBoxObject extends HouseObject<HousingJukeBox>
{
	/**
	 * Creates a new instance of a {@link JukeBoxObject}.<br>
	 * This constructor initializes the object with its owner and unique identifiers.
	 * @param owner The {@link House} that owns this object.
	 * @param objId The unique identifier for the specific object instance.
	 * @param templateId The ID of the template used to create this object.
	 */
	public JukeBoxObject(House owner, int objId, int templateId)
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
