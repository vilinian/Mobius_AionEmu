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
package com.aionemu.gameserver.services.rift;

import com.aionemu.gameserver.model.Race;

/**
 * This class defines the various types of rifts available in the game.<br>
 * It serves as a central registry for rift-related constants used by the {@code Rift} system.
 * @author Source
 */
public enum RiftEnum
{
	KAISINEL_AM(1170, "KAISINEL_AM", "KAISINEL_AS", 24, 45, 65, Race.ASMODIANS, true),
	HEIRON_AM(2140, "HEIRON_AM", "BELUSLAN_AS", 24, 20, 65, Race.ASMODIANS),
	HEIRON_BM(2141, "HEIRON_BM", "BELUSLAN_BS", 36, 20, 65, Race.ASMODIANS),
	HEIRON_CM(2142, "HEIRON_CM", "BELUSLAN_CS", 48, 20, 65, Race.ASMODIANS),
	HEIRON_DM(2143, "HEIRON_DM", "BELUSLAN_DS", 48, 20, 65, Race.ASMODIANS),
	HEIRON_EM(2144, "HEIRON_EM", "BELUSLAN_ES", 60, 20, 65, Race.ASMODIANS),
	HEIRON_FM(2145, "HEIRON_FM", "BELUSLAN_FS", 72, 20, 65, Race.ASMODIANS),
	HEIRON_GM(2146, "HEIRON_GM", "BELUSLAN_GS", 72, 20, 65, Race.ASMODIANS),
	INGGISON_AM(2150, "INGGISON_AM", "GELKMAROS_AS", 150, 20, 65, Race.ASMODIANS),
	INGGISON_BM(2151, "INGGISON_BM", "GELKMAROS_BS", 150, 20, 65, Race.ASMODIANS),
	INGGISON_CM(2152, "INGGISON_CM", "GELKMAROS_CS", 150, 20, 65, Race.ASMODIANS),
	INGGISON_DM(2153, "INGGISON_DM", "GELKMAROS_DS", 150, 20, 65, Race.ASMODIANS),
	CYGNEA_AM(2170, "CYGNEA_AM", "ENSHAR_AS", 12, 50, 65, Race.ASMODIANS),
	CYGNEA_BM(2171, "CYGNEA_BM", "ENSHAR_BS", 18, 50, 65, Race.ASMODIANS),
	CYGNEA_CM(2172, "CYGNEA_CM", "ENSHAR_CS", 24, 55, 65, Race.ASMODIANS),
	CYGNEA_DM(2173, "CYGNEA_DM", "ENSHAR_DS", 30, 55, 65, Race.ASMODIANS),
	CYGNEA_EM(2174, "CYGNEA_EM", "ENSHAR_ES", 36, 55, 65, Race.ASMODIANS),
	CYGNEA_FM(2175, "CYGNEA_FM", "ENSHAR_FS", 48, 55, 65, Race.ASMODIANS),
	CYGNEA_GM(2176, "CYGNEA_GM", "ENSHAR_GS", 144, 60, 65, Race.ASMODIANS),
	CYGNEA_HM(2177, "CYGNEA_HM", "ENSHAR_HS", 144, 60, 65, Race.ASMODIANS),
	CYGNEA_IM(2178, "CYGNEA_IM", "ENSHAR_IS", 144, 60, 65, Race.ASMODIANS),
	MARCHUTAN_AM(1280, "MARCHUTAN_AM", "MARCHUTAN_AS", 24, 45, 65, Race.ELYOS, true),
	BELUSLAN_AM(2240, "BELUSLAN_AM", "HEIRON_AS", 24, 20, 65, Race.ELYOS),
	BELUSLAN_BM(2241, "BELUSLAN_BM", "HEIRON_BS", 36, 20, 65, Race.ELYOS),
	BELUSLAN_CM(2242, "BELUSLAN_CM", "HEIRON_CS", 48, 20, 65, Race.ELYOS),
	BELUSLAN_DM(2243, "BELUSLAN_DM", "HEIRON_DS", 48, 20, 65, Race.ELYOS),
	BELUSLAN_EM(2244, "BELUSLAN_EM", "HEIRON_ES", 60, 20, 65, Race.ELYOS),
	BELUSLAN_FM(2245, "BELUSLAN_FM", "HEIRON_FS", 72, 20, 65, Race.ELYOS),
	BELUSLAN_GM(2246, "BELUSLAN_GM", "HEIRON_GS", 72, 20, 65, Race.ELYOS),
	GELKMAROS_AM(2270, "GELKMAROS_AM", "INGGISON_AS", 150, 20, 65, Race.ELYOS),
	GELKMAROS_BM(2271, "GELKMAROS_BM", "INGGISON_BS", 150, 20, 65, Race.ELYOS),
	GELKMAROS_CM(2272, "GELKMAROS_CM", "INGGISON_CS", 150, 20, 65, Race.ELYOS),
	GELKMAROS_DM(2273, "GELKMAROS_DM", "INGGISON_DS", 150, 20, 65, Race.ELYOS),
	ENSHAR_AM(2280, "ENSHAR_AM", "CYGNEA_AS", 12, 50, 65, Race.ELYOS),
	ENSHAR_BM(2281, "ENSHAR_BM", "CYGNEA_BS", 18, 50, 65, Race.ELYOS),
	ENSHAR_CM(2282, "ENSHAR_CM", "CYGNEA_CS", 24, 55, 65, Race.ELYOS),
	ENSHAR_DM(2283, "ENSHAR_DM", "CYGNEA_DS", 30, 55, 65, Race.ELYOS),
	ENSHAR_EM(2284, "ENSHAR_EM", "CYGNEA_ES", 36, 55, 65, Race.ELYOS),
	ENSHAR_FM(2285, "ENSHAR_FM", "CYGNEA_FS", 48, 55, 65, Race.ELYOS),
	ENSHAR_GM(2286, "ENSHAR_GM", "CYGNEA_GS", 144, 60, 65, Race.ELYOS),
	ENSHAR_HM(2287, "ENSHAR_HM", "CYGNEA_HS", 144, 60, 65, Race.ELYOS),
	ENSHAR_IM(2288, "ENSHAR_IM", "CYGNEA_IS", 144, 60, 65, Race.ELYOS);
	
