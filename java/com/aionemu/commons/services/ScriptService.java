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
package com.aionemu.commons.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.scriptmanager.ScriptManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This service manages all loaded script contexts within the system.<br>
 * It provides a centralized way to handle and access scripts via {@link ScriptManager}.
 * @author SoulKeeper
 */
public class ScriptService
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ScriptService.class);
	
	/**
	 * Container for ScriptManagers, sorted by file
	 */
	private final Map<File, ScriptManager> map = new ConcurrentHashMap<>();
	
	/**
	 * Loads a script descriptor from the specified path.<br>
	 * This method converts the {@code String} into a {@code File} object.<br>
	 * It then calls the overloaded {@code load} method to process it.
	 * @param file The path to the directory or file containing scripts.
	 * @throws RuntimeException If there is an error loading the script descriptor.
	 */
	public void load(String file) throws RuntimeException
	{
		load(new File(file));
	}
	
	/**
	 * Loads scripts from the specified {@code File}.<br>
	 * This method checks if the input is a file or a directory.<br>
	 * It then calls the appropriate internal loading logic.
	 * @param file The {@code File} object to load from.
	 * @throws RuntimeException If an error occurs during the loading process.
	 */
	public void load(File file) throws RuntimeException
	{
		if (file.isFile())
		{
			loadFile(file);
		}
		else if (file.isDirectory())
		{
			loadDir(file);
		}
	}
	
	/**
	 * Loads a script from the specified {@code File}.<br>
	 * This method creates a new {@link ScriptManager} instance.<br>
	 * It adds the manager to the internal map if it is not already present.
	 * @param file The {@code File} object representing the script to load.
	 */
	private void loadFile(File file)
	{
		if (map.containsKey(file))
		{
			throw new IllegalArgumentException("ScriptManager by file:" + file + " already loaded");
		}
		
		final ScriptManager sm = new ScriptManager();
		try
		{
			sm.load(file);
		}
		catch (Exception e)
		{
			log.error("loadFile", e);
			throw new RuntimeException(e);
		}
		
		map.put(file, sm);
	}
	
	/**
	 * Loads all script files from a specific directory.<br>
	 * This method looks for every {@code xml} file inside the provided folder.<br>
	 * It then calls {@code loadFile} for each file found.
	 * @param dir The {@code File} object representing the directory to scan.
	 */
	private void loadDir(File dir)
	{
		final String[] extensions =
		{
			"xml"
		};
		final Collection<File> files;
		try (Stream<Path> stream = Files.walk(dir.toPath(), 1))
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
		catch (IOException e)
		{
			throw new RuntimeException("Failed to list scripts in directory " + dir.getAbsolutePath(), e);
		}
		for (File file : files)
		{
			loadFile(file);
		}
	}
	
	/**
	 * Removes a script manager from the active service.<br>
	 * This method shuts down the manager associated with the specified {@code File}.<br>
	 * It will throw an exception if the file is not currently loaded.
	 * @param file The {@code File} object representing the script to unload.
	 * @throws IllegalArgumentException If the provided {@code File} was not found in the active map.
	 */
	public void unload(File file) throws IllegalArgumentException
	{
		final ScriptManager sm = map.remove(file);
		if (sm == null)
		{
			throw new IllegalArgumentException("ScriptManager by file " + file + " is not loaded.");
		}
		
		sm.shutdown();
	}
	
	/**
	 * Reloads the script manager associated with a specific file.<br>
	 * This method calls {@code reload} on the existing instance.<br>
	 * It ensures that the file is already loaded into the service.
	 * @param file The {@code File} object used to identify the script manager to reload.
	 * @throws IllegalArgumentException If the specified {@code file} is not currently loaded.
	 */
	public void reload(File file) throws IllegalArgumentException
	{
		final ScriptManager sm = map.get(file);
		if (sm == null)
		{
			throw new IllegalArgumentException("ScriptManager by file " + file + " is not loaded.");
		}
		
		sm.reload();
	}
	
	/**
	 * Registers a new {@link ScriptManager} with the service.<br>
	 * This method maps the manager to a specific {@code File}.<br>
	 * It will throw an exception if the file is already registered.
	 * @param scriptManager The manager instance to add.
	 * @param file The file associated with this manager.
	 */
	public void addScriptManager(ScriptManager scriptManager, File file)
	{
		if (map.containsKey(file))
		{
			throw new IllegalArgumentException("ScriptManager by file " + file + " is already loaded.");
		}
		
		map.put(file, scriptManager);
	}
	
	/**
	 * Retrieves all {@link ScriptManager} instances that have been loaded.<br>
	 * The results are mapped to their corresponding {@code File} locations.<br>
	 * This method returns an unmodifiable view of the internal map.
	 * @return A {@code Map} where keys are {@code File} objects and values are {@code ScriptManager} objects.
	 */
	public Map<File, ScriptManager> getLoadedScriptManagers()
	{
		return Collections.unmodifiableMap(map);
	}
	
	/**
	 * Shuts down all loaded script managers.<br>
	 * This method iterates through the internal map and calls {@code shutdown()} on each {@link ScriptManager}.<br>
	 * It removes the scripts from the active list during the process.
	 */
	public void shutdown()
	{
		for (Iterator<Entry<File, ScriptManager>> it = map.entrySet().iterator(); it.hasNext();)
		{
			try
			{
				it.next().getValue().shutdown();
			}
			catch (Exception e)
			{
				log.warn("An exception occured during shudown procedure.", e);
			}
			
			it.remove();
		}
	}
}
