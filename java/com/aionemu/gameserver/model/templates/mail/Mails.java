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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * This class serves as a template repository for all in-game mail messages.<br>
 * It defines the content and structure of various mail types used by the server.<br>
 * Developers can use this class to manage how different notifications are sent to players.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"sysMailTemplates"
})
@XmlRootElement(name = "mails")
public class Mails
{
	@XmlElement(name = "mail")
	private List<SysMail> sysMailTemplates;
	@XmlTransient
	private final Map<String, SysMail> sysMailByName = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code sysMailByName} map using the list of {@link SysMail} templates.<br>
	 * The {@code sysMailTemplates} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (SysMail template : sysMailTemplates)
		{
			final String sysMailName = template.getName().toLowerCase();
			sysMailByName.put(sysMailName, template);
		}
		
		sysMailTemplates.clear();
		sysMailTemplates = null;
	}
	
	/**
	 * Retrieves a specific mail template based on its name and context.<br>
	 * This method looks up the template by {@code name} and applies filters for an event and race.
	 * @param name The unique identifier of the mail template.
	 * @param eventName The name of the event triggering the mail.
	 * @param playerRace The {@link Race} of the player receiving the mail.
	 * @return The matching {@link MailTemplate} object or {@code null} if not found.
	 */
	public MailTemplate getMailTemplate(String name, String eventName, Race playerRace)
	{
		final SysMail template = sysMailByName.get(name.toLowerCase());
		if (template == null)
		{
			return null;
		}
		
		return template.getTemplate(eventName, playerRace);
	}
	
	/**
	 * Returns the total number of mail templates.<br>
	 * This count represents all entries in the {@code sysMailByName} map.
	 * @return The number of mail templates currently available.
	 */
	public int size()
	{
		return sysMailByName.values().size();
	}
}
