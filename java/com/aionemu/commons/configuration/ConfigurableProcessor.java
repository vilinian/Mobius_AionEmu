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
package com.aionemu.commons.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class processes classes and interfaces containing fields annotated with {@link Property}.<br>
 * It handles the configuration logic for these components.<br>
 * Use this class to manage property-based configurations across the system.
 * @author SoulKeeper
 */
public class ConfigurableProcessor
{
	private static final Logger log = LoggerFactory.getLogger(ConfigurableProcessor.class);
	
	/**
	 * Processes fields marked with the {@link Property} annotation.<br>
	 * This method handles both class types and specific instances.<br>
	 * It uses the provided {@code Properties} to map keys to values.
	 * @param object The {@code Class} or instance to process.
	 * @param properties A variable number of {@code Properties} objects used for lookups.
	 */
	public static void process(Object object, Properties... properties)
	{
		Class<?> clazz;
		
		if (object instanceof Class)
		{
			clazz = (Class<?>) object;
			object = null;
		}
		else
		{
			clazz = object.getClass();
		}
		
		process(clazz, object, properties);
	}
	
	/**
	 * Processes fields for a specific class and its hierarchy.<br>
	 * This method handles both instance and static properties.<br>
	 * It recursively explores interfaces and superclasses.
	 * @param clazz The {@code Class<?>} to be processed.
	 * @param obj The object instance to use for non-static field values.
	 * @param props An array of {@code Properties} used to look up configuration keys.
	 */
	private static void process(Class<?> clazz, Object obj, Properties[] props)
	{
		processFields(clazz, obj, props);
		
		// Since interfaces cannot contain instance fields, only classes can be parsed for object instances.
		if (obj == null)
		{
			for (Class<?> itf : clazz.getInterfaces())
			{
				process(itf, obj, props);
			}
		}
		
		final Class<?> superClass = clazz.getSuperclass();
		if ((superClass != null) && (superClass != Object.class))
		{
			process(superClass, obj, props);
		}
	}
	
	/**
	 * Iterates through all declared fields of a specific class.<br>
	 * It identifies fields marked with the {@link Property} annotation.<br>
	 * The method filters out static fields based on whether an instance is provided.<br>
	 * It calls {@code Object, java.util.Properties[])} for each valid field.
	 * @param clazz The class to inspect for fields.
	 * @param obj The object instance to use if processing non-static fields.
	 * @param props An array of {@link Properties} used to look up configuration values.
	 */
	private static void processFields(Class<?> clazz, Object obj, Properties[] props)
	{
		for (Field f : clazz.getDeclaredFields())
		{
			// Static fields should not be modified or processed when parsing classes.
			if ((Modifier.isStatic(f.getModifiers()) && (obj != null)) || (!Modifier.isStatic(f.getModifiers()) && (obj == null)))
			{
				continue;
			}
			
			if (f.isAnnotationPresent(Property.class))
			{
				// Final fields should not be processed
				if (Modifier.isFinal(f.getModifiers()))
				{
					log.error("Attempt to proceed final field " + f.getName() + " of class " + clazz.getName());
					throw new RuntimeException();
				}
				
				processField(f, obj, props);
			}
		}
	}
	
	/**
	 * Processes a single {@code Field} to update its value.<br>
	 * It checks if the field has a {@link Property} annotation.<br>
	 * The value is updated if it differs from the default or exists in the properties.
	 * @param f The {@code Field} to be processed.
	 * @param obj The object instance where the field value will be set.
	 * @param props An array of {@code Properties} used to look up values.
	 */
	private static void processField(Field f, Object obj, Properties[] props)
	{
		f.setAccessible(true);
		try
		{
			final Property property = f.getAnnotation(Property.class);
			if (!Property.DEFAULT_VALUE.equals(property.defaultValue()) || isKeyPresent(property.key(), props))
			{
				f.set(obj, getFieldValue(f, props));
			}
			else if (log.isDebugEnabled())
			{
				log.debug("Field " + f.getName() + " of class " + f.getDeclaringClass().getName() + " wasn't modified");
			}
		}
		catch (Exception e)
		{
			log.error("Can't transform field " + f.getName() + " of class " + f.getDeclaringClass());
			throw new RuntimeException();
		}
		
	}
	
	/**
	 * Retrieves and transforms the value for a specific {@code Field}.<br>
	 * It looks up the value in the provided {@code Properties} array using the key from the {@link Property} annotation.<br>
	 * If no value is found, it falls back to the default value defined in the annotation.<br>
	 * The final result is processed by a {@link PropertyTransformer}.
	 * @param field The {@code Field} to retrieve the value from.
	 * @param props An array of {@code Properties} used to look up configuration values.
	 * @return The transformed value as an {@code Object}.
	 * @throws TransformationException If an error occurs during the transformation process.
	 */
	private static Object getFieldValue(Field field, Properties[] props) throws TransformationException
	{
		final Property property = field.getAnnotation(Property.class);
		final String defaultValue = property.defaultValue();
		final String key = property.key();
		String value = null;
		
		if (key.isEmpty())
		{
			log.warn("Property " + field.getName() + " of class " + field.getDeclaringClass().getName() + " has empty key");
		}
		else
		{
			value = findPropertyByKey(key, props);
		}
		
		if ((value == null) || value.trim().equals(""))
		{
			value = defaultValue;
			if (log.isDebugEnabled())
			{
				log.debug("Using default value for field " + field.getName() + " of class " + field.getDeclaringClass().getName());
			}
		}
		
		final PropertyTransformer<?> pt = PropertyTransformerFactory.newTransformer(field.getType(), property.propertyTransformer());
		return pt.transform(value, field);
	}
	
	/**
	 * Searches for a specific property value across an array of {@code Properties}.<br>
	 * It returns the first value found that matches the provided {@code key}.<br>
	 * If no match is found, it returns {@code null}.
	 * @param key The name of the property to look for.
	 * @param props An array of {@code Properties} objects to search through.
	 * @return The value associated with the {@code key}, or {@code null} if not found.
	 */
	private static String findPropertyByKey(String key, Properties[] props)
	{
		for (Properties p : props)
		{
			if (p.containsKey(key))
			{
				return p.getProperty(key);
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if a specific key exists within an array of {@code Properties}.<br>
	 * It calls the {@code Properties[])} method to perform the search.
	 * @param key The string name of the key to look for.
	 * @param props An array of {@code Properties} objects to search through.
	 * @return {@code true} if the key is found, or {@code false} otherwise.
	 */
	private static boolean isKeyPresent(String key, Properties[] props)
	{
		return findPropertyByKey(key, props) != null;
	}
}
