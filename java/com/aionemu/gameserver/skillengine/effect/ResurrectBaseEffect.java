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

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class serves as the base implementation for resurrection effects.<br>
 * It handles the core logic for reviving a {@link Player} or {@link Creature}.<br>
 * It provides the foundation for all specific types of resurrection behaviors.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectBaseEffect")
public class ResurrectBaseEffect extends ResurrectEffect
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
		calculate(effect, null, null);
	}
	
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
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final Creature effected = effect.getEffected();
		
		if (effected instanceof Player)
		{
			final ActionObserver observer = new ActionObserver(ObserverType.DEATH)
			{
				@Override
				public void died(Creature creature)
				{
					final Player effected = (Player) effect.getEffected();
					if (effected.isInInstance())
					{
						PlayerReviveService.instanceRevive(effected, skillId);
					}
					else if (effected.getKisk() != null)
					{
						PlayerReviveService.kiskRevive(effected, skillId);
					}
					else
					{
						PlayerReviveService.bindRevive(effected, skillId);
					}
					
					PacketSendUtility.broadcastPacket(effected, new SM_EMOTION(effected, EmotionType.RESURRECT), true);
					PacketSendUtility.sendPacket(effected, new SM_PLAYER_SPAWN(effected));
				}
			};
			effect.getEffected().getObserveController().attach(observer);
			effect.setActionObserver(observer, position);
		}
	}
	
	/**
	 * Stops a specific {@code Effect} from being active.<br>
	 * This method removes the associated observers from the target controller.<br>
	 * Use this to clean up effects when they expire or are removed.
	 * @param effect The {@code Effect} object to stop.
	 */
	@Override
	public void endEffect(Effect effect)
	{
		super.endEffect(effect);
		
		if (!effect.getEffected().getLifeStats().isAlreadyDead() && (effect.getActionObserver(position) != null))
		{
			effect.getEffected().getObserveController().removeObserver(effect.getActionObserver(position));
		}
	}
}
