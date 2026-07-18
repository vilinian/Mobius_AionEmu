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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

/**
 * This class parses and formats time deltas into human-readable strings.<br>
 * It provides utilities to convert complex durations into simplified approximations like years, days, or hours.<br>
 * You can create instances using {@code HumanTime} or by parsing a {@code CharSequence} via {@code eval}.
 * @author <a href="mailto:jb@eaio.com">Johann Burkard</a>
 * @see #eval(CharSequence)
 * @see #approximately(CharSequence)
 * @see <a href="http://johannburkard.de/blog/programming/java/date-formatting-parsing-humans-humantime.html">Date Formatting and Parsing for Humans in Java with HumanTime</a>
 */
public class HumanTime implements Externalizable, Comparable<HumanTime>, Cloneable
{
	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 5179328390732826722L;
	/**
	 * One second.
	 */
	private static final long SECOND = 1000;
	/**
	 * One minute.
	 */
	private static final long MINUTE = SECOND * 60;
	/**
	 * One hour.
	 */
	private static final long HOUR = MINUTE * 60;
	/**
	 * One day.
	 */
	private static final long DAY = HOUR * 24;
	/**
	 * One year.
	 */
	private static final long YEAR = DAY * 365;
	/**
	 * Percentage of what is round up or down.
	 */
	private static final int CEILING_PERCENTAGE = 15;
	
	/**
	 * Parsing state.
	 */
	static enum State
	{
		NUMBER,
		IGNORED,
		UNIT
	}
	
	/**
	 * Determines the {@code State} of a given character.<br>
	 * It identifies if the character is a digit, a time unit, or should be ignored.
	 * @param c The character to evaluate.
	 * @return The corresponding {@code State} for the input character.
	 */
	static State getState(char c)
	{
		State out;
		switch (c)
		{
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				out = State.NUMBER;
				break;
			case 's':
			case 'm':
			case 'h':
			case 'd':
			case 'y':
			case 'S':
			case 'M':
			case 'H':
			case 'D':
			case 'Y':
				out = State.UNIT;
				break;
			default:
				out = State.IGNORED;
		}
		
		return out;
	}
	
	/**
	 * Parses a string representation of time into a {@link HumanTime} object.<br>
	 * This method handles units like years, days, hours, minutes, and seconds.<br>
	 * It ignores whitespace and is case insensitive during parsing.
	 * @param s The {@code CharSequence} containing the time string to parse.
	 * @return A new {@link HumanTime} instance representing the parsed duration.
	 */
	public static HumanTime eval(CharSequence s)
	{
		final HumanTime out = new HumanTime(0L);
		
		int num = 0;
		
		int start = 0;
		int end = 0;
		
		State oldState = State.IGNORED;
		
		for (char c : new Iterable<Character>()
		{
			/**
			 * @see java.lang.Iterable#iterator()
			 */
			@Override
			public Iterator<Character> iterator()
			{
				return new Iterator<>()
				{
					
					private int p = 0;
					
					/**
					 * @see java.util.Iterator#hasNext()
					 */
					@Override
					public boolean hasNext()
					{
						return p < s.length();
					}
					
					/**
					 * @see java.util.Iterator#next()
					 */
					@Override
					public Character next()
					{
						return s.charAt(p++);
					}
					
					/**
					 * @see java.util.Iterator#remove()
					 */
					@Override
					public void remove()
					{
						throw new UnsupportedOperationException();
					}
				};
			}
			
		})
		{
			final State newState = getState(c);
			if (oldState != newState)
			{
				if ((oldState == State.NUMBER) && ((newState == State.IGNORED) || (newState == State.UNIT)))
				{
					num = Integer.parseInt(s.subSequence(start, end).toString());
				}
				else if ((oldState == State.UNIT) && ((newState == State.IGNORED) || (newState == State.NUMBER)))
				{
					out.nTimes(s.subSequence(start, end).toString(), num);
					num = 0;
				}
				
				start = end;
			}
			
			++end;
			oldState = newState;
		}
		
		if (oldState == State.UNIT)
		{
			out.nTimes(s.subSequence(start, end).toString(), num);
		}
		
		return out;
	}
	
	/**
	 * Converts a time string into an exact human-readable format.<br>
	 * This method parses the input using {@code eval}.<br>
	 * It returns the formatted result as a {@code String}.
	 * @param in The time representation to parse.
	 * @return The formatted exact time string.
	 */
	public static String exactly(CharSequence in)
	{
		return eval(in).getExactly();
	}
	
