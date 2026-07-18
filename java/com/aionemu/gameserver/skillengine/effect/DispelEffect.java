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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.skillengine.model.DispelType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for removing specific status effects from targets.<br>
 * This class manages how {@link DispelType} values are applied to players.<br>
 * It is used by the skill engine to process dispel mechanics.
 * @author ATracer
 * @modified Blackfire
 * @update a7xatomic
 */
public class DispelEffect extends EffectTemplate
{
	@XmlElement(type = Integer.class)
	protected List<Integer> effectids;
	@XmlElement
	protected List<String> effecttype;
	@XmlElement
	protected List<String> slottype;
	@XmlAttribute
	protected DispelType dispeltype;
	@XmlAttribute
	protected Integer value;
	
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		if ((effect.getEffected() == null) || (effect.getEffected().getEffectController() == null) || (dispeltype == null))
		{
			return;
		}
		
		if (((dispeltype == DispelType.EFFECTID) || (dispeltype == DispelType.EFFECTIDRANGE)) && (effectids == null))
		{
			return;
		}
		
		if (((dispeltype == DispelType.EFFECTTYPE) && (effecttype == null)) || ((dispeltype == DispelType.SLOTTYPE) && (slottype == null)))
		{
			return;
		}
		
		switch (dispeltype)
		{
			case EFFECTID:
				for (Integer effectId : effectids)
				{
					if ((effectId == 10216101) || (effectId == 10216111) || (effectId == 10216121) || (effectId == 10216181))
					{
						if (effect.getEffected().getEffectController().isAbnormalPresentBySkillId(21610))
						{
							effect.getEffected().getEffectController().removeEffectByEffectId(10216101);
							break;
						}
						
						if (effect.getEffected().getEffectController().isAbnormalPresentBySkillId(21611))
						{
							effect.getEffected().getEffectController().removeEffectByEffectId(10216111);
							break;
						}
						
						if (effect.getEffected().getEffectController().isAbnormalPresentBySkillId(21612))
						{
							effect.getEffected().getEffectController().removeEffectByEffectId(10216121);
							break;
						}
						
						if (effect.getEffected().getEffectController().isAbnormalPresentBySkillId(21618))
						{
							effect.getEffected().getEffectController().removeEffectByEffectId(10216181);
							break;
						}
					}
					else
					{
						effect.getEffected().getEffectController().removeEffectByEffectId(effectId);
					}
				}
				break;
			case EFFECTIDRANGE:
				for (int i = effectids.get(0); i <= effectids.get(1); i++)
				{
					effect.getEffected().getEffectController().removeEffectByEffectId(i);
				}
				break;
			case EFFECTTYPE:
				if (effecttype == null)
				{
					return;
				}
				
				// fix for AT HyperGate skill - 3853 , 3818
				for (String type : effecttype)
				{
					if (type.equals("RIDEROBOT"))
					{
						final Player player = (Player) effect.getEffector();
						if (checkEmbark(player) != 0)
						{
							player.getEffectController().removeEffect(checkEmbark(player));
						}
					}
					else
					{
						final AbnormalState abnormalType = AbnormalState.getIdByName(type);
						if ((abnormalType != null) && effect.getEffected().getEffectController().isAbnormalSet(abnormalType))
						{
							for (Effect ef : effect.getEffected().getEffectController().getAbnormalEffects())
							{
								if ((ef.getAbnormals() & abnormalType.getId()) == abnormalType.getId())
								{
									ef.endEffect();
								}
							}
						}
					}
				}
				
				break;
			case SLOTTYPE:
				for (String type : slottype)
				{
					effect.getEffected().getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.valueOf(type));
				}
				break;
		}
		
		PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new SM_PLAYER_STATE(effect.getEffected()));
	}
	
	/**
	 * Checks if the {@link Player} has any active embark skills.<br>
	 * It iterates through a predefined list of skill IDs.<br>
	 * The method returns the first matching skill ID found or {@code 0}.
	 * @param player The {@code Player} object to check.
	 * @return The ID of the detected embark skill, or {@code 0} if none are present.
	 */
	private int checkEmbark(Player player)
	{
		// All Embark Skills 1 to 5
		final int[] embarkSkills =
		{
			2767,
			2768,
			2769,
			2770,
			2771,
			2772,
			2773,
			2774,
			2775,
			2776,
			2777,
			2778
		};
		for (int eIds : embarkSkills)
		{
			if (player.getEffectController().isNoshowPresentBySkillId(eIds))
			{
				return eIds;
			}
		}
		
		return 0;
	}
}
