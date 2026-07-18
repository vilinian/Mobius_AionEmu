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

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MANTRA_EFFECT;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Represents an aura effect that provides continuous status changes to a target.<br>
 * This class handles the logic for applying persistent buffs or debuffs associated with skills.
 * @author ATracer, kecimis, xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuraEffect")
public class AuraEffect extends EffectTemplate
{
	@XmlAttribute
	protected int distance;
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	// TODO distancez
	
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
		if (effector.getEffectController().isNoshowPresentBySkillId(effect.getSkillId()))
		{
			AuditLogger.info(effector, "Player might be abusing CM_CASTSPELL mantra effect Player kicked skill id: " + effect.getSkillId());
			effector.getClientConnection().closeNow();
			return;
		}
		
		effect.addToEffectedController();
	}
	
	/**
	 * Executes the periodic logic for a specific {@code Effect}.<br>
	 * This method checks if the effector is online and applies the effect to nearby players.<br>
	 * It handles group range calculations and ensures effects are applied correctly during duels.<br>
	 * Finally, it broadcasts the updated effect packet to the effector.
	 * @param effect The {@code Effect} object to be processed.
	 */
	@Override
	public void onPeriodicAction(Effect effect)
	{
		final Player effector = (Player) effect.getEffector();
		if (!effector.isOnline())
		{
			// task check
			return;
		}
		
		if (effector.isInGroup2() || effector.isInAlliance2())
		{
			final Collection<Player> onlynePlayers = effector.isInGroup2() ? effector.getPlayerGroup2().getOnlineMembers() : effector.getPlayerAllianceGroup2().getOnlineMembers();
			final int actualRange = (int) ((distance * effector.getGameStats().getStat(StatEnum.BOOST_MANTRA_RANGE, 100).getCurrent()) / 100f);
			for (Player player : onlynePlayers)
			{
				if (MathUtil.isIn3dRange(effector, player, actualRange))
				{
					if (!DuelService.getInstance().isDueling(player.getObjectId()) && (player != effector))
					{
						applyAuraTo(player, effect);
					}
					
					if (DuelService.getInstance().isDueling(effector.getObjectId()) && DuelService.getInstance().isDueling(player.getObjectId()))
					{
						applyAuraTo(effector, effect);
					}
					else
					{
						applyAuraTo(effector, effect);
					}
				}
			}
		}
		else
		{
			applyAuraTo(effector, effect);
		}
		
		PacketSendUtility.broadcastPacket(effector, new SM_MANTRA_EFFECT(effector, skillId));
	}
	
	/**
	 * Applies a specific aura effect to a player.<br>
	 * This method initializes and activates the {@code Effect} for the target.
	 * @param effected The {@link Player} who will receive the aura.
	 * @param effect The {@link Effect} data to be applied.
	 */
	private void applyAuraTo(Player effected, Effect effect)
	{
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		final Effect e = new Effect(effected, effected, template, template.getLvl(), 0);
		e.initialize();
		e.applyEffect();
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
		effect.setPeriodicTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new AuraTask(effect), 0, 6500), position);
	}
	
	private class AuraTask implements Runnable
	{
		private final Effect effect;
		
		public AuraTask(Effect effect)
		{
			this.effect = effect;
		}
		
		@Override
		public void run()
		{
			onPeriodicAction(effect);
			/**
			 * This has the special effect of clearing the current thread's quantum and putting it to the end of the queue for its priority level. Will just give-up the thread's turn, and gain it in the next round.
			 */
			Thread.yield();
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
		// nothing todo
	}
}
