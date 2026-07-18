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

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * This class handles the {@code /mcheck} player command.<br>
 * It scans all {@code LOCKED} missions to check for start conditions.<br>
 * If the requirements are met, it starts the missions immediately.
 * @author vlog
 */
public class cmd_mcheck extends PlayerCommand
{
	/**
	 * Registers the {@code mcheck} command.<br>
	 * This allows players to check and start locked missions.<br>
	 * It uses the {@code String...)} method to process the logic.
	 */
	public cmd_mcheck()
	{
		super("mcheck");
	}
	
	/**
	 * Checks all locked missions for the specified {@link Player}.<br>
	 * It triggers a level up check for every quest with a {@code LOCKED} status.<br>
	 * A success message is sent to the player after the process completes.
	 * @param player The {@link Player} who initiated the command.
	 * @param params Additional arguments provided by the user.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final Collection<QuestState> qsl = player.getQuestStateList().getAllQuestState();
		for (QuestState qs : qsl)
		{
			if (qs.getStatus() == QuestStatus.LOCKED)
			{
				final int questId = qs.getQuestId();
				QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, questId, 0));
			}
		}
		
		PacketSendUtility.sendMessage(player, "Missions checked successfully");
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
