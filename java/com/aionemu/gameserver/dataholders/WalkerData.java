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
package com.aionemu.gameserver.dataholders;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;
import com.aionemu.gameserver.world.World;

/**
 * This class serves as a data holder for {@link WalkerTemplate} information.<br>
 * It is used to map and store walker-related configuration data from XML files.<br>
 * It provides the necessary structure for the game server to load NPC walker properties.
 * @author KKnD, Rolandas
 */
@XmlRootElement(name = "npc_walker")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalkerData
{
	private static final Logger log = LoggerFactory.getLogger(WalkerData.class);
	@XmlElement(name = "walker_template")
	private List<WalkerTemplate> walkerlist;
	@XmlTransient
	private Map<String, WalkerTemplate> walkerlistData = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code walkerlistData} map using the list of {@link WalkerTemplate} objects.<br>
	 * The {@code walkerlist} is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (WalkerTemplate route : walkerlist)
		{
			if (walkerlistData.containsKey(route.getRouteId()))
			{
				log.warn("Duplicate route ID: " + route.getRouteId());
				continue;
			}
			
			walkerlistData.put(route.getRouteId(), route);
		}
		
		walkerlist.clear();
		walkerlist = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return walkerlistData.size();
	}
	
	/**
	 * Creates a copy of the current {@code WalkerData} object.<br>
	 * This method returns a new instance with the same data.
	 * @return A new {@link WalkerData} instance.
	 */
	@Override
	public WalkerData clone()
	{
		final WalkerData wd = new WalkerData();
		wd.setWalkerlistData(getWalkerlistData());
		return wd;
	}
	
	/**
	 * Retrieves a {@link WalkerTemplate} based on the provided route identifier.<br>
	 * This method looks up the template in the internal data map.
	 * @param routeId The unique identifier for the walker route.
	 * @return The {@code WalkerTemplate} associated with the ID, or {@code null} if not found.
	 */
	public WalkerTemplate getWalkerTemplate(String routeId)
	{
		if (routeId == null)
		{
			return null;
		}
		
		return walkerlistData.get(routeId);
	}
	
	/**
	 * Adds a new {@link WalkerTemplate} to the internal list.<br>
	 * This method initializes the list if it is currently {@code null}.
	 * @param newTemplate The {@code WalkerTemplate} object to be added.
	 */
	public void AddTemplate(WalkerTemplate newTemplate)
	{
		if (walkerlist == null)
		{
			walkerlist = new ArrayList<>();
		}
		
		walkerlist.add(newTemplate);
	}
	
	/**
	 * Saves the current walker data to an XML file.<br>
	 * The file name is generated based on the provided {@code routeId}.<br>
	 * This method clears the internal {@code walkerlist} after saving.
	 * @param routeId The unique identifier used to name the output file.
	 */
	public void saveData(String routeId)
	{
		Schema schema = null;
		final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		try
		{
			schema = sf.newSchema(new File("./data/static_data/npc_walker/npc_walker.xsd"));
		}
		catch (SAXException e1)
		{
			log.error("Error while saving data: " + e1.getMessage(), e1.getCause());
			return;
		}
		
		final File xml = new File("./data/static_data/npc_walker/generated_npc_walker_" + routeId + ".xml");
		JAXBContext jc;
		Marshaller marshaller;
		try
		{
			jc = JAXBContext.newInstance(WalkerData.class);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(this, xml);
		}
		catch (JAXBException e)
		{
			log.error("Error while saving data: " + e.getMessage(), e.getCause());
			return;
		}
		finally
		{
			if (walkerlist != null)
			{
				walkerlist.clear();
				walkerlist = null;
			}
		}
	}
	
	/**
	 * Retrieves all loaded walker templates.<br>
	 * This method returns the values stored in the internal data map.
	 * @return a {@code Collection} of {@link WalkerTemplate} objects.
	 */
	public Collection<WalkerTemplate> getTemplates()
	{
		return walkerlistData.values();
	}
	
	/**
	 * Retrieves the internal map of walker templates.<br>
	 * This method provides access to all loaded {@link WalkerTemplate} objects.
	 * @return a {@code Map} containing the walker data.
	 */
	private Map<String, WalkerTemplate> getWalkerlistData()
	{
		return walkerlistData;
	}
	
	/**
	 * Updates the internal map of walker templates.<br>
	 * This method sets the {@code walkerlistData} field to a new value.
	 * @param walkerlistData The {@link Map} containing the updated walker data.
	 */
	private void setWalkerlistData(Map<String, WalkerTemplate> walkerlistData)
	{
		this.walkerlistData = walkerlistData;
	}
	
	/**
	 * Updates the internal data map with a new {@code WalkerTemplate}.<br>
	 * It replaces any existing template that shares the same route ID.
	 * @param template The {@code WalkerTemplate} to add or update.
	 */
	public void replaceTemplate(WalkerTemplate template)
	{
		if (walkerlistData.containsKey(template.getRouteId()))
		{
			walkerlistData.remove(template.getRouteId());
		}
		
		walkerlistData.put(template.getRouteId(), template);
	}
	
	/**
	 * Saves the current walker data to an XML file.<br>
	 * The filename is generated using the provided {@code worldId}.<br>
	 * This method uses a schema for validation during the save process.
	 * @param worldId The unique identifier for the world used in the filename.
	 */
	public void writeXml(int worldId)
	{
		Schema schema = null;
		final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		try
		{
			schema = sf.newSchema(new File("./data/static_data/npc_walker/npc_walker.xsd"));
		}
		catch (SAXException e1)
		{
			log.error("Error while saving data: " + e1.getMessage(), e1.getCause());
			return;
		}
		
		final File xml = new File("./data/static_data/npc_walker/walker_" + worldId + "_" + World.getInstance().getWorldMap(worldId).getName() + ".xml");
		JAXBContext jc;
		Marshaller marshaller;
		try
		{
			jc = JAXBContext.newInstance(WalkerData.class);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(this, xml);
		}
		catch (JAXBException e)
		{
			log.error("Error while saving data: " + e.getMessage(), e.getCause());
			return;
		}
	}
}
