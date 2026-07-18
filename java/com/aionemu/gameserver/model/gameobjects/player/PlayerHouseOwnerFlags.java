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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;

/**
 * Defines the various permission flags for a player who owns a house.<br>
 * These flags determine what actions can be performed on a specific property.
 * @author Rolandas
 */
public enum PlayerHouseOwnerFlags
{
	IS_OWNER(1 << 0),
	HAS_OWNER(1 << 0),
	BUY_STUDIO_ALLOWED(1 << 1),
	SINGLE_HOUSE(1 << 1),
	BIDDING_ALLOWED(1 << 2),
	HOUSE_OWNER((IS_OWNER.getId() | BIDDING_ALLOWED.getId()) & ~BUY_STUDIO_ALLOWED.getId()),
	SELLING_HOUSE(IS_OWNER.getId() | BUY_STUDIO_ALLOWED.getId()),
	
	// Player status
	SOLD_HOUSE(BIDDING_ALLOWED.getId() | BUY_STUDIO_ALLOWED.getId());
	
	private final byte id;
	
	/**
	 * Creates a new instance of {@link PlayerHouseOwnerFlags}.<br>
	 * This constructor initializes the internal ID from an integer value.<br>
	 * It masks the input to ensure it fits within a {@code byte}.
	 * @param id The raw integer value used to define the house owner flag.
	 */
	private PlayerHouseOwnerFlags(int id)
	{
		this.id = (byte) (id & 0xFF);
	}
	
	/**
	 * Retrieves the unique identifier for this {@link CollisionIntention}.<br>
	 * This value is used to represent the collision type as a byte.
	 * @return The {@code byte} ID of the current enum constant.
	 */
	public byte getId()
	{
		return id;
	}
}
