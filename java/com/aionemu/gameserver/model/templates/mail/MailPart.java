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
 * Represents an individual component or section of a mail message.<br>
 * This class is used to structure the content within a {@code Mail} object.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailPart")
@XmlSeeAlso(
{
	Sender.class,
	Header.class,
	Body.class,
	Tail.class,
	Title.class
})
public abstract class MailPart extends StringParamList implements IMailFormatter
{
	@XmlAttribute(name = "id")
	protected Integer id;
	
	/**
	 * Retrieves the {@code MailPartType} of this mail part.<br>
	 * If no specific type is set, it returns {@code BODY}.
	 * @return The current {@code MailPartType}.
	 */
	@Override
	public MailPartType getType()
	{
		return MailPartType.CUSTOM;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link MailPart}.<br>
	 * This value is used to distinguish different parts of a mail.
	 * @return The {@code Integer} ID of the part, or {@code null} if not set.
	 */
	public Integer getId()
	{
		return id;
	}
	
	/**
	 * Converts the mail part into a formatted string.<br>
	 * It uses the provided {@code customFormatter} if it is not {@code null}.<br>
	 * If no formatter is provided, it uses the default one from this object.
	 * @param customFormatter The {@link IMailFormatter} to use for formatting.
	 * @return The final formatted string as a {@code String}.
	 */
	public String getFormattedString(IMailFormatter customFormatter)
	{
		String result = "";
		IMailFormatter formatter = this;
		if (customFormatter != null)
		{
			formatter = customFormatter;
		}
		
		result = getFormattedString(getType());
		
		final String[] paramValues = new String[getParam().size()];
		for (int i = 0; i < getParam().size(); i++)
		{
			final Param param = getParam().get(i);
			paramValues[i] = formatter.getParamValue(param.getId());
		}
		
		final String joinedParams = String.join(",", paramValues);
		if ((result == null) || result.isEmpty())
		{
			return joinedParams;
		}
		else if ((joinedParams != null) && !joinedParams.isEmpty())
		{
			result += "," + joinedParams;
		}
		
		return result;
	}
	
	/**
	 * Converts the mail part into a formatted string based on its type.<br>
	 * This method retrieves the {@code id} and returns it as a string.<br>
	 * If the {@code id} is less than or equal to 0, an empty string is returned.
	 * @param partType The type of the mail part to format.
	 * @return A formatted string representation of the mail part.
	 */
	@Override
	public String getFormattedString(MailPartType partType)
	{
		String result = "";
		if (id > 0)
		{
			result += id.toString();
		}
		
		return result;
	}
}
