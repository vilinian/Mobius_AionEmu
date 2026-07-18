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
package com.aionemu.gameserver.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.HTMLConfig;

/**
 * This class manages the caching of {@code HTML} content for the game server.<br>
 * It provides efficient access to pre-loaded web resources to reduce disk I/O.<br>
 * Use this class to retrieve and manage static HTML data throughout the application.
 * @authors Layane, nbali, savormix, hex1r0, lord_rex
 */
public final class HTMLCache
{
	private static final Logger log = LoggerFactory.getLogger(HTMLCache.class);
	private static final FileFilter HTML_FILTER = new FileFilter()
	{
		@Override
		public boolean accept(File file)
		{
			return file.isDirectory() || file.getName().endsWith(".xhtml");
		}
	};
	private static final File HTML_ROOT = new File(HTMLConfig.HTML_ROOT);
	
	private static final class SingletonHolder
	{
		private static final HTMLCache INSTANCE = new HTMLCache();
	}
	
	/**
	 * Provides the global instance of the {@link HTMLCache} class.<br>
	 * This method uses the singleton pattern to ensure only one cache exists.
	 * @return The single instance of {@code HTMLCache}.
	 */
	public static HTMLCache getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private Map<String, String> cache = new ConcurrentHashMap<>(16000);
	private int loadedFiles;
	private int size;
	
	/**
	 * Private constructor for the {@link HTMLCache} class.<br>
	 * This prevents other classes from creating new instances.<br>
	 * It automatically calls {@code reload} with {@code false}.
	 */
	private HTMLCache()
	{
		reload(false);
	}
	
	/**
	 * Reloads the HTML cache from the disk.<br>
	 * This method clears the current cache and attempts to load it from a file.<br>
	 * If no cache file exists, it parses the directory and creates a new one.
	 * @param deleteCacheFile Set to {@code true} to delete the existing cache file before reloading.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void reload(boolean deleteCacheFile)
	{
		cache.clear();
		loadedFiles = 0;
		size = 0;
		
		final File cacheFile = getCacheFile();
		
		if (deleteCacheFile && cacheFile.exists())
		{
			log.info("Cache[HTML]: Deleting cache file... OK.");
			
			cacheFile.delete();
		}
		
		log.info("Cache[HTML]: Caching started... OK.");
		
		if (cacheFile.exists())
		{
			log.info("Cache[HTML]: Using cache file... OK.");
			
			ObjectInputStream ois = null;
			try
			{
				ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(getCacheFile())));
				
				cache = (Map<String, String>) ois.readObject();
				
				for (String html : cache.values())
				{
					loadedFiles++;
					size += html.length();
				}
			}
			catch (Exception e)
			{
				log.warn("", e);
				
				reload(true);
				return;
			}
			finally
			{
				try
				{
					if (ois != null)
						ois.close();
				}
				catch (IOException ignored)
				{
				}
			}
		}
		else
		{
			parseDir(HTML_ROOT);
		}
		
		log.info(String.valueOf(this));
		
		if (cacheFile.exists())
		{
			log.info("Cache[HTML]: Compaction skipped!");
		}
		else
		{
			log.info("Cache[HTML]: Compacting htmls... OK.");
			
			final StringBuilder sb = new StringBuilder(8192);
			
			for (Entry<String, String> entry : cache.entrySet())
			{
				try
				{
					final String oldHtml = entry.getValue();
					final String newHtml = compactHtml(sb, oldHtml);
					
					size -= oldHtml.length();
					size += newHtml.length();
					
					entry.setValue(newHtml);
				}
				catch (RuntimeException e)
				{
					log.warn("Cache[HTML]: Error during compaction of " + entry.getKey(), e);
				}
			}
			
			log.info(String.valueOf(this));
		}
		
		if (!cacheFile.exists())
		{
			log.info("Cache[HTML]: Creating cache file... OK.");
			
			ObjectOutputStream oos = null;
			try
			{
				oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(getCacheFile())));
				
				oos.writeObject(cache);
			}
			catch (IOException e)
			{
				log.warn("", e);
			}
			finally
			{
				try
				{
					if (oos != null)
						oos.close();
				}
				catch (IOException ignored)
				{
				}
			}
		}
	}
	
	/**
	 * Retrieves the {@code File} object for the cache.<br>
	 * It uses the path defined in {@code HTML_CACHE_FILE}.
	 * @return The {@code File} representing the cache location.
	 */
	private File getCacheFile()
	{
		return new File(HTMLConfig.HTML_CACHE_FILE);
	}
	
