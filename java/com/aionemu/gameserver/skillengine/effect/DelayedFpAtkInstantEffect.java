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

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the execution of an instant attack effect that is triggered after a specific delay.<br>
 * This class allows for delayed physical attacks to be processed by the {@code Effect} system.
 * @author kecimis
 */
public class DelayedFpAtkInstantEffect extends EffectTemplate
{
	@XmlAttribute
	protected int delay;
	@XmlAttribute
	protected boolean percent;
	
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		// Only players have FP
		if (effect.getEffected() instanceof Player)
		{
			super.calculate(effect, null, null);
		}
	}
	
	/**
	 * Schedules the application of an {@code Effect} after a specific delay.<br>
	 * This method uses {@link ThreadPoolManager} to run the task asynchronously.<br>
	 * It checks if the target is an enemy before calling {@code calculateAndApplyDamage}.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (effect.getEffector().isEnemy(effect.getEffected()))
				{
					calculateAndApplyDamage(effect);
				}
			}
		}, delay);
	}
	
	/**
	 * This method calculates the damage for a specific {@code Effect}.<br>
	 * It checks if the target is an instance of {@link Player}.<br>
	 * The final value is applied to the player's FP stats.
	 * @param effect The {@code Effect} object containing the damage data.
	 */
	private void calculateAndApplyDamage(Effect effect)
	{
		if (!(effect.getEffected() instanceof Player))
		{
			return;
		}
		
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		final Player player = (Player) effect.getEffected();
		final int maxFP = player.getLifeStats().getMaxFp();
		
		int newValue = valueWithDelta;
		
		// Support for values in percentage
		if (percent)
		{
			newValue = (maxFP * valueWithDelta) / 100;
		}
		
		player.getLifeStats().reduceFp(newValue);
	}
}
