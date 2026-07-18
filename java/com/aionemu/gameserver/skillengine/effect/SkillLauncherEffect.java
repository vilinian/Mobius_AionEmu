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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * Represents an effect that is triggered specifically when a skill is launched.<br>
 * It extends {@link EffectTemplate} to provide data for these types of visual or functional actions.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillLauncherEffect")
public class SkillLauncherEffect extends EffectTemplate
{
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		// TODO figure out what value does
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		final Effect e = new Effect(effect.getEffector(), effect.getEffected(), template, template.getLvl(), 0);
		e.initialize();
		e.applyEffect();
	}
	
	/**
	 * Retrieves the unique identifier for the launch skill.<br>
	 * This value is used to identify which skill triggers this effect.
	 * @return The {@code int} ID of the launch skill.
	 */
	public int getLaunchSkillId()
	{
		return skillId;
	}
}
