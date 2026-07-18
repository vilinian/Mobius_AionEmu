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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;

/**
 * Manages the logic and operations related to {@link FlyRing} items.<br>
 * This service handles how players interact with flying rings within the game world.
 * @author xavier
 */
public class FlyRingService
{
	Logger log = LoggerFactory.getLogger(FlyRingService.class);
	
	private static class SingletonHolder
	{
		protected static final FlyRingService instance = new FlyRingService();
	}
	
	/**
	 * Provides access to the singleton instance of {@link FlyRingService}.<br>
	 * Use this method to get the global service for fly ring logic.
	 * @return The single shared instance of {@code FlyRingService}.
	 */
	public static FlyRingService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link FlyRingService} class.<br>
	 * This constructor initializes all fly ring data from the {@code DataManager}.<br>
	 * It prevents direct instantiation of this service.
	 */
	private FlyRingService()
	{
		for (FlyRingTemplate t : DataManager.FLY_RING_DATA.getFlyRingTemplates())
		{
			final FlyRing f = new FlyRing(t, 0);
			f.spawn();
			log.debug("Added " + f.getName() + " at m=" + f.getWorldId() + ",x=" + f.getX() + ",y=" + f.getY() + ",z=" + f.getZ());
		}
	}
}
