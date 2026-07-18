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
package system.handlers.ai.quests;

import com.aionemu.gameserver.ai2.AIName;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence for infiltrator NPCs.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific behaviors for these enemies.
 * @author Cheatkiller
 */
@AIName("infiltrator")
public class InfiltratorsAI2 extends AggressiveNpcAI2
{
	/**
	 * This method is called when the NPC dies.<br>
	 * It handles specific spawning logic based on the owner's ID.<br>
	 * A new NPC is spawned at the owner's location if a match is found.
	 */
	@Override
	protected void handleDied()
	{
		super.handleDied();
		final int owner = getOwner().getNpcId();
		int spawnNpc = 0;
		switch (owner)
		{
			case 282913:
				spawnNpc = 282914;
				break;
			case 282918:
				spawnNpc = 282920;
				break;
			case 282920:
				spawnNpc = 282922;
				break;
			case 282917:
				spawnNpc = 282915;
				break;
			case 282915:
				spawnNpc = 282916;
				break;
			case 282919:
				spawnNpc = 282921;
				break;
			case 282921:
				spawnNpc = 282923;
				break;
		}
		
		spawn(spawnNpc, getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ(), getOwner().getSpawn().getHeading());
	}
}
