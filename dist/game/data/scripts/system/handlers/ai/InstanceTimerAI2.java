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
package system.handlers.ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerFilters.SameInstanceFilter;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for an NPC that manages instance timers.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific behavior for timed events.
 * @author xTz
 */
@AIName("instancetimer")
public class InstanceTimerAI2 extends AggressiveNpcAI2
{
	private boolean isInTimer = false;
	private long curentTime;
	
	/**
	 * Starts the instance timer for a specific creature.<br>
	 * This method identifies the player and sends the initial time packet.<br>
	 * It sets {@code isInTimer} to {@code true} and records the start time.
	 * @param creature The {@link Creature} that triggered the timer.
	 */
	protected void setInstanceTimer(Creature creature)
	{
		int time = 0;
		switch (getNpcId())
		{
			case 215222:
			case 215221:
			case 215179:
			case 215178:
			case 215136:
			case 215135:
			case 233719:
			case 233676:
			case 233633:
				time = 600000;
				break;
		}
		
		Player player;
		if (creature instanceof Player)
		{
			player = (Player) creature;
		}
		else if (creature instanceof Summon)
		{
			player = (Player) creature.getMaster();
		}
		else
		{
			return;
		}
		
		isInTimer = true;
		curentTime = System.currentTimeMillis();
		sendTime(player, time);
	}
	
	/**
	 * Sends a time update packet to a specific player.<br>
	 * It checks if the {@code player} is in a team to determine the broadcast scope.<br>
	 * The {@code time} value is divided by 1000 before being sent.
	 * @param player The {@link Player} who will receive the message.
	 * @param time The remaining time in milliseconds.
	 */
	private void sendTime(Player player, int time)
	{
		if (player.isInTeam())
		{
			player.getCurrentTeam().sendPacket(new SM_QUEST_ACTION(0, time / 1000), new SameInstanceFilter(player));
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, time / 1000));
		}
	}
	
	/**
	 * Processes the logic for when this AI is attacked by a {@code Creature}.<br>
	 * It checks if the attacker is within 40 units of the owner.<br>
	 * If close enough, it calculates a path to move away from the attacker.<br>
	 * The owner will then move toward the nearest valid collision point.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		if (!isInTimer)
		{
			setInstanceTimer(creature);
		}
	}
	
	/**
	 * Calculates the amount of time left in the current instance.<br>
	 * It compares the current system time against the stored {@code curentTime}.<br>
	 * The result is capped at a maximum of 600,000 milliseconds.
	 * @return The remaining time in milliseconds as a {@code long}.
	 */
	@Override
	public long getRemainigTime()
	{
		final long time = System.currentTimeMillis() - curentTime;
		if (time < 0)
		{
			return 0;
		}
		
		return time > 600000 ? 0 : time;
	}
}
