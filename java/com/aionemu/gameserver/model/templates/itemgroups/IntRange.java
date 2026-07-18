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
package com.aionemu.gameserver.model.templates.itemgroups;

/**
 * An inclusive integer range used as a craft-reward lookup key.<br>
 * JDK replacement for the removed Apache commons-lang {@code IntRange}.<br>
 * Being a record it provides value-based {@code equals}/{@code hashCode}, so it is safe as a {@link java.util.HashMap} key.
 * @param min the inclusive lower bound of the range
 * @param max the inclusive upper bound of the range
 */
public record IntRange(int min, int max)
{
	/**
	 * Tests whether a value falls within this range, bounds inclusive.
	 * @param value the value to test
	 * @return {@code true} if {@code min <= value <= max}
	 */
	public boolean containsInteger(int value)
	{
		return (value >= min) && (value <= max);
	}
}
