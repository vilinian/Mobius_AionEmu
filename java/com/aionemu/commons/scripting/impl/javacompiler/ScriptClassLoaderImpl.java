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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.tools.JavaFileObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.ScriptClassLoader;
import com.aionemu.commons.utils.ClassUtils;

/**
 * This classloader is used to load script classes.<br>
 * It maintains a list of available classes to overcome {@code JavaCompiler} limitations.
 * @author SoulKeeper
 */
public class ScriptClassLoaderImpl extends ScriptClassLoader
{
	private static final Logger log = LoggerFactory.getLogger(ScriptClassLoaderImpl.class);
	
	/**
	 * ClassFileManager that is related to this ClassLoader
	 */
	private final ClassFileManager classFileManager;
	
	/**
	 * Creates a new instance of {@link ScriptClassLoaderImpl}.<br>
	 * This constructor initializes the internal {@code classFileManager}.<br>
	 * It sets the parent class loader to the loader of this class.
	 * @param classFileManager The {@code ClassFileManager} used by this class loader.
	 */
	ScriptClassLoaderImpl(ClassFileManager classFileManager)
	{
		super(new URL[] {}, ScriptClassLoaderImpl.class.getClassLoader());
		this.classFileManager = classFileManager;
	}
	
	/**
	 * Creates a new instance of {@link ScriptClassLoaderImpl}.<br>
	 * This constructor initializes the loader with a specific {@code ClassFileManager}.<br>
	 * It also sets the parent {@code ClassLoader} for the hierarchy.
	 * @param classFileManager The manager used to handle script classes.
	 * @param parent The parent {@code ClassLoader} to use for loading dependencies.
	 */
	ScriptClassLoaderImpl(ClassFileManager classFileManager, ClassLoader parent)
	{
		super(new URL[] {}, parent);
		this.classFileManager = classFileManager;
	}
	
	/**
	 * Retrieves the {@code ClassFileManager} associated with this loader.<br>
	 * This manager handles the file operations for compiled classes.
	 * @return the {@link ClassFileManager} instance.
	 */
	public ClassFileManager getClassFileManager()
	{
		return classFileManager;
	}
	
	/**
	 * Retrieves the names of all classes that have been compiled.<br>
	 * This method returns an unmodifiable set of strings.
	 * @return a {@code Set<String>} containing the names of the compiled classes.
	 */
	@Override
	public Set<String> getCompiledClasses()
	{
		final Set<String> compiledClasses = classFileManager.getCompiledClasses().keySet();
		return Collections.unmodifiableSet(compiledClasses);
	}
	
