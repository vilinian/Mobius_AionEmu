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
package com.aionemu.gameserver.controllers.attack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * Manages the list of targets currently threatening a specific {@link Creature}.<br>
 * It tracks which entities have initiated attacks or actions to determine target priority.<br>
 * This class helps the AI decide which {@link AionObject} to engage next.
 * @author ATracer, KKnD
 */
public class AggroList
{
	protected final Creature owner;
	private final Map<Integer, AggroInfo> aggroList = new ConcurrentHashMap<>();
	
	/**
	 * Creates a new {@code AggroList} for a specific creature.<br>
	 * This object tracks the aggression levels of nearby entities.
	 * @param owner The {@link Creature} that owns this aggro list.
	 */
	public AggroList(Creature owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Adds damage dealt by a specific creature to the aggro list.<br>
	 * This method updates the hate level of the {@code attacker}.<br>
	 * It also triggers an attack event for the {@link Creature} owner.
	 * @param attacker The {@code Creature} that is performing the attack.
	 * @param damage The amount of damage to add.
	 */
	public void addDamage(Creature attacker, int damage)
	{
		if (!isAware(attacker))
		{
			return;
		}
		
		final AggroInfo ai = getAggroInfo(attacker);
		ai.addDamage(damage);
		/**
		 * For now we add hate equal to each damage received Additionally there will be broadcast of extra hate
		 */
		ai.addHate(damage);
		
		// TODO move out to controller
		owner.getAi2().onCreatureEvent(AIEventType.ATTACK, attacker);
	}
	
	/**
	 * Increases the hate value for a specific creature.<br>
	 * This method checks if the {@code creature} is aware before updating its score.<br>
	 * It calls {@code int)} to apply the change.
	 * @param creature The {@code Creature} to receive more hate.
	 * @param hate The amount of hate to add.
	 */
	public void addHate(Creature creature, int hate)
	{
		if (!isAware(creature))
		{
			return;
		}
		
		addHateValue(creature, hate);
	}
	
	/**
	 * Starts the hate process for a specific {@code Creature}.<br>
	 * This method adds an initial hate value of {@code 1} to the target.
	 * @param creature The {@code Creature} that will begin generating hate.
	 */
	public void startHate(Creature creature)
	{
		addHateValue(creature, 1);
	}
	
	/**
	 * Increases the hate value for a specific {@code Creature}.<br>
	 * This method updates the internal aggro list and triggers AI events.<br>
	 * It also handles special quest logic if an {@code Npc} owner attacks a {@code Player}.
	 * @param creature The {@code Creature} to receive the hate value.
	 * @param hate The amount of hate to add.
	 */
	protected void addHateValue(Creature creature, int hate)
	{
		final AggroInfo ai = getAggroInfo(creature);
		ai.addHate(hate);
		
		// TODO move out to controller
		if ((creature instanceof Player) && (owner instanceof Npc))
		{
			for (Player player : owner.getKnownList().getKnownPlayers().values())
			{
				if (MathUtil.isIn3dRange(owner, player, 50))
				{
					QuestEngine.getInstance().onAddAggroList(new QuestEnv(owner, player, 0, 0));
				}
			}
		}
		
		owner.getAi2().onCreatureEvent(AIEventType.ATTACK, creature);
	}
	
	/**
	 * Finds the object that dealt the highest amount of damage.<br>
	 * This method ignores damage caused by the {@code owner}.<br>
	 * It returns {@code null} if no valid attacker is found.
	 * @return The {@link AionObject} responsible for the most damage, or {@code null}.
	 */
	public AionObject getMostDamage()
	{
		AionObject mostDamage = null;
		int maxDamage = 0;
		
		for (AggroInfo ai : getFinalDamageList(true))
		{
			if ((ai.getAttacker() == null) || owner.equals(ai.getAttacker()))
			{
				continue;
			}
			
			if (ai.getDamage() > maxDamage)
			{
				mostDamage = ai.getAttacker();
				maxDamage = ai.getDamage();
			}
		}
		
		return mostDamage;
	}
	
	/**
	 * Identifies the race of the player who dealt the most damage.<br>
	 * This method checks if the top damage source is a {@link Player} or a {@link PlayerGroup}.<br>
	 * It returns {@code null} if the winner is not a player entity.
	 * @return The {@code Race} of the winning player, or {@code null} if no player won.
	 */
	public Race getPlayerWinnerRace()
	{
		final AionObject winner = getMostDamage();
		if (winner instanceof PlayerGroup)
		{
			return ((PlayerGroup) winner).getRace();
		}
		else if (winner instanceof Player)
		{
			return ((Player) winner).getRace();
		}
		
		return null;
	}
	
	/**
	 * Finds the {@link Player} who dealt the highest amount of damage.<br>
	 * This method checks all attackers in the final damage list.<br>
	 * It returns {@code null} if no players have dealt any damage.
	 * @return The {@link Player} with the maximum damage, or {@code null}.
	 */
	public Player getMostPlayerDamage()
	{
		if (aggroList.isEmpty())
		{
			return null;
		}
		
		Player mostDamage = null;
		int maxDamage = 0;
		
		// Use final damage list to get pet damage as well.
		for (AggroInfo ai : getFinalDamageList(false))
		{
			if ((ai.getDamage() > maxDamage) && (ai.getAttacker() instanceof Player))
			{
				mostDamage = (Player) ai.getAttacker();
				maxDamage = ai.getDamage();
			}
		}
		
		return mostDamage;
	}
	
	/**
	 * Finds the {@link Player} who dealt the most damage from a specific team.<br>
	 * It filters attackers by their membership in the provided collection.<br>
	 * If the top attacker is a mentor, it returns a member at the specified level.
	 * @param team The collection of {@link Player} objects to check for membership.
	 * @param highestLevel The level used to identify a specific member if a mentor is found.
	 * @return The {@link Player} with the highest damage, or {@code null} if no valid player is found.
	 */
	public Player getMostPlayerDamageOfMembers(Collection<Player> team, int highestLevel)
	{
		if (aggroList.isEmpty())
		{
			return null;
		}
		
		Player mostDamage = null;
		int maxDamage = 0;
		
		// Use final damage list to get pet damage as well.
		for (AggroInfo ai : getFinalDamageList(false))
		{
			if (!(ai.getAttacker() instanceof Player) || !team.contains(ai.getAttacker()))
			{
				continue;
			}
			
			if (ai.getDamage() > maxDamage)
			{
				mostDamage = (Player) ai.getAttacker();
				maxDamage = ai.getDamage();
			}
		}
		
		if ((mostDamage != null) && mostDamage.isMentor())
		{
			for (Player member : team)
			{
				if (member.getLevel() == highestLevel)
				{
					mostDamage = member;
				}
			}
		}
		
		return mostDamage;
	}
	
	/**
	 * Finds the creature that has caused the most hate.<br>
	 * This method iterates through the {@code aggroList}.<br>
	 * It returns the attacker with the highest hate value.<br>
	 * If no creatures are in the list, it returns {@code null}.
	 * @return The {@link Creature} with the highest hate level or {@code null}.
	 */
	public Creature getMostHated()
	{
		if (aggroList.isEmpty())
		{
			return null;
		}
		
		Creature mostHated = null;
		int maxHate = 0;
		
		for (Map.Entry<Integer, AggroInfo> e : aggroList.entrySet())
		{
			final AggroInfo ai = e.getValue();
			if (ai == null)
			{
				continue;
			}
			
			// aggroList will never contain anything but creatures
			final Creature attacker = (Creature) ai.getAttacker();
			
			if (attacker.getLifeStats().isAlreadyDead())
			{
				ai.setHate(0);
			}
			
			if (ai.getHate() > maxHate)
			{
				mostHated = attacker;
				maxHate = ai.getHate();
			}
		}
		
		return mostHated;
	}
	
	/**
	 * Checks if the specified {@code creature} is currently the most hated target.<br>
	 * This method returns {@code false} if the input is {@code null} or already dead.<br>
	 * It compares the provided object against the result of {@code getMostHated}.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is the most hated, otherwise {@code false}.
	 */
	public boolean isMostHated(Creature creature)
	{
		if ((creature == null) || creature.getLifeStats().isAlreadyDead())
		{
			return false;
		}
		
		final Creature mostHated = getMostHated();
		return (mostHated != null) && mostHated.equals(creature);
	}
	
	/**
	 * Updates the hate level for a specific {@link Creature}.<br>
	 * This method only works if the creature is already being hated.<br>
	 * It calls {@code int)} to increase the value.
	 * @param creature The {@code Creature} to update.
	 * @param value The amount of hate to add.
	 */
	public void notifyHate(Creature creature, int value)
	{
		if (isHating(creature))
		{
			addHate(creature, value);
		}
	}
	
	/**
	 * Removes the hate value for a specific object.<br>
	 * This method sets the {@code hate} of the {@link AggroInfo} to {@code 0}.<br>
	 * It checks if the {@code creature} exists in the current list.
	 * @param creature The {@code VisibleObject} to stop hating.
	 */
	public void stopHating(VisibleObject creature)
	{
		final AggroInfo aggroInfo = aggroList.get(creature.getObjectId());
		if (aggroInfo != null)
		{
			aggroInfo.setHate(0);
		}
	}
	
	/**
	 * Removes a specific {@link Creature} from the aggro list.<br>
	 * This method uses the unique object ID of the creature to perform the removal.
	 * @param creature The {@code Creature} to be removed from the list.
	 */
	public void remove(Creature creature)
	{
		aggroList.remove(creature.getObjectId());
	}
	
	/**
	 * Removes all entries from the internal aggro list.<br>
	 * This resets the current hate data for all creatures.<br>
	 * The {@code size()} of the list will become 0 after this call.
	 */
	public void clear()
	{
		aggroList.clear();
	}
	
	/**
	 * Retrieves the {@code AggroInfo} for a specific {@link Creature}.<br>
	 * If no information exists, it creates and stores a new {@code AggroInfo} object.
	 * @param creature The {@link Creature} to check for aggro data.
	 * @return The {@code AggroInfo} associated with the provided {@code creature}.
	 */
	public AggroInfo getAggroInfo(Creature creature)
	{
		AggroInfo ai = aggroList.get(creature.getObjectId());
		if (ai == null)
		{
			ai = new AggroInfo(creature);
			aggroList.put(creature.getObjectId(), ai);
		}
		
		return ai;
	}
	
	/**
	 * Checks if a specific {@link Creature} is currently in the hate list.<br>
	 * It returns {@code true} if the creature exists in the internal map.<br>
	 * It returns {@code false} otherwise.
	 * @param creature The {@link Creature} to check.
	 * @return {@code true} if the creature is hating, {@code false} otherwise.
	 */
	public boolean isHating(Creature creature)
	{
		return aggroList.containsKey(creature.getObjectId());
	}
	
	/**
	 * Retrieves all current aggression information.<br>
	 * This method returns the values stored in the internal {@code aggroList}.
	 * @return A {@code Collection} of {@link AggroInfo} objects.
	 */
	public Collection<AggroInfo> getList()
	{
		return aggroList.values();
	}
	
	/**
	 * Calculates the sum of all damage values in the list.<br>
	 * It iterates through every {@link AggroInfo} object stored in the internal map.
	 * @return The total accumulated damage as an {@code int}.
	 */
	public int getTotalDamage()
	{
		int totalDamage = 0;
		for (AggroInfo ai : aggroList.values())
		{
			totalDamage += ai.getDamage();
		}
		
		return totalDamage;
	}
	
	/**
	 * Retrieves a collection of {@link AggroInfo} objects representing the final damage dealt.<br>
	 * This method filters out attackers that are not in the owner's known list.<br>
	 * It handles group damage logic based on the provided boolean flag.
	 * @param mergeGroupDamage Set to {@code true} to combine damage from team members into a single entry.
	 * @return A {@link Collection} of {@link AggroInfo} objects containing the processed damage data.
	 */
	public Collection<AggroInfo> getFinalDamageList(boolean mergeGroupDamage)
	{
		final Map<Integer, AggroInfo> list = new HashMap<>();
		
		for (AggroInfo ai : aggroList.values())
		{
			// Get master only to control damage.
			final Creature creature = ((Creature) ai.getAttacker()).getMaster();
			
			// Don't include damage from creatures outside the known list.
			if ((creature == null) || !owner.getKnownList().knowns(creature))
			{
				continue;
			}
			
			if (mergeGroupDamage)
			{
				AionObject source;
				
				if ((creature instanceof Player) && ((Player) creature).isInTeam())
				{
					source = ((Player) creature).getCurrentTeam();
				}
				else
				{
					source = creature;
				}
				
				if (list.containsKey(source.getObjectId()))
				{
					list.get(source.getObjectId()).addDamage(ai.getDamage());
				}
				else
				{
					final AggroInfo aggro = new AggroInfo(source);
					aggro.setDamage(ai.getDamage());
					list.put(source.getObjectId(), aggro);
				}
			}
			else if (list.containsKey(creature.getObjectId()))
			{
				// Summon or other assistance
				list.get(creature.getObjectId()).addDamage(ai.getDamage());
			}
			else
			{
				// Create a separate object so we don't taint current list.
				final AggroInfo aggro = new AggroInfo(creature);
				aggro.addDamage(ai.getDamage());
				list.put(creature.getObjectId(), aggro);
			}
		}
		
		return list.values();
	}
	
	/**
	 * Checks if a specific {@link Creature} is considered an enemy of the owner.<br>
	 * This method returns {@code true} if the creature is not the owner and has a hostile relationship.
	 * @param creature The {@link Creature} to check for awareness.
	 * @return {@code true} if the creature is an enemy, {@code false} otherwise.
	 */
	protected boolean isAware(Creature creature)
	{
		return (creature != null) && !creature.getObjectId().equals(owner.getObjectId()) && (creature.isEnemy(owner) || DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(owner.getTribe(), creature.getTribe()));
	}
}
