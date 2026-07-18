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
package com.aionemu.gameserver.model.templates.walker;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Represents a single point or segment within a navigation path for a walker.<br>
 * This class holds the data required to define one step of a {@code Route}.
 * @author KKnD, Rolandas
 */
@XmlRootElement(name = "routestep")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteStep
{
	@XmlAttribute(name = "step", required = true)
	private int routeStep;
	@XmlAttribute(name = "x", required = true)
	private float locX;
	@XmlAttribute(name = "y", required = true)
	private float locY;
	@XmlAttribute(name = "z", required = true)
	private float locZ;
	@XmlAttribute(name = "rest_time", required = true)
	private Integer time = 0;
	@XmlTransient
	private RouteStep nextStep;
	
	/**
	 * Prepares the object data before it is converted to XML.<br>
	 * This method sets certain fields to {@code null} if they contain default values.<br>
	 * It ensures that only relevant data is included in the final output.
	 * @param marshaller The {@link Marshaller} used to convert the object to XML.
	 */
	void beforeMarshal(Marshaller marshaller)
	{
		if (time == 0)
		{
			time = null;
		}
	}
	
	/**
	 * This method is called after the object is marshaled to XML.<br>
	 * It ensures that {@code time} has a default value of {@code 0} if it is {@code null}.
	 * @param marshaller The {@link Marshaller} used for the operation.
	 */
	void afterMarshal(Marshaller marshaller)
	{
		if (time == null)
		{
			time = 0;
		}
	}
	
	/**
	 * Creates a new instance of the {@link RouteStep} class.<br>
	 * This constructor initializes a default step in a path.
	 */
	public RouteStep()
	{
	}
	
	/**
	 * Creates a new {@link RouteStep} with specific coordinates and rest time.<br>
	 * This constructor initializes the position in 3D space.
	 * @param x The horizontal coordinate of the step.
	 * @param y The vertical coordinate of the step.
	 * @param z The depth coordinate of the step.
	 * @param restTime The amount of time to wait at this step.
	 */
	public RouteStep(float x, float y, float z, int restTime)
	{
		locX = x;
		locY = y;
		locZ = z;
		time = restTime;
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return locX;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return locY;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return locZ;
	}
	
	/**
	 * Sets the vertical coordinate of the route step.<br>
	 * This updates the {@code locZ} field with a new value.
	 * @param z The new height value to set for the location.
	 */
	public void setZ(float z)
	{
		locZ = z;
	}
	
	/**
	 * Retrieves the amount of rest time for this step.<br>
	 * This value is stored in the {@code time} field.
	 * @return The rest time as an {@code int}.
	 */
	public int getRestTime()
	{
		return time;
	}
	
	/**
	 * Retrieves the next step in the current path.<br>
	 * This method returns the {@code RouteStep} object linked to this one.<br>
	 * It may return {@code null} if there are no more steps.
	 * @return The next {@link RouteStep} in the sequence or {@code null}.
	 */
	public RouteStep getNextStep()
	{
		return nextStep;
	}
	
	/**
	 * Sets the next step in the navigation sequence.<br>
	 * This links this {@code RouteStep} to another one.
	 * @param nextStep The {@code RouteStep} that follows this one.
	 */
	public void setNextStep(RouteStep nextStep)
	{
		this.nextStep = nextStep;
	}
	
	/**
	 * Retrieves the current step number of the route.<br>
	 * This value identifies the position in a sequence of {@link RouteStep} objects.
	 * @return The integer value of the {@code routeStep}.
	 */
	public int getRouteStep()
	{
		return routeStep;
	}
	
	/**
	 * Sets the step number for this specific route.<br>
	 * This value identifies the position of the step in a sequence.
	 * @param routeStep The new step number to assign.
	 */
	public void setRouteStep(int routeStep)
	{
		this.routeStep = routeStep;
	}
}
