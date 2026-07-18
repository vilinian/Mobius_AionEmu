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
package com.aionemu.gameserver.services.drop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.configs.main.DropConfig;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.DropNpc;
import com.aionemu.gameserver.model.gameobjects.Npc;
// import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.templates.event.EventDrop;
import com.aionemu.gameserver.model.templates.event.EventTemplate;
import com.aionemu.gameserver.model.templates.housing.HouseType;
import com.aionemu.gameserver.model.templates.pet.PetFunctionType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MINIONS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.events.EventService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.stats.DropRewardEnum;
// import com.aionemu.gameserver.world.World;

/**
 * Manages the registration and handling of loot drops within the game world.<br>
 * This service coordinates how {@link NpcDrop} objects are generated and distributed to players.
 * @author xTz
 */
public class DropRegistrationService
{
	private final Map<Integer, Set<DropItem>> currentDropMap = new ConcurrentHashMap<>();
	private final Map<Integer, DropNpc> dropRegistrationMap = new ConcurrentHashMap<>();
	private final List<Integer> noReductionMaps;
	
	/**
	 * Registers a drop for a specific {@link Npc} and its associated players.<br>
	 * This method handles the loot registration logic for a kill event.<br>
	 * It uses the level of the primary {@code player} to determine the drop quality.
	 * @param npc The {@link Npc} that was defeated.
	 * @param player The main {@link Player} who triggered the drop.
	 * @param groupMembers A collection of other {@link Player} objects in the same group.
	 */
	public void registerDrop(Npc npc, Player player, Collection<Player> groupMembers)
	{
		registerDrop(npc, player, player.getLevel(), groupMembers);
	}
	
	/**
	 * Private constructor for the {@link DropRegistrationService} class.<br>
	 * This constructor initializes the service and sets up internal data structures.<br>
	 * It also parses zone IDs from the {@code DropConfig}.
	 */
	private DropRegistrationService()
	{
		init();
		noReductionMaps = new ArrayList<>();
		for (String zone : DropConfig.DISABLE_DROP_REDUCTION_IN_ZONES.split(","))
		{
			noReductionMaps.add(Integer.parseInt(zone));
		}
	}
	
	/**
	 * Initializes the internal data structures for this object.<br>
	 * It populates the {@code items} map from the source list.<br>
	 * This method sets the {@code v} field to {@code null} after completion.
	 */
	public void init()
	{
	}
	
