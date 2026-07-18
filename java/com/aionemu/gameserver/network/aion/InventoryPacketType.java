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
package com.aionemu.gameserver.network.aion;

/**
 * Defines the various types of network packets related to inventory management.<br>
 * This enum is used by {@code PacketHandler} to identify specific inventory actions.
 * @author ATracer
 */
public enum InventoryPacketType
{
	WAREHOUSE(false, false, false),
	INVENTORY(true, false, false),
	MAIL_REPURCHASE(false, true, false),
	PRIVATE_STORE(false, false, true),
	WEAPON_SWITCH(true, false, false, true);
	
	private final boolean isInventory;
	private final boolean isMailOrRepurchase;
	private final boolean isPrivateStore;
	private final boolean isWeaponSwitch;
	
	/**
	 * This constructor creates an {@link InventoryPacketType} with a specific set of flags.<br>
	 * It sets the {@code isWeaponSwitch} flag to {@code false}.
	 * @param isInventory Sets whether this packet relates to the player inventory.
	 * @param isMail Sets whether this packet relates to the mail system.
	 * @param isPrivateStore Sets whether this packet relates to a private store.
	 */
	private InventoryPacketType(boolean isInventory, boolean isMail, boolean isPrivateStore)
	{
		this(isInventory, isMail, isPrivateStore, false);
	}
	
	/**
	 * Constructs a new {@link InventoryPacketType} with specific flags.<br>
	 * This constructor sets the internal state for various packet categories.
	 * @param isInventory The value to set for {@code isInventory}.
	 * @param isMail The value to set for {@code isMailOrRepurchase}.
	 * @param isPrivateStore The value to set for {@code isPrivateStore}.
	 * @param isWeaponSwitch The value to set for {@code isWeaponSwitch}.
	 */
	private InventoryPacketType(boolean isInventory, boolean isMail, boolean isPrivateStore, boolean isWeaponSwitch)
	{
		this.isInventory = isInventory;
		isMailOrRepurchase = isMail;
		this.isPrivateStore = isPrivateStore;
		this.isWeaponSwitch = isWeaponSwitch;
	}
	
	/**
	 * Checks if the packet type belongs to an inventory.<br>
	 * This method returns {@code true} for inventory-related types.
	 * @return {@code true} if it is an inventory packet, {@code false} otherwise.
	 */
	public boolean isInventory()
	{
		return isInventory;
	}
	
	/**
	 * Checks if the packet type relates to mail or a repurchase.<br>
	 * This method returns {@code true} if it matches either category.
	 * @return {@code true} if the packet is for mail or repurchase, {@code false} otherwise.
	 */
	public boolean isMail()
	{
		return isMailOrRepurchase;
	}
	
	/**
	 * Checks if the packet type relates to mail or repurchase actions.<br>
	 * This method returns {@code true} for types like {@code MAIL_REPURCHASE}.
	 * @return {@code true} if it is a mail or repurchase packet, {@code false} otherwise.
	 */
	public boolean isRepurchase()
	{
		return isMailOrRepurchase;
	}
	
	/**
	 * Checks if the packet type belongs to a private store.<br>
	 * This method returns {@code true} for {@code PRIVATE_STORE}.<br>
	 * It returns {@code false} for all other types.
	 * @return {@code true} if it is a private store, otherwise {@code false}.
	 */
	public boolean isPrivateStore()
	{
		return isPrivateStore;
	}
	
	/**
	 * Checks if the packet type represents a weapon switch.<br>
	 * This method returns {@code true} for {@code WEAPON_SWITCH}.<br>
	 * It returns {@code false} for all other types.
	 * @return {@code true} if this is a weapon switch, otherwise {@code false}.
	 */
	public boolean isWeaponSwitch()
	{
		return isWeaponSwitch;
	}
}
