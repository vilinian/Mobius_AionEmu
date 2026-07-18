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
package com.aionemu.gameserver.services.item;

import java.time.ZonedDateTime;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.ChairObject;
import com.aionemu.gameserver.model.gameobjects.EmblemObject;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.JukeBoxObject;
import com.aionemu.gameserver.model.gameobjects.MoveableObject;
import com.aionemu.gameserver.model.gameobjects.NpcObject;
import com.aionemu.gameserver.model.gameobjects.PassiveObject;
import com.aionemu.gameserver.model.gameobjects.PictureObject;
import com.aionemu.gameserver.model.gameobjects.PostboxObject;
import com.aionemu.gameserver.model.gameobjects.StorageObject;
import com.aionemu.gameserver.model.gameobjects.UseableItemObject;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.HousingChair;
import com.aionemu.gameserver.model.templates.housing.HousingEmblem;
import com.aionemu.gameserver.model.templates.housing.HousingJukeBox;
import com.aionemu.gameserver.model.templates.housing.HousingMoveableItem;
import com.aionemu.gameserver.model.templates.housing.HousingNpc;
import com.aionemu.gameserver.model.templates.housing.HousingPicture;
import com.aionemu.gameserver.model.templates.housing.HousingPostbox;
import com.aionemu.gameserver.model.templates.housing.HousingStorage;
import com.aionemu.gameserver.model.templates.housing.HousingUseableItem;
import com.aionemu.gameserver.model.templates.housing.PlaceableHouseObject;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.actions.SummonHouseObjectAction;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

/**
 * This factory class is responsible for creating instances of {@link HouseObject}.<br>
 * It handles the instantiation of various house-related objects based on their respective templates.<br>
 * Use this class to generate game objects like chairs, emblems, and storage units within a house.
 * @author Rolandas
 */
public final class HouseObjectFactory
{
	/**
	 * Creates a new {@link HouseObject} based on the provided house and template ID.<br>
	 * This method identifies the correct object type from the data manager.<br>
	 * It then instantiates the specific subclass for that object.
	 * @param house The {@link House} where the object will be placed.
	 * @param objectId The unique identifier for the new object instance.
	 * @param objectTemplateId The ID of the template used to define the object type.
	 * @return A new instance of a {@link HouseObject}.
	 */
	public static HouseObject<?> createNew(House house, int objectId, int objectTemplateId)
	{
		final PlaceableHouseObject template = DataManager.HOUSING_OBJECT_DATA.getTemplateById(objectTemplateId);
		if (template instanceof HousingChair)
		{
			return new ChairObject(house, objectId, template.getTemplateId());
		}
		else if (template instanceof HousingJukeBox)
		{
			return new JukeBoxObject(house, objectId, template.getTemplateId());
		}
		else if (template instanceof HousingMoveableItem)
		{
			return new MoveableObject(house, objectId, template.getTemplateId());
		}
		else if (template instanceof HousingNpc)
		{
			return new NpcObject(house, objectId, template.getTemplateId());
		}
		else if (template instanceof HousingPicture)
		{
			return new PictureObject(house, objectId, template.getTemplateId());
		}
		else if (template instanceof HousingPostbox)
		{
			return new PostboxObject(house, objectId, template.getTemplateId());
		}
		else if (template instanceof HousingStorage)
		{
			return new StorageObject(house, objectId, template.getTemplateId());
		}
		else if (template instanceof HousingUseableItem)
		{
			return new UseableItemObject(house, objectId, template.getTemplateId());
		}
		else if (template instanceof HousingEmblem)
		{
			return new EmblemObject(house, objectId, template.getTemplateId());
		}
		
		return new PassiveObject(house, objectId, template.getTemplateId());
	}
	
	/**
	 * Creates a new {@link HouseObject} based on the provided template.<br>
	 * This method validates the actions and sets expiration times if necessary.
	 * @param house The {@link House} where the object will be placed.
	 * @param itemTemplate The {@link ItemTemplate} used to define the object properties.
	 * @return A new instance of a {@link HouseObject}.
	 */
	public static HouseObject<?> createNew(House house, ItemTemplate itemTemplate)
	{
		if (itemTemplate.getActions() == null)
		{
			throw new IllegalArgumentException("template actions null");
		}
		
		final SummonHouseObjectAction action = itemTemplate.getActions().getHouseObjectAction();
		if (action == null)
		{
			throw new IllegalArgumentException("template actions miss SummonHouseObjectAction");
		}
		
		final int objectTemplateId = action.getTemplateId();
		final HouseObject<?> obj = createNew(house, IDFactory.getInstance().nextId(), objectTemplateId);
		if (obj.getObjectTemplate().getUseDays() > 0)
		{
			final int expireEnd = (int) (ZonedDateTime.now().plusDays(obj.getObjectTemplate().getUseDays()).toInstant().toEpochMilli() / 1000);
			obj.setExpireTime(expireEnd);
		}
		
		return obj;
	}
}
