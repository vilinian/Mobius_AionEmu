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
package com.aionemu.gameserver.model.templates.npcshout;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p/>
 * Java class for ShoutGroup complex type.
 * <p/>
 * The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * 
 * <pre>
 * &lt;complexType name="ShoutGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shout_npcs" type="{}ShoutList" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="client_ai" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShoutGroup", propOrder =
{
	"shoutNpcs"
})
public class ShoutGroup
{
	@XmlElement(name = "shout_npcs", required = true)
	protected List<ShoutList> shoutNpcs;
	@XmlAttribute(name = "client_ai")
	protected String clientAi;
	
	/**
	 * Retrieves the list of {@link ShoutList} objects associated with this group.<br>
	 * If the internal list is {@code null}, a new {@code ArrayList} is created.<br>
	 * This method returns a reference to the live list rather than a copy.
	 * @return A {@code List} containing all {@code ShoutList} entries.
	 */
	public List<ShoutList> getShoutNpcs()
	{
		if (shoutNpcs == null)
		{
			shoutNpcs = new ArrayList<>();
		}
		
		return shoutNpcs;
	}
	
	/**
	 * Retrieves the AI identifier for the client.<br>
	 * This value is stored as a {@code String}.
	 * @return The {@code client_ai} attribute value.
	 */
	public String getClientAi()
	{
		return clientAi;
	}
	
	/**
	 * This method resets the object properties.<br>
	 * It sets both {@code shoutNpcs} and {@code clientAi} to {@code null}.
	 */
	public void makeNull()
	{
		shoutNpcs = null;
		clientAi = null;
	}
}
