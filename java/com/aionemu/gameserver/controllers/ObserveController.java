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
package com.aionemu.gameserver.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackerCriticalStatus;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class manages the registration and execution of {@link ActionObserver} instances.<br>
 * It handles the distribution of game events to various observers like {@link AttackCalcObserver}.<br>
 * The controller ensures that observers are processed efficiently while maintaining thread safety.
 * @author ATracer
 * @author Cura
 */
public class ObserveController
{
	private final ReentrantLock lock = new ReentrantLock();
	protected Collection<ActionObserver> observers = new CopyOnWriteArrayList<>();
	protected List<ActionObserver> onceUsedObservers = new ArrayList<>();
	protected Collection<AttackCalcObserver> attackCalcObservers = new CopyOnWriteArrayList<>();
	
	/**
	 * Registers a new {@link ActionObserver} to the system.<br>
	 * This method marks the observer as one-time use.<br>
	 * It adds the observer to the internal tracking list.
	 * @param observer The {@code ActionObserver} to be registered.
	 */
	public void attach(ActionObserver observer)
	{
		observer.makeOneTimeUse();
		lock.lock();
		try
		{
			onceUsedObservers.add(observer);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Registers a new observer to the system.<br>
	 * This allows the {@link ActionObserver} to receive notifications from this controller.
	 * @param observer The {@code ActionObserver} instance to be added.
	 */
	public void addObserver(ActionObserver observer)
	{
		observers.add(observer);
	}
	
	/**
	 * Registers a new {@link AttackCalcObserver} to the system.<br>
	 * This allows the observer to receive updates during attack calculations.
	 * @param observer The {@code AttackCalcObserver} instance to add.
	 */
	public void addAttackCalcObserver(AttackCalcObserver observer)
	{
		attackCalcObservers.add(observer);
	}
	
	/**
	 * Removes a specific observer from the active list.<br>
	 * This method also removes the observer from the {@code onceUsedObservers} collection.<br>
	 * It ensures that the observer will no longer receive notifications.
	 * @param observer The {@link ActionObserver} to be removed.
	 */
	public void removeObserver(ActionObserver observer)
	{
		observers.remove(observer);
		lock.lock();
		try
		{
			onceUsedObservers.remove(observer);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Removes a specific observer from the attack calculation list.<br>
	 * This stops the {@code AttackCalcObserver} from receiving updates.
	 * @param observer The {@code AttackCalcObserver} to be removed.
	 */
	public void removeAttackCalcObserver(AttackCalcObserver observer)
	{
		attackCalcObservers.remove(observer);
	}
	
	/**
	 * Sends a notification to all registered observers of a specific type.<br>
	 * This method handles both permanent and one-time observers.<br>
	 * It ensures that one-time observers are removed after they are used once.
	 * @param type The {@code ObserverType} that determines which observers should receive the notification.
	 * @param object A variable number of objects to pass as data to the observers.
	 */
	public void notifyObservers(ObserverType type, Object... object)
	{
		List<ActionObserver> tempOnceused = Collections.emptyList();
		lock.lock();
		try
		{
			if (onceUsedObservers.size() > 0)
			{
				tempOnceused = new ArrayList<>();
				final Iterator<ActionObserver> iterator = onceUsedObservers.iterator();
				while (iterator.hasNext())
				{
					final ActionObserver observer = iterator.next();
					if (observer.getObserverType().matchesObserver(type))
					{
						if (observer.tryUse())
						{
							tempOnceused.add(observer);
							iterator.remove();
						}
					}
				}
			}
		}
		finally
		{
			lock.unlock();
		}
		
		// notify outside of lock
		for (ActionObserver observer : tempOnceused)
		{
			notifyAction(type, observer, object);
		}
		
		if (observers.size() > 0)
		{
			for (ActionObserver observer : observers)
			{
				if (observer.getObserverType().matchesObserver(type))
				{
					notifyAction(type, observer, object);
				}
			}
		}
	}
	
	/**
	 * Sends a specific action notification to an individual observer.<br>
	 * This method routes the call based on the provided {@code ObserverType}.
	 * @param type The category of the action to notify.
	 * @param observer The {@link ActionObserver} instance receiving the notification.
	 * @param object A variable list of arguments required by the specific action type.
	 */
	private void notifyAction(ObserverType type, ActionObserver observer, Object... object)
	{
		switch (type)
		{
			case ATTACK:
				observer.attack((Creature) object[0]);
				break;
			case ATTACKED:
				observer.attacked((Creature) object[0]);
				break;
			case DEATH:
				observer.died((Creature) object[0]);
				break;
			case EQUIP:
				observer.equip((Item) object[0], (Player) object[1]);
				break;
			case UNEQUIP:
				observer.unequip((Item) object[0], (Player) object[1]);
				break;
			case MOVE:
				observer.moved();
				break;
			case SKILLUSE:
				observer.skilluse((Skill) object[0]);
				break;
			case DOT_ATTACKED:
				observer.dotattacked((Creature) object[0], (Effect) object[1]);
				break;
			case ITEMUSE:
				observer.itemused((Item) object[0]);
				break;
			case NPCDIALOGREQUEST:
				observer.npcdialogrequested((Npc) object[0]);
				break;
			case ABNORMALSETTED:
				observer.abnormalsetted((AbnormalState) object[0]);
				break;
			case SUMMONRELEASE:
				observer.summonrelease();
				break;
			default:
				break;
		}
	}
	
	/**
	 * Notifies all registered observers that a {@link Creature} has died.<br>
	 * This method triggers the {@code DEATH} observer type.
	 * @param creature The {@code Creature} instance that died.
	 */
	public void notifyDeathObservers(Creature creature)
	{
		notifyObservers(ObserverType.DEATH, creature);
	}
	
	/**
	 * Notifies all registered observers that a move action has occurred.<br>
	 * This method calls {@code Object...)} using the {@code MOVE} type.
	 */
	public void notifyMoveObservers()
	{
		notifyObservers(ObserverType.MOVE);
	}
	
	/**
	 * Notifies all registered observers that an attack has occurred.<br>
	 * This method passes the {@code creature} object to the relevant listeners.<br>
	 * It uses the {@code Object...)} method internally.
	 * @param creature The {@code Creature} involved in the attack.
	 */
	public void notifyAttackObservers(Creature creature)
	{
		notifyObservers(ObserverType.ATTACK, creature);
	}
	
	/**
	 * Notifies all registered observers that a specific {@code Creature} has been attacked.<br>
	 * This method triggers the {@code notifyObservers} logic using the {@code ATTACKED} type.
	 * @param creature The {@code Creature} object that was involved in the attack.
	 */
	public void notifyAttackedObservers(Creature creature)
	{
		notifyObservers(ObserverType.ATTACKED, creature);
	}
	
	/**
	 * Notifies all observers that a {@code Creature} has been hit by a {@code DOT} attack.<br>
	 * This method triggers the {@code DOT_ATTACKED} event.
	 * @param creature The {@code Creature} that was targeted by the effect.
	 * @param effect The specific {@code Effect} applied to the target.
	 */
	public void notifyDotAttackedObservers(Creature creature, Effect effect)
	{
		notifyObservers(ObserverType.DOT_ATTACKED, creature, effect);
	}
	
	/**
	 * Notifies all registered observers that a {@code Skill} has been used.<br>
	 * This method triggers the {@code SKILLUSE} event.
	 * @param skill The {@code Skill} object being used.
	 */
	public void notifySkilluseObservers(Skill skill)
	{
		notifyObservers(ObserverType.SKILLUSE, skill);
	}
	
	/**
	 * Notifies all observers that an item has been equipped.<br>
	 * This method triggers the {@code EQUIP} observer type.
	 * @param item The {@code Item} object being equipped.
	 * @param owner The {@link Player} who owns the item.
	 */
	public void notifyItemEquip(Item item, Player owner)
	{
		notifyObservers(ObserverType.EQUIP, item, owner);
	}
	
	/**
	 * Notifies all observers that an item has been unequipped.<br>
	 * This method triggers the {@code UNEQUIP} observer type.
	 * @param item The {@code Item} that was removed from the player.
	 * @param owner The {@link Player} who unequipped the item.
	 */
	public void notifyItemUnEquip(Item item, Player owner)
	{
		notifyObservers(ObserverType.UNEQUIP, item, owner);
	}
	
	/**
	 * Notifies all registered observers that an {@link Item} has been used.<br>
	 * This method calls {@code Object...)} with the {@code ITEMUSE} type.
	 * @param item The {@code Item} object that was used.
	 */
	public void notifyItemuseObservers(Item item)
	{
		notifyObservers(ObserverType.ITEMUSE, item);
	}
	
	/**
	 * Notifies all observers that an {@link Npc} has requested a dialog.<br>
	 * This method triggers the {@code NPCDIALOGREQUEST} event type.
	 * @param npc The {@code Npc} object involved in the dialog request.
	 */
	public void notifyRequestDialogObservers(Npc npc)
	{
		notifyObservers(ObserverType.NPCDIALOGREQUEST, npc);
	}
	
	/**
	 * Notifies all observers that an {@code AbnormalState} has been set.<br>
	 * This method calls {@code Object...)} with the {@code ABNORMALSETTED} type.
	 * @param state The {@code AbnormalState} object to notify the observers about.
	 */
	public void notifyAbnormalSettedObservers(AbnormalState state)
	{
		notifyObservers(ObserverType.ABNORMALSETTED, state);
	}
	
	/**
	 * Notifies all registered observers that a summon has been released.<br>
	 * This method triggers the {@code ObserverType.SUMMONRELEASE} event.<br>
	 * It uses the internal {@code Object...)} method to broadcast the update.
	 */
	public void notifySummonReleaseObservers()
	{
		notifyObservers(ObserverType.SUMMONRELEASE);
	}
	
	/**
	 * Checks if any registered observer validates the current attack status.<br>
	 * This method iterates through all {@link AttackCalcObserver} instances.<br>
	 * It returns {@code true} if at least one observer confirms the status.
	 * @param status The {@code AttackStatus} to verify.
	 * @return {@code true} if any observer validates the status, otherwise {@code false}.
	 */
	public boolean checkAttackStatus(AttackStatus status)
	{
		if (attackCalcObservers.size() > 0)
		{
			for (AttackCalcObserver observer : attackCalcObservers)
			{
				if (observer.checkStatus(status))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if any {@link AttackCalcObserver} validates the current attack status.<br>
	 * This method iterates through all registered observers to verify the condition.
	 * @param status The {@code AttackStatus} to check against each observer.
	 * @return {@code true} if at least one observer returns true, otherwise {@code false}.
	 */
	public boolean checkAttackerStatus(AttackStatus status)
	{
		if (attackCalcObservers.size() > 0)
		{
			for (AttackCalcObserver observer : attackCalcObservers)
			{
				if (observer.checkAttackerStatus(status))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the attacker has a critical status based on current attack data.<br>
	 * This method iterates through all registered {@link AttackCalcObserver} objects.<br>
	 * It returns the first valid result found or a default false status.
	 * @param status The current {@code AttackStatus} of the action.
	 * @param isSkill A boolean indicating if the attack is a skill.
	 * @return The resulting {@code AttackerCriticalStatus}.
	 */
	public AttackerCriticalStatus checkAttackerCriticalStatus(AttackStatus status, boolean isSkill)
	{
		if (attackCalcObservers.size() > 0)
		{
			for (AttackCalcObserver observer : attackCalcObservers)
			{
				final AttackerCriticalStatus acStatus = observer.checkAttackerCriticalStatus(status, isSkill);
				if (acStatus.isResult())
				{
					return acStatus;
				}
			}
		}
		
		return new AttackerCriticalStatus(false);
	}
	
	/**
	 * This method notifies all {@link AttackCalcObserver} instances to check shield status.<br>
	 * It iterates through the registered observers if any exist.
	 * @param attackList The list of {@code AttackResult} objects for the current action.
	 * @param effect The {@code Effect} being applied or processed.
	 * @param attacker The {@link Creature} performing the attack.
	 */
	public void checkShieldStatus(List<AttackResult> attackList, Effect effect, Creature attacker)
	{
		if (attackCalcObservers.size() > 0)
		{
			for (AttackCalcObserver observer : attackCalcObservers)
			{
				observer.checkShield(attackList, effect, attacker);
			}
		}
	}
	
	/**
	 * Calculates the total physical damage multiplier.<br>
	 * This method iterates through all {@link AttackCalcObserver} objects.<br>
	 * It multiplies their individual base multipliers together.
	 * @param isSkill A boolean indicating if the attack is a skill.
	 * @return The final calculated float multiplier.
	 */
	public float getBasePhysicalDamageMultiplier(boolean isSkill)
	{
		float multiplier = 1;
		if (attackCalcObservers.size() > 0)
		{
			for (AttackCalcObserver observer : attackCalcObservers)
			{
				multiplier *= observer.getBasePhysicalDamageMultiplier(isSkill);
			}
		}
		
		return multiplier;
	}
	
	/**
	 * Calculates the total magical damage multiplier.<br>
	 * This method iterates through all {@link AttackCalcObserver} objects.<br>
	 * It multiplies their individual base multipliers together.
	 * @return The final calculated {@code float} multiplier.
	 */
	public float getBaseMagicalDamageMultiplier()
	{
		float multiplier = 1;
		if (attackCalcObservers.size() > 0)
		{
			for (AttackCalcObserver observer : attackCalcObservers)
			{
				multiplier *= observer.getBaseMagicalDamageMultiplier();
			}
		}
		
		return multiplier;
	}
	
	/**
	 * Removes all observers from the controller.<br>
	 * This method clears both {@code observers} and {@code attackCalcObservers}.<br>
	 * It also clears the {@code onceUsedObservers} collection safely using a lock.
	 */
	public void clear()
	{
		lock.lock();
		try
		{
			onceUsedObservers.clear();
		}
		finally
		{
			lock.unlock();
		}
		
		observers.clear();
		attackCalcObservers.clear();
	}
}