	/**
	 * Converts a raw time delta into a human-readable string.<br>
	 * This method creates a new {@link HumanTime} instance using the provided value.<br>
	 * It then returns the exact formatted representation of that time.
	 * @param l The time delta in milliseconds to format.
	 * @return A string representing the exact time units.
	 */
	public static String exactly(long l)
	{
		return new HumanTime(l).getExactly();
	}
	
	/**
	 * Converts a time string into a simplified human-readable format.<br>
	 * This method rounds the time values to provide an approximation.<br>
	 * It uses {@code eval} to parse the input first.
	 * @param in The {@code CharSequence} representing the time to convert.
	 * @return A {@code String} containing the approximate time.
	 */
	public static String approximately(CharSequence in)
	{
		return eval(in).getApproximately();
	}
	
	/**
	 * Converts a time delta into a human-readable approximate string.<br>
	 * This method rounds the value to the nearest significant unit.<br>
	 * It internally creates a new {@link HumanTime} instance.
	 * @param l The time delta in milliseconds.
	 * @return A formatted string representing the approximate time.
	 */
	public static String approximately(long l)
	{
		return new HumanTime(l).getApproximately();
	}
	
	/**
	 * The time delta.
	 */
	private long delta;
	
	/**
	 * Creates a new {@link HumanTime} instance with a delta of {@code 0}.<br>
	 * This is useful for initializing an empty time object.
	 */
	public HumanTime()
	{
		this(0L);
	}
	
	/**
	 * Creates a new {@link HumanTime} instance from a time delta.<br>
	 * This constructor converts the input to an absolute value.
	 * @param delta The amount of time to represent as a long.
	 */
	public HumanTime(long delta)
	{
		super();
		this.delta = Math.abs(delta);
	}
	
	/**
	 * Updates the time delta based on a specific unit and amount.<br>
	 * This method identifies the {@code unit} string to call the correct setter.
	 * @param unit The time unit to use such as "ms", "s", "m", "h", "d", or "y".
	 * @param n The numeric value to add to the specified unit.
	 */
	private void nTimes(String unit, int n)
	{
		if ("ms".equalsIgnoreCase(unit))
		{
			ms(n);
		}
		else if ("s".equalsIgnoreCase(unit))
		{
			s(n);
		}
		else if ("m".equalsIgnoreCase(unit))
		{
			m(n);
		}
		else if ("h".equalsIgnoreCase(unit))
		{
			h(n);
		}
		else if ("d".equalsIgnoreCase(unit))
		{
			d(n);
		}
		else if ("y".equalsIgnoreCase(unit))
		{
			y(n);
		}
	}
	
	/**
	 * Calculates the upper ceiling for a given value.<br>
	 * This method rounds the input down to the nearest hundred and then applies a percentage reduction.
	 * @param x The original time delta value to process.
	 * @return The calculated ceiling value as a {@code long}.
	 */
	private long upperCeiling(long x)
	{
		return (x / 100) * (100 - CEILING_PERCENTAGE);
	}
	
	/**
	 * Calculates the lower ceiling for a given value.<br>
	 * This method rounds down to the nearest percentage based on {@code CEILING_PERCENTAGE}.
	 * @param x The original long value to process.
	 * @return The calculated lower ceiling as a {@code long}.
	 */
	private long lowerCeiling(long x)
	{
		return (x / 100) * CEILING_PERCENTAGE;
	}
	
	/**
	 * Calculates the ceiling of a division.<br>
	 * This method divides {@code d} by {@code n} and rounds up to the nearest integer.<br>
	 * The result is returned as a {@code String}.
	 * @param d The dividend value.
	 * @param n The divisor value.
	 * @return The ceiling of the division as a {@code String}.
	 */
	private String ceil(long d, long n)
	{
		return Integer.toString((int) Math.ceil((double) d / n));
	}
	
	/**
	 * Calculates the floor of a division between two numbers.<br>
	 * This method returns the result as a {@code String}.<br>
	 * It performs the calculation by casting to {@code double}.
	 * @param d The dividend value.
	 * @param n The divisor value.
	 * @return The floor of the division as a {@code String}.
	 */
	private String floor(long d, long n)
	{
		return Integer.toString((int) Math.floor((double) d / n));
	}
	
