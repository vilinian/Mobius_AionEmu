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
package com.aionemu.gameserver.services.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dao.PlayerLumielDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.LumielTransform;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.lumiel_transform.LumielMaterialTemplate;
import com.aionemu.gameserver.model.templates.lumiel_transform.LumielRewardItem;
import com.aionemu.gameserver.model.templates.lumiel_transform.LumielTransformReward;
import com.aionemu.gameserver.model.templates.lumiel_transform.LumielTransformTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUMIEL_TRANSFORM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUMIEL_TRANSFORM_EXP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUMIEL_TRANSFORM_REWARD;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUMIEL_TRANSFORM_REWARD_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for the {@link LumielTransform} system.<br>
 * It manages transformations, rewards, and related data for players.<br>
 * It interacts with {@link PlayerLumielDAO} to persist transformation progress.
 */
public class LumielTransformService
{
	private static final Logger log = LoggerFactory.getLogger(LumielTransformService.class);
	private final List<LumielTransformTemplate> activeLumiel = new ArrayList<>();
	private static Map<Integer, List<LumielRewardItem>> playerRewards = new ConcurrentHashMap<>();
	
	/**
	 * Initializes the {@code LumielTransformService}.<br>
	 * It logs a start message to the server console.<br>
	 * It calls the {@code generateActiveLumiel} method to populate data.
	 */
	public void init()
	{
		log.info("Lumiel Transform Service Start");
		generateActiveLumiel();
	}
	
	/**
	 * Populates the list of currently active Lumiel transforms.<br>
	 * This method scans all templates in {@code LUMIEL_TEMPLATE_DATA}.<br>
	 * It adds every template where {@code isActivate()} returns {@code true} to the internal list.
	 */
	public void generateActiveLumiel()
	{
		for (LumielTransformTemplate template : DataManager.LUMIEL_TEMPLATE_DATA.getAllTemplates().values())
		{
			if (!template.isActivate())
			{
				continue;
			}
			
			activeLumiel.add(template);
		}
		
		log.info("Lumiel Template actived size : " + activeLumiel.size());
	}
	
