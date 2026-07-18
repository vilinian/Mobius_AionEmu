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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.RiftService;
import com.aionemu.gameserver.services.VortexService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.rift.RiftEnum;
import com.aionemu.gameserver.services.rift.RiftInformer;
import com.aionemu.gameserver.services.rift.RiftManager;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the logic and interactions for Rift Valley (RV) related game mechanics.<br>
 * This controller manages {@link RiftManager} operations and coordinates with various services to process rift events.
 * @author ATracer, Source
 */
public class RVController extends NpcController
{
	private boolean isMaster = false;
	private boolean isVortex = false;
	protected Map<Integer, Player> passedPlayers = new HashMap<>();
	private static SpawnTemplate slaveSpawnTemplate;
	private Npc slave;
	private final Integer maxEntries;
	private final Integer minLevel;
	private final Integer maxLevel;
	private int usedEntries = 0;
	private boolean isAccepting;
	private final RiftEnum riftTemplate;
	private final int deSpawnedTime;
	
	/**
	 * Initializes a new {@code RVController} instance.<br>
	 * This constructor sets up the rift properties from the provided template.<br>
	 * It also configures whether this controller acts as a master based on the presence of a slave.
	 * @param slave The {@link Npc} acting as the slave for this rift.
	 * @param riftTemplate The {@link RiftEnum} containing the configuration data.
	 */
	public RVController(Npc slave, RiftEnum riftTemplate)
	{
		this.riftTemplate = riftTemplate;
		isVortex = riftTemplate.isVortex();
		maxEntries = riftTemplate.getEntries();
		minLevel = riftTemplate.getMinLevel();
		maxLevel = riftTemplate.getMaxLevel();
		deSpawnedTime = ((int) (System.currentTimeMillis() / 1000)) + (isVortex ? VortexService.getInstance().getDuration() * 3600 : RiftService.getInstance().getDuration() * 3600);
		
		if (slave != null)// master rift should be created
		{
			this.slave = slave;
			RVController.slaveSpawnTemplate = slave.getSpawn();
			isMaster = true;
			isAccepting = true;
		}
	}
	
	/**
	 * Handles the request from a {@link Player} to start a dialog.<br>
	 * This method is triggered when a player interacts with an NPC.
	 * @param player The {@code Player} object who initiated the request.
	 */
	@Override
	public void onDialogRequest(Player player)
	{
		if (!isMaster && !isAccepting)
		{
			return;
		}
		
		onRequest(player);
	}
	
