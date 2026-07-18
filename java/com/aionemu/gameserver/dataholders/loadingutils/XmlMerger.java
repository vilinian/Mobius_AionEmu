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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;

import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * <code>XmlMerger</code> is a utility that writes XML document onto an other document with resolving all <code>import</code> elements.
 * </p>
 * <p>
 * Schema:
 * <p/>
 * 
 * <pre>
 * &lt;xs:element name="import"&gt;
 * &lt;xs:annotation&gt;
 * &lt;xs:documentation&gt;&lt;![CDATA[
 *      Attributes:
 *          'file' :
 *              Required attribute.
 *              Specified path to imported file or directory.
 *          'skipRoot' :
 *              Optional attribute.
 *              Default value: 'false'.
 *              If enabled, then root tags of imported files are ignored.
 *          'recirsiveImport':
 *              Optional attribute.
 *              Default value: 'true'.
 *              If enabled and attribute 'file' points to the directory, then all xml files in that
 *              directory ( and deeper - recursively ) will be imported, otherwise only files inside
 *              that directory (without it subdirectories)
 *  ]]&gt;&lt;/xs:documentation&gt;
 * &lt;/xs:annotation&gt;
 * &lt;xs:complexType&gt;
 * &lt;xs:attribute type="xs:string" name="file" use="required"/&gt;
 * &lt;xs:attribute type="xs:boolean" name="skipRoot" use="optional" default="false"/&gt;
 * &lt;xs:attribute type="xs:boolean" name="recursiveImport" use="optional" default="true" /&gt;
 * &lt;/xs:complexType&gt;
 * &lt;/xs:element&gt;
 * </pre>
 * <p/>
 * </p>
 * <p/>
 * Created on: 23.07.2009 12:55:14
 * @author Aquanox
 */
public class XmlMerger
{
	private static final Logger logger = LoggerFactory.getLogger(XmlMerger.class);
	private final File baseDir;
	private final File sourceFile;
	private final File destFile;
	private final File metaDataFile;
	private final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	private final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	
	/**
	 * Creates a new {@code XmlMerger} instance to merge XML files.<br>
	 * This constructor uses the parent directory of the {@code source} file as the base directory.
	 * @param source The {@code File} containing the source XML data.
	 * @param target The {@code File} where the merged XML will be saved.
	 */
	public XmlMerger(File source, File target)
	{
		this(source, target, source.getParentFile());
	}
	
	/**
	 * Initializes a new {@link XmlMerger} with specific file paths.<br>
	 * This constructor sets the source, target, and base directory for merging operations.<br>
	 * It also prepares the metadata file based on the target path.
	 * @param source The {@code File} containing the original XML data.
	 * @param target The {@code File} where the merged XML will be saved.
	 * @param baseDir The {@code File} representing the root directory for relative paths.
	 */
	public XmlMerger(File source, File target, File baseDir)
	{
		this.baseDir = baseDir;
		
		sourceFile = source;
		destFile = target;
		
		metaDataFile = new File(target.getParent(), target.getName() + ".properties");
	}
	
	/**
	 * Executes the merging process between source and destination files.<br>
	 * This method checks if the files need updating based on existence or modifications.<br>
	 * If updates are required, it calls {@code doUpdate} to synchronize the data.
	 * @throws Exception
	 */
	public void process() throws Exception
	{
		logger.debug("Processing " + sourceFile + " files into " + destFile);
		
		if (!sourceFile.exists())
		{
			throw new FileNotFoundException("Source file " + sourceFile.getPath() + " not found.");
		}
		
		boolean needUpdate = false;
		
		if (!destFile.exists())
		{
			logger.debug("Dest file not found - creating new file");
			needUpdate = true;
		}
		else if (!metaDataFile.exists())
		{
			logger.debug("Meta file not found - creating new file");
			needUpdate = true;
		}
		else
		{
			logger.debug("Dest file found - checking file modifications");
			needUpdate = checkFileModifications();
		}
		
		if (needUpdate)
		{
			logger.debug("Modifications found. Updating...");
			try
			{
				doUpdate();
			}
			catch (Exception e)
			{
				try
				{
					Files.deleteIfExists(destFile.toPath());
				}
				catch (IOException ignored)
				{
				}
				try
				{
					Files.deleteIfExists(metaDataFile.toPath());
				}
				catch (IOException ignored)
				{
				}
				throw e;
			}
		}
		else
		{
			logger.debug("Files are up-to-date");
		}
	}
	
