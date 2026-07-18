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
package com.aionemu.gameserver.model.stats.container;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import com.aionemu.gameserver.services.LifeStatsRestoreService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the life statistics specifically for {@link Summon} entities.<br>
 * It extends {@link CreatureLifeStats} to provide specialized behavior for summons.
 * @author ATracer
 */
public class SummonLifeStats extends CreatureLifeStats<Summon>
{
	/**
	 * Creates a new {@code SummonLifeStats} instance for a specific summon.<br>
	 * This constructor initializes the stats using the current health and mana of the {@link Summon}.
	 * @param owner The {@link Summon} that owns these life statistics.
	 */
	public SummonLifeStats(Summon owner)
	{
		super(owner, owner.getGameStats().getMaxHp().getCurrent(), owner.getGameStats().getMaxMp().getCurrent());
	}
	
	/**
	 * Handles the logic when a {@code Summon} gains health.<br>
	 * This method updates the status and sends packets to both the summon and its owner.
	 * @param type The category of the health change.
	 * @param value The amount of health gained.
	 * @param skillId The ID of the skill used to gain health.
	 * @param log The log information for the action.
	 */
	@Override
	protected void onIncreaseHp(TYPE type, int value, int skillId, LOG log)
	{
		final Creature master = getOwner().getMaster();
		sendAttackStatusPacketUpdate(type, value, skillId, log);
		
		if (master instanceof Player)
		{
			PacketSendUtility.sendPacket((Player) master, new SM_SUMMON_UPDATE(getOwner()));
		}
	}
	
	/**
	 * Handles the logic when a {@code Summon} gains MP.<br>
	 * This method is called during mana restoration events.<br>
	 * It allows for custom behavior based on the skill used.
	 * @param type The category of the mana increase.
	 * @param value The amount of MP gained.
	 * @param skillId The unique identifier of the skill used.
	 * @param log The {@link LOG} object used to record the action.
	 */
	@Override
	protected void onIncreaseMp(TYPE type, int value, int skillId, LOG log)
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * This method is called when the {@code Summon} loses health.<br>
	 * It serves as a placeholder for future logic related to damage.
	 */
	@Override
	protected void onReduceHp()
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * This method is called when the {@code Mp} of an NPC decreases.<br>
	 * It serves as a placeholder for any logic needed during mana reduction.
	 */
	@Override
	protected void onReduceMp()
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * Retrieves the owner of this summon.<br>
	 * This method returns the {@link Summon} object associated with the AI.
	 * @return The {@code Summon} that owns this entity.
	 */
	@Override
	public Summon getOwner()
	{
		return super.getOwner();
	}
	
	/**
	 * Starts a background task to restore life stats.<br>
	 * This method checks if the {@code lifeRestoreTask} is {@code null}.<br>
	 * It also ensures the creature is not already dead before scheduling.<br>
	 * The task is managed by the {@link LifeStatsRestoreService} class.
	 */
	@Override
	public void triggerRestoreTask()
	{
		restoreLock.lock();
		try
		{
			if ((lifeRestoreTask == null) && !alreadyDead)
			{
				lifeRestoreTask = LifeStatsRestoreService.getInstance().scheduleHpRestoreTask(this);
			}
		}
		finally
		{
			restoreLock.unlock();
		}
	}
}
