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
package com.aionemu.gameserver.configs.schedule;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.commons.utils.xml.JAXBUtil;

/**
 * This class manages the configuration for siege events within the game server.<br>
 * It handles the scheduling data loaded from {@code siege_schedule} XML files.
 * @author SoulKeeper, Source
 */
@XmlRootElement(name = "siege_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiegeSchedule
{
	@XmlElement(name = "fortress", required = true)
	private List<Fortress> fortressesList;
	
	/**
	 * Retrieves the list of all fortresses.<br>
	 * This method returns the {@code fortressesList} from the current configuration.
	 * @return a {@code List} containing all {@code Fortress} objects.
	 */
	public List<Fortress> getFortressesList()
	{
		return fortressesList;
	}
	
	/**
	 * Updates the list of fortresses for the {@link SiegeSchedule}.<br>
	 * This method replaces the current list with a new one.
	 * @param fortressList The new list of {@code Fortress} objects to set.
	 */
	public void setFortressesList(List<Fortress> fortressList)
	{
		fortressesList = fortressList;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "fortress")
	public static class Fortress
	{
		@XmlAttribute(required = true)
		private int id;
		@XmlElement(name = "siegeTime", required = true)
		private List<String> siegeTimes;
		
		public int getId()
		{
			return id;
		}
		
		public void setId(int id)
		{
			this.id = id;
		}
		
		public List<String> getSiegeTimes()
		{
			return siegeTimes;
		}
		
		public void setSiegeTimes(List<String> siegeTimes)
		{
			this.siegeTimes = siegeTimes;
		}
	}
	
	/**
	 * Loads the siege schedule from a configuration file.<br>
	 * This method reads {@code ./config/schedule/siege_schedule.xml}.<br>
	 * It uses {@link JAXBUtil} to convert the XML data into a {@code SiegeSchedule} object.
	 * @return The loaded {@code SiegeSchedule} instance.
	 */
	public static SiegeSchedule load()
	{
		SiegeSchedule ss;
		try
		{
			final String xml = Files.readString(new File("./config/schedule/siege_schedule.xml").toPath(), StandardCharsets.UTF_8);
			ss = JAXBUtil.deserialize(xml, SiegeSchedule.class);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to initialize Sieges", e);
		}
		
		return ss;
	}
}
