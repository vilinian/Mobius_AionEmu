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
package com.aionemu.gameserver.model.templates.housing;

/**
 * Defines the different categories of houses available in the game.<br>
 * This enum is used to classify housing types within the {@code housing} system.
 * @author Rolandas
 */
public enum HouseType
{
	ESTATE(0, 3, "a"),
	MANSION(1, 2, "b"),
	HOUSE(2, 1, "c"),
	STUDIO(3, 0, "d"),
	PALACE(4, 4, "s");
	
	/**
	 * Creates a new instance of the {@link HouseType} enum.<br>
	 * This constructor initializes the internal properties for each house type.
	 * @param index The unique position of the house type in the list.
	 * @param id The numeric identifier used by the game engine.
	 * @param abbrev The short string code representing this house type.
	 */
	private HouseType(int index, int id, String abbrev)
	{
		this.abbrev = abbrev;
		limitTypeIndex = index;
		this.id = id;
	}
	
	private final String abbrev;
	private final int limitTypeIndex;
	private final int id;
	
	/**
	 * Retrieves the short code for this {@link HouseType}.<br>
	 * This is used to identify the house type in a compact format.
	 * @return The abbreviation as a {@code String}.
	 */
	public String getAbbreviation()
	{
		return abbrev;
	}
	
	/**
	 * Retrieves the index associated with the house limit type.<br>
	 * This value is used to identify specific housing categories.
	 * @return The {@code int} value of the limit type index.
	 */
	public int getLimitTypeIndex()
	{
		return limitTypeIndex;
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
	 * Returns the name of this {@code ChallengeType}.<br>
	 * This is useful for getting a human-readable string representation.
	 * @return The name of the enum constant as a {@code String}.
	 */
	public String value()
	{
		return name();
	}
	
	/**
	 * Converts a string into its corresponding {@link HouseType}.<br>
	 * This method uses the internal {@code valueOf} logic.
	 * @param value The string representation of the house type.
	 * @return The matching {@code HouseType} enum constant.
	 */
	public static HouseType fromValue(String value)
	{
		return valueOf(value);
	}
}
