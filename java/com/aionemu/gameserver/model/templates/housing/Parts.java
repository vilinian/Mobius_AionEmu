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
package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the individual components used to construct housing structures.<br>
 * This class defines the data model for various parts within the {@code housing} system.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Parts", propOrder =
{
	"fence",
	"garden",
	"frame",
	"outwall",
	"roof",
	"infloor",
	"inwall",
	"door"
})
public class Parts
{
	protected Integer fence;
	protected Integer garden;
	protected Integer frame;
	protected Integer outwall;
	protected Integer roof;
	protected int infloor;
	protected int inwall;
	protected int door;
	
	/**
	 * Retrieves the value of the {@code fence} property.<br>
	 * This method returns the current state of the fence component.
	 * @return The {@code Integer} value representing the fence.
	 */
	public Integer getFence()
	{
		return fence;
	}
	
	/**
	 * Retrieves the value of the garden property.<br>
	 * This method returns the current state of the garden.
	 * @return the {@code Integer} value representing the garden.
	 */
	public Integer getGarden()
	{
		return garden;
	}
	
	/**
	 * Retrieves the value of the {@code frame} property.<br>
	 * This method returns the current frame data for the parts.
	 * @return the {@code Integer} value of the frame.
	 */
	public Integer getFrame()
	{
		return frame;
	}
	
	/**
	 * Retrieves the value of the {@code outwall} property.<br>
	 * This method returns the current state of the outer wall.
	 * @return The {@code Integer} value representing the outwall.
	 */
	public Integer getOutwall()
	{
		return outwall;
	}
	
	/**
	 * Retrieves the value of the roof property.<br>
	 * This method returns the {@code Integer} associated with the roof part.
	 * @return The value of the roof as an {@link Integer}.
	 */
	public Integer getRoof()
	{
		return roof;
	}
	
	/**
	 * Retrieves the value of the {@code infloor} property.<br>
	 * This method returns the internal floor data for the housing parts.
	 * @return The integer value of the {@code infloor} field.
	 */
	public int getInfloor()
	{
		return infloor;
	}
	
	/**
	 * Retrieves the value of the {@code inwall} property.<br>
	 * This method returns the internal wall count for the housing structure.
	 * @return The current value of the {@code inwall} field as an {@code int}.
	 */
	public int getInwall()
	{
		return inwall;
	}
	
	/**
	 * Retrieves the current value of the door property.<br>
	 * This method returns the integer associated with the door part.
	 * @return The {@code int} value representing the door.
	 */
	public int getDoor()
	{
		return door;
	}
}
