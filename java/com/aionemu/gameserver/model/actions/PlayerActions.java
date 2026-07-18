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
package com.aionemu.gameserver.model.actions;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.player.InRoll;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.ride.RideInfo;
import com.aionemu.gameserver.model.templates.windstreams.WindstreamPath;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles specific actions performed by {@link Player} objects.<br>
 * It extends {@link CreatureActions} to provide player-specific logic and behaviors.
 * @author xTz
 */
public class PlayerActions extends CreatureActions
{
	/**
	 * Checks if a {@link Player} is currently in a specific {@code PlayerMode}.<br>
	 * This method verifies states like riding, being in a roll, or using a windstream.
	 * @param player The {@code Player} object to check.
	 * @param mode The {@code PlayerMode} to verify.
	 * @return {@code true} if the player is in the specified mode, otherwise {@code false}.
	 */
	public static boolean isInPlayerMode(Player player, PlayerMode mode)
	{
		switch (mode)
		{
			case RIDE:
				return player.ride != null;
			case IN_ROLL:
				return player.inRoll != null;
			case WINDSTREAM:
				return player.windstreamPath != null;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Updates the specific game mode for a {@link Player}.<br>
	 * This method assigns an object to the player based on the provided {@code PlayerMode}.
	 * @param player The {@link Player} whose mode will be updated.
	 * @param mode The type of mode to set, such as {@code RIDE} or {@code WINDSTREAM}.
	 * @param obj The data object associated with the chosen mode.
	 */
	public static void setPlayerMode(Player player, PlayerMode mode, Object obj)
	{
		switch (mode)
		{
			case RIDE:
				player.ride = (RideInfo) obj;
				break;
			case IN_ROLL:
				player.inRoll = (InRoll) obj;
				break;
			case WINDSTREAM:
				player.windstreamPath = (WindstreamPath) obj;
				break;
			default:
				break;
		}
	}
	
	/**
	 * Removes a specific {@link PlayerMode} from the given {@link Player}.<br>
	 * This method updates the player state and clears associated data.
	 * @param player The {@link Player} to modify.
	 * @param mode The {@link PlayerMode} to remove.
	 * @return {@code true} if the mode was successfully removed, or {@code false} otherwise.
	 */
	public static boolean unsetPlayerMode(Player player, PlayerMode mode)
	{
		switch (mode)
		{
			case RIDE:
				final RideInfo ride = player.ride;
				if (ride == null)
				{
					return false;
				}
				
				// check for sprinting when forcefully dismounting player
				if (player.isInSprintMode())
				{
					player.getLifeStats().triggerFpRestore();
					player.setSprintMode(false);
				}
				
				player.unsetState(CreatureState.RESTING);
				player.unsetState(CreatureState.FLOATING_CORPSE);
				player.setState(CreatureState.ACTIVE);
				player.ride = null;
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_EMOTE2, 0, 0), true);
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RIDE_END), true);
				
				player.getGameStats().updateStatsAndSpeedVisually();
				
				// remove rideObservers
				for (ActionObserver observer : player.getRideObservers())
				{
					player.getObserveController().removeObserver(observer);
				}
				
				player.getRideObservers().clear();
				return true;
			case IN_ROLL:
				if (player.inRoll == null)
				{
					return false;
				}
				
				player.inRoll = null;
				return true;
			case WINDSTREAM:
				if (player.windstreamPath == null)
				{
					return false;
				}
				
				player.windstreamPath = null;
				return true;
			default:
				return false;
		}
	}
}
