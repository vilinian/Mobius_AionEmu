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
package com.aionemu.gameserver.ai2;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a base request for the {@link Creature} AI system.<br>
 * This class serves as a template for handling various AI-related actions and commands.
 * @author ATracer
 */
public abstract class AI2Request
{
	public abstract void acceptRequest(Creature requester, Player responder);
	
	/**
	 * This method handles the rejection of a request.<br>
	 * It notifies the {@code requester} that their action was declined by the {@link Player}.
	 * @param requester The {@code Creature} who sent the initial request.
	 * @param responder The {@code Player} who is declining the request.
	 */
	public void denyRequest(Creature requester, Player responder)
	{
	}
}