	/**
	 * Gets the number of years in the current time delta.<br>
	 * This method is a convenience shortcut for {@code y} with a value of {@code 1}.
	 * @return a {@link HumanTime} object representing one year.
	 */
	public HumanTime y()
	{
		return y(1);
	}
	
	/**
	 * Sets the number of years for the time delta.<br>
	 * This method adds the absolute value of {@code n} multiplied by a year constant to the current delta.<br>
	 * It returns the current instance to allow for method chaining.
	 * @param n The number of years to add.
	 * @return The current {@link HumanTime} instance.
	 */
	public HumanTime y(int n)
	{
		delta += YEAR * Math.abs(n);
		return this;
	}
	
	/**
	 * Adds one day to the current time delta.<br>
	 * This method is a convenience wrapper for {@code d} with a value of 1.
	 * @return The updated {@link HumanTime} instance.
	 */
	public HumanTime d()
	{
		return d(1);
	}
	
	/**
	 * Adds a specific number of days to the current time delta.<br>
	 * The value is treated as an absolute number.<br>
	 * This method returns the current {@link HumanTime} instance.
	 * @param n The number of days to add.
	 * @return The updated {@link HumanTime} object.
	 */
	public HumanTime d(int n)
	{
		delta += DAY * Math.abs(n);
		return this;
	}
	
	/**
	 * Returns a new {@link HumanTime} instance with the hour value set to 1.<br>
	 * This is a convenience method for quickly creating an object representing one hour.
	 * @return A {@link HumanTime} object representing 1 hour.
	 */
	public HumanTime h()
	{
		return h(1);
	}
	
	/**
	 * Sets the hour component of the time delta.<br>
	 * This method adds the absolute value of {@code n} hours to the current total.<br>
	 * It returns the current instance to allow for method chaining.
	 * @param n The number of hours to add.
	 * @return The current {@link HumanTime} instance.
	 */
	public HumanTime h(int n)
	{
		delta += HOUR * Math.abs(n);
		return this;
	}
	
	/**
	 * Adds one minute to the current time delta.<br>
	 * This method is a convenience shortcut for {@code m} with a value of 1.
	 * @return The updated {@code HumanTime} instance.
	 */
	public HumanTime m()
	{
		return m(1);
	}
	
	/**
	 * Adds a specific number of minutes to the current time delta.<br>
	 * This method uses the absolute value of {@code n}.<br>
	 * It returns the current instance to allow for method chaining.
	 * @param n The number of minutes to add.
	 * @return The current {@link HumanTime} instance.
	 */
	public HumanTime m(int n)
	{
		delta += MINUTE * Math.abs(n);
		return this;
	}
	
	/**
	 * Sets the seconds component of the time delta.<br>
	 * This method is a convenience shortcut for {@code s} with a value of 1.
	 * @return The current {@link HumanTime} instance.
	 */
	public HumanTime s()
	{
		return s(1);
	}
	
	/**
	 * Adds a specific number of seconds to the current time delta.<br>
	 * This method uses the absolute value of {@code n}.<br>
	 * It returns the current instance to allow for method chaining.
	 * @param n The number of seconds to add.
	 * @return The current {@link HumanTime} instance.
	 */
	public HumanTime s(int n)
	{
		delta += SECOND * Math.abs(n);
		return this;
	}
	
	/**
	 * Adds one millisecond to the current time delta.<br>
	 * This method is a convenience wrapper for {@code ms} with a value of {@code 1}.
	 * @return A new {@link HumanTime} instance with the updated delta.
	 */
	public HumanTime ms()
	{
		return ms(1);
	}
	
	/**
	 * Adds a specific number of milliseconds to the current time delta.<br>
	 * This method uses the absolute value of {@code n}.<br>
	 * It returns the current instance to allow for method chaining.
	 * @param n The number of milliseconds to add.
	 * @return The current {@link HumanTime} instance.
	 */
	public HumanTime ms(int n)
	{
		delta += Math.abs(n);
		return this;
	}
	
	/**
	 * Returns the exact time delta as a formatted {@code String}.<br>
	 * This method provides a precise representation of the current time.<br>
	 * It is a convenience wrapper for the overloaded {@code getExactly} method.
	 * @return The formatted string representing the exact time.
	 */
	public String getExactly()
	{
		return getExactly(new StringBuilder()).toString();
	}
	
