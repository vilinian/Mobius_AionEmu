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
package com.aionemu.gameserver.model;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a marriage relationship between two {@link Player} objects.<br>
 * This class manages the data and state associated with an in-game wedding.
 * @author synchro2
 */
public class Wedding
{
	private final Player player;
	private final Player partner;
	private final Player priest;
	private boolean accepted;
	
	/**
	 * Creates a new {@link Wedding} instance.<br>
	 * This method initializes the wedding with the required participants.
	 * @param player The first {@code Player} involved in the wedding.
	 * @param partner The second {@code Player} involved in the wedding.
	 * @param priest The {@code Player} who performs the ceremony.
	 */
	public Wedding(Player player, Player partner, Player priest)
	{
		super();
		this.player = player;
		this.partner = partner;
		this.priest = priest;
	}
	
	/**
	 * Sets the wedding status to accepted.<br>
	 * This updates the {@code accepted} field to {@code true}.<br>
	 * You can check the new status using <link href="#isAccepted()">{@code isAccepted}.
	 */
	public void setAccept()
	{
		accepted = true;
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
	 * Retrieves the {@link Player} who is the partner in this wedding.<br>
	 * This method returns the {@code partner} object associated with the current {@code Wedding}.
	 * @return The {@code Player} object representing the partner.
	 */
	public Player getPartner()
	{
		return partner;
	}
	
	/**
	 * Retrieves the {@link Player} who is performing the wedding ceremony.<br>
	 * This method returns the {@code priest} associated with this {@code Wedding}.
	 * @return The {@code Player} object representing the priest.
	 */
	public Player getPriest()
	{
		return priest;
	}
	
	/**
	 * Checks if the wedding has been accepted.<br>
	 * This method returns the current status of the {@code accepted} field.
	 * @return {@code true} if the wedding is accepted, {@code false} otherwise.
	 */
	public boolean isAccepted()
	{
		return accepted;
	}
}
