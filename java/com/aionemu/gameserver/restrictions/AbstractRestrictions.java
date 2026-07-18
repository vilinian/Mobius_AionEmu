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
package com.aionemu.gameserver.restrictions;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class serves as a base for defining various game restrictions.<br>
 * It provides common functionality for checking if an action is permitted.<br>
 * Subclasses should extend this to implement specific logic for {@link Player} or {@link Item} interactions.
 * @author lord_rex
 */
public abstract class AbstractRestrictions implements Restrictions
{
	/**
	 * Enables the restriction rules for this object.<br>
	 * This method registers the current instance with {@link RestrictionsManager}.
	 */
	public void activate()
	{
		RestrictionsManager.activate(this);
	}
	
	/**
	 * Disables the current restriction.<br>
	 * This method notifies {@link RestrictionsManager} to stop applying these rules.<br>
	 * Use this when a player no longer needs to be restricted by this specific rule.
	 */
	public void deactivate()
	{
		RestrictionsManager.deactivate(this);
	}
	
	/**
	 * Returns a hash code value for this object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the class type of the instance.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}
	
	/**
	 * Compares this object with another object for equality.<br>
	 * This method checks if both objects belong to the same class.
	 * @param obj The object to compare this instance against.
	 * @return {@code true} if both objects have the same class, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		return getClass().equals(obj.getClass());
	}
	
	/**
	 * Checks if a {@link Player} is restricted by a specific restriction type.<br>
	 * This method determines if the action is allowed based on the provided class.
	 * @param player The {@link Player} to check.
	 * @param callingRestriction The {@link Restrictions} class being used to perform the check.
	 * @return {@code true} if the player is restricted, {@code false} otherwise.
	 */
	@Override
	@DisabledRestriction
	public boolean isRestricted(Player player, Class<? extends Restrictions> callingRestriction)
	{
		throw new AbstractMethodError();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to attack a specific {@link VisibleObject}.<br>
	 * This method validates the interaction between the attacker and the target.
	 * @param player The player attempting to perform the action.
	 * @param target The object being targeted by the player.
	 * @return {@code true} if the attack is permitted, otherwise {@code false}.
	 */
	@Override
	@DisabledRestriction
	public boolean canAttack(Player player, VisibleObject target)
	{
		throw new AbstractMethodError();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to be affected by a specific {@link Skill} on a {@link VisibleObject}.<br>
	 * This method determines if any restrictions prevent the skill from hitting the target.
	 * @param player The player who will be affected by the skill.
	 * @param target The object that is being targeted by the skill.
	 * @param skill The specific skill being used.
	 * @return {@code true} if the player can be affected, {@code false} otherwise.
	 */
	@Override
	@DisabledRestriction
	public boolean canAffectBySkill(Player player, VisibleObject target, Skill skill)
	{
		throw new AbstractMethodError();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to use a specific {@link Skill}.<br>
	 * This method validates all active restrictions for the player.
	 * @param player The {@code Player} who wants to use the skill.
	 * @param skill The {@code Skill} being attempted.
	 * @return {@code true} if the player can use the skill, otherwise {@code false}.
	 */
	@Override
	@DisabledRestriction
	public boolean canUseSkill(Player player, Skill skill)
	{
		throw new AbstractMethodError();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to send chat messages.<br>
	 * This method validates the current permissions of the player.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can chat, {@code false} otherwise.
	 */
	@Override
	@DisabledRestriction
	public boolean canChat(Player player)
	{
		throw new AbstractMethodError();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to invite another {@link Player} to a group.<br>
	 * This method validates the permissions of both players involved.
	 * @param player The player attempting to send the invitation.
	 * @param target The player who will receive the invitation.
	 * @return {@code true} if the invitation is allowed, {@code false} otherwise.
	 */
	@Override
	@DisabledRestriction
	public boolean canInviteToGroup(Player player, Player target)
	{
		throw new AbstractMethodError();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to change their equipment.<br>
	 * This method validates the current restrictions for the given player.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can change equipment, {@code false} otherwise.
	 */
	@Override
	@DisabledRestriction
	public boolean canChangeEquip(Player player)
	{
		throw new AbstractMethodError();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to access the warehouse.<br>
	 * This method validates specific permissions for the given player.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can use the warehouse, {@code false} otherwise.
	 */
	@Override
	@DisabledRestriction
	public boolean canUseWarehouse(Player player)
	{
		throw new AbstractMethodError();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to perform trades.<br>
	 * This method validates the trading permissions for the given player.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can trade, {@code false} otherwise.
	 */
	@Override
	@DisabledRestriction
	public boolean canTrade(Player player)
	{
		throw new AbstractMethodError();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to use a specific {@link Item}.<br>
	 * This method validates the player's permissions against the item provided.
	 * @param player The {@code Player} attempting to use the item.
	 * @param item The {@code Item} that wants to be used.
	 * @return {@code true} if the action is allowed, {@code false} otherwise.
	 */
	@Override
	@DisabledRestriction
	public boolean canUseItem(Player player, Item item)
	{
		throw new AbstractMethodError();
	}
}
