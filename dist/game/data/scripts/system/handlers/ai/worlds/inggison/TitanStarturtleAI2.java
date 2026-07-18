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
package system.handlers.ai.worlds.inggison;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence for the {@code titanstarturtle} NPC.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific behaviors for this creature.
 * @author Cheatkiller
 */
@AIName("titanstarturtle") // 215584
public class TitanStarturtleAI2 extends AggressiveNpcAI2
{
	/**
	 * This method is called when the NPC dies.<br>
	 * It handles spawning a new entity and broadcasting a system message.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		super.handleDied();
		spawn(getOwner().getWorldId(), 700545, 338.149f, 573.55f, 460, (byte) 0, 0, 1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(1400486));
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		final Npc windStream = getPosition().getWorldMapInstance().getNpc(700545);
		if (windStream != null)
		{
			windStream.getController().delete();
		}
	}
}
