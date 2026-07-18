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
 * Handles the client request to set a player's title.<br>
 * This packet processes the title change and updates the {@link Player} data accordingly.
 * @author Nemiroff Date: 01.12.2009
 * @modified cura
 */
public class CM_TITLE_SET extends AionClientPacket
{
	/**
	 * Title id
	 */
	private int titleId;
	
	/**
	 * Creates a new instance of the {@link CM_TITLE_SET} packet.<br>
	 * This method initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_TITLE_SET(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		titleId = readH();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (titleId != 0xFFFF)
		{
			if (!player.getTitleList().contains(titleId) && !player.havePermission(MembershipConfig.TITLES_ADDITIONAL_ENABLE))
			{
				return;
			}
		}
		
		player.getTitleList().setDisplayTitle(titleId);
	}
}
