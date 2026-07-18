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
package com.aionemu.gameserver.network.sequrity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages and prevents network flood attacks on the game server.<br>
 * It monitors incoming packets to ensure they do not exceed defined limits.<br>
 * It helps maintain server stability by identifying and blocking malicious traffic.
 * @author NB4L1
 */
public final class FloodManager
{
	public static enum ErrorMode
	{
		INVALID_OPCODE,
		BUFFER_UNDER_FLOW,
		BUFFER_OVER_FLOW,
		FAILED_READING,
		FAILED_RUNNING;
	}
	
	public static final class FloodFilter
	{
		private final int _warnLimit;
		private final int _rejectLimit;
		private final int _tickLimit;
		
		public FloodFilter(final int warnLimit, int rejectLimit, int tickLimit)
		{
			_warnLimit = warnLimit;
			_rejectLimit = rejectLimit;
			_tickLimit = tickLimit;
		}
		
		public int getRejectLimit()
		{
			return _rejectLimit;
		}
		
		public int getTickLimit()
		{
			return _tickLimit;
		}
		
		public int getWarnLimit()
		{
			return _warnLimit;
		}
	}
	
	private final class LogEntry
	{
		private final short[] _ticks = new short[_tickAmount];
		private int _lastTick = getCurrentTick();
		
		public int getCurrentTick()
		{
			return (int) ((System.currentTimeMillis() - ZERO) / _tickLength);
		}
		
		public boolean isActive()
		{
			return (getCurrentTick() - _lastTick) < (_tickAmount * 10);
		}
		
		public Result isFlooding(boolean increment)
		{
			final int currentTick = getCurrentTick();
			
			if ((currentTick - _lastTick) >= _ticks.length)
			{
				_lastTick = currentTick;
				Arrays.fill(_ticks, (short) 0);
			}
			else if (_lastTick > currentTick)
			{
				log.warn("The current tick (" + currentTick + ") is smaller than the last (" + _lastTick + ")!", new IllegalStateException());
				_lastTick = currentTick;
			}
			else
			{
				while (currentTick != _lastTick)
				{
					_lastTick++;
					_ticks[_lastTick % _ticks.length] = 0;
				}
			}
			
			if (increment)
			{
				_ticks[_lastTick % _ticks.length]++;
			}
			
			for (FloodFilter filter : _filters)
			{
				int previousSum = 0;
				int currentSum = 0;
				
				for (int i = 0; i <= filter.getTickLimit(); i++)
				{
					final int value = _ticks[(_lastTick - i) % _ticks.length];
					
					if (i != 0)
					{
						previousSum += value;
					}
					
					if (i != filter.getTickLimit())
					{
						currentSum += value;
					}
				}
				
				if ((previousSum > filter.getRejectLimit()) || (currentSum > filter.getRejectLimit()))
				{
					return Result.REJECTED;
				}
				
				if ((previousSum > filter.getWarnLimit()) || (currentSum > filter.getWarnLimit()))
				{
					return Result.WARNED;
				}
			}
			
			return Result.ACCEPTED;
		}
	}
	
	public static enum Result
	{
		ACCEPTED,
		WARNED,
		REJECTED;
		
		public static Result max(Result r1, Result r2)
		{
			if (r1.ordinal() > r2.ordinal())
			{
				return r1;
			}
			
			return r2;
		}
	}
	
	public final Logger log = LoggerFactory.getLogger(FloodManager.class);
	private static final long ZERO = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
	private final Map<String, LogEntry> _entries = new HashMap<>();
	private final ReentrantLock _lock = new ReentrantLock();
	private final int _tickLength;
	private final int _tickAmount;
	private final FloodFilter[] _filters;
	
	/**
	 * Initializes a new {@link FloodManager} instance.<br>
	 * This constructor sets up the tick timing and applies all provided filters.<br>
	 * It also schedules a background task to periodically clear old data.
	 * @param msecPerTick The number of milliseconds used to calculate one game tick.
	 * @param filters A variable number of {@code FloodFilter} objects to apply during flood checking.
	 */
	public FloodManager(final int msecPerTick, FloodFilter... filters)
	{
		_tickLength = msecPerTick;
		_filters = filters;
		
		int max = 1;
		
		for (FloodFilter filter : _filters)
		{
			max = Math.max(filter.getTickLimit() + 1, max);
		}
		
		_tickAmount = max;
		
		NetFlusher.add(new Runnable()
		{
			@Override
			public void run()
			{
				flush();
			}
		}, 60000);
	}
	
	/**
	 * Clears inactive entries from the internal log map.<br>
	 * This method removes any {@code LogEntry} that is no longer active.<br>
	 * It uses a {@code ReentrantLock} to ensure thread safety during the cleanup.
	 */
	private void flush()
	{
		_lock.lock();
		try
		{
			for (Iterator<LogEntry> it = _entries.values().iterator(); it.hasNext();)
			{
				if (it.next().isActive())
				{
					continue;
				}
				
				it.remove();
			}
		}
		finally
		{
			_lock.unlock();
		}
	}
	
	/**
	 * Checks if a specific action is being performed too frequently.<br>
	 * It uses the provided {@code key} to identify the unique activity.<br>
	 * If the activity exceeds limits, it returns a flood status.
	 * @param key The unique identifier for the action to check.
	 * @param increment Whether to increase the count of the current activity.
	 * @return The {@code Result} of the flooding check.
	 */
	public Result isFlooding(String key, boolean increment)
	{
		if ((key == null) || key.isEmpty())
		{
			return Result.REJECTED;
		}
		
		_lock.lock();
		try
		{
			LogEntry entry = _entries.get(key);
			
			if (entry == null)
			{
				entry = new LogEntry();
				
				_entries.put(key, entry);
			}
			
			return entry.isFlooding(increment);
		}
		finally
		{
			_lock.unlock();
		}
	}
}
