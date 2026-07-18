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

import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.summons.SummonsService;

/**
 * Handles the client command to summon or unsummon a pet.<br>
 * This packet processes requests sent by a {@link Player} to interact with their {@link Summon}.
 * @author ATracer
 */
public class CM_SUMMON_COMMAND extends AionClientPacket
{
	private int mode;
	private int targetObjId;
	
	/**
	 * Creates a new {@link CM_SUMMON_COMMAND} packet.<br>
	 * This constructor initializes the packet with specific network states.
	 * @param opcode The unique identifier for this command type.
	 * @param state The primary connection state of the sender.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_SUMMON_COMMAND(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		mode = readC();
		readD();
		readD();
		targetObjId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		final Summon summon = activePlayer.getSummon();
		final SummonMode summonMode = SummonMode.getSummonModeById(mode);
		if ((summon != null) && (summonMode != null))
		{
			SummonsService.doMode(summonMode, summon, targetObjId, UnsummonType.COMMAND);
		}
	}
}
