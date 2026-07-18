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
package com.aionemu.gameserver.model.templates.world;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.world.WorldType;
import com.aionemu.gameserver.world.zone.ZoneAttributes;

/**
 * Represents the configuration data for a world map template.<br>
 * This class holds the structural properties and attributes defined in the XML configuration files.<br>
 * It is used by the server to initialize {@link com.aionemu.gameserver.world.zone.ZoneAttributes} for specific regions.
 * @author Luno
 */
@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.NONE)
public class WorldMapTemplate
{
	@XmlAttribute(name = "name")
	protected String name = "";
	@XmlAttribute(name = "id", required = true)
	protected Integer mapId;
	@XmlAttribute(name = "twin_count")
	protected int twinCount;
	@XmlAttribute(name = "beginner_twin_count")
	protected int beginnerTwinCount;
	@XmlAttribute(name = "max_user")
	protected int maxUser;
	@XmlAttribute(name = "prison")
	protected boolean prison = false;
	@XmlAttribute(name = "instance")
	protected boolean instance = false;
	@XmlAttribute(name = "death_level", required = true)
	protected int deathlevel = 0;
	@XmlAttribute(name = "water_level", required = true)
	
	// TODO: Move to Zone
	protected int waterlevel = 16;
	@XmlAttribute(name = "world_type")
	protected WorldType worldType = WorldType.NONE;
	@XmlAttribute(name = "world_size")
	protected int worldSize;
	@XmlElement(name = "ai_info")
	protected AiInfo aiInfo = AiInfo.DEFAULT;
	@XmlAttribute(name = "except_buff")
	protected boolean exceptBuff = false;
	@XmlAttribute(name = "flags")
	protected List<ZoneAttributes> flagValues;
	@XmlTransient
	protected Integer flags;
	
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
	 * Retrieves the unique identifier for the map.<br>
	 * This value corresponds to the {@code mapId} field.
	 * @return The {@code Integer} ID of the map.
	 */
	public Integer getMapId()
	{
		return mapId;
	}
	
	/**
	 * Retrieves the number of twins allowed on this map.<br>
	 * This value is checked against {@code WorldConfig.WORLD_MAX_TWINS_USUAL}.<br>
	 * If the configuration is disabled, it returns 0.<br>
	 * Otherwise, it returns the smaller value between the config limit and the map's twin count.
	 * @return The calculated number of twins allowed.
	 */
	public int getTwinCount()
	{
		if (WorldConfig.WORLD_MAX_TWINS_USUAL == 0)
		{
			return twinCount;
		}
		else if (WorldConfig.WORLD_MAX_TWINS_USUAL == -1)
		{
			// disabled
			return 0;
		}
		
		return Math.min(WorldConfig.WORLD_MAX_TWINS_USUAL, twinCount);
	}
	
	/**
	 * Retrieves the number of twins allowed for beginners on this map.<br>
	 * This value is checked against {@code WorldConfig.WORLD_MAX_TWINS_BEGINNER}.<br>
	 * If the config is set to -1, it returns 0.<br>
	 * Otherwise, it returns the smaller value between the config limit and {@code beginnerTwinCount}.
	 * @return The calculated number of allowed beginner twins.
	 */
	public int getBeginnerTwinCount()
	{
		if (WorldConfig.WORLD_MAX_TWINS_BEGINNER == 0)
		{
			return beginnerTwinCount;
		}
		else if (WorldConfig.WORLD_MAX_TWINS_BEGINNER == -1)
		{
			// disabled
			return 0;
		}
		
		return Math.min(WorldConfig.WORLD_MAX_TWINS_BEGINNER, beginnerTwinCount);
	}
	
	/**
	 * Retrieves the maximum number of users allowed on this map.<br>
	 * This value is stored in the {@code maxUser} field.
	 * @return The maximum user limit as an {@code int}.
	 */
	public int getMaxUser()
	{
		return maxUser;
	}
	
	/**
	 * Checks if the current map is designated as a prison.<br>
	 * This method returns the value of the {@code prison} attribute.
	 * @return {@code true} if the map is a prison, {@code false} otherwise.
	 */
	public boolean isPrison()
	{
		return prison;
	}
	
	/**
	 * Checks if this portal is an instance.<br>
	 * Returns {@code true} if it is an instance.<br>
	 * Returns {@code false} otherwise.
	 * @return the instance status of the portal path.
	 */
	public boolean isInstance()
	{
		return instance;
	}
	
	/**
	 * Retrieves the current water level of the map.<br>
	 * This value is used to determine environmental conditions.
	 * @return The {@code int} value representing the water level.
	 */
	public int getWaterLevel()
	{
		return waterlevel;
	}
	
	/**
	 * Retrieves the death level for this map.<br>
	 * This value determines the difficulty or penalty associated with dying in this area.
	 * @return The current {@code int} death level.
	 */
	public int getDeathLevel()
	{
		return deathlevel;
	}
	
