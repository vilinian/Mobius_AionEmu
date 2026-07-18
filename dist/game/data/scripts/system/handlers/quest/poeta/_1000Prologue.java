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
package system.handlers.quest.poeta;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles the logic for the {@code 1000Prologue} quest.<br>
 * It manages quest progression and triggers specific events for players.
 * @author MrPoke
 * @rework FrozenKiller
 */
public class _1000Prologue extends QuestHandler
{
	private final static int questId = 1000;
	
	/**
	 * Initializes the quest handler for quest {@code 1000}.<br>
	 * This constructor calls the superclass to register the quest ID.
	 */
	public _1000Prologue()
	{
		super(questId);
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		qe.registerOnEnterWorld(questId);
		qe.registerOnMovieEndQuest(1, questId);
	}
	
	/**
	 * Checks if a player should start the quest when entering a specific world.<br>
	 * This method triggers for players who do not have the quest active.<br>
	 * It uses {@code startQuest} to begin the quest.
	 * @param env The current quest environment containing player data.
	 * @return {@code true} if the quest was successfully started, otherwise {@code false}.
	 */
	@Override
	public boolean onEnterWorldEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) && (player.getRace() == Race.ELYOS))
		{
			if (QuestService.startQuest(env))
			{
				PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(1, 1));
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Handles the logic that occurs when a movie finishes playing.<br>
	 * It checks if the player should receive a reward based on the {@code movieId}.<br>
	 * This method is triggered by the quest engine to progress the story.
	 * @param env The current quest environment containing player data.
	 * @param movieId The unique identifier of the movie that just ended.
	 * @return {@code true} if the quest was successfully completed, otherwise {@code false}.
	 */
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() != QuestStatus.START))
		{
			return false;
		}
		
		if ((movieId == 1) && (player.getRace() == Race.ELYOS))
		{
			qs.setStatus(QuestStatus.REWARD);
			QuestService.finishQuest(env);
			return true;
		}
		
		return false;
	}
}
