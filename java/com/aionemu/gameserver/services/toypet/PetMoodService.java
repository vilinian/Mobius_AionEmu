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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Manages the mood states and behaviors of {@link Pet} objects.<br>
 * It handles logic for updating pet emotions based on various game interactions.
 * @author ATracer
 */
public class PetMoodService
{
	private static final Logger log = LoggerFactory.getLogger(PetMoodService.class);
	
	/**
	 * Validates and updates the mood of a {@link Pet}.<br>
	 * This method handles different actions based on the provided {@code type}.
	 * @param pet The {@code Pet} object to check.
	 * @param type The action type used to determine which logic to execute.
	 * @param shuggleEmotion The specific emotion value for interaction.
	 */
	public static void checkMood(Pet pet, int type, int shuggleEmotion)
	{
		switch (type)
		{
			case 0:
				startCheckingMood(pet);
				break;
			case 1:
				interactWithPet(pet, shuggleEmotion);
				break;
			case 3:
				requestPresent(pet);
				break;
		}
	}
	
	/**
	 * Handles the logic for a player giving a present to their {@link Pet}.<br>
	 * It checks if the pet has enough mood points and is not on cooldown.<br>
	 * It also verifies that the player's inventory is not full before awarding an item.
	 * @param pet The {@code Pet} object involved in the transaction.
	 */
	private static void requestPresent(Pet pet)
	{
		if (pet.getCommonData().getMoodPoints(false) < 9000)
		{
			log.warn("Requested present before mood fill up: {}", pet.getMaster().getName());
			return;
		}
		
		if (pet.getCommonData().getGiftRemainingTime() > 0)
		{
			AuditLogger.info(pet.getMaster(), "Trying to get gift during CD for pet " + pet.getPetId());
			return;
		}
		
		if (pet.getMaster().getInventory().isFull())
		{
			PacketSendUtility.sendPacket(pet.getMaster(), SM_SYSTEM_MESSAGE.STR_WAREHOUSE_FULL_INVENTORY);
			return;
		}
		
		pet.getCommonData().clearMoodStatistics();
		PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 4, 0));
		PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 3, 0));
		final int itemId = pet.getPetTemplate().getConditionReward();
		if (itemId != 0)
		{
			ItemService.addItem(pet.getMaster(), pet.getPetTemplate().getConditionReward(), 1);
		}
	}
	
	/**
	 * Handles the interaction logic between a player and their pet.<br>
	 * It updates the shuggle counter for the {@code Pet}.<br>
	 * If successful, it sends an {@link SM_PET} packet to the master.
	 * @param pet The {@code Pet} object to interact with.
	 * @param shuggleEmotion The emotion value to send to the player.
	 */
	private static void interactWithPet(Pet pet, int shuggleEmotion)
	{
		if (pet.getCommonData() != null)
		{
			if (pet.getCommonData().increaseShuggleCounter())
			{
				PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 2, shuggleEmotion));
				PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 4, 0)); // Update progress immediately
			}
		}
	}
	
	/**
	 * Starts the process of checking a {@link Pet}'s mood.<br>
	 * It sends an {@code SM_PET} packet to the pet's master.
	 * @param pet The {@code Pet} object to check.
	 */
	private static void startCheckingMood(Pet pet)
	{
		PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 0, 0));
	}
}
