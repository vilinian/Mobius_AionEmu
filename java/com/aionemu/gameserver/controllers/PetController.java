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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles the logic and behavior for {@link Pet} objects in the game world.<br>
 * It manages pet-related actions and interactions for players.
 * @author ATracer
 */
public class PetController extends VisibleObjectController<Pet>
{
	/**
	 * This method registers a {@link Player} as an observer.<br>
	 * It creates a new {@code FlyRingObserver} for the target.<br>
	 * The observer is added to the player's observation controller.
	 * @param object The {@code VisibleObject} representing the player to observe.
	 */
	@Override
	public void see(VisibleObject object)
	{
	}
	
	/**
	 * Updates the visibility status of a specific object.<br>
	 * This method also clears the target if the object is the current target.
	 * @param object The {@code VisibleObject} that is no longer seen.
	 * @param isOutOfRange Whether the object is outside of the visible range.
	 */
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange)
	{
	}
	
	public static class PetUpdateTask implements Runnable
	{
		private final Player player;
		private long startTime = 0;
		
		public PetUpdateTask(Player player)
		{
			this.player = player;
		}
		
		@Override
		public void run()
		{
			if (startTime == 0)
			{
				startTime = System.currentTimeMillis();
			}
			
			try
			{
				final Pet pet = player.getPet();
				if (pet == null)
				{
					throw new IllegalStateException("Pet is null");
				}
				
				int currentPoints = 0;
				boolean saved = false;
				
				if (pet.getCommonData().getMoodPoints(false) < 9000)
				{
					if ((System.currentTimeMillis() - startTime) >= (60 * 1000))
					{
						currentPoints = pet.getCommonData().getMoodPoints(false);
						if (currentPoints == 9000)
						{
							PacketSendUtility.sendPacket(player, new SM_PET(pet, 4, 0));
						}
						
						DAOManager.getDAO(PlayerPetsDAO.class).savePetMoodData(pet.getCommonData());
						saved = true;
						startTime = System.currentTimeMillis();
					}
				}
				
				if (currentPoints < 9000)
				{
					PacketSendUtility.sendPacket(player, new SM_PET(pet, 4, 0));
				}
				else
				{
					PacketSendUtility.sendPacket(player, new SM_PET(pet, 3, 0));
					
					// Save if it reaches 100% after player snuggles the pet, not by the scheduler itself
					if (!saved)
					{
						DAOManager.getDAO(PlayerPetsDAO.class).savePetMoodData(pet.getCommonData());
					}
				}
			}
			catch (Exception ex)
			{
				player.getController().cancelTask(TaskId.PET_UPDATE);
			}
		}
	}
}
