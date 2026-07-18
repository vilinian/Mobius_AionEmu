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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.events.ArcadeUpgradeService;

/**
 * Handles the client request to upgrade an arcade.<br>
 * This packet is processed by the {@link ArcadeUpgradeService} to manage the upgrade logic.
 * @author Raziel
 */
public class CM_UPGRADE_ARCADE extends AionClientPacket
{
	private int action;
	@SuppressWarnings("unused")
	private int sessionId;
	
	/**
	 * This constructor initializes a new {@code CM_UPGRADE_ARCADE} packet.<br>
	 * It sets the required network states for the request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} of the connection.
	 * @param restStates A variable number of additional {@link State} objects.
	 */
	public CM_UPGRADE_ARCADE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		action = readC();
		sessionId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		// GameServer.log.info("[CM_UPGRADE_ARCADE] SessionId: "+sessionId+ " ActionId: "+action);
		switch (action)
		{
			case 0:// get start upgrade arcade info
				ArcadeUpgradeService.getInstance().startArcadeUpgrade(player);
				break;
			case 1: // Close window
				ArcadeUpgradeService.getInstance().closeWindow(player);
				break;
			case 2:// try upgrade arcade
				ArcadeUpgradeService.getInstance().tryArcadeUpgrade(player);
				break;
			case 3:// get reward
				ArcadeUpgradeService.getInstance().getReward(player);
				break;
			case 4:// ReTry upgrade arcade
				player.getUpgradeArcade().setReTry(true);
				ArcadeUpgradeService.getInstance().tryArcadeUpgrade(player);
				break;
			case 5:// get reward list
				ArcadeUpgradeService.getInstance().showRewardList(player);
				break;
			default:
				GameServer.log.info("Found new switch : " + action);
				break;
		}
	}
}