	/**
	 * Checks if the source file has been modified compared to the destination.<br>
	 * It compares timestamps and parses the XML content using a {@code TimeCheckerHandler}.
	 * @return {@code true} if modifications are detected, {@code false} otherwise.
	 * @throws Exception
	 */
	private boolean checkFileModifications() throws Exception
	{
		final long destFileTime = destFile.lastModified();
		
		if (sourceFile.lastModified() > destFileTime)
		{
			logger.debug("Source file was modified ");
			return true;
		}
		
		final Properties metadata = restoreFileModifications(metaDataFile);
		
		if (metadata == null) // new file or smth else.
		{
			return true;
		}
		
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		
		final SAXParser parser = parserFactory.newSAXParser();
		
		final TimeCheckerHandler handler = new TimeCheckerHandler(baseDir, metadata);
		
		parser.parse(sourceFile, handler);
		
		return handler.isModified();
	}
	
	/**
	 * Performs the core logic for merging XML files.<br>
	 * It reads from {@code sourceFile} and writes to {@code destFile}.<br>
	 * This method handles imports, skips comments, and removes whitespace.<br>
	 * Finally, it saves the modification metadata to {@code metaDataFile}.
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void doUpdate() throws XMLStreamException, IOException
	{
		XMLEventReader reader = null;
		XMLEventWriter writer = null;
		
		final Properties metadata = new Properties();
		
		try
		{
			writer = outputFactory.createXMLEventWriter(new BufferedWriter(new FileWriter(destFile, false)));
			reader = inputFactory.createXMLEventReader(new FileReader(sourceFile));
			
			while (reader.hasNext())
			{
				final XMLEvent xmlEvent = reader.nextEvent();
				
				if (xmlEvent.isStartElement() && isImportQName(xmlEvent.asStartElement().getName()))
				{
					processImportElement(xmlEvent.asStartElement(), writer, metadata);
					continue;
				}
				
				if ((xmlEvent.isEndElement() && isImportQName(xmlEvent.asEndElement().getName())) || (xmlEvent instanceof Comment))// skip comments.
				{
					continue;
				}
				
				if (xmlEvent.isCharacters())// skip whitespaces.
				{
					if (xmlEvent.asCharacters().isWhiteSpace() || xmlEvent.asCharacters().isIgnorableWhiteSpace())// skip
					
					// whitespaces.
					{
						continue;
					}
				}
				
				writer.add(xmlEvent);
				
				if (xmlEvent.isStartDocument())
				{
					writer.add(eventFactory.createComment("\nThis file is machine-generated. DO NOT MODIFY IT!\n"));
				}
			}
			
			storeFileModifications(metadata, metaDataFile);
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (Exception ignored)
				{
				}
			}
			
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (Exception ignored)
				{
				}
			}
		}
	}
	
	/**
	 * Checks if the given {@code QName} represents an import element.<br>
	 * It returns {@code true} if the local part is exactly {@code import}.
	 * @param name The {@code QName} to check.
	 * @return {@code true} if it is an import, otherwise {@code false}.
	 */
	private boolean isImportQName(QName name)
	{
		return "import".equals(name.getLocalPart());
	}
	
	private static final QName qNameFile = new QName("file");
	private static final QName qNameSkipRoot = new QName("skipRoot");
	/**
	 * If this option is enabled you import the directory, and all its subdirectories. Default is 'true'.
	 */
	private static final QName qNameRecursiveImport = new QName("recursiveImport");
	
