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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerCollectionDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollection;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionEntry;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionInfos;
import com.aionemu.gameserver.model.templates.collection.CollectionExpTemplate;
import com.aionemu.gameserver.model.templates.collection.CollectionTemplate;
import com.aionemu.gameserver.model.templates.collection.CollectionType;
import com.aionemu.gameserver.model.templates.collection.RewardCollectionTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_COLLECTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_COLLECTION_COMPLETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_COLLECTION_FINISH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_COLLECTION_PROGRESS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_COLLECTION_REGISTER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_COLLECTION_UNK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service manages the player collection system within the game server.<br>
 * It handles logic for tracking progress, rewards, and updates for {@link PlayerCollection} objects.<br>
 * It interacts with {@link PlayerCollectionDAO} to persist data and sends relevant packets to the client.
 */
public class PlayerCollectionService
{
	private static final Logger log = LoggerFactory.getLogger(PlayerCollectionService.class);
	
	/**
	 * Initializes the {@link PlayerCollectionService}.<br>
	 * This method logs that the service has been successfully loaded.
	 */
	public void init()
	{
		log.info("[PlayerCollectionService] loaded ...");
	}
	
	/**
	 * Initializes the collection data for a {@code Player} when they log in.<br>
	 * This method loads existing collections from the database or creates new ones if none exist.<br>
	 * It also sends necessary synchronization packets and applies any completed collection rewards.
	 * @param player The {@code Player} object who is currently logging into the game.
	 */
	public void onLogin(Player player)
	{
		player.setPlayerCollection(new PlayerCollection());
		player.getPlayerCollection().setCollectionInfos(getDao().loadPlayerCollection(player));
		getDao().loadCollectionEntry(player);
		if (player.getPlayerCollection().getCollectionInfos().size() == 0)
		{
			final PlayerCollection collection = new PlayerCollection();
			collection.getCollectionInfos().put(CollectionType.COMMON, new PlayerCollectionInfos(CollectionType.COMMON, 1, 0));
			collection.getCollectionInfos().put(CollectionType.ANCIENT, new PlayerCollectionInfos(CollectionType.ANCIENT, 1, 0));
			collection.getCollectionInfos().put(CollectionType.RELIC, new PlayerCollectionInfos(CollectionType.RELIC, 1, 0));
			collection.getCollectionInfos().put(CollectionType.EVENT, new PlayerCollectionInfos(CollectionType.EVENT, 1, 0));
			player.setPlayerCollection(collection);
			for (PlayerCollectionInfos infos : player.getPlayerCollection().getCollectionInfos().values())
			{
				getDao().insertPlayerCollection(player, infos);
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION(player));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_FINISH(player));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_UNK());
		PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_PROGRESS(player));
		for (PlayerCollectionEntry complete : player.getPlayerCollection().getCompleteCollection())
		{
			complete.apply(player);
		}
		
		for (PlayerCollectionInfos infos : player.getPlayerCollection().getCollectionInfos().values())
		{
			infos.apply(player);
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out.<br>
	 * It saves the {@code Kisk} object if the player has one.<br>
	 * This ensures the binding is kept while the player is offline.
	 * @param player The {@code Player} who is logging out.
	 */
	public void onLogout(Player player)
	{
		for (PlayerCollectionEntry complete : player.getPlayerCollection().getCompleteCollection())
		{
			complete.end(player);
		}
		
		for (PlayerCollectionInfos infos : player.getPlayerCollection().getCollectionInfos().values())
		{
			infos.end(player);
		}
	}
	
	/**
	 * Registers a progress update for a player's collection.<br>
	 * This method checks if the player has the required item and updates their collection status.<br>
	 * It also handles completing the collection if all requirements are met.
	 * @param player The {@code Player} who is interacting with the collection.
	 * @param id The unique identifier for the collection type.
	 * @param index The current progress step of the collection.
	 * @param objectId The ID of the item being used to progress the collection.
	 * @param count The amount of the item to be consumed from the player's inventory.
	 */
	public void registerCollection(Player player, int id, int index, int objectId, int count)
	{
		log.info("index : " + index);
		final int matSize = DataManager.COLLECTION_TEMPLATE_DATA.getTemplate(id).getMaterials().size();
		PlayerCollectionEntry entry = null;
		if (player.getPlayerCollection().getPlayerCollectionEntry().containsKey(id))
		{
			entry = player.getPlayerCollection().getPlayerCollectionEntry().get(id);
		}
		else
		{
			entry = new PlayerCollectionEntry(id, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 0);
			getDao().insertCollection(player, entry);
			player.getPlayerCollection().getPlayerCollectionEntry().put(entry.getId(), entry);
		}
		
		final boolean delete = player.getInventory().decreaseByObjectId(objectId, count);
		
		if (delete)
		{
			entry.update(index);
			entry.setStep(entry.getStep() + 1);
			PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_REGISTER(entry));
			if (entry.getStep() == matSize)
			{
				completeCollection(player, entry);
			}
			
			getDao().updateCollection(player, entry);
		}
	}
	
	/**
	 * Marks a specific collection entry as finished for the player.<br>
	 * This method updates the database and sends completion packets to the client.<br>
	 * It also grants any rewards defined in the {@code CollectionTemplate}.<br>
	 * Finally, it applies effects and adds experience to the player.
	 * @param player The {@link Player} who is completing the collection.
	 * @param entry The specific {@link PlayerCollectionEntry} that was completed.
	 */
	public void completeCollection(Player player, PlayerCollectionEntry entry)
	{
		final CollectionTemplate template = DataManager.COLLECTION_TEMPLATE_DATA.getTemplate(entry.getId());
		entry.setComplete(true);
		getDao().updateCollection(player, entry);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION_COMPLETE(entry));
		player.getPlayerCollection().getPlayerCollectionEntry().remove(entry.getId());
		player.getPlayerCollection().getCompleteCollection().add(entry);
		if (template.getRewards() != null)
		{
			for (RewardCollectionTemplate rewards : template.getRewards())
			{
				ItemService.addItem(player, rewards.getItemId(), rewards.getCount());
			}
		}
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEMCOLLECT_COMPLETE);
		entry.apply(player);
		addCollectionExp(player, entry);
	}
	
	/**
	 * Adds experience to a specific collection for a player.<br>
	 * This method updates the progress based on the {@code PlayerCollectionEntry}.<br>
	 * It handles level up logic and saves the new data to the database.
	 * @param player The {@link Player} who owns the collection.
	 * @param entry The {@link PlayerCollectionEntry} being updated.
	 */
	public void addCollectionExp(Player player, PlayerCollectionEntry entry)
	{
		final CollectionTemplate template = DataManager.COLLECTION_TEMPLATE_DATA.getTemplate(entry.getId());
		final PlayerCollectionInfos info = player.getPlayerCollection().getCollectionInfos().get(template.getGrade());
		final int level = info.getLevel();
		final int exp = info.getExp();
		if (template.getGrade() == CollectionType.EVENT)
		{
			info.addexp();
		}
		else
		{
			final CollectionExpTemplate expTemplate = DataManager.COLLECTION_EXP_DATA.getTemplate(level + 1, template.getGrade());
			if ((exp + 1) >= expTemplate.getExp())
			{
				info.onLevelUp();
			}
			else
			{
				info.addexp();
			}
		}
		
		getDao().updatePlayerCollection(player, info);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_COLLECTION(player));
	}
	
	/**
	 * Retrieves the {@link PlayerCollectionDAO} instance.<br>
	 * This method uses {@code DAOManager} to fetch the database access object.
	 * @return The {@code PlayerCollectionDAO} used for database operations.
	 */
	public PlayerCollectionDAO getDao()
	{
		return DAOManager.getDAO(PlayerCollectionDAO.class);
	}
	
	/**
	 * Provides the global instance of the {@link PlayerCollectionService}.<br>
	 * This method follows the singleton pattern.
	 * @return The active {@code PlayerCollectionService} instance.
	 */
	public static PlayerCollectionService getInstance()
	{
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder
	{
		private static final PlayerCollectionService INSTANCE = new PlayerCollectionService();
	}
}
