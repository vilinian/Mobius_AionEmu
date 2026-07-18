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
package com.aionemu.gameserver.model.templates.mail;

/**
 * This interface defines the standard methods for formatting mail content.<br>
 * It is used to convert raw data into a readable format for players.<br>
 * Implementations handle how different types of messages are displayed in the game.
 * @author Rolandas
 */
public interface IMailFormatter
{
	MailPartType getType();
	
	String getFormattedString(MailPartType partType);
	
	String getParamValue(String name);
}
