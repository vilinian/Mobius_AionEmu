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

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This packet handles the transmission of channel information to the client.<br>
 * It provides details about the current world state and active channels.
 * @author ATracer
 */
public class SM_CHANNEL_INFO extends AionServerPacket
{
	int instanceCount = 0;
	int currentChannel = 0;
	
	/**
	 * Creates a new {@code SM_CHANNEL_INFO} packet based on a specific location.<br>
	 * This method calculates the instance count and current channel for the given area.<br>
	 * It checks if the map is a beginner instance to determine the correct counts.
	 * @param position The {@link WorldPosition} used to identify the map and instance details.
	 */
	public SM_CHANNEL_INFO(WorldPosition position)
	{
		final WorldMapTemplate template = position.getWorldMapInstance().getTemplate();
		if (position.getWorldMapInstance().isBeginnerInstance())
		{
			instanceCount = template.getBeginnerTwinCount();
			if (WorldConfig.WORLD_EMULATE_FASTTRACK)
			{
				instanceCount += template.getTwinCount();
			}
			
			currentChannel = position.getInstanceId() - 1;
		}
		else
		{
			instanceCount = template.getTwinCount();
			if (WorldConfig.WORLD_EMULATE_FASTTRACK)
			{
				instanceCount += template.getBeginnerTwinCount();
			}
			
			currentChannel = position.getInstanceId() - 1;
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(currentChannel);
		writeD(instanceCount);
	}
}
