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

import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client request to apply a stigma effect.<br>
 * This packet processes the logic for marking a target with a specific status.<br>
 * It interacts with {@link Player} data and {@link AbnormalState} effects.
 * @author Falke_34, FrozenKiller
 */
public class CM_STIGMA extends AionClientPacket
{
	private int action;
	private long slotRead;
	private int itemObjectId;
	
	/**
	 * This constructor initializes a new {@link CM_STIGMA} packet.<br>
	 * It sets the required network states for the packet.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} of the connection.
	 * @param restStates A variable number of additional {@link State} objects.
	 */
	public CM_STIGMA(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		action = readC(); // add=0, remove=1
		slotRead = readQ(); // Slot
		itemObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		activePlayer.getController().cancelUseItem();
		
		final Equipment equipment = activePlayer.getEquipment();
		
		if (!RestrictionsManager.canChangeEquip(activePlayer))
		{
			return;
		}
		
		if (activePlayer.getEffectController().isAbnormalState(AbnormalState.CANT_ATTACK_STATE))
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_ACT_WHILE_IN_ABNORMAL_STATE);
			return;
		}
		
		switch (action)
		{
			case 0:
				equipment.equipItem(itemObjectId, slotRead);
				break;
			case 1:
				equipment.unEquipItem(itemObjectId, slotRead);
				break;
			default:
				break;
		}
	}
}