	/**
	 * Appends the exact time representation to the provided {@code Appendable} object.<br>
	 * This method formats the delta into years, days, hours, minutes, seconds, and milliseconds.<br>
	 * It handles spacing between units automatically.
	 * @param <T> The type of the {@code Appendable} object.
	 * @param a The {@code Appendable} where the formatted string will be written.
	 * @return The same {@code Appendable} instance used as an argument.
	 */
	public <T extends Appendable> T getExactly(T a)
	{
		try
		{
			boolean prependBlank = false;
			long d = delta;
			if (d >= YEAR)
			{
				a.append(floor(d, YEAR));
				a.append(' ');
				a.append('y');
				prependBlank = true;
			}
			
			d %= YEAR;
			if (d >= DAY)
			{
				if (prependBlank)
				{
					a.append(' ');
				}
				a.append(floor(d, DAY));
				a.append(' ');
				a.append('d');
				prependBlank = true;
			}
			
			d %= DAY;
			if (d >= HOUR)
			{
				if (prependBlank)
				{
					a.append(' ');
				}
				a.append(floor(d, HOUR));
				a.append(' ');
				a.append('h');
				prependBlank = true;
			}
			
			d %= HOUR;
			if (d >= MINUTE)
			{
				if (prependBlank)
				{
					a.append(' ');
				}
				a.append(floor(d, MINUTE));
				a.append(' ');
				a.append('m');
				prependBlank = true;
			}
			
			d %= MINUTE;
			if (d >= SECOND)
			{
				if (prependBlank)
				{
					a.append(' ');
				}
				a.append(floor(d, SECOND));
				a.append(' ');
				a.append('s');
				prependBlank = true;
			}
			
			d %= SECOND;
			if (d > 0)
			{
				if (prependBlank)
				{
					a.append(' ');
				}
				a.append(Integer.toString((int) d));
				a.append(' ');
				a.append('m');
				a.append('s');
			}
		}
		catch (IOException ex)
		{
			// What were they thinking...
		}
		
		return a;
	}
	
	/**
	 * Returns a human-readable approximation of the time delta.<br>
	 * This method rounds the time values to make them easier for people to read.<br>
	 * It simplifies the output by removing smaller units where appropriate.
	 * @return A {@code String} representing the approximate time.
	 */
	public String getApproximately()
	{
		return getApproximately(new StringBuilder()).toString();
	}
	
	/**
	 * Appends an approximate representation of the time delta to the provided {@code Appendable}.<br>
	 * This method rounds values up or down to create a human-readable format.<br>
	 * It handles units like years, days, hours, minutes, seconds, and milliseconds.
	 * @param <T> The type of the {@code Appendable} object.
	 * @param a The {@code Appendable} where the formatted string will be written.
	 * @return The same {@code Appendable} instance used as an argument.
	 */
	public <T extends Appendable> T getApproximately(T a)
	{
		try
		{
			int parts = 0;
			boolean rounded = false;
			boolean prependBlank = false;
			long d = delta;
			long mod = d % YEAR;
			
			if (mod >= upperCeiling(YEAR))
			{
				a.append(ceil(d, YEAR));
				a.append(' ');
				a.append('y');
				++parts;
				rounded = true;
				prependBlank = true;
			}
			else if (d >= YEAR)
			{
				a.append(floor(d, YEAR));
				a.append(' ');
				a.append('y');
				++parts;
				rounded = mod <= lowerCeiling(YEAR);
				prependBlank = true;
			}
			
			if (!rounded)
			{
				d %= YEAR;
				mod = d % DAY;
				
				if (mod >= upperCeiling(DAY))
				{
					if (prependBlank)
					{
						a.append(' ');
					}
					a.append(ceil(d, DAY));
					a.append(' ');
					a.append('d');
					++parts;
					rounded = true;
					prependBlank = true;
				}
				else if (d >= DAY)
				{
					if (prependBlank)
					{
						a.append(' ');
					}
					a.append(floor(d, DAY));
					a.append(' ');
					a.append('d');
					++parts;
					rounded = mod <= lowerCeiling(DAY);
					prependBlank = true;
				}
				
				if (parts < 2)
				{
					d %= DAY;
					mod = d % HOUR;
					
					if (mod >= upperCeiling(HOUR))
					{
						if (prependBlank)
						{
							a.append(' ');
						}
						a.append(ceil(d, HOUR));
						a.append(' ');
						a.append('h');
						++parts;
						rounded = true;
						prependBlank = true;
					}
					else if ((d >= HOUR) && !rounded)
					{
						if (prependBlank)
						{
							a.append(' ');
						}
						a.append(floor(d, HOUR));
						a.append(' ');
						a.append('h');
						++parts;
						rounded = mod <= lowerCeiling(HOUR);
						prependBlank = true;
					}
					
					if (parts < 2)
					{
						d %= HOUR;
						mod = d % MINUTE;
						
						if (mod >= upperCeiling(MINUTE))
						{
							if (prependBlank)
							{
								a.append(' ');
							}
							a.append(ceil(d, MINUTE));
							a.append(' ');
							a.append('m');
							++parts;
							rounded = true;
							prependBlank = true;
						}
						else if ((d >= MINUTE) && !rounded)
						{
							if (prependBlank)
							{
								a.append(' ');
							}
							a.append(floor(d, MINUTE));
							a.append(' ');
							a.append('m');
							++parts;
							rounded = mod <= lowerCeiling(MINUTE);
							prependBlank = true;
						}
						
						if (parts < 2)
						{
							d %= MINUTE;
							mod = d % SECOND;
							
							if (mod >= upperCeiling(SECOND))
							{
								if (prependBlank)
								{
									a.append(' ');
								}
								a.append(ceil(d, SECOND));
								a.append(' ');
								a.append('s');
								++parts;
								rounded = true;
								prependBlank = true;
							}
							else if ((d >= SECOND) && !rounded)
							{
								if (prependBlank)
								{
									a.append(' ');
								}
								a.append(floor(d, SECOND));
								a.append(' ');
								a.append('s');
								++parts;
								rounded = mod <= lowerCeiling(SECOND);
								prependBlank = true;
							}
							
							if (parts < 2)
							{
								d %= SECOND;
								
								if ((d > 0) && !rounded)
								{
									if (prependBlank)
									{
										a.append(' ');
									}
									a.append(Integer.toString((int) d));
									a.append(' ');
									a.append('m');
									a.append('s');
								}
							}
							
						}
						
					}
					
				}
			}
		}
		catch (IOException ex)
		{
			// What were they thinking...
		}
		
		return a;
	}
	
