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
package com.aionemu.gameserver.dataholders.loadingutils;

import java.io.File;
import java.io.FileReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.aionemu.gameserver.dataholders.StaticData;

/**
 * This class is responsible for loading {@code XML} files into the system.<br>
 * It utilizes the {@code JAXB} framework to perform the parsing and unmarshalling operations.
 * @author Luno
 * @{link {@link XmlMerger} to create input file from all xml files.
 */
public class XmlDataLoader
{
	private static final Logger log = LoggerFactory.getLogger(XmlDataLoader.class);
	/**
	 * File containing xml schema declaration
	 */
	private final static String XML_SCHEMA_FILE = "./data/static_data/static_data.xsd";
	private static final String CACHE_DIRECTORY = "./cache/";
	private static final String CACHE_XML_FILE = "./cache/static_data.xml";
	private static final String MAIN_XML_FILE = "./data/static_data/static_data.xml";
	
	/**
	 * Gets the singleton instance of the {@link XmlDataLoader} class.<br>
	 * This ensures that only one loader is used throughout the application.
	 * @return The active {@code XmlDataLoader} instance.
	 */
	public static XmlDataLoader getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link XmlDataLoader} class.<br>
	 * This prevents other classes from creating new instances of this loader.<br>
	 * Use {@code getInstance} to get the singleton instance instead.
	 */
	private XmlDataLoader()
	{
	}
	
	/**
	 * Loads the game's static data from the cache file.<br>
	 * This method prepares the cache directory and merges necessary XML files.<br>
	 * It uses {@link JAXBContext} to parse the data into a {@code StaticData} object.<br>
	 * Returns {@code null} if an error occurs during the loading process.
	 * @return The loaded {@code StaticData} object or {@code null} if it fails to load.
	 */
	public StaticData loadStaticData()
	{
		makeCacheDirectory();
		
		final File cachedXml = new File(CACHE_XML_FILE);
		final File cleanMainXml = new File(MAIN_XML_FILE);
		
		mergeXmlFiles(cachedXml, cleanMainXml);
		
		try
		{
			final JAXBContext jc = JAXBContext.newInstance(StaticData.class);
			final Unmarshaller un = jc.createUnmarshaller();
			un.setEventHandler(new XmlValidationHandler());
			un.setSchema(getSchema());
			return (StaticData) un.unmarshal(new FileReader(CACHE_XML_FILE));
		}
		catch (Exception e)
		{
			log.error("Error while loading static data", e);
			return null;
		}
	}
	
	/**
	 * Loads the XML schema from the local file system.<br>
	 * This method uses {@link SchemaFactory} to initialize the {@code Schema}.<br>
	 * It throws an {@code Error} if the schema file cannot be loaded.
	 * @return The loaded {@code Schema} object.
	 */
	private Schema getSchema()
	{
		Schema schema = null;
		final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		try
		{
			schema = sf.newSchema(new File(XML_SCHEMA_FILE));
		}
		catch (SAXException saxe)
		{
			log.error("Error while getting schema", saxe);
			throw new Error("Error while getting schema", saxe);
		}
		
		return schema;
	}
	
	/**
	 * This method ensures the cache folder exists.<br>
	 * It checks for the directory defined in {@code CACHE_DIRECTORY}.<br>
	 * If it is missing, it creates a new one.
	 */
	private void makeCacheDirectory()
	{
		final File cacheDir = new File(CACHE_DIRECTORY);
		if (!cacheDir.exists())
		{
			cacheDir.mkdir();
		}
	}
	
	/**
	 * Merges data from the main XML file into the cached XML file.<br>
	 * This method uses {@link XmlMerger} to perform the operation.
	 * @param cachedXml The destination {@code File} where data is saved.
	 * @param cleanMainXml The source {@code File} containing the original data.
	 * @throws Error If an error occurs during the merging process.
	 */
	private void mergeXmlFiles(File cachedXml, File cleanMainXml) throws Error
	{
		final XmlMerger merger = new XmlMerger(cleanMainXml, cachedXml);
		try
		{
			merger.process();
		}
		catch (Exception e)
		{
			log.error("Error while merging xml files", e);
			throw new Error("Error while merging xml files", e);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final XmlDataLoader instance = new XmlDataLoader();
	}
}
