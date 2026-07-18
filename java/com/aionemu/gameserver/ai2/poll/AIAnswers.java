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
 * This class serves as a data container for storing responses from the AI polling system.<br>
 * It holds the results processed by the game server to determine appropriate actions.
 * @author ATracer
 */
public class AIAnswers
{
	public static final AIAnswer POSITIVE = new SimpleAIAnswer(true);
	public static final AIAnswer NEGATIVE = new SimpleAIAnswer(false);
}
