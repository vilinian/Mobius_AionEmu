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
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.aionemu.gameserver.model.templates.housing.LBox;

/**
 * This class holds the data for house scripts within the game server.<br>
 * It serves as a data container for loading and managing script information from XML files.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "lboxes")
public class HouseScriptData
{
	private static final Logger log = LoggerFactory.getLogger(HouseScriptData.class);
	private static Marshaller marshaller;
	
	static
	{
		final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		JAXBContext jc = null;
		
		try
		{
			schema = sf.newSchema(new File("./data/static_data/housing/scripts.xsd"));
			jc = JAXBContext.newInstance(HouseScriptData.class);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-16");
		}
		catch (Exception e)
		{
			log.error("Could not instantiate HouseScriptData : \n" + e);
		}
	}
	
	@XmlElement(name = "lbox", required = true)
	protected List<LBox> scriptData;
	@XmlTransient
	private final Map<Integer, LBox> defaultTemplates = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code defaultTemplates} map using the list of {@link LBox} templates.<br>
	 * The {@code scriptData} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (LBox template : scriptData)
		{
			defaultTemplates.put(template.getId(), template);
		}
		
		scriptData.clear();
		scriptData = null;
	}
	
	public static class XmlFormatter
	{
		private static final Logger log = LoggerFactory.getLogger(XmlFormatter.class);
		private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		private static DocumentBuilder db;
		
		static
		{
			try
			{
				db = dbf.newDocumentBuilder();
			}
			catch (ParserConfigurationException e)
			{
				log.error("Could not instantiate XmlFormatter : \n" + e);
			}
		}
		
		public static String format(String unformattedXml)
		{
			try
			{
				final Document document = parseXmlFile(unformattedXml);
				
				final Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				final Writer out = new StringWriter();
				transformer.transform(new DOMSource(document), new StreamResult(out));
				return out.toString();
			}
			catch (Exception e)
			{
			}
			
			return null;
		}
		
		private static Document parseXmlFile(String in)
		{
			try
			{
				final InputSource is = new InputSource(new StringReader(in));
				return db.parse(is);
			}
			catch (SAXException e)
			{
				throw new RuntimeException(e);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Creates a new script entry based on an existing template.<br>
	 * This method clones a template and assigns it a unique position and icon.<br>
	 * It returns the formatted XML string for the new data.
	 * @param scriptId The ID of the template to use as a base.
	 * @param position The unique identifier for the new script instance.
	 * @param iconId The ID of the icon to display for this script.
	 * @return A formatted {@code String} containing the XML representation of the script.
	 */
	public String createScript(int scriptId, int position, int iconId)
	{
		final LBox template = defaultTemplates.get(scriptId);
		final LBox result = (LBox) template.clone();
		result.setId(position);
		result.setIcon(iconId);
		
		final HouseScriptData fragment = new HouseScriptData();
		fragment.scriptData = new ArrayList<>();
		fragment.scriptData.add(result);
		
		final Writer writer = new StringWriter();
		try
		{
			marshaller.marshal(fragment, writer);
		}
		catch (JAXBException e)
		{
		}
		
		return XmlFormatter.format(writer.toString());
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return defaultTemplates.size();
	}
}
