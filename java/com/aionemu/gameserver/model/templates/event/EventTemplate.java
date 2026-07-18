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
package com.aionemu.gameserver.model.templates.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import java.time.ZonedDateTime;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.SpawnsData2;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.Guides.GuideTemplate;
import com.aionemu.gameserver.model.templates.spawns.Spawn;
import com.aionemu.gameserver.model.templates.spawns.SpawnMap;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.DateTimeUtil;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Represents the configuration data for a game event.<br>
 * This class stores all necessary parameters required to initialize and manage an {@code Event}.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventTemplate")
public class EventTemplate
{
	@XmlElement(name = "event_drops", required = false)
	protected EventDrops eventDrops;
	@XmlElement(name = "quests", required = false)
	protected EventQuestList quests;
	@XmlElement(name = "spawns", required = false)
	protected SpawnsData2 spawns;
	@XmlElement(name = "inventory_drop", required = false)
	private InventoryDrop inventoryDrop;
	@XmlList
	@XmlElement(name = "surveys", required = false)
	protected List<String> surveys;
	@XmlAttribute(name = "name", required = true)
	protected String name;
	@XmlAttribute(name = "start", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar startDate;
	@XmlAttribute(name = "end", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar endDate;
	@XmlAttribute(name = "theme", required = false)
	private String theme;
	@XmlTransient
	protected List<VisibleObject> spawnedObjects;
	@XmlTransient
	private Future<?> invDropTask = null;
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the list of drops associated with this event.<br>
	 * This method returns the {@code eventDrops} field from the template.
	 * @return The {@link EventDrops} object containing drop information.
	 */
	public EventDrops EventDrop()
	{
		return eventDrops;
	}
	
	/**
	 * Retrieves the start date of the achievement event.<br>
	 * This method converts the internal {@code XMLGregorianCalendar} to a {@link java.time.ZonedDateTime} object.
	 * @return The {@code ZonedDateTime} representing when the event begins.
	 */
	public ZonedDateTime getStartDate()
	{
		return DateTimeUtil.getDateTime(startDate.toGregorianCalendar());
	}
	
	/**
	 * Retrieves the end date of the achievement event.<br>
	 * This method converts the internal {@code XMLGregorianCalendar} to a {@link java.time.ZonedDateTime} object.
	 * @return The {@code ZonedDateTime} representing when the event ends.
	 */
	public ZonedDateTime getEndDate()
	{
		return DateTimeUtil.getDateTime(endDate.toGregorianCalendar());
	}
	
	/**
	 * Retrieves the list of quests that can be started.<br>
	 * This method ensures the internal {@code startQuests} list is initialized.
	 * @return a {@code List<Integer>} containing the IDs of all startable quests.
	 */
	public List<Integer> getStartableQuests()
	{
		if (quests == null)
		{
			return new ArrayList<>();
		}
		
		return quests.getStartableQuests();
	}
	
	/**
	 * Retrieves the list of maintainable quest IDs for this event.<br>
	 * It returns an empty {@code List<Integer>} if no quests are defined.
	 * @return A {@code List<Integer>} containing the maintainable quest IDs.
	 */
	public List<Integer> getMaintainableQuests()
	{
		if (quests == null)
		{
			return new ArrayList<>();
		}
		
		return quests.getMaintainQuests();
	}
	
	/**
	 * Checks if the event is currently active.<br>
	 * This method compares the current time against the {@code startDate} and {@code endDate}.
	 * @return {@code true} if the current time is between the start and end dates, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return getStartDate().isBefore(java.time.ZonedDateTime.now()) && getEndDate().isAfter(java.time.ZonedDateTime.now());
	}
	
	/**
	 * Checks if the event has finished.<br>
	 * This method returns {@code true} if the current time is past the end date.<br>
	 * It internally calls the {@code isActive} method to determine the status.
	 * @return {@code true} if the event is no longer active, {@code false} otherwise.
	 */
	public boolean isExpired()
	{
		return !isActive();
	}
	
	@XmlTransient
	volatile boolean isStarted = false;
	
	/**
	 * Sets the event status to started.<br>
	 * This method updates the {@code isStarted} flag to {@code true}.
	 */
	public void setStarted()
	{
		isStarted = true;
	}
	
	/**
	 * Checks if the event has been started.<br>
	 * This method returns {@code true} if the event is currently active.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the event has started, {@code false} otherwise.
	 */
	public boolean isStarted()
	{
		return isStarted;
	}
	
	/**
	 * Initializes and activates the event.<br>
	 * This method handles spawning objects, scheduling inventory drops, and activating surveys.<br>
	 * It sets the {@code isStarted} flag to {@code true} upon completion.
	 */
	public void Start()
	{
		if (isStarted)
		{
			return;
		}
		
		if ((spawns != null) && (spawns.size() > 0))
		{
			if (spawnedObjects == null)
			{
				spawnedObjects = new ArrayList<>();
			}
			
			int spawnCount = 0;
			for (SpawnMap map : spawns.getTemplates())
			{
				DataManager.SPAWNS_DATA2.addNewSpawnMap(map);
				final Collection<Integer> instanceIds = World.getInstance().getWorldMap(map.getMapId()).getAvailableInstanceIds();
				for (Integer instanceId : instanceIds)
				{
					for (Spawn spawn : map.getSpawns())
					{
						spawn.setEventTemplate(this);
						for (SpawnSpotTemplate spot : spawn.getSpawnSpotTemplates())
						{
							final SpawnTemplate t = SpawnEngine.addNewSpawn(map.getMapId(), spawn.getNpcId(), spot.getX(), spot.getY(), spot.getZ(), spot.getHeading(), spawn.getRespawnTime());
							t.setEventTemplate(this);
							SpawnEngine.spawnObject(t, instanceId);
							spawnCount++;
						}
					}
				}
			}
			
			GameServer.log.info("[EventService] Spawned " + spawnCount + " Event objects:" + " (" + getName() + ")");
			DataManager.SPAWNS_DATA2.afterUnmarshal(null, null);
			DataManager.SPAWNS_DATA2.clearTemplates();
		}
		
		if (getInventoryDrop() != null)
		{
			invDropTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
			{
				@Override
				public void run()
				{
					World.getInstance().doOnAllPlayers(new Visitor<Player>()
					{
						
						@Override
						public void visit(Player player)
						{
							final int itemId = getInventoryDrop().getDropItem();
							if ((player.getCommonData().getLevel() >= getInventoryDrop().getStartLevel()) && (player.getCommonData().getLevel() <= getInventoryDrop().getEndLevel()) && (player.getItemMaxThisCount(itemId) < getInventoryDrop().getMaxCountOfDay()))
							{
								ItemService.dropItemToInventory(player, getInventoryDrop().getDropItem());
								player.addItemMaxCountOfDay(itemId, player.getItemMaxThisCount(itemId) + 1);
							}
						}
					});
				}
			}, getInventoryDrop().getInterval() * 60000, getInventoryDrop().getInterval() * 60000);
		}
		
		if (surveys != null)
		{
			for (String survey : surveys)
			{
				final GuideTemplate template = DataManager.GUIDE_HTML_DATA.getTemplateByTitle(survey);
				if (template != null)
				{
					template.setActivated(true);
				}
			}
		}
		
		isStarted = true;
	}
	
	/**
	 * Stops the current event and cleans up all associated resources.<br>
	 * This method removes spawned objects from the world.<br>
	 * It also cancels active inventory drop tasks.<br>
	 * Finally, it sets the {@code isStarted} status to {@code false}.
	 */
	public void Stop()
	{
		if (!isStarted)
		{
			return;
		}
		
		if (spawnedObjects != null)
		{
			for (VisibleObject o : spawnedObjects)
			{
				if (o.isSpawned())
				{
					o.getController().delete();
				}
			}
			
			DataManager.SPAWNS_DATA2.removeEventSpawnObjects(spawnedObjects);
			GameServer.log.info("[EventService] Despawned " + spawnedObjects.size() + " Event objects (" + getName() + ")");
			spawnedObjects.clear();
			spawnedObjects = null;
		}
		
		if (invDropTask != null)
		{
			invDropTask.cancel(false);
			invDropTask = null;
		}
		
		if (surveys != null)
		{
			for (String survey : surveys)
			{
				final GuideTemplate template = DataManager.GUIDE_HTML_DATA.getTemplateByTitle(survey);
				if (template != null)
				{
					template.setActivated(false);
				}
			}
		}
		
		isStarted = false;
	}
	
	/**
	 * Adds a new {@code VisibleObject} to the list of spawned objects.<br>
	 * This method initializes the internal list if it is currently {@code null}.
	 * @param object The {@code VisibleObject} to be added.
	 */
	public void addSpawnedObject(VisibleObject object)
	{
		if (spawnedObjects == null)
		{
			spawnedObjects = new ArrayList<>();
		}
		
		spawnedObjects.add(object);
	}
	
	/**
	 * Retrieves the name of the current theme.<br>
	 * This value is used to identify the specific event style.
	 * @return The {@code String} representation of the theme.
	 */
	public String getTheme()
	{
		if (theme != null)
		{
			return theme.toLowerCase();
		}
		
		return theme;
	}
	
	/**
	 * Retrieves the {@code InventoryDrop} associated with this event.<br>
	 * This method returns the specific drop data for the player's inventory.
	 * @return The {@link InventoryDrop} object.
	 */
	public InventoryDrop getInventoryDrop()
	{
		return inventoryDrop;
	}
}