	private static final String[] TAGS_TO_COMPACT;
	
	static
	{
		// TODO: is there any other tag that should be replaced?
		final String[] tagsToCompact =
		{
			"html",
			"title",
			"body",
			"br",
			"br1",
			"p",
			"table",
			"tr",
			"td"
		};
		
		final List<String> list = new ArrayList<>();
		
		for (String tag : tagsToCompact)
		{
			list.add("<" + tag + ">");
			list.add("</" + tag + ">");
			list.add("<" + tag + "/>");
			list.add("<" + tag + " />");
		}
		
		final List<String> list2 = new ArrayList<>();
		
		for (String tag : list)
		{
			list2.add(tag);
			list2.add(tag + " ");
			list2.add(" " + tag);
		}
		
		TAGS_TO_COMPACT = list2.toArray(new String[list.size()]);
	}
	
	/**
	 * This method removes unnecessary whitespace from an HTML string.<br>
	 * It cleans up the content and stores it in a {@code StringBuilder}.<br>
	 * The final result is returned as a trimmed string.
	 * @param sb The {@code StringBuilder} used to store the processed text.
	 * @param html The raw HTML string to be compacted.
	 * @return The compacted HTML string.
	 */
	private String compactHtml(StringBuilder sb, String html)
	{
		sb.setLength(0);
		sb.append(html);
		
		for (int i = 0; i < sb.length(); i++)
		{
			if (Character.isWhitespace(sb.charAt(i)))
			{
				sb.setCharAt(i, ' ');
			}
		}
		
		replaceAll(sb, "  ", " ");
		
		replaceAll(sb, "< ", "<");
		replaceAll(sb, " >", ">");
		
		for (int i = 0; i < TAGS_TO_COMPACT.length; i += 3)
		{
			replaceAll(sb, TAGS_TO_COMPACT[i + 1], TAGS_TO_COMPACT[i]);
			replaceAll(sb, TAGS_TO_COMPACT[i + 2], TAGS_TO_COMPACT[i]);
		}
		
		replaceAll(sb, "  ", " ");
		
		// String.trim() without additional garbage
		int fromIndex = 0;
		int toIndex = sb.length();
		
		while ((fromIndex < toIndex) && (sb.charAt(fromIndex) == ' '))
		{
			fromIndex++;
		}
		
		while ((fromIndex < toIndex) && (sb.charAt(toIndex - 1) == ' '))
		{
			toIndex--;
		}
		
		return sb.substring(fromIndex, toIndex);
	}
	
	/**
	 * Replaces all occurrences of a specific string within a {@code StringBuilder}.<br>
	 * It searches for the {@code pattern} and replaces it with the provided {@code value}.<br>
	 * This method modifies the {@code sb} object directly.
	 * @param sb The {@code StringBuilder} to modify.
	 * @param pattern The string sequence to search for.
	 * @param value The replacement string to insert.
	 */
	private void replaceAll(StringBuilder sb, String pattern, String value)
	{
		for (int index = 0; (index = sb.indexOf(pattern, index)) != -1;)
		{
			sb.replace(index, index + pattern.length(), value);
		}
	}
	
	/**
	 * Reloads the cache from a specific directory.<br>
	 * This method calls {@code parseDir} to refresh the data.<br>
	 * It logs a message once the operation is complete.
	 * @param f The {@code File} object representing the path to reload.
	 */
	public void reloadPath(File f)
	{
		parseDir(f);
		
		log.info("Cache[HTML]: Reloaded specified path.");
	}
	
