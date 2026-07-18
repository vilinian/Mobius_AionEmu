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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the sender information for a mail template.<br>
 * This class holds the data required to identify who is sending the message.<br>
 * It is used by the {@code Mail} system.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sender")
@XmlSeeAlso(
{
	com.aionemu.gameserver.model.templates.mail.MailPart.class
})
public class Sender extends MailPart
{
	@XmlAttribute(name = "type")
	protected MailPartType type;
	
	/**
	 * Gets the {@code MailPartType} for this mail part.<br>
	 * If the type is {@code null}, it returns {@code SENDER}.
	 * @return The current {@code MailPartType}.
	 */
	@Override
	public MailPartType getType()
	{
		if (type == null)
		{
			return MailPartType.SENDER;
		}
		
		return type;
	}
	
	/**
	 * Retrieves the value of a specific parameter by its name.<br>
	 * This method returns an empty {@code String} if no value is found.
	 * @param name The name of the parameter to look up.
	 * @return The value associated with the provided {@code name}.
	 */
	@Override
	public String getParamValue(String name)
	{
		return "";
	}
}
