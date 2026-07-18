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

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client request to perform an emotion with a {@link Summon}.<br>
 * This packet processes the specific {@link EmotionType} requested by the player.<br>
 * It ensures the action is valid before sending the corresponding {@link SM_EMOTION} back to the client.
 * @author ATracer
 */
public class CM_SUMMON_EMOTION extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_SUMMON_EMOTION.class);
	@SuppressWarnings("unused")
	private int objId;
	private int emotionTypeId;
	
	/**
	 * Handles a request to play an emotion for a summon.<br>
	 * This packet is sent from the client to the server.<br>
	 * It uses the {@link AionClientPacket} base class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if applicable.
	 */
	public CM_SUMMON_EMOTION(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		objId = readD();
		emotionTypeId = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final EmotionType emotionType = EmotionType.getEmotionTypeById(emotionTypeId);
		
		// Unknown Summon Emotion Type
		if (emotionType == EmotionType.UNK)
		{
			log.error("Unknown emotion type? 0x" + Integer.toHexString(emotionTypeId).toUpperCase());
		}
		
		final Summon summon = player.getSummon();
		if (summon == null)
		{
			log.warn("summon emotion without active summon on " + player.getName() + ".");
			return;
		}
		
		switch (emotionType)
		{
			case FLY:
			case LAND:
				PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, EmotionType.START_EMOTE2));
				PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, emotionType));
				break;
			case ATTACKMODE: // start attacking
				summon.setState(CreatureState.WEAPON_EQUIPPED);
				PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, emotionType));
				break;
			case NEUTRALMODE: // stop attacking
				summon.unsetState(CreatureState.WEAPON_EQUIPPED);
				PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, emotionType));
				break;
			default:
				break;
		}
	}
}
