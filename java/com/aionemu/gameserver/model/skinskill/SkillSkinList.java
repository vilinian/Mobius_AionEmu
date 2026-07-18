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
package com.aionemu.gameserver.model.skinskill;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerSkillSkinListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.SkillSkinTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the collection of skill skins associated with a {@link Player}.<br>
 * It handles the retrieval and processing of active {@link SkillSkinTemplate} data.<br>
 * Use this class to manage how visual effects are applied to specific skills.
 * @author Ghostfur (Aion-Unique)
 * @rework FrozenKiller
 */
public class SkillSkinList
{
	private final Map<Integer, SkillSkin> skillskins;
	private Player owner;
	
	/**
	 * Creates a new instance of {@code SkillSkinList}.<br>
	 * Initializes an empty map for skill skins.<br>
	 * Sets the owner to {@code null}.
	 */
	public SkillSkinList()
	{
		skillskins = new HashMap<>();
		owner = null;
	}
	
	/**
	 * Retrieves the {@link Player} that owns this object.<br>
	 * This method casts the result of the parent class's owner retrieval to a {@code Player}.
	 * @return The {@code Player} associated with this object.
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * Sets the {@link Player} who owns this support object.<br>
	 * This method updates the internal {@code owner} field.<br>
	 * It will throw an {@code IllegalArgumentException} if the provided value is {@code null}.
	 * @param owner The {@code Player} to be set as the owner.
	 */
	public void setOwner(Player owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Checks if a specific skin exists in the list.<br>
	 * This method looks for the provided {@code skinId}.
	 * @param skinId The unique identifier of the skin to check.
	 * @return {@code true} if the skin is found, otherwise {@code false}.
	 */
	public boolean contains(int skinId)
	{
		return skillskins.containsKey(skinId);
	}
	
	/**
	 * Adds a new {@code SkillSkin} to the list.<br>
	 * This method creates an entry using the provided data.<br>
	 * It will throw an exception if the {@code skinId} is invalid.
	 * @param skinId The unique identifier for the skill skin.
	 * @param remaining The amount of time remaining for the skin.
	 * @param active The status indicating if the skin is currently active.
	 */
	public void addEntry(int skinId, int remaining, int active)
	{
		final SkillSkinTemplate sst = DataManager.SKILL_SKIN_DATA.getSkillSkinTemplate(skinId);
		if (sst == null)
		{
			throw new IllegalArgumentException("Invalid skill skin id " + skinId);
		}
		
		skillskins.put(skinId, new SkillSkin(sst, skinId, remaining, active));
	}
	
	/**
	 * Adds a new skill skin to the player's collection.<br>
	 * This method validates the {@code skinId} and saves it to the database.<br>
	 * It also handles timer tasks if the provided {@code time} is non-zero.
	 * @param skinId The unique identifier for the skill skin.
	 * @param time The duration of the animation in seconds.
	 * @param expireTime The timestamp when the skin should expire.
	 * @return {@code true} if the skin was added successfully, {@code false} otherwise.
	 */
	public boolean addSkillSkin(int skinId, int time, int expireTime)
	{
		final SkillSkinTemplate sst = DataManager.SKILL_SKIN_DATA.getSkillSkinTemplate(skinId);
		if (sst == null)
		{
			throw new IllegalArgumentException("Invalid skin id " + skinId);
		}
		
		if (owner != null)
		{
			final SkillSkin skillSkin = new SkillSkin(sst, skinId, expireTime, 1); // expireTime = System.currentTimeMillis() / 1000 + minutes * 60 (Calculated in SkillAnimationAction)
			if (!skillskins.containsKey(skinId))
			{
				skillskins.put(skinId, skillSkin);
				if (time != 0)
				{
					ExpireTimerTask.getInstance().addTask(skillSkin, owner);
				}
				
				DAOManager.getDAO(PlayerSkillSkinListDAO.class).storeSkillSkins(owner, skillSkin);
			}
			else
			{
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_COSTUME_SKILL_ALREADY_HAS_COSTUME);
				return false;
			}
			
			PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_GET_ITEM(sst.getName()));
			PacketSendUtility.sendPacket(owner, new SM_SKILL_ANIMATION(skinId, time)); // time = templateTime * 60 (Calculated in SkillAnimationAction)
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes a specific skill skin from the player's list.<br>
	 * This method updates the internal map and notifies the database.<br>
	 * It also sends an animation packet to the {@link Player}.
	 * @param skinId The unique identifier of the skill skin to remove.
	 */
	public void removeSkillSkin(int skinId)
	{
		if (!skillskins.containsKey(skinId))
		{
			return;
		}
		
		skillskins.remove(skinId);
		PacketSendUtility.sendPacket(owner, new SM_SKILL_ANIMATION(owner));
		DAOManager.getDAO(PlayerSkillSkinListDAO.class).removeSkillSkin(owner.getObjectId(), skinId);
	}
	
	/**
	 * Sets the active skill skin for the owner.<br>
	 * This method updates the database and refreshes the {@link Player} object.<br>
	 * It also sends a visual update packet to the player.
	 * @param skinId The unique identifier of the skin to activate.
	 */
	public void setActive(int skinId)
	{
		DAOManager.getDAO(PlayerSkillSkinListDAO.class).setActive(owner.getObjectId(), skinId);
		owner.setSkillSkinList(DAOManager.getDAO(PlayerSkillSkinListDAO.class).loadSkillSkinList(owner.getObjectId()));
		PacketSendUtility.sendPacket(owner, new SM_SKILL_ANIMATION(owner));
	}
	
	/**
	 * Deactivates the active skill skin for a specific skill group.<br>
	 * This method finds the currently active skin and updates its status in the database.<br>
	 * It then refreshes the owner's skill list and sends an animation packet to the player.
	 * @param skillId The unique identifier of the skill to process.
	 */
	public void setDeactive(int skillId)
	{
		int skinIdToremove = 0;
		final SkillTemplate skillGroup = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (owner.getSkillSkinList() != null)
		{
			for (SkillSkin skillSkin : owner.getSkillSkinList().getSkillSkins())
			{
				if (skillSkin.getTemplate() != null)
				{
					if (skillSkin.getTemplate().getSkillGroup().equalsIgnoreCase(skillGroup.getSkillGroup()) && (skillSkin.getIsActive() == 1))
					{
						skinIdToremove = skillSkin.getId();
						break;
					}
				}
			}
		}
		
		DAOManager.getDAO(PlayerSkillSkinListDAO.class).setDeactive(owner.getObjectId(), skinIdToremove);
		owner.setSkillSkinList(DAOManager.getDAO(PlayerSkillSkinListDAO.class).loadSkillSkinList(owner.getObjectId()));
		PacketSendUtility.sendPacket(owner, new SM_SKILL_ANIMATION(owner));
	}
	
	/**
	 * Retrieves the active skin ID for a specific skill.<br>
	 * It checks if the {@code owner} has any active skins in their list.<br>
	 * The method returns {@code 0} if no valid skin is found.
	 * @param skillId The unique identifier of the skill to check.
	 * @return The ID of the active skin or {@code 0}.
	 */
	public int getSkinId(int skillId)
	{
		int skinId = 0;
		if ((skillId == 0) || (getOwner().getSkillSkinList() == null) || (getOwner() == null))
		{
			return 0;
		}
		
		for (SkillSkin skillSkin : getOwner().getSkillSkinList().getSkillSkins())
		{
			if (DataManager.SKILL_DATA.getSkillTemplate(skillId).getSkillGroup() != null)
			{
				if (skillSkin.getTemplate().getSkillGroup().equalsIgnoreCase(DataManager.SKILL_DATA.getSkillTemplate(skillId).getSkillGroup()) && (skillSkin.getIsActive() == 1))
				{
					skinId = skillSkin.getId();
				}
			}
		}
		
		return skinId;
	}
	
	/**
	 * Returns the total number of skill skins.<br>
	 * This method calls {@code size} to get the count.
	 * @return The number of items currently stored in the collection.
	 */
	public int size()
	{
		return skillskins.size();
	}
	
	/**
	 * Retrieves all {@link SkillSkin} objects associated with this list.<br>
	 * This method returns the values stored in the internal map.
	 * @return A {@code Collection} of all available {@code SkillSkin} instances.
	 */
	public Collection<SkillSkin> getSkillSkins()
	{
		return skillskins.values();
	}
}
