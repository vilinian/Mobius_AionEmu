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
package com.aionemu.gameserver.world.zone;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the unique name of a game zone.<br>
 * It provides helper methods to manage and retrieve zone identifiers within the {@code zone} package.
 * @author Rolandas
 */
public final class ZoneName
{
	private final static Logger log = LoggerFactory.getLogger(ZoneName.class);
	private static final Map<String, ZoneName> zoneNames = new ConcurrentHashMap<>();
	public static final String NONE = "NONE";
	public static final String ABYSS_CASTLE = "_ABYSS_CASTLE_AREA_";
	
	static
	{
		zoneNames.put(NONE, new ZoneName(NONE));
		zoneNames.put(ABYSS_CASTLE, new ZoneName(ABYSS_CASTLE));
	}
	
	private final String _name;
	
	/**
	 * Creates a new instance of {@link ZoneName}.<br>
	 * This constructor initializes the internal name field.
	 * @param name The string representation of the zone name.
	 */
	private ZoneName(String name)
	{
		_name = name;
	}
	
	/**
	 * Retrieves the name of the zone.<br>
	 * This method returns the {@code _name} field associated with this object.
	 * @return The name as a {@code String}.
	 */
	public String name()
	{
		return _name;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link ZoneName}.<br>
	 * This value is generated based on the internal name of the zone.
	 * @return The integer ID of the zone.
	 */
	public int id()
	{
		return _name.hashCode();
	}
	
	/**
	 * Retrieves an existing {@link ZoneName} or creates a new one.<br>
	 * The input string is converted to uppercase before lookup.<br>
	 * This method ensures that each unique name maps to the same instance.
	 * @param name The name of the zone to find or create.
	 * @return The corresponding {@code ZoneName} object.
	 */
	public static ZoneName createOrGet(String name)
	{
		name = name.toUpperCase();
		if (zoneNames.containsKey(name))
		{
			return zoneNames.get(name);
		}
		
		final ZoneName newZone = new ZoneName(name);
		zoneNames.put(name, newZone);
		return newZone;
	}
	
	/**
	 * Retrieves the unique identifier for a given zone name.<br>
	 * The input string is converted to uppercase before searching.<br>
	 * If the name is not found, it returns the ID for {@code NONE}.
	 * @param name The name of the zone to look up.
	 * @return The integer ID associated with the provided name.
	 */
	public static int getId(String name)
	{
		name = name.toUpperCase();
		if (zoneNames.containsKey(name))
		{
			return zoneNames.get(name).id();
		}
		
		return zoneNames.get(NONE).id();
	}
	
	/**
	 * Retrieves a {@link ZoneName} based on the provided string.<br>
	 * The input is converted to uppercase before searching.<br>
	 * If the name is not found, it returns the {@code NONE} zone.
	 * @param name The name of the zone to look up.
	 * @return The corresponding {@link ZoneName} object.
	 */
	public static ZoneName get(String name)
	{
		name = name.toUpperCase();
		if (zoneNames.containsKey(name))
		{
			return zoneNames.get(name);
		}
		
		log.warn("Missing zone : " + name);
		return zoneNames.get(NONE);
	}
	
	/**
	 * Returns the name of this zone.<br>
	 * This provides a human-readable string representation.
	 * @return The {@code String} value of the zone name.
	 */
	@Override
	public String toString()
	{
		return _name;
	}
}
