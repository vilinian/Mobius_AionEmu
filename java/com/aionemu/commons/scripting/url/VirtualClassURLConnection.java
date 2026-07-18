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
package com.aionemu.commons.scripting.url;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.aionemu.commons.scripting.ScriptClassLoader;

/**
 * This class represents a {@link URLConnection} used to access script binary data.<br>
 * It connects to scripts loaded by the {@link com.aionemu.commons.scripting.impl.javacompiler.ScriptCompilerImpl} class.
 * @author SoulKeeper
 */
public class VirtualClassURLConnection extends URLConnection
{
	/**
	 * Input stream, is assigned from class
	 */
	private final InputStream is;
	
	/**
	 * Creates a new connection to script binary data.<br>
	 * This constructor initializes the internal {@code InputStream} using the provided {@link ScriptClassLoader}.
	 * @param url The {@code URL} representing the script location.
	 * @param cl The {@link ScriptClassLoader} used to fetch the byte code.
	 */
	protected VirtualClassURLConnection(URL url, ScriptClassLoader cl)
	{
		super(url);
		is = new ByteArrayInputStream(cl.getByteCode(url.getHost()));
	}
	
	/**
	 * Establishes a connection to the script binary data.<br>
	 * This method prepares the internal state for reading the input stream.
	 */
	@Override
	public void connect()
	{
	}
	
	/**
	 * Retrieves the input stream for the connected resource.<br>
	 * This method returns the {@code InputStream} associated with this connection.
	 * @return The {@code InputStream} of the binary data.
	 */
	@Override
	public InputStream getInputStream()
	{
		return is;
	}
}
