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
package com.aionemu.gameserver.services.toypet;

import java.sql.Timestamp;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.PeriodicSaveConfig;
import com.aionemu.gameserver.controllers.PetController;
import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.templates.pet.PetDopingBag;
import com.aionemu.gameserver.model.templates.pet.PetFunction;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import com.aionemu.gameserver.services.MinionService;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the spawning and management of toy pets in the game world.<br>
 * This service manages pet creation, visibility, and synchronization for {@link Player} objects.
 * @author ATracer
 */
public class PetSpawnService
{
	/**
	 * Summons a specific pet for the given {@link Player}.<br>
	 * This method handles despawning existing pets and updating mood statistics.<br>
	 * It uses {@code int)} to create the new pet.
	 * @param player The {@link Player} who is summoning the pet.
	 * @param petId The unique identifier for the pet to be summoned.
	 * @param isManualSpawn A boolean flag indicating if the spawn was triggered manually.
	 */
	public static void summonPet(Player player, int petId, boolean isManualSpawn)
	{
		if (player.getMinion() != null)
		{
			MinionService.getInstance().despawnMinion(player, player.getMinion().getObjectId());
		}
		
		PetCommonData lastPetCommonData;
		
		if (player.getPet() != null)
		{
			if (player.getPet().getPetId() == petId)
			{
				PacketSendUtility.broadcastPacket(player, new SM_PET(3, player.getPet()), true);
				return;
			}
			
			lastPetCommonData = player.getPet().getCommonData();
			dismissPet(player, isManualSpawn);
		}
		else
		{
			lastPetCommonData = player.getPetList().getLastUsedPet();
		}
		
		if (lastPetCommonData != null)
		{
			// reset mood if other pet is spawned
			if (petId != lastPetCommonData.getPetId())
			{
				lastPetCommonData.clearMoodStatistics();
			}
		}
		
		player.getController().addTask(TaskId.PET_UPDATE, ThreadPoolManager.getInstance().scheduleAtFixedRate(new PetController.PetUpdateTask(player), PeriodicSaveConfig.PLAYER_PETS * 1000, PeriodicSaveConfig.PLAYER_PETS * 1000));
		
		final Pet pet = VisibleObjectSpawner.spawnPet(player, petId);
		
		// It means serious error or cheater - why its just nothing say "null"?
		if (pet != null)
		{
			sendWhInfo(player, petId);
			
			if ((System.currentTimeMillis() - pet.getCommonData().getDespawnTime().getTime()) > (10 * 60 * 1000))
			{
				// reset mood if pet was despawned for longer than 10 mins.
				player.getPet().getCommonData().clearMoodStatistics();
			}
			
			lastPetCommonData = pet.getCommonData();
			player.getPetList().setLastUsedPetId(petId);
		}
	}
	
	/**
	 * Sends warehouse information to a specific player.<br>
	 * This method checks if the pet has an associated storage location.<br>
	 * It sends {@link SM_WAREHOUSE_INFO} packets based on that location.
	 * @param player The {@code Player} who will receive the packet.
	 * @param petId The unique identifier for the pet being checked.
	 */
	private static void sendWhInfo(Player player, int petId)
	{
		final PetTemplate petTemplate = DataManager.PET_DATA.getPetTemplate(petId);
		final PetFunction pf = petTemplate.getWarehouseFunction();
		if ((pf != null) && (pf.getSlots() != 0))
		{
			final int itemLocation = StorageType.getStorageId(pf.getSlots(), 6);
			if (itemLocation != -1)
			{
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(itemLocation).getItemsWithKinah(), itemLocation, 0, true, player));
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, itemLocation, 0, false, player));
			}
		}
	}
	
	/**
	 * Removes the current pet from a {@link Player}.<br>
	 * This method saves the pet's progress and mood data before deletion.<br>
	 * It also cancels any active pet update tasks.
	 * @param player The {@link Player} who owns the pet to be dismissed.
	 * @param isManualDespawn Set to {@code true} if the despawn was triggered by a manual action.
	 */
	public static void dismissPet(Player player, boolean isManualDespawn)
	{
		final Pet toyPet = player.getPet();
		if (toyPet != null)
		{
			final PetFeedProgress progress = toyPet.getCommonData().getFeedProgress();
			if (progress != null)
			{
				toyPet.getCommonData().setCancelFeed(true);
				DAOManager.getDAO(PlayerPetsDAO.class).saveFeedStatus(player, toyPet.getPetId(), progress.getHungryLevel().getValue(), progress.getDataForPacket(), toyPet.getCommonData().getRefeedTime());
			}
			
			final PetDopingBag bag = toyPet.getCommonData().getDopingBag();
			if ((bag != null) && bag.isDirty())
			{
				DAOManager.getDAO(PlayerPetsDAO.class).saveDopingBag(player, toyPet.getPetId(), bag);
			}
			
			player.getController().cancelTask(TaskId.PET_UPDATE);
			
			// TODO needs for pet teleportation
			if (isManualDespawn)
			{
				toyPet.getCommonData().setDespawnTime(new Timestamp(System.currentTimeMillis()));
			}
			
			toyPet.getCommonData().savePetMoodData();
			
			player.setToyPet(null);
			toyPet.getController().delete();
		}
		
	}
}
