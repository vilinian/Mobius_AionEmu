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

import java.util.Arrays;

/**
 * This class represents the result of a script compilation within a specific context.<br>
 * It holds information about whether the compilation succeeded or failed.
 * @author SoulKeeper
 */
public class CompilationResult
{
	/**
	 * List of classes that were compiled by compiler
	 */
	private final Class<?>[] compiledClasses;
	
	/**
	 * Classloader that was used to load classes
	 */
	private final ScriptClassLoader classLoader;
	
	/**
	 * Creates a new {@link CompilationResult} instance.<br>
	 * This constructor stores the classes produced by the compiler.<br>
	 * It also saves the {@code ScriptClassLoader} used during the process.
	 * @param compiledClasses The array of {@code Class<?>} objects that were compiled.
	 * @param classLoader The {@link ScriptClassLoader} used to load the classes.
	 */
	public CompilationResult(Class<?>[] compiledClasses, ScriptClassLoader classLoader)
	{
		this.compiledClasses = compiledClasses;
		this.classLoader = classLoader;
	}
	
	/**
	 * Retrieves the {@link ScriptClassLoader} used during compilation.<br>
	 * This allows you to access the specific loader that handled the scripts.
	 * @return The {@code ScriptClassLoader} instance associated with this result.
	 */
	public ScriptClassLoader getClassLoader()
	{
		return classLoader;
	}
	
	/**
	 * Retrieves the list of classes that were successfully compiled.<br>
	 * This method returns the internal array of {@code Class<?>} objects.
	 * @return an array containing all compiled classes.
	 */
	public Class<?>[] getCompiledClasses()
	{
		return compiledClasses;
	}
	
	/**
	 * Returns a string representation of the {@code CompilationResult}.<br>
	 * This includes the {@code classLoader} and {@code compiledClasses} fields.
	 * @return A formatted string describing this result.
	 */
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("CompilationResult");
		sb.append("{classLoader=").append(classLoader);
		sb.append(", compiledClasses=").append(compiledClasses == null ? "null" : Arrays.asList(compiledClasses).toString());
		sb.append('}');
		return sb.toString();
	}
}
