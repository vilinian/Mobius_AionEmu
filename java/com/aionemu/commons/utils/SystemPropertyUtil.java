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

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a set of utility methods for retrieving and parsing {@code java.lang.System} properties.<br>
 * It simplifies access to configuration values throughout the application.
 */
public final class SystemPropertyUtil
{
	private static final Logger logger = LoggerFactory.getLogger(SystemPropertyUtil.class);
	private static boolean loggedException;
	
	/**
	 * Checks if a specific system property exists.<br>
	 * This method calls {@code get} to verify the value.
	 * @param key The name of the system property to check.
	 * @return {@code true} if the property is found, or {@code false} otherwise.
	 */
	public static boolean contains(String key)
	{
		return get(key) != null;
	}
	
	/**
	 * Retrieves the value of a system property using its unique key.<br>
	 * This method returns {@code null} if the key does not exist.
	 * @param key The name of the system property to look up.
	 * @return The string value of the property, or {@code null}.
	 */
	public static String get(String key)
	{
		return get(key, null);
	}
	
	/**
	 * Retrieves a system property value based on the provided {@code key}.<br>
	 * If the property does not exist, it returns the default value.
	 * @param key The name of the system property to retrieve.
	 * @param def The default value to return if the property is missing.
	 * @return The value of the property or the {@code def} string.
	 */
	public static String get(String key, String def)
	{
		if (key == null)
		{
			throw new NullPointerException("key");
		}
		
		if (key.isEmpty())
		{
			throw new IllegalArgumentException("key must not be empty.");
		}
		
		String value = null;
		try
		{
			value = System.getProperty(key);
		}
		catch (Exception e)
		{
			if (!loggedException)
			{
				log("Unable to retrieve a system property '" + key + "'; default values will be used.", e);
				loggedException = true;
			}
		}
		
		if (value == null)
		{
			return def;
		}
		
		return value;
	}
	
	/**
	 * Retrieves a boolean value from the system properties.<br>
	 * It returns {@code true} for values like {@code true}, {@code yes}, or {@code 1}.<br>
	 * It returns {@code false} for values like {@code false}, {@code no}, or {@code 0}.<br>
	 * If the key is missing or invalid, it returns the default value.
	 * @param key The name of the system property to look up.
	 * @param def The default value to return if the property is not found or cannot be parsed.
	 * @return The boolean value associated with the key, or the default value.
	 */
	public static boolean getBoolean(String key, boolean def)
	{
		String value = get(key);
		if (value == null)
		{
			return def;
		}
		
		value = value.trim().toLowerCase();
		if (value.isEmpty() || "true".equals(value) || "yes".equals(value) || "1".equals(value))
		{
			return true;
		}
		
		if ("false".equals(value) || "no".equals(value) || "0".equals(value))
		{
			return false;
		}
		
		log("Unable to parse the boolean system property '" + key + "':" + value + " - " + "using the default value: " + def);
		
		return def;
	}
	
	private static final Pattern INTEGER_PATTERN = Pattern.compile("-?[0-9]+");
	
	/**
	 * Retrieves an integer value from a system property.<br>
	 * It returns the default value if the key is missing or invalid.<br>
	 * The method automatically trims and converts the string to lowercase.
	 * @param key The name of the system property to look up.
	 * @param def The default value to return if no valid integer is found.
	 * @return The parsed integer value or the provided default.
	 */
	public static int getInt(String key, int def)
	{
		String value = get(key);
		if (value == null)
		{
			return def;
		}
		
		value = value.trim().toLowerCase();
		if (INTEGER_PATTERN.matcher(value).matches())
		{
			try
			{
				return Integer.parseInt(value);
			}
			catch (Exception e)
			{
				// Ignore
			}
		}
		
		log("Unable to parse the integer system property '" + key + "':" + value + " - " + "using the default value: " + def);
		
		return def;
	}
	
	/**
	 * Retrieves a {@code long} value from the system properties.<br>
	 * It uses the provided {@code key} to find the property.<br>
	 * If the property is missing or not a valid number, it returns the {@code def} value.
	 * @param key The name of the system property to retrieve.
	 * @param def The default value to return if the property is not found or invalid.
	 * @return The parsed {@code long} value or the default value.
	 */
	public static long getLong(String key, long def)
	{
		String value = get(key);
		if (value == null)
		{
			return def;
		}
		
		value = value.trim().toLowerCase();
		if (INTEGER_PATTERN.matcher(value).matches())
		{
			try
			{
				return Long.parseLong(value);
			}
			catch (Exception e)
			{
				// Ignore
			}
		}
		
		log("Unable to parse the long integer system property '" + key + "':" + value + " - " + "using the default value: " + def);
		
		return def;
	}
	
	/**
	 * Logs a warning message.
	 * @param msg The message to be logged.
	 */
	private static void log(String msg)
	{
		logger.warn(msg);
	}
	
	/**
	 * Logs a warning message and an associated exception.
	 * @param msg The message to be logged.
	 * @param e The exception to be logged.
	 */
	private static void log(String msg, Exception e)
	{
		logger.warn(msg, e);
	}
	
	/**
	 * Private constructor for the {@link SystemPropertyUtil} class.<br>
	 * This prevents other classes from creating new instances of this utility class.
	 */
	private SystemPropertyUtil()
	{
		// Unused
	}
}
