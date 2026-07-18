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

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the specific AI behavior for NPCs associated with quest {@code 14026}.<br>
 * This class extends {@link AggressiveNpcAI2} to manage hostile interactions during this quest.
 * @author FrozenKiller
 */
@AIName("quest14026")

// 213575,213576,213577,213578
public class Quest14026 extends AggressiveNpcAI2
{
	private final String walkerId = "quest14026";
	private Npc kimeia;
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		kimeia = getPosition().getWorldMapInstance().getNpc(204044);
		if (kimeia != null)
		{
			AI2Actions.targetCreature(this, kimeia);
			getAggroList().addHate(kimeia, 100000);
			getSpawnTemplate().setWalkerId(walkerId);
			WalkManager.startWalking(this);
			getOwner().setState(1);
			PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
		}
	}
}
