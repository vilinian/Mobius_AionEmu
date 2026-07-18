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
package com.aionemu.gameserver.model.gameobjects.player.npcFaction;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.factions.FactionCategory;
import com.aionemu.gameserver.model.templates.factions.NpcFactionTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestMentorType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the faction relationships between players and {@link Npc} entities.<br>
 * It handles how player actions affect their reputation with different NPC groups.
 * @author MrPoke
 * @modified synchro2
 */
public class NpcFactions
{
	private final Player owner;
	private final Map<Integer, NpcFaction> factions = new HashMap<>();
	private final NpcFaction[] activeNpcFaction = new NpcFaction[2];
	private final int[] timeLimit = new int[]
	{
		0,
		0
	};
	
	/**
	 * Creates a new instance of {@code NpcFactions}.<br>
	 * This constructor links the faction data to a specific {@link Player}.
	 * @param owner The {@code Player} who owns these factions.
	 */
	public NpcFactions(Player owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Adds a new {@link NpcFaction} to the player's list of factions.<br>
	 * This method registers the faction and updates its active status.<br>
	 * It also handles time limits for completed quests.
	 * @param faction The {@code NpcFaction} object to be added.
	 */
	public void addNpcFaction(NpcFaction faction)
	{
		factions.put(faction.getId(), faction);
		int type = 0;
		if (faction.isMentor())
		{
			type = 1;
		}
		
		if (faction.isActive())
		{
			activeNpcFaction[type] = faction;
		}
		
		if (faction.getTime() == -1)
		{
			// used to reset from quest daily command
			faction.setTime((int) (System.currentTimeMillis() / 1000));
			timeLimit[type] = faction.getTime();
		}
		else if ((timeLimit[type] < faction.getTime()) && (faction.getState() == ENpcFactionQuestState.COMPLETE))
		{
			timeLimit[type] = faction.getTime();
		}
	}
	
	/**
	 * Retrieves a specific {@link NpcFaction} using its unique identifier.<br>
	 * This method looks up the faction in the internal collection.
	 * @param id The unique integer ID of the faction to find.
	 * @return The {@code NpcFaction} object associated with the given ID, or {@code null} if not found.
	 */
	public NpcFaction getNpcFactinById(int id)
	{
		return factions.get(id);
	}
	
	/**
	 * Retrieves all the {@link NpcFaction} objects associated with this owner.<br>
	 * This method returns a collection of all currently registered factions.
	 * @return A {@code Collection} containing all {@code NpcFaction} instances.
	 */
	public Collection<NpcFaction> getNpcFactions()
	{
		return factions.values();
	}
	
	/**
	 * Retrieves the currently active {@link NpcFaction} for the player.<br>
	 * It selects different factions based on whether the mentor status is active.
	 * @param mentor A boolean indicating if the faction should be retrieved for a mentor.
	 * @return The {@code NpcFaction} object corresponding to the requested state.
	 */
	public NpcFaction getActiveNpcFaction(boolean mentor)
	{
		if (mentor)
		{
			return activeNpcFaction[1];
		}
		
		return activeNpcFaction[0];
	}
	
	/**
	 * Sets the active {@link NpcFaction} based on the provided ID.<br>
	 * If the faction does not exist, it creates a new one.<br>
	 * The method updates the internal active faction array.
	 * @param npcFactionId The unique identifier for the faction to activate.
	 * @return The {@code NpcFaction} object that was activated.
	 */
	public NpcFaction setActive(int npcFactionId)
	{
		NpcFaction npcFaction = factions.get(npcFactionId);
		if (npcFaction == null)
		{
			npcFaction = new NpcFaction(npcFactionId, 0, false, ENpcFactionQuestState.NOTING, 0);
			factions.put(npcFactionId, npcFaction);
		}
		
		npcFaction.setActive(true);
		if (npcFaction.isMentor())
		{
			activeNpcFaction[1] = npcFaction;
		}
		else
		{
			activeNpcFaction[0] = npcFaction;
		}
		
		return npcFaction;
	}
	
	/**
	 * Removes the player from a specific {@link Npc} faction.<br>
	 * This method checks if the {@code Npc} belongs to a valid active faction.<br>
	 * If successful, it triggers the internal logic to leave that faction.
	 * @param npc The {@link Npc} object associated with the faction to leave.
	 */
	public void leaveNpcFaction(Npc npc)
	{
		final int targetObjectId = npc.getObjectId();
		final NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionByNpcId(npc.getNpcId());
		if (npcFactionTemplate == null)
		{
			return;
		}
		
		final NpcFaction npcFaction = getNpcFactinById(npcFactionTemplate.getId());
		if ((npcFaction == null) || !npcFaction.isActive())
		{
			PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1438));
			return;
		}
		
		PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1353));
		leaveNpcFaction(npcFaction);
	}
	
	/**
	 * Removes the player from a specific {@code NpcFaction}.<br>
	 * It sends a system message to the {@code owner}.<br>
	 * The faction is set to inactive and cleared from active slots.<br>
	 * If a quest was started, it is abandoned via {@code abandonQuest}.
	 * @param npcFaction The {@code NpcFaction} object to leave.
	 */
	private void leaveNpcFaction(NpcFaction npcFaction)
	{
		final NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionById(npcFaction.getId());
		
		PacketSendUtility.sendPacket(owner, new SM_SYSTEM_MESSAGE(1300526, new DescriptionId(npcFactionTemplate.getNameId())));
		npcFaction.setActive(false);
		activeNpcFaction[npcFactionTemplate.isMentor() ? 1 : 0] = null;
		if (npcFaction.getState() == ENpcFactionQuestState.START)
		{
			QuestService.abandonQuest(owner, npcFaction.getQuestId());
			npcFaction.setState(ENpcFactionQuestState.NOTING);
		}
	}
	
	/**
	 * Allows the player to join a specific {@link Npc} faction.<br>
	 * This method checks if the player meets all requirements such as level, race, and skills.<br>
	 * If the requirements are met, it activates the new faction and triggers any associated daily quests.
	 * @param npc The {@link Npc} object representing the faction to join.
	 */
	public void enterGuild(Npc npc)
	{
		final int targetObjectId = npc.getObjectId();
		final NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionByNpcId(npc.getNpcId());
		if (npcFactionTemplate == null)
		{
			return;
		}
		
		final NpcFaction npcFaction = getNpcFactinById(npcFactionTemplate.getId());
		final NpcFaction activeNpcFaction = getActiveNpcFaction(npcFactionTemplate.isMentor());
		final int npcFactionId = npcFactionTemplate.getId();
		final int skillPoints = npcFactionTemplate.getSkillPoints();
		if (skillPoints != 0)
		{
			boolean canEnter = false;
			if (npcFactionTemplate.getCategory() == FactionCategory.COMBINESKILL)
			{
				for (PlayerSkillEntry skill : owner.getSkillList().getAllSkills())
				{
					if (CraftSkillUpdateService.isCraftingSkill(skill.getSkillId()) && (skill.getSkillLevel() >= skillPoints))
					{
						canEnter = true;
						break;
					}
				}
			}
			
			if (!canEnter)
			{
				PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1098));
				return;
			}
		}
		
		if ((owner.getLevel() < npcFactionTemplate.getMinLevel()) || (owner.getLevel() > npcFactionTemplate.getMaxLevel()))
		{
			PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1182));
			return;
		}
		
		if ((owner.getRace() != npcFactionTemplate.getRace()) && !npcFactionTemplate.getRace().equals(Race.NPC))
		{
			PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1097));
			return;
		}
		
		if ((npcFaction != null) && npcFaction.isActive())
		{
			PacketSendUtility.sendPacket(owner, new SM_SYSTEM_MESSAGE(1300525));
			return;
		}
		
		if ((activeNpcFaction != null) && (activeNpcFaction.getId() != npcFactionId))
		{
			askLeaveNpcFaction(npc);
			return;
		}
		
		if ((npcFaction == null) || !npcFaction.isActive())
		{
			PacketSendUtility.sendPacket(owner, new SM_SYSTEM_MESSAGE(1300524, new DescriptionId(npcFactionTemplate.getNameId())));
			PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1012));
			setActive(npcFactionId);
			
			sendDailyQuest();
		}
	}
	
	/**
	 * Prompts the player to join a new faction by leaving their current one.<br>
	 * It sends a question window to the {@link Player}.<br>
	 * If accepted, it calls {@code leaveNpcFaction} and then {@code enterGuild}.
	 * @param npc The {@link Npc} representing the new faction to join.
	 */
	private void askLeaveNpcFaction(Npc npc)
	{
		final NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionByNpcId(npc.getNpcId());
		final NpcFaction activeNpcFaction = getActiveNpcFaction(npcFactionTemplate.isMentor());
		final NpcFactionTemplate activeNpcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionById(activeNpcFaction.getId());
		final RequestResponseHandler responseHandler = new RequestResponseHandler(owner)
		{
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				leaveNpcFaction(activeNpcFaction);
				enterGuild(npc);
			}
			
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
			}
		};
		final boolean requested = owner.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_JOIN_NEW_FACTION, responseHandler);
		if (requested)
		{
			PacketSendUtility.sendPacket(owner, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_JOIN_NEW_FACTION, 0, 0, new DescriptionId(activeNpcFactionTemplate.getNameId()), new DescriptionId(npcFactionTemplate.getNameId())));
		}
		return;
	}
	
	/**
	 * Starts a new quest for the active NPC faction.<br>
	 * This method checks if the current faction is available to begin a quest.<br>
	 * If valid, it updates the state of the {@code NpcFaction} to {@code START}.
	 * @param questTemplate The {@link QuestTemplate} that the player wants to start.
	 */
	public void startQuest(QuestTemplate questTemplate)
	{
		final NpcFaction npcFaction = activeNpcFaction[questTemplate.isMentor() ? 1 : 0];
		if ((npcFaction == null) || ((npcFaction.getState() != ENpcFactionQuestState.NOTING) && (npcFaction.getQuestId() == 0)))
		{
			return;
		}
		
		npcFaction.setState(ENpcFactionQuestState.START);
	}
	
	/**
	 * Cancels the current quest for the active {@link NpcFaction}.<br>
	 * This method resets the faction state to {@code NOTING}.<br>
	 * It also triggers the {@code sendDailyQuest} method.
	 * @param questTemplate The {@code QuestTemplate} that needs to be aborted.
	 */
	public void abortQuest(QuestTemplate questTemplate)
	{
		final NpcFaction npcFaction = factions.get(questTemplate.getNpcFactionId());
		if ((npcFaction == null) || !npcFaction.isActive())
		{
			return;
		}
		
		npcFaction.setState(ENpcFactionQuestState.NOTING);
		sendDailyQuest();
	}
	
	/**
	 * Finishes the current quest for the player.<br>
	 * This method updates the {@code NpcFaction} state to complete.<br>
	 * It also handles specific rewards like mentor flags if applicable.
	 * @param questTemplate The {@link QuestTemplate} that is being finished.
	 */
	public void completeQuest(QuestTemplate questTemplate)
	{
		final NpcFaction npcFaction = activeNpcFaction[questTemplate.isMentor() ? 1 : 0];
		if (npcFaction == null)
		{
			return;
		}
		
		npcFaction.setTime(getNextTime());
		npcFaction.setState(ENpcFactionQuestState.COMPLETE);
		timeLimit[npcFaction.isMentor() ? 1 : 0] = npcFaction.getTime();
		if (questTemplate.getMentorType() == QuestMentorType.MENTOR)
		{
			owner.getCommonData().setMentorFlagTime((int) (System.currentTimeMillis() / 1000) + (60 * 60 * 24)); // TODO 1 day
			PacketSendUtility.broadcastPacket(owner, new SM_TITLE_INFO(owner, true), false);
			PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(true));
		}
	}
	
	/**
	 * Sends the daily quest packets to the player.<br>
	 * This method checks all active {@link NpcFaction} objects for eligible quests.<br>
	 * It updates the quest status and sends an {@code SM_QUEST_ACTION} packet if a new quest is assigned.
	 */
	public void sendDailyQuest()
	{
		for (int i = 0; i < 2; i++)
		{
			final NpcFaction faction = activeNpcFaction[i];
			if ((faction == null) || !faction.isActive() || (timeLimit[i] > (System.currentTimeMillis() / 1000)))
			{
				continue;
			}
			
			int questId = 0;
			switch (faction.getState())
			{
				case COMPLETE:
					if (faction.getTime() > (System.currentTimeMillis() / 1000))
					{
						continue;
					}
					break;
				case START:
					continue;
				case NOTING:
					if (faction.getTime() > (System.currentTimeMillis() / 1000))
					{
						questId = faction.getQuestId();
					}
					break;
			}
			
			if (questId == 0)
			{
				final List<QuestTemplate> quests = DataManager.QUEST_DATA.getQuestsByNpcFaction(faction.getId(), owner);
				if (quests.isEmpty())
				{
					continue;
				}
				
				questId = quests.get(Rnd.get(quests.size())).getId();
				faction.setQuestId(questId);
				faction.setTime(getNextTime());
			}
			
			PacketSendUtility.sendPacket(owner, new SM_QUEST_ACTION(questId, true));
		}
	}
	
	/**
	 * Checks if the player has exceeded the level limit for active factions.<br>
	 * It deactivates any faction where the {@code owner} level is higher than the template maximum.<br>
	 * If a quest was in progress, it calls {@code int)} to cancel it.<br>
	 * A system message is sent to the player notifying them of the faction leave.
	 */
	public void onLevelUp()
	{
		for (int i = 0; i < 2; i++)
		{
			final NpcFaction faction = activeNpcFaction[i];
			if ((faction == null) || !faction.isActive())
			{
				continue;
			}
			
			final NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionById(faction.getId());
			if (npcFactionTemplate.getMaxLevel() < owner.getLevel())
			{
				faction.setActive(false);
				activeNpcFaction[i] = null;
				if (faction.getState() == ENpcFactionQuestState.START)
				{
					QuestService.abandonQuest(owner, faction.getQuestId());
				}
				
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_FACTION_LEAVE_BY_LEVEL_LIMIT(npcFactionTemplate.getNameId()));
				faction.setState(ENpcFactionQuestState.NOTING);
			}
		}
	}
	
	/**
	 * Calculates the timestamp for the next daily reset.<br>
	 * This method sets the time to 9:00 AM of the current day.<br>
	 * If that time has already passed, it moves to the next day.
	 * @return The Unix timestamp in seconds for the calculated date.
	 */
	private int getNextTime()
	{
		final Calendar repeatDate = Calendar.getInstance(); // current date
		repeatDate.set(Calendar.AM_PM, Calendar.AM);
		repeatDate.set(Calendar.HOUR, 9);
		repeatDate.set(Calendar.MINUTE, 0);
		repeatDate.set(Calendar.SECOND, 0); // current date 09:00
		if (repeatDate.getTime().getTime() < System.currentTimeMillis())
		{
			repeatDate.add(Calendar.HOUR, 24); // can repeat next day
		}
		
		return (int) (repeatDate.getTimeInMillis() / 1000);
	}
	
	/**
	 * Checks if the player is allowed to start a specific quest.<br>
	 * This method verifies if the current faction time limit has expired.<br>
	 * It returns {@code true} if the quest can be started and {@code false} otherwise.
	 * @param template The {@link QuestTemplate} to check for availability.
	 * @return A boolean indicating if the quest is available to start.
	 */
	public boolean canStartQuest(QuestTemplate template)
	{
		final int type = template.isMentor() ? 1 : 0;
		final NpcFaction faction = activeNpcFaction[type];
		if ((faction != null) && (timeLimit[type] < (System.currentTimeMillis() / 1000)))
		{
			return true;
		}
		
		return false;
	}
}
