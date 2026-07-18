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

import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

/**
 * This class provides a way to compile Java source code that exists only in memory.<br>
 * It extends {@link SimpleJavaFileObject} to handle source content as a {@code String}.
 * @author SoulKeeper
 */
public class JavaSourceFromString extends SimpleJavaFileObject
{
	/**
	 * Source code of the class
	 */
	private final String code;
	
	/**
	 * Creates a new {@link JavaSourceFromString} object from a string of source code.<br>
	 * This method is used to compile Java classes that are stored in memory rather than on disk.
	 * @param className The name of the class to be created.
	 * @param code The raw source code as a {@code String}.
	 */
	public JavaSourceFromString(String className, String code)
	{
		super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
		this.code = code;
	}
	
	/**
	 * Retrieves the source code content as a {@code CharSequence}.<br>
	 * This method returns the internal {@code code} string.
	 * @param ignoreEncodingErrors Set to {@code true} to skip errors, or {@code false} to throw them.
	 * @return The source code of the class.
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
	{
		return code;
	}
}
