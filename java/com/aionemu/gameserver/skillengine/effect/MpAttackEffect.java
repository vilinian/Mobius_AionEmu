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

import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HitType;

/**
 * Represents an effect that deals damage based on the target's {@code MP} value.<br>
 * This class is used by the skill engine to calculate and apply specific magic point attacks.
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MpAttackEffect")
public class MpAttackEffect extends AbstractOverTimeEffect
{
	// TODO bosses are resistent to this?
	/**
	 * Applies the periodic MP reduction logic to a target.<br>
	 * This method calculates the amount to reduce based on the {@code Effect} settings.<br>
	 * It handles both flat values and percentage calculations for the target's maximum MP.
	 * @param effect The {@code Effect} object containing the reduction details.
	 */
	@Override
	public void onPeriodicAction(Effect effect)
	{
		final int maxMP = effect.getEffected().getLifeStats().getMaxMp();
		int newValue = value;
		
		// Support for values in percentage
		if (hitType == HitType.FEAR)
		{
			// TODO
		}
		
		if (percent)
		{
			newValue = (maxMP * value) / 100;
		}
		
		effect.getEffected().getLifeStats().reduceMp(newValue);
	}
}
