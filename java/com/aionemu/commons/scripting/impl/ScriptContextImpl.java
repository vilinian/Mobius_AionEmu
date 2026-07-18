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
package com.aionemu.commons.scripting.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.CompilationResult;
import com.aionemu.commons.scripting.ScriptCompiler;
import com.aionemu.commons.scripting.ScriptContext;
import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;

/**
 * This class provides the concrete implementation for {@link ScriptContext}.<br>
 * It manages the environment and state required for executing scripts within the system.
 * @author SoulKeeper
 */
public class ScriptContextImpl implements ScriptContext
{
	/**
	 * logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(ScriptContextImpl.class);
	
	/**
	 * Script context that is parent for this script context
	 */
	private final ScriptContext parentScriptContext;
	
	/**
	 * Libraries (list of jar files) that have to be loaded class loader
	 */
	private Iterable<File> libraries;
	
	/**
	 * Root directory of this script context. It and it's subdirectories will be scanned for .java files.
	 */
	private final File root;
	
	/**
	 * Result of compilation of script context
	 */
	private CompilationResult compilationResult;
	
	/**
	 * List of child script contexts
	 */
	private Set<ScriptContext> childScriptContexts;
	
	/**
	 * Classlistener for this script context
	 */
	private ClassListener classListener;
	
	/**
	 * Class name of the compiler that will be used to compile sources
	 */
	private String compilerClassName;
	
	/**
	 * Creates a new instance of {@link ScriptContextImpl}.<br>
	 * This constructor initializes the context with no parent.<br>
	 * It uses the provided {@code root} directory for script files.
	 * @param root The base directory for scanning source files.
	 */
	public ScriptContextImpl(File root)
	{
		this(root, null);
	}
	
	/**
	 * Creates a new {@link ScriptContextImpl} with a specific root directory.<br>
	 * This constructor also sets the parent context for the hierarchy.
	 * @param root The base directory to scan for script files.
	 * @param parent The parent {@link ScriptContext} for this instance.
	 */
	public ScriptContextImpl(File root, ScriptContext parent)
	{
		if (root == null)
		{
			throw new NullPointerException("Root file must be specified");
		}
		
		if (!root.exists() || !root.isDirectory())
		{
			throw new IllegalArgumentException("Root directory not exists or is not a directory");
		}
		
		this.root = root;
		parentScriptContext = parent;
	}
	
	/**
	 * Initializes the script context and its components.<br>
	 * This method prepares the {@code ScriptCompiler}, loads files from the root directory, and compiles them.<br>
	 * It also initializes all child {@link ScriptContext} objects if they exist.
	 */
	@Override
	public synchronized void init()
	{
		if (compilationResult != null)
		{
			log.error("Init request on initialized ScriptContext");
			return;
		}
		
		final ScriptCompiler scriptCompiler = instantiateCompiler();
		
		final String[] extensions = scriptCompiler.getSupportedFileTypes();
		final Collection<File> files;
		try (Stream<Path> stream = Files.walk(root.toPath(), Integer.MAX_VALUE))
		{
			files = stream.filter(Files::isRegularFile).filter(p ->
			{
				final String n = p.getFileName().toString();
				for (String ext : extensions)
				{
					if (n.endsWith("." + ext))
					{
						return true;
					}
				}
				return false;
			}).map(Path::toFile).collect(Collectors.toList());
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to list script files in " + root.getAbsolutePath(), e);
		}
		
		if (parentScriptContext != null)
		{
			scriptCompiler.setParentClassLoader(parentScriptContext.getCompilationResult().getClassLoader());
		}
		
		scriptCompiler.setLibraires(libraries);
		compilationResult = scriptCompiler.compile(files);
		
		getClassListener().postLoad(compilationResult.getCompiledClasses());
		
		if (childScriptContexts != null)
		{
			for (ScriptContext context : childScriptContexts)
			{
				context.init();
			}
		}
	}
	
	/**
	 * Shuts down the current script context.<br>
	 * This method stops all child {@link ScriptContext} instances.<br>
	 * It also triggers the pre-unload process for compiled classes.<br>
	 * The {@code compilationResult} is set to {@code null} after completion.
	 */
	@Override
	public synchronized void shutdown()
	{
		if (compilationResult == null)
		{
			log.error("Shutdown of not initialized stript context", new Exception());
			return;
		}
		
		if (childScriptContexts != null)
		{
			for (ScriptContext child : childScriptContexts)
			{
				child.shutdown();
			}
		}
		
		getClassListener().preUnload(compilationResult.getCompiledClasses());
		compilationResult = null;
	}
	
	/**
	 * Reloads the current script context.<br>
	 * This method calls {@code shutdown} to clear existing resources.<br>
	 * It then calls {@code init} to restart the context.
	 */
	@Override
	public void reload()
	{
		shutdown();
		init();
	}
	
	/**
	 * Retrieves the root directory for this script context.<br>
	 * This directory is used to scan for {@code .java} files.
	 * @return the {@code File} object representing the root directory.
	 */
	@Override
	public File getRoot()
	{
		return root;
	}
	
	/**
	 * Retrieves the result of the script compilation.<br>
	 * This method returns the {@code CompilationResult} object associated with this context.
	 * @return The {@code CompilationResult} of the current script context.
	 */
	@Override
	public CompilationResult getCompilationResult()
	{
		return compilationResult;
	}
	
	/**
	 * Checks if the script context has been initialized.<br>
	 * This method returns {@code true} if the {@link CompilationResult} is not {@code null}.
	 * @return {@code true} if initialized, {@code false} otherwise.
	 */
	@Override
	public synchronized boolean isInitialized()
	{
		return compilationResult != null;
	}
	
