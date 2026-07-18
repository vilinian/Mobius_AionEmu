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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.CompilationResult;
import com.aionemu.commons.scripting.ScriptClassLoader;
import com.aionemu.commons.scripting.ScriptCompiler;

/**
 * This class serves as a wrapper for the standard {@code getTool} {@code JavaCompiler} API.<br>
 * It provides functionality to compile script source files into executable bytecode.<br>
 * It implements the {@link ScriptCompiler} interface to integrate with the project's scripting system.
 * @author SoulKeeper
 */
public class ScriptCompilerImpl implements ScriptCompiler
{
	private static final Logger log = LoggerFactory.getLogger(ScriptCompilerImpl.class);
	
	/**
	 * Instance of JavaCompiler that will be used to compile classes
	 */
	protected final JavaCompiler javaCompiler;
	
	/**
	 * List of jar files
	 */
	protected Iterable<File> libraries;
	
	/**
	 * Parent classloader that has to be used for this compiler
	 */
	protected ScriptClassLoader parentClassLoader;
	
	/**
	 * Creates a new instance of {@link ScriptCompilerImpl}.<br>
	 * This constructor initializes the internal {@code javaCompiler}.<br>
	 * It throws a {@code RuntimeException} if the system compiler is not found.
	 */
	public ScriptCompilerImpl()
	{
		javaCompiler = ToolProvider.getSystemJavaCompiler();
		
		if (javaCompiler == null)
		{
			throw new RuntimeException(new InstantiationException("JavaCompiler is not available. Aion must run on a JDK, not a JRE."));
		}
	}
	
	/**
	 * Sets the parent {@link ScriptClassLoader} for this manager.<br>
	 * This is used to define where the system should look for classes first.
	 * @param classLoader The {@code ScriptClassLoader} to use as the parent.
	 */
	@Override
	public void setParentClassLoader(ScriptClassLoader classLoader)
	{
		parentClassLoader = classLoader;
	}
	
	/**
	 * Sets the list of library files to be used during compilation.<br>
	 * This updates the {@code libraries} field with the provided collection.
	 * @param files The collection of {@link File} objects representing the libraries.
	 */
	@Override
	public void setLibraires(Iterable<File> files)
	{
		libraries = files;
	}
	
	/**
	 * Compiles a single piece of Java source code into a class.<br>
	 * This method takes the class name and the raw source string as input.<br>
	 * It returns a {@link CompilationResult} containing the outcome.
	 * @param className The name of the class to be created.
	 * @param sourceCode The actual Java source code as a string.
	 * @return A {@code CompilationResult} object representing the success or failure of the compilation.
	 */
	@Override
	public CompilationResult compile(String className, String sourceCode)
	{
		return compile(new String[]
		{
			className
		}, new String[]
		{
			sourceCode
		});
	}
	
	/**
	 * Compiles multiple source files into classes.<br>
	 * This method takes arrays of names and code strings.<br>
	 * It ensures that both arrays have the same length.
	 * @param classNames An array of class names to be compiled.
	 * @param sourceCode An array of source code strings corresponding to each class name.
	 * @return A {@code CompilationResult} containing the outcome of the compilation.
	 * @throws IllegalArgumentException If the lengths of {@code classNames} and {@code sourceCode} do not match.
	 */
	@Override
	public CompilationResult compile(String[] classNames, String[] sourceCode) throws IllegalArgumentException
	{
		if (classNames.length != sourceCode.length)
		{
			throw new IllegalArgumentException("Amount of classes is not equal to amount of sources");
		}
		
		final List<JavaFileObject> compilationUnits = new ArrayList<>();
		
		for (int i = 0; i < classNames.length; i++)
		{
			final JavaFileObject compilationUnit = new JavaSourceFromString(classNames[i], sourceCode[i]);
			compilationUnits.add(compilationUnit);
		}
		
		return doCompilation(compilationUnits);
	}
	
	/**
	 * Compiles a collection of source files into bytecode.<br>
	 * This method converts each {@code File} into a {@code JavaFileObject}.<br>
	 * It then delegates the actual compilation to the internal {@code doCompilation} method.
	 * @param compilationUnits A collection of {@code File} objects representing the source files to compile.
	 * @return A {@link CompilationResult} containing the outcome of the compilation process.
	 */
	@Override
	public CompilationResult compile(Iterable<File> compilationUnits)
	{
		final List<JavaFileObject> list = new ArrayList<>();
		
		for (File f : compilationUnits)
		{
			list.add(new JavaSourceFromFile(f, JavaFileObject.Kind.SOURCE));
		}
		
		return doCompilation(list);
	}
	
	/**
	 * Performs the actual compilation of the provided Java files.<br>
	 * This method sets up the compiler options and manages the compilation task.<br>
	 * It returns a {@link CompilationResult} containing the compiled classes.
	 * @param compilationUnits The collection of {@code JavaFileObject} units to compile.
	 * @return A {@code CompilationResult} object containing the results of the compilation.
	 */
	protected CompilationResult doCompilation(Iterable<JavaFileObject> compilationUnits)
	{
		final List<String> options = Arrays.asList("-encoding", "UTF-8", "-g");
		final DiagnosticListener<JavaFileObject> listener = new ErrorListener();
		final ClassFileManager manager = new ClassFileManager(javaCompiler, listener);
		manager.setParentClassLoader(parentClassLoader);
		
		if (libraries != null)
		{
			try
			{
				manager.addLibraries(libraries);
			}
			catch (IOException e)
			{
				log.error("Can't set libraries for compiler.", e);
			}
		}
		
		final JavaCompiler.CompilationTask task = javaCompiler.getTask(null, manager, listener, options, null, compilationUnits);
		
		if (!task.call())
		{
			throw new RuntimeException("Error while compiling classes");
		}
		
		final ScriptClassLoader cl = manager.getClassLoader(null);
		final Class<?>[] compiledClasses = classNamesToClasses(manager.getCompiledClasses().keySet(), cl);
		return new CompilationResult(compiledClasses, cl);
	}
	
	/**
	 * Converts a collection of class names into an array of {@code Class<?>} objects.<br>
	 * This method uses the provided {@link ScriptClassLoader} to load each class.<br>
	 * It throws a {@code RuntimeException} if any class cannot be found.
	 * @param classNames A collection of strings representing the names of the classes to load.
	 * @param cl The {@link ScriptClassLoader} used to load the classes.
	 * @return An array containing the loaded {@code Class<?>} objects.
	 */
	protected Class<?>[] classNamesToClasses(Collection<String> classNames, ScriptClassLoader cl)
	{
		final Class<?>[] classes = new Class<?>[classNames.size()];
		
		int i = 0;
		for (String className : classNames)
		{
			try
			{
				final Class<?> clazz = cl.loadClass(className);
				classes[i] = clazz;
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}
			
			i++;
		}
		
		return classes;
	}
	
	/**
	 * Returns the list of file extensions supported by this compiler.<br>
	 * This method identifies which types of source files can be processed.
	 * @return an array of {@code String} containing the supported file types.
	 */
	@Override
	public String[] getSupportedFileTypes()
	{
		return new String[]
		{
			"java"
		};
	}
}
