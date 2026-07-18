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
package com.aionemu.gameserver.model.templates.portal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * Represents the path configuration for a portal in the game world.<br>
 * It defines where a player is transported to when using a specific portal.<br>
 * This class maps portal data to its destination coordinates and associated {@link com.aionemu.gameserver.model.Race} requirements.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalPath")
public class PortalPath
{
	@XmlElement(name = "portal_req")
	protected PortalReq portalReq;
	@XmlAttribute(name = "dialog")
	protected int dialog;
	@XmlAttribute(name = "loc_id")
	protected int locId;
	@XmlAttribute(name = "player_count")
	protected int playerCount;
	@XmlAttribute(name = "instance")
	protected boolean instance;
	@XmlAttribute(name = "siege_id")
	protected int siegeId;
	@XmlAttribute(name = "race")
	protected Race race = Race.PC_ALL;
	@XmlAttribute(name = "err_group")
	protected int errGroup;
	
	/**
	 * Retrieves the {@code PortalReq} object associated with this path.<br>
	 * This method returns the requirements needed to enter the portal.
	 * @return the {@code PortalReq} object or {@code null} if it is not set.
	 */
	public PortalReq getPortalReq()
	{
		return portalReq;
	}
	
	/**
	 * Retrieves the dialog ID associated with this portal path.<br>
	 * This value is used to identify which dialogue to display.
	 * @return The {@code int} value of the dialog.
	 */
	public int getDialog()
	{
		return dialog;
	}
	
	/**
	 * Sets the dialog ID for this portal path.<br>
	 * This value is used to identify which text should be displayed.
	 * @param value The unique identifier for the dialog.
	 */
	public void setDialog(int value)
	{
		dialog = value;
	}
	
	/**
	 * Retrieves the location identifier for this event drop.<br>
	 * This value corresponds to the {@code loc_id} attribute.
	 * @return The unique integer ID of the location.
	 */
	public int getLocId()
	{
		return locId;
	}
	
	/**
	 * Sets the unique location identifier for this portal.<br>
	 * This updates the {@code locId} field.
	 * @param value The new integer ID to assign.
	 */
	public void setLocId(int value)
	{
		locId = value;
	}
	
	/**
	 * Retrieves the current number of players.<br>
	 * This value is stored in the {@code playerCount} field.
	 * @return The total count of players as an {@code int}.
	 */
	public int getPlayerCount()
	{
		return playerCount;
	}
	
	/**
	 * Sets the maximum number of players allowed for this portal.<br>
	 * This updates the {@code playerCount} field.
	 * @param value The new player count to set.
	 */
	public void setPlayerCount(int value)
	{
		playerCount = value;
	}
	
	/**
	 * Checks if this portal is an instance.<br>
	 * Returns {@code true} if it is an instance.<br>
	 * Returns {@code false} otherwise.
	 * @return the instance status of the portal path.
	 */
	public boolean isInstance()
	{
		return instance;
	}
	
	/**
	 * Sets whether this portal is an instance.<br>
	 * Use {@code true} for instance portals and {@code false} for regular ones.
	 * @param value The boolean value to set for the instance property.
	 */
	public void setInstance(boolean value)
	{
		instance = value;
	}
	
	/**
	 * Retrieves the unique identifier for the siege. <br>
	 * This value is used to identify specific siege events.
	 * @return The {@code int} value of the siege ID.
	 */
	public int getSigeId()
	{
		return siegeId;
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
	 * Retrieves the error group identifier.<br>
	 * This value is used to categorize different types of errors.
	 * @return The {@code int} value of the error group.
	 */
	public int getErrGroup()
	{
		return errGroup;
	}
}
