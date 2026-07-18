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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for evasion effects within the skill engine.<br>
 * This class manages how characters successfully dodge or avoid incoming attacks.
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvadeEffect")
public class EvadeEffect extends DispelEffect
{
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		final Player effector = (Player) effect.getEffector();
		effect.setSkillMoveType(SkillMoveType.MOVEBEHIND);
		if (effect.getEffected().getState() == 3)
		{
			super.calculate(effect, null, null);
		}
		else
		{
			super.calculate(effect, null, SpellStatus.CLOSEAERIAL);
		}
		
		if (effector.isFlying())
		{
			effector.setFlyState(0);
		}
		
		effector.getEffectController().updatePlayerEffectIcons();
		PacketSendUtility.broadcastPacketAndReceive(effector, new SM_TRANSFORM(effector, true));
		PacketSendUtility.broadcastPacketAndReceive(effector, new SM_TRANSFORM(effector, effector.getTransformedModelId(), true, effector.getTransformedItemId(), effector.getTransformedSkillId()));
	}
}
