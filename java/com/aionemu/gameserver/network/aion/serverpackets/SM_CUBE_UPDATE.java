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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet updates the client regarding the status of a cube.<br>
 * It is used to synchronize cube information between the server and the player.
 * @author Sweetkr
 */
public class SM_CUBE_UPDATE extends AionServerPacket
{
	private final int action;
	/**
	 * for action 0 - its storage type<br>
	 * for action 6 - its advanced stigma count
	 */
	private final int actionValue;
	private int itemsCount;
	private int cubeExpands;
	
	/**
	 * Creates a new {@link SM_CUBE_UPDATE} packet for stigma slots.<br>
	 * This method sets the action to {@code 6}.
	 * @param slots The number of available slots.
	 * @return A new {@code SM_CUBE_UPDATE} object.
	 */
	public static SM_CUBE_UPDATE stigmaSlots(int slots)
	{
		return new SM_CUBE_UPDATE(6, slots);
	}
	
	/**
	 * Creates a new {@link SM_CUBE_UPDATE} packet based on the player's storage.<br>
	 * This method calculates the item count and expansion size for different storage types.<br>
	 * It handles {@code CUBE}, {@code REGULAR_WAREHOUSE}, and {@code LEGION_WAREHOUSE} types.
	 * @param type The {@link StorageType} to retrieve data from.
	 * @param player The {@link Player} whose storage information will be used.
	 * @return A new {@link SM_CUBE_UPDATE} packet containing the updated storage data.
	 */
	public static SM_CUBE_UPDATE cubeSize(StorageType type, Player player)
	{
		int itemsCount = 0;
		int cubeExpands = 0;
		switch (type)
		{
			case CUBE:
				itemsCount = player.getInventory().size();
				cubeExpands = player.getCubeExpands();
				break;
			case REGULAR_WAREHOUSE:
				itemsCount = player.getWarehouse().size();
				cubeExpands = player.getWarehouseSize();
				break;
			case LEGION_WAREHOUSE:
				itemsCount = player.getLegion().getLegionWarehouse().size();
				cubeExpands = player.getLegion().getWarehouseLevel();
				break;
			default:
				break;
		}
		
		return new SM_CUBE_UPDATE(0, type.ordinal(), itemsCount, cubeExpands);
	}
	
	/**
	 * Creates a new {@code SM_CUBE_UPDATE} packet with specific data.<br>
	 * This constructor initializes the action details and expansion counts.
	 * @param action The type of action to perform.
	 * @param actionValue The value associated with the chosen action.
	 * @param itemsCount The total number of items involved.
	 * @param cubeExpands The number of times the cube has expanded.
	 */
	private SM_CUBE_UPDATE(int action, int actionValue, int itemsCount, int cubeExpands)
	{
		this(action, actionValue);
		this.itemsCount = itemsCount;
		this.cubeExpands = cubeExpands;
	}
	
	/**
	 * Creates a new {@code SM_CUBE_UPDATE} packet.<br>
	 * This constructor initializes the basic action data.
	 * @param action The type of action to perform.
	 * @param actionValue The specific value associated with the action.
	 */
	private SM_CUBE_UPDATE(int action, int actionValue)
	{
		this.action = action;
		this.actionValue = actionValue;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(action);
		writeC(actionValue);
		switch (action)
		{
			case 0:
				writeD(itemsCount);
				writeC(cubeExpands); // cube size from npc (so max 5 for now)
				writeC(0);
				writeC(0); // unk - expands from items?
				break;
			case 6:
				break;
			default:
				break;
		}
	}
}
