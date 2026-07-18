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
 * Represents the Gold Points (GP) data for a {@link Player} during siege events.<br>
 * This class is used to track and compare player rankings based on their accumulated points.
 * @author Ever
 */
public class PlayerGP implements Comparable<PlayerGP>
{
	private final Player player;
	private final Race race;
	private int gp;
	
	/**
	 * Creates a new {@link PlayerGP} instance for a specific player.<br>
	 * This constructor initializes the race from the provided {@code Player}.<br>
	 * The initial GP value is set to {@code 0}.
	 * @param player The {@code Player} object used to initialize this instance.
	 */
	public PlayerGP(Player player)
	{
		this.player = player;
		race = player.getRace();
		gp = 0;
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
	 * Retrieves the current GP value for the player.<br>
	 * This method returns the integer stored in the {@code gp} field.
	 * @return The current GP amount as an {@code int}.
	 */
	public int getGP()
	{
		return gp;
	}
	
	/**
	 * Increases the current {@code gp} value.<br>
	 * This method adds the specified amount to the existing total.
	 * @param gp The amount of {@code gp} to add.
	 */
	public void increaseGP(int gp)
	{
		this.gp += gp;
	}
	
	/**
	 * Compares this {@code PlayerGP} object with another {@code PlayerGP} object.<br>
	 * It determines the order based on the {@code gp} value.
	 * @param pl The other {@code PlayerGP} object to compare against.
	 * @return A positive integer if this GP is higher, a negative integer if lower, or zero if equal.
	 */
	@Override
	public int compareTo(PlayerGP pl)
	{
		return gp - pl.gp;
	}
}
