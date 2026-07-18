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

import java.util.ArrayList;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.enchant.CombineDaevanionBook;

/**
 * Handles the client request for fusing Daevanion skills.<br>
 * This packet processes the logic required to combine skill books using {@link CombineDaevanionBook}.
 * @author Falke_34
 */
public class CM_DAEVANION_SKILL_FUSION extends AionClientPacket
{
	private final ArrayList<Integer> sacrificeBook = new ArrayList<>();
	private int count;
	
	/**
	 * Handles the client request for Daevanion skill fusion.<br>
	 * This method initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states for the packet.
	 */
	public CM_DAEVANION_SKILL_FUSION(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		readD();
		count = readH();
		for (int i = 0; i < count; ++i)
		{
			sacrificeBook.add(readD());
		}
	}
	
	@Override
	protected void runImpl()
	{
		CombineDaevanionBook.combineDaevanionBook(getConnection().getActivePlayer(), sacrificeBook);
	}
}
