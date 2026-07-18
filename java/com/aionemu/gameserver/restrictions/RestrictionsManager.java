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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class manages various game restrictions such as events, quests, and instances.<br>
 * It provides a centralized system to check if specific actions are allowed for {@link Player} objects. You can extend the restriction logic by creating custom classes that inherit from {@code AbstractRestrictions}.
 * @author lord_rex
 */
public final class RestrictionsManager
{
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This class is intended to be used as a utility manager for restrictions.
	 */
	private RestrictionsManager()
	{
	}
	
	private static enum RestrictionMode implements Comparator<Restrictions>
	{
		isRestricted,
		canAttack,
		canAffectBySkill,
		canUseSkill,
		canChat,
		canInviteToGroup,
		canInviteToAlliance,
		canInviteToLeague,
		canChangeEquip,
		canTrade,
		canUseWarehouse,
		canUseItem;
		
		private final Method METHOD;
		
		private RestrictionMode()
		{
			for (Method method : Restrictions.class.getMethods())
			{
				if (name().equals(method.getName()))
				{
					METHOD = method;
					return;
				}
			}
			
			throw new InternalError();
		}
		
		private boolean equalsMethod(Method method)
		{
			if (!METHOD.getName().equals(method.getName()) || !METHOD.getReturnType().equals(method.getReturnType()))
			{
				return false;
			}
			
			return Arrays.equals(METHOD.getParameterTypes(), method.getParameterTypes());
		}
		
		private static final RestrictionMode[] VALUES = RestrictionMode.values();
		
		private static RestrictionMode parse(Method method)
		{
			for (RestrictionMode mode : VALUES)
			{
				if (mode.equalsMethod(method))
				{
					return mode;
				}
			}
			
			return null;
		}
		
		@Override
		public int compare(Restrictions o1, Restrictions o2)
		{
			return Double.compare(getPriority(o2), getPriority(o1));
		}
		
		private double getPriority(Restrictions restriction)
		{
			final RestrictionPriority a1 = getMatchingMethod(restriction.getClass()).getAnnotation(RestrictionPriority.class);
			if (a1 != null)
			{
				return a1.value();
			}
			
			final RestrictionPriority a2 = restriction.getClass().getAnnotation(RestrictionPriority.class);
			if (a2 != null)
			{
				return a2.value();
			}
			
			return RestrictionPriority.DEFAULT_PRIORITY;
		}
		
		private Method getMatchingMethod(Class<? extends Restrictions> clazz)
		{
			for (Method method : clazz.getMethods())
			{
				if (equalsMethod(method))
				{
					return method;
				}
			}
			
			throw new InternalError();
		}
	}
	
	private static final Restrictions[][] RESTRICTIONS = new Restrictions[RestrictionMode.VALUES.length][0];
	
	/**
	 * Activates a specific {@link Restrictions} type.<br>
	 * This method registers the restriction into the global system.<br>
	 * It ensures that all relevant methods are processed and sorted correctly.
	 * @param restriction The {@code Restrictions} object to activate.
	 */
	public synchronized static void activate(Restrictions restriction)
	{
		for (Method method : restriction.getClass().getMethods())
		{
			final RestrictionMode mode = RestrictionMode.parse(method);
			
			if ((mode == null) || (method.getAnnotation(DisabledRestriction.class) != null))
			{
				continue;
			}
			
			Restrictions[] restrictions = RESTRICTIONS[mode.ordinal()];
			
			if (!Arrays.asList(restrictions).contains(restriction))
			{
				restrictions = Arrays.copyOf(restrictions, restrictions.length + 1);
				restrictions[restrictions.length - 1] = restriction;
			}
			
			Arrays.sort(restrictions, mode);
			
			RESTRICTIONS[mode.ordinal()] = restrictions;
		}
	}
	
	/**
	 * This method removes a specific restriction from the system.<br>
	 * It updates all active {@code RestrictionMode} categories.<br>
	 * Use this to disable a rule that was previously added via {@code activate}.
	 * @param restriction The {@code Restrictions} object to remove.
	 */
	public synchronized static void deactivate(Restrictions restriction)
	{
		for (RestrictionMode mode : RestrictionMode.VALUES)
		{
			Restrictions[] restrictions = RESTRICTIONS[mode.ordinal()];
			
			for (int index; (index = Arrays.asList(restrictions).indexOf(restriction)) != -1;)
			{
				final Restrictions[] _trimmed = Arrays.copyOf(restrictions, restrictions.length - 1);
				System.arraycopy(restrictions, index + 1, _trimmed, index, restrictions.length - index - 1);
				restrictions = _trimmed;
			}
			
			RESTRICTIONS[mode.ordinal()] = restrictions;
		}
	}
	
	static
	{
		// This is the Restrictions when player is in normal game.
		activate(new PlayerRestrictions());
		// This is the Restrictions when player is in shutdown.
		activate(new ShutdownRestrictions());
		// This is the Restrictions when player is in prison.
		activate(new PrisonRestrictions());
	}
	
