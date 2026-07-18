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
package com.aionemu.gameserver.controllers.observer;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class handles the observation and execution of actions triggered by game entities.<br>
 * It monitors {@link Skill} effects and updates relevant game objects like {@link Player} or {@link Creature}.<br>
 * Use this class to manage side effects that occur during gameplay actions.
 * @author ATracer
 */
public class ActionObserver
{
	private AtomicBoolean used;
	private final ObserverType observerType;
	
	/**
	 * Creates a new instance of {@code ActionObserver}.<br>
	 * This constructor initializes the observer with a specific type.
	 * @param observerType The {@link ObserverType} to assign to this observer.
	 */
	public ActionObserver(ObserverType observerType)
	{
		this.observerType = observerType;
	}
	
	/**
	 * Resets the usage status of this observer.<br>
	 * This method sets the {@code used} flag to {@code false}.<br>
	 * Use this to allow the action to be performed again.
	 */
	public void makeOneTimeUse()
	{
		used = new AtomicBoolean(false);
	}
	
	/**
	 * Attempts to mark this observer as used.<br>
	 * This method is thread-safe and ensures the action only happens once.
	 * @return {@code true} if the observer was successfully marked as used, or {@code false} if it was already used.
	 */
	public boolean tryUse()
	{
		return used.compareAndSet(false, true);
	}
	
	/**
	 * Retrieves the type of observer associated with this action.<br>
	 * This helps identify what specific event is being monitored.
	 * @return The {@code ObserverType} of this instance.
	 */
	public ObserverType getObserverType()
	{
		return observerType;
	}
	
	/**
	 * This method handles the movement logic for a Creature.
	 */
	public void moved()
	{
	}
	
	/**
	 * This method is called when a creature is attacked.<br>
	 * It allows the observer to react to combat events involving a {@link Creature}.
	 * @param creature The {@code Creature} that was targeted in the attack.
	 */
	public void attacked(Creature creature)
	{
	}
	
	/**
	 * Executes an attack action against a specific target.<br>
	 * This method triggers the logic for interacting with a {@link Creature}.
	 * @param creature The {@code Creature} object to be attacked.
	 */
	public void attack(Creature creature)
	{
	}
	
	/**
	 * Equips a specific {@code Item} to a {@link Player}.<br>
	 * This method updates the equipment status for the owner.
	 * @param item The {@code Item} to be equipped.
	 * @param owner The {@link Player} who will wear the item.
	 */
	public void equip(Item item, Player owner)
	{
	}
	
	/**
	 * Removes an {@code Item} from a {@link Player}.<br>
	 * This method updates the player's equipment status.
	 * @param item The {@code Item} to be removed.
	 * @param owner The {@link Player} who currently owns the item.
	 */
	public void unequip(Item item, Player owner)
	{
	}
	
	/**
	 * Executes a specific skill action.<br>
	 * This method triggers the logic associated with the provided {@code Skill}.
	 * @param skill The {@code Skill} object to be used.
	 */
	public void skilluse(Skill skill)
	{
	}
	
	/**
	 * This method is called when a {@link Creature} dies.<br>
	 * It handles the logic for processing death events.
	 * @param creature The {@code Creature} that has died.
	 */
	public void died(Creature creature)
	{
	}
	
	/**
	 * Handles the logic when a {@link Creature} is hit by a damage-over-time effect.<br>
	 * This method triggers specific actions related to the {@code dotEffect}.
	 * @param creature The {@code Creature} that received the attack.
	 * @param dotEffect The {@code Effect} instance representing the damage over time.
	 */
	public void dotattacked(Creature creature, Effect dotEffect)
	{
	}
	
	/**
	 * This method is called when an {@link Item} is used.<br>
	 * It updates the internal state of the observer.
	 * @param item The {@code Item} object that was used.
	 */
	public void itemused(Item item)
	{
	}
	
	/**
	 * This method is called when a player requests to start a dialog with an {@link Npc}.<br>
	 * It handles the initial interaction logic for the NPC.
	 * @param npc The {@code Npc} object that the player is interacting with.
	 */
	public void npcdialogrequested(Npc npc)
	{
	}
	
	/**
	 * Updates the current status of an {@link AbnormalState}.<br>
	 * This method is called when a new abnormal effect is applied.
	 * @param state The {@code AbnormalState} to be processed.
	 */
	public void abnormalsetted(AbnormalState state)
	{
	}
	
	/**
	 * Releases a summoned creature from its current state.<br>
	 * This method handles the logic for ending a summon effect.
	 */
	public void summonrelease()
	{
	}
}
