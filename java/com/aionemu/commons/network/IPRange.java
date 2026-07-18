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
package com.aionemu.commons.network;

import java.util.Arrays;

/**
 * A utility class used to determine if a specific IP address belongs to a given range.<br>
 * It is designed to be compatible with both IPv4 and IPv6 to simplify future migrations.
 * @author Taran
 * @author SoulKeeper
 */
public class IPRange
{
	/**
	 * Minimal ip address of the range
	 */
	private final long min;
	
	/**
	 * Maximum ip address of the range
	 */
	private final long max;
	
	/**
	 * Address that is host for this range
	 */
	private final byte[] address;
	
	/**
	 * Creates a new {@link IPRange} instance using string representations.<br>
	 * This constructor converts the input strings into byte arrays and long values.
	 * @param min The minimum IP address of the range as a {@code String}.
	 * @param max The maximum IP address of the range as a {@code String}.
	 * @param address The host IP address for this range as a {@code String}.
	 */
	public IPRange(String min, String max, String address)
	{
		this.min = toLong(toByteArray(min));
		this.max = toLong(toByteArray(max));
		this.address = toByteArray(address);
	}
	
	/**
	 * Creates a new {@link IPRange} instance using byte arrays.<br>
	 * This constructor converts the range boundaries into long values.<br>
	 * It stores the host address as a byte array.
	 * @param min The minimum IP address of the range as a {@code byte[]} array.
	 * @param max The maximum IP address of the range as a {@code byte[]} array.
	 * @param address The host IP address for this range as a {@code byte[]} array.
	 */
	public IPRange(byte[] min, byte[] max, byte[] address)
	{
		this.min = toLong(min);
		this.max = toLong(max);
		this.address = address;
	}
	
	/**
	 * Checks if the provided address falls within this range.<br>
	 * It converts the {@code String} to a numeric value for comparison.
	 * @param address The IP address string to check.
	 * @return {@code true} if the address is within the range, otherwise {@code false}.
	 */
	public boolean isInRange(String address)
	{
		final long addr = toLong(toByteArray(address));
		return (addr >= min) && (addr <= max);
	}
	
	/**
	 * Retrieves the host address for this range.<br>
	 * This method returns the internal {@code byte[]} representation of the address.
	 * @return the {@code byte[]} array containing the host address.
	 */
	public byte[] getAddress()
	{
		return address;
	}
	
	/**
	 * Converts the minimum IP address of this range into a byte array.<br>
	 * This method uses the internal {@code min} value.
	 * @return a {@code byte[]} representing the minimum IP address.
	 */
	public byte[] getMinAsByteArray()
	{
		return toBytes(min);
	}
	
	/**
	 * Retrieves the maximum IP address of this range.<br>
	 * It converts the internal {@code max} value into a byte array.
	 * @return The maximum IP address as a {@code byte[]} array.
	 */
	public byte[] getMaxAsByteArray()
	{
		return toBytes(max);
	}
	
	/**
	 * Converts a {@code byte[]} array into a {@code long} value.<br>
	 * This method treats the bytes as an IPv4 address.<br>
	 * It uses bitwise operations to reconstruct the number.
	 * @param bytes The array of bytes to convert.
	 * @return The resulting {@code long} value.
	 */
	private static long toLong(byte[] bytes)
	{
		long result = 0;
		result += (bytes[3] & 0xFF);
		result += ((bytes[2] & 0xFF) << 8);
		result += ((bytes[1] & 0xFF) << 16);
		result += (bytes[0] << 24);
		return result & 0xFFFFFFFFL;
	}
	
	/**
	 * Converts a {@code long} value into a {@code byte[]} array.<br>
	 * This method extracts the lower 32 bits of the input.<br>
	 * The resulting array has a length of 4.
	 * @param val The {@code long} value to convert.
	 * @return A {@code byte[]} containing the converted value.
	 */
	private static byte[] toBytes(long val)
	{
		final byte[] result = new byte[4];
		result[3] = (byte) (val & 0xFF);
		result[2] = (byte) ((val >> 8) & 0xFF);
		result[1] = (byte) ((val >> 16) & 0xFF);
		result[0] = (byte) ((val >> 24) & 0xFF);
		return result;
	}
	
	/**
	 * Converts an IPv4 address string into a {@code byte[]} array.<br>
	 * This method splits the input by dots and parses each part as a byte.
	 * @param address The IP address string to convert.
	 * @return A {@code byte[]} containing the 4 octets of the IP address.
	 */
	public static byte[] toByteArray(String address)
	{
		final byte[] result = new byte[4];
		final String[] strings = address.split("\\.");
		for (int i = 0, n = strings.length; i < n; i++)
		{
			result[i] = (byte) Integer.parseInt(strings[i]);
		}
		
		return result;
	}
	
	/**
	 * Compares this {@link IPRange} object with another object for equality.<br>
	 * It checks if both objects represent the same range and host address.
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
		
		if (!(o instanceof IPRange))
		{
			return false;
		}
		
		final IPRange ipRange = (IPRange) o;
		return (max == ipRange.max) && (min == ipRange.min) && Arrays.equals(address, ipRange.address);
	}
	
	/**
	 * Returns a hash code value for this {@link IPRange} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code min}, {@code max}, and {@code address} fields.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		int result = (int) (min ^ (min >>> 32));
		result = (31 * result) + (int) (max ^ (max >>> 32));
		result = (31 * result) + Arrays.hashCode(address);
		return result;
	}
}