	/**
	 * Awards points to a player based on the materials they provide.<br>
	 * This method calculates the total score from the {@code matrials} map.<br>
	 * It then updates the player's Lumiel transform and sends an experience packet.
	 * @param player The {@link Player} receiving the reward.
	 * @param lumielId The unique identifier for the Lumiel transform.
	 * @param matrials A map containing item IDs and their corresponding quantities.
	 */
	public void onRewardPoints(Player player, int lumielId, Map<Integer, Long> matrials)
	{
		long finalScore = 0;
		final LumielTransform lumielTransform = player.getPlayerLumiel().get(lumielId);
		for (Map.Entry<Integer, Long> mats : matrials.entrySet())
		{
			final Item mat = player.getInventory().getItemByObjId(mats.getKey());
			final Long count = mats.getValue();
			final LumielMaterialTemplate materialTemplate = DataManager.LUMIEL_MATERIAL_DATA.getTemplate(lumielId, mat.getItemId());
			finalScore += materialTemplate.getPoint() * count;
			player.getInventory().decreaseByItemId(mat.getItemId(), count);
		}
		
		lumielTransform.setPoints(lumielTransform.getPoints() + finalScore);
		getDao().updateLumielTransform(player, lumielTransform);
		PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRANSFORM_EXP(lumielId, finalScore));
	}
	
	/**
	 * Generates rewards for a player based on a specific Lumiel transformation.<br>
	 * This method selects random items from the template and stores them in {@code playerRewards}.<br>
	 * It also sends a {@link com.aionemu.gameserver.network.aion.serverpackets.SM_LUMIEL_TRANSFORM_REWARD_LIST} packet to the player.
	 * @param player The {@link Player} who will receive the rewards.
	 * @param lumielId The unique identifier for the Lumiel transformation template.
	 */
	public void onGenerateReward(Player player, int lumielId)
	{
		final LumielTransformTemplate template = DataManager.LUMIEL_TEMPLATE_DATA.getTemplate(lumielId);
		final List<LumielRewardItem> rewardItems = new ArrayList<>();
		for (LumielTransformReward reward : template.getLumielTransformRewards())
		{
			final int index = Rnd.get(0, reward.getLumielTransformRewards().size() - 1);
			final LumielRewardItem item = reward.getLumielTransformRewards().get(index);
			rewardItems.add(item);
		}
		
		playerRewards.put(player.getObjectId(), rewardItems);
		PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRANSFORM_REWARD_LIST(lumielId, rewardItems));
	}
	
	/**
	 * Sends the {@code SM_LUMIEL_TRANSFORM} packet to a specific player.<br>
	 * This method updates the client with the current Lumiel transformation data.
	 * @param player The {@link Player} object to receive the packet.
	 */
	public void sendLumielPacket(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRANSFORM(player));
	}
	
	/**
	 * Grants a random reward to the player for completing a Lumiel transform.<br>
	 * This method checks if the inventory is full before proceeding.<br>
	 * It updates the player's points and saves the progress to the database.
	 * @param player The {@code Player} receiving the reward.
	 * @param lumielId The unique identifier for the {@code LumielTransform}.
	 */
	public void onRewardPlayer(Player player, int lumielId)
	{
		if (player.getInventory().isFull())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REINVENT_MSG_NOT_ENOUGH_INVENTORY);
			return;
		}
		
		final List<LumielRewardItem> rewards = getPlayerRewardList(player);
		final LumielTransform lumielTransform = player.getPlayerLumiel().get(lumielId);
		final int index = Rnd.get(0, rewards.size() - 1);
		final LumielRewardItem reward = rewards.get(index);
		PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRANSFORM_REWARD(lumielId, reward));
		ItemService.addItem(player, reward.getItemId(), reward.getCount());
		lumielTransform.setPoints(0);
		getDao().updateLumielTransform(player, lumielTransform);
		PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRANSFORM(player));
		playerRewards.remove(player.getObjectId());
	}
	
	/**
	 * Retrieves the list of reward items for a specific player.<br>
	 * This method searches the {@code playerRewards} map using the unique object ID of the {@link Player}.<br>
	 * It returns all {@link LumielRewardItem} objects associated with that player.
	 * @param player The {@link Player} whose rewards need to be retrieved.
	 * @return A {@code List} containing the reward items for the given player.
	 */
	public List<LumielRewardItem> getPlayerRewardList(Player player)
	{
		final List<LumielRewardItem> rewards = new ArrayList<>();
		for (Map.Entry<Integer, List<LumielRewardItem>> playerReward : playerRewards.entrySet())
		{
			if (playerReward.getKey() != player.getObjectId())
			{
				continue;
			}
			
			for (LumielRewardItem item : playerReward.getValue())
			{
				rewards.add(item);
			}
		}
		
		return rewards;
	}
	
	/**
	 * Handles the logic for checking and updating a player's passport status when they log in.<br>
	 * This method verifies if the player has an active passport or needs to be assigned one.<br>
	 * It also checks for daily stamp resets and sends the appropriate server packets to the {@code Player}.
	 * @param player The {@code Player} object who is currently logging into the game.
	 */
	public void onLogin(Player player)
	{
		player.setPlayerLumiel(getDao().loadPlayerLumiel(player));
		if (player.getPlayerLumiel().size() == 0)
		{
			for (LumielTransformTemplate template : activeLumiel)
			{
				final LumielTransform lumiel = new LumielTransform(template.getId(), 0);
				getDao().addPlayerLumiel(player, lumiel);
				player.getPlayerLumiel().put(lumiel.getId(), lumiel);
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_LUMIEL_TRANSFORM(player));
	}
	
	/**
	 * Retrieves the {@link PlayerLumielDAO} instance.<br>
	 * This method uses {@link DAOManager} to fetch the correct data access object.
	 * @return The {@code PlayerLumielDAO} used for database operations.
	 */
	public PlayerLumielDAO getDao()
	{
		return (PlayerLumielDAO) DAOManager.getDAO(PlayerLumielDAO.class);
	}
	
	/**
	 * Provides the singleton instance of the {@link LumielTransformService}.<br>
	 * Use this method to access the global service for managing Lumiel transformations.
	 * @return The active {@code LumielTransformService} instance.
	 */
	public static LumielTransformService getInstance()
	{
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder
	{
		private static final LumielTransformService INSTANCE = new LumielTransformService();
	}
}
