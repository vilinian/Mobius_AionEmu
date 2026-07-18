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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages the process of requesting and responding to {@code SM_QUESTION_WINDOW} requests.<br>
 * It handles the communication flow between players when a question window is triggered.
 * @author Ben
 */
public class ResponseRequester
{
	private final Player player;
	private final HashMap<Integer, RequestResponseHandler> map = new HashMap<>();
	private static Logger log = LoggerFactory.getLogger(ResponseRequester.class);
	
	/**
	 * Creates a new instance of {@code ResponseRequester}.<br>
	 * This object manages requests for the current {@link Player}.
	 * @param player The {@code Player} associated with this requester.
	 */
	public ResponseRequester(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Registers a new request with a specific message ID.<br>
	 * This method links a {@code RequestResponseHandler} to an ID in the internal map.<br>
	 * It returns {@code false} if the ID is already being used.
	 * @param messageId The unique identifier for the request.
	 * @param handler The callback handler to process the response.
	 * @return {@code true} if the request was added successfully, or {@code false} otherwise.
	 */
	public synchronized boolean putRequest(int messageId, RequestResponseHandler handler)
	{
		if (map.containsKey(messageId))
		{
			return false;
		}
		
		map.put(messageId, handler);
		return true;
	}
	
	/**
	 * Processes a response for a specific request.<br>
	 * It finds the handler associated with the {@code messageId}.<br>
	 * If found, it removes the handler and executes its logic.
	 * @param messageId The unique identifier for the request.
	 * @param response The value provided in response to the request.
	 * @return {@code true} if a handler was found and executed, otherwise {@code false}.
	 */
	public synchronized boolean respond(int messageId, int response)
	{
		final RequestResponseHandler handler = map.get(messageId);
		if (handler != null)
		{
			map.remove(messageId);
			log.debug("RequestResponseHandler triggered for response code " + messageId + " from " + player.getName());
			handler.handle(player, response);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Clears all pending requests for the player.<br>
	 * This method calls {@code handle} on every {@link RequestResponseHandler} with a value of {@code 0}.<br>
	 * It then empties the internal map.
	 */
	public synchronized void denyAll()
	{
		for (RequestResponseHandler handler : map.values())
		{
			handler.handle(player, 0);
		}
		
		map.clear();
	}
}
