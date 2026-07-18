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
package com.aionemu.gameserver.ai2.poll;

/**
 * This class represents a basic implementation of the {@link AIAnswer} interface.<br>
 * It provides a simple way to handle responses within the AI polling system.
 * @author ATracer
 */
public class SimpleAIAnswer implements AIAnswer
{
	private final boolean answer;
	
	/**
	 * Creates a new instance of {@code SimpleAIAnswer}.<br>
	 * This method sets the internal state based on the provided boolean.
	 * @param answer The boolean value to store in the object.
	 */
	SimpleAIAnswer(boolean answer)
	{
		this.answer = answer;
	}
	
	/**
	 * Checks if the current answer is positive.<br>
	 * This method returns {@code true} if the value is positive.<br>
	 * It returns {@code false} otherwise.
	 * @return The boolean state of the answer.
	 */
	@Override
	public boolean isPositive()
	{
		return answer;
	}
	
	/**
	 * Retrieves the result of the AI calculation.<br>
	 * This method returns the underlying {@code boolean} value.
	 * @return The result as an {@code Object}.
	 */
	@Override
	public Object getResult()
	{
		return answer;
	}
}
