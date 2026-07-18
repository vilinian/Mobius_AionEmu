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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the data model for a housing land area.<br>
 * This class stores the configuration and properties of specific land plots in the game world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Land", propOrder =
{
	"addresses",
	"buildings",
	"sale",
	"fee",
	"caps"
})
public class HousingLand
{
	@XmlElementWrapper(name = "addresses", required = true)
	@XmlElement(name = "address")
	protected List<HouseAddress> addresses;
	@XmlElementWrapper(name = "buildings", required = true)
	@XmlElement(name = "building")
	protected List<Building> buildings;
	@XmlElement(required = true)
	protected Sale sale;
	@XmlElement(required = true)
	protected long fee;
	@XmlElement(required = true)
	protected BuildingCapabilities caps;
	@XmlAttribute(name = "sign_nosale", required = true)
	protected int signNosale;
	@XmlAttribute(name = "sign_sale", required = true)
	protected int signSale;
	@XmlAttribute(name = "sign_waiting", required = true)
	protected int signWaiting;
	@XmlAttribute(name = "sign_home", required = true)
	protected int signHome;
	@XmlAttribute(name = "manager_npc", required = true)
	protected int managerNpc;
	@XmlAttribute(name = "teleport_npc", required = true)
	protected int teleportNpc;
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Retrieves the list of addresses for this land.<br>
	 * This method returns all {@link HouseAddress} objects associated with the property.
	 * @return a {@code List} of {@code HouseAddress} objects.
	 */
	public List<HouseAddress> getAddresses()
	{
		return addresses;
	}
	
	/**
	 * Retrieves the list of all buildings on this land.<br>
	 * This method returns the {@code buildings} collection.
	 * @return a {@code List} of {@link Building} objects.
	 */
	public List<Building> getBuildings()
	{
		return buildings;
	}
	
	/**
	 * Retrieves the primary {@link Building} for this land.<br>
	 * It searches for a building where {@code isDefault()} is true.<br>
	 * If no default is found, it returns the first building in the list.
	 * @return The default {@code Building} object or the first available one.
	 */
	public Building getDefaultBuilding()
	{
		for (Building building : buildings)
		{
			if (building.isDefault())
			{
				return building;
			}
		}
		
		return buildings.get(0); // fail
	}
	
	/**
	 * Retrieves the sale options for this land.<br>
	 * This method returns the {@code Sale} object associated with the housing.
	 * @return the {@code Sale} object.
	 */
	public Sale getSaleOptions()
	{
		return sale;
	}
	
	/**
	 * Retrieves the maintenance cost for this land.<br>
	 * This value is stored in the {@code fee} field.
	 * @return The total maintenance fee as a {@code long}.
	 */
	public long getMaintenanceFee()
	{
		return fee;
	}
	
	/**
	 * Retrieves the capabilities of the building.<br>
	 * This method returns the {@code BuildingCapabilities} object associated with this land.
	 * @return the {@code BuildingCapabilities} instance.
	 */
	public BuildingCapabilities getCapabilities()
	{
		return caps;
	}
	
	/**
	 * Retrieves the unique identifier for the "No Sale" NPC.<br>
	 * This ID is used to display the correct character in the game world.
	 * @return The {@code int} ID of the "No Sale" sign NPC.
	 */
	public int getNosaleSignNpcId()
	{
		return signNosale;
	}
	
	/**
	 * Retrieves the unique identifier for the sale sign NPC.<br>
	 * This ID is used to identify which NPC displays the sale sign.
	 * @return The {@code int} ID of the sale sign NPC.
	 */
	public int getSaleSignNpcId()
	{
		return signSale;
	}
	
	/**
	 * Sets the NPC ID for the sale sign.<br>
	 * This updates the {@code signSale} field.
	 * @param value The new NPC ID to assign.
	 */
	public void setSignSale(int value)
	{
		signSale = value;
	}
	
	/**
	 * Retrieves the unique identifier for the waiting sign NPC.<br>
	 * This ID is used to identify which NPC displays the waiting status.
	 * @return The {@code int} ID of the waiting sign NPC.
	 */
	public int getWaitingSignNpcId()
	{
		return signWaiting;
	}
	
	/**
	 * Retrieves the unique identifier for the home sign NPC.<br>
	 * This ID is used to identify which NPC handles home-related signs.
	 * @return The {@code int} ID of the home sign NPC.
	 */
	public int getHomeSignNpcId()
	{
		return signHome;
	}
	
	/**
	 * Retrieves the unique identifier for the manager NPC.<br>
	 * This ID is used to identify which NPC manages this land.
	 * @return The {@code int} ID of the manager NPC.
	 */
	public int getManagerNpcId()
	{
		return managerNpc;
	}
	
	/**
	 * Retrieves the unique identifier for the teleport NPC.<br>
	 * This ID is used to identify which NPC handles teleportation for this land.
	 * @return The {@code int} ID of the teleport NPC.
	 */
	public int getTeleportNpcId()
	{
		return teleportNpc;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Returns a hash code value for this {@link HousingLand} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code id} field.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		return id;
	}
}
