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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class serves as a base for data objects that can be reloaded from external files.<br>
 * It provides a standard structure for handling dynamic game configuration and assets.
 * @author ViAl
 */
public abstract class ReloadableData
{
	protected static final Logger log = LoggerFactory.getLogger(ReloadableData.class);
	
	public abstract void reload(Player admin);
	
	protected abstract List<?> getData();
	
	protected abstract void setData(List<?> data);
	
	/**
	 * Loads an XML schema from a file path.<br>
	 * This method uses {@code SchemaFactory} to create the {@link Schema} object.<br>
	 * It throws an error if the file cannot be loaded correctly.
	 * @param xml_schema The file path of the XML schema to load.
	 * @return The loaded {@code Schema} object.
	 */
	protected Schema getSchema(String xml_schema)
	{
		Schema schema = null;
		final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try
		{
			schema = sf.newSchema(new File(xml_schema));
		}
		catch (SAXException saxe)
		{
			throw new Error("Error while getting schema", saxe);
		}
		
		return schema;
	}
	
	/**
	 * Retrieves a list of XML files from a specific directory.<br>
	 * It filters out hidden files and those starting with {@code new}.<br>
	 * The search can be performed recursively or just in the top folder.
	 * @param root The base {@code File} directory to start searching from.
	 * @param recursive Set to {@code true} to search subdirectories, or {@code false} to stay in the root.
	 * @return A {@code Collection} of {@code File} objects found during the search.
	 * @throws IOException If an error occurs while walking the directory tree.
	 */
	protected Collection<File> listFiles(File root, boolean recursive) throws IOException
	{
		try (Stream<Path> stream = Files.walk(root.toPath(), recursive ? Integer.MAX_VALUE : 1))
		{
			return stream.filter(Files::isRegularFile).filter(p ->
			{
				final String n = p.getFileName().toString();
				return n.endsWith(".xml") && !n.startsWith("new") && !p.toFile().isHidden();
			}).map(Path::toFile).collect(Collectors.toList());
		}
	}
}
