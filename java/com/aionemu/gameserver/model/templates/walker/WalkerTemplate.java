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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.spawnengine.WalkerGroupType;

/**
 * Represents the configuration data for a walker template.<br>
 * This class holds the properties used to define how walkers behave and appear in the game world.<br>
 * It is mapped from XML files via {@code XmlRootElement}.
 * @author KKnD
 */
@XmlRootElement(name = "walker_template")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalkerTemplate
{
	@XmlElement(name = "routestep")
	private List<RouteStep> routeStepList;
	@XmlAttribute(name = "route_id", required = true)
	private String routeId;
	@XmlAttribute(name = "pool", required = true)
	private int pool = 1;
	@XmlAttribute(name = "formation")
	private WalkerGroupType formation = WalkerGroupType.POINT;
	@XmlAttribute(name = "rows")
	private String rowValues;
	@XmlAttribute(name = "reversed")
	private Boolean isReversed = false;
	@XmlTransient
	private int[] rows;
	
	/**
	 * Creates a new instance of the {@link WalkerTemplate} class.<br>
	 * This is the default constructor used for initializing a template.
	 */
	public WalkerTemplate()
	{
	}
	
	/**
	 * Creates a new {@link WalkerTemplate} instance.<br>
	 * This constructor initializes the template with a specific identifier.
	 * @param routeId The unique ID for the walker route.
	 */
	public WalkerTemplate(String routeId)
	{
		this.routeId = routeId;
	}
	
	/**
	 * Prepares the object data before it is converted to XML.<br>
	 * This method sets certain fields to {@code null} if they contain default values.<br>
	 * It ensures that only relevant data is included in the final output.
	 * @param marshaller The {@link Marshaller} used to convert the object to XML.
	 */
	void beforeMarshal(Marshaller marshaller)
	{
		if (!isReversed)
		{
			isReversed = null;
		}
		
		if (formation == WalkerGroupType.POINT)
		{
			formation = null;
		}
	}
	
	/**
	 * This method is called after the object is marshaled to XML.<br>
	 * It ensures that {@code isReversed} and {@code formation} have default values if they are {@code null}.
	 * @param marshaller The {@link Marshaller} used for the operation.
	 */
	void afterMarshal(Marshaller marshaller)
	{
		if (isReversed == null)
		{
			isReversed = false;
		}
		
		if (formation == null)
		{
			formation = WalkerGroupType.POINT;
		}
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It processes the {@code routeStepList} and sets up the navigation links.<br>
	 * It also calculates the {@code formation} and {@code rows} based on the pool size.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (isReversed)
		{
			for (int i = routeStepList.size() - 2; i > 0; i--)
			{
				final RouteStep step = routeStepList.get(i);
				routeStepList.add(new RouteStep(step.getX(), step.getY(), step.getZ(), step.getRestTime()));
			}
		}
		
		for (int i = 0; i < (routeStepList.size() - 1); i++)
		{
			routeStepList.get(i).setNextStep(routeStepList.get(i + 1));
			routeStepList.get(i).setRouteStep(i + 1);
		}
		
		routeStepList.get(routeStepList.size() - 1).setRouteStep(routeStepList.size());
		routeStepList.get(routeStepList.size() - 1).setNextStep(routeStepList.get(0));
		
		if (pool == 2)
		{
			formation = WalkerGroupType.SQUARE;
			rows = new int[1];
			rows[0] = 2;
		}
		else if (formation == WalkerGroupType.SQUARE)
		{
			if (rowValues != null)
			{
				final String[] values = rowValues.split(",");
				rows = new int[values.length];
				for (int i = 0; i < values.length; i++)
				{
					rows[i] = Integer.parseInt(values[i]);
				}
			}
			else
			{
				formation = WalkerGroupType.POINT;
			}
		}
		
		rowValues = null;
	}
	
	/**
	 * Retrieves the list of steps for this walker's route.<br>
	 * This method returns all {@link RouteStep} objects stored in the template.
	 * @return a {@code List} containing all {@code RouteStep} objects.
	 */
	public List<RouteStep> getRouteSteps()
	{
		return routeStepList;
	}
	
	/**
	 * Retrieves a specific {@link RouteStep} from the list.<br>
	 * The index is based on a 1-based position.
	 * @param value The 1-based position of the step to retrieve.
	 * @return The {@code RouteStep} at the specified position.
	 */
	public RouteStep getRouteStep(int value)
	{
		return routeStepList.get(value - 1);
	}
	
	/**
	 * Retrieves the unique identifier for this walker route.<br>
	 * This ID is used to distinguish different paths in the system.
	 * @return The {@code String} representation of the route ID.
	 */
	public String getRouteId()
	{
		return routeId;
	}
	
	/**
	 * Retrieves the version identifier for the current route.<br>
	 * It uses the {@code routeId} to look up data in {@link DataManager}.
	 * @return The unique version ID as a {@code String}.
	 */
	public String getVersionId()
	{
		return DataManager.WALKER_VERSIONS_DATA.getRouteVersionId(routeId);
	}
	
	/**
	 * Retrieves the current value of the spawn pool.<br>
	 * This value is used to determine the size or quantity of the spawn group.
	 * @return The integer value of the {@code pool}.
	 */
	public int getPool()
	{
		return pool;
	}
	
	/**
	 * Sets the pool value for this walker template.<br>
	 * This value determines which group the walker belongs to.
	 * @param pool The new {@code int} value for the pool.
	 */
	public void setPool(int pool)
	{
		this.pool = pool;
	}
	
	/**
	 * Updates the list of steps for this route.<br>
	 * This method replaces the current {@code routeStepList} with a new one.
	 * @param newSteps The new {@code ArrayList} of {@link RouteStep} objects to set.
	 */
	public void setRouteSteps(ArrayList<RouteStep> newSteps)
	{
		routeStepList = newSteps;
	}
	
	/**
	 * Checks if the walker route is currently reversed.<br>
	 * This method returns the value of the {@code isReversed} property.
	 * @return {@code true} if the route is reversed, {@code false} otherwise.
	 */
	public boolean isReversed()
	{
		return isReversed;
	}
	
	/**
	 * Sets whether the route direction is reversed.<br>
	 * This updates the {@code isReversed} field.
	 * @param value The boolean value to set. Use {@code true} to reverse or {@code false} to keep original order.
	 */
	public void setIsReversed(boolean value)
	{
		isReversed = value;
	}
	
	/**
	 * Retrieves the current formation type of the walker.<br>
	 * This value determines how walkers are grouped in the game world.
	 * @return The {@code WalkerGroupType} assigned to this template.
	 */
	public WalkerGroupType getType()
	{
		return formation;
	}
	
	/**
	 * Retrieves the array of row values.<br>
	 * This method returns the {@code rows} field from the template.
	 * @return an {@code int[]} containing the row data.
	 */
	public int[] getRows()
	{
		return rows;
	}
	
	/**
	 * Removes all {@link RouteStep} objects from the internal list.<br>
	 * The {@code routeStepList} will be empty after this call.
	 */
	public void clear()
	{
		routeStepList.clear();
	}
	
	/**
	 * Adds a new {@code RouteStep} to the current route list.<br>
	 * This method updates the internal collection of steps for this template.
	 * @param step The {@code RouteStep} object to be added.
	 */
	public void addRouteStep(RouteStep step)
	{
		routeStepList.add(step);
	}
}
