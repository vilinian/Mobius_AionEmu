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

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * Handles the client request to claim or receive a bonus title.<br>
 * This packet processes the logic for awarding specific titles to a {@link Player}.
 * @author -Enomine-
 */
public class CM_BONUS_TITLE extends AionClientPacket
{
	private int bonusTitleId;
	
	/**
	 * Creates a new instance of the {@link CM_BONUS_TITLE} packet.<br>
	 * This constructor initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if applicable.
	 */
	public CM_BONUS_TITLE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		bonusTitleId = readH();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (bonusTitleId != 0xFFFF)
		{
			if (!player.getTitleList().contains(bonusTitleId) && !player.havePermission(MembershipConfig.TITLES_ADDITIONAL_ENABLE))
			{
				return;
			}
		}
		
		player.getTitleList().setBonusTitle(bonusTitleId);
	}
}
