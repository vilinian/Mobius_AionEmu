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
package com.aionemu.commons.scripting.impl.javacompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import com.aionemu.commons.scripting.ScriptClassLoader;

/**
 * This class manages loaded classes and handles the requirements of the {@link JavaCompiler}.<br>
 * Since the compiler does not natively support custom classloaders, this manager manually provides class data during compilation.
 * @author SoulKeeper
 */
public class ClassFileManager extends ForwardingJavaFileManager<JavaFileManager>
{
	/**
	 * This map contains classes compiled for this classloader
	 */
	private final Map<String, BinaryClass> compiledClasses = new HashMap<>();
	
	/**
	 * Classloader that will be used to load compiled classes
	 */
	protected ScriptClassLoaderImpl loader;
	
	/**
	 * Parent classloader for loader
	 */
	protected ScriptClassLoader parentClassLoader;
	
	/**
	 * Creates a new instance of {@link ClassFileManager}.<br>
	 * This constructor initializes the manager using the provided compiler and listener.<br>
	 * It sets up the internal file manager required for compilation tasks.
	 * @param compiler The {@code JavaCompiler} used to compile the source code.
	 * @param listener The {@code DiagnosticListener} that reports errors during compilation.
	 */
	public ClassFileManager(JavaCompiler compiler, DiagnosticListener<? super JavaFileObject> listener)
	{
		super(compiler.getStandardFileManager(listener, null, null));
	}
	
	/**
	 * Creates and returns a {@link JavaFileObject} for a compiled class.<br>
	 * This method maps the {@code className} to its binary representation.<br>
	 * It is used by the compiler to determine where to save output files.
	 * @param location The {@code Location} of the compilation.
	 * @param className The name of the class to be compiled.
	 * @param kind The {@link Kind} of the file object.
	 * @param sibling A sibling {@link FileObject} for context.
	 * @return The generated {@link JavaFileObject} for the class.
	 */
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
	{
		final BinaryClass co = new BinaryClass(className);
		compiledClasses.put(className, co);
		return co;
	}
	
	/**
	 * Retrieves the {@link ScriptClassLoaderImpl} for a specific location.<br>
	 * This method ensures that the internal loader is initialized before returning it.<br>
	 * It uses the parent classloader if one is available.
	 * @param location The {@code Location} associated with the requested classloader.
	 * @return The initialized {@code ScriptClassLoaderImpl} instance.
	 */
	@Override
	public synchronized ScriptClassLoaderImpl getClassLoader(Location location)
	{
		if (loader == null)
		{
			if (parentClassLoader != null)
			{
				loader = new ScriptClassLoaderImpl(this, parentClassLoader);
			}
			else
			{
				loader = new ScriptClassLoaderImpl(this);
			}
		}
		
		return loader;
	}
	
	/**
	 * Sets the parent {@link ScriptClassLoader} for this manager.<br>
	 * This is used to define where the system should look for classes first.
	 * @param classLoader The {@code ScriptClassLoader} to use as the parent.
	 */
	public void setParentClassLoader(ScriptClassLoader classLoader)
	{
		parentClassLoader = classLoader;
	}
	
	/**
	 * Adds a new library to the script classloader.<br>
	 * This method takes a {@code File} and registers it as a JAR file.<br>
	 * It uses the internal {@code getClassLoader} method to get the loader.
	 * @param file The {@code File} object representing the library to add.
	 * @throws IOException If an error occurs while accessing the file.
	 */
	public void addLibrary(File file) throws IOException
	{
		final ScriptClassLoaderImpl classLoader = getClassLoader(null);
		classLoader.addJarFile(file);
	}
	
	/**
	 * Adds multiple library files to the compiler. <br>
	 * This method iterates through each {@code File} in the provided collection.<br>
	 * It calls {@code addLibrary} for every item found.
	 * @param files A collection of {@code File} objects representing the libraries to add.
	 * @throws IOException If an error occurs while processing any of the files.
	 */
	public void addLibraries(Iterable<File> files) throws IOException
	{
		for (File f : files)
		{
			addLibrary(f);
		}
	}
	
	/**
	 * Retrieves all classes that have been compiled. <br>
	 * This method returns the internal map of compiled data.
	 * @return a {@code Map} containing class names and their corresponding {@link BinaryClass} objects.
	 */
	public Map<String, BinaryClass> getCompiledClasses()
	{
		return compiledClasses;
	}
	
	/**
	 * Retrieves a list of {@link JavaFileObject} items from a specific location.<br>
	 * This method filters files based on the provided package name and types.<br>
	 * It also includes classes from the internal loader if searching the classpath.
	 * @param location The {@code Location} to search for files.
	 * @param packageName The package name to filter by.
	 * @param kinds A set of {@link Kind} values to include.
	 * @param recurse Set to {@code true} to search subdirectories.
	 * @return An {@code Iterable} containing the found {@code JavaFileObject} items.
	 * @throws IOException If an error occurs while accessing the files.
	 */
	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException
	{
		Iterable<JavaFileObject> objects = super.list(location, packageName, kinds, recurse);
		
		if (StandardLocation.CLASS_PATH.equals(location) && kinds.contains(Kind.CLASS))
		{
			final List<JavaFileObject> temp = new ArrayList<>();
			for (JavaFileObject object : objects)
			{
				temp.add(object);
			}
			
			temp.addAll(loader.getClassesForPackage(packageName));
			objects = temp;
		}
		
		return objects;
	}
	
	/**
	 * Determines the binary name for a given {@link JavaFileObject}.<br>
	 * It checks if the file is an instance of {@code BinaryClass}.<br>
	 * If it is, it uses the specific logic from that class.<br>
	 * Otherwise, it falls back to the default behavior.
	 * @param location The location where the file is stored.
	 * @param file The file object to process.
	 * @return The inferred binary name as a {@code String}.
	 */
	@Override
	public String inferBinaryName(Location location, JavaFileObject file)
	{
		if (file instanceof BinaryClass)
		{
			return ((BinaryClass) file).inferBinaryName(null);
		}
		
		return super.inferBinaryName(location, file);
	}
}
