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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for expanding player cubes in the game.<br>
 * It manages cube growth and sends updates to the {@link Player} via {@link SM_CUBE_UPDATE}.
 * @author ATracer
 * @author Simple
 * @reworked Luzien
 */
public class CubeExpandService
{
	private static final int MIN_EXPAND = 0;
	private static final int MAX_EXPAND = 15;
	
	/**
	 * This method expands the cube storage for a {@link Player}.<br>
	 * It checks if the expansion is allowed before updating the size.<br>
	 * If {@code isCubeExpand} is {@code true}, it increments the expansion count.
	 * @param player The {@link Player} who will receive the expanded cube.
	 * @param isCubeExpand A boolean flag to determine if the expansion count should increase.
	 */
	public static void expand(Player player, boolean isCubeExpand)
	{
		if (!canExpand(player))
		{
			return;
		}
		
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300431, "9")); // 9 Slots added
		if (isCubeExpand)
		{
			player.setCubeExpands(player.getCubeExpands() + 1);
		}
		
		PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(StorageType.CUBE, player));
	}
	
	/**
	 * Checks if a {@link Player} is allowed to expand their cube.<br>
	 * This method verifies the next expansion level against the maximum limit.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the expansion is valid, {@code false} otherwise.
	 */
	public static boolean canExpand(Player player)
	{
		return validateNewSize(player.getCubeExpands() + 1);
	}
	
	/**
	 * Checks if the provided size is within the allowed limits.<br>
	 * It ensures the value is not smaller than {@code 0}.<br>
	 * It also ensures the value does not exceed {@code 15}.
	 * @param level The new size to validate.
	 * @return {@code true} if the size is valid, otherwise {@code false}.
	 */
	private static boolean validateNewSize(int level)
	{
		// check min and max level
		return !((level < MIN_EXPAND) || (level > MAX_EXPAND));
	}
}