	/**
	 * Sets the list of library files for this script context.<br>
	 * These files will be loaded by the class loader.
	 * @param files The {@code Iterable<File>} containing the library files to load.
	 */
	@Override
	public void setLibraries(Iterable<File> files)
	{
		libraries = files;
	}
	
	/**
	 * Retrieves the list of library files for this script context.<br>
	 * These are the {@code java.io.File} objects that need to be loaded by the class loader.
	 * @return an {@link Iterable} containing all library {@code File} objects.
	 */
	@Override
	public Iterable<File> getLibraries()
	{
		return libraries;
	}
	
	/**
	 * Retrieves the parent {@link ScriptContext} for this context.<br>
	 * This is useful when navigating up the hierarchy of nested scripts.
	 * @return The parent {@code ScriptContext} or {@code null} if no parent exists.
	 */
	@Override
	public ScriptContext getParentScriptContext()
	{
		return parentScriptContext;
	}
	
	/**
	 * Retrieves all child {@link ScriptContext} objects.<br>
	 * This method returns the collection of nested contexts associated with this instance.
	 * @return a {@code Collection} of {@link ScriptContext} children.
	 */
	@Override
	public Collection<ScriptContext> getChildScriptContexts()
	{
		return childScriptContexts;
	}
	
	/**
	 * Adds a new child {@link ScriptContext} to this context.<br>
	 * This method ensures the child is initialized if this context is already active.<br>
	 * It prevents adding the same {@code ScriptContext} more than once.
	 * @param context The {@link ScriptContext} to add as a child.
	 */
	@Override
	public void addChildScriptContext(ScriptContext context)
	{
		synchronized (this)
		{
			if (childScriptContexts == null)
			{
				childScriptContexts = new HashSet<>();
			}
			
			if (childScriptContexts.contains(context))
			{
				log.error("Double child definition, root: " + root.getAbsolutePath() + ", child: " + context.getRoot().getAbsolutePath());
				return;
			}
			
			if (isInitialized())
			{
				context.init();
			}
		}
		
		childScriptContexts.add(context);
	}
	
	/**
	 * Sets the listener for class loading events.<br>
	 * This method assigns a {@link ClassListener} to this context.
	 * @param cl The {@code ClassListener} to be used.
	 */
	@Override
	public void setClassListener(ClassListener cl)
	{
		classListener = cl;
	}
	
	/**
	 * Retrieves the {@link ClassListener} for this script context.<br>
	 * If no listener is set, it provides a default one or inherits from the parent.
	 * @return the {@code ClassListener} instance associated with this context.
	 */
	@Override
	public ClassListener getClassListener()
	{
		if (classListener == null)
		{
			if (getParentScriptContext() == null)
			{
				final AggregatedClassListener acl = new AggregatedClassListener();
				acl.addClassListener(new OnClassLoadUnloadListener());
				acl.addClassListener(new ScheduledTaskClassListener());
				setClassListener(acl);
				return classListener;
			}
			
			return getParentScriptContext().getClassListener();
		}
		
		return classListener;
	}
	
	/**
	 * Sets the name of the compiler class used to compile sources.<br>
	 * This value is used by {@code instantiateCompiler} to create a new compiler instance.
	 * @param className The fully qualified name of the compiler class.
	 */
	@Override
	public void setCompilerClassName(String className)
	{
		compilerClassName = className;
	}
	
	/**
	 * Retrieves the name of the compiler class used for compiling sources.<br>
	 * This value is set via {@code setCompilerClassName}.
	 * @return The name of the compiler class as a {@code String}.
	 */
	@Override
	public String getCompilerClassName()
	{
		return compilerClassName;
	}
	
	/**
	 * Creates a new instance of the {@link ScriptCompiler}.<br>
	 * It uses the class name stored in this context to load the compiler.<br>
	 * The loader is determined by the parent script context if one exists.
	 * @return A new instance of {@code ScriptCompiler}.
	 * @throws RuntimeException
	 */
	protected ScriptCompiler instantiateCompiler() throws RuntimeException
	{
		ClassLoader cl = getClass().getClassLoader();
		if (getParentScriptContext() != null)
		{
			cl = getParentScriptContext().getCompilationResult().getClassLoader();
		}
		
		ScriptCompiler sc;
		try
		{
			sc = (ScriptCompiler) Class.forName(getCompilerClassName(), true, cl).getDeclaredConstructor().newInstance();
		}
		catch (Exception e)
		{
			log.error("Can't create instance of compiler");
			throw new RuntimeException(e);
		}
		
		return sc;
	}
	
	/**
	 * Compares this object with another object for equality.<br>
	 * It checks if both objects are of type {@code ScriptContextImpl}.<br>
	 * Two contexts are equal if they have the same root and parent context.
	 * @param obj The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ScriptContextImpl))
		{
			return false;
		}
		
		final ScriptContextImpl another = (ScriptContextImpl) obj;
		
		if (parentScriptContext == null)
		{
			return another.getRoot().equals(root);
		}
		
		return another.getRoot().equals(root) && parentScriptContext.equals(another.parentScriptContext);
	}
	
	/**
	 * Returns a hash code value for this {@link ScriptContextImpl} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code parentScriptContext} and {@code root} fields.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		int result = parentScriptContext != null ? parentScriptContext.hashCode() : 0;
		result = (31 * result) + root.hashCode();
		return result;
	}
	
}
