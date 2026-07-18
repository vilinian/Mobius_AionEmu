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
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_ARTIFACT_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORTRESS_INFO;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

import system.handlers.ai.GeneralNpcAI2;

/**
 * Handles the artificial intelligence for siege teleporters.<br>
 * This class manages how these NPCs behave during siege events.<br>
 * It extends {@link GeneralNpcAI2} to provide specific teleportation logic.
 * @author Source
 */
@AIName("siege_teleporter")
public class SiegeTeleporterAI2 extends GeneralNpcAI2
{
	boolean isArtifact = false;
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It updates the teleport status to {@code false}.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		canTeleport(false);
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
		canTeleport(true);
		super.handleSpawned();
	}
	
	/**
	 * Updates the teleportation status for a siege object.<br>
	 * This method notifies all players of the change via server packets.
	 * @param status The new {@code boolean} value for teleportation availability.
	 */
	private void canTeleport(boolean status)
	{
		final int id = ((SiegeNpc) getOwner()).getSiegeId();
		try
		{
			SiegeService.getInstance().getFortress(id).setCanTeleport(status);
		}
		catch (Exception e)
		{
			SiegeService.getInstance().getArtifact(id).setCanTeleport(status);
		}
		
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				if (isArtifact)
				{
					PacketSendUtility.sendPacket(player, new SM_ABYSS_ARTIFACT_INFO(id, status));
				}
				else
				{
					PacketSendUtility.sendPacket(player, new SM_FORTRESS_INFO(id, status));
				}
			}
		});
	}
}
