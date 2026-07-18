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
package system.handlers.ai.portals;

import java.util.List;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FIND_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.PortalService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Handles the AI logic for portals that trigger a dialog window.<br>
 * This class manages how NPCs interact with players when they initiate portal-related conversations.
 * @author xTz
 * @reworked vlog
 * @reworked Blackfire
 */
@AIName("portal_dialog")
public class PortalDialogAI2 extends PortalAI2
{
	protected int rewardDialogId = 5;
	protected int startingDialogId = 10;
	protected int questDialogId = 10;
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		if (getTalkDelay() == 0)
		{
			checkDialog(player);
		}
		else
		{
			super.handleDialogStart(player);
		}
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		switch (getNpcId())
		{
			case 730399: // Rentus Base
			case 731549: // [Seized] Danuar Sanctuary
			case 731570: // Danuar Sanctuary
			case 832991: // Occupied Rentus Base [Elyos]
			case 832992: // Occupied Rentus Base [Asmodians]
			case 832995: // Tiamat Stronghold [Elyos]
			case 832996: // Tiamat Stronghold [Asmodians]
			case 832997: // [Anguished] Dragon Lord Refuge
			case 832998: // Dragon Lord Refuge
				startLifeTask();
				break;
			case 730883: // [Infernal] Illuminary Obelisk
				announceIlluminaryObeliskOpen();
				break;
		}
	}
	
	/**
	 * Schedules a task to handle portal closure logic.<br>
	 * This method waits for {@code 120000} milliseconds before executing.<br>
	 * It removes the owner and sends a system message to all players.
	 */
	private void startLifeTask()
	{
		ThreadPoolManager.getInstance().schedule(() -> AI2Actions.deleteOwner(PortalDialogAI2.this), 120000); // 2 Minutes
	}
	
	/**
	 * Handles the logic when a player selects an option in a dialog.<br>
	 * It checks for specific items and grants rewards or skills based on the {@code dialogId}.<br>
	 * This method is triggered by the NPC's interaction system.
	 * @param player The {@link Player} who is interacting with the NPC.
	 * @param dialogId The unique identifier for the current dialog window.
	 * @param questId The ID of the quest associated with this interaction.
	 * @param extendedRewardIndex The index used to determine specific rewards.
	 * @return Always returns {@code true} to indicate the action was processed.
	 */
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex)
	{
		final QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if ((questId > 0) && QuestEngine.getInstance().onDialog(env))
		{
			return true;
		}
		
		if (dialogId == DialogAction.INSTANCE_PARTY_MATCH.id())
		{
			final AutoGroupType agt = AutoGroupType.getAutoGroup(player.getLevel(), getNpcId());
			if (agt != null)
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(agt.getInstanceMaskId()));
			}
			
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		}
		else if (dialogId == DialogAction.OPEN_INSTANCE_RECRUIT.id())
		{
			final AutoGroupType agt = AutoGroupType.getAutoGroup(player.getLevel(), getNpcId());
			if (agt != null)
			{
				PacketSendUtility.sendPacket(player, new SM_FIND_GROUP(0x1A, agt.getInstanceMapId()));
			}
		}
		else
		{
			if (questId == 0)
			{
				final PortalPath portalPath = DataManager.PORTAL2_DATA.getPortalDialog(getNpcId(), dialogId, player.getRace());
				if (portalPath != null)
				{
					PortalService.port(portalPath, player, getObjectId());
				}
			}
			else
			{
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
			}
		}
		
		return true;
	}
	
	/**
	 * Handles the completion of an item usage action.<br>
	 * This method is called when a {@link Player} finishes using an item.<br>
	 * It checks if the owner is in an instance before processing.
	 * @param player The {@code Player} who finished using the item.
	 */
	@Override
	protected void handleUseItemFinish(Player player)
	{
		checkDialog(player);
	}
	
	/**
	 * Checks the player's quest status to determine which dialog to display.<br>
	 * It sends a {@code SM_DIALOG_WINDOW} based on whether the player can start, is currently in, or has finished a related quest.<br>
	 * Specific NPC IDs are handled with custom logic for different races and world locations.
	 * @param player The {@link Player} object to check and send packets to.
	 */
	private void checkDialog(Player player)
	{
		final int npcId = getNpcId();
		final int teleportationDialogId = DataManager.PORTAL2_DATA.getTeleportDialogId(npcId);
		final List<Integer> relatedQuests = QuestEngine.getInstance().getQuestNpc(npcId).getOnTalkEvent();
		boolean playerHasQuest = false;
		boolean playerCanStartQuest = false;
		if (!relatedQuests.isEmpty())
		{
			for (int questId : relatedQuests)
			{
				final QuestState qs = player.getQuestStateList().getQuestState(questId);
				if ((qs != null) && ((qs.getStatus() == QuestStatus.START) || (qs.getStatus() == QuestStatus.REWARD)))
				{
					if ((qs.getQuestId() == 10032) && (qs.getStatus() == QuestStatus.REWARD))
					{
						playerHasQuest = false;
					}
					else
					{
						playerHasQuest = true;
					}
					break;
				}
				else if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
				{
					if (QuestService.checkStartConditions(new QuestEnv(getOwner(), player, questId, 0), false))
					{
						playerCanStartQuest = true;
						continue;
					}
				}
			}
		}
		
		if (playerHasQuest)
		{
			boolean isRewardStep = false;
			for (int questId : relatedQuests)
			{
				final QuestState qs = player.getQuestStateList().getQuestState(questId);
				if ((qs != null) && (qs.getStatus() == QuestStatus.REWARD))
				{
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), rewardDialogId, questId));
					isRewardStep = true;
					break;
				}
			}
			
			if (!isRewardStep)
			{
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), questDialogId));
			}
		}
		else if (playerCanStartQuest)
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), startingDialogId));
		}
		else
		{
			switch (npcId)
			{
				case 730883: // Illuminary Obelisk
				case 730892: // Iaphetus
				case 730893: // Bress
				case 730894: // Ombrios
				case 730895: // Danan
				case 730896: // Iaphetus
				case 730897: // Bress
				case 804619: // Lucky Danuar Reliquary Gatekeeper
				case 804620: // Lucky Ophidan Bridge Gatekeeper
				case 804621: // Danuar Reliquary
				case 805609: // Torino
				case 805610: // Zenoa
				case 805623: // Trieste
				case 805624: // Ankona
				case 832991: // Occupied Rentus Base [Elyos]
				case 832992: // Occupied Rentus Base [Asmodians]
				case 730721: // Sealed Danuar Mysticarium - Argent Manor [Elyos]
				case 730722: // Sealed Danuar Mysticarium - Argent Manor [Asmodians]
				case 833024: // Stonespear Reach [Elyos]
				case 833025: // Stonespear Reach [Asmodians]
				case 833043: // Stonespear Reach [Elyos]
				case 833044: // Stonespear Reach [Asmodians]
				case 833045: // Stonespear Reach [Elyos]
				case 833046: // Stonespear Reach [Asmodians]
				case 835609: // IDTransform_NPC_Entrance_PC
				case 835610: // IDStation_NPC_Entrance_PC
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10, 0));
					break;
				case 731549: // Seized Danuar Sanctuary
					switch (player.getWorldId())
					{
						case 210070000: // Cygnea Enter Seized Danuar Sanctuary
							if (player.getCommonData().getRace() == Race.ASMODIANS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 0));
							}
							break;
						case 220080000: // Enshar Enter Seized Danuar Sanctuary
							if (player.getCommonData().getRace() == Race.ELYOS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 0));
							}
							break;
					}
					break;
				case 731570: // Danuar Sanctuary
					switch (player.getWorldId())
					{
						case 210070000: // Cygnea Enter Danuar Sanctuary
							if (player.getCommonData().getRace() == Race.ELYOS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 0));
							}
							break;
						case 220080000: // Enshar Enter Danuar Sanctuary
							if (player.getCommonData().getRace() == Race.ASMODIANS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 0));
							}
							break;
					}
					break;
				case 832995: // Tiamat Stronghold [Elyos]
					switch (player.getWorldId())
					{
						case 210070000: // Cygnea Enter Tiamat Stronghold
							if (player.getCommonData().getRace() == Race.ELYOS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 0));
							}
							break;
					}
					break;
				case 832996: // Tiamat Stronghold [Asmodian]
					switch (player.getWorldId())
					{
						case 220080000: // Enshar Enter Tiamat Stronghold
							if (player.getCommonData().getRace() == Race.ASMODIANS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1352, 0));
							}
							break;
					}
					break;
				case 832997: // [Anguished] Dragon Lord Refuge
					switch (player.getWorldId())
					{
						case 210070000: // Cygnea Enter the Anguished Dragon Lord's Refuge
							if (player.getCommonData().getRace() == Race.ASMODIANS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 0));
							}
							break;
						case 220080000: // Enshar Enter the Anguished Dragon Lord's Refuge
							if (player.getCommonData().getRace() == Race.ELYOS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 0));
							}
							break;
					}
					break;
				case 832998: // Dragon Lord Refuge
					switch (player.getWorldId())
					{
						case 210070000: // Cygnea Enter Dragon Lord's Refuge
							if (player.getCommonData().getRace() == Race.ELYOS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1352, 0));
							}
							break;
						case 220080000: // Enshar Enter Dragon Lord's Refuge
							if (player.getCommonData().getRace() == Race.ASMODIANS)
							{
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1352, 0));
							}
							break;
					}
					break;
				default:
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), teleportationDialogId, 0));
					break;
			}
		}
	}
	
	/**
	 * Sends a system message to all players.<br>
	 * This notifies everyone that the Illuminary Obelisk is now open.
	 */
	private void announceIlluminaryObeliskOpen()
	{
		World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF5_U3_Hard_Door_Open));
	}
}
