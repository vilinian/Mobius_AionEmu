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
package com.aionemu.gameserver.controllers;

import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.observer.StartMovingListener;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.task.GatheringTask;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.RndSelector;
import com.aionemu.gameserver.utils.captcha.CAPTCHAUtil;
import com.aionemu.gameserver.world.World;

/**
 * Handles the logic for interactable objects that can be gathered in the game world.<br>
 * This controller manages interactions between {@link Player} entities and {@link Gatherable} objects.<br>
 * It processes gathering tasks, rewards, and related state updates.
 * @author ATracer, sphinx, Cura
 */
public class GatherableController extends VisibleObjectController<Gatherable>
{
	private int gatherCount;
	private int currentGatherer;
	private GatheringTask task;
	private GatherState state = GatherState.IDLE;
	private RndSelector<Material> mats;
	
	public enum GatherState
	{
		GATHERED,
		GATHERING,
		IDLE
	}
	
	/**
	 * Handles the logic when a {@link Player} attempts to interact with a gatherable object.<br>
	 * This method validates requirements such as level, inventory space, and required tools.<br>
	 * It also handles security checks like CAPTCHA before starting the gathering task.
	 * @param player The {@link Player} who is attempting to use the gatherable object.
	 */
	public void onStartUse(Player player)
	{
		// basic actions, need to improve here
		final GatherableTemplate template = getOwner().getObjectTemplate();
		final int gatherId = template.getTemplateId();
		if (player.getLevel() > 10)
		{
			switch (gatherId)
			{
				case 400201: // Impure Iron Ore.
				case 400251: // Impure Iron Ore.
				case 400601: // Young Aria.
				case 400651: // Young Azpha.
				case 400701: // Mela Sapling.
				case 400751: // Raydam Sapling.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GATHER_INCORRECT_SKILL);
					break;
			}
			
			finishGathering(player);
		}
		
		if (template.getLevelLimit() > 0)
		{
			// You must be at least level %0 to perform extraction.
			if (player.getLevel() < template.getLevelLimit())
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400737, template.getLevelLimit()));
				return;
			}
		}
		
		if (player.isInPlayerMode(PlayerMode.RIDE))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401096));
			return;
		}
		
		if (player.getInventory().isFull())
		{
			// You must have at least one free space in your cube to gather.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330036));
			return;
		}
		
		// check is gatherable
		if ((MathUtil.getDistance(getOwner(), player) > 6) || !checkGatherable(player, template) || !checkPlayerSkill(player, template))
		{
			return;
		}
		
		// check for extractor in inventory
		final byte result = checkPlayerRequiredExtractor(player, template);
		if (result == 0)
		{
			return;
		}
		
		// CAPTCHA
		if (SecurityConfig.CAPTCHA_ENABLE)
		{
			if (SecurityConfig.CAPTCHA_APPEAR.equals(template.getSourceType()) || SecurityConfig.CAPTCHA_APPEAR.equals("ALL"))
			{
				int rate = SecurityConfig.CAPTCHA_APPEAR_RATE;
				if (template.getCaptchaRate() > 0)
				{
					rate = (int) (template.getCaptchaRate() * 0.1f);
				}
				
				if (Rnd.get(0, 100) < rate)
				{
					player.setCaptchaWord(CAPTCHAUtil.getRandomWord());
					player.setCaptchaImage(CAPTCHAUtil.createCAPTCHA(player.getCaptchaWord()).array());
					PunishmentService.setIsNotGatherable(player, 0, true, SecurityConfig.CAPTCHA_EXTRACTION_BAN_TIME * 1000L);
					
					// You were poisoned during extraction and cannot extract for (Time remaining: 10Min)
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CAPTCHA_RESTRICTED("10"));
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 600));
				}
			}
		}
		
		List<Material> materials = null;
		switch (result)
		{
			case 1: // player has equipped item, or have a consumable in inventory, so he will obtain extra items
				materials = template.getExtraMaterials().getMaterial();
				break;
			case 2:// regular thing
				materials = template.getMaterials().getMaterial();
				break;
		}
		
		mats = new RndSelector<>();
		
		if (materials != null)
		{
			for (Material mat : materials)
			{
				mats.add(mat, mat.getRate());
			}
		}
		
		synchronized (state)
		{
			if (state != GatherState.GATHERING)
			{
				state = GatherState.GATHERING;
				currentGatherer = player.getObjectId();
				startGatherProtection(player);
				player.getObserveController().attach(new StartMovingListener()
				{
					
					@Override
					public void moved()
					{
						finishGathering(player);
						stopGatherProtection(player);
					}
				});
				final int skillLvlDiff = player.getSkillList().getSkillLevel(template.getHarvestSkill()) - template.getSkillLevel();
				task = new GatheringTask(player, getOwner(), getMaterial(), skillLvlDiff);
				task.start();
			}
		}
	}
	
	/**
	 * Retrieves a {@link Material} based on random selection.<br>
	 * This method uses the rates defined in the material list.<br>
	 * It returns {@code null} if no material is selected.
	 * @return The selected {@code Material} object or {@code null}.
	 */
	public Material getMaterial()
	{
		final Material m = mats.select();
		final int chance = Rnd.get(m.getRate());
		int current = 0;
		current += m.getRate();
		if (mats != null)
		{
			if (current >= chance)
			{
				return m;
			}
		}
		
		return null;
	}
	
	/**
	 * Verifies if a {@link Player} has the required skill to gather an item.<br>
	 * It checks for both the presence of the skill and the minimum level.<br>
	 * If requirements are not met, it sends a system message to the player.
	 * @param player The {@link Player} attempting to perform the action.
	 * @param template The {@link GatherableTemplate} containing the required skill data.
	 * @return {@code true} if the player meets all requirements, otherwise {@code false}.
	 */
	private boolean checkPlayerSkill(Player player, GatherableTemplate template)
	{
		final int harvestSkillId = template.getHarvestSkill();
		if (!player.getSkillList().isSkillPresent(harvestSkillId))
		{
			if (harvestSkillId == 30001)
			{
				// You are Daeva now, leave this to humans.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GATHER_INCORRECT_SKILL);
			}
			else
			{
				// You must learn the %0 skill to start gathering.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330054, new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(harvestSkillId).getNameId())));
			}
			
			return false;
		}
		
		if (player.getSkillList().getSkillLevel(harvestSkillId) < template.getSkillLevel())
		{
			// Your %0 skill level is not high enough.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330001, new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(harvestSkillId).getNameId())));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if the {@link Player} meets the extractor requirements for a {@link GatherableTemplate}.<br>
	 * It validates whether the player has the correct item equipped or in their inventory.
	 * @param player The {@link Player} performing the action.
	 * @param template The {@link GatherableTemplate} being interacted with.
	 * @return A byte value representing the result: 1 for success, 2 for failure, and 0 if a system message is sent.
	 */
	private byte checkPlayerRequiredExtractor(Player player, GatherableTemplate template)
	{
		if (template.getRequiredItemId() > 0)
		{
			if (template.getCheckType() == 1)
			{
				final List<Item> items = player.getEquipment().getEquippedItemsByItemId(template.getRequiredItemId());
				boolean condOk = false;
				for (Item item : items)
				{
					if (item.isEquipped())
					{
						condOk = true;
						break;
					}
				}
				
				return (byte) (condOk ? 1 : 2);
				
			}
			else if (template.getCheckType() == 2)
			{
				if (player.getInventory().getItemCountByItemId(template.getRequiredItemId()) < template.getEraseValue())
				{
					// You do not have enough %0 to gather.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400376, new DescriptionId(template.getRequiredItemNameId())));
					return 0;
				}
				
				return 1;
			}
		}
		
		return 2;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to gather resources.<br>
	 * It verifies that the player is not currently under a gathering cooldown.<br>
	 * If the player is restricted, it sends a system message with the remaining time.
	 * @param player The {@link Player} attempting to perform the action.
	 * @param template The {@link GatherableTemplate} being interacted with.
	 * @return {@code true} if the player can gather; {@code false} otherwise.
	 */
	private boolean checkGatherable(Player player, GatherableTemplate template)
	{
		if (player.isNotGatherable())
		{
			// You are currently unable to extract. (Time remaining: 10Min)
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400273, (int) ((player.getGatherableTimer() - (System.currentTimeMillis() - player.getStopGatherable())) / 1000)));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Finalizes the current gathering action.<br>
	 * Resets the state to {@code IDLE}.<br>
	 * Increments the total gather count.<br>
	 * Triggers {@code onDespawn} if the harvest limit is reached.
	 */
	public void completeInteraction()
	{
		state = GatherState.IDLE;
		gatherCount++;
		if (gatherCount == getOwner().getObjectTemplate().getHarvestCount())
		{
			onDespawn();
		}
	}
	
	/**
	 * Gives experience points to the player for gathering a resource.<br>
	 * This method calculates rewards based on the skill level of the object.<br>
	 * It sends a success message or an error message depending on the result.
	 * @param player The {@link Player} who is receiving the reward.
	 */
	public void rewardPlayer(Player player)
	{
		if (player != null)
		{
			final int skillLvl = getOwner().getObjectTemplate().getSkillLevel();
			final int xpReward = (int) (((0.0031 * (skillLvl + 5.3) * (skillLvl + 1592.8)) + 60));
			
			if (player.getSkillList().addSkillXp(player, getOwner().getObjectTemplate().getHarvestSkill(), (int) RewardType.GATHERING.calcReward(player, xpReward), skillLvl))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_EXTRACT_GATHERING_SUCCESS_GETEXP);
				player.getCommonData().addExp(xpReward, RewardType.GATHERING);
			}
			else
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(getOwner().getObjectTemplate().getHarvestSkill()).getNameId())));
			}
		}
	}
	
	/**
	 * Ends the gathering process for a specific player.<br>
	 * This method resets the {@code currentGatherer} to {@code 0}.<br>
	 * It also sets the gather state back to {@code GatherState.IDLE}.<br>
	 * The active {@link GatheringTask} is aborted if it was running.
	 * @param player The {@link Player} who is finishing the gathering action.
	 */
	public void finishGathering(Player player)
	{
		if (currentGatherer == player.getObjectId())
		{
			if (state == GatherState.GATHERING)
			{
				task.abort();
			}
			
			currentGatherer = 0;
			state = GatherState.IDLE;
		}
	}
	
	/**
	 * Enables protection for a player during the gathering process.<br>
	 * This method checks if {@code CraftConfig.PROTECTION_GATHER_ENABLE} is {@code true}.<br>
	 * If enabled, it hides the player and updates their visual state.
	 * @param player The {@link Player} who is starting to gather an item.
	 */
	public void startGatherProtection(Player player)
	{
		if (CraftConfig.PROTECTION_GATHER_ENABLE)
		{
			player.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
			player.setVisualState(CreatureVisualState.HIDE3);
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
		}
	}
	
	/**
	 * Removes the protection status from a player.<br>
	 * This method clears the {@code HIDE} abnormal state and updates the visual state.<br>
	 * It also broadcasts the updated state to all nearby players.
	 * @param player The {@link Player} whose protection will be stopped.
	 */
	public void stopGatherProtection(Player player)
	{
		player.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());
		player.unsetVisualState(CreatureVisualState.HIDE3);
		PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
	}
	
	/**
	 * Handles the logic when a gatherable object is despawned.<br>
	 * This method schedules a respawn task for the owner if they are not in an instance.<br>
	 * It then removes the owner from the {@link World}.
	 */
	@Override
	public void onDespawn()
	{
		final Gatherable owner = getOwner();
		if (!getOwner().isInInstance())
		{
			RespawnService.scheduleRespawnTask(owner);
		}
		
		World.getInstance().despawn(owner);
	}
	
	/**
	 * This method is called before a {@code Gatherable} object is spawned.<br>
	 * It resets the internal {@code gatherCount} to {@code 0}.
	 */
	@Override
	public void onBeforeSpawn()
	{
		gatherCount = 0;
	}
	
	/**
	 * Retrieves the {@link Gatherable} object that owns this controller.<br>
	 * This method calls the parent class implementation to fetch the owner.
	 * @return the {@code Gatherable} object associated with this controller.
	 */
	@Override
	public Gatherable getOwner()
	{
		return super.getOwner();
	}
}
