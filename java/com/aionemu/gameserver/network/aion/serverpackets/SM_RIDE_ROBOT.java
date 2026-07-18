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
 * This packet is sent to the client to synchronize the action of riding a robot.<br>
 * It handles the visual and logical state when a {@link Player} mounts a robotic entity.
 * @author Ever'
 */
public class SM_RIDE_ROBOT extends AionServerPacket
{
	private final Player player;
	private final int robotInfo;
	
	/**
	 * Creates a new {@code SM_RIDE_ROBOT} packet.<br>
	 * This packet handles the action of a player riding a robot.
	 * @param player The {@link Player} who is performing the action.
	 * @param robotInfo The unique identifier for the robot being ridden.
	 */
	public SM_RIDE_ROBOT(Player player, int robotInfo)
	{
		this.player = player;
		this.robotInfo = robotInfo;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(player.getObjectId());
		writeD(robotInfo);
	}
}
