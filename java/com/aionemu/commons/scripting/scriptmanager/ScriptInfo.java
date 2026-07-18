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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the metadata and structure of a script.<br>
 * It contains the {@code Script} root, a list of libraries, and a list of child contexts.
 * @author SoulKeeper
 */
@XmlRootElement(name = "scriptinfo")
@XmlAccessorType(XmlAccessType.NONE)
public class ScriptInfo
{
	/**
	 * Root of this script context. Child directories of root will be scanned for script files
	 */
	@XmlAttribute(required = true)
	private File root;
	
	/**
	 * List of libraries of this script context
	 */
	@XmlElement(name = "library")
	private List<File> libraries;
	
	/**
	 * List of child contexts
	 */
	@XmlElement(name = "scriptinfo")
	private List<ScriptInfo> scriptInfos;
	
	/**
	 * Default compiler class name.
	 */
	@XmlElement(name = "compiler")
	private String compilerClass = ScriptManager.DEFAULT_COMPILER_CLASS.getName();
	
	/**
	 * Retrieves the root directory for this script context.<br>
	 * This directory is used to scan for {@code .java} files.
	 * @return the {@code File} object representing the root directory.
	 */
	public File getRoot()
	{
		return root;
	}
	
	/**
	 * Sets the base directory for this script context.<br>
	 * The system will scan child directories of this {@code File} for scripts.
	 * @param root The {@code File} object representing the root directory.
	 */
	public void setRoot(File root)
	{
		this.root = root;
	}
	
	/**
	 * Retrieves the list of library files for this script context.<br>
	 * This method returns all {@code File} objects associated with the libraries.
	 * @return a {@code List} of {@code File} objects representing the libraries.
	 */
	public List<File> getLibraries()
	{
		return libraries;
	}
	
	/**
	 * Sets the list of library files for this script context.<br>
	 * This updates the {@code libraries} field with the provided {@code List<File>}.
	 * @param libraries The list of {@code File} objects to set as libraries.
	 */
	public void setLibraries(List<File> libraries)
	{
		this.libraries = libraries;
	}
	
	/**
	 * Retrieves the list of child script information.<br>
	 * This method returns all {@link ScriptInfo} objects associated with this context.
	 * @return a {@code List} of {@link ScriptInfo} objects.
	 */
	public List<ScriptInfo> getScriptInfos()
	{
		return scriptInfos;
	}
	
	/**
	 * Sets the list of child {@link ScriptInfo} objects.<br>
	 * This updates the nested scripts for this context.
	 * @param scriptInfos The list of {@code ScriptInfo} objects to set.
	 */
	public void setScriptInfos(List<ScriptInfo> scriptInfos)
	{
		this.scriptInfos = scriptInfos;
	}
	
	/**
	 * Retrieves the name of the compiler class.<br>
	 * This value is used to determine which compiler to load.
	 * @return The name of the compiler class as a {@code String}.
	 */
	public String getCompilerClass()
	{
		return compilerClass;
	}
	
	/**
	 * Sets the name of the class used for compiling scripts.<br>
	 * This value is stored in the {@code compilerClass} field.
	 * @param compilerClass The fully qualified name of the compiler class.
	 */
	public void setCompilerClass(String compilerClass)
	{
		this.compilerClass = compilerClass;
	}
	
	/**
	 * Compares this {@link ScriptInfo} object with another object for equality.<br>
	 * It checks if both objects have the same {@code root} file.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		
		if ((o == null) || (getClass() != o.getClass()))
		{
			return false;
		}
		
		final ScriptInfo that = (ScriptInfo) o;
		
		return root.equals(that.root);
	}
	
	/**
	 * Returns a hash code value for this {@link ScriptInfo} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code root} field.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		return root.hashCode();
	}
	
	/**
	 * Returns a string representation of the {@code ScriptInfo} object.<br>
	 * This method includes the root, libraries, compiler class, and child script infos.
	 * @return A formatted string describing this instance.
	 */
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("ScriptInfo");
		sb.append("{root=").append(root);
		sb.append(", libraries=").append(libraries);
		sb.append(", compilerClass='").append(compilerClass).append('\'');
		sb.append(", scriptInfos=").append(scriptInfos);
		sb.append('}');
		return sb.toString();
	}
}
