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

import java.util.Collection;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.player.motion.Motion;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles motion updates for characters in the game world.<br>
 * It synchronizes movement data between the server and the client.<br>
 * It extends {@link AionServerPacket} to transmit {@link Motion} information.
 * @author MrPoke
 */
public class SM_MOTION extends AionServerPacket
{
	byte action;
	short motionId;
	int remainingTime;
	int playerId;
	Map<Integer, Motion> activeMotions;
	Collection<Motion> motions;
	byte type;
	
	/**
	 * Creates a new {@link SM_MOTION} packet.<br>
	 * This constructor sets the action type to {@code 1}.<br>
	 * It initializes the packet with a collection of {@link Motion} objects.
	 * @param motions The collection of motions to include in the packet.
	 */
	public SM_MOTION(Collection<Motion> motions)
	{
		action = 1;
		this.motions = motions;
	}
	
	/**
	 * Creates a new {@code SM_MOTION} packet with a specific ID and time.<br>
	 * This constructor sets the action type to {@code 2}.
	 * @param motionId The unique identifier for the animation.
	 * @param remainingTime The amount of time left for the current motion.
	 */
	public SM_MOTION(short motionId, int remainingTime)
	{
		action = 2;
		this.motionId = motionId;
		this.remainingTime = remainingTime;
	}
	
	/**
	 * Creates a new {@code SM_MOTION} packet.<br>
	 * This constructor sets the action to {@code 5}.<br>
	 * It initializes the motion data for the server.
	 * @param motionId The unique identifier for the motion.
	 * @param type The specific type of the motion.
	 */
	public SM_MOTION(short motionId, byte type)
	{
		action = 5;
		this.motionId = motionId;
		this.type = type;
	}
	
	/**
	 * Creates a new {@code SM_MOTION} packet with a specific motion identifier.<br>
	 * This constructor sets the action type to {@code 6}.
	 * @param motionId The unique identifier for the motion.
	 */
	public SM_MOTION(short motionId)
	{
		action = 6;
		this.motionId = motionId;
	}
	
	/**
	 * Creates a new {@link SM_MOTION} packet for a specific player.<br>
	 * This constructor sets the action type to {@code 7}.<br>
	 * It maps the provided motions to the correct player ID.
	 * @param playerId The unique identifier of the player.
	 * @param activeMotions A map containing the current motion data for the player.
	 */
	public SM_MOTION(int playerId, Map<Integer, Motion> activeMotions)
	{
		action = 7;
		this.playerId = playerId;
		this.activeMotions = activeMotions;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(action);
		switch (action)
		{
			case 1:
			{
				writeH(motions.size());
				for (Motion motion : motions)
				{
					writeH(motion.getId());
					writeD(motion.getRemainingTime());
					writeC(motion.isActive() ? 1 : 0);
				}
				break;
			}
			case 2:
			{
				// Add motion
				writeH(motionId);
				writeD(remainingTime);
				break;
			}
			case 5:
			{
				// Set motion
				writeH(motionId);
				writeC(type);
				break;
			}
			case 6:
			{
				// remove
				writeH(motionId);
				break;
			}
			case 7:
			{
				// Player motions
				writeD(playerId);
				for (int i = 1; i < 6; i++)
				{
					final Motion motion = activeMotions.get(i);
					if (motion == null)
					{
						writeH(0);
					}
					else
					{
						writeH(motion.getId());
					}
				}
				break;
			}
			default:
				break;
		}
	}
}
