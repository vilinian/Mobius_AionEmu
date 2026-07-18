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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different types of content parts that can exist within a mail template.<br>
 * This enum is used to categorize components like text, images, or HTML in {@link com.aionemu.gameserver.model.templates.mail.MailTemplate}.
 * @author Rolandas
 */
@XmlType(name = "MailPartType")
@XmlEnum
public enum MailPartType
{
	CUSTOM,
	SENDER,
	TITLE,
	HEADER,
	BODY,
	TAIL;
	
	/**
	 * Returns the name of this {@code ChallengeType}.<br>
	 * This is useful for getting a human-readable string representation.
	 * @return The name of the enum constant as a {@code String}.
	 */
	public String value()
	{
		return name();
	}
	
	/**
	 * Converts a string into its corresponding {@link MailPartType}.<br>
	 * This method is used to map text values back to the enum constants.
	 * @param v The string value to convert.
	 * @return The matching {@code MailPartType} constant.
	 */
	public static MailPartType fromValue(String v)
	{
		return valueOf(v);
	}
}
