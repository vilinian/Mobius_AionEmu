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
import com.aionemu.gameserver.model.templates.housing.HousingEmblem;

/**
 * Represents an emblem object within a {@link House}.<br>
 * This class handles the data and behavior for decorative emblems based on a {@code HousingEmblem} template.
 * @author Rolandas
 */
public class EmblemObject extends HouseObject<HousingEmblem>
{
	/**
	 * Creates a new instance of an {@link EmblemObject}.<br>
	 * This constructor initializes the object with its owner and unique identifiers.
	 * @param owner The {@link House} that owns this emblem.
	 * @param objId The unique identifier for the specific object instance.
	 * @param templateId The ID of the {@link HousingEmblem} template used to create it.
	 */
	public EmblemObject(House owner, int objId, int templateId)
	{
		super(owner, objId, templateId);
	}
	
	/**
	 * Checks if the emblem object is allowed to expire at this moment.<br>
	 * This method currently always returns {@code false}.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
