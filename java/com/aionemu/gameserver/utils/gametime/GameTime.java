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
package com.aionemu.gameserver.utils.gametime;

import java.security.InvalidParameterException;

import com.aionemu.gameserver.services.WeatherService;
import com.aionemu.gameserver.spawnengine.TemporarySpawnEngine;

/**
 * This class represents the internal clock for the time within the Aion world.<br>
 * It manages the progression of game time and provides access to current time values.
 * @author Ben, reworked by vlog
 */
public class GameTime implements Cloneable
{
	private static final int MINUTES_IN_HOUR = 60;
	private static final int MINUTES_IN_DAY = MINUTES_IN_HOUR * 24;
	private static final int MINUTES_IN_YEAR = ((31 * 7) + (30 * 4) + (28 * 1)) * MINUTES_IN_DAY;
	private int gameTime = 0;
	private DayTime dayTime;
	
	private enum Monthes
	{
		JANUARY(31),
		FEBRUARY(28),
		MARCH(31),
		APRIL(30),
		MAY(31),
		JUNE(30),
		JULY(31),
		AUGUST(31),
		SEPTEMBER(30),
		OCTOBER(31),
		NOVEMBER(30),
		DECEMBER(31);
		
		private final int _days;
		
		Monthes(int days)
		{
			_days = days;
		}
		
		public int getDays()
		{
			return _days;
		}
	}
	
	/**
	 * Creates a new {@link GameTime} instance.<br>
	 * This constructor sets the initial clock based on minutes passed since midnight of 01.01.0000.<br>
	 * It automatically calls {@code calculateDayTime} to update internal values.
	 * @param time The total number of minutes since the start date. Must be greater than or equal to {@code 0}.
	 */
	public GameTime(int time)
	{
		if (time < 0)
		{
			throw new InvalidParameterException("Time must be >= 0");
		}
		
		gameTime = time;
		calculateDayTime();
	}
	
	/**
	 * Calculates the total number of minutes in a specific month.<br>
	 * This method uses the days defined in the {@code Monthes} enum.<br>
	 * It multiplies the number of days by the constant for minutes in one day.
	 * @param m The {@code Monthes} object representing the month to calculate.
	 * @return The total number of minutes in that month as an {@code int}.
	 */
	public int getProperMinutesInMonth(Monthes m)
	{
		return m.getDays() * MINUTES_IN_DAY;
	}
	
	/**
	 * Retrieves the current internal game time.<br>
	 * The value represents total minutes since midnight on 01.01.0000.
	 * @return the current game time as an {@code int}
	 */
	public int getTime()
	{
		return gameTime;
	}
	
	/**
	 * Advances the internal game clock by one minute.<br>
	 * This method updates the {@code gameTime} value.<br>
	 * It also triggers a check for day time changes if the new minute is {@code 0}.
	 */
	public void increase()
	{
		gameTime++;
		if (getMinute() == 0)
		{
			checkDayTimeChange();
		}
	}
	
	/**
	 * Updates the current game time and checks for changes.<br>
	 * This method calls {@code calculateDayTime} to refresh the state.<br>
	 * It triggers specific events if the day of the week changes.
	 */
	public void checkDayTimeChange()
	{
		final DayTime oldDayTime = dayTime;
		calculateDayTime();
		onHourChange();
		if (oldDayTime != dayTime)
		{
			onDayTimeChange();
		}
	}
	
	/**
	 * Updates the current {@link DayTime} based on the game hour.<br>
	 * This method checks the value from {@code getHour} to determine if it is morning, afternoon, evening, or night.
	 */
	public void calculateDayTime()
	{
		final int hour = getHour();
		if ((hour > 21) || (hour < 4))
		{
			dayTime = DayTime.NIGHT;
		}
		else if (hour > 16)
		{
			dayTime = DayTime.EVENING;
		}
		else if (hour > 8)
		{
			dayTime = DayTime.AFTERNOON;
		}
		else
		{
			dayTime = DayTime.MORNING;
		}
	}
	
	/**
	 * Updates the game world when the hour changes.<br>
	 * This method notifies the {@link TemporarySpawnEngine} to refresh its data.
	 */
	private void onHourChange()
	{
		TemporarySpawnEngine.onHourChange();
	}
	
	/**
	 * Updates the weather based on the new day time.<br>
	 * This method calls {@code checkWeathersTime}.
	 */
	private void onDayTimeChange()
	{
		WeatherService.getInstance().checkWeathersTime();
	}
	
	/**
	 * Retrieves the current year from the internal clock.<br>
	 * This value is calculated based on the total minutes elapsed since the start date.
	 * @return The current year as an {@code int}.
	 */
	public int getYear()
	{
		return gameTime / MINUTES_IN_YEAR;
	}
	
