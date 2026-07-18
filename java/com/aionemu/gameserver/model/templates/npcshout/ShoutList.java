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
 * Java class for ShoutList complex type.
 * <p/>
 * The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * 
 * <pre>
 * &lt;complexType name="ShoutList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shout" type="{}NpcShout" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="npc_ids" use="required" type="{}NpcList" />
 *       &lt;attribute name="restrict_world" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShoutList", propOrder =
{
	"npcShouts"
})
public class ShoutList
{
	@XmlElement(name = "shout", required = true)
	protected List<NpcShout> npcShouts;
	@XmlAttribute(name = "npc_ids", required = true)
	protected List<Integer> npcIds;
	@XmlAttribute(name = "restrict_world")
	protected Integer restrictWorld;
	
	/**
	 * Retrieves the list of {@link NpcShout} objects.<br>
	 * This method ensures that a new {@code ArrayList} is created if the internal list is {@code null}.<br>
	 * The returned list is a live reference to the internal data.
	 * @return A {@code List} containing all {@code NpcShout} entries.
	 */
	public List<NpcShout> getNpcShouts()
	{
		if (npcShouts == null)
		{
			npcShouts = new ArrayList<>();
		}
		
		return npcShouts;
	}
	
	/**
	 * Retrieves the list of NPC identifiers for this group.<br>
	 * Returns an empty {@code List} if no IDs are defined.
	 * @return a {@code List<Integer>} containing the NPC IDs.
	 */
	public List<Integer> getNpcIds()
	{
		if (npcIds == null)
		{
			npcIds = new ArrayList<>();
		}
		
		return npcIds;
	}
	
	/**
	 * Retrieves the world restriction value.<br>
	 * This method returns {@code 0} if the internal field is {@code null}.
	 * @return The integer value of the world restriction.
	 */
	public int getRestrictWorld()
	{
		if (restrictWorld == null)
		{
			return 0;
		}
		
		return restrictWorld;
	}
	
	/**
	 * This method resets all properties of this object.<br>
	 * It sets {@code npcIds}, {@code npcShouts}, and {@code restrictWorld} to {@code null}.
	 */
	public void makeNull()
	{
		npcIds = null;
		npcShouts = null;
		restrictWorld = null;
	}
}
