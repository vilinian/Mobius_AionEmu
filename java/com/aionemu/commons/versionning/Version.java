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
package com.aionemu.commons.versionning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the retrieval and management of version information for the application.<br>
 * It provides utility methods to read metadata from a {@code JarFile} manifest.<br>
 * Use this class to determine the current software version during startup.
 * @author lord_rex
 */
public class Version
{
	private static final Logger log = LoggerFactory.getLogger(Version.class);
	private String revision;
	private String date;
	private String branch;
	private String commitTime;
	
	/**
	 * Creates a new instance of the {@code Version} class.<br>
	 * This constructor initializes an empty version object.<br>
	 * Use {@code loadInformation} to populate the data.
	 */
	public Version()
	{
	}
	
	/**
	 * Creates a new {@link Version} instance.<br>
	 * This constructor automatically loads version data from the provided class.<br>
	 * It calls the {@code loadInformation} method internally.
	 * @param c The class to extract version information from.
	 */
	public Version(Class<?> c)
	{
		loadInformation(c);
	}
	
	/**
	 * Loads version information from the JAR file containing the provided class.<br>
	 * It extracts attributes like revision, date, and branch from the manifest.<br>
	 * If the file is not a valid JAR, an error will be logged.
	 * @param c The {@code Class} object used to locate the source JAR file.
	 */
	public void loadInformation(Class<?> c)
	{
		File jarName = null;
		JarFile jarFile = null;
		try
		{
			jarName = Locator.getClassSource(c);
			jarFile = new JarFile(jarName);
			
			final Attributes attrs = jarFile.getManifest().getMainAttributes();
			revision = getAttribute("Revision", attrs);
			date = getAttribute("Date", attrs);
			branch = getAttribute("Branch", attrs);
			commitTime = getAttribute("CommitTime", attrs);
		}
		catch (IOException e)
		{
			log.error("Unable to get Soft information\nFile name '" + (jarName == null ? "null" : jarName.getAbsolutePath()) + "' isn't a valid jar", e);
		}
		finally
		{
			if (jarFile != null)
			{
				try
				{
					jarFile.close();
				}
				catch (IOException e)
				{
					log.error("error closing jar file " + jarName, e);
				}
			}
		}
	}
	
	/**
	 * This method copies the manifest from a JAR file to a destination file.<br>
	 * It checks if the output file exists before starting the process.<br>
	 * If an error occurs, it logs the exception details.
	 * @param jarName The name of the source JAR file located in the root directory.
	 * @param type A description of the component type for logging purposes.
	 * @param fileToWrite The {@code File} object where the manifest will be saved.
	 */
	public void transferInfo(String jarName, String type, File fileToWrite)
	{
		JarFile jarFile = null;
		try
		{
			if (!fileToWrite.exists())
			{
				log.error("Unable to Find File :" + fileToWrite.getName() + " Please Update your " + type);
				return;
			}
			
			// Open the JAR file
			jarFile = new JarFile("./" + jarName);
			
			// Get the manifest
			final Manifest manifest = jarFile.getManifest();
			
			// Write the manifest to a file
			final OutputStream fos = new FileOutputStream(fileToWrite);
			manifest.write(fos);
			fos.close();
		}
		catch (IOException e)
		{
			log.error("Error, " + e);
		}
		finally
		{
			if (jarFile != null)
			{
				try
				{
					jarFile.close();
				}
				catch (IOException e)
				{
					log.error("cannot close jar file " + jarName, e);
				}
			}
		}
	}
	
	/**
	 * Retrieves the current version revision.<br>
	 * This value is loaded during the initialization of the {@link Version} class.
	 * @return The revision string.
	 */
	public String getRevision()
	{
		return revision;
	}
	
	/**
	 * Retrieves the version release date.<br>
	 * This method returns the {@code date} string stored in this instance.
	 * @return The date as a {@code String}.
	 */
	public String getDate()
	{
		return date;
	}
	
	/**
	 * Retrieves the current version branch name.<br>
	 * This value is loaded during the initialization of the {@link Version} object.
	 * @return The name of the branch as a {@code String}.
	 */
	public String getBranch()
	{
		return branch;
	}
	
	/**
	 * Retrieves the timestamp of the last commit.<br>
	 * This value is stored as a {@code String}.
	 * @return The commit time string.
	 */
	public String getCommitTime()
	{
		return commitTime;
	}
	
	/**
	 * Retrieves a specific value from the {@code Attributes} object.<br>
	 * It returns a default string if the attribute is not found.
	 * @param attribute The name of the attribute to look up.
	 * @param attrs The {@link Attributes} object containing the data.
	 * @return The value of the attribute or a default message.
	 */
	private String getAttribute(String attribute, Attributes attrs)
	{
		final String date = attrs.getValue(attribute);
		return date != null ? date : "Unknown " + attribute;
	}
}
