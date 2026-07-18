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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.tools.SimpleJavaFileObject;

/**
 * This class serves as a simple wrapper for {@link SimpleJavaFileObject}.<br>
 * It is used to load Java source code directly from the file system.
 * @author SoulKeeper
 */
public class JavaSourceFromFile extends SimpleJavaFileObject
{
	/**
	 * Creates a new {@link JavaSourceFromFile} instance from a file on the disk.<br>
	 * This method initializes the object with the specified file path and type.
	 * @param file The {@code File} containing the source code.
	 * @param kind The {@code Kind} of the Java source file.
	 */
	public JavaSourceFromFile(File file, Kind kind)
	{
		super(file.toURI(), kind);
	}
	
	/**
	 * Retrieves the character content of the source file.<br>
	 * This method reads the file using {@code StandardCharsets.UTF_8}.
	 * @param ignoreEncodingErrors A boolean flag to determine if encoding errors should be ignored.
	 * @return The content of the file as a {@code CharSequence}.
	 * @throws IOException If an error occurs while reading the file.
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
	{
		return Files.readString(new File(toUri()).toPath(), StandardCharsets.UTF_8);
	}
}
