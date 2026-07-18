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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.autogroup.AGPlayer;
import com.aionemu.gameserver.model.autogroup.AGQuestion;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.autogroup.AutoInstance;
import com.aionemu.gameserver.model.autogroup.EntryRequestType;
import com.aionemu.gameserver.model.autogroup.LookingForParty;
import com.aionemu.gameserver.model.autogroup.SearchInstance;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.InstanceCooltime;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.instance.BalaurMarchingRouteService;
import com.aionemu.gameserver.services.instance.DredgionService;
import com.aionemu.gameserver.services.instance.GoldenCrucibleService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.instance.JormungandService;
import com.aionemu.gameserver.services.instance.KamarBattlefieldService;
import com.aionemu.gameserver.services.instance.PandaemoniumBattlefieldService;
import com.aionemu.gameserver.services.instance.PvPArenaService;
import com.aionemu.gameserver.services.instance.RunatoriumRuinsService;
import com.aionemu.gameserver.services.instance.RunatoriumService;
import com.aionemu.gameserver.services.instance.SanctumBattlefieldService;
import com.aionemu.gameserver.services.instance.SteelWallBastionBattlefieldService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapInstanceFactory;

/**
 * Manages the automated group finding and instance joining system for players.<br>
 * This service handles {@link AGPlayer} requests to join groups or search for instances based on specific criteria.
 * @author xTz
 * @Reworked Eloann v4.5
 * @author GiGatR00n v4.7.5.x
 */
public class AutoGroupService
{
	private static final Logger log = LoggerFactory.getLogger("AUTOGROUP_LOG");
	private final Map<Integer, LookingForParty> searchers = new ConcurrentHashMap<>();
	private final Map<Integer, AutoInstance> autoInstances = new ConcurrentHashMap<>();
	private final Collection<Integer> penaltys = Collections.synchronizedList(new ArrayList<>());
	private final Lock lock = new ReentrantLock();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This class is managed as a singleton service.
	 */
	private AutoGroupService()
	{
	}
	
