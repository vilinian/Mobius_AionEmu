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
package com.aionemu.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@code enum} defines the various inventory slots where items can be equipped.<br>
 * It serves as a reference for mapping specific items to their designated equipment positions.
 * @author Luno, xTz
 */
public enum ItemSlot
{
	MAIN_HAND(1L),
	SUB_HAND(1L << 1),
	HELMET(1L << 2),
	TORSO(1L << 3),
	GLOVES(1L << 4),
	BOOTS(1L << 5),
	EARRINGS_LEFT(1L << 6),
	EARRINGS_RIGHT(1L << 7),
	RING_LEFT(1L << 8),
	RING_RIGHT(1L << 9),
	NECKLACE(1L << 10),
	SHOULDER(1L << 11),
	PANTS(1L << 12),
	POWER_SHARD_RIGHT(1L << 13),
	POWER_SHARD_LEFT(1L << 14),
	WINGS(1L << 15),
	// non-NPC equips (slot > Short.MAX)
	WAIST(1L << 16),
	MAIN_OFF_HAND(1L << 17),
	SUB_OFF_HAND(1L << 18),
	PLUME(1L << 19),
	BRACELET(1L << 21),
	GLYPH(1L << 23),
	
	// combo
	MAIN_OR_SUB(MAIN_HAND.slotIdMask | SUB_HAND.slotIdMask, true), // 3
	MAIN_OFF_OR_SUB_OFF(MAIN_OFF_HAND.slotIdMask | SUB_OFF_HAND.slotIdMask, true),
	EARRING_RIGHT_OR_LEFT(EARRINGS_LEFT.slotIdMask | EARRINGS_RIGHT.slotIdMask, true), // 192
	RING_RIGHT_OR_LEFT(RING_LEFT.slotIdMask | RING_RIGHT.slotIdMask, true), // 768
	SHARD_RIGHT_OR_LEFT(POWER_SHARD_LEFT.slotIdMask | POWER_SHARD_RIGHT.slotIdMask, true), // 24576
	RIGHT_HAND(MAIN_HAND.slotIdMask | MAIN_OFF_HAND.slotIdMask, true),
	LEFT_HAND(SUB_HAND.slotIdMask | SUB_OFF_HAND.slotIdMask, true),
	// TORSO_GLOVE_FOOT_SHOULDER_LEG(0, true), // TODO
	
	// STIGMA slots
	STIGMA1(1L << 30), // 1073741824L Normal
	STIGMA2(1L << 31), // 2147483648L Normal
	STIGMA3(1L << 32), // 4294967296L Normal
	REGULAR_STIGMAS(STIGMA1.slotIdMask | STIGMA2.slotIdMask | STIGMA3.slotIdMask, true),
	
	ADV_STIGMA1(1L << 33), // 8589934592L Greater Stigma
	ADV_STIGMA2(1L << 34), // 17179869184L Greater Stigma
	ADVANCED_STIGMAS(ADV_STIGMA1.slotIdMask | ADV_STIGMA2.slotIdMask, true),
	
	MAJ_STIGMA(1L << 35), // 34359738368L Major Stigma
	MAJOR_STIGMAS(MAJ_STIGMA.slotIdMask, true),
	
	SPECIAL_STIGMA1(1L << 36), // 6.x New Stigma Slot 68719476736
	SPECIAL_STIGMA2(1L << 37), // 6.x New Stigma Slot 137438953472
	SPECIAL_STIGMA3(1L << 38), // 6.x New Stigma Slot 274877906944
	SPECIAL_STIGMAS(SPECIAL_STIGMA1.slotIdMask | SPECIAL_STIGMA2.slotIdMask | SPECIAL_STIGMA3.slotIdMask, true),
	
	ALL_STIGMA(REGULAR_STIGMAS.slotIdMask | ADVANCED_STIGMAS.slotIdMask | MAJOR_STIGMAS.slotIdMask | SPECIAL_STIGMAS.slotIdMask, true),
	
	VIEW_DISPLAY(MAIN_HAND.slotIdMask | SUB_HAND.slotIdMask | HELMET.slotIdMask | TORSO.slotIdMask | GLOVES.slotIdMask | BOOTS.slotIdMask | EARRINGS_LEFT.slotIdMask | EARRINGS_RIGHT.slotIdMask | RING_LEFT.slotIdMask | RING_RIGHT.slotIdMask | NECKLACE.slotIdMask | SHOULDER.slotIdMask | PANTS.slotIdMask | WINGS.slotIdMask | WAIST.slotIdMask | PLUME.slotIdMask, true); // 13 * 16 = 208...
	
	private final long slotIdMask;
	private final boolean combo;
	
	/**
	 * Creates a new {@link ItemSlot} instance using a bitmask.<br>
	 * This constructor sets the combo status to {@code false}.
	 * @param mask The bitmask value representing the specific equipment slot.
	 */
	private ItemSlot(long mask)
	{
		this(mask, false);
	}
	
	/**
	 * Creates a new {@link ItemSlot} with a specific bitmask and combo status.<br>
	 * This constructor is used to define both individual slots and combined slot groups.
	 * @param mask The bitmask value representing the slot identifier.
	 * @param combo A boolean indicating if this slot represents a combination of multiple positions.
	 */
	private ItemSlot(long mask, boolean combo)
	{
		slotIdMask = mask;
		this.combo = combo;
	}
	
