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
package system.handlers.ai.siege;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHIELD_EFFECT;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Handles the artificial intelligence for shield NPCs during siege events.<br>
 * This class extends {@link SiegeNpcAI2} to provide specific behaviors for these entities.
 * @author Source
 */
@AIName("siege_shieldnpc")
public class ShieldNpcAI2 extends SiegeNpcAI2
{
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It sends a packet to update the shield status to {@code false}.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		sendShieldPacket(false);
		super.handleDespawned();
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		sendShieldPacket(true);
		super.handleSpawned();
	}
	
	/**
	 * Sends a shield status update to all players.<br>
	 * This method updates the fortress state and broadcasts the {@code SM_SHIELD_EFFECT} packet.
	 * @param shieldStatus The current state of the shield, either {@code true} or {@code false}.
	 */
	private void sendShieldPacket(boolean shieldStatus)
	{
		final int id = getSpawnTemplate().getSiegeId();
		SiegeService.getInstance().getFortress(id).setUnderShield(shieldStatus);
		
		final SM_SHIELD_EFFECT packet = new SM_SHIELD_EFFECT(id);
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				PacketSendUtility.sendPacket(player, packet);
			}
		});
	}
}
