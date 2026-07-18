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
package com.aionemu.gameserver.controllers.observer;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This class serves as a base for handling events related to dialogue systems.<br>
 * It allows the server to observe and react to specific {@link Creature} or {@link Player} interactions during conversations.
 * @author nrg
 */
public abstract class DialogObserver extends ActionObserver
{
	private final Player responder;
	private final Creature requester;
	private final int maxDistance;
	
	/**
	 * Creates a new instance of {@link DialogObserver}.<br>
	 * This constructor initializes the observer with the required participants.<br>
	 * It sets up the interaction between the requester and the responder.
	 * @param requester The {@code Creature} who starts the dialog.
	 * @param responder The {@code Player} who receives the dialog.
	 * @param maxDistance The maximum distance allowed for the interaction.
	 */
	public DialogObserver(Creature requester, Player responder, int maxDistance)
	{
		super(ObserverType.MOVE);
		this.responder = responder;
		this.requester = requester;
		this.maxDistance = maxDistance;
	}
	
	/**
	 * Checks if the {@link Player} and {@link Creature} are within a valid distance.<br>
	 * It calls {@code Player)} if they are too far apart.
	 */
	@Override
	public void moved()
	{
		if (!MathUtil.isIn3dRange(responder, requester, maxDistance))
		{
			tooFar(requester, responder);
		}
	}
	
	/**
	 * Is called when player is too far away from dialog serving object
	 * @param requester
	 * @param responder
	 */
	public abstract void tooFar(Creature requester, Player responder);
}
