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
package com.aionemu.commons.configuration.transformers;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.aionemu.commons.configuration.PropertyTransformer;
import com.aionemu.commons.configuration.TransformationException;

/**
 * Transforms a {@code String} into an {@link InetSocketAddress}.<br>
 * It supports formats like {@code address:port} or {@code *:port} to bind to all available interfaces.
 * @author SoulKeeper
 */
public class InetSocketAddressTransformer implements PropertyTransformer<InetSocketAddress>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final InetSocketAddressTransformer SHARED_INSTANCE = new InetSocketAddressTransformer();
	
	/**
	 * Converts a string into an {@link InetSocketAddress} object.<br>
	 * The input string must follow the "address:port" format.<br>
	 * If the address is "*", it will bind to all available network interfaces.
	 * @param value The raw string value to be converted.
	 * @param field The {@code Field} where the result will be assigned.
	 * @return The resulting {@link InetSocketAddress}.
	 * @throws TransformationException If the string format is invalid or an error occurs during parsing.
	 */
	@Override
	public InetSocketAddress transform(String value, Field field) throws TransformationException
	{
		final String[] parts = value.split(":");
		
		if (parts.length != 2)
		{
			throw new TransformationException("Can't transform property, must be in format \"address:port\"");
		}
		
		try
		{
			if ("*".equals(parts[0]))
			{
				return new InetSocketAddress(Integer.parseInt(parts[1]));
			}
			
			final InetAddress address = InetAddress.getByName(parts[0]);
			final int port = Integer.parseInt(parts[1]);
			return new InetSocketAddress(address, port);
		}
		catch (Exception e)
		{
			throw new TransformationException(e);
		}
	}
}
