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

import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the synchronization of auto-group information.<br>
 * It is sent to clients to update their local state regarding active {@link AutoGroupType} groups.
 * @author SheppeR, Guapo, nrg
 */
public class SM_AUTO_GROUP extends AionServerPacket
{
	private byte windowId;
	private final int instanceMaskId;
	private final int mapId;
	private final int messageId;
	private final int titleId;
	private int waitTime;
	private boolean close;
	String name = "";
	public static final byte wnd_EntryIcon = 6;
	
	/**
	 * Creates a new {@code SM_AUTO_GROUP} packet.<br>
	 * This method initializes the packet using an instance mask ID.<br>
	 * It automatically retrieves the group type and its associated IDs.
	 * @param instanceMaskId The unique identifier for the auto group.
	 */
	public SM_AUTO_GROUP(int instanceMaskId)
	{
		final AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
		if (agt == null)
		{
			throw new IllegalArgumentException("Auto Groups Type no found for Instance MaskId: " + instanceMaskId);
		}
		
		this.instanceMaskId = instanceMaskId;
		messageId = agt.getNameId();
		titleId = agt.getTittleId();
		mapId = agt.getInstanceMapId();
	}
	
	/**
	 * Creates a new {@code SM_AUTO_GROUP} packet with a specific instance mask and window ID.<br>
	 * This constructor initializes the basic properties required for auto-grouping data.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 * @param windowId The identifier for the UI window.
	 */
	public SM_AUTO_GROUP(int instanceMaskId, Number windowId)
	{
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
	}
	
	/**
	 * Creates a new {@code SM_AUTO_GROUP} packet.<br>
	 * This constructor sets the instance mask, window ID, and closing status.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 * @param windowId The ID of the UI window to display.
	 * @param close A boolean indicating whether the window should be closed.
	 */
	public SM_AUTO_GROUP(int instanceMaskId, Number windowId, boolean close)
	{
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
		this.close = close;
	}
	
	/**
	 * Creates a new {@code SM_AUTO_GROUP} packet with specific details.<br>
	 * This constructor initializes the group window and timing information.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 * @param windowId The ID of the window to display.
	 * @param waitTime The amount of time to wait before an action occurs.
	 * @param name The custom name assigned to this group.
	 */
	public SM_AUTO_GROUP(int instanceMaskId, Number windowId, int waitTime, String name)
	{
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
		this.waitTime = waitTime;
		this.name = name;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(instanceMaskId);
		writeC(windowId);
		writeD(mapId);
		switch (windowId)
		{
			case 0: // request entry
				writeD(messageId);
				writeD(titleId);
				writeD(0);
				break;
			case 1: // waiting window
				writeD(0);
				writeD(0);
				writeD(waitTime);
				break;
			case 2: // cancel looking
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case 3: // pass window
				writeD(0);
				writeD(0);
				writeD(waitTime);
				break;
			case 4: // enter window
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case 5: // after you click enter
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case wnd_EntryIcon: // entry icon
				writeD(messageId);
				writeD(titleId);
				writeD(close ? 0 : 1);
				break;
			case 7: // failed window
				writeD(messageId);
				writeD(titleId);
				writeD(0);
				break;
			case 8: // on login
				writeD(0);
				writeD(0);
				writeD(waitTime);
				break;
		}
		
		writeC(0);
		writeS(name);
	}
}
