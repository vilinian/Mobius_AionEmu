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
package com.aionemu.commons.scripting.scriptmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.ScriptCompiler;
import com.aionemu.commons.scripting.ScriptContext;
import com.aionemu.commons.scripting.ScriptContextFactory;
import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.scripting.impl.javacompiler.ScriptCompilerImpl;

/**
 * Class that represents managers of script contexts. It loads, reloads and unload script contexts. In the future it may be extended to support programatic manipulation of contexts, but for now it's not needed. <br />
 * Example:
 * 
 * <pre>
 *      ScriptManager sm = new ScriptManager();
 *      sm.load(new File(&quot;st/contexts.xml&quot;));
 *      ...
 *      sm.shutdown();
 * </pre>
 * 
 * <br>
 * @author SoulKeeper, Aquanox
 */
public class ScriptManager
{
	/**
	 * Logger for script context
	 */
	private static final Logger log = LoggerFactory.getLogger(ScriptManager.class);
	
	public static final Class<? extends ScriptCompiler> DEFAULT_COMPILER_CLASS = ScriptCompilerImpl.class;
	
	/**
	 * Collection of script contexts
	 */
	private final Set<ScriptContext> contexts = new HashSet<>();
	
	/**
	 * Global ClassListener instance. Automatically assigned for each new context. Fires after each successful compilation.
	 */
	private ClassListener globalClassListener;
	
	/**
	 * Loads script contexts from a descriptor file.<br>
	 * This method reads the {@code File} and initializes each context found inside.<br>
	 * It uses {@code ScriptContext)} to build the new contexts.
	 * @param scriptDescriptor The {@code File} containing the script definitions.
	 * @throws Exception If there is an error reading or parsing the file.
	 */
	public synchronized void load(File scriptDescriptor) throws Exception
	{
		final FileInputStream fin = new FileInputStream(scriptDescriptor);
		final JAXBContext c = JAXBContext.newInstance(ScriptInfo.class, ScriptList.class);
		final Unmarshaller u = c.createUnmarshaller();
		
		ScriptList list = null;
		try
		{
			list = (ScriptList) u.unmarshal(fin);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			fin.close();
		}
		
		for (ScriptInfo si : list.getScriptInfos())
		{
			final ScriptContext context = createContext(si, null);
			if (context != null)
			{
				contexts.add(context);
				context.init();
			}
		}
	}
	
	/**
	 * Loads all scripts and libraries from a specific folder.<br>
	 * This method scans the {@code directory} for {@code jar} files.<br>
	 * It uses the default compiler class to process the contents.
	 * @param directory The {@code File} path where the scripts are located.
	 * @throws RuntimeException If an error occurs while loading the directory.
	 */
	public synchronized void loadDirectory(File directory) throws RuntimeException
	{
		final String[] extensions =
		{
			"jar"
		};
		final Collection<File> libraries;
		try (Stream<Path> stream = Files.walk(directory.toPath(), Integer.MAX_VALUE))
		{
			libraries = stream.filter(Files::isRegularFile).filter(p ->
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
			throw new RuntimeException("Failed to load script context from directory " + directory.getAbsolutePath(), e);
		}
		final List<File> list = new ArrayList<>(libraries);
		try
		{
			loadDirectory(directory, list, DEFAULT_COMPILER_CLASS.getName());
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to load script context from directory " + directory.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Loads scripts from a specific directory into the manager.<br>
	 * This method initializes a new {@link ScriptContext} using the provided libraries and compiler class.
	 * @param directory The folder containing the script files to load.
	 * @param libraries A list of additional library files to include during compilation.
	 * @param compilerClassName The name of the class used for compiling scripts.
	 * @throws Exception If an error occurs during context creation or initialization.
	 */
	public synchronized void loadDirectory(File directory, List<File> libraries, String compilerClassName) throws Exception
	{
		if (!directory.isDirectory())
		{
			throw new IllegalArgumentException("File should be directory");
		}
		
		final ScriptInfo si = new ScriptInfo();
		si.setRoot(directory);
		si.setCompilerClass(compilerClassName);
		si.setScriptInfos(Collections.<ScriptInfo> emptyList());
		si.setLibraries(libraries);
		
		final ScriptContext sc = createContext(si, null);
		contexts.add(sc);
		sc.init();
	}
	
	/**
	 * Creates a new {@link ScriptContext} based on the provided script information.<br>
	 * This method initializes the context with libraries and compiler settings.<br>
	 * It also recursively creates child contexts if they are defined in {@code si}.
	 * @param si The information containing the scripts to be processed.
	 * @param parent The parent context for this script, or {@code null} if it is a root context.
	 * @return The created {@link ScriptContext}, or {@code null} if a duplicate root context is detected.
	 * @throws Exception If an error occurs during context creation.
	 */
	protected ScriptContext createContext(ScriptInfo si, ScriptContext parent) throws Exception
	{
		final ScriptContext context = ScriptContextFactory.getScriptContext(si.getRoot(), parent);
		context.setLibraries(si.getLibraries());
		context.setCompilerClassName(si.getCompilerClass());
		
		if ((parent == null) && contexts.contains(context))
		{
			log.warn("Double root script context definition: " + si.getRoot().getAbsolutePath());
			return null;
		}
		
		if ((si.getScriptInfos() != null) && !si.getScriptInfos().isEmpty())
		{
			for (ScriptInfo child : si.getScriptInfos())
			{
				createContext(child, context);
			}
		}
		
		if ((parent == null) && (globalClassListener != null))
		{
			context.setClassListener(globalClassListener);
		}
		
		return context;
	}
	
	/**
	 * Shuts down all managed script contexts.<br>
	 * This method calls {@code shutdown} on every context in the manager.<br>
	 * It then clears the internal collection of contexts.
	 */
	public synchronized void shutdown()
	{
		for (ScriptContext context : contexts)
		{
			context.shutdown();
		}
		
		contexts.clear();
	}
	
	/**
	 * Reloads all currently loaded script contexts.<br>
	 * This method iterates through the {@code contexts} set.<br>
	 * It calls {@code reloadContext} for each context.
	 */
	public synchronized void reload()
	{
		for (ScriptContext context : contexts)
		{
			reloadContext(context);
		}
	}
	
	/**
	 * This method reloads a specific script context.<br>
	 * It calls the {@code reload()} method on the provided {@code ScriptContext}.
	 * @param ctx The {@code ScriptContext} to be reloaded.
	 */
	public void reloadContext(ScriptContext ctx)
	{
		ctx.reload();
	}
	
	/**
	 * Retrieves all currently loaded script contexts.<br>
	 * This method returns an unmodifiable view of the internal context set.
	 * @return A {@code Collection} containing all {@link ScriptContext} objects.
	 */
	public synchronized Collection<ScriptContext> getScriptContexts()
	{
		return Collections.unmodifiableSet(contexts);
	}
	
	/**
	 * Sets the global listener for class events.<br>
	 * This listener is used by every script context.<br>
	 * It triggers after a successful compilation.
	 * @param instance The {@code ClassListener} to set as the global listener.
	 */
	public void setGlobalClassListener(ClassListener instance)
	{
		globalClassListener = instance;
	}
}
