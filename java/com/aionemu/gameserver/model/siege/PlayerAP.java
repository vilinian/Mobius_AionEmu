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
package com.aionemu.gameserver.model.siege;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents the Attack Power (AP) statistics for a {@link Player} during siege activities.<br>
 * This class is used to compare and manage player combat strength in specific game modes.
 * @author antness
 */
public class PlayerAP implements Comparable<PlayerAP>
{
	private final Player player;
	private final Race race;
	private int ap;
	
	/**
	 * Creates a new {@code PlayerAP} instance for a specific player.<br>
	 * This method initializes the {@code race} from the provided {@link Player}.<br>
	 * The initial {@code ap} value is set to 0.
	 * @param player The {@code Player} object used to initialize this instance.
	 */
	public PlayerAP(Player player)
	{
		this.player = player;
		race = player.getRace();
		ap = 0;
	}
	
	/**
	 * Retrieves the {@link Player} who initiated this wedding.<br>
	 * This method returns the primary participant of the ceremony.
	 * @return The {@code Player} object representing the main character.
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the current AP value for the player.<br>
	 * This method returns the integer stored in the {@code ap} field.
	 * @return The current AP value as an {@code int}.
	 */
	public int getAP()
	{
		return ap;
	}
	
	/**
	 * Increases the current {@code ap} value.<br>
	 * This method adds the specified amount to the existing total.
	 * @param ap The amount of {@code ap} to add.
	 */
	public void increaseAP(int ap)
	{
		this.ap += ap;
	}
	
	/**
	 * Compares this {@code PlayerAP} object with another {@code PlayerAP} object.<br>
	 * This method is used to determine the order of players based on their AP values.
	 * @param pl The other {@code PlayerAP} object to compare against.
	 * @return A positive integer if this AP is greater, a negative integer if it is smaller, or zero if they are equal.
	 */
	@Override
	public int compareTo(PlayerAP pl)
	{
		return ap - pl.ap;
	}
}
