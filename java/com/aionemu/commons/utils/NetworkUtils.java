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
package com.aionemu.commons.utils;

/**
 * Provides a collection of helper methods for common networking tasks.<br>
 * This utility class simplifies operations related to {@code IP} addresses and network configurations.
 * @author KID, -Nemesiss-
 */
public class NetworkUtils
{
	/**
	 * Checks if a given IP address matches a specific pattern.<br>
	 * The method supports wildcards, exact matches, and range definitions.
	 * @param pattern The matching rule such as {@code *.*.*.*}, {@code *}, or a range like {@code 192.168.1.0-255}.
	 * @param address The IP address string to validate.
	 * @return {@code true} if the {@code address} matches the {@code pattern}, otherwise {@code false}.
	 */
	public static boolean checkIPMatching(String pattern, String address)
	{
		if (pattern.equals("*.*.*.*") || pattern.equals("*"))
		{
			return true;
		}
		
		final String[] mask = pattern.split("\\.");
		final String[] ip_address = address.split("\\.");
		for (int i = 0; i < mask.length; i++)
		{
			if (mask[i].equals("*") || mask[i].equals(ip_address[i]))
			{
				continue;
			}
			else if (mask[i].contains("-"))
			{
				final byte min = Byte.parseByte(mask[i].split("-")[0]);
				final byte max = Byte.parseByte(mask[i].split("-")[1]);
				final byte ip = Byte.parseByte(ip_address[i]);
				if ((ip < min) || (ip > max))
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		
		return true;
	}
}
