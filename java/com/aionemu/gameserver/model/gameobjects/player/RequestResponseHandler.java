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

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This class serves as a base for handling {@code CM_QUESTION_RESPONSE} packets.<br>
 * It defines the logic for processing responses from NPC interactions.<br>
 * Subclasses should implement specific behaviors based on the request type.
 * @author Ben
 * @modified Lyahim
 */
public abstract class RequestResponseHandler
{
	private final Creature requester;
	
	/**
	 * Creates a new instance of this handler.<br>
	 * It links the handler to a specific {@link Creature}.
	 * @param requester The {@code Creature} who sent the request.
	 */
	public RequestResponseHandler(Creature requester)
	{
		this.requester = requester;
	}
	
	/**
	 * Processes the response received from a {@link Player}.<br>
	 * It decides whether to accept or deny a request based on the {@code response} value.
	 * @param responder The {@code Player} who is providing the answer.
	 * @param response The integer code representing the player's choice.
	 */
	public void handle(Player responder, int response)
	{
		if (response == 0)
		{
			denyRequest(requester, responder);
		}
		else
		{
			acceptRequest(requester, responder);
		}
	}
	
	/**
	 * Called when the player accepts a request
	 * @param requester Creature whom requested this response
	 * @param responder Player whom responded to this request
	 */
	public abstract void acceptRequest(Creature requester, Player responder);
	
	/**
	 * Called when the player denies a request
	 * @param requester Creature whom requested this response
	 * @param responder Player whom responded to this request
	 */
	public abstract void denyRequest(Creature requester, Player responder);
}
