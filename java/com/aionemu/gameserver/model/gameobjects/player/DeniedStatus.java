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

/**
 * Represents the various reasons why a player action or request was rejected.<br>
 * This enum is used by {@link com.aionemu.gameserver.model.gameobjects.player.Player} to handle validation errors.
 * @author Sweetkr
 */
public enum DeniedStatus
{
	VIEW_DETAILS(1),
	TRADE(2),
	GROUP(4),
	GUILD(8),
	FRIEND(16),
	DUEL(32);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link DeniedStatus}.<br>
	 * This constructor assigns the unique identifier to the status.
	 * @param id The numeric value used to identify this specific status.
	 */
	private DeniedStatus(int id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
