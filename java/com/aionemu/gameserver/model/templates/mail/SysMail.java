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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * Represents a system-generated mail template used by the game server.<br>
 * This class handles the data structure for messages sent from the system to players.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SysMail", propOrder =
{
	"templates"
})
public class SysMail
{
	@XmlElement(name = "template", required = true)
	private List<MailTemplate> templates;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlTransient
	private final Map<String, List<MailTemplate>> mailCaseTemplates = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code mailCaseTemplates} map using the list of {@link MailTemplate} objects.<br>
	 * The {@code templates} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (MailTemplate template : templates)
		{
			final String caseName = template.getName().toLowerCase();
			List<MailTemplate> sysTemplates = mailCaseTemplates.get(caseName);
			if (sysTemplates == null)
			{
				sysTemplates = new ArrayList<>();
				mailCaseTemplates.put(caseName, sysTemplates);
			}
			
			sysTemplates.add(template);
		}
		
		templates.clear();
		templates = null;
	}
	
	/**
	 * Retrieves a specific mail template based on an event and player race.<br>
	 * It searches for templates matching the provided {@code eventName}.<br>
	 * The method returns the first template that matches the {@code playerRace} or is set to {@code PC_ALL}.
	 * @param eventName The name of the event used to identify the mail category.
	 * @param playerRace The race of the player receiving the mail.
	 * @return The matching {@link MailTemplate} object, or {@code null} if no match is found.
	 */
	public MailTemplate getTemplate(String eventName, Race playerRace)
	{
		final List<MailTemplate> sysTemplates = mailCaseTemplates.get(eventName.toLowerCase());
		if (sysTemplates == null)
		{
			return null;
		}
		
		for (MailTemplate template : sysTemplates)
		{
			if ((template.getRace() == playerRace) || (template.getRace() == Race.PC_ALL))
			{
				return template;
			}
		}
		
		return null;
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
}
