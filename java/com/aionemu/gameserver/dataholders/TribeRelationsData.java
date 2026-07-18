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

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.templates.tribe.Tribe;

import gnu.trove.map.hash.THashMap;

/**
 * This class holds the data for relations between different tribes.<br>
 * It maps {@link com.aionemu.gameserver.model.templates.tribe.Tribe} objects to their respective {@link com.aionemu.gameserver.model.TribeClass} types.
 * @author ATracer
 * @author GiGatR00n v4.7.5.x
 */
@XmlRootElement(name = "tribe_relations")
@XmlAccessorType(XmlAccessType.FIELD)
public class TribeRelationsData
{
	@XmlElement(name = "tribe", required = true)
	protected List<Tribe> tribeList;
	protected THashMap<TribeClass, Tribe> tribeNameMap = new THashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code tribeNameMap} using the list of {@link Tribe} objects.<br>
	 * The {@code tribeList} is set to {@code null} after the map is built.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (Tribe tribe : tribeList)
		{
			tribeNameMap.put(tribe.getName(), tribe);
		}
		
		tribeList = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return tribeNameMap.size();
	}
	
	/**
	 * Retrieves the base tribe for a specific tribe class.<br>
	 * This method looks up the {@code TribeClass} in the internal map.<br>
	 * It returns the underlying base tribe associated with that name.
	 * @param tribeName The {@code TribeClass} to look up.
	 * @return The base {@code TribeClass} of the provided tribe.
	 */
	public TribeClass getBaseTribe(TribeClass tribeName)
	{
		final Tribe tribe = tribeNameMap.get(tribeName);
		return tribe.getBase();
	}
	
	/**
	 * Checks if a specific tribe has aggressive relations.<br>
	 * This method looks at the tribe and its base tribe for any aggressive entries.
	 * @param tribeName The {@code TribeClass} to check.
	 * @return {@code true} if the tribe or its base is aggressive, otherwise {@code false}.
	 */
	public boolean hasAggressiveRelations(TribeClass tribeName)
	{
		final Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
		{
			return false;
		}
		
		final Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getAggro().isEmpty() || ((baseTribe != null) && !baseTribe.getAggro().isEmpty());
	}
	
	/**
	 * Checks if a specific tribe has hostile relations.<br>
	 * This method looks up the {@code TribeClass} in the internal map.<br>
	 * It returns {@code true} if the tribe or its base tribe is marked as hostile.
	 * @param tribeName The {@link TribeClass} to check for hostility.
	 * @return {@code true} if relations are hostile, otherwise {@code false}.
	 */
	public boolean hasHostileRelations(TribeClass tribeName)
	{
		final Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
		{
			return false;
		}
		
		final Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getHostile().isEmpty() || ((baseTribe != null) && !baseTribe.getHostile().isEmpty());
	}
	
	/**
	 * Checks if a specific tribe has any support relations.<br>
	 * This method looks at both the tribe and its base tribe.<br>
	 * It returns {@code true} if either tribe has a non-empty support list.
	 * @param tribeName The {@link TribeClass} to check for support relations.
	 * @return {@code true} if support relations exist, otherwise {@code false}.
	 */
	public boolean hasSupportRelations(TribeClass tribeName)
	{
		final Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
		{
			return false;
		}
		
		final Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getSupport().isEmpty() || ((baseTribe != null) && !baseTribe.getSupport().isEmpty());
	}
	
	/**
	 * Checks if a specific tribe has any friend relations.<br>
	 * This method looks at both the tribe and its base tribe.<br>
	 * It returns {@code true} if either has friends in their list.
	 * @param tribeName The {@link TribeClass} to check.
	 * @return {@code true} if friend relations exist, otherwise {@code false}.
	 */
	public boolean hasFriendRelations(TribeClass tribeName)
	{
		final Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
		{
			return false;
		}
		
		final Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getFriend().isEmpty() || ((baseTribe != null) && !baseTribe.getFriend().isEmpty());
	}
	
	/**
	 * Checks if a specific tribe has any "none" relations defined.<br>
	 * This method looks at both the tribe and its base tribe.
	 * @param tribeName The {@link TribeClass} to check.
	 * @return {@code true} if any none relations exist, otherwise {@code false}.
	 */
	public boolean hasNoneRelations(TribeClass tribeName)
	{
		final Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
		{
			return false;
		}
		
		final Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getNone().isEmpty() || ((baseTribe != null) && !baseTribe.getNone().isEmpty());
	}
	
	/**
	 * Checks if a specific tribe has any neutral relations.<br>
	 * This method looks at both the tribe and its base tribe.<br>
	 * It returns {@code true} if either tribe contains neutral entries.
	 * @param tribeName The {@link TribeClass} to check.
	 * @return {@code true} if neutral relations exist, otherwise {@code false}.
	 */
	public boolean hasNeutralRelations(TribeClass tribeName)
	{
		final Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
		{
			return false;
		}
		
		final Tribe baseTribe = tribe.isBasic() ? tribeNameMap.get(tribe.getBase()) : null;
		return !tribe.getNeutral().isEmpty() || ((baseTribe != null) && !baseTribe.getNeutral().isEmpty());
	}
	
	/**
	 * Checks if two tribes have an aggressive relationship with each other.<br>
	 * This method verifies if either tribe is listed in the other's aggression list.<br>
	 * It also ensures that neither tribe is considered neutral by the other.
	 * @param tribeName1 The first {@link TribeClass} to check.
	 * @param tribeName2 The second {@link TribeClass} to check.
	 * @return {@code true} if the tribes are aggressive toward each other, otherwise {@code false}.
	 */
	public boolean isAggressiveRelation(TribeClass tribeName1, TribeClass tribeName2)
	{
		final Tribe tribe1 = tribeNameMap.get(tribeName1);
		final Tribe tribe2 = tribeNameMap.get(tribeName2);
		if ((tribe1 == null) || (tribe2 == null))
		{
			return false;
		}
		
		return (tribe1.getAggro().contains(tribe2.getBase()) || tribe1.getAggro().contains(tribeName2) || tribe2.getAggro().contains(tribe1.getBase()) || tribe2.getAggro().contains(tribeName1)) && (!tribe1.getNeutral().contains(tribeName2) && !tribe2.getNeutral().contains(tribeName1));
	}
	
	/**
	 * Checks if two tribes have a support relationship with each other.<br>
	 * This method returns {@code true} if either tribe supports the other.<br>
	 * It also checks for support between base tribes.
	 * @param tribeName1 The first {@link TribeClass} to check.
	 * @param tribeName2 The second {@link TribeClass} to check.
	 * @return {@code true} if a support relation exists, otherwise {@code false}.
	 */
	public boolean isSupportRelation(TribeClass tribeName1, TribeClass tribeName2)
	{
		final Tribe tribe1 = tribeNameMap.get(tribeName1);
		final Tribe tribe2 = tribeNameMap.get(tribeName2);
		if ((tribe1 == null) || (tribe2 == null))
		{
			return false;
		}
		
		return tribe1.getSupport().contains(tribe2.getBase()) || tribe1.getSupport().contains(tribeName2) || tribe2.getSupport().contains(tribe1.getBase()) || tribe2.getSupport().contains(tribeName1);
	}
	
	/**
	 * Checks if two tribes have a friendly relationship.<br>
	 * This method returns {@code true} if either tribe is listed as a friend of the other.<br>
	 * It returns {@code false} if one or both tribes are {@code null}.
	 * @param tribeName1 The first tribe to check.
	 * @param tribeName2 The second tribe to check.
	 * @return {@code true} if the tribes are friendly, otherwise {@code false}.
	 */
	public boolean isFriendlyRelation(TribeClass tribeName1, TribeClass tribeName2)
	{
		final Tribe tribe1 = tribeNameMap.get(tribeName1);
		final Tribe tribe2 = tribeNameMap.get(tribeName2);
		if ((tribe1 == null) || (tribe2 == null))
		{
			return false;
		}
		
		return tribe1.getFriend().contains(tribe2.getBase()) || tribe1.getFriend().contains(tribeName2) || tribe2.getFriend().contains(tribe1.getBase()) || tribe2.getFriend().contains(tribeName1);
	}
	
	/**
	 * Checks if two tribes have a neutral relationship.<br>
	 * This method compares the {@code TribeClass} types against their defined neutral lists.<br>
	 * It returns {@code false} if either tribe is not found in the data map.
	 * @param tribeName1 The first tribe to check.
	 * @param tribeName2 The second tribe to check.
	 * @return {@code true} if a neutral relationship exists, otherwise {@code false}.
	 */
	public boolean isNeutralRelation(TribeClass tribeName1, TribeClass tribeName2)
	{
		final Tribe tribe1 = tribeNameMap.get(tribeName1);
		final Tribe tribe2 = tribeNameMap.get(tribeName2);
		if ((tribe1 == null) || (tribe2 == null))
		{
			return false;
		}
		
		return tribe1.getNeutral().contains(tribe2.getBase()) || tribe1.getNeutral().contains(tribeName2) || tribe2.getNeutral().contains(tribe1.getBase()) || tribe2.getNeutral().contains(tribeName1);
	}
	
	/**
	 * Checks if there is no specific relation between two tribes.<br>
	 * This method returns {@code true} if either tribe lists the other as a none relation.<br>
	 * It returns {@code false} if one or both of the tribes are {@code null}.
	 * @param tribeName1 The first tribe to check.
	 * @param tribeName2 The second tribe to check.
	 * @return {@code true} if no relation exists, otherwise {@code false}.
	 */
	public boolean isNoneRelation(TribeClass tribeName1, TribeClass tribeName2)
	{
		final Tribe tribe1 = tribeNameMap.get(tribeName1);
		final Tribe tribe2 = tribeNameMap.get(tribeName2);
		if ((tribe1 == null) || (tribe2 == null))
		{
			return false;
		}
		
		return tribe1.getNone().contains(tribe2.getBase()) || tribe1.getNone().contains(tribeName2) || tribe2.getNone().contains(tribe1.getBase()) || tribe2.getNone().contains(tribeName1);
	}
	
	/**
	 * Checks if two tribes have a hostile relationship with each other.<br>
	 * This method compares the hostiles of both {@code TribeClass} entries.<br>
	 * It returns {@code true} if either tribe is listed as an enemy of the other.
	 * @param tribeName1 The first tribe to check.
	 * @param tribeName2 The second tribe to check.
	 * @return {@code true} if the tribes are hostile, otherwise {@code false}.
	 */
	public boolean isHostileRelation(TribeClass tribeName1, TribeClass tribeName2)
	{
		final Tribe tribe1 = tribeNameMap.get(tribeName1);
		final Tribe tribe2 = tribeNameMap.get(tribeName2);
		if ((tribe1 == null) || (tribe2 == null))
		{
			return false;
		}
		
		return tribe1.getHostile().contains(tribe2.getBase()) || tribe1.getHostile().contains(tribeName2) || tribe2.getHostile().contains(tribe1.getBase()) || tribe2.getHostile().contains(tribeName1);
	}
	
	/**
	 * Checks if any other tribe supports the specified tribe.<br>
	 * It searches through all tribes in the {@code tribeNameMap}.
	 * @param tribeName The {@link TribeClass} to check for supporters.
	 * @return {@code true} if at least one supporter is found, otherwise {@code false}.
	 */
	public boolean hasAnySupporter(TribeClass tribeName)
	{
		final Tribe tribe1 = tribeNameMap.get(tribeName);
		if (tribe1 == null)
		{
			return false;
		}
		
		for (TribeClass tribe2 : tribeNameMap.keySet())
		{
			if (isSupportRelation(tribe2, tribeName))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the {@link Tribe} data for a specific tribe.<br>
	 * This method looks up the information using the provided {@code tribeName}.
	 * @param tribeName The {@code TribeClass} to search for.
	 * @return The corresponding {@code Tribe} object or {@code null} if not found.
	 */
	public Tribe getTribeData(TribeClass tribeName)
	{
		return tribeNameMap.get(tribeName);
	}
}
