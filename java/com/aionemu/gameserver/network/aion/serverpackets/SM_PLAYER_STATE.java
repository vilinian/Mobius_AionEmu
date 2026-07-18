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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet manages the visual state of a player character.<br>
 * It is primarily used to stop character blinking immediately after a player logs into the game.
 * @author Luno, Sweetkr states: 0 - normal char 1- crounched invisible char 64 - standing blinking char 128- char is invisible
 */
public class SM_PLAYER_STATE extends AionServerPacket
{
	private final int playerObjId;
	private final int visualState;
	private final int seeState;
	
	/**
	 * Creates a new {@code SM_PLAYER_STATE} packet for a specific character.<br>
	 * This method extracts the object ID, visual state, and see state from the provided {@link Creature}.<br>
	 * It is primarily used to manage character visibility and blinking effects.
	 * @param creature The {@code Creature} object containing the player data.
	 */
	public SM_PLAYER_STATE(Creature creature)
	{
		playerObjId = creature.getObjectId();
		visualState = creature.getVisualState();
		seeState = creature.getSeeState();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerObjId);
		writeC(visualState);
		writeC(seeState);
		writeC(visualState == 64 ? 0x01 : 0x00);
	}
}
