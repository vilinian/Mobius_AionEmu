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
package com.aionemu.gameserver.model.gameobjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.KiskStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_KISK_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * Represents a kisk object in the game world.<br>
 * This class handles the behavior and data for kisks associated with a {@link Player}. It extends {@link SummonedObject} to manage its presence as a summoned entity.
 * @author Sarynth, nrg
 */
public class Kisk extends SummonedObject<Player>
{
	private final Legion ownerLegion;
	private final Race ownerRace;
	private KiskStatsTemplate kiskStatsTemplate;
	private int remainingResurrections;
	private final long kiskSpawnTime;
	public final int KISK_LIFETIME_IN_SEC = 2 * 60 * 60; // 2 hours
	private final Set<Integer> kiskMemberIds;
	
	/**
	 * Creates a new instance of a {@link Kisk}.<br>
	 * This constructor initializes the kisk with its required templates and owner data.
	 * @param objId The unique identifier for the object.
	 * @param controller The {@link NpcController} that handles this entity.
	 * @param spawnTemplate The template defining where the kisk spawns.
	 * @param npcTemplate The template containing the base stats and data for the kisk.
	 * @param owner The {@link Player} who owns this kisk.
	 */
	public Kisk(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate npcTemplate, Player owner)
	{
		super(objId, controller, spawnTemplate, npcTemplate, npcTemplate.getLevel());
		
		kiskStatsTemplate = npcTemplate.getKiskStatsTemplate();
		
		if (kiskStatsTemplate == null)
		{
			kiskStatsTemplate = new KiskStatsTemplate();
		}
		
		kiskMemberIds = new HashSet<>(kiskStatsTemplate.getMaxMembers());
		remainingResurrections = kiskStatsTemplate.getMaxResurrects();
		kiskSpawnTime = System.currentTimeMillis() / 1000;
		ownerLegion = owner.getLegion();
		ownerRace = owner.getRace();
	}
	
	/**
	 * Checks if the specified {@code Creature} is an enemy of this object.<br>
	 * This method delegates the check to the {@code isEnemyFrom} method of the provided creature.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is an enemy, {@code false} otherwise.
	 */
	@Override
	public boolean isEnemy(Creature creature)
	{
		return creature.isEnemyFrom(this);
	}
	
	/**
	 * Checks if the specified {@link Player} is an enemy based on their race.<br>
	 * It compares the race of the {@code Player} with the race of the kisk owner.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the races are different, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Player player)
	{
		// int worldId = getPosition().getMapId();
		// if (worldId == 600020000 || worldId == 600030000) {
		// if (!isInsideZoneType(ZoneType.PVP)) {
		// return false;
		// }
		// }
		
		return player.getRace() != ownerRace;
	}
	
	/**
	 * Retrieves the type of the NPC object.<br>
	 * This method returns a constant value representing a standard NPC.
	 * @return the {@code NpcObjectType} of this entity.
	 */
	@Override
	public NpcObjectType getNpcObjectType()
	{
		return NpcObjectType.NORMAL;
	}
	
	/**
	 * Retrieves the usage mask for this {@link Kisk}.<br>
	 * This value is fetched from the associated {@code kiskStatsTemplate}.
	 * @return The integer value of the use mask.
	 */
	public int getUseMask()
	{
		return kiskStatsTemplate.getUseMask();
	}
	
	/**
	 * Retrieves a list of all {@link Player} objects currently in the kisk.<br>
	 * It searches for players using the IDs stored in the internal member set.<br>
	 * Only active players that are not {@code null} are included in the result.
	 * @return A {@code List} of {@link Player} objects.
	 */
	public List<Player> getCurrentMemberList()
	{
		final List<Player> currentMemberList = new ArrayList<>();
		
		for (int memberId : kiskMemberIds)
		{
			final Player member = World.getInstance().findPlayer(memberId);
			if (member != null)
			{
				currentMemberList.add(member);
			}
		}
		
		return currentMemberList;
	}
	
	/**
	 * Retrieves the total number of members currently in the {@code Kisk}.<br>
	 * This method returns the size of the internal member set.
	 * @return The current count of members as an {@code int}.
	 */
	public int getCurrentMemberCount()
	{
		return kiskMemberIds.size();
	}
	
	/**
	 * Retrieves the unique identifiers of all current members.<br>
	 * This method returns a {@code Set} containing the IDs.
	 * @return A {@code Set<Integer>} of member IDs.
	 */
	public Set<Integer> getCurrentMemberIds()
	{
		return kiskMemberIds;
	}
	
	/**
	 * Retrieves the maximum number of members allowed for this {@link Kisk}.<br>
	 * This value is fetched from the associated {@code kiskStatsTemplate}.
	 * @return The maximum member limit as an {@code int}.
	 */
	public int getMaxMembers()
	{
		return kiskStatsTemplate.getMaxMembers();
	}
	
	/**
	 * Gets the number of resurrections left for this {@link Kisk}.<br>
	 * This value decreases each time a resurrection is used.
	 * @return The current count of available resurrections.
	 */
	public int getRemainingResurrects()
	{
		return remainingResurrections;
	}
	
	/**
	 * Retrieves the maximum number of resurrections allowed for this {@link Kisk}.<br>
	 * This value is fetched from the associated {@code kiskStatsTemplate}.
	 * @return The total number of times a {@code Kisk} can be resurrected.
	 */
	public int getMaxRessurects()
	{
		return kiskStatsTemplate.getMaxResurrects();
	}
	
