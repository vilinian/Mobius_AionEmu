/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 * <p/>
 * Aion-Lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * Aion-Lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. *
 * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.pets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.pet.PetBonusAttr;
import com.aionemu.gameserver.model.templates.pet.PetFunctionType;
import com.aionemu.gameserver.model.templates.pet.PetPenaltyAttr;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Represents a status effect or bonus provided by a {@link Pet} to its owner.<br>
 * This class manages the application of various statistics and attributes to a {@link Player}.
 * @author Ace on 01/08/2016
 */
public class PetBuff implements StatOwner
{
	private final List<IStatFunction> functions = new ArrayList<>();
	private final PetBonusAttr petBonusAttr;
	private long startTime;
	private ScheduledFuture<?> task = null;
	
	/**
	 * Creates a new {@link PetBuff} instance based on a specific ID.<br>
	 * This method retrieves the corresponding attributes from the data manager.
	 * @param buffId The unique identifier for the pet buff.
	 */
	public PetBuff(int buffId)
	{
		petBonusAttr = DataManager.PET_BUFF_DATA.getPetBonusattr(buffId);
	}
	
	/**
	 * Applies the instance buff effects to a specific {@link Player}.<br>
	 * This method calculates penalties and adds them to the player's stats.<br>
	 * It also schedules a task if the provided {@code time} is not {@code 0}.
	 * @param player The {@link Player} who will receive the buff effect.
	 * @param time The duration in milliseconds for the scheduled task.
	 */
	public void applyEffect(Player player, int time)
	{
		if (hasPetBuff() || (petBonusAttr == null))
		{
			return;
		}
		
		if (time != 0)
		{
			task = ThreadPoolManager.getInstance().schedule(new PetBuffTask(player), time);
		}
		
		startTime = System.currentTimeMillis();
		for (PetPenaltyAttr petPenaltyAttr : petBonusAttr.getPenaltyAttr())
		{
			final StatEnum stat = petPenaltyAttr.getStat();
			final int statToModified = player.getGameStats().getStat(stat, 0).getBase();
			final int value = petPenaltyAttr.getValue();
			final int valueModified = petPenaltyAttr.getFunc().equals(Func.PERCENT) ? ((statToModified * value) / 100) : (value);
			functions.add(new StatAddFunction(stat, valueModified, true));
		}
		
		player.getGameStats().addEffect(this, functions);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_BUFF_PET_USE_START_MESSAGE);
		PacketSendUtility.sendPacket(player, new SM_PET(true, 0, 0)); // start buffing
	}
	
	/**
	 * Handles the recurring logic for a pet buff effect.<br>
	 * It schedules a {@code PetBuffTask} if the duration is not {@code 0}.<br>
	 * It also sends an {@link com.aionemu.gameserver.network.aion.serverpackets.SM_PET} packet to the player.
	 * @param player The {@link Player} who receives the buff effect.
	 * @param time The duration in milliseconds for the scheduled task.
	 */
	private void loopEffect(Player player, int time)
	{
		if (time != 0)
		{
			task = ThreadPoolManager.getInstance().schedule(new PetBuffTask(player), time);
		}
		
		PacketSendUtility.sendPacket(player, new SM_PET(true, 0, 0)); // start buffing
	}
	
	/**
	 * Removes the effect from the specified {@link Player}.<br>
	 * This method clears all internal functions.<br>
	 * It also notifies the player's game stats to stop the effect.
	 * @param player The {@code Player} who currently has this effect applied.
	 */
	public void endEffect(Player player)
	{
		// Test for http://falke34.bplaced.net/forums/showthread.php?tid=2977
		functions.clear();
		if (task != null)
		{
			task.cancel(false);
			task = null;
			player.getGameStats().endEffect(this);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_BUFF_PET_USE_STOP_MESSAGE);
			PacketSendUtility.sendPacket(player, new SM_PET(false, 0, 0)); // stop buffing
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		}
		else
		{
			return;
		}
	}
	
	/**
	 * Calculates the time remaining for this buff.<br>
	 * It returns the difference between the current time and the start time.<br>
	 * The result is converted from milliseconds to seconds.
	 * @return The number of seconds left for the buff effect.
	 */
	public int getBuffRemaningTime()
	{
		return (int) ((System.currentTimeMillis() - startTime) / 1000);
	}
	
	private class PetBuffTask implements Runnable
	{
		private final Player player;
		
		public PetBuffTask(Player player)
		{
			this.player = player;
		}
		
		@Override
		public void run()
		{
			final Pet pet = player.getPet();
			final PetTemplate petTemp = DataManager.PET_DATA.getPetTemplate(pet.getPetId());
			final PetBonusAttr petBuff = DataManager.PET_BUFF_DATA.getPetBonusattr(petTemp.getPetFunction(PetFunctionType.BUFF).getId());
			
			if ((task != null) && (player.getInventory().getItemCountByItemId(182007162) >= petBuff.getFoodCount()))
			{
				player.getInventory().decreaseByItemId(182007162, petBuff.getFoodCount());
				loopEffect(player, 300000);
			}
			else
			{
				endEffect(player);
			}
		}
	}
	
	/**
	 * Checks if the pet buff is currently active.<br>
	 * It verifies that the internal task is not {@code null}.<br>
	 * It also checks that the task has not finished yet.
	 * @return {@code true} if the buff is still running, otherwise {@code false}.
	 */
	public boolean hasPetBuff()
	{
		return (task != null) && !task.isDone();
	}
	
}
