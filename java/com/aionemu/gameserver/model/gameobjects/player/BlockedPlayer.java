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
 * Represents a {@link Player} object that has been restricted or blocked from certain actions.<br>
 * This class is used to manage the state of players who are currently under a block penalty.
 * @author Ben
 */
public class BlockedPlayer
{
	PlayerCommonData pcd;
	String reason;
	
	/**
	 * Creates a new {@link BlockedPlayer} instance.<br>
	 * This constructor initializes the player with an empty reason string.
	 * @param pcd The {@code PlayerCommonData} for the player being blocked.
	 */
	public BlockedPlayer(PlayerCommonData pcd)
	{
		this(pcd, "");
	}
	
	/**
	 * Creates a new {@link BlockedPlayer} instance.<br>
	 * This constructor initializes the player data and the block reason.
	 * @param pcd The {@code PlayerCommonData} of the blocked player.
	 * @param reason The string describing why the player was blocked.
	 */
	public BlockedPlayer(PlayerCommonData pcd, String reason)
	{
		this.pcd = pcd;
		this.reason = reason;
	}
	
	/**
	 * Retrieves the unique object identifier for this player.<br>
	 * This value is fetched from the underlying {@link PlayerCommonData}.
	 * @return The {@code int} ID of the player object.
	 */
	public int getObjId()
	{
		return pcd.getPlayerObjId();
	}
	
	/**
	 * Retrieves the name of the player.<br>
	 * This method returns the {@code String} name from the underlying {@link PlayerCommonData}.
	 * @return The name of the player as a {@code String}.
	 */
	public String getName()
	{
		return pcd.getName();
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
	
	/**
	 * Updates the reason for why the player is blocked.<br>
	 * This method is thread-safe because it is {@code synchronized}.
	 * @param reason The new {@code String} value to set as the block reason.
	 */
	public synchronized void setReason(String reason)
	{
		this.reason = reason;
	}
}