	/**
	 * Retrieves the type of the world associated with this map.
	 * @return The {@link WorldType} of the current map.
	 */
	public WorldType getWorldType()
	{
		return worldType;
	}
	
	/**
	 * Retrieves the size of the world map.<br>
	 * This value corresponds to the {@code world_size} attribute.
	 * @return The integer size of the world.
	 */
	public int getWorldSize()
	{
		return worldSize;
	}
	
	/* Default zone attributes for the map */
	/**
	 * Checks if flying is enabled on this map.<br>
	 * This method evaluates the {@code flags} property.<br>
	 * It returns {@code true} if the fly attribute is set.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if flying is allowed, {@code false} otherwise.
	 */
	public boolean isFly()
	{
		return (flags & ZoneAttributes.FLY.getId()) != 0;
	}
	
	/**
	 * Checks if gliding is enabled on this map.<br>
	 * This method looks at the {@code flags} property to see if the glide attribute is set.
	 * @return {@code true} if players can glide, otherwise {@code false}.
	 */
	public boolean canGlide()
	{
		return (flags & ZoneAttributes.GLIDE.getId()) != 0;
	}
	
	/**
	 * Checks if the map allows placing a kisk.<br>
	 * This method evaluates the {@code flags} property against the {@code BIND} ID.
	 * @return {@code true} if the bind flag is set, {@code false} otherwise.
	 */
	public boolean canPutKisk()
	{
		return (flags & ZoneAttributes.BIND.getId()) != 0;
	}
	
	/**
	 * Checks if the map allows players to use the recall feature.<br>
	 * This method evaluates the current {@code flags} against the {@code RECALL} ID.
	 * @return {@code true} if recall is enabled, {@code false} otherwise.
	 */
	public boolean canRecall()
	{
		return (flags & ZoneAttributes.RECALL.getId()) != 0;
	}
	
	/**
	 * Checks if riding is allowed on this map.<br>
	 * This method evaluates the {@code flags} property.<br>
	 * It returns {@code true} if the ride attribute is enabled.
	 * @return {@code true} if riding is permitted, {@code false} otherwise.
	 */
	public boolean canRide()
	{
		return (flags & ZoneAttributes.RIDE.getId()) != 0;
	}
	
	/**
	 * Checks if flying while riding is allowed on this map.<br>
	 * This method evaluates the {@code flags} property against the {@code FLY_RIDE} ID.
	 * @return {@code true} if flying while riding is enabled, {@code false} otherwise.
	 */
	public boolean canFlyRide()
	{
		return (flags & ZoneAttributes.FLY_RIDE.getId()) != 0;
	}
	
	/**
	 * Checks if Player vs Player combat is enabled on this map.<br>
	 * This method evaluates the {@code flags} attribute against the {@code PVP_ENABLED} ID.
	 * @return {@code true} if PVP is allowed, {@code false} otherwise.
	 */
	public boolean isPvpAllowed()
	{
		return (flags & ZoneAttributes.PVP_ENABLED.getId()) != 0;
	}
	
	/**
	 * Checks if duels between players of the same race are permitted.<br>
	 * This method evaluates the current {@code flags} for this map.
	 * @return {@code true} if same-race duels are allowed, {@code false} otherwise.
	 */
	public boolean isSameRaceDuelsAllowed()
	{
		return (flags & ZoneAttributes.DUEL_SAME_RACE_ENABLED.getId()) != 0;
	}
	
	/**
	 * Checks if duels between different races are permitted on this map.<br>
	 * This method evaluates the current {@code flags} against the {@code DUEL_OTHER_RACE_ENABLED} bitmask.
	 * @return {@code true} if other race duels are allowed, {@code false} otherwise.
	 */
	public boolean isOtherRaceDuelsAllowed()
	{
		return (flags & ZoneAttributes.DUEL_OTHER_RACE_ENABLED.getId()) != 0;
	}
	
	/**
	 * Retrieves the raw flag value for this map.<br>
	 * This value represents specific configuration settings.
	 * @return The {@code int} value of the flags.
	 */
	public int getFlags()
	{
		return flags;
	}
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It converts the {@code flagValues} list into a single {@code flags} integer.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current instance.
	 */
	protected void afterUnmarshal(Unmarshaller u, Object parent)
	{
		flags = ZoneAttributes.fromList(flagValues);
	}
	
	/**
	 * Checks if the map has a special buff exception.<br>
	 * This returns the value of the {@code exceptBuff} attribute.
	 * @return {@code true} if the buff is excepted, {@code false} otherwise.
	 */
	public boolean isExceptBuff()
	{
		return exceptBuff;
	}
	
	/**
	 * Retrieves the AI information for this map.<br>
	 * This method returns the {@code aiInfo} object associated with the template.
	 * @return the {@link AiInfo} data for the current world map.
	 */
	public AiInfo getAiInfo()
	{
		return aiInfo;
	}
}
