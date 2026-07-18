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
package com.aionemu.gameserver.model.account;

/**
 * This class represents the information associated with a banned character.<br>
 * It stores details such as the ban reason and expiration time for a specific {@link com.aionemu.gameserver.model.account.Account}.
 * @author nrg
 */
public class CharacterBanInfo
{
	private final int playerId;
	private final long start;
	private final long end;
	private final String reason;
	
	/**
	 * Creates a new {@link CharacterBanInfo} object.<br>
	 * This constructor initializes the ban details for a specific player.<br>
	 * It automatically calculates the end time based on the duration.
	 * @param playerId The unique ID of the player being banned.
	 * @param start The timestamp when the ban begins.
	 * @param duration The length of time the ban lasts in milliseconds.
	 * @param reason The explanation for the ban. If {@code reason} is an empty string, a default message is used.
	 */
	public CharacterBanInfo(int playerId, long start, long duration, String reason)
	{
		this.playerId = playerId;
		this.start = start;
		end = duration + start;
		this.reason = (reason.equals("") ? "You are suspected to have violated the server's rules" : reason);
	}
	
	/**
	 * Retrieves the unique identifier for the player.<br>
	 * This value is stored in the {@code playerId} field.
	 * @return The {@code int} ID of the player.
	 */
	public int getPlayerId()
	{
		return playerId;
	}
	
	/**
	 * Retrieves the starting timestamp of the ban.<br>
	 * This value is stored as a {@code long}.
	 * @return The start time of the character ban.
	 */
	public long getStart()
	{
		return start;
	}
	
	/**
	 * Retrieves the timestamp when the ban expires.<br>
	 * This value is calculated based on the start time and duration.
	 * @return The {@code long} value representing the end of the ban.
	 */
	public long getEnd()
	{
		return end;
	}
	
	/**
	 * Retrieves the reason for the character ban.<br>
	 * This information is stored as a {@code String}.
	 * @return The reason string associated with this ban.
	 */
	public String getReason()
	{
		return reason;
	}
}
