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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * Represents a template for in-game mail messages.<br>
 * This class holds the data required to format and send emails to players.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailTemplate")
public class MailTemplate
{
	/**
	 * Represents the structural components of a mail template.<br>
	 * It includes fields for the {@link Sender}, {@link Title}, {@link Header}, {@link Body}, and {@link Tail}.
	 */
	@XmlElements(
	{
		@XmlElement(name = "sender", type = Sender.class),
		@XmlElement(name = "title", type = Title.class),
		@XmlElement(name = "header", type = Header.class),
		@XmlElement(name = "body", type = Body.class),
		@XmlElement(name = "tail", type = Tail.class)
	})
	private List<MailPart> mailParts;
	@XmlAttribute(name = "name", required = true)
	protected String name;
	@XmlAttribute(name = "race", required = true)
	protected Race race;
	@XmlTransient
	private final Map<MailPartType, MailPart> mailPartsMap = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code mailPartsMap} using the list of {@link MailPart} objects.<br>
	 * The {@code mailParts} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (MailPart part : mailParts)
		{
			mailPartsMap.put(part.getType(), part);
		}
		
		mailParts.clear();
		mailParts = null;
	}
	
	/**
	 * Retrieves the sender part of the mail template.<br>
	 * This method looks up the {@code MailPart} associated with the {@code SENDER} type.
	 * @return the {@link MailPart} representing the sender, or {@code null} if not found.
	 */
	public MailPart getSender()
	{
		return mailPartsMap.get(MailPartType.SENDER);
	}
	
	/**
	 * Retrieves the title part of the mail template.<br>
	 * This method looks up the {@code MailPart} associated with the {@code TITLE} type.
	 * @return the {@code MailPart} object representing the title, or {@code null} if not found.
	 */
	public MailPart getTitle()
	{
		return mailPartsMap.get(MailPartType.TITLE);
	}
	
	/**
	 * Retrieves the header part of the mail template.<br>
	 * This method looks up the {@code MailPart} associated with the {@code HEADER} type.
	 * @return The {@link MailPart} object representing the header.
	 */
	public MailPart getHeader()
	{
		return mailPartsMap.get(MailPartType.HEADER);
	}
	
	/**
	 * Retrieves the body section of the mail template.<br>
	 * This method looks up the {@code MailPart} associated with the {@code BODY} type.
	 * @return the {@link MailPart} object representing the main content of the mail.
	 */
	public MailPart getBody()
	{
		return mailPartsMap.get(MailPartType.BODY);
	}
	
	/**
	 * Retrieves the tail section of the mail template.<br>
	 * This part is typically displayed at the very end of the message.
	 * @return the {@link MailPart} corresponding to the tail type, or {@code null} if it does not exist.
	 */
	public MailPart getTail()
	{
		return mailPartsMap.get(MailPartType.TAIL);
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Gets the title of the mail template as a formatted string.<br>
	 * It uses the provided {@code IMailFormatter} to handle the formatting logic.<br>
	 * This method calls {@code getTitle} to retrieve the raw content.
	 * @param customFormatter The formatter used to style the title text.
	 * @return The final formatted title as a {@code String}.
	 */
	public String getFormattedTitle(IMailFormatter customFormatter)
	{
		return getTitle().getFormattedString(customFormatter);
	}
	
	/**
	 * This method creates a single string from the mail parts.<br>
	 * It combines the header, body, and tail content using a comma separator.<br>
	 * The {@code customFormatter} is used to process each part individually.
	 * @param customFormatter The {@link IMailFormatter} used to format the text components.
	 * @return A concatenated string containing all non-empty mail parts.
	 */
	public String getFormattedMessage(IMailFormatter customFormatter)
	{
		final String headerStr = getHeader().getFormattedString(customFormatter);
		final String bodyStr = getBody().getFormattedString(customFormatter);
		final String tailStr = getTail().getFormattedString(customFormatter);
		String message = headerStr;
		if ((message == null) || message.isEmpty())
		{
			message = bodyStr;
		}
		else if ((bodyStr != null) && !bodyStr.isEmpty())
		{
			message += "," + bodyStr;
		}
		
		if ((message == null) || message.isEmpty())
		{
			message = tailStr;
		}
		else if ((tailStr != null) && !tailStr.isEmpty())
		{
			message += "," + tailStr;
		}
		
		return message;
	}
}
