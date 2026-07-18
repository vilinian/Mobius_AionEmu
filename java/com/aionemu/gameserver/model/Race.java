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
package com.aionemu.gameserver.model;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Represents the different character and NPC races available in the game.<br>
 * This {@code enum} provides a centralized location for all race types.
 * @author SoulKeeper
 */
@XmlEnum
public enum Race
{
	/**
	 * Playable races
	 */
	ELYOS(0, new DescriptionId(480480)),
	ASMODIANS(1, new DescriptionId(480481)),
	/**
	 * Npc races
	 */
	LYCAN(2),
	CONSTRUCT(3),
	CARRIER(4),
	DRAKAN(5),
	LIZARDMAN(6),
	TELEPORTER(7),
	NAGA(8),
	BROWNIE(9),
	KRALL(10),
	SHULACK(11),
	BARRIER(12),
	PC_LIGHT_CASTLE_DOOR(13),
	PC_DARK_CASTLE_DOOR(14),
	DRAGON_CASTLE_DOOR(15),
	GCHIEF_LIGHT(16),
	GCHIEF_DARK(17),
	DRAGON(18),
	OUTSIDER(19),
	RATMAN(20),
	DEMIHUMANOID(21),
	UNDEAD(22),
	BEAST(23),
	MAGICALMONSTER(24),
	ELEMENTAL(25),
	LIVINGWATER(28),
	/**
	 * Special races
	 */
	NONE(26),
	PC_ALL(27),
	DEFORM(28),
	// 2.6
	NEUT(29),
	// 2.7 -- NOT SURE !!!
	GHENCHMAN_LIGHT(30),
	GHENCHMAN_DARK(31),
	// 3.0
	EVENT_TOWER_DARK(32),
	EVENT_TOWER_LIGHT(33),
	GOBLIN(34),
	TRICODARK(35),
	NPC(36),
	// 3.5
	LIGHT(37),
	DARK(38),
	WORLD_EVENT_DEFTOWER(39),
	// 4.3
	ORC(40),
	DRAGONET(41),
	SIEGEDRAKAN(42),
	GCHIEF_DRAGON(43),
	WORLD_EVENT_BONFIRE(44),
	BATTLEGROUND_LI(45),
	BATTLEGROUND_DA(46),
	TYPE_A(47),
	TYPE_B(48),
	TYPE_C(49),
	TYPE_D(50),
	GINSENGS(51),
	EVENT_YEAR(52);
	
	private final int raceId;
	private final DescriptionId descriptionId;
	
	/**
	 * Creates a new {@link Race} instance using only an ID.<br>
	 * This constructor sets the {@code descriptionId} to {@code null}.
	 * @param raceId The unique identifier for the race.
	 */
	private Race(int raceId)
	{
		this(raceId, null);
	}
	
	/**
	 * Creates a new {@link Race} instance with a specific ID and description.<br>
	 * This constructor is used to initialize the internal fields of the enum.
	 * @param raceId The unique integer identifier for the race.
	 * @param descriptionId The {@code DescriptionId} associated with this race.
	 */
	private Race(int raceId, DescriptionId descriptionId)
	{
		this.raceId = raceId;
		this.descriptionId = descriptionId;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link Race}.<br>
	 * This value is used to identify different types of races in the game.
	 * @return The integer ID associated with the race.
	 */
	public int getRaceId()
	{
		return raceId;
	}
	
	/**
	 * Checks if the current {@code Race} is a playable player race.<br>
	 * This method returns {@code true} for specific IDs like {@code ELYOS}, {@code ASMODIANS}, or {@code PC_ALL}.<br>
	 * It returns {@code false} for NPC-only races.
	 * @return {@code true} if the race is playable by a player, otherwise {@code false}.
	 */
	public boolean isPlayerRace()
	{
		return (raceId < 2) || (raceId == 27);
	}
	
	/**
	 * Retrieves the unique identifier for the race description.<br>
	 * This method returns a {@link DescriptionId} object.<br>
	 * It throws a {@code UnsupportedOperationException} if the ID is {@code null}.
	 * @return the {@code DescriptionId} associated with this race.
	 */
	public DescriptionId getRaceDescriptionId()
	{
		if (descriptionId == null)
		{
			throw new UnsupportedOperationException("Race name DescriptionId is unknown for race" + this);
		}
		
		return descriptionId;
	}
	
	/**
	 * Finds a {@link Race} based on its string name.<br>
	 * This method iterates through all available races to find a match for the provided {@code fieldName}.
	 * @param fieldName The string name of the race to look for.
	 * @return The matching {@link Race} object, or {@code null} if no match is found.
	 */
	public static Race getRaceByString(String fieldName)
	{
		for (Race r : values())
		{
			if (r.toString().equals(fieldName))
			{
				return r;
			}
		}
		
		return null;
	}
}
