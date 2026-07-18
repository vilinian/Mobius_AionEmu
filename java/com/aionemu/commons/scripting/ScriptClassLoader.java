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
package com.aionemu.commons.scripting;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.url.VirtualClassURLStreamHandler;
import com.aionemu.commons.utils.ClassUtils;

/**
 * This is an abstract class loader designed to be extended by specific script-related class loaders.<br>
 * It can optionally wrap another {@code ClassLoader} instance to manage nested dependencies.
 * @author SoulKeeper
 */
public abstract class ScriptClassLoader extends URLClassLoader
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ScriptClassLoader.class);
	
	/**
	 * URL Stream handler to allow valid url generation by {@code getResource}
	 */
	private final VirtualClassURLStreamHandler urlStreamHandler = new VirtualClassURLStreamHandler(this);
	
	/**
	 * Classes that were loaded from libraries. They are no parsed for any annotations, but they are needed by JavaCompiler to perform valid compilation
	 */
	private final Set<String> libraryClassNames = new HashSet<>();
	
	/**
	 * List of jar files that were scanned by this classloader for classes
	 */
	private final Set<File> loadedLibraries = new HashSet<>();
	
	/**
	 * Creates a new instance of {@link ScriptClassLoader}.<br>
	 * This constructor initializes the loader with specific URLs and a parent classloader.<br>
	 * It is used to set up the classpath for script loading.
	 * @param urls An array of {@code URL} objects to search for classes.
	 * @param parent The {@code ClassLoader} to use as the parent.
	 */
	public ScriptClassLoader(URL[] urls, ClassLoader parent)
	{
		super(urls, parent);
	}
	
	/**
	 * Creates a new {@link ScriptClassLoader} using the provided URLs.<br>
	 * This constructor initializes the classloader with the specified search paths.<br>
	 * It uses the system class loader as the parent by default.
	 * @param urls An array of {@code URL} objects to be searched for classes.
	 */
	public ScriptClassLoader(URL[] urls)
	{
		super(urls);
	}
	
	/**
	 * Creates a new {@link ScriptClassLoader} with specific configuration.<br>
	 * This constructor initializes the loader using provided URLs and a custom factory.<br>
	 * It allows for fine-grained control over how classes are loaded from resources.
	 * @param urls The array of {@code URL} objects to search for classes.
	 * @param parent The {@code ClassLoader} used as the parent.
	 * @param factory The {@code URLStreamHandlerFactory} to use for creating new URLs.
	 */
	public ScriptClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory)
	{
		super(urls, parent, factory);
	}
	
	/**
	 * Adds a new {@code .jar} file to the list of loaded libraries.<br>
	 * This method scans the file for class names and registers them.<br>
	 * It ensures that each unique file is only processed once.
	 * @param file The {@code File} object representing the jar to add.
	 * @throws IOException If an error occurs while reading the file.
	 */
	public void addJarFile(File file) throws IOException
	{
		if (!loadedLibraries.contains(file))
		{
			final Set<String> jarFileClasses = ClassUtils.getClassNamesFromJarFile(file);
			libraryClassNames.addAll(jarFileClasses);
			loadedLibraries.add(file);
		}
	}
	
	/**
	 * Retrieves a {@code URL} for the specified resource.<br>
	 * This method handles special logic for files ending in {@code .class}.<br>
	 * It attempts to map compiled classes to virtual URLs if they exist.
	 * @param name The name of the resource to locate.
	 * @return The {@code URL} of the requested resource, or {@code null} if not found.
	 */
	@Override
	public URL getResource(String name)
	{
		if (!name.endsWith(".class"))
		{
			return super.getResource(name);
		}
		
		String newName = name.substring(0, name.length() - 6);
		newName = newName.replace('/', '.');
		if (getCompiledClasses().contains(newName))
		{
			try
			{
				return URL.of(URI.create(VirtualClassURLStreamHandler.HANDLER_PROTOCOL + newName), urlStreamHandler);
			}
			catch (MalformedURLException e)
			{
				log.error("Can't create url for compiled class", e);
			}
		}
		
		return super.getResource(name);
	}
	
	/**
	 * Loads a class by its fully qualified name.<br>
	 * This method checks if the class is already compiled or defined.<br>
	 * It uses {@code getCompiledClasses} to determine the loading strategy.
	 * @param name The fully qualified name of the class to load.
	 * @return The {@code Class} object corresponding to the provided name.
	 * @throws ClassNotFoundException If the class cannot be found or loaded.
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		final boolean isCompiled = getCompiledClasses().contains(name);
		if (!isCompiled)
		{
			return super.loadClass(name, true);
		}
		
		Class<?> c = getDefinedClass(name);
		if (c == null)
		{
			final byte[] b = getByteCode(name);
			c = super.defineClass(name, b, 0, b.length);
			setDefinedClass(name, c);
		}
		
		return c;
	}
	
	/**
	 * Retrieves the names of classes loaded from libraries.<br>
	 * These classes are required for valid compilation by the {@code JavaCompiler}.
	 * @return a {@code Set} containing the class names.
	 */
	protected Set<String> getLibraryClassNames()
	{
		return Collections.unmodifiableSet(libraryClassNames);
	}
	
	/**
	 * Retuns unmodifiable set of class names that were compiled
	 * @return unmodifiable set of class names that were compiled
	 */
	public abstract Set<String> getCompiledClasses();
	
	/**
	 * Returns bytecode for given className. Array is copy of actual bytecode, so modifications will not harm.
	 * @param className class name
	 * @return bytecode
	 */
	public abstract byte[] getByteCode(String className);
	
	/**
	 * Returns cached class instance for give name or null if is not cached yet
	 * @param name class name
	 * @return cached class instance or null
	 */
	public abstract Class<?> getDefinedClass(String name);
	
	/**
	 * Sets defined class into cache
	 * @param name class name
	 * @param clazz class object
	 * @throws IllegalArgumentException if class was not loaded by this class loader
	 */
	public abstract void setDefinedClass(String name, Class<?> clazz) throws IllegalArgumentException;
}
