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
import com.aionemu.gameserver.skillengine.model.ChargedSkill;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * Handles the client request to use a charged skill.<br>
 * This packet processes the execution of a {@link ChargedSkill} by the player.
 * @author Cheatkiller
 */
public class CM_USE_CHARGE_SKILL extends AionClientPacket
{
	/**
	 * This method creates a new {@code CM_USE_CHARGE_SKILL} packet.<br>
	 * It initializes the packet with specific network states.<br>
	 * Use this constructor to handle charged skill actions from the client.
	 * @param opcode The unique identifier for the packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if required.
	 */
	public CM_USE_CHARGE_SKILL(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final Skill chargeCastingSkill = player.getCastingSkill();
		if (chargeCastingSkill == null)
		{
			return;
		}
		
		long time = System.currentTimeMillis() - chargeCastingSkill.getCastStartTime();
		int i = 0;
		for (; i < chargeCastingSkill.getChargeSkillList().size(); i++)
		{
			final ChargedSkill skill = chargeCastingSkill.getChargeSkillList().get(i);
			if (time <= skill.getTime())
			{
				break;
			}
			
			time -= skill.getTime();
		}
		
		player.getController().useChargeSkill(chargeCastingSkill.getChargeSkillList().get(i).getId(), chargeCastingSkill.getSkillLevel());
		chargeCastingSkill.cancelCast();
		chargeCastingSkill.getChargeSkillList().clear();
	}
	
	@Override
	protected void readImpl()
	{
	}
}