	/**
	 * This method handles the import of a specific XML element.<br>
	 * It reads the file path from the {@code StartElement} and processes it.<br>
	 * If the path is a directory, it recursively imports all files inside.
	 * @param element The {@code StartElement} containing the file path attribute.
	 * @param writer The {@code XMLEventWriter} used to write the imported content.
	 * @param metadata A {@code Properties} object holding additional configuration data.
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void processImportElement(StartElement element, XMLEventWriter writer, Properties metadata) throws XMLStreamException, IOException
	{
		final File file = new File(baseDir, getAttributeValue(element, qNameFile, null, "Attribute 'file' is missing or empty."));
		
		if (!file.exists())
		{
			throw new FileNotFoundException("Missing file to import:" + file.getPath());
		}
		
		final boolean skipRoot = Boolean.valueOf(getAttributeValue(element, qNameSkipRoot, "false", null));
		final boolean recImport = Boolean.valueOf(getAttributeValue(element, qNameRecursiveImport, "true", null));
		
		if (file.isFile())
		{
			importFile(file, skipRoot, writer, metadata);
		}
		else
		{
			logger.debug("Processing dir " + file);
			
			final Collection<File> files = listFiles(file, recImport);
			
			for (File childFile : files)
			{
				importFile(childFile, skipRoot, writer, metadata);
			}
		}
	}
	
	/**
	 * Retrieves a list of XML files from a specific directory.<br>
	 * It filters out hidden files and those starting with the prefix {@code new}.
	 * @param root The base {@code File} directory to start searching from.
	 * @param recursive Whether to search through subdirectories.
	 * @return A {@code Collection} of {@code File} objects matching the criteria.
	 */
	private static Collection<File> listFiles(File root, boolean recursive)
	{
		try (Stream<Path> stream = Files.walk(root.toPath(), recursive ? Integer.MAX_VALUE : 1))
		{
			return stream.filter(Files::isRegularFile).filter(p ->
			{
				final String n = p.getFileName().toString();
				return n.endsWith(".xml") && !n.startsWith("new") && !p.toFile().isHidden();
			}).map(Path::toFile).collect(Collectors.toList());
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to list files in " + root.getPath(), e);
		}
	}
	
	/**
	 * Retrieves the value of a specific attribute from an {@code StartElement}.<br>
	 * If the attribute is missing, it returns the provided default value.<br>
	 * If no default is provided and the attribute is missing, it throws an {@code XMLStreamException}.
	 * @param element The {@code StartElement} to search for the attribute.
	 * @param name The {@code QName} of the attribute to retrieve.
	 * @param def The default value to return if the attribute does not exist.
	 * @param onErrorMessage The message to include in the exception if the attribute is missing and no default exists.
	 * @return The string value of the attribute or the default value.
	 * @throws XMLStreamException If the attribute is missing and {@code def} is {@code null}.
	 */
	private String getAttributeValue(StartElement element, QName name, String def, String onErrorMessage) throws XMLStreamException
	{
		final Attribute attribute = element.getAttributeByName(name);
		
		if (attribute == null)
		{
			if (def == null)
			{
				throw new XMLStreamException(onErrorMessage, element.getLocation());
			}
			
			return def;
		}
		
		return attribute.getValue();
	}
	
