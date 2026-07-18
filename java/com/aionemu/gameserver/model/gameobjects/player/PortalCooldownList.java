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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the cooldown periods for various portals used by players.<br>
 * It tracks when a player can next use a specific portal to prevent spamming.<br>
 * This class helps maintain game balance and server stability.
 * @author ATracer
 */
public class PortalCooldownList
{
	private final Player owner;
	private Map<Integer, PortalCooldownItem> portalCooldowns;
	
	/**
	 * Creates a new {@code PortalCooldownList} for a specific player.<br>
	 * This object tracks portal cooldowns associated with the {@link Player}.
	 * @param owner The {@code Player} who owns this cooldown list.
	 */
	PortalCooldownList(Player owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Checks if the portal for a specific world is currently disabled.<br>
	 * This method verifies cooldown status and entry requirements.
	 * @param worldId The unique identifier of the world to check.
	 * @return {@code true} if the portal is disabled, {@code false} otherwise.
	 */
	public boolean isPortalUseDisabled(int worldId)
	{
		if ((portalCooldowns == null) || !portalCooldowns.containsKey(worldId))
		{
			return false;
		}
		
		final PortalCooldownItem coolDown = portalCooldowns.get(worldId);
		if ((coolDown == null) || (DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCountByWorldId(worldId) == 0) || (coolDown.getEntryCount() < DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCountByWorldId(worldId)))
		{
			return false;
		}
		
		if (coolDown.getCooldown() < System.currentTimeMillis())
		{
			portalCooldowns.remove(worldId);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Retrieves the current cooldown time for a specific world.<br>
	 * It checks if the {@code worldId} exists in the internal map.<br>
	 * If no cooldown is found, it returns {@code 0}.
	 * @param worldId The unique identifier of the world to check.
	 * @return The remaining cooldown time in milliseconds.
	 */
	public long getPortalCooldown(int worldId)
	{
		if ((portalCooldowns == null) || !portalCooldowns.containsKey(worldId))
		{
			return 0;
		}
		
		return portalCooldowns.get(worldId).getCooldown();
	}
	
	/**
	 * Retrieves the number of entries for a specific world.<br>
	 * This method checks if the {@code worldId} exists in the cooldown list.<br>
	 * If it does not exist, it returns {@code 0}.
	 * @param worldId The unique identifier of the world to check.
	 * @return The total count of entries for the given {@code worldId}.
	 */
	public long getEntryCount(int worldId)
	{
		if ((portalCooldowns == null) || !portalCooldowns.containsKey(worldId))
		{
			return 0;
		}
		
		return portalCooldowns.get(worldId).getEntryCount();
	}
	
	/**
	 * Retrieves the cooldown information for a specific world.<br>
	 * This method looks up the {@code PortalCooldownItem} using the provided {@code worldId}.<br>
	 * It returns {@code null} if no cooldown exists for that ID.
	 * @param worldId The unique identifier of the world to check.
	 * @return The {@link PortalCooldownItem} associated with the world, or {@code null} if not found.
	 */
	public PortalCooldownItem getPortalCooldownItem(int worldId)
	{
		if ((portalCooldowns == null) || !portalCooldowns.containsKey(worldId))
		{
			return null;
		}
		
		return portalCooldowns.get(worldId);
	}
	
	/**
	 * Retrieves the collection of all portal cooldowns.<br>
	 * This map uses {@code Integer} as the key for world IDs.
	 * @return a {@link Map} containing all {@link PortalCooldownItem} objects.
	 */
	public Map<Integer, PortalCooldownItem> getPortalCoolDowns()
	{
		return portalCooldowns;
	}
	
	/**
	 * Updates the internal collection of portal cooldowns.<br>
	 * This method replaces the current {@code Map} with a new one.
	 * @param portalCoolDowns The new map containing {@link PortalCooldownItem} objects.
	 */
	public void setPortalCoolDowns(Map<Integer, PortalCooldownItem> portalCoolDowns)
	{
		portalCooldowns = portalCoolDowns;
	}
	
	/**
	 * Adds a new cooldown entry for a specific portal.<br>
	 * This method updates the {@code portalCooldowns} map.<br>
	 * It also sends an {@link SM_INSTANCE_INFO} packet to the player or their team.
	 * @param worldId The unique identifier for the world.
	 * @param entryCount The number of entries allowed for this portal.
	 * @param useDelay The delay time before the portal can be used again.
	 */
	public void addPortalCooldown(int worldId, int entryCount, long useDelay)
	{
		if (portalCooldowns == null)
		{
			portalCooldowns = new HashMap<>();
		}
		
		portalCooldowns.put(worldId, new PortalCooldownItem(worldId, entryCount, useDelay));
		
		if (owner.isInTeam())
		{
			owner.getCurrentTeam().sendPacket(new SM_INSTANCE_INFO(owner, worldId));
		}
		else
		{
			PacketSendUtility.sendPacket(owner, new SM_INSTANCE_INFO(owner, worldId));
		}
	}
	
	/**
	 * Removes the cooldown for a specific portal.<br>
	 * This method clears the entry from the internal map.<br>
	 * It also sends an {@link com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO} packet to update the player.
	 * @param worldId The unique identifier for the portal to clear.
	 */
	public void removePortalCoolDown(int worldId)
	{
		if (portalCooldowns != null)
		{
			portalCooldowns.remove(worldId);
		}
		
		if (owner.isInTeam())
		{
			owner.getCurrentTeam().sendPacket(new SM_INSTANCE_INFO(owner, worldId));
		}
		else
		{
			PacketSendUtility.sendPacket(owner, new SM_INSTANCE_INFO(owner, worldId));
		}
	}
	
	/**
	 * Adds a single entry to the cooldown list for a specific world.<br>
	 * This method updates the {@code entryCount} for the given {@code worldId}.<br>
	 * It also sends an {@link SM_INSTANCE_INFO} packet to the player or their team.
	 * @param worldId The unique identifier of the world to update.
	 */
	public void addEntry(int worldId)
	{
		if ((portalCooldowns != null) && portalCooldowns.containsKey(worldId))
		{
			portalCooldowns.get(worldId).setEntryCount(portalCooldowns.get(worldId).getEntryCount() + 1);
		}
		
		if (owner.isInTeam())
		{
			owner.getCurrentTeam().sendPacket(new SM_INSTANCE_INFO(owner, worldId));
		}
		else
		{
			PacketSendUtility.sendPacket(owner, new SM_INSTANCE_INFO(owner, worldId));
		}
	}
	
	/**
	 * Decreases the entry count for a specific portal.<br>
	 * This method updates the cooldown status based on the provided {@code worldId}.<br>
	 * If the count reaches zero, it removes the cooldown entirely.<br>
	 * It also sends an update packet to the player or their team.
	 * @param worldId The unique identifier of the portal to update.
	 */
	public void reduceEntry(int worldId)
	{
		if ((portalCooldowns != null) && portalCooldowns.containsKey(worldId))
		{
			portalCooldowns.get(worldId).setEntryCount(portalCooldowns.get(worldId).getEntryCount() - 1);
		}
		
		if (portalCooldowns.get(worldId).getEntryCount() == 0)
		{
			removePortalCoolDown(worldId);
			return;
		}
		
		if (owner.isInTeam())
		{
			owner.getCurrentTeam().sendPacket(new SM_INSTANCE_INFO(owner, worldId));
		}
		else
		{
			PacketSendUtility.sendPacket(owner, new SM_INSTANCE_INFO(owner, worldId));
		}
	}
	
	/**
	 * Checks if there are any active cooldowns.<br>
	 * It returns {@code true} if the {@code portalCooldowns} map is not {@code null} and contains items.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if cooldowns exist, {@code false} otherwise.
	 */
	public boolean hasCooldowns()
	{
		return (portalCooldowns != null) && (portalCooldowns.size() > 0);
	}
	
	/**
	 * Returns the total number of portal cooldown entries.<br>
	 * This method checks if {@code portalCooldowns} is {@code null}.<br>
	 * If it is {@code null}, it returns {@code 0}.<br>
	 * Otherwise, it returns the size of the map.
	 * @return The count of items in the cooldown list.
	 */
	public int size()
	{
		return portalCooldowns != null ? portalCooldowns.size() : 0;
	}
}
