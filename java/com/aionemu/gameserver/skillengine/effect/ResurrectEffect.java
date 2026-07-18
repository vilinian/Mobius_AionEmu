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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RESURRECT;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for resurrecting a {@link Player} character.<br>
 * It triggers the necessary server actions and sends the {@code SM_RESURRECT} packet to the client.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectEffect")
public class ResurrectEffect extends EffectTemplate
{
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	
	/**
	 * Applies a resurrection effect to a {@link Player}.<br>
	 * This method enables the player's resurrection status.<br>
	 * It sets the specific skill ID and sends a {@code SM_RESURRECT} packet.
	 * @param effect The {@code Effect} object containing the target player data.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		final Player effectedPlayer = (Player) effect.getEffected();
		effectedPlayer.setPlayerResActivate(true);
		effectedPlayer.setResurrectionSkill(skillId);
		PacketSendUtility.sendPacket(effectedPlayer, new SM_RESURRECT(effect.getEffector(), effect.getSkillId()));
	}
	
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		if ((effect.getEffected() instanceof Player) && effect.getEffected().getLifeStats().isAlreadyDead())
		{
			super.calculate(effect, null, null);
		}
	}
}