	/**
	 * Starts the process for a player to look for an auto-group.<br>
	 * This method validates if the {@code Player} can enter the specified {@code instanceMaskId}.<br>
	 * It registers the search request and notifies relevant group members if applicable.<br>
	 * Finally, it triggers the sorting logic for the new request.
	 * @param player The {@link Player} who is initiating the search.
	 * @param instanceMaskId The unique identifier for the instance type.
	 * @param ert The {@link EntryRequestType} defining how the player wants to enter.
	 */
	public void startLooking(Player player, int instanceMaskId, EntryRequestType ert)
	{
		final AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
		if ((agt == null) || !canEnter(player, ert, agt))
		{
			return;
		}
		
		final Integer obj = player.getObjectId();
		final LookingForParty lfp = searchers.get(obj);
		if (penaltys.contains(obj))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400181, agt.getInstanceMapId()));
			return;
		}
		
		if (lfp == null)
		{
			searchers.put(obj, new LookingForParty(player, instanceMaskId, ert));
		}
		else if (lfp.hasPenalty() || lfp.isRegistredInstance(instanceMaskId))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400181, agt.getInstanceMapId()));
			return;
		}
		else
		{
			lfp.addInstanceMaskId(instanceMaskId, ert);
		}
		
		if (ert.isGroupEntry())
		{
			for (Player member : player.getPlayerGroup2().getOnlineMembers())
			{
				if (agt.isDredgion())
				{
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				else if (agt.isKamar())
				{
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				else if (agt.isJormungand())
				{
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				else if (agt.isSteelWall())
				{
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				else if (agt.isRunatorium())
				{
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				else if (agt.isBalaurMarching())
				{
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				else if (agt.isRunatoriumRuins())
				{
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				else if (agt.isGoldenCrucible())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				else if (agt.isSanctumBattlefield())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				else if (agt.isPandaemoniumBattlefield())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				
				PacketSendUtility.sendPacket(member, new SM_SYSTEM_MESSAGE(1400194, agt.getInstanceMapId()));
				PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 1, ert.getId(), player.getName()));
			}
		}
		else
		{
			if (agt.isDredgion())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			else if (agt.isKamar())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			else if (agt.isJormungand())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			else if (agt.isSteelWall())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			else if (agt.isRunatorium())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			else if (agt.isBalaurMarching())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			else if (agt.isRunatoriumRuins())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			else if (agt.isGoldenCrucible())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			else if (agt.isSanctumBattlefield())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			else if (agt.isPandaemoniumBattlefield())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400194, agt.getInstanceMapId()));
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 1, ert.getId(), player.getName()));
		}
		
		if (LoggingConfig.LOG_AUTOGROUP)
		{
			log.info("[AUTOGROUPSERVICE] > Register playerName: " + player.getName() + " class: " + player.getPlayerClass() + " race: " + player.getRace());
			log.info("[AUTOGROUPSERVICE] > Register instanceMaskId: " + instanceMaskId + " type: " + ert);
		}
		
		startSort(ert, instanceMaskId, true);
	}
	
	/**
	 * Processes the action when a {@code Player} presses enter to join an instance.<br>
	 * This method validates the request and removes the player from groups or alliances if necessary.<br>
	 * It then triggers the entry logic for the specific {@code instanceMaskId}.
	 * @param player The {@code Player} object performing the action.
	 * @param instanceMaskId The unique identifier for the target instance.
	 */
	public synchronized void pressEnter(Player player, int instanceMaskId)
	{
		final AutoInstance instance = getAutoInstance(player, instanceMaskId);
		if ((instance == null) || instance.players.get(player.getObjectId()).isPressedEnter())
		{
			return;
		}
		
		if (player.isInGroup2())
		{
			PlayerGroupService.removePlayer(player);
		}
		
		if (player.isInAlliance2())
		{
			PlayerAllianceService.removePlayer(player);
		}
		
		instance.onPressEnter(player);
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 5));
	}
	
	/**
	 * This method is called when a {@link Player} enters an instance.<br>
	 * It updates the auto group entry icon for the player.<br>
	 * It checks if the player belongs to an {@link AutoInstance}.<br>
	 * If they do, it triggers the instance logic and enables auto group usage.
	 * @param player The {@link Player} object who entered the instance.
	 */
	public void onEnterInstance(Player player)
	{
		if (player.isInInstance())
		{
			setAutoGroupEntryIcon(player, true);
			final Integer obj = player.getObjectId();
			final AutoInstance autoInstance = autoInstances.get(player.getInstanceId());
			if ((autoInstance != null) && autoInstance.players.containsKey(obj))
			{
				autoInstance.onEnterInstance(player);
				player.setUseAutoGroup(1);
			}
		}
	}
	
	/**
	 * Removes a player from the search list for a specific instance.<br>
	 * This method updates the {@code LookingForParty} status and applies penalties if necessary.<br>
	 * It also calls {@code SearchInstance)} to clean up the data.
	 * @param player The {@code Player} object to unregister.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 */
	public void unregisterLooking(Player player, int instanceMaskId)
	{
		if (LoggingConfig.LOG_AUTOGROUP)
		{
			log.info("[AUTOGROUPSERVICE] > unregisterLooking instanceMaskId: " + instanceMaskId + " player: " + player.getName());
		}
		
		final Integer obj = player.getObjectId();
		final LookingForParty lfp = searchers.get(obj);
		SearchInstance si;
		if (lfp != null)
		{
			lfp.setPenaltyTime();
			si = lfp.getSearchInstance(instanceMaskId);
			if (si != null)
			{
				if (lfp.unregisterInstance(instanceMaskId) == 0)
				{
					searchers.remove(obj);
					startPenalty(obj);
				}
				
				getInstance().unRegisterSearchInstance(player, si);
			}
		}
	}
	
	/**
	 * Cancels the entry process for a player into a specific instance.<br>
	 * This method removes the player from the auto-group and handles cleanup.<br>
	 * It may trigger penalties or destroy the instance if it becomes empty.
	 * @param player The {@code Player} who is attempting to cancel their entry.
	 * @param instanceMaskId The unique identifier for the instance being joined.
	 */
	public void cancelEnter(Player player, int instanceMaskId)
	{
		AutoInstance autoInstance = getAutoInstance(player, instanceMaskId);
		if (autoInstance != null)
		{
			final Integer obj = player.getObjectId();
			if (!autoInstance.players.get(obj).isInInstance())
			{
				autoInstance.unregister(player);
				if (!searchers.containsKey(obj))
				{
					startPenalty(obj);
				}
				
				if (autoInstance.agt.hasRegisterQuick())
				{
					startSort(EntryRequestType.QUICK_GROUP_ENTRY, instanceMaskId, false);
				}
				
				if (autoInstance.players.isEmpty())
				{
					final WorldMapInstance instance = autoInstance.instance;
					autoInstance = autoInstances.remove(instance.getInstanceId());
					InstanceService.destroyInstance(instance);
					autoInstance.clear();
					player.setUseAutoGroup(0);
				}
			}
			
			if (autoInstance.agt.isDredgion() && DredgionService.getInstance().isDredgionAvailable() && KamarBattlefieldService.getInstance().isKamarAvailable() && JormungandService.getInstance().isJormungandAvailable() && SteelWallBastionBattlefieldService.getInstance().isSteelWallAvailable() && RunatoriumService.getInstance().isRunatoriumAvailable() && BalaurMarchingRouteService.getInstance().isBalaurMarchingAvailable() && RunatoriumRuinsService.getInstance().isRunatoriumRuinsAvailable() && GoldenCrucibleService.getInstance().isGoldenCrucibleAvailable() && SanctumBattlefieldService.getInstance().isSanctumBattlefieldAvailable() && PandaemoniumBattlefieldService.getInstance().isPandaemoniumBattlefieldAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			
			if (autoInstance.agt.isPvpArena() || autoInstance.agt.isPvPSoloArena())
			{
				if (player.getInventory().decreaseByItemId(186000135, 1))
				{
					PacketSendUtility.sendMessage(player, "[Anti-Abuse] You have refuse to enter, you have lost an arena ticket");
					return;
				}
			}
			
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 2));
		}
	}
	
	/**
	 * This method is called when a {@link Player} logs into the server.<br>
	 * It checks if the player can join various special instances and sends the appropriate {@code SM_AUTO_GROUP} packets.<br>
	 * It also handles logic for players who are currently looking for a party by updating their search status.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		if (DredgionService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(DredgionService.getInstance().getInstanceMaskId(player), 6));
		}
		else if (KamarBattlefieldService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(KamarBattlefieldService.maskId, 6));
		}
		else if (JormungandService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(JormungandService.maskId, 6));
		}
		else if (SteelWallBastionBattlefieldService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(SteelWallBastionBattlefieldService.maskId, 6));
		}
		else if (RunatoriumService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(RunatoriumService.maskId, 6));
		}
		else if (BalaurMarchingRouteService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(BalaurMarchingRouteService.maskId, 6));
		}
		else if (RunatoriumRuinsService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(RunatoriumRuinsService.maskId, 6));
		}
		else if (GoldenCrucibleService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(GoldenCrucibleService.maskId, 6));
		}
		else if (SanctumBattlefieldService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(SanctumBattlefieldService.maskId, 6));
		}
		else if (PandaemoniumBattlefieldService.getInstance().canPlayerJoin(player))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(PandaemoniumBattlefieldService.maskId, 6));
		}
		
		final Integer obj = player.getObjectId();
		final LookingForParty lfp = searchers.get(obj);
		if (lfp != null)
		{
			for (SearchInstance searchInstance : lfp.getSearchInstances())
			{
				if (searchInstance.getEntryRequestType().isGroupEntry() && !player.isInGroup2())
				{
					final int instanceMaskId = searchInstance.getInstanceMaskId();
					lfp.unregisterInstance(instanceMaskId);
					if (searchInstance.isDredgion() && DredgionService.getInstance().isDredgionAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (searchInstance.isKamar() && KamarBattlefieldService.getInstance().isKamarAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (searchInstance.isJormungand() && JormungandService.getInstance().isJormungandAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (searchInstance.isBastion() && SteelWallBastionBattlefieldService.getInstance().isSteelWallAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (searchInstance.isRunatorium() && RunatoriumService.getInstance().isRunatoriumAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (searchInstance.isBalaurMarching() && BalaurMarchingRouteService.getInstance().isBalaurMarchingAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (searchInstance.isRunatoriumRuins() && RunatoriumRuinsService.getInstance().isRunatoriumRuinsAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (searchInstance.isGoldenCrusible() && GoldenCrucibleService.getInstance().isGoldenCrucibleAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (searchInstance.isSanctumBattlefield() && SanctumBattlefieldService.getInstance().isSanctumBattlefieldAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (searchInstance.isPandaemoniumBattlefield() && PandaemoniumBattlefieldService.getInstance().isPandaemoniumBattlefieldAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 2));
					continue;
				}
				
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 8, searchInstance.getRemainingTime() + searchInstance.getEntryRequestType().getId(), player.getName()));
				if (searchInstance.isDredgion())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				else if (searchInstance.isKamar())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				else if (searchInstance.isJormungand())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				else if (searchInstance.isBastion())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				else if (searchInstance.isRunatorium())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				else if (searchInstance.isBalaurMarching())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				else if (searchInstance.isRunatoriumRuins())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				else if (searchInstance.isGoldenCrusible())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				else if (searchInstance.isSanctumBattlefield())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				else if (searchInstance.isPandaemoniumBattlefield())
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
			}
			
			if (lfp.getSearchInstances().isEmpty())
			{
				searchers.remove(obj);
				return;
			}
			
			lfp.setPlayer(player);
			for (SearchInstance si : lfp.getSearchInstances())
			{
				startSort(si.getEntryRequestType(), si.getInstanceMaskId(), true);
			}
		}
	}
	
	/**
	 * This method is called when a {@link Player} logs out of the game.<br>
	 * It cleans up any active search tasks for the player.<br>
	 * It also handles instance cleanup if the player was the last one online.
	 * @param player The {@code Player} object who is logging out.
	 */
	public void onPlayerLogOut(Player player)
	{
		final Integer obj = player.getObjectId();
		final int instanceId = player.getInstanceId();
		final LookingForParty lfp = searchers.get(obj);
		if (lfp != null)
		{
			lfp.setPlayer(null);
			if (lfp.isOnStartEnterTask())
			{
				for (AutoInstance autoInstance : autoInstances.values())
				{
					if (autoInstance.players.containsKey(obj) && !autoInstance.players.get(obj).isInInstance())
					{
						cancelEnter(player, autoInstance.agt.getInstanceMaskId());
					}
				}
			}
		}
		
		if (player.isInInstance())
		{
			AutoInstance autoInstance = autoInstances.get(instanceId);
			if ((autoInstance != null) && autoInstance.players.containsKey(obj))
			{
				final WorldMapInstance instance = autoInstance.instance;
				if (instance != null)
				{
					autoInstance.players.get(obj).setOnline(false);
					if (autoInstance.players.values().stream().filter(AGPlayer::isOnline).collect(Collectors.toList()).isEmpty())
					{
						autoInstance = autoInstances.remove(instanceId);
						InstanceService.destroyInstance(instance);
						autoInstance.clear();
						player.setUseAutoGroup(0);
					}
				}
			}
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} leaves this instance.<br>
	 * This method is called to clean up any specific data for the player.
	 * @param player The {@code Player} object who is leaving the instance.
	 */
	public void onLeaveInstance(Player player)
	{
		if (player.isInInstance())
		{
			final Integer obj = player.getObjectId();
			final int instanceId = player.getInstanceId();
			final AutoInstance autoInstance = autoInstances.get(instanceId);
			if ((autoInstance != null) && autoInstance.players.containsKey(obj))
			{
				autoInstance.onLeaveInstance(player);
				if (autoInstance.players.values().stream().filter(AGPlayer::isOnline).collect(Collectors.toList()).isEmpty())
				{
					final WorldMapInstance instance = autoInstance.instance;
					autoInstances.remove(instanceId);
					if (instance != null)
					{
						InstanceService.destroyInstance(instance);
						player.setUseAutoGroup(0);
					}
				}
				else if (autoInstance.agt.hasRegisterQuick())
				{
					startSort(EntryRequestType.QUICK_GROUP_ENTRY, autoInstance.agt.getInstanceMaskId(), false);
				}
			}
		}
	}
	
	/**
	 * This method handles the logic for sorting and processing group entries.<br>
	 * It manages quick group entries and initializes new groups based on player requests.<br>
	 * The process is synchronized to ensure thread safety during instance creation.
	 * @param ert The type of entry request being processed.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 * @param checkNewGroup A flag indicating whether to check and initialize a new group.
	 */
	private void startSort(EntryRequestType ert, Integer instanceMaskId, boolean checkNewGroup)
	{
		lock.lock();
		try
		{
			final Collection<Player> players = new HashSet<>();
			if (ert.isQuickGroupEntry())
			{
				for (LookingForParty lfp : searchers.values())
				{
					if ((lfp.getPlayer() == null) || lfp.isOnStartEnterTask())
					{
						continue;
					}
					
					for (AutoInstance autoInstance : autoInstances.values())
					{
						final int searchMaskId = autoInstance.agt.getInstanceMaskId();
						final SearchInstance searchInstance = lfp.getSearchInstance(searchMaskId);
						if ((searchInstance != null) && searchInstance.getEntryRequestType().isQuickGroupEntry())
						{
							final Player owner = lfp.getPlayer();
							if (autoInstance.addPlayer(owner, searchInstance).isAdded())
							{
								lfp.setStartEnterTime();
								if (lfp.unregisterInstance(searchMaskId) == 0)
								{
									players.add(owner);
								}
								
								PacketSendUtility.sendPacket(lfp.getPlayer(), new SM_AUTO_GROUP(searchMaskId, 4));
							}
						}
					}
				}
				
				for (Player p : players)
				{
					searchers.remove(p.getObjectId());
				}
				
				players.clear();
			}
			
			if (checkNewGroup)
			{
				final AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
				final AutoInstance autoInstance = agt.getAutoInstance();
				autoInstance.initialize(instanceMaskId);
				boolean canCreate = false;
				final Iterator<LookingForParty> iter = searchers.values().iterator();
				LookingForParty lfp;
				while (iter.hasNext())
				{
					lfp = iter.next();
					if ((lfp.getPlayer() == null) || lfp.isOnStartEnterTask())
					{
						continue;
					}
					
					final SearchInstance searchInstance = lfp.getSearchInstance(instanceMaskId);
					if (searchInstance != null)
					{
						if (searchInstance.getEntryRequestType().isGroupEntry())
						{
							if (!lfp.getPlayer().isInGroup2())
							{
								if (lfp.unregisterInstance(instanceMaskId) == 0)
								{
									iter.remove();
								}
								continue;
							}
						}
						
						final AGQuestion question = autoInstance.addPlayer(lfp.getPlayer(), searchInstance);
						if (!question.isFailed())
						{
							if (searchInstance.getEntryRequestType().isGroupEntry())
							{
								for (Player member : lfp.getPlayer().getPlayerGroup2().getOnlineMembers())
								{
									if (searchInstance.getMembers().contains(member.getObjectId()))
									{
										players.add(member);
									}
								}
							}
							else
							{
								players.add(lfp.getPlayer());
							}
						}
						
						if (question.isReady())
						{
							canCreate = true;
							break;
						}
					}
				}
				
				if (canCreate)
				{
					final WorldMapInstance instance = createInstance(agt.getInstanceMapId(), agt.getDifficultId());
					autoInstance.onInstanceCreate(instance);
					autoInstances.put(instance.getInstanceId(), autoInstance);
					for (Player player : players)
					{
						final Integer obj = player.getObjectId();
						lfp = searchers.get(obj);
						if (lfp != null)
						{
							lfp.setStartEnterTime();
							if (lfp.unregisterInstance(autoInstance.agt.getInstanceMaskId()) == 0)
							{
								searchers.remove(obj);
							}
						}
						
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 4));
					}
				}
				else
				{
					autoInstance.clear();
				}
				
				players.clear();
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Checks if a {@code Player} is allowed to enter an instance based on the request type and group type.<br>
	 * This method validates level requirements, instance availability, cooldowns, and group permissions.<br>
	 * It also verifies that group members meet all necessary criteria for entry.
	 * @param player The {@code Player} attempting to enter the instance.
	 * @param ert The type of entry request being made.
	 * @param agt The type of auto group associated with the instance.
	 * @return {@code true} if the player can enter, otherwise {@code false}.
	 */
	private boolean canEnter(Player player, EntryRequestType ert, AutoGroupType agt)
	{
		final int mapId = agt.getInstanceMapId();
		final int instanceMaskId = agt.getInstanceMaskId();
		if (!agt.hasLevelPermit(player.getLevel()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
			return false;
		}
		
		if (agt.isDredgion() && !DredgionService.getInstance().isDredgionAvailable())
		{
			return false;
		}
		else if ((agt.isPvPFFAArena() || agt.isPvPSoloArena() || agt.isHarmonyArena() || agt.isGloryArena()) && !PvPArenaService.isPvPArenaAvailable(player, agt))
		{
			return false;
		}
		else if (agt.isKamar() && !KamarBattlefieldService.getInstance().isKamarAvailable())
		{
			return false;
		}
		else if (agt.isJormungand() && !JormungandService.getInstance().isJormungandAvailable())
		{
			return false;
		}
		else if (agt.isSteelWall() && !SteelWallBastionBattlefieldService.getInstance().isSteelWallAvailable())
		{
			return false;
		}
		else if (agt.isRunatorium() && !RunatoriumService.getInstance().isRunatoriumAvailable())
		{
			return false;
		}
		else if (agt.isBalaurMarching() && !BalaurMarchingRouteService.getInstance().isBalaurMarchingAvailable())
		{
			return false;
		}
		else if (agt.isRunatoriumRuins() && !RunatoriumRuinsService.getInstance().isRunatoriumRuinsAvailable())
		{
			return false;
		}
		else if (agt.isGoldenCrucible() && !GoldenCrucibleService.getInstance().isGoldenCrucibleAvailable())
		{
			return false;
		}
		else if (agt.isSanctumBattlefield() && !SanctumBattlefieldService.getInstance().isSanctumBattlefieldAvailable())
		{
			return false;
		}
		else if (agt.isPandaemoniumBattlefield() && !PandaemoniumBattlefieldService.getInstance().isPandaemoniumBattlefieldAvailable())
		{
			return false;
		}
		else if (hasCoolDown(player, mapId))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_MAKE_INSTANCE_COOL_TIME);
			return false;
		}
		
		switch (ert)
		{
			case NEW_GROUP_ENTRY:
				if (!agt.hasRegisterNew())
				{
					return false;
				}
				break;
			case QUICK_GROUP_ENTRY:
				if (!agt.hasRegisterQuick())
				{
					return false;
				}
				break;
			case GROUP_ENTRY:
				if (!agt.hasRegisterGroup())
				{
					return false;
				}
				
				final PlayerGroup group = player.getPlayerGroup2();
				if ((group == null) || !group.isLeader(player))
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_NOT_LEADER);
					return false;
				}
				
				if (agt.isHarmonyArena() || agt.isTrainingHarmonyArena())
				{
					if (group.getOnlineMembers().size() > 3)
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_TOO_MANY_MEMBERS(3, Integer.toString(mapId)));
						return false;
					}
				}
				
				for (Player member : group.getMembers())
				{
					if (group.getLeaderObject().equals(member))
					{
						continue;
					}
					
					final LookingForParty lfp = searchers.get(member.getObjectId());
					if ((lfp != null) && lfp.isRegistredInstance(instanceMaskId))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					
					if (agt.isHarmonyArena() && !PvPArenaService.checkItem(member, agt))
					{
						PacketSendUtility.sendPacket(member, new SM_SYSTEM_MESSAGE(1400219, agt.getInstanceMapId()));
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					
					if (agt.isDredgion() && DredgionService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (agt.isKamar() && KamarBattlefieldService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (agt.isJormungand() && JormungandService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (agt.isSteelWall() && SteelWallBastionBattlefieldService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (agt.isRunatorium() && RunatoriumService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (agt.isBalaurMarching() && BalaurMarchingRouteService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (agt.isRunatoriumRuins() && RunatoriumRuinsService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (agt.isGoldenCrucible() && GoldenCrucibleService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (agt.isSanctumBattlefield() && SanctumBattlefieldService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (agt.isPandaemoniumBattlefield() && PandaemoniumBattlefieldService.getInstance().hasCoolDown(member))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (hasCoolDown(member, mapId))
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					
					if (!agt.hasLevelPermit(member.getLevel()))
					{
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
				}
				break;
		}
		
		return true;
	}
	
	/**
	 * Finds a specific {@link AutoInstance} for a given player.<br>
	 * It checks if the instance matches the provided {@code instanceMaskId}.<br>
	 * The method returns the first matching instance found in the collection.
	 * @param player The {@link Player} object to check.
	 * @param instanceMaskId The unique identifier for the instance type.
	 * @return The matching {@link AutoInstance} or {@code null} if no match is found.
	 */
	private AutoInstance getAutoInstance(Player player, int instanceMaskId)
	{
		for (AutoInstance autoInstance : autoInstances.values())
		{
			if ((autoInstance.agt.getInstanceMaskId() == instanceMaskId) && autoInstance.players.containsKey(player.getObjectId()))
			{
				return autoInstance;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if a {@link Player} is currently restricted from entering an instance.<br>
	 * This method calculates the delay based on the world ID and player rates.<br>
	 * It returns {@code true} if the cooldown period has not yet expired.
	 * @param player The {@link Player} object to check.
	 * @param worldId The unique identifier for the instance world.
	 * @return {@code true} if the player is on cooldown, otherwise {@code false}.
	 */
	private boolean hasCoolDown(Player player, int worldId)
	{
		final int instanceCooldownRate = InstanceService.getInstanceRate(player, worldId);
		int useDelay = 0;
		int instanceCooldown = 0;
		final InstanceCooltime clt = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId);
		if (clt != null)
		{
			instanceCooldown = clt.getEntCoolTime();
		}
		
		if (instanceCooldownRate > 0)
		{
			useDelay = instanceCooldown / instanceCooldownRate;
		}
		
		return player.getPortalCooldownList().isPortalUseDisabled(worldId) && (useDelay > 0);
	}
	
	/**
	 * Creates a new {@link WorldMapInstance} for a specific map and difficulty.<br>
	 * This method handles the registration of the instance with the world map.<br>
	 * It also triggers the spawning process via {@link SpawnEngine}.
	 * @param worldId The unique identifier for the world map.
	 * @param difficultId The difficulty level for the new instance.
	 * @return The newly created {@code WorldMapInstance} object.
	 */
	private WorldMapInstance createInstance(int worldId, byte difficultId)
	{
		final WorldMap map = World.getInstance().getWorldMap(worldId);
		final int nextInstanceId = map.getNextInstanceId();
		final WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId);
		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId(), difficultId);
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		return worldMapInstance;
	}
	
	/**
	 * Initiates a penalty period for a specific object.<br>
	 * This method adds the {@code obj} to the penalty list.<br>
	 * It schedules a task to remove it after 10000 milliseconds.
	 * @param obj The object to be penalized.
	 */
	private void startPenalty(Integer obj)
	{
		if (penaltys.contains(obj))
		{
			penaltys.remove(obj);
		}
		
		penaltys.add(obj);
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			if (penaltys.contains(obj))
			{
				penaltys.remove(obj);
			}
		}, 10000);
	}
	
	/**
	 * Removes a specific instance from the search registry.<br>
	 * This method updates all active searches associated with the given ID.<br>
	 * It ensures that players are no longer looking for this instance type.
	 * @param instanceMaskId The unique identifier of the instance to unregister.
	 */
	public void unRegisterInstance(int instanceMaskId)
	{
		for (LookingForParty lfp : searchers.values())
		{
			if (lfp.isRegistredInstance(instanceMaskId))
			{
				if (lfp.getPlayer() != null)
				{
					getInstance().unregisterLooking(lfp.getPlayer(), instanceMaskId);
				}
				else
				{
					getInstance().unRegisterSearchInstance(null, lfp.getSearchInstance(instanceMaskId));
					if (lfp.unregisterInstance(instanceMaskId) == 0)
					{
						searchers.values().remove(lfp);
					}
				}
			}
		}
	}
	
	/**
	 * Removes a search instance and notifies the relevant players.<br>
	 * This method checks if the instance is a group entry type.<br>
	 * It sends an {@code SM_AUTO_GROUP} packet to all members and the primary player.<br>
	 * The notification depends on whether the specific battlefield service is currently available.
	 * @param player The {@link Player} who initiated or is associated with the search.
	 * @param si The {@link SearchInstance} being unregistered.
	 */
	private void unRegisterSearchInstance(Player player, SearchInstance si)
	{
		final int instanceMaskId = si.getInstanceMaskId();
		if (si.getEntryRequestType().isGroupEntry() && (si.getMembers() != null))
		{
			for (Integer obj : si.getMembers())
			{
				final Player member = World.getInstance().findPlayer(obj);
				if (member != null)
				{
					if (si.isDredgion() && DredgionService.getInstance().isDredgionAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (si.isKamar() && KamarBattlefieldService.getInstance().isKamarAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (si.isJormungand() && JormungandService.getInstance().isJormungandAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (si.isBastion() && SteelWallBastionBattlefieldService.getInstance().isSteelWallAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (si.isRunatorium() && RunatoriumService.getInstance().isRunatoriumAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (si.isBalaurMarching() && BalaurMarchingRouteService.getInstance().isBalaurMarchingAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (si.isRunatoriumRuins() && RunatoriumRuinsService.getInstance().isRunatoriumRuinsAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (si.isGoldenCrusible() && GoldenCrucibleService.getInstance().isGoldenCrucibleAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (si.isSanctumBattlefield() && SanctumBattlefieldService.getInstance().isSanctumBattlefieldAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					else if (si.isPandaemoniumBattlefield() && PandaemoniumBattlefieldService.getInstance().isPandaemoniumBattlefieldAvailable())
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 2));
				}
			}
		}
		
		if (player != null)
		{
			if (si.isDredgion() && DredgionService.getInstance().isDredgionAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			else if (si.isKamar() && KamarBattlefieldService.getInstance().isKamarAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			else if (si.isJormungand() && JormungandService.getInstance().isJormungandAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			else if (si.isBastion() && SteelWallBastionBattlefieldService.getInstance().isSteelWallAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			else if (si.isRunatorium() && RunatoriumService.getInstance().isRunatoriumAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			else if (si.isBalaurMarching() && BalaurMarchingRouteService.getInstance().isBalaurMarchingAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			else if (si.isRunatoriumRuins() && RunatoriumRuinsService.getInstance().isRunatoriumRuinsAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			else if (si.isGoldenCrusible() && GoldenCrucibleService.getInstance().isGoldenCrucibleAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			else if (si.isSanctumBattlefield() && SanctumBattlefieldService.getInstance().isSanctumBattlefieldAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			else if (si.isPandaemoniumBattlefield() && PandaemoniumBattlefieldService.getInstance().isPandaemoniumBattlefieldAvailable())
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 2));
		}
	}
	
	/**
	 * Removes an instance from the active list.<br>
	 * This method destroys the associated {@code WorldMapInstance} if it exists.<br>
	 * It also clears the data for the removed {@code AutoInstance}.
	 * @param instanceId The unique identifier of the instance to remove.
	 */
	public void unRegisterInstance(Integer instanceId)
	{
		final AutoInstance autoInstance = autoInstances.remove(instanceId);
		if (autoInstance != null)
		{
			final WorldMapInstance instance = autoInstance.instance;
			if (instance != null)
			{
				InstanceService.destroyInstance(instance);
			}
			
			autoInstance.clear();
		}
	}
	
	/**
	 * Sends auto group entry packets to the specified player.<br>
	 * This method updates various battlefield and instance entries.
	 * @param player The {@code Player} receiving the packets.
	 * @param removeit A {@code Boolean} indicating whether to remove the entry.
	 */
	private void setAutoGroupEntryIcon(Player player, Boolean removeit)
	{
		// Dredgion, ChantraDredgion,TerathDredgion,
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(1, 6, removeit));
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(2, 6, removeit));
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(3, 6, removeit));
		
		// Kamar BattleField
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(107, 6, removeit));
		
		// Jormungand Marching Route
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(108, 6, removeit));
		
		// Steel Wall Bastion Battlefield
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(109, 6, removeit));
		
		// Runatorium
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(111, 6, removeit));
		
		// Ashunatal Dredgion
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(121, 6, removeit));
		
		// Balaur Marching Route
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(122, 6, removeit));
		
		// Runatorium Ruins
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(123, 6, removeit));
		
		// Golden Crusible
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(125, 6, removeit));
		
		// Sanctum Battlefield
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(416, 6, removeit));
		
		// Pandaemonium Battlefield
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(417, 6, removeit));
	}
	
	/**
	 * Checks if a specific instance is registered as an auto instance.<br>
	 * This method looks up the {@code instanceId} in the internal collection.
	 * @param instanceId The unique identifier of the instance to check.
	 * @return {@code true} if the instance exists in the auto instances list, {@code false} otherwise.
	 */
	public boolean isAutoInstance(int instanceId)
	{
		return autoInstances.containsKey(instanceId);
	}
	
	/**
	 * Retrieves the singleton instance of the {@link AutoGroupService}.<br>
	 * Use this method to access the global service for auto-grouping logic.
	 * @return The active {@code AutoGroupService} instance.
	 */
	public static AutoGroupService getInstance()
	{
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder
	{
		private static final AutoGroupService INSTANCE = new AutoGroupService();
	}
}
