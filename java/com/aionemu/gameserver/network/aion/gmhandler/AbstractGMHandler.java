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
package com.aionemu.gameserver.network.aion.gmhandler;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Provides a base structure for handling Game Master (GM) commands.<br>
 * This abstract class serves as the foundation for all specific {@code GMHandler} implementations.
 * @author Magenik, Antraxx, Alcapwnd
 */
abstract public class AbstractGMHandler
{
	
	protected String params;
	protected Player admin;
	protected Player target;
	
	/**
	 * Creates a new instance of an {@link AbstractGMHandler}.<br>
	 * This constructor initializes the administrator and the command parameters.<br>
	 * It also automatically calls {@code getTarget} to set the target player.
	 * @param admin The {@code Player} who is executing the command.
	 * @param params The string containing the command arguments.
	 */
	public AbstractGMHandler(Player admin, String params)
	{
		this.admin = admin;
		this.params = params;
		getTarget();
	}
	
	/**
	 * Updates the {@code target} field based on the current selection.<br>
	 * This method checks if the admin's target is a {@link Player}.<br>
	 * If the target is not a player, it sets {@code target} to {@code null}.
	 */
	public void getTarget()
	{
		final VisibleObject t = admin.getTarget();
		if (t instanceof Player)
		{
			return;
		}
		
		target = null;
	}
	
	/**
	 * Verifies if a valid {@link Player} target has been selected.<br>
	 * It checks if the {@code target} variable is not {@code null}.<br>
	 * If no target exists, it sends an error message to the {@code admin}.
	 * @return {@code true} if a target is found, otherwise {@code false}.
	 */
	public boolean checkTarget()
	{
		if (target != null)
		{
			return true;
		}
		
		PacketSendUtility.sendMessage(admin, "Target not found or target is not an player");
		return false;
	}
	
}
