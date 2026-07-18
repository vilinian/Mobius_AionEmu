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
 * Represents the support system for players within the game world.<br>
 * This class handles various utility actions and interactions related to {@link Player} assistance.
 * @author paranaix
 */
public class Support
{
	private Player owner;
	private String summary = "";
	private String description = "";
	
	/**
	 * Creates a new {@code Support} object.<br>
	 * This constructor initializes the owner, summary, and description fields.
	 * @param owner The {@link Player} who owns this support.
	 * @param summary A short summary of the support.
	 * @param description A detailed description of the support.
	 */
	public Support(Player owner, String summary, String description)
	{
		this.owner = owner;
		this.summary = summary;
		this.description = description;
	}
	
	/**
	 * Creates a new {@link Support} object for a specific player.<br>
	 * This constructor initializes the owner and sets default empty strings for summary and description.
	 * @param owner The {@code Player} who owns this support.
	 */
	public Support(Player owner)
	{
		this(owner, "", "");
	}
	
	/**
	 * Retrieves the {@link Player} that owns this object.<br>
	 * This method casts the result of the parent class's owner retrieval to a {@code Player}.
	 * @return The {@code Player} associated with this object.
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * Retrieves the short summary of the support request.<br>
	 * This method returns the {@code summary} field as a {@code String}.
	 * @return The summary text associated with this support object.
	 */
	public String getSummary()
	{
		return summary;
	}
	
	/**
	 * Retrieves the detailed description of the support request.<br>
	 * This method returns the {@code String} stored in the {@code description} field.
	 * @return The description text as a {@code String}.
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Sets the {@link Player} who owns this support object.<br>
	 * This method updates the internal {@code owner} field.<br>
	 * It will throw an {@code IllegalArgumentException} if the provided value is {@code null}.
	 * @param owner The {@code Player} to be set as the owner.
	 */
	public void setOwner(Player owner)
	{
		if (owner == null)
		{
			throw new IllegalArgumentException("Owner cant be null");
		}
		
		this.owner = owner;
	}
	
	/**
	 * Updates the summary of the {@link Support} object.<br>
	 * This method sets the internal {@code summary} field to a new value.
	 * @param summary The new summary text to assign.
	 */
	public void setSummary(String summary)
	{
		this.summary = summary;
	}
	
	/**
	 * Updates the description of the {@link Support} object.<br>
	 * This method sets the internal {@code description} field to a new value.
	 * @param description The new text to set as the description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
}
