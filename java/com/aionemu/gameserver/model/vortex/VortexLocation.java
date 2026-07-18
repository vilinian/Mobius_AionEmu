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
package com.aionemu.gameserver.model.vortex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.controllers.RVController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.vortex.HomePoint;
import com.aionemu.gameserver.model.templates.vortex.ResurrectionPoint;
import com.aionemu.gameserver.model.templates.vortex.StartPoint;
import com.aionemu.gameserver.model.templates.vortex.VortexTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.vortexservice.DimensionalVortex;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.zone.InvasionZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;

/**
 * Represents a specific location within a {@link DimensionalVortex}.<br>
 * It handles spatial logic and interactions for entities entering or existing in the vortex.<br>
 * This class implements {@link ZoneHandler} to manage zone-specific behaviors.
 * @author Source
 */
public class VortexLocation implements ZoneHandler
{
	protected boolean isActive;
	@SuppressWarnings("rawtypes")
	protected DimensionalVortex activeVortex;
	protected RVController vortexController;
	protected VortexTemplate template;
	protected int id;
	protected Race offenceRace;
	protected Race defendsRace;
	protected List<InvasionZoneInstance> zones;
	protected Map<Integer, Player> players = new HashMap<>();
	protected Map<Integer, Kisk> kisks = new HashMap<>();
	private final List<VisibleObject> spawned = new ArrayList<>();
	protected HomePoint home;
	protected ResurrectionPoint resurrection;
	protected StartPoint start;
	
	/**
	 * Creates a new instance of the {@code VortexLocation} class.<br>
	 * This constructor initializes a default location object.
	 */
	public VortexLocation()
	{
	}
	
	/**
	 * Creates a new {@code VortexLocation} using the provided template.<br>
	 * This constructor initializes all required fields from the {@link VortexTemplate}.
	 * @param template The {@code VortexTemplate} used to configure this location.
	 */
	public VortexLocation(VortexTemplate template)
	{
		this.template = template;
		id = template.getId();
		offenceRace = template.getInvadersRace();
		defendsRace = template.getDefendersRace();
		zones = new ArrayList<>();
		home = template.getHomePoint();
		resurrection = template.getResurrectionPoint();
		start = template.getStartPoint();
	}
	
	/**
	 * Checks if the portal location is currently active.<br>
	 * This method returns the current state of the {@code isActive} field.
	 * @return {@code true} if the location is active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return isActive;
	}
	
	/**
	 * Sets the current active {@link DimensionalVortex} for this location.<br>
	 * This method also updates the {@code isActive} status based on whether the provided object is {@code null}.
	 * @param vortex The {@code DimensionalVortex} to set as active.
	 */
	@SuppressWarnings("rawtypes")
	public void setActiveVortex(DimensionalVortex vortex)
	{
		isActive = vortex != null;
		activeVortex = vortex;
	}
	
	/**
	 * Retrieves the current {@code DimensionalVortex} instance.<br>
	 * This method returns the vortex that is currently active in this location.
	 * @return The active {@link DimensionalVortex} object or {@code null} if none exists.
	 */
	@SuppressWarnings("rawtypes")
	public DimensionalVortex getActiveVortex()
	{
		return activeVortex;
	}
	
	/**
	 * Sets the {@link RVController} for this vortex location.<br>
	 * This method assigns a new controller to manage the vortex behavior.
	 * @param controller The {@code RVController} to be assigned.
	 */
	public void setVortexController(RVController controller)
	{
		vortexController = controller;
	}
	
	/**
	 * Retrieves the {@code RVController} associated with this location.<br>
	 * This controller manages the logic for the dimensional vortex.
	 * @return the {@code RVController} instance.
	 */
	public RVController getVortexController()
	{
		return vortexController;
	}
	
	/**
	 * Retrieves the {@code VortexTemplate} associated with this location.<br>
	 * This provides access to the configuration data for the vortex.
	 * @return The {@link VortexTemplate} object.
	 */
	public VortexTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Retrieves the coordinates of the home point.<br>
	 * This method creates a new {@link WorldPosition} object.<br>
	 * It populates the object with the current map and coordinate values.
	 * @return A new {@code WorldPosition} containing the home point data.
	 */
	public WorldPosition getHomePoint()
	{
		return home.getHomePoint();
	}
	
	/**
	 * Retrieves the coordinates for this resurrection point.<br>
	 * It creates a new {@link WorldPosition} object using the stored map and coordinate values.
	 * @return A new {@code WorldPosition} object containing the x, y, z, and h coordinates.
	 */
	public WorldPosition getResurrectionPoint()
	{
		return resurrection.getResurrectionPoint();
	}
	