	/**
	 * Retrieves the raw time delta stored in this instance.<br>
	 * This value represents the total duration in milliseconds.
	 * @return The current time delta as a {@code long}.
	 */
	public long getDelta()
	{
		return delta;
	}
	
	/**
	 * Compares this object with another object for equality.<br>
	 * It checks if both objects are of type {@code HumanTime}.<br>
	 * Two instances are equal if they have the same delta value.
	 * @param obj The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if (!(obj instanceof HumanTime))
		{
			return false;
		}
		
		return delta == ((HumanTime) obj).delta;
	}
	
	/**
	 * Returns a hash code value for this {@link HumanTime} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the internal time delta.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		return (int) (delta ^ (delta >> 32));
	}
	
	/**
	 * Returns an exact string representation of the time delta.<br>
	 * This format includes years, days, hours, minutes, seconds, and milliseconds.<br>
	 * It provides a precise breakdown of the internal value.
	 * @return A formatted string representing the exact time delta.
	 */
	@Override
	public String toString()
	{
		return getExactly();
	}
	
	/**
	 * Compares this {@link HumanTime} instance with another {@code HumanTime} object.<br>
	 * This method determines the chronological order of two time deltas.
	 * @param t The other {@code HumanTime} to compare against.
	 * @return A negative integer if this time is earlier, zero if they are equal, and a positive integer if this time is later.
	 */
	@Override
	public int compareTo(HumanTime t)
	{
		return delta == t.delta ? 0 : (delta < t.delta ? -1 : 1);
	}
	
	/**
	 * Creates and returns a copy of this {@code CM_FILE_VERIFY} object.<br>
	 * This method uses the default cloning mechanism from the parent class.
	 * @return A new object that is a copy of this instance.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	/**
	 * Reads the time delta from an external input stream.<br>
	 * This method updates the internal state of this {@link HumanTime} instance.
	 * @param in The {@link ObjectInput} used to read the data.
	 * @throws IOException If an error occurs during reading.
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException
	{
		delta = in.readLong();
	}
	
	/**
	 * Writes the time delta of this object to an external stream.<br>
	 * This method is used for serializing the object data.
	 * @param out The {@link ObjectOutput} where the data will be written.
	 * @throws IOException If an error occurs during writing.
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeLong(delta);
	}
}
