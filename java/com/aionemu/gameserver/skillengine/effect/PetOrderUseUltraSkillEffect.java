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

import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_USESKILL;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the visual and functional effects triggered when a pet uses an ultra skill.<br>
 * This class processes specific logic for {@link SummonController} actions related to pet skills.
 * @author ATracer modified by Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetOrderUseUltraSkillEffect")
public class PetOrderUseUltraSkillEffect extends EffectTemplate
{
	@XmlAttribute
	protected boolean release;
	
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		final Player effector = (Player) effect.getEffector();
		
		if (effector.getSummon() == null)
		{
			return;
		}
		
		final int effectorId = effector.getSummon().getObjectId();
		
		final int npcId = effector.getSummon().getNpcId();
		final int orderSkillId = effect.getSkillId();
		
		final int petUseSkillId = DataManager.PET_SKILL_DATA.getPetOrderSkill(orderSkillId, npcId);
		final int targetId = effect.getEffected().getObjectId();
		
		// Handle automatic release if skill expects so
		if (release)
		{
			final SummonController controller = effector.getSummon().getController();
			if (controller != null)
			{
				effector.getSummon().getController().setReleaseAfterSkill(petUseSkillId);
			}
		}
		
		PacketSendUtility.sendPacket(effector, new SM_SUMMON_USESKILL(effectorId, petUseSkillId, 1, targetId));
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
		if ((effect.getEffector() instanceof Player) && (effect.getEffected() != null))
		{
			super.calculate(effect, null, null);
		}
	}
}
