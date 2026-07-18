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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to indicate that a player is looking at a specific {@link VisibleObject}.<br>
 * It helps synchronize the visual focus of characters in the game world.
 * @author alexa026
 */
public class SM_LOOKATOBJECT extends AionServerPacket
{
	private final VisibleObject visibleObject;
	private int targetObjectId;
	private int heading;
	
	/**
	 * Creates a new {@code SM_LOOKATOBJECT} packet.<br>
	 * This method initializes the packet with data from a {@link VisibleObject}.<br>
	 * It sets the target ID and heading based on whether the object has a target.
	 * @param visibleObject The {@code VisibleObject} used to populate the packet data.
	 */
	public SM_LOOKATOBJECT(VisibleObject visibleObject)
	{
		this.visibleObject = visibleObject;
		if (visibleObject.getTarget() != null)
		{
			targetObjectId = visibleObject.getTarget().getObjectId();
			heading = Math.abs(128 - visibleObject.getTarget().getHeading());
		}
		else
		{
			targetObjectId = 0;
			heading = visibleObject.getHeading();
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(visibleObject.getObjectId());
		writeD(targetObjectId);
		writeC(heading);
	}
}