	/**
	 * Registers the loot drops for a specific {@link Npc} after it is defeated.<br>
	 * This method calculates drop rates based on player stats, group rules, and event configurations.<br>
	 * It also handles automatic looting by pets or minions if they are active.
	 * @param npc The {@link Npc} that was killed to trigger the drops.
	 * @param player The {@link Player} who initiated the kill.
	 * @param heighestLevel The level used to calculate drop reduction.
	 * @param groupMembers A collection of players in the same group or alliance as the player.
	 */
	public void registerDrop(Npc npc, Player player, int heighestLevel, Collection<Player> groupMembers)
	{
		if (player == null)
		{
			return;
		}
		
		final int npcObjId = npc.getObjectId();
		
		// Getting all possible drops for this Npc
		final NpcDrop npcDrop = npc.getNpcDrop();
		final Set<DropItem> droppedItems = new HashSet<>();
		int index = 1;
		int dropChance = 100;
		final int npcLevel = npc.getLevel();
		final boolean isChest = npc.getAi2().getName().equals("chest");
		if (!DropConfig.DISABLE_DROP_REDUCTION && (((isChest && (npcLevel != 1)) || !isChest)) && !noReductionMaps.contains(npc.getWorldId()))
		{
			dropChance = DropRewardEnum.dropRewardFrom(npcLevel - heighestLevel); // reduce chance depending on level
		}
		
		// Generete drop by this player
		Player genesis = player;
		Integer winnerObj = 0;
		
		// Distributing drops to players
		final Collection<Player> dropPlayers = new ArrayList<>();
		Collection<Player> winningPlayers = new ArrayList<>();
		if (player.isInGroup2() || player.isInAlliance2())
		{
			final List<Integer> dropMembers = new ArrayList<>();
			final LootGroupRules lootGrouRules = player.getLootGroupRules();
			
			switch (lootGrouRules.getLootRule())
			{
				case ROUNDROBIN:
					final int size = groupMembers.size();
					if (size > lootGrouRules.getNrRoundRobin())
					{
						lootGrouRules.setNrRoundRobin(lootGrouRules.getNrRoundRobin() + 1);
					}
					else
					{
						lootGrouRules.setNrRoundRobin(1);
					}
					
					int i = 0;
					for (Player p : groupMembers)
					{
						i++;
						if (i == lootGrouRules.getNrRoundRobin())
						{
							winningPlayers.add(p);
							winnerObj = p.getObjectId();
							setItemsToWinner(droppedItems, winnerObj);
							genesis = p;
							break;
						}
					}
					break;
				case FREEFORALL:
					winningPlayers = groupMembers;
					break;
				case LEADER:
					final Player leader = player.isInGroup2() ? player.getPlayerGroup2().getLeaderObject() : player.getPlayerAlliance2().getLeaderObject();
					winningPlayers.add(leader);
					winnerObj = leader.getObjectId();
					setItemsToWinner(droppedItems, winnerObj);
					
					genesis = leader;
					break;
			}
			
			for (Player member : winningPlayers)
			{
				dropMembers.add(member.getObjectId());
				dropPlayers.add(member);
			}
			
			final DropNpc dropNpc = new DropNpc(npcObjId);
			dropRegistrationMap.put(npcObjId, dropNpc);
			dropNpc.setPlayersObjectId(dropMembers);
			dropNpc.setInRangePlayers(groupMembers);
			dropNpc.setGroupSize(groupMembers.size());
		}
		else
		{
			final List<Integer> singlePlayer = new ArrayList<>();
			singlePlayer.add(player.getObjectId());
			dropPlayers.add(player);
			dropRegistrationMap.put(npcObjId, new DropNpc(npcObjId));
			dropRegistrationMap.get(npcObjId).setPlayersObjectId(singlePlayer);
		}
		
		// Drop rate from NPC can be boosted by Spiritmaster Erosion skill
		float boostDropRate = npc.getGameStats().getStat(StatEnum.BOOST_DROP_RATE, 100).getCurrent() / 100f;
		
		// Drop rate can be boosted by player buff too
		boostDropRate += genesis.getGameStats().getStat(StatEnum.DR_BOOST, 0).getCurrent() / 100f;
		
		// Some personal drops boost the EoR drop rate by 10%.
		boostDropRate += genesis.getCommonData().getCurrentReposteEnergy() > 0 ? 0.1f : 0;
		
		// EoS 5% Boost drop rate
		boostDropRate += genesis.getCommonData().getCurrentSalvationPercent() > 0 ? 0.05f : 0;
		
		// Deed to Palace 5% Boost drop rate
		boostDropRate += genesis.getActiveHouse() != null ? genesis.getActiveHouse().getHouseType().equals(HouseType.PALACE) ? 0.05f : 0 : 0;
		// Hmm.. 169625013 have boost drop rate 5% info but no such desc on buff
		
		// Online Time 10% Boost drop rate
		boostDropRate += genesis.getBonusTime().isBonus() ? 0.1f : 0;
		
		// can be exploited on duel with Spiritmaster Erosion skill
		boostDropRate += (genesis.getGameStats().getStat(StatEnum.BOOST_DROP_RATE, 100).getCurrent() / 100f) - 1;
		
		final float dropRate = ((genesis.getRates().getDropRate() * boostDropRate * dropChance) / 100F);
		
		if (npcDrop != null)
		{
			index = npcDrop.dropCalculator(droppedItems, index, dropRate, genesis.getRace(), groupMembers);
		}
		
		// Updating current dropMap
		currentDropMap.put(npcObjId, droppedItems);
		
		index = QuestService.getQuestDrop(droppedItems, index, npc, groupMembers, genesis);
		
		if (EventsConfig.ENABLE_EVENT_SERVICE)
		{
			final List<EventTemplate> activeEvents = EventService.getInstance().getActiveEvents();
			for (EventTemplate eventTemplate : activeEvents)
			{
				if (eventTemplate.EventDrop() == null)
				{
					continue;
				}
				
				final List<EventDrop> eventDrops = eventTemplate.EventDrop().getEventDrops();
				for (EventDrop eventDrop : eventDrops)
				{
					final int diff = npc.getLevel() - eventDrop.getItemTemplate().getLevel();
					final int minDiff = eventDrop.getMinDiff();
					final int maxDiff = eventDrop.getMaxDiff();
					if (minDiff != 0)
					{
						if (diff < eventDrop.getMinDiff())
						{
							continue;
						}
					}
					
					if (maxDiff != 0)
					{
						if (diff > eventDrop.getMaxDiff())
						{
							continue;
						}
					}
					
					float percent = eventDrop.getChance();
					percent *= dropRate;
					if ((Rnd.get() * 100) > percent)
					{
						continue;
					}
					
					droppedItems.add(regDropItem(index++, winnerObj, npcObjId, eventDrop.getItemId(), eventDrop.getCount()));
				}
			}
		}
		
		if (npc.getPosition().isInstanceMap())
		{
			npc.getPosition().getWorldMapInstance().getInstanceHandler().onDropRegistered(npc);
		}
		else
		{
			npc.getPosition().getWorld().getWorldMap(npc.getWorldId()).getWorldHandler().onDropRegistered(npc);
		}
		
		npc.getAi2().onGeneralEvent(AIEventType.DROP_REGISTERED);
		
		for (Player p : dropPlayers)
		{
			PacketSendUtility.sendPacket(p, new SM_LOOT_STATUS(npcObjId, 0));
		}
		
		if ((player.getPet() != null) && (player.getPet().getPetTemplate().getPetFunction(PetFunctionType.LOOT) != null) && player.getPet().getCommonData().isLooting())
		{
			PacketSendUtility.sendPacket(player, new SM_PET(true, npcObjId));
			final Set<DropItem> drops = getCurrentDropMap().get(npcObjId);
			if ((drops == null) || (drops.size() == 0))
			{
				npc.getController().onDelete();
			}
			else
			{
				final DropItem[] dropItems = drops.toArray(new DropItem[drops.size()]);
				for (int i = 0; i < dropItems.length; i++)
				{
					DropService.getInstance().requestDropItem(player, npcObjId, dropItems[i].getIndex(), true);
				}
			}
			
			PacketSendUtility.sendPacket(player, new SM_PET(false, npcObjId));
			
			// if everything was looted, npc is deleted
			if ((drops == null) || (drops.size() == 0))
			{
				return;
			}
		}
		
		// Minion auto loot
		if ((player.getMinion() != null) && player.getMinion().getCommonData().isLooting())
		{
			PacketSendUtility.sendPacket(player, new SM_MINIONS(true, npcObjId));
			final Set<DropItem> drops = getCurrentDropMap().get(npcObjId);
			if ((drops == null) || (drops.size() == 0))
			{
				npc.getController().onDelete();
			}
			else
			{
				final DropItem[] dropItems = drops.toArray(new DropItem[drops.size()]);
				for (int i = 0; i < dropItems.length; i++)
				{
					DropService.getInstance().requestDropItem(player, npcObjId, dropItems[i].getIndex(), true);
				}
			}
			
			PacketSendUtility.sendPacket(player, new SM_MINIONS(false, npcObjId));
			
			// if everything was looted, npc is deleted
			if ((drops == null) || (drops.size() == 0))
			{
				return;
			}
		}
		
		final Set<DropItem> drops = getCurrentDropMap().get(npcObjId);
		
		if ((drops == null) || (drops.size() == 0))
		{
			ThreadPoolManager.getInstance().schedule(() -> npc.getController().onDelete(), 300); // <--TEST--> // Old 1Sec Delay !! DO NOT REMOVE !! (Negative effect's on onBeforeDie and some other's)
		}
		else
		{
			DropService.getInstance().scheduleFreeForAll(npcObjId);
		}
	}
	
