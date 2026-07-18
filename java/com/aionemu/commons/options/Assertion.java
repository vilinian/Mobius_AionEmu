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
package com.aionemu.commons.options;

/**
 * This class contains {@code public static final boolean} constants to control where assertions are enabled.<br>
 * If a value is set to {@code false}, the {@code javac} compiler will remove the corresponding assertion code at compile time.
 * @author -Nemesiss-
 */
public final class Assertion
{
	/**
	 * False if assertion at Network code should be removed at compile time. [0 overhead]
	 */
	public static final boolean NetworkAssertion = false;
}
