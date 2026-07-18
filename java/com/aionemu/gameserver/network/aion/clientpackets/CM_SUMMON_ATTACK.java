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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * Handles the client request to initiate an attack by a {@link Summon}.<br>
 * This packet processes the command sent from the client to trigger combat actions.
 * @author ATracer
 */
public class CM_SUMMON_ATTACK extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_SUMMON_ATTACK.class);
	private int summonObjId;
	private int targetObjId;
	private int attackNo;
	private int time;
	private int type;
	
	/**
	 * Handles the packet for a summon performing an attack.<br>
	 * This method initializes the {@link CM_SUMMON_ATTACK} packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if necessary.
	 */
	public CM_SUMMON_ATTACK(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		summonObjId = readD();
		targetObjId = readD();
		attackNo = readC(); // AttackCounter
		time = readH();
		type = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		final Summon summon = player.getSummon();
		if (summon == null)
		{
			log.warn("summon attack without active summon on " + player.getName() + ".");
			return;
		}
		
		if (summon.getObjectId() != summonObjId)
		{
			log.warn("summon attack from a different summon instance on " + player.getName() + ".");
			return;
		}
		
		final VisibleObject obj = summon.getKnownList().getObject(targetObjId);
		if ((obj != null) && (obj instanceof Creature))
		{
			summon.getController().attackTarget((Creature) obj, attackNo, time, type);
		}
		else
		{
			log.warn("summon attack on a wrong target on " + player.getName());
		}
	}
}