	/**
	 * Retrieves the starting coordinates for this point.<br>
	 * It creates a new {@link WorldPosition} object using the internal map and coordinate values.
	 * @return The {@code WorldPosition} representing the start location.
	 */
	public WorldPosition getStartPoint()
	{
		return start.getStartPoint();
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the race that is being defended.<br>
	 * This method returns the {@code Race} object stored in the {@code defendsRace} field.
	 * @return the defending {@link Race}
	 */
	public Race getDefendersRace()
	{
		return defendsRace;
	}
	
	/**
	 * Retrieves the race of the invaders.<br>
	 * This method returns the {@code oRace} field from the template.
	 * @return the {@link Race} object representing the invading group.
	 */
	public Race getInvadersRace()
	{
		return offenceRace;
	}
	
	/**
	 * Retrieves the unique identifier for the home world.<br>
	 * This value is obtained from the {@code getHomePoint} location.
	 * @return The {@code int} ID of the home world.
	 */
	public int getHomeWorldId()
	{
		return home.getWorldId();
	}
	
	/**
	 * Retrieves the unique identifier for the invasion world.<br>
	 * This value is obtained from the {@code start} point of the vortex.
	 * @return The {@code int} ID of the invasion world.
	 */
	public int getInvasionWorldId()
	{
		return start.getWorldId();
	}
	
	/**
	 * Retrieves the list of objects currently spawned at this location.<br>
	 * This method returns all {@link VisibleObject} instances associated with the portal.
	 * @return a {@code List} containing all {@code VisibleObject} items.
	 */
	public List<VisibleObject> getSpawned()
	{
		return spawned;
	}
	
	/**
	 * Retrieves the list of players currently at this location.<br>
	 * The map uses {@code Integer} IDs as keys to identify each {@link Player}.
	 * @return A {@code Map} containing all current {@code Player} objects.
	 */
	public Map<Integer, Player> getPlayers()
	{
		return players;
	}
	
	/**
	 * Retrieves the map of all invader {@link Kisk} objects.<br>
	 * The keys in the {@code Map} represent the unique IDs of the kisks.
	 * @return A {@code Map} containing the invader kisks.
	 */
	public Map<Integer, Kisk> getInvadersKisks()
	{
		return kisks;
	}
	
	/**
	 * Checks if a specific object is currently inside the active vortex.<br>
	 * This method verifies that the vortex is active and contains the given {@code objId}.
	 * @param objId The unique identifier of the object to check.
	 * @return {@code true} if the object is inside the active vortex, {@code false} otherwise.
	 */
	public boolean isInvaderInside(int objId)
	{
		return isActive() && getVortexController().getPassedPlayers().containsKey(objId);
	}
	
	/**
	 * Checks if a specific player is currently inside an active vortex.<br>
	 * This method returns {@code true} only if the vortex is active and the {@code Player} is within its boundaries.
	 * @param player The {@link Player} object to check.
	 * @return {@code true} if the player is inside the active vortex, otherwise {@code false}.
	 */
	public boolean isInsideActiveVortex(Player player)
	{
		return isActive() && isInsideLocation(player);
	}
	
	/**
	 * Adds a new {@code InvasionZoneInstance} to the list of zones.<br>
	 * This method also registers this location as a handler for the zone.
	 * @param zone The {@code InvasionZoneInstance} to be added.
	 */
	public void addZone(InvasionZoneInstance zone)
	{
		zones.add(zone);
		zone.addHandler(this);
	}
	
	/**
	 * Checks if a specific creature is located within this siege area.<br>
	 * It iterates through all associated {@link InvasionZoneInstance} objects to verify the position.
	 * @param creature The {@code Creature} object to check.
	 * @return {@code true} if the creature is inside any zone, otherwise {@code false}.
	 */
	public boolean isInsideLocation(Creature creature)
	{
		if (zones.isEmpty())
		{
			return false;
		}
		
		for (int i = 0; i < zones.size(); i++)
		{
			if (zones.get(i).isInsideCreature(creature))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the list of invasion zones associated with this location.
	 * @return a {@code List} containing all {@link InvasionZoneInstance} objects.
	 */
	public List<InvasionZoneInstance> getZones()
	{
		return zones;
	}
	
	/**
	 * Handles logic when a {@code Creature} enters a specific {@code ZoneInstance}.<br>
	 * This method checks if the creature is a non-GM {@link Player}.<br>
	 * It kills players who enter the wrong faction base.
	 * @param creature The {@code Creature} entering the zone.
	 * @param zone The {@code ZoneInstance} being entered.
	 */
	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone)
	{
		if (creature instanceof Kisk)
		{
			if (creature.getRace().equals(getInvadersRace()))
			{
				kisks.put(creature.getObjectId(), (Kisk) creature);
			}
		}
		else if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			
			// if (player.isGM()) {
			// return;
			// }
			
			if (!players.containsKey(player.getObjectId()))
			{
				players.put(player.getObjectId(), player);
				
				if (isActive())
				{
					if (player.getRace().equals(getInvadersRace()))
					{
						if (getVortexController().getPassedPlayers().containsKey(player.getObjectId()) && !getActiveVortex().getInvaders().containsKey(player.getObjectId()))
						{
							getActiveVortex().addPlayer(player, true);
						}
					}
					else
					{
						getActiveVortex().updateDefenders(player);
					}
				}
			}
		}
	}
	
	/**
	 * This method is called when a {@link Creature} leaves the vortex location.<br>
	 * It removes players and kisks from the internal tracking maps.<br>
	 * If the vortex is active, it handles system messages and kick timers for players based on their race.
	 * @param creature The {@link Creature} that is leaving the zone.
	 * @param zone The {@link ZoneInstance} being exited.
	 */
	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone)
	{
		if (!isInsideLocation(creature))
		{
			if (creature instanceof Kisk)
			{
				kisks.remove(creature.getObjectId());
			}
			
			if (creature instanceof Player)
			{
				final Player player = (Player) creature;
				
				// if (player.isGM()) {
				// return;
				// }
				
				players.remove(player.getObjectId());
				
				if (isActive())
				{
					if (player.getRace().equals(getInvadersRace()))
					{
						if (getVortexController().getPassedPlayers().containsKey(player.getObjectId()))
						{
							// You have left the battlefield.
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(904305));
							
							// start kick timer
							ThreadPoolManager.getInstance().schedule(() ->
							{
								if (player.isOnline() && !isInsideActiveVortex(player))
								{
									getActiveVortex().kickPlayer(player, true);
								}
							}, 10 * 1000);
						}
					}
					else
					{
						// start kick timer
						ThreadPoolManager.getInstance().schedule(() ->
						{
							if (player.isOnline() && !isInsideActiveVortex(player))
							{
								getActiveVortex().kickPlayer(player, false);
							}
						}, 10 * 1000);
					}
				}
			}
		}
	}
}
