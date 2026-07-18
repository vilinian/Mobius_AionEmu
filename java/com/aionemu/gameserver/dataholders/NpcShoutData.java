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
package com.aionemu.gameserver.dataholders;

import java.util.ArrayList;
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

import com.aionemu.gameserver.model.templates.npcshout.NpcShout;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.model.templates.npcshout.ShoutGroup;
import com.aionemu.gameserver.model.templates.npcshout.ShoutList;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * <p/>
 * Java class for anonymous complex type.
 * <p/>
 * The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shout_group" type="{}ShoutGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"shoutGroups"
})
@XmlRootElement(name = "npc_shouts")
public class NpcShoutData
{
	@XmlElement(name = "shout_group")
	protected List<ShoutGroup> shoutGroups;
	@XmlTransient
	private final TIntObjectHashMap<Map<Integer, List<NpcShout>>> shoutsByWorldNpcs = new TIntObjectHashMap<>();
	@XmlTransient
	private int count = 0;
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It processes the {@code shoutGroups} to populate the internal maps.<br>
	 * It also clears the temporary lists used during the loading process.
	 * @param u The {@link Unmarshaller} used to load the data.
	 * @param parent The parent object of this instance.
	 */
	public void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (ShoutGroup group : shoutGroups)
		{
			for (int i = group.getShoutNpcs().size() - 1; i >= 0; i--)
			{
				final ShoutList shoutList = group.getShoutNpcs().get(i);
				final int worldId = shoutList.getRestrictWorld();
				
				Map<Integer, List<NpcShout>> worldShouts = shoutsByWorldNpcs.get(worldId);
				if (worldShouts == null)
				{
					worldShouts = new HashMap<>();
					shoutsByWorldNpcs.put(worldId, worldShouts);
				}
				
				count += shoutList.getNpcShouts().size();
				for (int j = shoutList.getNpcIds().size() - 1; j >= 0; j--)
				{
					final int npcId = shoutList.getNpcIds().get(j);
					final List<NpcShout> shouts = new ArrayList<>(shoutList.getNpcShouts());
					if (worldShouts.get(npcId) == null)
					{
						worldShouts.put(npcId, shouts);
					}
					else
					{
						worldShouts.get(npcId).addAll(shouts);
					}
					
					shoutList.getNpcIds().remove(j);
				}
				
				shoutList.getNpcShouts().clear();
				shoutList.makeNull();
				group.getShoutNpcs().remove(i);
			}
			
			group.makeNull();
		}
		
		shoutGroups.clear();
		shoutGroups = null;
	}
	
	/**
	 * Returns the total number of shout groups.<br>
	 * This value is stored in the {@code count} field.
	 * @return The total number of elements.
	 */
	public int size()
	{
		return count;
	}
	
	/**
	 * Retrieves the list of shouts for a specific NPC in a given world.<br>
	 * This method combines shouts from both the default world and the specified {@code worldId}.<br>
	 * It returns a new {@code ArrayList} containing all matching {@link NpcShout} objects.
	 * @param worldId The unique identifier of the world.
	 * @param npcId The unique identifier of the NPC.
	 * @return A list of {@link NpcShout} objects, or {@code null} if no shouts are found.
	 */
	public List<NpcShout> getNpcShouts(int worldId, int npcId)
	{
		Map<Integer, List<NpcShout>> worldShouts = shoutsByWorldNpcs.get(0);
		
		if ((worldShouts == null) || (worldShouts.get(npcId) == null))
		{
			worldShouts = shoutsByWorldNpcs.get(worldId);
			if ((worldShouts == null) || (worldShouts.get(npcId) == null))
			{
				return null;
			}
			
			return new ArrayList<>(worldShouts.get(npcId));
		}
		
		final List<NpcShout> npcShouts = new ArrayList<>(worldShouts.get(npcId));
		worldShouts = shoutsByWorldNpcs.get(worldId);
		if ((worldShouts == null) || (worldShouts.get(npcId) == null))
		{
			return npcShouts;
		}
		
		npcShouts.addAll(worldShouts.get(npcId));
		
		return npcShouts;
	}
	
	/**
	 * Checks if any shouts exist for a specific NPC in a given world.<br>
	 * This method returns {@code true} if the NPC has associated shouts.<br>
	 * It returns {@code false} if no shouts are found.
	 * @param worldId The unique identifier for the world.
	 * @param npcId The unique identifier for the NPC.
	 * @return {@code true} if shouts exist, otherwise {@code false}.
	 */
	public boolean hasAnyShout(int worldId, int npcId)
	{
		Map<Integer, List<NpcShout>> worldShouts = shoutsByWorldNpcs.get(0);
		
		if ((worldShouts == null) || (worldShouts.get(npcId) == null))
		{
			worldShouts = shoutsByWorldNpcs.get(worldId);
			if ((worldShouts == null) || (worldShouts.get(npcId) == null))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a specific NPC has any shout of a certain type.<br>
	 * This method looks up shouts for the given {@code worldId} and {@code npcId}.<br>
	 * It returns {@code true} if at least one matching {@link ShoutEventType} is found.
	 * @param worldId The unique identifier for the world.
	 * @param npcId The unique identifier for the NPC.
	 * @param type The specific type of shout to check for.
	 * @return {@code true} if a matching shout exists, otherwise {@code false}.
	 */
	public boolean hasAnyShout(int worldId, int npcId, ShoutEventType type)
	{
		final List<NpcShout> shouts = getNpcShouts(worldId, npcId);
		if (shouts == null)
		{
			return false;
		}
		
		for (NpcShout s : shouts)
		{
			if (s.getWhen() == type)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves a filtered list of shouts for a specific NPC.<br>
	 * This method checks the shout type, pattern, and skill number.<br>
	 * It returns {@code null} if no matching shouts are found.
	 * @param worldId The unique identifier for the world.
	 * @param npcId The unique identifier for the NPC.
	 * @param type The specific {@link ShoutEventType} to filter by.
	 * @param pattern The string pattern to match against the shout.
	 * @param skillNo The skill number required for the shout.
	 * @return A {@code List} of matching {@link NpcShout} objects, or {@code null}.
	 */
	public List<NpcShout> getNpcShouts(int worldId, int npcId, ShoutEventType type, String pattern, int skillNo)
	{
		final List<NpcShout> shouts = getNpcShouts(worldId, npcId);
		if (shouts == null)
		{
			return null;
		}
		
		final List<NpcShout> result = new ArrayList<>();
		for (NpcShout s : shouts)
		{
			if (s.getWhen() == type)
			{
				if (((pattern != null) && !pattern.equals(s.getPattern())) || ((skillNo != 0) && (skillNo != s.getSkillNo())))
				{
					continue;
				}
				
				result.add(s);
			}
		}
		
		shouts.clear();
		return result.size() > 0 ? result : null;
	}
}
