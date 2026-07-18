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

/**
 * Provides utility methods for performing arithmetic operations safely.<br>
 * This class helps prevent {@code ArithmeticException} errors during calculations.<br>
 * It ensures that mathematical results remain within expected bounds.
 * @author MrPoke
 */
public class SafeMath
{
	/**
	 * Adds two integers together safely.<br>
	 * This method checks if the result exceeds the limits of an {@code int}.<br>
	 * It throws an {@link OverfowException} if the sum is too large or too small.
	 * @param source The first integer to add.
	 * @param value The second integer to add.
	 * @return The sum of {@code source} and {@code value}.
	 * @throws OverfowException
	 */
	public static int addSafe(int source, int value) throws OverfowException
	{
		final long s = (long) source + (long) value;
		if ((s < Integer.MIN_VALUE) || (s > Integer.MAX_VALUE))
		{
			throw new OverfowException(source + " + " + value + " = " + ((long) source + (long) value));
		}
		
		return (int) s;
	}
	
	/**
	 * Adds two {@code long} values together safely.<br>
	 * This method checks if the result will exceed the limits of a {@code long}.<br>
	 * It throws an {@link OverfowException} if the calculation overflows.
	 * @param source The starting {@code long} value.
	 * @param value The {@code long} value to add to the source.
	 * @return The sum of {@code source} and {@code value}.
	 * @throws OverfowException If the addition results in an overflow.
	 */
	public static long addSafe(long source, long value) throws OverfowException
	{
		if (((source > 0) && (value > (Long.MAX_VALUE - source))) || ((source < 0) && (value < (Long.MIN_VALUE - source))))
		{
			throw new OverfowException(source + " + " + value + " = " + (source + value));
		}
		
		return source + value;
	}
	
	/**
	 * Multiplies two integers safely.<br>
	 * This method checks if the result exceeds the limits of an {@code int}.<br>
	 * It throws an {@link OverfowException} if the calculation overflows.
	 * @param source The first integer to multiply.
	 * @param value The second integer to multiply.
	 * @return The product of {@code source} and {@code value} as an {@code int}.
	 * @throws OverfowException If the result is too large or too small for an {@code int}.
	 */
	public static int multSafe(int source, int value) throws OverfowException
	{
		final long m = ((long) source) * ((long) value);
		if ((m < Integer.MIN_VALUE) || (m > Integer.MAX_VALUE))
		{
			throw new OverfowException(source + " * " + value + " = " + ((long) source * (long) value));
		}
		
		return (int) m;
	}
	
	/**
	 * Multiplies two {@code long} values safely.<br>
	 * This method checks if the result exceeds the limits of a {@code long}.<br>
	 * It throws an {@link OverfowException} if an overflow occurs.
	 * @param a The first number to multiply.
	 * @param b The second number to multiply.
	 * @return The product of {@code a} and {@code b}.
	 * @throws OverfowException
	 */
	public static long multSafe(long a, long b) throws OverfowException
	{
		long ret;
		final String msg = "overflow: multiply";
		if (a > b)
		{
			// use symmetry to reduce boundry cases
			ret = multSafe(b, a);
		}
		else
		{
			if (a < 0)
			{
				if (b < 0)
				{
					// check for positive overflow with negative a, negative b
					if (a >= (Long.MAX_VALUE / b))
					{
						ret = a * b;
					}
					else
					{
						throw new OverfowException(msg);
					}
				}
				else if (b > 0)
				{
					// check for negative overflow with negative a, positive b
					if ((Long.MIN_VALUE / b) <= a)
					{
						ret = a * b;
					}
					else
					{
						throw new OverfowException(msg);
						
					}
				}
				else
				{
					ret = 0;
				}
			}
			else if (a > 0)
			{
				// check for positive overflow with positive a, positive b
				if (a <= (Long.MAX_VALUE / b))
				{
					ret = a * b;
				}
				else
				{
					throw new OverfowException(msg);
				}
			}
			else
			{
				ret = 0;
			}
		}
		
		return ret;
	}
}
