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
package com.aionemu.gameserver.services.vortexservice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.model.vortex.VortexStateType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the logic and state for world invasions.<br>
 * This class handles how invasion events are triggered and processed within a {@link VortexLocation}.
 * @author Source
 */
public class Invasion extends DimensionalVortex<VortexLocation>
{
	PlayerAlliance invAlliance, defAlliance;
	protected Map<Integer, Player> invaders = new ConcurrentHashMap<>();
	protected Map<Integer, Player> defenders = new ConcurrentHashMap<>();
	
	/**
	 * Creates a new {@link Invasion} instance.<br>
	 * This constructor initializes the invasion at a specific location.<br>
	 * It uses the provided {@code vortex} to set the starting point.
	 * @param vortex The {@code VortexLocation} where the invasion will occur.
	 */
	public Invasion(VortexLocation vortex)
	{
		super(vortex);
	}
	
	/**
	 * Starts the invasion process for this vortex.<br>
	 * It sets the active vortex and spawns the {@code VortexStateType.INVASION} state.<br>
	 * This method also initializes the rift generator and updates alliances.
	 */
	@Override
	public void startInvasion()
	{
		getVortexLocation().setActiveVortex(this);
		despawn();
		spawn(VortexStateType.INVASION);
		initRiftGenerator();
		updateAlliance();
	}
	
	/**
	 * Stops the current invasion event.<br>
	 * This method resets the vortex state to {@code PEACE}.<br>
	 * It removes all active invaders and kills any remaining kisks.<br>
	 * All participating players are kicked from the instance.
	 */
	@Override
	public void stopInvasion()
	{
		getVortexLocation().setActiveVortex(null);
		unregisterSiegeBossListeners();
		for (Kisk kisk : getVortexLocation().getInvadersKisks().values())
		{
			kisk.getController().die();
		}
		
		for (Player invader : invaders.values())
		{
			if (invader.isOnline())
			{
				kickPlayer(invader, true);
			}
		}
		
		despawn();
		spawn(VortexStateType.PEACE);
	}
	
	/**
	 * Adds a {@link Player} to the current invasion.<br>
	 * This method assigns the player to either the invaders or defenders list based on the {@code isInvader} flag.<br>
	 * It also handles alliance creation and group management for the new player.
	 * @param player The {@link Player} object to be added.
	 * @param isInvader Set to {@code true} if the player is an invader, or {@code false} if they are a defender.
	 */
	@Override
	public void addPlayer(Player player, boolean isInvader)
	{
		final Map<Integer, Player> list = isInvader ? invaders : defenders;
		final PlayerAlliance alliance = isInvader ? invAlliance : defAlliance;
		
		if ((alliance != null) && (alliance.size() > 0))
		{
			PlayerAllianceService.addPlayer(alliance, player);
		}
		else if (!list.isEmpty())
		{
			Player first = null;
			
			for (Player firstOne : list.values())
			{
				if (firstOne.isInGroup2())
				{
					PlayerGroupService.removePlayer(firstOne);
				}
				else if (firstOne.isInAlliance2())
				{
					PlayerAllianceService.removePlayer(firstOne);
				}
				
				first = firstOne;
			}
			
			if ((first != null) && (first.getObjectId() != player.getObjectId()))
			{
				if (isInvader)
				{
					invAlliance = PlayerAllianceService.createAlliance(first, player, TeamType.ALLIANCE_OFFENCE);
				}
				else
				{
					defAlliance = PlayerAllianceService.createAlliance(first, player, TeamType.ALLIANCE_DEFENCE);
				}
			}
			else
			{
				kickPlayer(player, isInvader);
			}
		}
		
		list.put(player.getObjectId(), player);
	}
	
