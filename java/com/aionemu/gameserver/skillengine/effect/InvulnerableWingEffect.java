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
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class handles the visual and functional logic for the invulnerability wing effect.<br>
 * It applies a state where the {@link Player} becomes protected from damage while the effect is active.
 * @author VladimirZ, Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvulnerableWingEffect")
public class InvulnerableWingEffect extends EffectTemplate
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
		// Only for players
		if (effect.getEffected() instanceof Player)
		{
			super.calculate(effect, null, null);
		}
	}
	
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
		((Player) effect.getEffected()).setInvulnerableWing(true);
	}
	
	/**
	 * Stops the invulnerability wing effect for a player.<br>
	 * This method sets the {@code setInvulnerableWing} status to {@code false}.<br>
	 * Use this when the effect duration expires or is manually removed.
	 * @param effect The {@code Effect} object representing the active wing effect.
	 */
	@Override
	public void endEffect(Effect effect)
	{
		((Player) effect.getEffected()).setInvulnerableWing(false);
	}
}
