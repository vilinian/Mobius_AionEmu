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
package com.aionemu.gameserver.utils;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.configs.main.NameConfig;

/**
 * This class provides a collection of static helper methods for general purpose tasks.<br>
 * It contains common utility functions used throughout the {@code gameserver} project.
 * @author -Nemesiss-
 * @author GiGatR00n
 */
public class Util
{
	/**
	 * Prints a formatted section header to the console.<br>
	 * It wraps the input string in brackets if it is not empty.<br>
	 * The method pads the output with equals signs to reach a specific length.
	 * @param s The text to be displayed as a section header.
	 */
	public static void printSection(String s)
	{
		if (!s.isEmpty())
		{
			s = "[ " + s + " ]";
		}
		
		while (s.length() < 119)
		{
			s = "=" + s + "=";
		}
		
		System.out.println("");
		System.out.println(s);
		System.out.println("");
	}
	
	/**
	 * Prints a formatted section of text to the console.<br>
	 * This method wraps the input string in parentheses and adds dashes as padding.<br>
	 * It ensures the output is centered or padded to a specific length.
	 * @param s The text content to be printed.
	 */
	public static void printSsSection(String s)
	{
		s = "( " + s + " )";
		
		while (s.length() < 119)
		{
			s = "-" + s + "-";
		}
		
		System.out.println("");
		System.out.println(s);
		System.out.println("");
	}
	
	/**
	 * Prints a visual progress bar header to the console.<br>
	 * The length of the bar is determined by the {@code size} parameter.<br>
	 * This method helps visualize tasks during execution.
	 * @param size The number of dashes to display in the middle of the bar.
	 */
	public static void printProgressBarHeader(int size)
	{
		final StringBuilder header = new StringBuilder("0%[");
		for (int i = 0; i < size; i++)
		{
			header.append("-");
		}
		header.append("]100%");
		System.out.println(header);
		System.out.print("   ");
	}
	
	/**
	 * Prints a progress indicator to the console.<br>
	 * This method displays a {@code +} symbol to show current activity.<br>
	 * It is often used in conjunction with {@code printProgressBarHeader} and {@code printEndProgress}.
	 */
	public static void printCurrentProgress()
	{
		System.out.print("+");
	}
	
	/**
	 * Prints a completion message to the console.<br>
	 * This method signals that a task is finished.
	 */
	public static void printEndProgress()
	{
		System.out.print(" Done. \n");
	}
	
	/**
	 * Prints a rotating progress header to the console.<br>
	 * This method displays the current amount of data being processed.<br>
	 * It uses an animation effect based on the {@code dataSize}.
	 * @param dataSize The total number of data items to display.
	 */
	public static void printRotatingBarHeader(int dataSize)
	{
		final String anim = "|/-\\";
		System.out.print("\r" + anim.charAt(Math.round(dataSize / 50) % anim.length()) + " Processing data : " + dataSize + " data" + (dataSize <= 1 ? "." : "s.                 "));
		System.out.print("\r");
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
		final StringBuilder result = new StringBuilder();
		int counter = 0;
		int b;
		while (data.hasRemaining())
		{
			if ((counter % 16) == 0)
			{
				result.append(String.format("%04X: ", counter));
			}
			
			b = data.get() & 0xff;
			result.append(String.format("%02X ", b));
			
			counter++;
			if ((counter % 16) == 0)
			{
				result.append("  ");
				toText(data, result, 16);
				result.append("\n");
			}
		}
		
		final int rest = counter % 16;
		if (rest > 0)
		{
			for (int i = 0; i < (17 - rest); i++)
			{
				result.append("   ");
			}
			
			toText(data, result, rest);
		}
		
		return result.toString();
	}
	
	/**
	 * Converts a {@code ByteBuffer} into a formatted hexadecimal string.<br>
	 * Each byte is represented as two uppercase hex characters followed by a space.<br>
	 * A new line is added after every 16 bytes to improve readability.
	 * @param data The {@code ByteBuffer} containing the raw bytes to convert.
	 * @return A formatted hexadecimal string representation of the input data.
	 */
	public static String toHexStream(ByteBuffer data)
	{
		final StringBuilder result = new StringBuilder();
		int counter = 0;
		int b;
		while (data.hasRemaining())
		{
			b = data.get() & 0xff;
			result.append(String.format("%02X ", b));
			
			counter++;
			if ((counter % 16) == 0)
			{
				result.append("\n");
			}
		}
		
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
		for (int a = 0; a < cnt; a++)
		{
			final int c = data.get(charPos++);
			if ((c > 0x1f) && (c < 0x80))
			{
				result.append((char) c);
			}
			else
			{
				result.append('.');
			}
		}
	}
	
	/**
	 * Formats a player name based on the server configuration.<br>
	 * If custom names are allowed, it returns the original {@code name}.<br>
	 * Otherwise, it capitalizes only the first letter of the string.<br>
	 * Returns an empty string if the input is null or empty.
	 * @param name The raw name string to be processed.
	 * @return The formatted name string.
	 */
	public static String convertName(String name)
	{
		if (!name.isEmpty())
		{
			if (NameConfig.ALLOW_CUSTOM_NAMES)
			{
				return name;
			}
			
			return name.substring(0, 1).toUpperCase() + name.toLowerCase().substring(1);
		}
		
		return "";
	}
}
