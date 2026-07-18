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
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;

/**
 * This is a utility class used to locate specific items within the system environment.<br>
 * It provides helper methods to find files and resources based on various criteria.
 * @since Ant 1.6
 */
public final class Locator
{
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This class is intended to be used as a utility class only.
	 */
	private Locator()
	{
	}
	
	/**
	 * Finds the physical file location of a given {@code Class}.<br>
	 * It uses the {@code getClassLoader} to locate the resource.
	 * @param c The {@code Class} object to locate.
	 * @return A {@code File} object representing the source path.
	 */
	public static File getClassSource(Class<?> c)
	{
		final String classResource = c.getName().replace('.', '/') + ".class";
		return getResourceSource(c.getClassLoader(), classResource);
	}
	
	/**
	 * Finds the {@code File} object for a specific resource.<br>
	 * It uses the provided {@code ClassLoader} to locate the path.<br>
	 * If the loader is {@code null}, it defaults to the system class loader.
	 * @param c The {@code ClassLoader} to use for searching.
	 * @param resource The name of the resource to find.
	 * @return A {@code File} object if found, or {@code null} otherwise.
	 */
	public static File getResourceSource(ClassLoader c, String resource)
	{
		if (c == null)
		{
			c = Locator.class.getClassLoader();
		}
		
		URL url = null;
		if (c == null)
		{
			url = ClassLoader.getSystemResource(resource);
		}
		else
		{
			url = c.getResource(resource);
		}
		
		if (url != null)
		{
			final String u = url.toString();
			if (u.startsWith("jar:file:"))
			{
				final int pling = u.indexOf("!");
				final String jarName = u.substring(4, pling);
				return new File(fromURI(jarName));
			}
			else if (u.startsWith("file:"))
			{
				final int tail = u.indexOf(resource);
				final String dirName = u.substring(0, tail);
				return new File(fromURI(dirName));
			}
		}
		
		return null;
	}
	
	/**
	 * Converts a {@code String} URI into a system-specific file path.<br>
	 * This method validates that the input is a valid {@code file} protocol.<br>
	 * It handles character decoding and replaces forward slashes with the correct separator.
	 * @param uri The {@code String} representation of the URI to convert.
	 * @return The decoded system path as a {@code String}.
	 */
	public static String fromURI(String uri)
	{
		URL url = null;
		try
		{
			url = new URI(uri).toURL();
		}
		catch (MalformedURLException | URISyntaxException emYouEarlEx)
		{
			// Ignore malformed exception
		}
		
		if ((url == null) || !("file".equals(url.getProtocol())))
		{
			throw new IllegalArgumentException("Can only handle valid file: URIs");
		}
		
		final StringBuffer buf = new StringBuffer(url.getHost());
		if (buf.length() > 0)
		{
			buf.insert(0, File.separatorChar).insert(0, File.separatorChar);
		}
		
		final String file = url.getFile();
		final int queryPos = file.indexOf('?');
		buf.append((queryPos < 0) ? file : file.substring(0, queryPos));
		
		uri = buf.toString().replace('/', File.separatorChar);
		
		if ((File.pathSeparatorChar == ';') && uri.startsWith("\\") && (uri.length() > 2) && Character.isLetter(uri.charAt(1)) && (uri.lastIndexOf(':') > -1))
		{
			uri = uri.substring(1);
		}
		
		final String path = decodeUri(uri);
		return path;
	}
	
	/**
	 * Converts a URL-encoded string into its plain text format.<br>
	 * This method replaces percent-encoded characters with their actual values.<br>
	 * It returns the original string if no encoding is detected.
	 * @param uri The encoded {@code String} to be decoded.
	 * @return The decoded {@code String}.
	 */
	private static String decodeUri(String uri)
	{
		if (uri.indexOf('%') == -1)
		{
			return uri;
		}
		
		final StringBuffer sb = new StringBuffer();
		final CharacterIterator iter = new StringCharacterIterator(uri);
		for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next())
		{
			if (c == '%')
			{
				final char c1 = iter.next();
				if (c1 != CharacterIterator.DONE)
				{
					final int i1 = Character.digit(c1, 16);
					final char c2 = iter.next();
					if (c2 != CharacterIterator.DONE)
					{
						final int i2 = Character.digit(c2, 16);
						sb.append((char) ((i1 << 4) + i2));
					}
				}
			}
			else
			{
				sb.append(c);
			}
		}
		
