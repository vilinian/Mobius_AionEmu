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
 * Handles the client request to cast a spell on a {@link Summon} entity.<br>
 * This packet processes the interaction between a {@link Player} and their summoned creatures.
 * @author ATracer, KID
 */
public class CM_SUMMON_CASTSPELL extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_SUMMON_CASTSPELL.class);
	private int summonObjId;
	private int targetObjId;
	private int skillId;
	@SuppressWarnings("unused")
	private int skillLvl;
	@SuppressWarnings("unused")
	private float unk;
	
	/**
	 * Handles the packet for summoning a creature and casting a spell.<br>
	 * This method initializes the {@link CM_SUMMON_CASTSPELL} object with network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states provided by the client.
	 */
	public CM_SUMMON_CASTSPELL(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		summonObjId = readD();
		skillId = readH();
		skillLvl = readC();
		targetObjId = readD();
		unk = readF();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		final long currentTime = System.currentTimeMillis();
		if (player.getNextSummonSkillUse() > currentTime)
		{
			return;
		}
		
		final Summon summon = player.getSummon();
		if (summon == null)
		{
			log.warn("summon castspell without active summon on " + player.getName() + ".");
			return;
		}
		
		if (summon.getObjectId() != summonObjId)
		{
			log.warn("summon castspell from a different summon instance on " + player.getName() + ".");
			return;
		}
		
		Creature target = null;
		if (targetObjId != summon.getObjectId())
		{
			final VisibleObject obj = summon.getKnownList().getObject(targetObjId);
			if (obj instanceof Creature)
			{
				target = (Creature) obj;
			}
		}
		else
		{
			target = summon;
		}
		
		if (target != null)
		{
			player.setNextSummonSkillUse(currentTime + 1100);
			summon.getController().useSkill(skillId, target);
		}
		else
		{
			log.warn("summon castspell on a wrong target on " + player.getName());
		}
	}
}
