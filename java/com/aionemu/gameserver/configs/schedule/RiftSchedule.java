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
import com.aionemu.gameserver.model.templates.rift.OpenRift;

/**
 * This class manages the configuration for scheduled rift events.<br>
 * It handles the loading and parsing of {@code rift_schedule} data from XML files.<br>
 * Use this class to define when specific {@link OpenRift} instances become available.
 * @author Source
 */
@XmlRootElement(name = "rift_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class RiftSchedule
{
	@XmlElement(name = "rift", required = true)
	private List<Rift> riftsList;
	
	/**
	 * Retrieves the list of all configured rifts.<br>
	 * This method returns the {@code riftsList} field from this object.
	 * @return a {@link List} containing all {@code Rift} objects.
	 */
	public List<Rift> getRiftsList()
	{
		return riftsList;
	}
	
	/**
	 * Updates the internal list of rifts.<br>
	 * This method assigns a new {@code List} of {@code Rift} objects to the schedule.
	 * @param fortressList The list of {@code Rift} objects to set.
	 */
	public void setRiftsList(List<Rift> fortressList)
	{
		riftsList = fortressList;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "rift")
	public static class Rift
	{
		@XmlAttribute(required = true)
		private int id;
		@XmlElement(name = "open")
		private List<OpenRift> openRift;
		
		public int getWorldId()
		{
			return id;
		}
		
		public List<OpenRift> getRift()
		{
			return openRift;
		}
	}
	
	/**
	 * Loads the rift schedule configuration from a file.<br>
	 * This method reads {@code ./config/schedule/rift_schedule.xml}.<br>
	 * It uses {@link JAXBUtil} to deserialize the XML data into a {@code RiftSchedule} object.
	 * @return The loaded {@code RiftSchedule} instance.
	 */
	public static RiftSchedule load()
	{
		RiftSchedule rs;
		try
		{
			final String xml = Files.readString(new File("./config/schedule/rift_schedule.xml").toPath(), StandardCharsets.UTF_8);
			rs = JAXBUtil.deserialize(xml, RiftSchedule.class);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to initialize Rifts", e);
		}
		
		return rs;
	}
}