	private final int id;
	private final String master;
	private final String slave;
	private final int entries;
	private final int minLevel;
	private final int maxLevel;
	private final Race destination;
	private final boolean vortex;
	
	/**
	 * Creates a new {@code RiftEnum} instance without the vortex property.<br>
	 * This constructor sets the vortex status to {@code false}.
	 * @param id The unique identifier for the rift.
	 * @param master The name of the master rift.
	 * @param slave The name of the slave rift.
	 * @param entries The number of allowed entries.
	 * @param minLevel The minimum level required to enter.
	 * @param maxLevel The maximum level allowed in the rift.
	 * @param destination The {@link Race} of the destination area.
	 */
	RiftEnum(int id, String master, String slave, int entries, int minLevel, int maxLevel, Race destination)
	{
		this(id, master, slave, entries, minLevel, maxLevel, destination, false);
	}
	
	/**
	 * Constructs a new {@code RiftEnum} instance with specific rift properties.<br>
	 * This constructor defines the configuration for a game rift.
	 * @param id The unique identifier for the rift.
	 * @param master The name of the master rift.
	 * @param slave The name of the slave rift.
	 * @param entries The maximum number of allowed entries.
	 * @param minLevel The minimum level required to enter.
	 * @param maxLevel The maximum level allowed in the rift.
	 * @param destination The {@link Race} of the destination area.
	 * @param vortex Whether this rift is considered a vortex.
	 */
	RiftEnum(int id, String master, String slave, int entries, int minLevel, int maxLevel, Race destination, boolean vortex)
	{
		this.id = id;
		this.master = master;
		this.slave = slave;
		this.entries = entries;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.destination = destination;
		this.vortex = vortex;
	}
	
	/**
	 * Retrieves a {@link RiftEnum} instance based on its unique identifier.<br>
	 * This method searches through all available rifts to find a match.<br>
	 * It throws an exception if the provided ID is not found.
	 * @param id The unique integer ID of the rift to retrieve.
	 * @return The corresponding {@link RiftEnum} object.
	 * @throws IllegalArgumentException If no rift exists with the given {@code id}.
	 */
	public static RiftEnum getRift(int id) throws IllegalArgumentException
	{
		for (RiftEnum rift : RiftEnum.values())
		{
			if (rift.getId() == id)
			{
				return rift;
			}
		}
		
		throw new IllegalArgumentException("Unsupported rift id: " + id);
	}
	
	/**
	 * Retrieves the {@link RiftEnum} associated with a specific race that is marked as a vortex.<br>
	 * This method searches through all available rifts to find a match.
	 * @param race The {@code Race} type to filter by.
	 * @return The matching {@code RiftEnum} object.
	 * @throws IllegalArgumentException If no vortex is found for the provided {@code Race}.
	 */
	public static RiftEnum getVortex(Race race) throws IllegalArgumentException
	{
		for (RiftEnum rift : RiftEnum.values())
		{
			if (rift.isVortex() && rift.getDestination().equals(race))
			{
				return rift;
			}
		}
		
		throw new IllegalArgumentException("Unsupported vortex race: " + race);
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the master name of the rift.<br>
	 * This value corresponds to the {@code master} field defined in the constructor.
	 * @return The {@code String} representing the master name.
	 */
	public String getMaster()
	{
		return master;
	}
	
	/**
	 * Retrieves the name of the slave rift.<br>
	 * This value corresponds to the third parameter in the {@link RiftEnum} constructor.
	 * @return The {@code String} representation of the slave rift.
	 */
	public String getSlave()
	{
		return slave;
	}
	
	/**
	 * Retrieves the number of entries for this rift.<br>
	 * This value is defined during the initialization of the {@link RiftEnum}.
	 * @return The total count of entries.
	 */
	public int getEntries()
	{
		return entries;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return minLevel;
	}
	
	/**
	 * Gets the maximum level allowed for this challenge task.<br>
	 * This value is retrieved from the {@code maxLevel} field.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLevel()
	{
		return maxLevel;
	}
	
	/**
	 * Retrieves the target race for this rift.<br>
	 * This method returns the {@code Race} associated with the rift configuration.
	 * @return The {@link Race} of the destination.
	 */
	public Race getDestination()
	{
		return destination;
	}
	
	/**
	 * Checks if this rift is a vortex.
	 * @return {@code true} if it is a vortex, {@code false} otherwise.
	 */
	public boolean isVortex()
	{
		return vortex;
	}
}
