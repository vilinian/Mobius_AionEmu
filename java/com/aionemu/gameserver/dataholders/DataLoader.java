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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for loading data from static {@code .txt} files.<br>
 * It serves as the base class for {@link NpcData} and {@code SpawnData}.<br>
 * Please do not use this class for any purpose other than these two specific types.
 * @author Luno
 */
abstract class DataLoader
{
	/**
	 * The logger used for <tt>DataLoader</tt> and its subclasses
	 */
	protected Logger log = LoggerFactory.getLogger(getClass().getName());
	/**
	 * Relative path to directory containing .txt files with static data
	 */
	private static final String PATH = "./data/static_data/";
	/**
	 * File containing data to load ( may be file or directory )
	 */
	private final File dataFile;
	
	/**
	 * Initializes the data loader with a specific file path.<br>
	 * This constructor sets the {@code dataFile} field using the {@code PATH} constant.
	 * @param file The relative path to the file or directory containing the data.
	 */
	DataLoader(String file)
	{
		dataFile = new File(PATH + file);
	}
	
	/**
	 * Loads data from the specified {@code dataFile}.<br>
	 * It checks if the file is a directory or a single file.<br>
	 * If it is a directory, it processes all valid {@code .txt} files inside.<br>
	 * It calls the private {@code loadFile} method for each item.
	 */
	protected void loadData()
	{
		if (dataFile.isDirectory())
		{
			final List<File> files;
			try (Stream<Path> stream = Files.walk(dataFile.toPath()))
			{
				files = stream.filter(Files::isRegularFile).filter(p ->
				{
					final String n = p.getFileName().toString();
					return n.endsWith(".txt") && !n.equals("new") && !p.toFile().isHidden();
				}).map(Path::toFile).collect(Collectors.toList());
			}
			catch (IOException e)
			{
				log.error("Error while listing " + getClass().getSimpleName() + ", dir: " + dataFile.getPath(), e);
				return;
			}
			
			for (File f : files)
			{
				loadFile(f);
			}
		}
		else
		{
			loadFile(dataFile);
		}
	}
	
	/**
	 * Reads the content of a {@code File} line by line.<br>
	 * It skips empty lines and comments starting with {@code #}.<br>
	 * Each valid line is processed by the {@code parse} method.
	 * @param file The {@code File} object to be read.
	 */
	private void loadFile(File file)
	{
		try
		{
			for (String line : Files.readAllLines(file.toPath(), Charset.defaultCharset()))
			{
				if (line.isEmpty() || line.startsWith("#"))
				{
					continue;
				}
				
				parse(line);
			}
		}
		catch (IOException e)
		{
			log.error("Error while loading " + getClass().getSimpleName() + ", file: " + file.getPath(), e);
		}
	}
	
	/**
	 * This method must be overriden in every subclass and is responsible for parsing given <tt>dataEntry</tt> String which represents one row from data file.
	 * @param dataEntry A String containing data about a data entry, that is to be parsed by this method.
	 */
	protected abstract void parse(String dataEntry);
	
	/**
	 * Saves the current data to a static file.<br>
	 * This method uses {@code saveEntries} to write the content.<br>
	 * It logs an info message before starting and an error if it fails.
	 * @return {@code true} if the save was successful, or {@code false} if an exception occurred.
	 */
	public boolean saveData()
	{
		final String desc = PATH + getSaveFile();
		
		log.info("Saving " + desc);
		
		FileWriter fr = null;
		try
		{
			fr = new FileWriter(desc);
			
			saveEntries(fr);
			
			fr.flush();
			
			return true;
		}
		catch (Exception e)
		{
			log.error("Error while saving " + desc, e);
			return false;
		}
		finally
		{
			if (fr != null)
			{
				try
				{
					fr.close();
				}
				catch (Exception e)
				{
					log.error("Error while closing save data file", e);
				}
			}
		}
	}
	
	/**
	 * Name of the file which is used to store data in.<br>
	 * This method must be overriden in sublass if we want to be able to store its data. It's used only in SpawnData and should not be used anywhere else.
	 * @return name of the file
	 */
	protected abstract String getSaveFile();
	
	/**
	 * Saves the current data entries to a file.<br>
	 * This method uses the provided {@code FileWriter} to write information.
	 * @param fileWriter The {@code FileWriter} used to save the data.
	 */
	protected void saveEntries(FileWriter fileWriter)
	{
		// TODO Auto-generated method stub
	}
}
