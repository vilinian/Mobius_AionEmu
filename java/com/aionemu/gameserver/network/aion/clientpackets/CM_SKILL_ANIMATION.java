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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * This packet handles the synchronization of skill animations between the client and the server.<br>
 * It ensures that visual effects and character movements are correctly triggered for players.
 * @author FrozenKiller
 * @reworked Ghostfur (Aion-Unique)
 */
public class CM_SKILL_ANIMATION extends AionClientPacket
{
	private int skillId;
	private int skillSkinId;
	
	/**
	 * Creates a new {@link CM_SKILL_ANIMATION} packet.<br>
	 * This constructor initializes the packet with specific network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates A variable number of additional states.
	 */
	public CM_SKILL_ANIMATION(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		skillId = readH();
		skillSkinId = readH();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (skillSkinId > 0)
		{
			player.getSkillSkinList().setActive(skillSkinId);
		}
		else
		{
			player.getSkillSkinList().setDeactive(skillId);
		}
	}
}
