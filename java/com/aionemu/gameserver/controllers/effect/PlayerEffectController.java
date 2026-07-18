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
package com.aionemu.gameserver.controllers.effect;

import java.util.Collection;
import java.util.Collections;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STANCE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.taskmanager.tasks.TeamEffectUpdater;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the application and lifecycle of effects specifically for {@link Player} objects.<br>
 * It handles how visual or functional effects are triggered, updated, and removed from players in the game world.
 * @author ATracer
 */
public class PlayerEffectController extends EffectController
{
	/**
	 * Creates a new instance of {@link PlayerEffectController}.<br>
	 * This constructor initializes the controller for a specific creature.
	 * @param owner The {@code Creature} that will own these effects.
	 */
	public PlayerEffectController(Creature owner)
	{
		super(owner);
	}
	
	/**
	 * Adds a new {@code Effect} to the player.<br>
	 * This method updates the player icons and group status.<br>
	 * It skips adding the effect if it fails the duel condition check.
	 * @param effect The {@code Effect} object to be added.
	 */
	@Override
	public void addEffect(Effect effect)
	{
		if (checkDuelCondition(effect) && !effect.getIsForcedEffect())
		{
			return;
		}
		
		super.addEffect(effect);
		updatePlayerIconsAndGroup(effect);
	}
	
	/**
	 * Removes a specific {@code Effect} from the owner.<br>
	 * This method identifies the correct map using {@code getMapForEffect}.<br>
	 * It deletes the effect based on its stack and then calls {@code broadCastEffects}.
	 * @param effect The {@code Effect} object to be removed.
	 */
	@Override
	public void clearEffect(Effect effect)
	{
		super.clearEffect(effect);
		updatePlayerIconsAndGroup(effect);
	}
	
	/**
	 * Retrieves the {@link Player} that owns this object.<br>
	 * This method casts the result of the parent class's owner retrieval to a {@code Player}.
	 * @return The {@code Player} associated with this object.
	 */
	@Override
	public Player getOwner()
	{
		return (Player) super.getOwner();
	}
	
	/**
	 * Updates the visual icons for a player based on an {@code Effect}.<br>
	 * This method only runs if the effect is not passive.<br>
	 * It also starts a team update task if the owner belongs to a team.
	 * @param effect The {@code Effect} object to process.
	 */
	private void updatePlayerIconsAndGroup(Effect effect)
	{
		if (!effect.isPassive())
		{
			updatePlayerEffectIcons();
			if (getOwner().isInTeam())
			{
				TeamEffectUpdater.getInstance().startTask(getOwner());
			}
		}
	}
	
	/**
	 * Sends a packet to update the player's effect icons.<br>
	 * This method adds the {@code UPDATE_PLAYER_EFFECT_ICONS} mask to the owner's broadcast mask.<br>
	 * It triggers a synchronization of visual status symbols on the client side.
	 */
	@Override
	public void updatePlayerEffectIcons()
	{
		getOwner().addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_EFFECT_ICONS);
	}
	
	/**
	 * Updates the visual icons for player effects.<br>
	 * This method refreshes the displayed status icons on the {@link Player}.
	 */
	@Override
	public void updatePlayerEffectIconsImpl()
	{
		final Collection<Effect> effects = getAbnormalEffectsToShow();
		PacketSendUtility.sendPacket(getOwner(), new SM_ABNORMAL_STATE(effects, abnormals));
	}
	
	/**
	 * Checks if an {@code Effect} violates duel rules.<br>
	 * It verifies if a non-enemy player is receiving a debuff.<br>
	 * This helps prevent illegal status effects during combat.
	 * @param effect The {@code Effect} to validate.
	 * @return {@code true} if the condition is met, {@code false} otherwise.
	 */
	private boolean checkDuelCondition(Effect effect)
	{
		final Creature creature = effect.getEffector();
		if (creature instanceof Player)
		{
			if (!getOwner().isEnemy(creature) && (effect.getTargetSlot() == SkillTargetSlot.DEBUFF.ordinal()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Adds a new effect to the player based on a skill template.<br>
	 * This method validates the remaining time before creating the {@code Effect}.<br>
	 * It also handles specific logic for deity avatars if configured in {@code CustomConfig}.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLvl The level of the skill being applied.
	 * @param remainingTime The duration of the effect in milliseconds.
	 * @param endTime The timestamp when the effect is scheduled to expire.
	 */
	public void addSavedEffect(int skillId, int skillLvl, int remainingTime, long endTime)
	{
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		
		if (remainingTime <= 0)
		{
			return;
		}
		
		if (CustomConfig.ABYSSXFORM_LOGOUT && template.isDeityAvatar())
		{
			if (System.currentTimeMillis() >= endTime)
			{
				return;
			}
			
			remainingTime = (int) (endTime - System.currentTimeMillis());
		}
		
		final Effect effect = new Effect(getOwner(), getOwner(), template, skillLvl, remainingTime);
		abnormalEffectMap.put(effect.getStack(), effect);
		effect.addAllEffectToSucess();
		effect.startEffect(true);
		
		if (effect.getSkillTemplate().getTargetSlot() != SkillTargetSlot.NOSHOW)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_ABNORMAL_STATE(Collections.singletonList(effect), abnormals));
		}
		
	}
	
	/**
	 * Sends the current abnormal effects to all nearby clients.<br>
	 * This method uses {@code getAbnormalEffects} to retrieve the list of active effects.<br>
	 * It then broadcasts an {@code SM_ABNORMAL_EFFECT} packet to everyone around the {@code owner}.
	 */
	@Override
	public void broadCastEffectsImp()
	{
		super.broadCastEffectsImp();
		final Player player = getOwner();
		if (player.getController().isUnderStance())
		{
			PacketSendUtility.sendPacket(player, new SM_PLAYER_STANCE(player, 1));
		}
	}
}