	/**
	 * Recursively scans a directory to load HTML files.<br>
	 * It identifies all valid files using the {@code HTML_FILTER}.<br>
	 * If it finds a subdirectory, it calls {@code parseDir} again.
	 * @param dir The {@code File} directory to be processed.
	 */
	public void parseDir(File dir)
	{
		for (File file : dir.listFiles(HTML_FILTER))
		{
			if (!file.isDirectory())
			{
				loadFile(file);
			}
			else
			{
				parseDir(file);
			}
		}
	}
	
	/**
	 * Reads the content of a specific file into a {@code String}.<br>
	 * This method checks if the file is valid before loading.<br>
	 * It updates the internal cache and tracks the total size.
	 * @param file The {@code File} object to be read.
	 * @return The content of the file as a {@code String}, or {@code null} if an error occurs.
	 */
	public String loadFile(File file)
	{
		if (isLoadable(file))
		{
			BufferedInputStream bis = null;
			try
			{
				bis = new BufferedInputStream(new FileInputStream(file));
				final byte[] raw = new byte[bis.available()];
				bis.read(raw);
				
				final String content = new String(raw, HTMLConfig.HTML_ENCODING);
				final String relpath = getRelativePath(HTML_ROOT, file);
				
				size += content.length();
				
				final String oldContent = cache.get(relpath);
				if (oldContent == null)
				{
					loadedFiles++;
				}
				else
				{
					size -= oldContent.length();
				}
				
				cache.put(relpath, content);
				
				return content;
			}
			catch (Exception e)
			{
				log.warn("Problem with htm file:", e);
			}
			finally
			{
				try
				{
					if (bis != null)
						bis.close();
				}
				catch (IOException ignored)
				{
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the HTML content from the internal cache.<br>
	 * It uses the provided {@code path} as the key to find the data.<br>
	 * If the path is not found, it may return {@code null}.
	 * @param path The file path used to identify the cached HTML content.
	 * @return The HTML string associated with the given {@code path}.
	 */
	public String getHTML(String path)
	{
		return cache.get(path);
	}
	
	/**
	 * Checks if a specific {@code File} can be loaded by the system.<br>
	 * It verifies that the file exists and is not a directory.<br>
	 * It also ensures the file matches the required naming criteria.
	 * @param file The {@code File} object to validate.
	 * @return {@code true} if the file is valid for loading, otherwise {@code false}.
	 */
	private boolean isLoadable(File file)
	{
		return file.exists() && !file.isDirectory() && HTML_FILTER.accept(file);
	}
	
	/**
	 * Checks if a specific path is currently stored in the cache.<br>
	 * This method returns {@code true} if the path exists.<br>
	 * It returns {@code false} otherwise.
	 * @param path The string path to check for in the cache.
	 * @return {@code true} if the path is found, {@code false} if it is not.
	 */
	public boolean pathExists(String path)
	{
		return cache.containsKey(path);
	}
	
	/**
	 * Returns a string representation of the cache status.<br>
	 * It shows the total size in kilobytes and the number of files loaded.
	 * @return A formatted string containing the cache statistics.
	 */
	@Override
	public String toString()
	{
		return "Cache[HTML]: " + String.format("%.3f", (float) size / 1024) + " kilobytes on " + loadedFiles + " file(s) loaded.";
	}
	
	/**
	 * Calculates the relative path of a {@code File} from a base directory.<br>
	 * This method returns the portion of the path that follows the {@code base} path.<br>
	 * It is useful for determining file locations within a specific root folder.
	 * @param base The starting directory used as the reference point.
	 * @param file The target file to calculate the path for.
	 * @return A {@code String} representing the relative path.
	 */
	public static String getRelativePath(File base, File file)
	{
		return file.toURI().getPath().substring(base.toURI().getPath().length());
	}
}
