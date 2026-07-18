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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the transmission of custom settings between the server and the client.<br>
 * It allows for the synchronization of user-specific configuration data.
 * @author Sweetkr
 */
public class SM_CUSTOM_SETTINGS extends AionServerPacket
{
	private final Integer obj;
	private int unk = 0;
	private final int display;
	private final int deny;
	
	/**
	 * Creates a new {@code SM_CUSTOM_SETTINGS} packet for a specific player.<br>
	 * This constructor retrieves the display and deny settings from the {@link Player}.
	 * @param player The {@code Player} object used to initialize the packet data.
	 */
	public SM_CUSTOM_SETTINGS(Player player)
	{
		this(player.getObjectId(), 1, player.getPlayerSettings().getDisplay(), player.getPlayerSettings().getDeny());
	}
	
	/**
	 * Creates a new {@code SM_CUSTOM_SETTINGS} packet.<br>
	 * This constructor initializes the settings for a specific game object.
	 * @param objectId The unique identifier of the object.
	 * @param unk An unknown value used by the server.
	 * @param display The value to be displayed in the game world.
	 * @param deny The value used to determine if an action is denied.
	 */
	public SM_CUSTOM_SETTINGS(int objectId, int unk, int display, int deny)
	{
		obj = objectId;
		this.display = display;
		this.deny = deny;
		this.unk = unk;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(obj);
		writeC(unk); // unk
		writeH(display);
		writeH(deny);
	}
}