	/**
	 * Retrieves the bitmask associated with this {@link ItemSlot}.<br>
	 * This value represents the unique identifier for the equipment slot.<br>
	 * It can also represent a combination of multiple slots if it is a combo.
	 * @return The {@code long} bitmask for the current slot.
	 */
	public long getSlotIdMask()
	{
		return slotIdMask;
	}
	
	/**
	 * Checks if this {@link ItemSlot} represents a combination of multiple slots.<br>
	 * This is used to identify group slots like {@code RIGHT_HAND}.
	 * @return {@code true} if the slot is a combo, {@code false} otherwise.
	 */
	public boolean isCombo()
	{
		return combo;
	}
	
	/**
	 * Checks if the given {@code long} value corresponds to a regular stigma slot.<br>
	 * It verifies if the {@code slot} matches any of the masks defined in {@code REGULAR_STIGMAS}.
	 * @param slot The bitmask representing the item slot to check.
	 * @return {@code true} if the slot is a regular stigma, otherwise {@code false}.
	 */
	public static boolean isRegularStigma(long slot)
	{
		return (REGULAR_STIGMAS.slotIdMask & slot) == slot;
	}
	
	/**
	 * Checks if the provided {@code long} value corresponds to an advanced stigma slot.<br>
	 * This method verifies if the {@code slot} matches any of the masks defined in {@code ADVANCED_STIGMAS}.
	 * @param slot The bitmask representing the item slot to check.
	 * @return {@code true} if the slot is an advanced stigma, otherwise {@code false}.
	 */
	public static boolean isAdvancedStigma(long slot)
	{
		return (ADVANCED_STIGMAS.slotIdMask & slot) == slot;
	}
	
	/**
	 * Checks if the given {@code long} value corresponds to a Major Stigma slot.<br>
	 * This method verifies if the {@code slot} bitmask matches any of the {@code MAJ_STIG} positions.
	 * @param slot The bitmask representing the item slot to check.
	 * @return {@code true} if the slot is a Major Stigma, otherwise {@code false}.
	 */
	public static boolean isMajorStigma(long slot)
	{
		return (MAJOR_STIGMAS.slotIdMask & slot) == slot;
	}
	
	/**
	 * Checks if the provided {@code long} value corresponds to a special stigma slot.<br>
	 * This method verifies if the {@code slot} matches any of the masks defined in {@code SPECIAL_STIGMAS}.
	 * @param slot The bitmask representing the item slot to check.
	 * @return {@code true} if the slot is a special stigma, otherwise {@code false}.
	 */
	public static boolean isSpecialStigma(long slot)
	{
		return (SPECIAL_STIGMAS.slotIdMask & slot) == slot;
	}
	
	/**
	 * Checks if the given {@code long} value represents a stigma slot.<br>
	 * This method validates if the {@code slot} matches any of the defined stigma masks.
	 * @param slot The bitmask value to check.
	 * @return {@code true} if the value is a stigma slot, {@code false} otherwise.
	 */
	public static boolean isStigma(long slot)
	{
		return (ALL_STIGMA.slotIdMask & slot) == slot;
	}
	
	/**
	 * Retrieves all {@link ItemSlot} values that match a specific bitmask.<br>
	 * This method filters out combo slots and returns an array of matching results.
	 * @param slot The bitmask value to check against the available slots.
	 * @return An array of {@link ItemSlot} objects that correspond to the provided mask.
	 */
	public static ItemSlot[] getSlotsFor(long slot)
	{
		final List<ItemSlot> slots = new ArrayList<>();
		for (ItemSlot itemSlot : values())
		{
			if ((slot != 0) && !itemSlot.isCombo() && ((slot & itemSlot.slotIdMask) == itemSlot.slotIdMask))
			{
				slots.add(itemSlot);
			}
		}
		
		return slots.toArray(new ItemSlot[slots.size()]);
	}
	
	/**
	 * Retrieves the {@link ItemSlot} associated with a specific ID.<br>
	 * This method looks up the first matching slot from an array of possibilities.<br>
	 * It throws an exception if the provided value is not a valid slot.
	 * @param slot The unique identifier for the equipment slot.
	 * @return The corresponding {@link ItemSlot} object.
	 */
	public static ItemSlot getSlotFor(long slot)
	{
		final ItemSlot[] slots = getSlotsFor(slot);
		if ((slots != null) && (slots.length > 0))
		{
			return slots[0];
		}
		
		throw new IllegalArgumentException("Invalid provided slotIdMask " + slot);
	}
	
	/**
	 * Checks if the given {@code long} value represents a display slot.<br>
	 * This method verifies if the {@code slot} matches the mask for {@code VIEW_DISPLAY}.
	 * @param slot The slot identifier to check.
	 * @return {@code true} if the slot is a display slot, otherwise {@code false}.
	 */
	public static boolean isDisplaySlot(long slot)
	{
		return (VIEW_DISPLAY.slotIdMask & slot) == slot;
	}
}
