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
package com.aionemu.gameserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.road.Road;
import com.aionemu.gameserver.model.templates.road.RoadTemplate;
import com.aionemu.gameserver.world.World;

/**
 * This service handles the logic and management of roads within the game world.<br>
 * It provides methods to interact with {@link Road} objects and their corresponding {@link RoadTemplate} data.
 * @author SheppeR
 */
public class RoadService
{
	Logger log = LoggerFactory.getLogger(RoadService.class);
	
	private static class SingletonHolder
	{
		protected static final RoadService instance = new RoadService();
	}
	
	/**
	 * Provides access to the singleton instance of {@link RoadService}.<br>
	 * Use this method to get the global service for road management.
	 * @return The single shared instance of {@code RoadService}.
	 */
	public static RoadService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link RoadService} class.<br>
	 * This method initializes all road data when the service starts.<br>
	 * It prevents direct instantiation of this class.
	 */
	private RoadService()
	{
		GameServer.log.info("[RoadService] started ..");
		for (RoadTemplate rt : DataManager.ROAD_DATA.getRoadTemplates())
		{
			for (Integer instanceId : World.getInstance().getWorldMap(rt.getMap()).getAvailableInstanceIds())
			{
				final Road r = new Road(rt, instanceId);
				r.spawn();
				log.debug("[RoadService] Added " + r.getName() + " at m=" + r.getWorldId() + ",x=" + r.getX() + ",y=" + r.getY() + ",z=" + r.getZ() + " [" + instanceId + "]");
			}
		}
	}
}
