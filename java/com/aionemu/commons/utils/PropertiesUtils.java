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
package com.aionemu.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This utility class simplifies common operations involving {@code Properties}.<br>
 * It provides helper methods to handle property files and configuration data more easily.
 * @author SoulKeeper
 */
public class PropertiesUtils
{
	/**
	 * Loads configuration settings from a specified file.<br>
	 * This method reads the contents of the file into a {@code Properties} object.<br>
	 * It internally converts the {@code String} path into a {@link File} object.
	 * @param file The path to the properties file as a {@code String}.
	 * @return A {@code Properties} object containing the loaded data.
	 * @throws IOException If there is an error reading the file.
	 */
	public static Properties load(String file) throws IOException
	{
		return load(new File(file));
	}
	
	/**
	 * Loads configuration settings from a specified file.<br>
	 * This method reads the content of a {@code File} into a {@link Properties} object.
	 * @param file The {@code File} to read from.
	 * @return A {@code Properties} object containing the loaded data.
	 * @throws IOException If an error occurs while reading the file.
	 */
	public static Properties load(File file) throws IOException
	{
		final FileInputStream fis = new FileInputStream(file);
		final Properties p = new Properties();
		p.load(fis);
		fis.close();
		return p;
	}
	
	/**
	 * Loads multiple property files into an array.<br>
	 * This method calls {@code load} for each provided path.
	 * @param files The paths to the property files to load.
	 * @return An array of {@code Properties} objects corresponding to the input files.
	 * @throws IOException If any file cannot be read or accessed.
	 */
	public static Properties[] load(String... files) throws IOException
	{
		final Properties[] result = new Properties[files.length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = load(files[i]);
		}
		
		return result;
	}
	
	/**
	 * Loads multiple property files into an array.<br>
	 * This method calls {@code load} for each provided file.
	 * @param files The list of {@code File} objects to load.
	 * @return An array of {@code Properties} objects corresponding to the input files.
	 * @throws IOException If any file cannot be read correctly.
	 */
	public static Properties[] load(File... files) throws IOException
	{
		final Properties[] result = new Properties[files.length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = load(files[i]);
		}
		
		return result;
	}
	
	/**
	 * Loads all {@code Properties} files from a specific directory.<br>
	 * This method scans the folder and returns an array of loaded properties.<br>
	 * It does not search subdirectories recursively.
	 * @param dir The path to the directory containing the property files.
	 * @return An array of {@code Properties} objects found in the directory.
	 * @throws IOException If there is an error accessing the directory or reading files.
	 */
	public static Properties[] loadAllFromDirectory(String dir) throws IOException
	{
		return loadAllFromDirectory(new File(dir), false);
	}
	
	/**
	 * Loads all {@code Properties} files from a specific directory.<br>
	 * This method scans the folder and returns an array of loaded properties.<br>
	 * It does not search into subdirectories.
	 * @param dir The {@code File} object representing the directory to scan.
	 * @return An array of {@code Properties} objects found in the directory.
	 * @throws IOException If there is an error accessing the directory or reading files.
	 */
	public static Properties[] loadAllFromDirectory(File dir) throws IOException
	{
		return loadAllFromDirectory(dir, false);
	}
	
	/**
	 * Loads all {@code Properties} files from a specified directory.<br>
	 * This method scans the folder for property files.<br>
	 * It can search through subdirectories if requested.
	 * @param dir The path to the directory to scan.
	 * @param recursive Set to {@code true} to include subdirectories, or {@code false} to only scan the top level.
	 * @return An array of loaded {@code Properties} objects.
	 * @throws IOException If an error occurs while accessing the files.
	 */
	public static Properties[] loadAllFromDirectory(String dir, boolean recursive) throws IOException
	{
		return loadAllFromDirectory(new File(dir), recursive);
	}
	
	/**
	 * Loads all {@code Properties} files from a specific directory.<br>
	 * It searches for files ending with the extension {@code .properties}.<br>
	 * This method uses {@code load} to process the found files.
	 * @param dir The {@code File} object representing the directory to search.
	 * @param recursive A boolean flag that determines if the search should include subdirectories.
	 * @return An array of loaded {@code Properties} objects.
	 * @throws IOException If an error occurs while accessing the files.
	 */
	public static Properties[] loadAllFromDirectory(File dir, boolean recursive) throws IOException
	{
		final String[] extensions =
		{
			"properties"
		};
		final Collection<File> files;
		try (Stream<Path> stream = Files.walk(dir.toPath(), recursive ? Integer.MAX_VALUE : 1))
		{
			files = stream.filter(Files::isRegularFile).filter(p ->
			{
				final String n = p.getFileName().toString();
				for (String ext : extensions)
				{
					if (n.endsWith("." + ext))
					{
						return true;
					}
				}
				return false;
			}).map(Path::toFile).collect(Collectors.toList());
		}
		return load(files.toArray(new File[files.size()]));
	}
	
	/**
	 * Merges multiple property sets into an existing array.<br>
	 * Values in the {@code properties} array will overwrite values in {@code initialProperties}.<br>
	 * This method modifies the original {@code initialProperties} array directly.
	 * @param initialProperties The base set of properties to be updated.
	 * @param properties The new properties to apply as overrides.
	 * @return The modified {@code initialProperties} array.
	 */
	public static Properties[] overrideProperties(Properties[] initialProperties, Properties[] properties)
	{
		if (properties != null)
		{
			for (Properties props : properties)
			{
				overrideProperties(initialProperties, props);
			}
		}
		
		return initialProperties;
	}
	
	/**
	 * Merges values from a single {@code Properties} object into an array of existing properties.<br>
	 * This method updates each {@code Properties} object in the first parameter with all keys and values from the second parameter.<br>
	 * If the second parameter is {@code null}, no changes are made.
	 * @param initialProperties The array of {@code Properties} objects to be updated.
	 * @param properties The {@code Properties} object containing the new values to apply.
	 * @return The modified array of {@code Properties}.
	 */
	public static Properties[] overrideProperties(Properties[] initialProperties, Properties properties)
	{
		if (properties != null)
		{
			for (Properties initialProps : initialProperties)
			{
				initialProps.putAll(properties);
			}
		}
		
		return initialProperties;
	}
}
