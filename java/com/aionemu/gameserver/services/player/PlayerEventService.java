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
package com.aionemu.gameserver.services.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This service handles the execution of various in-game events related to {@link Player} actions.<br>
 * It manages event logic and ensures they are processed correctly within the game world.
 * @author Source
 */
public class PlayerEventService
{
	private static final Logger log = LoggerFactory.getLogger(PlayerEventService.class);
	
	/**
	 * This is a private constructor.<br>
	 * It initializes the background task for player events.<br>
	 * It schedules a recurring task using {@link ThreadPoolManager}.<br>
	 * This prevents other classes from creating new instances of {@code PlayerEventService}.
	 */
	private PlayerEventService()
	{
		final EventCollector visitor = new EventCollector();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				World.getInstance().doOnAllPlayers(visitor);
			}
		}, EventsConfig.EVENT_PERIOD * 60000, EventsConfig.EVENT_PERIOD * 60000);
	}
	
	private static final class EventCollector implements Visitor<Player>
	{
		@Override
		public void visit(Player player)
		{
			final int membership = player.getClientConnection().getAccount().getMembership();
			final int rate = EventsConfig.EVENT_REWARD_MEMBERSHIP_RATE ? membership + 1 : 1;
			if (membership >= EventsConfig.EVENT_REWARD_MEMBERSHIP)
			{
				try
				{
					if (player.getInventory().isFull())
					{
						log.warn("[EventReward] player " + player.getName() + " tried to receive item with full inventory.");
					}
					else
					{
						ItemService.addItem(player, (player.getRace() == Race.ELYOS ? EventsConfig.EVENT_ITEM_ELYOS : EventsConfig.EVENT_ITEM_ASMO), EventsConfig.EVENT_ITEM_COUNT * rate);
					}
				}
				catch (Exception ex)
				{
					log.error("Exception during event rewarding of player " + player.getName(), ex);
				}
			}
		}
	}
	
	/**
	 * Gets the global instance of the {@link PlayerEventService}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the service from anywhere in your code.
	 * @return The single instance of {@code PlayerEventService}.
	 */
	public static PlayerEventService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerEventService instance = new PlayerEventService();
	}
}