		final String path = sb.toString();
		return path;
	}
	
	/**
	 * Retrieves the location of the {@code tools.jar} file.<br>
	 * It checks if the compiler is already available in the classpath.<br>
	 * If not found, it attempts to locate the jar using the {@code java.home} property.
	 * @return a {@code File} object representing the path to {@code tools.jar}, or {@code null} if it cannot be found.
	 */
	public static File getToolsJar()
	{
		// firstly check if the tools jar is already in the classpath
		boolean toolsJarAvailable = false;
		try
		{
			// just check whether this throws an exception
			Class.forName("com.sun.tools.javac.Main");
			toolsJarAvailable = true;
		}
		catch (Exception e)
		{
			try
			{
				Class.forName("sun.tools.javac.Main");
				toolsJarAvailable = true;
			}
			catch (Exception e2)
			{
				// ignore
			}
		}
		
		if (toolsJarAvailable)
		{
			return null;
		}
		
		// Could not find the compiler; try finding tools.jar based on the java.home setting.
		String javaHome = System.getProperty("java.home");
		if (javaHome.toLowerCase(Locale.US).endsWith("jre"))
		{
			javaHome = javaHome.substring(0, javaHome.length() - 4);
		}
		
		final File toolsJar = new File(javaHome + "/lib/tools.jar");
		if (!toolsJar.exists())
		{
			System.out.println("Unable to locate tools.jar. " + "Expected to find it in " + toolsJar.getPath());
			return null;
		}
		
		return toolsJar;
	}
	
	/**
	 * Converts a {@code File} into an array of {@link URL} objects.<br>
	 * This method specifically looks for files with the {@code .jar} extension.
	 * @param location The {@code File} to be converted.
	 * @return An array of {@link URL} objects representing the file locations.
	 * @throws MalformedURLException If the path is not a valid URL.
	 */
	public static URL[] getLocationURLs(File location) throws MalformedURLException
	{
		return getLocationURLs(location, new String[]
		{
			".jar"
		});
	}
	
	/**
	 * Converts a {@code File} and its matching children into an array of {@link URL} objects.<br>
	 * This method filters files based on the provided {@code extensions}.<br>
	 * If the {@code location} does not exist, it returns an empty array.
	 * @param location The {@code File} path to search.
	 * @param extensions An array of file extensions to filter by.
	 * @return An array of {@link URL} objects matching the criteria.
	 * @throws MalformedURLException If the file path cannot be converted to a valid {@link URL}.
	 */
	public static URL[] getLocationURLs(File location, String[] extensions) throws MalformedURLException
	{
		URL[] urls = new URL[0];
		
		if (!location.exists())
		{
			return urls;
		}
		
		if (!location.isDirectory())
		{
			urls = new URL[1];
			final String path = location.getPath();
			for (int i = 0; i < extensions.length; ++i)
			{
				if (path.toLowerCase().endsWith(extensions[i]))
				{
					urls[0] = location.toURI().toURL();
					break;
				}
			}
			
			return urls;
		}
		
		final File[] matches = location.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				for (int i = 0; i < extensions.length; ++i)
				{
					if (name.toLowerCase().endsWith(extensions[i]))
					{
						return true;
					}
				}
				
				return false;
			}
		});
		urls = new URL[matches.length];
		for (int i = 0; i < matches.length; ++i)
		{
			urls[i] = matches[i].toURI().toURL();
		}
		
		return urls;
	}
}
