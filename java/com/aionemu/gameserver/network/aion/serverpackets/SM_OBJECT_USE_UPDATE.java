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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.PostboxObject;
import com.aionemu.gameserver.model.gameobjects.StorageObject;
import com.aionemu.gameserver.model.gameobjects.UseableItemObject;
import com.aionemu.gameserver.model.templates.housing.UseItemAction;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet updates the client on the result of interacting with a world object.<br>
 * It is used to synchronize state changes for {@link HouseObject}, {@link PostboxObject}, and other useable entities.
 * @author Rolandas
 */
public class SM_OBJECT_USE_UPDATE extends AionServerPacket
{
	private final int usingPlayerId;
	private final int ownerPlayerId;
	private final int useCount;
	private UseItemAction action = null;
	HouseObject<?> object;
	
	/**
	 * Updates the status of a house object being used by a player.<br>
	 * This packet synchronizes the usage count and owner information.
	 * @param usingPlayerId The unique ID of the player currently using the object.
	 * @param ownerPlayerId The unique ID of the player who owns the object.
	 * @param useCount The current number of times the object has been used.
	 * @param object The {@link HouseObject} instance being updated.
	 */
	public SM_OBJECT_USE_UPDATE(int usingPlayerId, int ownerPlayerId, int useCount, HouseObject<?> object)
	{
		this.usingPlayerId = usingPlayerId;
		this.ownerPlayerId = ownerPlayerId;
		this.useCount = useCount;
		this.object = object;
		if (object instanceof UseableItemObject)
		{
			action = ((UseableItemObject) object).getObjectTemplate().getAction();
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(object.getObjectTemplate().getTypeId());
		if ((object instanceof PostboxObject) || (object instanceof StorageObject))
		{
			writeD(usingPlayerId);
			writeC(1); // unk
			writeD(object.getObjectId());
		}
		else if (object instanceof UseableItemObject)
		{
			writeD(usingPlayerId);
			writeD(ownerPlayerId);
			writeD(object.getObjectId());
			writeD(useCount);
			int checkType = 0;
			if ((action != null) && (action.getCheckType() != null))
			{
				checkType = action.getCheckType();
			}
			
			writeC(checkType);
		}
	}
}