	/**
	 * Imports the content of a file into an XML stream.<br>
	 * It handles metadata updates and optional root element skipping.
	 * @param file The source file to be imported.
	 * @param skipRoot If {@code true}, the root element of the imported file is omitted.
	 * @param writer The {@link XMLEventWriter} used to output the XML events.
	 * @param metadata The {@link Properties} object used to store file hashes.
	 * @throws XMLStreamException If an error occurs during XML processing.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	private void importFile(File file, boolean skipRoot, XMLEventWriter writer, Properties metadata) throws XMLStreamException, IOException
	{
		logger.debug("Appending file " + file);
		metadata.setProperty(file.getPath(), makeHash(file));
		
		XMLEventReader reader = null;
		
		try
		{
			reader = inputFactory.createXMLEventReader(new FileReader(file));
			
			QName firstTagQName = null;
			
			while (reader.hasNext())
			{
				XMLEvent event = reader.nextEvent();
				
				// skip start and end of document.
				// skip all comments.
				if (event.isStartDocument() || event.isEndDocument() || (event instanceof Comment))
				{
					continue;
				}
				
				// skip white-spaces and all ignoreable white-spaces.
				if (event.isCharacters())
				{
					if (event.asCharacters().isWhiteSpace() || event.asCharacters().isIgnorableWhiteSpace())
					{
						continue;
					}
				}
				
				// modify root-tag of imported file.
				if ((firstTagQName == null) && event.isStartElement())
				{
					firstTagQName = event.asStartElement().getName();
					
					if (skipRoot)
					{
						continue;
					}
					
					final StartElement old = event.asStartElement();
					
					event = eventFactory.createStartElement(old.getName(), old.getAttributes(), null);
				}
				
				// if root was skipped - skip root end too.
				if (event.isEndElement() && skipRoot && event.asEndElement().getName().equals(firstTagQName))
				{
					continue;
				}
				
				// finally - write tag
				writer.add(event);
			}
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (Exception ignored)
				{
				}
			}
		}
	}
	
	private static class TimeCheckerHandler extends DefaultHandler
	{
		private final File basedir;
		private final Properties metadata;
		private boolean isModified = false;
		private Locator locator;
		
		private TimeCheckerHandler(File basedir, Properties metadata)
		{
			this.basedir = basedir;
			this.metadata = metadata;
		}
		
		@Override
		public void setDocumentLocator(Locator locator)
		{
			this.locator = locator;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if (isModified || !"import".equals(qName))
			{
				return;
			}
			
			final String value = attributes.getValue(qNameFile.getLocalPart());
			
			if (value == null)
			{
				throw new SAXParseException("Attribute 'file' is missing", locator);
			}
			
			final File file = new File(basedir, value);
			
			if (!file.exists()) // noinspection ThrowableInstanceNeverThrown
			{
				throw new SAXParseException("Imported file not found. file=" + file.getPath(), locator);
			}
			
			if (file.isFile() && checkFile(file))// if file - just check it.
			{
				isModified = true;
				return;
			}
			
			if (file.isDirectory())// otherwise check all files inside
			{
				final String rec = attributes.getValue(qNameRecursiveImport.getLocalPart());
				
				final Collection<File> files = listFiles(file, rec == null ? true : Boolean.valueOf(rec));
				
				for (File childFile : files)
				{
					if (checkFile(childFile))
					{
						isModified = true;
						return;
					}
				}
			}
		}
		
		private boolean checkFile(File file)
		{
			final String data = metadata.getProperty(file.getPath());
			
			if (data == null) // file was added.
			{
				return true;
			}
			
			try
			{
				final String hash = makeHash(file);
				
				if (!data.equals(hash))// file|dir was changed.
				{
					return true;
				}
			}
			catch (IOException e)
			{
				logger.warn("File varification error. File: " + file.getPath() + ", location=" + locator.getLineNumber() + ":" + locator.getColumnNumber(), e);
				return true; // was modified.
			}
			
			return false;
		}
		
		public boolean isModified()
		{
			return isModified;
		}
	}
	
	/**
	 * Loads modification data from a specific file into a {@code Properties} object.<br>
	 * This method checks if the {@code File} exists and is a valid file before reading.<br>
	 * It returns {@code null} if the file is missing or an {@code IOException} occurs.
	 * @param file The {@code File} to read modifications from.
	 * @return A {@code Properties} object containing the data, or {@code null} if loading fails.
	 */
	private Properties restoreFileModifications(File file)
	{
		if (!file.exists() || !file.isFile())
		{
			return null;
		}
		
		FileReader reader = null;
		
		try
		{
			final Properties props = new Properties();
			
			reader = new FileReader(file);
			
			props.load(reader);
			
			return props;
		}
		catch (IOException e)// properties
		{
			logger.debug("File modfications restoring error. ", e);
			return null;
		}
		finally
		{
			try
			{
				if (reader != null)
					reader.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}
	
	/**
	 * Saves the current configuration properties into a specific file.<br>
	 * This method adds a warning comment to indicate the file is machine-generated.<br>
	 * It overwrites any existing content in the target {@code File}.
	 * @param props The {@code Properties} object containing the data to save.
	 * @param file The destination {@code File} where the properties will be stored.
	 * @throws IOException If an error occurs while writing to the file.
	 */
	private void storeFileModifications(Properties props, File file) throws IOException
	{
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(file, false);
			props.store(writer, " This file is machine-generated. DO NOT EDIT!");
		}
		catch (IOException e)
		{
			logger.error("Failed to store file modification data.");
			throw e;
		}
		finally
		{
			try
			{
				if (writer != null)
					writer.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}
	
	/**
	 * Generates a unique hash for the given {@code File}.<br>
	 * This method uses the {@code CRC32} checksum algorithm.
	 * @param file The {@code File} to be hashed.
	 * @return A {@code String} representation of the file's checksum.
	 * @throws IOException
	 */
	private static String makeHash(File file) throws IOException
	{
		final CRC32 crc = new CRC32();
		crc.update(Files.readAllBytes(file.toPath()));
		return String.valueOf(crc.getValue());
	}
}
