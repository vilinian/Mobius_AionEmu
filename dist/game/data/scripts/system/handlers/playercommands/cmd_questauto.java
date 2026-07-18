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

import java.util.Arrays;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /questauto} player command.<br>
 * This class allows players to automate quest progression by automatically completing requirements.<br>
 * It interacts with the {@link QuestState} and sends {@link SM_QUEST_ACTION} packets to the client.
 * @author ATracer
 */
public class cmd_questauto extends PlayerCommand
{
	/**
	 * put quests for automation here (new int[]{1245,1345,7895})
	 */
	private final int[] questIds = new int[] {};
	
	/**
	 * Initializes the {@link cmd_questauto} command.<br>
	 * This command allows players to automate specific quests.<br>
	 * It registers the command name as {@code questauto}.
	 */
	public cmd_questauto()
	{
		super("questauto");
	}
	
	/**
	 * Executes the command to automatically complete a quest.<br>
	 * It checks if the provided {@code questId} is supported and currently started.<br>
	 * If valid, it updates the quest status to {@code REWARD}.
	 * @param player The {@link Player} executing the command.
	 * @param params Variable arguments where the first value must be a valid quest ID.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "syntax .questauto <questid>");
			return;
		}
		
		final int questId;
		try
		{
			questId = Integer.parseInt(params[0]);
		}
		catch (Exception ex)
		{
			PacketSendUtility.sendMessage(player, "wrong quest id");
			return;
		}
		
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() != QuestStatus.START))
		{
			PacketSendUtility.sendMessage(player, "quest is not started");
			return;
		}
		
		if (!Arrays.stream(questIds).anyMatch(e -> e == questId))
		{
			PacketSendUtility.sendMessage(player, "this quest is not supported");
			return;
		}
		
		qs.setStatus(QuestStatus.REWARD);
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
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
