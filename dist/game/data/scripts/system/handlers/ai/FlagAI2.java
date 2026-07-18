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
package system.handlers.ai;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FLAG_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FLAG_UPDATE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Handles the artificial intelligence logic for {@code FLAG} entities.<br>
 * This class manages how flags behave and interact within the game world.
 */
@AIName("flag")
public class FlagAI2 extends NoActionAI2
{
	private Future<?> sendPacketTask;
	
	/**
	 * This method is called when the AI entity is first spawned.<br>
	 * It starts a background task to send updates to all players.<br>
	 * The task checks if the player is in the same world as the owner.<br>
	 * If they are, it sends an {@code SM_FLAG_INFO} packet every 2 seconds.
	 */
	@Override
	public void handleSpawned()
	{
		super.handleSpawned();
		World.getInstance().doOnAllPlayers(player -> sendPacketTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			if (player.getWorldId() == getOwner().getWorldId())
			{
				if (getOwner().isSpawned())
				{
					PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, getOwner()));
				}
			}
		}, 1000, 2000));
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It schedules a periodic task to update players about the owner's status.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		super.handleDespawned();
		World.getInstance().doOnAllPlayers(player -> sendPacketTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			if (player.getWorldId() == getOwner().getWorldId())
			{
				PacketSendUtility.sendPacket(player, new SM_FLAG_UPDATE(getOwner()));
				AI2Actions.deleteOwner(FlagAI2.this);
			}
		}, 1000, 2000));
	}
	
	/**
	 * Stops the current packet sending task.<br>
	 * It checks if {@code sendPacketTask} is active before calling {@code cancel}.
	 */
	@SuppressWarnings("unused")
	private void cancelTask()
	{
		if ((sendPacketTask != null) && !sendPacketTask.isCancelled())
		{
			sendPacketTask.cancel(true);
		}
	}
}
