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
package system.handlers.playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the player command to restart a specific quest.<br>
 * It resets the {@code QuestState} for the target quest and updates the player's status.<br>
 * This allows players to re-attempt quests that have already been completed or failed.
 * @author ginho1
 */
public class cmd_questrestart extends PlayerCommand
{
	/**
	 * Registers the {@code questrestart} command.<br>
	 * This allows players to restart their quests via the chat system.
	 */
	public cmd_questrestart()
	{
		super("questrestart");
	}
	
	private int id;
	
	/**
	 * Restarts a specific quest for the player.<br>
	 * It takes a quest ID from the {@code params} array to identify which quest to reset.<br>
	 * The method checks if the quest is eligible for restarting based on its current status.
	 * @param player The player who is executing the command.
	 * @param params Variable arguments where the first element must be a valid quest ID.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "syntax .questrestart <quest id>");
			return;
		}
		
		try
		{
			id = Integer.valueOf(params[0]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "syntax .questrestart <quest id>");
			return;
		}
		
		final QuestState qs = player.getQuestStateList().getQuestState(id);
		
		if ((qs == null) || (id == 1006) || (id == 2008) || (id == 10021) || (id == 20021) || (id == 18602) || (id == 28602))
		{
			PacketSendUtility.sendMessage(player, "Quest [quest: " + id + "] can't be restarted.");
			return;
		}
		
		if ((qs.getStatus() == QuestStatus.START) || (qs.getStatus() == QuestStatus.REWARD))
		{
			if (qs.getQuestVarById(0) != 0)
			{
				qs.setStatus(QuestStatus.START);
				qs.setQuestVar(0);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				PacketSendUtility.sendMessage(player, "Quest [quest: " + id + "] restarted.");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "Quest [quest: " + id + "] can't be restarted.");
			}
		}
		else
		{
			PacketSendUtility.sendMessage(player, "Quest [quest: " + id + "] can't be restarted.");
		}
	}
	
	/**
	 * Handles the failure of an {@code execute} command.<br>
	 * It sends a syntax hint to the player.
	 * @param player The {@code Player} who attempted the command.
	 * @param message The error message associated with the failure.
	 */
	@Override
	public void onFail(Player player, String message)
	{
		// TODO Auto-generated method stub
	}
}