	/**
	 * Retrieves all {@link JavaFileObject} instances for a specific package.<br>
	 * This method searches through the parent classloader and current compiled classes.<br>
	 * It also scans resources and libraries to find matching members.
	 * @param packageName The name of the package to search for.
	 * @return A {@code Set} containing the found {@link JavaFileObject} instances.
	 * @throws IOException If an error occurs while reading class data from the filesystem or JARs.
	 */
	public Set<JavaFileObject> getClassesForPackage(String packageName) throws IOException
	{
		final Set<JavaFileObject> result = new HashSet<>();
		
		// load parent
		final ClassLoader parent = getParent();
		if (parent instanceof ScriptClassLoaderImpl)
		{
			final ScriptClassLoaderImpl pscl = (ScriptClassLoaderImpl) parent;
			result.addAll(pscl.getClassesForPackage(packageName));
			pscl.close();
		}
		
		// load current classloader compiled classes
		for (String cn : classFileManager.getCompiledClasses().keySet())
		{
			if (ClassUtils.isPackageMember(cn, packageName))
			{
				final BinaryClass bc = classFileManager.getCompiledClasses().get(cn);
				result.add(bc);
			}
		}
		
		// initialize set with class names, will be used to resolve classes
		final Set<String> classNames = new HashSet<>();
		
		// load package members from this classloader
		final Enumeration<URL> urls = getResources(packageName.replace('.', '/'));
		while (urls.hasMoreElements())
		{
			final URL url = urls.nextElement();
			String path = URLDecoder.decode(url.getPath(), "UTF-8");
			if (new File(path).isDirectory())
			{
				final Set<String> packageClasses = ClassUtils.getClassNamesFromPackage(new File(path), packageName, false);
				classNames.addAll(packageClasses);
			}
			else if (path.toLowerCase().contains(".jar!"))
			{
				File file = new File(path);
				while (!file.getName().toLowerCase().endsWith(".jar!"))
				{
					file = file.getParentFile();
				}
				
				path = file.getPath().substring(0, file.getPath().length() - 1);
				path = path.replace('\\', '/');
				path = path.substring(path.indexOf(":") + 1);
				file = new File(path);
				
				// Add the JAR file as a library, although it does not matter if it is included as a library or a file in the classpath.
				addJarFile(file);
			}
		}
		
		// add library class names from this classloader to available classes
		classNames.addAll(getLibraryClassNames());
		
		// load classes for class names from this classloader
		for (String cn : classNames)
		{
			if (ClassUtils.isPackageMember(cn, packageName))
			{
				final BinaryClass bc = new BinaryClass(cn);
				try
				{
					final byte[] data = getRawClassByName(cn);
					final OutputStream os = bc.openOutputStream();
					os.write(data);
				}
				catch (IOException e)
				{
					log.error("Error while loading class from package " + packageName, e);
					throw e;
				}
				
				result.add(bc);
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieves the raw byte array of a class file by its name.<br>
	 * This method locates the resource and reads its content into memory.
	 * @param name The fully qualified name of the class to load.
	 * @return A {@code byte[]} containing the compiled class data.
	 * @throws IOException If an error occurs while reading the file.
	 */
	protected byte[] getRawClassByName(String name) throws IOException
	{
		final String resourceName = name.replace('.', '/').concat(".class");
		final URL resource = getResource(resourceName);
		InputStream is = null;
		byte[] clazz = null;
		
		try
		{
			is = resource.openStream();
			clazz = is.readAllBytes();
		}
		catch (IOException e)
		{
			log.error("Error while loading class data: " + name, e);
			throw e;
		}
		catch (NullPointerException e)
		{
			log.error("Can't open input stream for resource: " + name);
			throw new IllegalArgumentException("Failed to open input stream for resource: " + name);
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					log.error("Error while closing stream", e);
				}
			}
		}
		
		return clazz;
	}
	
	/**
	 * Retrieves the compiled bytecode for a specific class.<br>
	 * This method looks up the class in the {@link ClassFileManager}.<br>
	 * It returns a copy of the byte array representing the class file.
	 * @param className The fully qualified name of the class to retrieve.
	 * @return A {@code byte[]} containing the compiled bytecode.
	 */
	@Override
	public byte[] getByteCode(String className)
	{
		final BinaryClass bc = getClassFileManager().getCompiledClasses().get(className);
		final byte[] b = new byte[bc.getBytes().length];
		System.arraycopy(bc.getBytes(), 0, b, 0, b.length);
		return b;
	}
	
	/**
	 * Retrieves the {@code Class<?>} object for a given class name.<br>
	 * It looks up the compiled class in the internal manager.<br>
	 * Returns {@code null} if the class is not found.
	 * @param name The fully qualified name of the class to retrieve.
	 * @return The {@code Class<?>} object, or {@code null} if it does not exist.
	 */
	@Override
	public Class<?> getDefinedClass(String name)
	{
		final BinaryClass bc = classFileManager.getCompiledClasses().get(name);
		if (bc == null)
		{
			return null;
		}
		
		return bc.getDefinedClass();
	}
	
	/**
	 * Registers a specific {@code Class<?>} instance for a given name.<br>
	 * This method links a compiled class to its loaded representation.<br>
	 * It will throw an {@code IllegalArgumentException} if the name was not previously compiled.
	 * @param name The name of the class to associate with the provided class object.
	 * @param clazz The {@code Class<?>} instance that represents the defined class.
	 */
	@Override
	public void setDefinedClass(String name, Class<?> clazz)
	{
		final BinaryClass bc = classFileManager.getCompiledClasses().get(name);
		
		if (bc == null)
		{
			throw new IllegalArgumentException("Attempt to set defined class for class that was not compiled?");
		}
		
		bc.setDefinedClass(clazz);
	}
}
