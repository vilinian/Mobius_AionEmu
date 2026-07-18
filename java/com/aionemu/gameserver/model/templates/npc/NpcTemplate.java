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
package com.aionemu.gameserver.model.templates.npc;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.ai2.AiNames;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.items.NpcEquippedGear;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.stats.KiskStatsTemplate;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;

/**
 * Represents the base configuration and static data for a Non-Player Character (NPC).<br>
 * This class defines properties such as stats, drops, and equipment for NPCs in the game world.<br>
 * It extends {@link VisibleObjectTemplate} to provide common visual and spatial attributes.
 * @author Luno
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "npc_template")
public class NpcTemplate extends VisibleObjectTemplate
{
	private int npcId;
	@XmlAttribute(name = "level", required = true)
	private byte level;
	@XmlAttribute(name = "name_id", required = true)
	private int nameId;
	@XmlAttribute(name = "title_id")
	private int titleId;
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "name_desc")
	private String desc;
	@XmlAttribute(name = "height")
	private float height = 1;
	@XmlElement(name = "stats")
	private NpcStatsTemplate statsTemplate;
	@XmlElement(name = "equipment")
	private NpcEquippedGear equipment;
	@XmlElement(name = "kisk_stats")
	private KiskStatsTemplate kiskStatsTemplate;
	@XmlElement(name = "ammo_speed")
	private int ammoSpeed = 0;
	@XmlAttribute(name = "rank")
	private NpcRank rank;
	@XmlAttribute(name = "rating")
	private NpcRating rating;
	@XmlAttribute(name = "srange")
	private int aggrorange;
	@XmlAttribute(name = "arange")
	private int attackRange;
	@XmlAttribute(name = "arate")
	private int attackRate;
	@XmlAttribute(name = "adelay")
	private int attackDelay;
	@XmlAttribute(name = "hpgauge")
	private int hpGauge;
	@XmlAttribute(name = "tribe")
	private TribeClass tribe;
	@XmlAttribute(name = "ai")
	private String ai = AiNames.DUMMY_NPC.getName();
	@XmlAttribute
	private Race race = Race.NONE;
	@XmlAttribute
	private int state;
	@XmlAttribute
	private boolean floatcorpse;
	@XmlAttribute(name = "on_mist")
	private Boolean onMist;
	@XmlElement(name = "bound_radius")
	private BoundRadius boundRadius;
	@XmlAttribute(name = "type")
	private NpcTemplateType npcTemplateType;
	@XmlAttribute(name = "ui_type")
	private NpcUiType npcUiType;
	@XmlAttribute(name = "abyss_type")
	private AbyssNpcType abyssNpcType;
	@XmlElement(name = "talk_info")
	private TalkInfo talkInfo;
	@XmlTransient
	private NpcDrop npcDrop;
	@XmlElement(name = "massive_looting")
	private MassiveLooting massiveLooting;
	
	/**
	 * Retrieves the unique identifier for this NPC template.<br>
	 * This value corresponds to the {@code npcId} field.
	 * @return The unique template ID as an {@code int}.
	 */
	@Override
	public int getTemplateId()
	{
		return npcId;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	@Override
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the unique identifier for the title.<br>
	 * This value corresponds to the {@code title_id} attribute.
	 * @return The integer ID of the title.
	 */
	public int getTitleId()
	{
		return titleId;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the current height of the player.<br>
	 * This value is stored as a {@code float}.
	 * @return The player's height.
	 */
	public float getHeight()
	{
		return height;
	}
	
	/**
	 * Retrieves the gear equipped by this {@link NpcTemplate}.<br>
	 * This method returns the {@code NpcEquippedGear} object associated with the NPC.
	 * @return The {@code NpcEquippedGear} instance.
	 */
	public NpcEquippedGear getEquipment()
	{
		return equipment;
	}
	
	/**
	 * Retrieves the current level of this summon.<br>
	 * This value is stored as a {@code byte}.
	 * @return The level of the summon.
	 */
	public byte getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the statistics template for this NPC.<br>
	 * This method returns the {@code NpcStatsTemplate} associated with the current object.
	 * @return The {@code NpcStatsTemplate} of the NPC.
	 */
	public NpcStatsTemplate getStatsTemplate()
	{
		return statsTemplate;
	}
	
	/**
	 * Sets the statistics template for this {@link NpcTemplate}.<br>
	 * This method updates the internal {@code statsTemplate} field.
	 * @param statsTemplate The new {@code NpcStatsTemplate} to assign.
	 */
	public void setStatsTemplate(NpcStatsTemplate statsTemplate)
	{
		this.statsTemplate = statsTemplate;
	}
	
	/**
	 * Retrieves the {@code KiskStatsTemplate} for this NPC.<br>
	 * This template contains specific statistics related to the Kisk system.
	 * @return the {@code KiskStatsTemplate} associated with this NPC.
	 */
	public KiskStatsTemplate getKiskStatsTemplate()
	{
		return kiskStatsTemplate;
	}
	
	/**
	 * Retrieves the {@link TribeClass} of this creature.<br>
	 * This method returns the specific class type assigned to the object.
	 * @return the {@code TribeClass} associated with this entity.
	 */
	public TribeClass getTribe()
	{
		return tribe;
	}
	
	/**
	 * Sets the {@code TribeClass} for this creature.<br>
	 * This updates the internal tribe property of the object.
	 * @param tribe The {@link TribeClass} to assign.
	 */
	public void setTribe(TribeClass tribe)
	{
		this.tribe = tribe;
	}
	
	/**
	 * Retrieves the artificial intelligence type for this NPC.<br>
	 * It checks if the NPC is a teleporter at a level greater than 1.<br>
	 * If it is a teleporter, it returns {@code siege_teleporter}.<br>
	 * Otherwise, it returns the default AI value.
	 * @return The AI type as a {@code String}.
	 */
	public String getAi()
	{
		// TODO: npc_template repars
		return (!"noaction".equals(ai) && (level > 1) && getAbyssNpcType().equals(AbyssNpcType.TELEPORTER)) ? "siege_teleporter" : ai;
	}
	
	/**
	 * Returns a string representation of the NPC template.<br>
	 * This includes the {@code npcId} and the {@code getName} value.
	 * @return A formatted string containing the ID and name.
	 */
	@Override
	public String toString()
	{
		return "Npc Template id: " + npcId + " name: " + name;
	}
	
	/**
	 * Sets the unique identifier for this template.<br>
	 * This method is used by the JAXB unmarshaller to parse the {@code uid} from an XML file.<br>
	 * It converts the provided {@code String} into an {@code int}.
	 * @param uid The string representation of the NPC ID.
	 */
	@XmlID
	@XmlAttribute(name = "npc_id", required = true)
	private void setXmlUid(String uid)
	{
		/*
		 * This method is used only by JAXB unmarshaller. I couldn't set annotations at field, because ID must be a string.
		 */
		npcId = Integer.parseInt(uid);
	}
	
	/**
	 * Retrieves the rank of this NPC.<br>
	 * This method fetches the rank from the associated {@link NpcTemplate}.
	 * @return the {@code NpcRank} of the NPC.
	 */
	public NpcRank getRank()
	{
		return rank;
	}
	
	/**
	 * Retrieves the rating of this NPC.<br>
	 * This value is fetched from the {@link NpcTemplate}.
	 * @return the {@code NpcRating} associated with this object.
	 */
	public NpcRating getRating()
	{
		return rating;
	}
	
	/**
	 * Gets the current aggression range for this NPC.<br>
	 * This value is modified by the {@link AI2Engine}.
	 * @return The calculated aggro range as an {@code int}.
	 */
	public int getAggroRange()
	{
		return aggrorange;
	}
	
	/**
	 * Retrieves the minimum allowed shout range for this NPC.<br>
	 * This method ensures that the value is never less than {@code 10}.<br>
	 * It returns the aggro range if it is greater than or equal to {@code 10}.
	 * @return The shout range as an {@code int}.
	 */
	public int getMinimumShoutRange()
	{
		if (aggrorange < 10)
		{
			return 10;
		}
		
		return aggrorange;
	}
	
	/**
	 * Retrieves the maximum distance a weapon can reach.<br>
	 * This value is stored in the {@code attackRange} field.
	 * @return The current attack range as an {@code int}.
	 */
	public int getAttackRange()
	{
		return attackRange;
	}
	
	/**
	 * Sets the maximum distance an NPC can attack.<br>
	 * This updates the {@code attackRange} field of the template.
	 * @param value The new range value to set.
	 */
	public void setAttackRange(int value)
	{
		attackRange = value;
	}
	
	/**
	 * Sets the distance at which an NPC will become aggressive.<br>
	 * This updates the internal {@code aggrorange} variable.
	 * @param value The new range for aggression as an {@code int}.
	 */
	public void setAggroRange(int value)
	{
		aggrorange = value;
	}
	
	/**
	 * Retrieves the current attack rate of the NPC.<br>
	 * This value determines how frequently the NPC performs an attack action.
	 * @return The {@code int} value representing the attack rate.
	 */
	public int getAttackRate()
	{
		return attackRate;
	}
	
	/**
	 * Retrieves the time delay between attacks.<br>
	 * This value is used to determine how often an NPC can strike a target.
	 * @return The attack delay as an {@code int}.
	 */
	public int getAttackDelay()
	{
		return attackDelay;
	}
	
	/**
	 * Retrieves the health gauge value for this NPC.<br>
	 * This value is fetched from the {@link NpcTemplate}.
	 * @return The integer value of the HP gauge.
	 */
	public int getHpGauge()
	{
		return hpGauge;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the current state of the creature.<br>
	 * This value represents the internal status of the object.
	 * @return The current state as an {@code int}.
	 */
	@Override
	public int getState()
	{
		return state;
	}
	
	/**
	 * Retrieves the {@link BoundRadius} for this NPC.<br>
	 * This value defines the area around the NPC where certain actions are restricted.
	 * @return The {@code BoundRadius} object associated with this NPC.
	 */
	@Override
	public BoundRadius getBoundRadius()
	{
		// TODO all npcs should have BR in xml
		return boundRadius != null ? boundRadius : super.getBoundRadius();
	}
	
	/**
	 * Retrieves the template type for this NPC.<br>
	 * It returns {@code NONE} if no type is assigned.
	 * @return The {@code NpcTemplateType} of the NPC.
	 */
	public NpcTemplateType getNpcTemplateType()
	{
		return npcTemplateType != null ? npcTemplateType : NpcTemplateType.NONE;
	}
	
	/**
	 * Retrieves the user interface type for this NPC.<br>
	 * It returns {@code NONE} if no specific type is set.
	 * @return the {@code NpcUiType} associated with this template.
	 */
	public NpcUiType getNpcUiType()
	{
		return npcUiType != null ? npcUiType : NpcUiType.NONE;
	}
	
	/**
	 * Retrieves the {@link AbyssNpcType} for this NPC.<br>
	 * This value is fetched from the underlying {@link NpcTemplate}.
	 * @return The {@code AbyssNpcType} associated with this object.
	 */
	public AbyssNpcType getAbyssNpcType()
	{
		return abyssNpcType != null ? abyssNpcType : AbyssNpcType.NONE;
	}
	
	/**
	 * Retrieves the distance required to initiate a conversation with this NPC.<br>
	 * If no specific information is available, it returns a default value of {@code 2}.
	 * @return The talk distance as an {@code int}.
	 */
	public int getTalkDistance()
	{
		if (talkInfo == null)
		{
			return 2;
		}
		
		return talkInfo.getDistance();
	}
	
	/**
	 * Retrieves the delay time for NPC dialogue.<br>
	 * This method checks if {@code talkInfo} exists before returning a value.<br>
	 * If no information is available, it returns {@code 0}.
	 * @return The delay value from the talk information or {@code 0} if null.
	 */
	public int getTalkDelay()
	{
		if (talkInfo == null)
		{
			return 0;
		}
		
		return talkInfo.getDelay();
	}
	
	/**
	 * Retrieves the list of function dialog IDs for this NPC.<br>
	 * This method returns {@code null} if no talk information is available.
	 * @return a {@code List<Integer>} containing the dialog IDs, or {@code null}.
	 */
	public List<Integer> getFuncDialogIds()
	{
		if (talkInfo == null)
		{
			return null;
		}
		
		return talkInfo.getFuncDialogIds();
	}
	
	/**
	 * Retrieves the specific sub-dialog type for this NPC.<br>
	 * This method checks if {@code talkInfo} exists before accessing the value.
	 * @return The sub-dialog type as a {@code String}, or {@code null} if no information is available.
	 */
	public String getSubDialogType()
	{
		if (talkInfo == null)
		{
			return null;
		}
		
		return talkInfo.getSubDialogType();
	}
	
	/**
	 * Retrieves the drop information for this NPC.<br>
	 * This method fetches data from the {@link NpcTemplate}.
	 * @return the {@code NpcDrop} object associated with this NPC.
	 */
	public NpcDrop getNpcDrop()
	{
		return npcDrop;
	}
	
	/**
	 * Sets the drop table for this NPC.<br>
	 * This method assigns a {@link NpcDrop} object to the template.
	 * @param npcDrop The {@code NpcDrop} object to associate with this NPC.
	 */
	public void setNpcDrop(NpcDrop npcDrop)
	{
		this.npcDrop = npcDrop;
	}
	
	/**
	 * Checks if the NPC is capable of being interacted with.<br>
	 * This method verifies if the {@code talkInfo} object is not {@code null}.
	 * @return {@code true} if interaction is possible, otherwise {@code false}.
	 */
	public boolean canInteract()
	{
		return talkInfo != null;
	}
	
	/**
	 * Checks if this NPC is configured to have a dialog.<br>
	 * It returns {@code true} if the NPC has valid dialog information.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the NPC is a dialog NPC, {@code false} otherwise.
	 */
	public boolean isDialogNpc()
	{
		if (talkInfo == null)
		{
			return false;
		}
		
		return talkInfo.isDialogNpc();
	}
	
	/**
	 * Checks if the NPC template is a floating corpse.<br>
	 * This method returns {@code true} if the entity is a floating corpse.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if it is a floating corpse, {@code false} otherwise.
	 */
	public boolean isFloatCorpse()
	{
		return floatcorpse;
	}
	
	/**
	 * Checks if the NPC is allowed to spawn in the mist.<br>
	 * Returns {@code true} if the condition is met.<br>
	 * Returns {@code false} otherwise.
	 * @return The spawn condition status for the mist.
	 */
	public Boolean getMistSpawnCondition()
	{
		return onMist;
	}
	
	/**
	 * Retrieves the descriptive text for this title.<br>
	 * This corresponds to the {@code desc} attribute in the XML configuration.
	 * @return The description string or {@code null} if no description is provided.
	 */
	public String getDesc()
	{
		return desc;
	}
	
	/**
	 * Retrieves the massive looting status of the NPC.<br>
	 * This value determines if the NPC allows for large scale loot collection.
	 * @return the {@code MassiveLooting} status.
	 */
	public MassiveLooting getMassiveLooting()
	{
		return massiveLooting;
	}
}
