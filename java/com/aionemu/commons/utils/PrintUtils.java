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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;

/**
 * Provides utility methods for printing and formatting data.<br>
 * This class simplifies common output operations for the {@code com.aionemu} project.
 */
public class PrintUtils
{
	/**
	 * Prints a formatted header to the console.<br>
	 * The output includes the {@code sectionName} inside brackets.<br>
	 * It pads the left side with equals signs.
	 * @param sectionName The name of the section to display.
	 */
	public static void printSection(String sectionName)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("-[ " + sectionName + " ]");
		while (sb.length() < 119)
		{
			sb.insert(0, "=");
		}
		
		System.out.println(sb.toString());
	}
	
	/**
	 * Converts a hexadecimal {@code String} into a {@code byte[]} array.<br>
	 * This method removes all whitespace from the input string before processing.<br>
	 * It parses every two characters as a single hex value.
	 * @param string The hex string to convert.
	 * @return A new {@code byte[]} containing the converted data.
	 */
	public static byte[] hex2bytes(String string)
	{
		final String finalString = string.replaceAll("\\s+", "");
		final byte[] bytes = new byte[finalString.length() / 2];
		for (int i = 0; i < bytes.length; ++i)
		{
			bytes[i] = (byte) Integer.parseInt(finalString.substring(2 * i, (2 * i) + 2), 16);
		}
		
		return bytes;
	}
	
	/**
	 * Converts an array of bytes into a hexadecimal string.<br>
	 * Each byte is represented by two uppercase characters.<br>
	 * This method is the inverse of {@code hex2bytes}.
	 * @param bytes The {@code byte[]} array to convert.
	 * @return A {@code String} containing the hex representation.
	 */
	public static String bytes2hex(byte[] bytes)
	{
		final StringBuilder result = new StringBuilder();
		for (byte b : bytes)
		{
			final int value = b & 0xFF;
			result.append(String.format("%02X", value));
		}
		
		return result.toString();
	}
	
	/**
	 * This method reverses the order of hex byte pairs in a string.<br>
	 * It treats every two characters as a single unit.<br>
	 * For example, {@code "AABB"} becomes {@code "BBAA"}.
	 * @param input The hexadecimal string to reverse.
	 * @return A new string with the reversed byte order.
	 */
	public static String reverseHex(String input)
	{
		final String[] chunked = new String[input.length() / 2];
		int position = 0;
		for (int i = 0; i < input.length(); i += 2)
		{
			chunked[position] = input.substring(position * 2, (position * 2) + 2);
			++position;
		}
		
		Collections.reverse(Arrays.asList(chunked));
		return String.join("", chunked);
	}
	
	/**
	 * Converts a {@code ByteBuffer} into a formatted hexadecimal string.<br>
	 * The output includes memory addresses and line breaks for readability.<br>
	 * It restores the original position of the {@code data} buffer.
	 * @param data The {@code ByteBuffer} to convert.
	 * @return A formatted hex string representation of the data.
	 */
	public static String toHex(ByteBuffer data)
	{
		final int position = data.position();
		final StringBuilder result = new StringBuilder();
		int counter = 0;
		while (data.hasRemaining())
		{
			if ((counter % 16) == 0)
			{
				result.append(String.format("%04X: ", counter));
			}
			
			final int b = data.get() & 0xFF;
			result.append(String.format("%02X ", b));
			if ((++counter % 16) == 0)
			{
				result.append("  ");
				toText(data, result, 16);
				result.append("\n");
			}
		}
		
		final int rest = counter % 16;
		if (rest > 0)
		{
			for (int i = 0; i < (17 - rest); ++i)
			{
				result.append("   ");
			}
			
			toText(data, result, rest);
		}
		
		data.position(position);
		return result.toString();
	}
	
	/**
	 * Converts a portion of a {@code ByteBuffer} into a readable string.<br>
	 * It replaces non-printable characters with a period.<br>
	 * This method is used internally by {@code toHex} to format data.
	 * @param data The source {@code ByteBuffer} containing the bytes.
	 * @param result The {@code StringBuilder} where the resulting text will be stored.
	 * @param cnt The number of characters to process from the current position.
	 */
	private static void toText(ByteBuffer data, StringBuilder result, int cnt)
	{
		int charPos = data.position() - cnt;
		for (int a = 0; a < cnt; ++a)
		{
			final int c = data.get(charPos++);
			if ((c > 31) && (c < 128))
			{
				result.append((char) c);
			}
			else
			{
				result.append('.');
			}
		}
	}
}
