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

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.services.player.PlayerVisualStateService;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for searching and identifying targets within a specific range.<br>
 * This effect is used to trigger visual or mechanical responses when a {@link Creature} is detected.
 * @author Sweetkr
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchEffect")
public class SearchEffect extends EffectTemplate
{
	@XmlAttribute
	protected CreatureSeeState state;
	
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
	 * Stops a specific {@code Effect} from being active.<br>
	 * This method removes the associated observers from the target controller.<br>
	 * Use this to clean up effects when they expire or are removed.
	 * @param effect The {@code Effect} object to stop.
	 */
	@Override
	public void endEffect(Effect effect)
	{
		final Creature effected = effect.getEffected();
		
		effected.unsetSeeState(state);
		
		if (SecurityConfig.INVIS && (effected instanceof Player))
		{
			PlayerVisualStateService.seeValidate((Player) effected);
		}
		
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_PLAYER_STATE(effected));
	}
	
	/**
	 * Starts a specific {@link Effect} on a target creature.<br>
	 * This method updates the target's see state and broadcasts the change.<br>
	 * It also handles visibility validation if security settings are enabled.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final Creature effected = effect.getEffected();
		
		effected.setSeeState(state);
		
		if (SecurityConfig.INVIS && (effected instanceof Player))
		{
			PlayerVisualStateService.seeValidate((Player) effected);
		}
		
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_PLAYER_STATE(effected));
	}
}
