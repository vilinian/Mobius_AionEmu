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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class handles the logic for observing and reacting to item usage events.<br>
 * It allows developers to implement custom behaviors when a {@link Item} is used by a {@link Player} or {@link Creature}.<br>
 * It extends {@link ActionObserver} to provide a structured way to process these interactions.
 * @author MrPoke
 */
public abstract class ItemUseObserver extends ActionObserver
{
	/**
	 * Creates a new instance of this observer.<br>
	 * This constructor initializes the base {@link ActionObserver} with all types.
	 */
	public ItemUseObserver()
	{
		super(ObserverType.ALL);
	}
	
	/**
	 * Executes an attack action against a specific target.<br>
	 * This method triggers the logic for interacting with a {@link Creature}.
	 * @param creature The {@code Creature} object to be attacked.
	 */
	@Override
	public void attack(Creature creature)
	{
		abort();
	}
	
	/**
	 * This method is called when a creature is attacked.<br>
	 * It allows the observer to react to combat events involving a {@link Creature}.
	 * @param creature The {@code Creature} that was targeted in the attack.
	 */
	@Override
	public void attacked(Creature creature)
	{
		abort();
	}
	
	/**
	 * This method is called when a {@link Creature} dies.<br>
	 * It handles the logic for processing death events.
	 * @param creature The {@code Creature} that has died.
	 */
	@Override
	public void died(Creature creature)
	{
		abort();
	}
	
	/**
	 * Handles the logic when a {@link Creature} is hit by a damage-over-time effect.<br>
	 * This method triggers specific actions related to the {@code dotEffect}.
	 * @param creature The {@code Creature} that received the attack.
	 * @param dotEffect The {@code Effect} instance representing the damage over time.
	 */
	@Override
	public void dotattacked(Creature creature, Effect dotEffect)
	{
		abort();
	}
	
	/**
	 * Equips a specific {@code Item} to a {@link Player}.<br>
	 * This method updates the equipment status for the owner.
	 * @param item The {@code Item} to be equipped.
	 * @param owner The {@link Player} who will wear the item.
	 */
	@Override
	public void equip(Item item, Player owner)
	{
		abort();
	}
	
	/**
	 * This method handles the movement logic for a Creature.
	 */
	@Override
	public void moved()
	{
		abort();
	}
	
	/**
	 * Executes a specific skill action.<br>
	 * This method triggers the logic associated with the provided {@code Skill}.
	 * @param skill The {@code Skill} object to be used.
	 */
	@Override
	public void skilluse(Skill skill)
	{
		abort();
	}
	
	public abstract void abort();
}
