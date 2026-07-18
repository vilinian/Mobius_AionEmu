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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different categories of weapons available in the game.<br>
 * This enum is used to classify items within the item system.
 * @author ATracer
 */
@XmlType(name = "weapon_type")
@XmlEnum
public enum WeaponType
{
	DAGGER_1H(new int[]
	{
		66,
		45
	}, 1),
	MACE_1H(new int[]
	{
		39,
		46
	}, 1),
	SWORD_1H(new int[]
	{
		37,
		44
	}, 1),
	TOOLHOE_1H(new int[] {}, 1),
	GUN_1H(new int[]
	{
		112,
		117
	}, 1),
	BOOK_2H(new int[]
	{
		100,
		107
	}, 2),
	ORB_2H(new int[]
	{
		111
	}, 2), // 65 right skill, why 64 ? u_u
	POLEARM_2H(new int[]
	{
		52
	}, 2),
	STAFF_2H(new int[]
	{
		89
	}, 2),
	SWORD_2H(new int[]
	{
		51
	}, 2),
	TOOLPICK_2H(new int[] {}, 2),
	TOOLROD_2H(new int[] {}, 2),
	BOW(new int[]
	{
		53
	}, 2),
	CANNON_2H(new int[]
	{
		113
	}, 2),
	HARP_2H(new int[]
	{
		114,
		124
	}, 2),
	GUN_2H(new int[]
	{
		113
	}, 2),
	SPRAY_2H(new int[]
	{
		473
	}, 2),
	KEYBLADE_2H(new int[]
	{
		115
	}, 2),
	KEYHAMMER_2H(new int[] {}, 2);
	
	private final int[] requiredSkill;
	private final int slots;
	
	/**
	 * Creates a new instance of {@link WeaponType}.<br>
	 * This constructor initializes the skill requirements and slot count.
	 * @param requiredSkills An array of {@code int} values representing the skills needed.
	 * @param slots The number of equipment slots used by this weapon type.
	 */
	private WeaponType(int[] requiredSkills, int slots)
	{
		requiredSkill = requiredSkills;
		this.slots = slots;
	}
	
	/**
	 * Retrieves the list of skills needed for this weapon type.<br>
	 * This method returns the internal {@code requiredSkills} array.
	 * @return an {@code int[]} containing the skill requirements.
	 */
	public int[] getRequiredSkills()
	{
		return requiredSkill;
	}
	
	/**
	 * Returns the number of inventory slots required for this weapon type.<br>
	 * This value is used to determine how much space an item occupies.
	 * @return The total number of {@code int} slots needed.
	 */
	public int getRequiredSlots()
	{
		return slots;
	}
	
	/**
	 * Retrieves the bitmask associated with this {@code ArmorType}.<br>
	 * This value is used for bitwise AND operations.<br>
	 * It helps identify types when multiple options are allowed.
	 * @return The integer mask for this type.
	 */
	public int getMask()
	{
		return 1 << ordinal();
	}
}