	/**
	 * Checks if a {@link Player} is currently restricted by specific rules.<br>
	 * This method evaluates the player against all active restrictions.<br>
	 * It returns {@code true} if the player is null or meets restriction criteria.
	 * @param player The {@link Player} object to check.
	 * @param callingRestriction The specific {@link Restrictions} class being checked.
	 * @return {@code true} if the player is restricted, otherwise {@code false}.
	 */
	public static boolean isRestricted(Player player, Class<? extends Restrictions> callingRestriction)
	{
		if (player == null)
		{
			return true;
		}
		
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.isRestricted.ordinal()])
		{
			if (!restrictions.isRestricted(player, callingRestriction))
			{
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to attack a specific {@link VisibleObject}.<br>
	 * This method verifies all active restrictions for the current action.
	 * @param player The {@code Player} attempting to perform the attack.
	 * @param target The {@code VisibleObject} that is being targeted.
	 * @return {@code true} if the attack is permitted, or {@code false} otherwise.
	 */
	public static boolean canAttack(Player player, VisibleObject target)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canAttack.ordinal()])
		{
			if (!restrictions.canAttack(player, target))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} can be affected by a specific {@link Skill} on a {@link VisibleObject}.<br>
	 * This method validates the action against all active restrictions.
	 * @param player The player who is casting or being affected by the skill.
	 * @param target The object that is receiving the skill effect.
	 * @param skill The specific skill being used.
	 * @return {@code true} if no restrictions prevent the skill, otherwise {@code false}.
	 */
	public static boolean canAffectBySkill(Player player, VisibleObject target, Skill skill)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canAffectBySkill.ordinal()])
		{
			if (!restrictions.canAffectBySkill(player, target, skill))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to use a specific {@link Skill}.<br>
	 * This method verifies all active restrictions.
	 * @param player The {@code Player} who wants to use the skill.
	 * @param skill The {@code Skill} being attempted.
	 * @return {@code true} if the skill is allowed, or {@code false} otherwise.
	 */
	public static boolean canUseSkill(Player player, Skill skill)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canUseSkill.ordinal()])
		{
			if (!restrictions.canUseSkill(player, skill))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to send chat messages.<br>
	 * This method verifies all active restrictions for the player.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can chat, or {@code false} otherwise.
	 */
	public static boolean canChat(Player player)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canChat.ordinal()])
		{
			if (!restrictions.canChat(player))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to invite another {@link Player} to a group.<br>
	 * This method evaluates all active restrictions for the action.
	 * @param player The player attempting to send the invitation.
	 * @param target The player who will receive the invitation.
	 * @return {@code true} if the invitation is allowed, otherwise {@code false}.
	 */
	public static boolean canInviteToGroup(Player player, Player target)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canInviteToGroup.ordinal()])
		{
			if (!restrictions.canInviteToGroup(player, target))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to invite another {@link Player} to an alliance.<br>
	 * This method evaluates all active restrictions for the action.
	 * @param player The player attempting to send the invitation.
	 * @param target The player who will receive the invitation.
	 * @return {@code true} if the invitation is allowed, otherwise {@code false}.
	 */
	public static boolean canInviteToAlliance(Player player, Player target)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canInviteToAlliance.ordinal()])
		{
			if (!restrictions.canInviteToAlliance(player, target))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to invite another {@link Player} to a league.<br>
	 * This method verifies all active restrictions for the action.
	 * @param player The player attempting to send the invitation.
	 * @param target The player who will receive the invitation.
	 * @return {@code true} if the invitation is allowed, otherwise {@code false}.
	 */
	public static boolean canInviteToLeague(Player player, Player target)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canInviteToLeague.ordinal()])
		{
			if (!restrictions.canInviteToLeague(player, target))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to change their equipment.<br>
	 * This method verifies all active restrictions for the player.<br>
	 * It returns {@code false} if any restriction prevents changing equipment.
	 * @param player The {@link Player} object to check.
	 * @return {@code true} if the player can change equipment, or {@code false} otherwise.
	 */
	public static boolean canChangeEquip(Player player)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canChangeEquip.ordinal()])
		{
			if (!restrictions.canChangeEquip(player))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to trade.<br>
	 * This method verifies all active restrictions for the player.<br>
	 * It also checks if the player is currently alive.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can trade, otherwise {@code false}.
	 */
	public static boolean canTrade(Player player)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canTrade.ordinal()])
		{
			if (!restrictions.canTrade(player))
			{
				return false;
			}
		}
		
		if (player.getLifeStats().isAlreadyDead())
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to access the warehouse.<br>
	 * This method verifies all active restrictions for the player.<br>
	 * It returns {@code false} if any restriction blocks the action.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can use the warehouse, otherwise {@code false}.
	 */
	public static boolean canUseWarehouse(Player player)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canUseWarehouse.ordinal()])
		{
			if (!restrictions.canUseWarehouse(player))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to use a specific {@link Item}.<br>
	 * This method verifies the player against all active restrictions.
	 * @param player The {@code Player} who wants to use the item.
	 * @param item The {@code Item} that is being used.
	 * @return {@code true} if the player can use the item, otherwise {@code false}.
	 */
	public static boolean canUseItem(Player player, Item item)
	{
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canUseItem.ordinal()])
		{
			if (!restrictions.canUseItem(player, item))
			{
				return false;
			}
		}
		
		return true;
	}
}
