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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages restrictions applied to the game server during a shutdown process.<br>
 * It ensures that specific actions are restricted or disabled while the server is closing.
 * @author lord_rex
 */
public class ShutdownRestrictions extends AbstractRestrictions
{
	/**
	 * Checks if a {@link Player} is restricted by the shutdown process.<br>
	 * This method verifies if the player is currently undergoing a server shutdown.
	 * @param player The {@link Player} to check.
	 * @param callingRestriction The {@link Restrictions} class being used to perform the check.
	 * @return {@code true} if the player is restricted, {@code false} otherwise.
	 */
	@Override
	public boolean isRestricted(Player player, Class<? extends Restrictions> callingRestriction)
	{
		if (isInShutdownProgress(player))
		{
			PacketSendUtility.sendMessage(player, "You are in shutdown progress!");
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
		if (isInShutdownProgress(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot attack in Shutdown progress!");
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
	 * Checks if a {@link Player} is allowed to use a specific {@link Skill}.<br>
	 * This method validates all active restrictions for the player.
	 * @param player The {@code Player} who wants to use the skill.
	 * @param skill The {@code Skill} being attempted.
	 * @return {@code true} if the player can use the skill, otherwise {@code false}.
	 */
	@Override
	public boolean canUseSkill(Player player, Skill skill)
	{
		if (isInShutdownProgress(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot use skills in Shutdown progress!");
			return false;
		}
		
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
		if (isInShutdownProgress(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot chat in Shutdown progress!");
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
		if (isInShutdownProgress(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot invite members to group in Shutdown progress!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a player can send an alliance invitation.<br>
	 * This method verifies that the {@code player} is not currently in a shutdown process.<br>
	 * If a shutdown is in progress, it sends a warning message and returns {@code false}.
	 * @param player The player attempting to send the invitation.
	 * @param target The player who will receive the invitation.
	 * @return {@code true} if the invitation can be sent, otherwise {@code false}.
	 */
	@Override
	public boolean canInviteToAlliance(Player player, Player target)
	{
		if (isInShutdownProgress(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot invite members to alliance in Shutdown progress!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a player is allowed to invite another player to their league.<br>
	 * This method validates whether the server is currently in shutdown progress.<br>
	 * It sends a system message to the {@code player} if the invitation is blocked.
	 * @param player The player attempting to send the invitation.
	 * @param target The player who is being invited.
	 * @return {@code true} if the invitation is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canInviteToLeague(Player player, Player target)
	{
		if (isInShutdownProgress(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot invite members to league in Shutdown progress!");
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
		if (isInShutdownProgress(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot equip / unequip item in Shutdown progress!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if the server is currently shutting down for a specific player.<br>
	 * This method retrieves the status from the {@code Player} controller.
	 * @param player The {@link Player} to check.
	 * @return {@code true} if the shutdown is in progress, {@code false} otherwise.
	 */
	private boolean isInShutdownProgress(Player player)
	{
		return player.getController().isInShutdownProgress();
	}
}
