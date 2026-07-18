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

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.reward.RewardService;

/**
 * Handles the {@code CM_PLAYER_LISTENER} packet received from the client.<br>
 * This class processes requests to listen for player-related events or updates.<br>
 * It interacts with {@link Player} objects and other game services to manage listener states.
 * @author ginho1
 */
public class CM_PLAYER_LISTENER extends AionClientPacket
{
	/*
	 * This CM is send every five minutes by client.
	 */
	
	/**
	 * Creates a new instance of {@link CM_PLAYER_LISTENER}.<br>
	 * This constructor initializes the packet with its required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_PLAYER_LISTENER(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		if (CustomConfig.ENABLE_REWARD_SERVICE)
		{
			RewardService.getInstance().verify(player);
		}
	}
}