	/**
	 * Removes a {@link Player} from the current invasion.<br>
	 * This method handles alliance updates and teleports invaders back to their home point if necessary.
	 * @param player The {@link Player} object to be removed from the list.
	 * @param isInvader A boolean value where {@code true} indicates the player is an invader and {@code false} indicates they are a defender.
	 */
	@Override
	public void kickPlayer(Player player, boolean isInvader)
	{
		final Map<Integer, Player> list = isInvader ? invaders : defenders;
		final PlayerAlliance alliance = isInvader ? invAlliance : defAlliance;
		
		list.remove(player.getObjectId());
		
		if ((alliance != null) && alliance.hasMember(player.getObjectId()))
		{
			if (player.isOnline())
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(isInvader ? 1401452 : 1401476));
			}
			
			PlayerAllianceService.removePlayer(player);
			if (alliance.size() == 0)
			{
				if (isInvader)
				{
					invAlliance = null;
				}
				else
				{
					defAlliance = null;
				}
			}
		}
		
		if (isInvader && player.isOnline() && (player.getWorldId() == getVortexLocation().getInvasionWorldId()))
		{
			// You will be returned to where you entered.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401474));
			TeleportService2.teleportTo(player, getVortexLocation().getHomePoint());
		}
		
		getVortexLocation().getVortexController().getPassedPlayers().remove(player.getObjectId());
		getVortexLocation().getVortexController().syncPassed(true);
	}
	
	/**
	 * Registers a {@link Player} as a defender for the current invasion.<br>
	 * This method checks if the player is already registered before proceeding.<br>
	 * It sends a request to the player to join the defense alliance.<br>
	 * If the player accepts, they are added to the defenders list.
	 * @param defender The {@link Player} who will be joining the defense.
	 */
	@Override
	public void updateDefenders(Player defender)
	{
		if (defenders.containsKey(defender.getObjectId()))
		{
			return;
		}
		
		if ((defAlliance == null) || !defAlliance.isFull())
		{
			final RequestResponseHandler responseHandler = new RequestResponseHandler(defender)
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if (responder.isInGroup2())
					{
						PlayerGroupService.removePlayer(responder);
					}
					else if (responder.isInAlliance2())
					{
						PlayerAllianceService.removePlayer(responder);
					}
					
					if ((defAlliance == null) || !defAlliance.isFull())
					{
						addPlayer(responder, false);
					}
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					// do nothing
				}
			};
			
			final boolean requested = defender.getResponseRequester().putRequest(904306, responseHandler);
			if (requested)
			{
				PacketSendUtility.sendPacket(defender, new SM_QUESTION_WINDOW(904306, 0, 0));
			}
		}
	}
	
	/**
	 * Updates the list of active invaders.<br>
	 * This method checks if the {@code Player} is already in the {@code invaders} map.<br>
	 * If they are not present, it calls {@code boolean)} to add them.
	 * @param invader The {@code Player} object to be added as an invader.
	 */
	@Override
	public void updateInvaders(Player invader)
	{
		if (invaders.containsKey(invader.getObjectId()))
		{
			return;
		}
		
		addPlayer(invader, true);
	}
	
	/**
	 * Updates the list of defenders for the current invasion.<br>
	 * This method checks all players at the {@link VortexLocation}.<br>
	 * It identifies defenders based on their race.<br>
	 * It calls {@code updateDefenders} for each matching player.
	 */
	private void updateAlliance()
	{
		for (Player player : getVortexLocation().getPlayers().values())
		{
			if (player.getRace().equals(getVortexLocation().getDefendersRace()))
			{
				updateDefenders(player);
			}
		}
	}
	
	/**
	 * Retrieves the current list of invading players.<br>
	 * The map uses {@code Integer} IDs as keys to identify each {@link Player}.
	 * @return A {@code Map} containing all players currently marked as invaders.
	 */
	@Override
	public Map<Integer, Player> getInvaders()
	{
		return invaders;
	}
	
	/**
	 * Retrieves the list of players currently defending the vortex.<br>
	 * This method returns a {@code Map} where the key is the player ID.
	 * @return A {@code Map} containing all defender {@link Player} objects.
	 */
	@Override
	public Map<Integer, Player> getDefenders()
	{
		return defenders;
	}
}
