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
package system.handlers.quest.ishalgen;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ASCENSION_MORPH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Handles the logic for the 2008 Ascension quest.<br>
 * This class manages player progression, transformations, and rewards associated with this specific quest line.
 * @author Falke_34
 */
public class _2008Ascension extends QuestHandler
{
	private final static int questId = 2008;
	
	/**
	 * Initializes the quest handler for quest {@code 2008}.<br>
	 * This constructor calls the superclass constructor to register the quest ID.
	 */
	public _2008Ascension()
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
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(806814).addOnTalkEvent(questId);
		qe.registerQuestNpc(203516).addOnTalkEvent(questId);
		qe.registerQuestNpc(203550).addOnTalkEvent(questId);
		qe.registerQuestNpc(806810).addOnTalkEvent(questId);
		qe.registerQuestNpc(806848).addOnTalkEvent(questId);
		qe.registerQuestNpc(205020).addOnTalkEvent(questId);
		qe.registerQuestNpc(205040).addOnKillEvent(questId);
		qe.registerQuestNpc(205041).addOnKillEvent(questId);
		qe.registerOnMovieEndQuest(152, questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnDie(questId);
	}
	
	/**
	 * This method is triggered when a player kills a target.<br>
	 * It checks if the kill meets the requirements for the quest.<br>
	 * Use this to progress the quest state.
	 * @param env The {@link QuestEnv} object containing current quest data.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int instanceId = player.getInstanceId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() != QuestStatus.START))
		{
			return false;
		}
		
		final int var = qs.getQuestVarById(0);
		final int targetId = env.getTargetId();
		final Npc npc = (Npc) env.getVisibleObject();
		if (targetId == 205040)
		{
			CreatureActions.delete(npc);
			if ((var >= 51) && (var <= 53))
			{
				qs.setQuestVar(qs.getQuestVars().getQuestVars() + 1);
				updateQuestStatus(env);
				return true;
			}
			else if (var == 54)
			{
				qs.setQuestVar(5);
				updateQuestStatus(env);
				final Npc mob = (Npc) QuestService.spawnQuestNpc(320020000, instanceId, 205041, 301f, 259f, 205.5f, (byte) 0);
				mob.getAggroList().addDamage(player, 1000);
				return true;
			}
		}
		else if ((targetId == 205041) && (var == 5))
		{
			playQuestMovie(env, 152);
			for (Npc npcInside : player.getPosition().getWorldMapInstance().getNpcs())
			{
				CreatureActions.delete(npcInside);
			}
			
			QuestService.addNewSpawn(320020000, instanceId, 806848, 301.92999f, 274.26001f, 205.7f, (byte) 0);
			qs.setQuestVar(6);
			updateQuestStatus(env);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Handles dialog events for the quest.<br>
	 * This method checks the current {@link QuestState} and {@code targetId}.<br>
	 * It determines which dialog to send based on the {@link DialogAction}.
	 * @param env The environment containing player data and current quest context.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			return false;
		}
		
		final int targetId = env.getTargetId();
		final DialogAction action = env.getDialog();
		
		final int var = qs.getQuestVarById(0);
		if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 806814)
			{
				if (var == 0)
				{
					switch (action)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env, 1011);
						case SELECT_ACTION_1012:
							return sendQuestDialog(env, 1012);
						case SELECT_ACTION_1013:
							return sendQuestDialog(env, 1013);
						case SETPRO1:
							qs.setQuestVar(1); // 99
							updateQuestStatus(env);
							return closeDialogWindow(env);
						default:
							break;
					}
				}
				else if (var == 4)
				{
					switch (action)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env, 2375);
						case SETPRO5:
							final WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(320020000);
							InstanceService.registerPlayerWithInstance(newInstance, player);
							TeleportService2.teleportTo(player, 320020000, newInstance.getInstanceId(), 457.65f, 426.8f, 230.4f);
							qs.setQuestVar(99); // 99
							updateQuestStatus(env);
							return closeDialogWindow(env);
						default:
							break;
						
					}
				}
			}
			else if (targetId == 203516)
			{
				switch (env.getDialog())
				{
					case QUEST_SELECT:
						if (var == 1)
						{
							return sendQuestDialog(env, 1352);
						}
					case SETPRO2:
						if (var == 1)
						{
							qs.setQuestVar(2);
							updateQuestStatus(env);
							TeleportService2.teleportTo(player, 220010000, 577f, 2443f, 279.875f, (byte) 46, TeleportAnimation.BEAM_ANIMATION);
							return true;
						}
					default:
						break;
				}
			}
			else if (targetId == 203550)
			{
				switch (env.getDialog())
				{
					case QUEST_SELECT:
						if (var == 2)
						{
							return sendQuestDialog(env, 1693);
						}
					case SETPRO3:
						if (var == 2)
						{
							qs.setQuestVar(3);
							updateQuestStatus(env);
							return true;
						}
					default:
						break;
				}
			}
			else if (targetId == 806810)
			{
				switch (env.getDialog())
				{
					case QUEST_SELECT:
						if (var == 3)
						{
							return sendQuestDialog(env, 2034);
						}
					case SETPRO4:
						if (var == 3)
						{
							qs.setQuestVar(4);
							updateQuestStatus(env);
							return true;
						}
					default:
						break;
				}
			}
			else if (targetId == 205020)
			{
				switch (env.getDialog())
				{
					case QUEST_SELECT:
						if ((var == 99) || (var == 35))
						{
							final int instanceId = player.getInstanceId();
							SkillEngine.getInstance().applyEffectDirectly(1853, player, player, 0);
							player.setState(CreatureState.FLIGHT_TELEPORT);
							player.unsetState(CreatureState.ACTIVE);
							player.setFlightTeleportId(3001);
							PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 3001, 0));
							qs.setQuestVar(50);
							updateQuestStatus(env);
							ThreadPoolManager.getInstance().schedule(new Runnable()
							{
								
								@Override
								public void run()
								{
									qs.setQuestVar(51);
									updateQuestStatus(env);
									final List<Npc> mobs = new ArrayList<>();
									mobs.add((Npc) QuestService.spawnQuestNpc(320020000, instanceId, 205040, 294f, 277f, 207f, (byte) 0));
									mobs.add((Npc) QuestService.spawnQuestNpc(320020000, instanceId, 205040, 305f, 279f, 206.5f, (byte) 0));
									mobs.add((Npc) QuestService.spawnQuestNpc(320020000, instanceId, 205040, 298f, 253f, 205.7f, (byte) 0));
									mobs.add((Npc) QuestService.spawnQuestNpc(320020000, instanceId, 205040, 306f, 251f, 206f, (byte) 0));
									for (Npc mob : mobs)
									{
										mob.getAggroList().addDamage(player, 1000);
									}
								}
							}, 43000);
							return true;
						}
						
						return false;
					default:
						return false;
				}
			}
			else if (targetId == 806848)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 2716);
					case SETPRO6:
						final PlayerClass playerClass = player.getCommonData().getPlayerClass();
						if (playerClass.isStartingClass())
						{
							if (playerClass == PlayerClass.WARRIOR)
							{
								return sendQuestDialog(env, 3057);
							}
							else if (playerClass == PlayerClass.SCOUT)
							{
								return sendQuestDialog(env, 3398);
							}
							else if (playerClass == PlayerClass.MAGE)
							{
								return sendQuestDialog(env, 3741);
							}
							else if (playerClass == PlayerClass.PRIEST)
							{
								return sendQuestDialog(env, 4082);
							}
							else if (playerClass == PlayerClass.ENGINEER)
							{
								return sendQuestDialog(env, 3569);
							}
							else if (playerClass == PlayerClass.ARTIST)
							{
								return sendQuestDialog(env, 3932);
							}
						}
					case SETPRO7:
						return setPlayerClass(env, qs, PlayerClass.GLADIATOR);
					case SETPRO8:
						return setPlayerClass(env, qs, PlayerClass.TEMPLAR);
					case SETPRO9:
						return setPlayerClass(env, qs, PlayerClass.ASSASSIN);
					case SETPRO10:
						return setPlayerClass(env, qs, PlayerClass.RANGER);
					case SETPRO11:
						return setPlayerClass(env, qs, PlayerClass.SORCERER);
					case SETPRO12:
						return setPlayerClass(env, qs, PlayerClass.SPIRIT_MASTER);
					case SETPRO13:
						return setPlayerClass(env, qs, PlayerClass.CLERIC);
					case SETPRO14:
						return setPlayerClass(env, qs, PlayerClass.CHANTER);
					case SETPRO15:
						return setPlayerClass(env, qs, PlayerClass.GUNNER);
					case SETPRO16:
						return setPlayerClass(env, qs, PlayerClass.BARD);
					case SETPRO17:
						return setPlayerClass(env, qs, PlayerClass.RIDER);
					case SETPRO18:
					{
						return setPlayerClass(env, qs, PlayerClass.PAINTER);
					}
					default:
						break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 806848)
			{
				switch (env.getDialog())
				{
					case SELECTED_QUEST_NOREWARD:
						if (player.getWorldId() == 320020000)
						{
							TeleportService2.teleportTo(player, 220010000, 597.5192f, 2442.231f, 280.61536f, (byte) 99, TeleportAnimation.BEAM_ANIMATION);
						}
						break;
					default:
						break;
				}
				
				return sendQuestEndDialog(env);
			}
		}
		
		return false;
	}
	
	/**
	 * This method handles the logic when a player levels up.<br>
	 * It checks if the level up triggers specific quest progress.<br>
	 * It calls {@code defaultOnLvlUpEvent} to process the event.
	 * @param env The current quest environment context.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		return defaultOnLvlUpEvent(env);
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
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			final int var = qs.getQuestVars().getQuestVars();
			if ((var == 5) || ((var == 6) && player.getPlayerClass().isStartingClass()) || ((var >= 50) && (var <= 55)) || (var == 99))
			{
				if (player.getWorldId() != 320020000)
				{
					qs.setQuestVar(4);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
				}
				else
				{
					PacketSendUtility.sendPacket(player, new SM_ASCENSION_MORPH(1));
					return true;
				}
			}
		}
		else if (qs == null)
		{
			env.setQuestId(questId);
			QuestService.startQuest(env);
		}
		
		return false;
	}
	
	/**
	 * Updates the player's class to a new {@code PlayerClass}.<br>
	 * This method checks if the current class is a starting class before applying changes.<br>
	 * It also updates the quest progress and sends a dialog to the player.
	 * @param env The {@link QuestEnv} containing the current environment data.
	 * @param qs The {@link QuestState} representing the current state of the quest.
	 * @param playerClass The new {@code PlayerClass} to assign to the player.
	 * @return {@code true} if the class was successfully changed, otherwise {@code false}.
	 */
	private boolean setPlayerClass(QuestEnv env, QuestState qs, PlayerClass playerClass)
	{
		final Player player = env.getPlayer();
		if (player.getPlayerClass().isStartingClass())
		{
			ClassChangeService.setClass(player, playerClass);
			player.getController().upgradePlayer();
			changeQuestStep(env, 6, 6, true); // reward
			return sendQuestDialog(env, 5);
		}
		
		return false;
	}
	
	/**
	 * Handles the logic when a player dies during this quest.<br>
	 * It checks if the quest is currently in the {@code START} status.<br>
	 * If specific conditions are met, it fails the quest and sends a system message.
	 * @param env The current quest environment containing player and state data.
	 * @return Always returns {@code false}.
	 */
	@Override
	public boolean onDieEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() != QuestStatus.START) || (qs.getStatus() != QuestStatus.START))
		{
			return false;
		}
		
		final int var = qs.getQuestVars().getQuestVars();
		if ((var == 5) || ((var == 6) && player.getPlayerClass().isStartingClass()) || ((var >= 51) && (var <= 53)))
		{
			qs.setQuestVar(4);
			updateQuestStatus(env);
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
		}
		
		return false;
	}
}