	/**
	 * Assigns the dropped items to a specific winner.<br>
	 * This method updates the {@code playerObjId} for each item in the set.<br>
	 * It only assigns items that are not marked as "each member" drops.
	 * @param droppedItems The set of {@link DropItem} objects to process.
	 * @param obj The unique identifier of the winning object.
	 */
	public void setItemsToWinner(Set<DropItem> droppedItems, Integer obj)
	{
		for (DropItem dropItem : droppedItems)
		{
			if (!dropItem.getDropTemplate().isEachMember())
			{
				dropItem.setPlayerObjId(obj);
			}
		}
	}
	
	/**
	 * Registers a new {@code DropItem} into the system.<br>
	 * This method creates and initializes a drop object with specific properties.
	 * @param index The unique index for the dropped item.
	 * @param playerObjId The ID of the player who received the item.
	 * @param objId The ID of the NPC or object that dropped the item.
	 * @param itemId The unique identifier for the item type.
	 * @param count The quantity of the item to be dropped.
	 * @return The newly created {@code DropItem} object.
	 */
	public DropItem regDropItem(int index, int playerObjId, int objId, int itemId, long count)
	{
		final DropItem item = new DropItem(new Drop(itemId, 1, 1, 100, false));
		item.setPlayerObjId(playerObjId);
		item.setNpcObj(objId);
		item.setCount(count);
		item.setIndex(index);
		return item;
	}
	
	/**
	 * Retrieves the current map of registered drops.<br>
	 * This map links unique identifiers to their corresponding {@link DropNpc} objects.
	 * @return A {@code Map<Integer, DropNpc>} containing all active drop registrations.
	 */
	public Map<Integer, DropNpc> getDropRegistrationMap()
	{
		return dropRegistrationMap;
	}
	
	/**
	 * Retrieves the map of active drops currently in progress.<br>
	 * The keys represent unique identifiers for each drop event.<br>
	 * Each value is a {@code Set} containing the {@link DropItem} objects associated with that ID.
	 * @return A {@code Map} where the key is an {@code Integer} and the value is a {@code Set} of {@link DropItem}.
	 */
	public Map<Integer, Set<DropItem>> getCurrentDropMap()
	{
		return currentDropMap;
	}
	
	/**
	 * Provides the global instance of the {@link DropRegistrationService}.<br>
	 * This method follows the singleton pattern to ensure only one service exists.
	 * @return The single instance of {@code DropRegistrationService}.
	 */
	public static DropRegistrationService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DropRegistrationService instance = new DropRegistrationService();
	}
}
