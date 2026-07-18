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
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides helper methods for common operations involving {@code java.lang.Class} objects.<br>
 * These utilities simplify tasks such as loading, inspecting, and managing classes within the application.
 * @author SoulKeeper
 */
public class ClassUtils
{
	private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);
	
	/**
	 * Checks if a class is related to another class.<br>
	 * This method returns {@code true} if the first class is the same as, extends, or implements the second class.<br>
	 * It handles both classes and interfaces.
	 * @param a The class to check.
	 * @param b The parent class or interface to compare against.
	 * @return {@code true} if a is a subclass of b, otherwise {@code false}.
	 */
	public static boolean isSubclass(Class<?> a, Class<?> b)
	{
		// We rely on the fact that every Java class and primitive type has a unique Class object, allowing us to use object equivalence in comparisons.
		if (a == b)
		{
			return true;
		}
		
		if ((a == null) || (b == null))
		{
			return false;
		}
		
		for (Class<?> x = a; x != null; x = x.getSuperclass())
		{
			if (x == b)
			{
				return true;
			}
			
			if (b.isInterface())
			{
				final Class<?>[] interfaces = x.getInterfaces();
				for (Class<?> anInterface : interfaces)
				{
					if (isSubclass(anInterface, b))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific class belongs to a given package.<br>
	 * This method verifies the package name of the {@code clazz}.<br>
	 * It returns {@code true} if the class is part of the specified {@code packageName}.
	 * @param clazz The class to check.
	 * @param packageName The package name to compare against.
	 * @return {@code true} if the class belongs to the package, otherwise {@code false}.
	 */
	public static boolean isPackageMember(Class<?> clazz, String packageName)
	{
		return isPackageMember(clazz.getName(), packageName);
	}
	
	/**
	 * Checks if a class belongs to a specific package.<br>
	 * This method compares the package part of the {@code className} with the provided {@code packageName}.<br>
	 * It returns {@code true} if they match or if the name has no dots and the package is empty.
	 * @param className The full name of the class, including its package.
	 * @param packageName The name of the package to check against.
	 * @return {@code true} if the class is a member of the package, otherwise {@code false}.
	 */
	public static boolean isPackageMember(String className, String packageName)
	{
		if (!className.contains("."))
		{
			return (packageName == null) || packageName.isEmpty();
		}
		
		final String classPackage = className.substring(0, className.lastIndexOf('.'));
		return packageName.equals(classPackage);
	}
	
	/**
	 * Retrieves all class names from a specified folder.<br>
	 * This method scans the {@code directory} and its subdirectories.<br>
	 * It returns a set of strings representing the found classes.
	 * @param directory The {@link File} object pointing to the folder to scan.
	 * @return A {@link Set} containing all discovered class names.
	 * @throws IllegalArgumentException If the provided {@code directory} is not a valid directory or does not exist.
	 */
	public static Set<String> getClassNamesFromDirectory(File directory) throws IllegalArgumentException
	{
		if (!directory.isDirectory() || !directory.exists())
		{
			throw new IllegalArgumentException("Directory " + directory + " doesn't exists or is not directory");
		}
		
		return getClassNamesFromPackage(directory, null, true);
	}
	
	/**
	 * Retrieves a set of class names from a specific package within a directory.<br>
	 * It scans the file system to find all {@code .class} files.<br>
	 * The search can be performed recursively or limited to the top level.
	 * @param directory The root {@code File} where the search begins.
	 * @param packageName The base package name to prepend to the class names.
	 * @param recursive A boolean flag that determines if the method should search subdirectories.
	 * @return A {@code Set<String>} containing all discovered class names.
	 */
	public static Set<String> getClassNamesFromPackage(File directory, String packageName, boolean recursive)
	{
		final Set<String> classes = new HashSet<>();
		if (!directory.exists())
		{
			return classes;
		}
		
		final File[] files = directory.listFiles();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				if (!recursive)
				{
					continue;
				}
				
				String newPackage = file.getName();
				if (!GenericValidator.isBlankOrNull(packageName))
				{
					newPackage = packageName + "." + newPackage;
				}
				
				classes.addAll(getClassNamesFromPackage(file, newPackage, recursive));
			}
			else if (file.getName().endsWith(".class"))
			{
				String className = file.getName().substring(0, file.getName().length() - 6);
				if (!GenericValidator.isBlankOrNull(packageName))
				{
					className = packageName + "." + className;
				}
				
				classes.add(className);
			}
		}
		
		return classes;
	}
	
	/**
	 * This method extracts all class names from a given {@code JarFile}.<br>
	 * It scans the entries and converts file paths into dot-separated names.<br>
	 * The resulting set contains only valid {@code .class} files.
	 * @param file The {@code File} object pointing to the JAR archive.
	 * @return A {@code Set<String>} containing all discovered class names.
	 * @throws IOException If an error occurs while reading the file.
	 */
	public static Set<String> getClassNamesFromJarFile(File file) throws IOException
	{
		if (!file.exists() || file.isDirectory())
		{
			throw new IllegalArgumentException("File " + file + " is not valid jar file");
		}
		
		final Set<String> result = new HashSet<>();
		
		JarFile jarFile = null;
		try
		{
			jarFile = new JarFile(file);
			
			final Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements())
			{
				final JarEntry entry = entries.nextElement();
				
				String name = entry.getName();
				if (name.endsWith(".class"))
				{
					name = name.substring(0, name.length() - 6);
					name = name.replace('/', '.');
					result.add(name);
				}
			}
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
					log.error("Failed to close jar file " + jarFile.getName(), e);
				}
			}
		}
		
		return result;
	}
}
