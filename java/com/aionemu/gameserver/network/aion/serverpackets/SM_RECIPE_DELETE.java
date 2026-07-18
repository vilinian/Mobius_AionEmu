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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to notify it that a recipe has been deleted.<br>
 * It handles the removal of specific items from the player's available recipes.
 * @author namedrisk
 */
public class SM_RECIPE_DELETE extends AionServerPacket
{
	private final int recipeId;
	
	/**
	 * This packet is used to delete a specific recipe.<br>
	 * It stores the unique identifier for the recipe to be removed.
	 * @param recipeId The {@code int} ID of the recipe to delete.
	 */
	public SM_RECIPE_DELETE(int recipeId)
	{
		this.recipeId = recipeId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(recipeId);
	}
}
