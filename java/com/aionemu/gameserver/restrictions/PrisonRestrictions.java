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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * This class defines the specific limitations and rules applied to players while they are in prison.<br>
 * It handles restrictions such as movement, skill usage, and interaction with {@link Item} objects.
 * @author lord_rex
 */
public class PrisonRestrictions extends AbstractRestrictions
{
	/**
	 * Checks if a {@link Player} is currently restricted by the prison system.<br>
	 * This method verifies if the player is in prison and sends a notification message if they are.
	 * @param player The {@link Player} to check.
	 * @param callingRestriction The {@link Restrictions} class being used to perform the check.
	 * @return {@code true} if the player is in prison, {@code false} otherwise.
	 */
	@Override
	public boolean isRestricted(Player player, Class<? extends Restrictions> callingRestriction)
	{
		if (isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You are in prison!");
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to attack a specific {@link VisibleObject}.<br>
	 * This method validates the interaction between the attacker and the target.
	 * @param player The player attempting to perform the action.
	 * @param target The object being targeted by the player.
	 * @return {@code true} if the attack is permitted, otherwise {@code false}.
	 */
	@Override
	public boolean canAttack(Player player, VisibleObject target)
	{
		if (isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot attack in prison!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to use a specific {@link Skill}.<br>
	 * This method validates all active restrictions for the player.
	 * @param player The {@code Player} who wants to use the skill.
	 * @param skill The {@code Skill} being attempted.
	 * @return {@code true} if the player can use the skill, otherwise {@code false}.
	 */
	@Override
	public boolean canUseSkill(Player player, Skill skill)
	{
		if (isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot use skills in prison!");
			return false;
		}
		
		return true;
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
	public boolean canAffectBySkill(Player player, VisibleObject target, Skill skill)
	{
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to send chat messages.<br>
	 * This method validates the current permissions of the player.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can chat, {@code false} otherwise.
	 */
	@Override
	public boolean canChat(Player player)
	{
		if (isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot chat in prison!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to invite another {@link Player} to a group.<br>
	 * This method validates the permissions of both players involved.
	 * @param player The player attempting to send the invitation.
	 * @param target The player who will receive the invitation.
	 * @return {@code true} if the invitation is allowed, {@code false} otherwise.
	 */
	@Override
	public boolean canInviteToGroup(Player player, Player target)
	{
		if (isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot invite members to group in prison!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a player can send an alliance invitation.<br>
	 * This method verifies if the {@code player} is currently in prison.<br>
	 * If the player is in prison, it sends a warning message and returns {@code false}.
	 * @param player The player attempting to send the invitation.
	 * @param target The player who will receive the invitation.
	 * @return {@code true} if the invitation is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canInviteToAlliance(Player player, Player target)
	{
		if (isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot invite members to alliance in prison!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a player is allowed to invite another player to their league.<br>
	 * This method validates whether the {@code player} is currently in prison.<br>
	 * It sends a system message to the {@code player} if they are restricted from inviting others.
	 * @param player The player attempting to send the invitation.
	 * @param target The player who is being invited.
	 * @return {@code true} if the invitation is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canInviteToLeague(Player player, Player target)
	{
		if (isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot invite members to league in prison!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to change their equipment.<br>
	 * This method validates the current restrictions for the given player.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can change equipment, {@code false} otherwise.
	 */
	@Override
	public boolean canChangeEquip(Player player)
	{
		if (isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot equip / unequip item in prison!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to use a specific {@link Item}.<br>
	 * This method validates the player's permissions against the item provided.
	 * @param player The {@code Player} attempting to use the item.
	 * @param item The {@code Item} that wants to be used.
	 * @return {@code true} if the action is allowed, {@code false} otherwise.
	 */
	@Override
	public boolean canUseItem(Player player, Item item)
	{
		if (isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot use item in prison!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Player} is currently in a prison area.<br>
	 * This method evaluates the player's status and current world ID.
	 * @param player The {@code Player} to check.
	 * @return {@code true} if the player is in prison, {@code false} otherwise.
	 */
	private boolean isInPrison(Player player)
	{
		return player.isInPrison() || (player.getWorldId() == WorldMapType.DF_PRISON.getId()) || (player.getWorldId() == WorldMapType.LF_PRISON.getId());
	}
}
