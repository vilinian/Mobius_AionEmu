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
package com.aionemu.gameserver.skillengine.periodicaction;

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Handles periodic actions that consume {@code MP} from the caster.<br>
 * It extends {@link PeriodicAction} to manage recurring effects with mana costs.
 * @author antness
 * @Reworked Kill3r
 */
public class MpUsePeriodicAction extends PeriodicAction
{
	@XmlAttribute(name = "value")
	protected int value;
	@XmlAttribute(name = "ratio")
	protected boolean ratio;
	
	/**
	 * Applies an MP reduction to the target of an {@link Effect}.<br>
	 * It checks if the target has enough current MP before applying the change.<br>
	 * The amount can be a fixed value or a percentage of maximum MP.
	 * @param effect The {@code Effect} object containing the target and details.
	 */
	@Override
	public void act(Effect effect)
	{
		final Creature effected = effect.getEffected();
		final int maxMp = effected.getGameStats().getMaxMp().getCurrent();
		int requiredMp = value;
		if (ratio)
		{
			requiredMp = (int) (maxMp * (value / 100f));
		}
		
		if (effected.getLifeStats().getCurrentMp() < requiredMp)
		{
			effect.endEffect();
			return;
		}
		
		effected.getLifeStats().reduceMp(requiredMp);
	}
}
