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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * This class acts as a container for classes compiled in memory.<br>
 * It implements {@code JavaFileObject} to allow the compiler to write bytes directly to memory instead of disk.<br>
 * It also serves as a holder for the resulting loaded {@link Class}.
 * @author SoulKeeper
 */
public class BinaryClass extends SimpleJavaFileObject
{
	/**
	 * ClassName
	 */
	private final String name;
	
	/**
	 * Class data will be written here
	 */
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	/**
	 * Loaded class will be set here
	 */
	private Class<?> definedClass;
	
	/**
	 * Creates a new instance of {@link BinaryClass}.<br>
	 * This constructor initializes the class with a specific name.<br>
	 * It sets up the internal URI for in-memory storage.
	 * @param name The fully qualified name of the class.
	 */
	protected BinaryClass(String name)
	{
		super(URI.create("bytes:///" + name.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
		this.name = name;
	}
	
	/**
	 * Gets the fully qualified class name.<br>
	 * This method returns the {@code name} field appended with the {@code .class} extension.
	 * @return The name of the compiled class as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return name + ".class";
	}
	
	/**
	 * Opens a stream to read the compiled class bytes.<br>
	 * This method returns a new {@code ByteArrayInputStream}.
	 * @return an {@code InputStream} containing the byte data of the class.
	 */
	@Override
	public InputStream openInputStream()
	{
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	/**
	 * Opens the internal stream used to write class bytes.<br>
	 * This method returns the {@code ByteArrayOutputStream} where compiled data is stored.
	 * @return The {@link OutputStream} for writing binary data.
	 */
	@Override
	public OutputStream openOutputStream()
	{
		return baos;
	}
	
	/**
	 * Checks if the provided name and kind are compatible.<br>
	 * This method returns {@code true} only if the {@code kind} is {@code CLASS}.
	 * @param simpleName The simple name of the class.
	 * @param kind The type of the object being checked.
	 * @return {@code true} if the kind matches {@code CLASS}, otherwise {@code false}.
	 */
	@Override
	public boolean isNameCompatible(String simpleName, Kind kind)
	{
		return Kind.CLASS.equals(kind);
	}
	
	/**
	 * Determines the binary name based on a path of files.<br>
	 * This method looks at the provided {@code Iterable} to find the correct name.<br>
	 * It returns the internal {@code name} field.
	 * @param path The collection of {@link File} objects to check.
	 * @return The inferred binary name as a {@code String}.
	 */
	protected String inferBinaryName(Iterable<? extends File> path)
	{
		return name;
	}
	
	/**
	 * Retrieves the compiled class bytes from memory.<br>
	 * This method returns the data stored in the internal {@code ByteArrayOutputStream}.
	 * @return a {@code byte[]} array containing the class data.
	 */
	public byte[] getBytes()
	{
		return baos.toByteArray();
	}
	
	/**
	 * Retrieves the {@link Class} object that was loaded into this container.<br>
	 * This method returns the class associated with the compiled bytes.
	 * @return The loaded {@code Class} object or {@code null}.
	 */
	public Class<?> getDefinedClass()
	{
		return definedClass;
	}
	
	/**
	 * Sets the {@code Class} object for this compiled class.<br>
	 * This method stores the loaded class in the {@code definedClass} field.<br>
	 * Use this after the compilation process is complete.
	 * @param definedClass The {@code Class} to be stored.
	 */
	public void setDefinedClass(Class<?> definedClass)
	{
		this.definedClass = definedClass;
	}
	
	/**
	 * Compares this object with another object for equality.<br>
	 * It checks if the other object is a {@link BinaryClass}.<br>
	 * If it is, it compares their names.
	 * @param arg0 The object to compare against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object arg0)
	{
		if (arg0 instanceof BinaryClass)
		{
			return ((BinaryClass) arg0).name.equals(name);
		}
		
		return false;
	}
	
	/**
	 * Returns a hash code value for this {@link BinaryClass} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code name} field.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