	/**
	 * Calculates the remaining time for the {@code Kisk} instance.<br>
	 * It subtracts the elapsed time from the total {@code KISK_LIFETIME_IN_SEC}.<br>
	 * The result is returned in seconds.
	 * @return The number of seconds left before the {@code Kisk} expires, or 0 if it has already expired.
	 */
	public int getRemainingLifetime()
	{
		final long timeElapsed = (System.currentTimeMillis() / 1000) - kiskSpawnTime;
		final int timeRemaining = (int) (KISK_LIFETIME_IN_SEC - timeElapsed);
		return (timeRemaining > 0 ? timeRemaining : 0);
	}
	
	/**
	 * Checks if a {@link Player} is allowed to join this kisk.<br>
	 * It verifies the player meets the required race, legion, or group criteria.<br>
	 * It also ensures the kisk has not reached its maximum member capacity.
	 * @param player The {@link Player} attempting to bind to the kisk.
	 * @return {@code true} if the player can join; {@code false} otherwise.
	 */
	public boolean canBind(Player player)
	{
		if (!player.getName().equals(getMasterName()))
		{
			// Check if they fit the usemask
			switch (getUseMask())
			{
				case 0:
				case 1: // Race
					if (ownerRace != player.getRace())
					{
						return false;
					}
					break;
				
				case 2: // Legion
					if ((ownerLegion == null) || !ownerLegion.isMember(player.getObjectId()))
					{
						return false;
					}
					break;
				case 3: // Solo
					return false; // Already Checked Name
					
				case 4: // Group (PlayerGroup or PlayerAllianceGroup)
					if (!player.isInTeam() || !player.getCurrentGroup().hasMember(getCreatorId()))
					{
						return false;
					}
					break;
				case 5: // Alliance
					if (!player.isInTeam() || (player.isInAlliance2() && !player.getPlayerAlliance2().hasMember(getCreatorId())) || (player.isInGroup2() && !player.getPlayerGroup2().hasMember(getCreatorId())))
					{
						return false;
					}
					break;
				
				default:
					return false;
			}
		}
		
		if (getCurrentMemberCount() >= getMaxMembers())
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Adds a {@link Player} to the current kisk group.<br>
	 * This method updates the player's kisk status and broadcasts changes.
	 * @param player The {@code Player} object to be added.
	 */
	public synchronized void addPlayer(Player player)
	{
		if (kiskMemberIds.add(player.getObjectId()))
		{
			broadcastKiskUpdate();
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_KISK_UPDATE(this));
		}
		
		player.setKisk(this);
	}
	
	/**
	 * Removes a {@link Player} from the current kisk.<br>
	 * This method updates the player's status and broadcasts the change if successful.
	 * @param player The {@code Player} object to be removed.
	 */
	public synchronized void removePlayer(Player player)
	{
		player.setKisk(null);
		if (kiskMemberIds.remove(player.getObjectId()))
		{
			broadcastKiskUpdate();
		}
	}
	
	/**
	 * Sends the {@code SM_KISK_UPDATE} packet to relevant players.<br>
	 * It updates members who are not already in the known list.<br>
	 * It also updates all players of the same race as the owner who are in the known list.
	 */
	private void broadcastKiskUpdate()
	{
		// on all members, but not the ones in knownlist, they will receive the update in the next step
		for (Player member : getCurrentMemberList())
		{
			if (!getKnownList().knowns(member))
			{
				PacketSendUtility.sendPacket(member, new SM_KISK_UPDATE(this));
			}
		}
		
		final Kisk kisk = this;
		
		// all players having the same race in knownlist
		getKnownList().doOnAllPlayers(object ->
		{
			// Logic to prevent enemy race from knowing kisk information.
			if (object.getRace() == ownerRace)
			{
				PacketSendUtility.sendPacket(object, new SM_KISK_UPDATE(kisk));
			}
		});
	}
	
	/**
	 * Sends a system message to all current members of the kisk.<br>
	 * This method iterates through the list provided by {@code getCurrentMemberList}.<br>
	 * It ensures that only non-null {@code Player} objects receive the packet.
	 * @param message The {@code SM_SYSTEM_MESSAGE} to be sent to each member.
	 */
	public void broadcastPacket(SM_SYSTEM_MESSAGE message)
	{
		for (Player member : getCurrentMemberList())
		{
			if (member != null)
			{
				PacketSendUtility.sendPacket(member, message);
			}
		}
	}
	
	/**
	 * Decrements the count of available resurrections.<br>
	 * Updates the current state and broadcasts changes to all players.<br>
	 * Deletes the {@code Kisk} object if no resurrections remain.
	 */
	public void resurrectionUsed()
	{
		remainingResurrections--;
		broadcastKiskUpdate();
		if (remainingResurrections <= 0)
		{
			getController().onDelete();
		}
	}
	
	/**
	 * Retrieves the race of the owner.<br>
	 * This method returns the {@code Race} associated with this kisk.
	 * @return the {@code Race} of the owner.
	 */
	public Race getOwnerRace()
	{
		return ownerRace;
	}
	
	/**
	 * Checks if the kisk is currently active.<br>
	 * A kisk is active if it is not dead and has remaining resurrections.
	 * @return {@code true} if the kisk is active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return !getLifeStats().isAlreadyDead() && (getRemainingResurrects() > 0);
	}
}
