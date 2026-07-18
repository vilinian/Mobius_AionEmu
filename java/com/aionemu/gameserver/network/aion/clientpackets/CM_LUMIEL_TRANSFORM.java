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

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.player.LumielTransformService;

/**
 * Handles the client request to transform into a Lumiel character.<br>
 * This packet interacts with {@link LumielTransformService} to process the transformation logic.
 */
public class CM_LUMIEL_TRANSFORM extends AionClientPacket
{
	private int actionId;
	private int lumielId;
	private int matCount;
	private Player player;
	int objectId;
	long count;
	private final Map<Integer, Long> matrials = new HashMap<>();
	
	/**
	 * This constructor initializes a new {@code CM_LUMIEL_TRANSFORM} packet.<br>
	 * It passes the required network states to the parent class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_LUMIEL_TRANSFORM(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		player = (getConnection()).getActivePlayer();
		actionId = readH();
		switch (actionId)
		{
			case 1:
				break;
			case 2:
				lumielId = readD();
				matCount = readH();
				for (int i = 0; i < matCount; ++i)
				{
					objectId = readD();
					count = readQ();
					matrials.put(objectId, count);
				}
				break;
			case 3:
				lumielId = readD();
				break;
			case 4:
				lumielId = readD();
		}
	}
	
	@Override
	protected void runImpl()
	{
		switch (actionId)
		{
			case 1:
				LumielTransformService.getInstance().sendLumielPacket(player);
				break;
			case 2:
				LumielTransformService.getInstance().onRewardPoints(player, lumielId, matrials);
				break;
			case 3:
				LumielTransformService.getInstance().onGenerateReward(player, lumielId);
				break;
			case 4:
				LumielTransformService.getInstance().onRewardPlayer(player, lumielId);
		}
	}
}
