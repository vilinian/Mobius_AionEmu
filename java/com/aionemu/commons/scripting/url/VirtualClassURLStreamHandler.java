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

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.aionemu.commons.scripting.ScriptClassLoader;

/**
 * This class provides a custom {@code URLStreamHandler} for the {@code HANDLER_PROTOCOL} protocol.<br>
 * It allows the system to load resources from virtual paths using the {@link com.aionemu.commons.scripting.ScriptClassLoader}.
 * @author SoulKeeper
 */
public class VirtualClassURLStreamHandler extends URLStreamHandler
{
	/**
	 * Script Handler protocol for classes compiled from source
	 */
	public static final String HANDLER_PROTOCOL = "aescript://";
	
	/**
	 * Script class loader that loaded those classes
	 */
	private final ScriptClassLoader cl;
	
	/**
	 * Creates a new instance of this handler.<br>
	 * It initializes the internal {@code ScriptClassLoader}.
	 * @param cl The {@link ScriptClassLoader} used to load the classes.
	 */
	public VirtualClassURLStreamHandler(ScriptClassLoader cl)
	{
		this.cl = cl;
	}
	
	/**
	 * Opens a connection to the specified {@code URL}.<br>
	 * This method creates a new {@link VirtualClassURLConnection} instance.
	 * @param u The {@code URL} to connect to.
	 * @return A new {@code URLConnection} object for the given {@code URL}.
	 */
	@Override
	protected URLConnection openConnection(URL u)
	{
		return new VirtualClassURLConnection(u, cl);
	}
}