	/**
	 * Retrieves the current month of the game time.<br>
	 * This method calculates the month based on the total minutes elapsed.
	 * @return The month as an integer value.
	 */
	public int getMonth()
	{
		int answer = 1;
		int minutesInYear = gameTime % MINUTES_IN_YEAR;
		for (Monthes m : Monthes.values())
		{
			if ((minutesInYear - getProperMinutesInMonth(m)) > 0)
			{
				minutesInYear = minutesInYear - getProperMinutesInMonth(m);
				answer = answer + 1;
			}
			else if ((minutesInYear - getProperMinutesInMonth(m)) == 0)
			{
				answer = answer + 1;
				break;
			}
			else
			{
				break;
			}
		}
		
		return answer;
	}
	
	/**
	 * Retrieves the current day of the year.<br>
	 * This value is calculated based on the total minutes elapsed since the start of the year.
	 * @return The integer representation of the current day.
	 */
	public int getDay()
	{
		int answer = 1;
		int minutesInYear = gameTime % MINUTES_IN_YEAR;
		for (Monthes m : Monthes.values())
		{
			if ((minutesInYear - getProperMinutesInMonth(m)) > 0)
			{
				minutesInYear = minutesInYear - getProperMinutesInMonth(m);
			}
			else if ((minutesInYear - getProperMinutesInMonth(m)) == 0)
			{
				break;
			}
			else
			{
				answer = (minutesInYear / MINUTES_IN_DAY) + 1;
				break;
			}
		}
		
		return answer;
	}
	
	/**
	 * Retrieves the current hour from the game time.<br>
	 * This value is calculated based on the total minutes elapsed.
	 * @return The current hour as an {@code int}.
	 */
	public int getHour()
	{
		return (gameTime % MINUTES_IN_DAY) / (MINUTES_IN_HOUR);
	}
	
	/**
	 * Retrieves the current minute of the hour.<br>
	 * This value is calculated based on the internal {@code gameTime}.
	 * @return The current minute as an {@code int}.
	 */
	public int getMinute()
	{
		return (gameTime % MINUTES_IN_HOUR);
	}
	
	/**
	 * Retrieves the current {@link DayTime} object.<br>
	 * This represents the specific time of day in the game world.
	 * @return The current {@code DayTime} instance.
	 */
	public DayTime getDayTime()
	{
		return dayTime;
	}
	
	/**
	 * Converts the current game time into a specific unit.<br>
	 * This method divides the total minutes by {@code 12}.
	 * @return The converted time value as an {@code int}.
	 */
	public int convertTime()
	{
		return getTime() / 12;
	}
	
	/**
	 * Subtracts one {@link GameTime} from the current time.<br>
	 * This method returns a new {@code GameTime} object.<br>
	 * It calculates the difference based on total minutes.
	 * @param gt The {@code GameTime} to subtract.
	 * @return A new {@code GameTime} representing the result of the subtraction.
	 */
	public GameTime minus(GameTime gt)
	{
		return new GameTime(getTime() - gt.getTime());
	}
	
	/**
	 * Adds the time from another {@link GameTime} object to this one.<br>
	 * This method returns a new instance representing the sum of both times.
	 * @param gt The {@code GameTime} value to add.
	 * @return A new {@code GameTime} object containing the combined total.
	 */
	public GameTime plus(GameTime gt)
	{
		return new GameTime(getTime() + gt.getTime());
	}
	
	/**
	 * Checks if this time is later than another {@link GameTime}.<br>
	 * It compares the total minutes of both objects.
	 * @param gt The other {@code GameTime} to compare against.
	 * @return {@code true} if this time is greater, otherwise {@code false}.
	 */
	public boolean isGreaterThan(GameTime gt)
	{
		return getTime() > gt.getTime();
	}
	
	/**
	 * Checks if this time is earlier than another {@link GameTime}.<br>
	 * It compares the total minutes of both objects.
	 * @param gt The other {@link GameTime} to compare against.
	 * @return {@code true} if this time is less than {@code gt}, otherwise {@code false}.
	 */
	public boolean isLessThan(GameTime gt)
	{
		return getTime() < gt.getTime();
	}
	
	/**
	 * Compares this {@link GameTime} object with another object for equality.<br>
	 * It checks if both objects represent the same time in minutes.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		final GameTime other = (GameTime) o;
		return getTime() == other.getTime();
	}
	
	/**
	 * Returns a hash code value for this {@link GameTime} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the default implementation of the {@code Object} class.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	/**
	 * Creates and returns a copy of this {@link GameTime} object.<br>
	 * This method creates a new instance with the same internal time value.
	 * @return A new {@code Object} that is a copy of this instance, or {@code null} if cloning fails.
	 */
	@Override
	public Object clone()
	{
		return new GameTime(gameTime);
	}
}
