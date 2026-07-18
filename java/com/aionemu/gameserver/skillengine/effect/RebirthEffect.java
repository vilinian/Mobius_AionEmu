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

import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Handles the logic for the rebirth effect within the skill engine.<br>
 * This class defines how a character is revived or reborn during gameplay.
 * @author Sarynth
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RebirthEffect")
public class RebirthEffect extends EffectTemplate
{
	@XmlAttribute(name = "resurrect_percent", required = true)
	protected int resurrectPercent;
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	
	/**
	 * Adds the specified {@code Effect} to the controller.<br>
	 * This updates the internal state of the effect's target.
	 * @param effect The {@code Effect} object to be added.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}
	
	/**
	 * Gets the resurrection percentage for this effect.<br>
	 * This value is stored in the {@code resurrectPercent} field.
	 * @return The resurrection percentage as an {@code int}.
	 */
	public int getResurrectPercent()
	{
		return resurrectPercent;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
}
