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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for changing a player's character class.<br>
 * It manages requirements and updates the {@link Player} data accordingly.
 * @author ATracer, sweetkr
 */
public class ClassChangeService
{
	// TODO dialog enum
	
	/**
	 * Displays the class change dialog to a player.<br>
	 * This method checks if {@code CustomConfig.ENABLE_SIMPLE_2NDCLASS} is true.<br>
	 * It sends the correct {@link com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW} based on the player's race and class.
	 * @param player The {@link Player} who will receive the dialog packet.
	 */
	public static void showClassChangeDialog(Player player)
	{
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS)
		{
			final PlayerClass playerClass = player.getPlayerClass();
			final Race playerRace = player.getRace();
			if ((player.getLevel() >= 9) && playerClass.isStartingClass())
			{
				if (playerRace == Race.ELYOS)
				{
					switch (playerClass)
					{
						case WARRIOR:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2375, 1006));
							break;
						case SCOUT:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2716, 1006));
							break;
						case MAGE:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 1006));
							break;
						case PRIEST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 1006));
							break;
						case ENGINEER:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3739, 1006)); // 4.5
							break;
						case ARTIST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 4080, 1006));
							break;
						default:
							break;
					}
				}
				else if (playerRace == Race.ASMODIANS)
				{
					switch (playerClass)
					{
						case WARRIOR:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 2008));
							break;
						case SCOUT:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 2008));
							break;
						case MAGE:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3739, 2008));
							break;
						case PRIEST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 4080, 2008));
							break;
						case ENGINEER:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3612, 2008)); // 4.5
							break;
						case ARTIST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3910, 2008));
							break;
						default:
							break;
					}
				}
			}
		}
	}
	
	/**
	 * Changes the player's class based on a specific dialog ID.<br>
	 * This method checks the {@code Race} of the {@link Player}.<br>
	 * It updates the character class and completes related quests.
	 * @param player The {@link Player} object to modify.
	 * @param dialogId The unique identifier for the selection dialog.
	 */
	public static void changeClassToSelection(Player player, int dialogId)
	{
		final Race playerRace = player.getRace();
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS)
		{
			if (playerRace == Race.ELYOS)
			{
				switch (dialogId)
				{
					case 2376:
						setClass(player, PlayerClass.getPlayerClassById((byte) 1));
						break;
					case 2461:
						setClass(player, PlayerClass.getPlayerClassById((byte) 2));
						break;
					case 2717:
						setClass(player, PlayerClass.getPlayerClassById((byte) 4));
						break;
					case 2802:
						setClass(player, PlayerClass.getPlayerClassById((byte) 5));
						break;
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById((byte) 7));
						break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById((byte) 8));
						break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById((byte) 10));
						break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById((byte) 11));
						break;
					case 3825:
						setClass(player, PlayerClass.getPlayerClassById((byte) 13)); // 4.5
						break;
					case 3740:
						setClass(player, PlayerClass.getPlayerClassById((byte) 14));
						break;
					case 4081:
						setClass(player, PlayerClass.getPlayerClassById((byte) 16));
						break;
					case 4166:
						setClass(player, PlayerClass.getPlayerClassById((byte) 17)); // 7.0
						break;
				}
				
				completeQuest(player, 60100);
				completeQuest(player, 60101);
				
				// Stigma Quests Elyos
				if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST))
				{
					completeQuest(player, 1929);
				}
			}
			else if (playerRace == Race.ASMODIANS)
			{
				switch (dialogId)
				{
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById((byte) 1));
						break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById((byte) 2));
						break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById((byte) 4));
						break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById((byte) 5));
						break;
					case 3740:
						setClass(player, PlayerClass.getPlayerClassById((byte) 7));
						break;
					case 3825:
						setClass(player, PlayerClass.getPlayerClassById((byte) 8));
						break;
					case 4081:
						setClass(player, PlayerClass.getPlayerClassById((byte) 10));
						break;
					case 4166:
						setClass(player, PlayerClass.getPlayerClassById((byte) 11));
						break;
					case 3591:
						setClass(player, PlayerClass.getPlayerClassById((byte) 13)); // 4.5
						break;
					case 3570:
						setClass(player, PlayerClass.getPlayerClassById((byte) 14));
						break;
					case 3911:
						setClass(player, PlayerClass.getPlayerClassById((byte) 16));
						break;
					case 3932:
						setClass(player, PlayerClass.getPlayerClassById((byte) 17)); // 7.0
						break;
				}
				
				// Optimate @Enomine
				completeQuest(player, 70100);
				completeQuest(player, 70101);
				
				// Stigma Quests Asmodians
				if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST))
				{
					completeQuest(player, 2900);
				}
			}
		}
	}
	
	/**
	 * Marks a specific quest as finished for a player.<br>
	 * This method updates the {@code QuestState} and sends a packet to the client.<br>
	 * If the quest does not exist, it is added as completed.
	 * @param player The {@link Player} who is completing the quest.
	 * @param questId The unique identifier for the quest.
	 */
	private static void completeQuest(Player player, int questId)
	{
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		// Calendar calendar = Calendar.getInstance();
		// Timestamp timeStamp = new Timestamp(calendar.getTime().getTime());
		if (qs == null)
		{
			player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.COMPLETE, 0, 0, null, 0, null));
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, QuestStatus.COMPLETE.value(), 0));
		}
		else
		{
			qs.setStatus(QuestStatus.COMPLETE);
			qs.setCompleteCount(qs.getCompleteCount() + 1);
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
	}
	
	/**
	 * Updates the character class for a specific player.<br>
	 * This method checks if the change is valid before applying it.<br>
	 * It also triggers an upgrade and sends a confirmation packet.
	 * @param player The {@link Player} object to modify.
	 * @param playerClass The new {@link PlayerClass} to assign.
	 */
	public static void setClass(Player player, PlayerClass playerClass)
	{
		if (validateSwitch(player, playerClass))
		{
			player.getCommonData().setPlayerClass(playerClass);
			player.getController().upgradePlayer();
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0, 0));
		}
	}
	
	/**
	 * Checks if a player is allowed to change their character class.<br>
	 * This method verifies the current level and the valid target class for the starting class.
	 * @param player The {@link Player} object being checked.
	 * @param playerClass The {@link PlayerClass} the player wants to switch to.
	 * @return {@code true} if the switch is valid, otherwise {@code false}.
	 */
	private static boolean validateSwitch(Player player, PlayerClass playerClass)
	{
		final int level = player.getLevel();
		final int levelToChange = GSConfig.STARTCLASS_MAXLEVEL - 1;
		final PlayerClass oldClass = player.getPlayerClass();
		if (level != levelToChange)
		{
			PacketSendUtility.sendMessage(player, "You can only switch class at level " + levelToChange);
			return false;
		}
		
		if (!oldClass.isStartingClass())
		{
			PacketSendUtility.sendMessage(player, "You already switched class");
			return false;
		}
		
		switch (oldClass)
		{
			case WARRIOR:
				if ((playerClass == PlayerClass.GLADIATOR) || (playerClass == PlayerClass.TEMPLAR))
				{
					break;
				}
			case SCOUT:
				if ((playerClass == PlayerClass.ASSASSIN) || (playerClass == PlayerClass.RANGER))
				{
					break;
				}
			case MAGE:
				if ((playerClass == PlayerClass.SORCERER) || (playerClass == PlayerClass.SPIRIT_MASTER))
				{
					break;
				}
			case PRIEST:
				if ((playerClass == PlayerClass.CLERIC) || (playerClass == PlayerClass.CHANTER))
				{
					break;
				}
			case ENGINEER:
				if ((playerClass == PlayerClass.GUNNER) || (playerClass == PlayerClass.RIDER))
				{
					break;
				}
			case ARTIST:
				if ((playerClass == PlayerClass.BARD) || (playerClass == PlayerClass.PAINTER))
				{
					break;
				}
			default:
				PacketSendUtility.sendMessage(player, "Invalid class switch chosen");
				return false;
		}
		
		return true;
	}
	
	/**
	 * Updates the progress of quest {@code 15545}.<br>
	 * This method checks if the player has started the quest.<br>
	 * It sets the quest status to {@code REWARD} and sends a packet.
	 * @param player The {@link Player} object being updated.
	 */
	public static void onUpdateQuest15545(Player player)
	{
		if (player.getQuestStateList().hasQuest(15545))
		{
			final QuestState qs = player.getQuestStateList().getQuestState(15545);
			if ((qs.getStatus() == QuestStatus.START) && (qs.getQuestVarById(0) == 0))
			{
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(15545, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
	}
	
	/**
	 * Updates the progress for quest {@code 25545}.<br>
	 * This method checks if the player has started the quest.<br>
	 * It updates the quest status to {@code REWARD} and sends a packet.
	 * @param player The {@link Player} object who is receiving the update.
	 */
	public static void onUpdateQuest25545(Player player)
	{
		if (player.getQuestStateList().hasQuest(25545))
		{
			final QuestState qs = player.getQuestStateList().getQuestState(25545);
			if ((qs.getStatus() == QuestStatus.START) && (qs.getQuestVarById(0) == 0))
			{
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(25545, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			}
		}
	}
}