	/**
	 * Handles the request logic for players interacting with this controller.<br>
	 * It determines whether to show a question window based on the {@code isVortex} status.<br>
	 * The method sets up a {@link RequestResponseHandler} to manage player acceptance or denial.<br>
	 * If accepted, it teleports the player and updates the passed players count.
	 * @param player The {@link Player} who initiated the request.
	 */
	private void onRequest(Player player)
	{
		if (isVortex)
		{
			final RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner())
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if (onAccept(responder))
					{
						final int worldId = RVController.slaveSpawnTemplate.getWorldId();
						final float x = RVController.slaveSpawnTemplate.getX();
						final float y = RVController.slaveSpawnTemplate.getY();
						final float z = RVController.slaveSpawnTemplate.getZ();
						if (responder.isInTeam())
						{
							if (responder.getCurrentTeam() instanceof PlayerGroup)
							{
								PlayerGroupService.removePlayer(responder);
							}
							else
							{
								PlayerAllianceService.removePlayer(responder);
							}
						}
						
						final VortexLocation loc = VortexService.getInstance().getLocationByRift(getOwner().getNpcId());
						TeleportService2.teleportTo(responder, loc.getStartPoint());
						TeleportService2.teleportTo(responder, worldId, x, y, z);
						AchievementService.getInstance().onUpdateAchievementAction(responder, worldId, 1, AchievementActionType.ENTER_WORLD);
						
						// A Rift Portal battle has begun.
						PacketSendUtility.sendPacket(responder, new SM_SYSTEM_MESSAGE(1401454));
						
						// Update passed players count
						passedPlayers.put(responder.getObjectId(), responder);
						syncPassed(true);
					}
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					onDeny(responder);
				}
			};
			
			final boolean requested = player.getResponseRequester().putRequest(904304, responseHandler);
			if (requested)
			{
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(904304, getOwner().getObjectId(), 5));
			}
		}
		else
		{
			final RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner())
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if (onAccept(responder))
					{
						final int worldId = slaveSpawnTemplate.getWorldId();
						final float x = slaveSpawnTemplate.getX();
						final float y = slaveSpawnTemplate.getY();
						final float z = slaveSpawnTemplate.getZ();
						
						TeleportService2.teleportTo(responder, worldId, x, y, z);
						
						// Update passed players count
						syncPassed(false);
					}
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					onDeny(responder);
				}
			};
			
			final boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_DIRECT_PORTAL, responseHandler);
			if (requested)
			{
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_DIRECT_PORTAL, 0, 0));
			}
		}
	}
	
	/**
	 * Checks if the {@link Player} can accept the request.<br>
	 * It verifies level requirements and entry limits.<br>
	 * Returns {@code true} if all conditions are met.
	 * @param player The {@link Player} attempting to accept.
	 * @return {@code true} if accepted, otherwise {@code false}.
	 */
	private boolean onAccept(Player player)
	{
		if (!isAccepting || !getOwner().isSpawned())
		{
			return false;
		}
		
		if ((player.getLevel() > getMaxLevel()) || (player.getLevel() < getMinLevel()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_INVADE_DIRECT_PORTAL_LEVEL_LIMIT);
			return false;
		}
		
		if (isVortex && (getUsedEntries() >= getMaxEntries()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_INVADE_DIRECT_PORTAL_USE_COUNT_LIMIT);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handles the logic when a {@link Player} denies an action.<br>
	 * This method currently always returns {@code true}.
	 * @param player The {@code Player} who denied the request.
	 * @return Always returns {@code true}.
	 */
	private boolean onDeny(Player player)
	{
		return true;
	}
	
	/**
	 * Handles the cleanup process when this controller is removed.<br>
	 * It notifies the {@link RiftInformer} to despawn the rift.<br>
	 * It removes the owner from the active spawns in {@link RiftManager}.<br>
	 * Finally, it calls the superclass {@code onDelete()} method.
	 */
	@Override
	public void onDelete()
	{
		RiftInformer.sendRiftDespawn(getOwner().getWorldId(), getOwner().getObjectId());
		RiftManager.getSpawned().remove(getOwner());
		super.onDelete();
	}
	
	/**
	 * Checks if this {@link RVController} instance is the master.<br>
	 * This is used to distinguish between master and slave controllers.
	 * @return {@code true} if this controller is a master, {@code false} otherwise.
	 */
	public boolean isMaster()
	{
		return isMaster;
	}
	
	/**
	 * Checks if this controller represents a vortex.
	 * @return {@code true} if the object is a vortex, {@code false} otherwise.
	 */
	public boolean isVortex()
	{
		return isVortex;
	}
	
	/**
	 * Retrieves the maximum number of entries allowed.<br>
	 * This value is defined in the {@code maxEntries} field.
	 * @return The maximum number of entries as an {@code Integer}.
	 */
	public Integer getMaxEntries()
	{
		return maxEntries;
	}
	
	/**
	 * Retrieves the minimum level required for this controller.<br>
	 * This value is used to check if a {@link Player} meets the entry requirements.
	 * @return The minimum level as an {@code Integer}.
	 */
	public Integer getMinLevel()
	{
		return minLevel;
	}
	
	/**
	 * Retrieves the maximum level required for this controller.<br>
	 * This value is defined during the initialization of the {@code RVController}.
	 * @return the maximum level as an {@code Integer}.
	 */
	public Integer getMaxLevel()
	{
		return maxLevel;
	}
	
	/**
	 * Retrieves the {@code RiftEnum} template for this controller.<br>
	 * This value was provided during the initialization of the {@link RVController}.
	 * @return The {@code RiftEnum} template associated with this instance.
	 */
	public RiftEnum getRiftTemplate()
	{
		return riftTemplate;
	}
	
	/**
	 * Retrieves the {@link Npc} associated with this controller.<br>
	 * This represents the slave entity linked to the master.
	 * @return the {@code Npc} object representing the slave.
	 */
	public Npc getSlave()
	{
		return slave;
	}
	
	/**
	 * Returns the current number of entries in use.<br>
	 * This value represents how many players are currently active.
	 * @return The count of {@code usedEntries}.
	 */
	public int getUsedEntries()
	{
		return usedEntries;
	}
	
	/**
	 * Calculates the time remaining until this object is despawned.<br>
	 * It returns the difference between the despawn timestamp and the current system time.
	 * @return The number of seconds left before despawning.
	 */
	public int getRemainTime()
	{
		return deSpawnedTime - (int) (System.currentTimeMillis() / 1000);
	}
	
	/**
	 * Retrieves the list of players who have already passed.<br>
	 * This method returns a {@code Map} containing player data.
	 * @return A {@code Map} where the key is an {@code Integer} and the value is a {@link Player}.
	 */
	public Map<Integer, Player> getPassedPlayers()
	{
		return passedPlayers;
	}
	
	/**
	 * Synchronizes the number of entries used for a rift.<br>
	 * It updates the {@code usedEntries} count based on whether an invasion is active.<br>
	 * This method also triggers a broadcast via {@code sendRiftInfo}.
	 * @param invasion A boolean indicating if the current state is an invasion.
	 */
	public void syncPassed(boolean invasion)
	{
		usedEntries = invasion ? passedPlayers.size() : ++usedEntries;
		RiftInformer.sendRiftInfo(getWorldsList(this));
	}
	
	/**
	 * Retrieves the list of world IDs associated with a {@link RVController}.<br>
	 * It checks if the controller is a master to determine which worlds to include.
	 * @param controller The {@link RVController} instance to check.
	 * @return An array of integers containing the world IDs.
	 */
	private int[] getWorldsList(RVController controller)
	{
		final int first = controller.getOwner().getWorldId();
		if (controller.isMaster())
		{
			return new int[]
			{
				first,
				RVController.slaveSpawnTemplate.getWorldId()
			};
		}
		
		return new int[]
		{
			first
		};
	}
}
