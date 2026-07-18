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
package com.aionemu.gameserver.model.gameobjects.player.title;

import java.util.Collection;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerTitleListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.listeners.TitleChangeListener;
import com.aionemu.gameserver.model.templates.TitleTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the collection of titles associated with a {@link Player}.<br>
 * It handles title data retrieval and updates for the game server.
 * @author xavier, cura, xTz
 */
public class TitleList
{
	private final Map<Integer, Title> titles;
	private Player owner;
	
	/**
	 * Creates a new instance of {@link TitleList}.<br>
	 * Initializes an empty collection for titles.<br>
	 * Sets the owner to {@code null}.
	 */
	public TitleList()
	{
		titles = new HashMap<>();
		owner = null;
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
	 * Retrieves the {@link Player} that owns this object.<br>
	 * This method casts the result of the parent class's owner retrieval to a {@code Player}.
	 * @return The {@code Player} associated with this object.
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * Checks if a specific title exists in the list.<br>
	 * It looks for the provided {@code titleId}.
	 * @param titleId The unique identifier of the title to check.
	 * @return {@code true} if the title is found, otherwise {@code false}.
	 */
	public boolean contains(int titleId)
	{
		return titles.containsKey(titleId);
	}
	
	/**
	 * Adds a new title entry to the player's list.<br>
	 * This method creates a {@code Title} object using the provided ID and duration.<br>
	 * It validates that the {@code titleId} exists in the data manager.
	 * @param titleId The unique identifier for the title to add.
	 * @param remaining The amount of time remaining for this title.
	 */
	public void addEntry(int titleId, int remaining)
	{
		final TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if (tt == null)
		{
			throw new IllegalArgumentException("Invalid title id " + titleId);
		}
		
		titles.put(titleId, new Title(tt, titleId, remaining));
	}
	
	/**
	 * Adds a new title to the player's collection.<br>
	 * This method validates the race requirements and stores the title in the database.<br>
	 * It also handles expiration timers if the time is not {@code 0}.
	 * @param titleId The unique identifier for the title template.
	 * @param questReward A boolean indicating if the title was earned via a quest.
	 * @param time The duration of the title in seconds. Use {@code 0} for permanent titles.
	 * @return {@code true} if the title was successfully added, {@code false} otherwise.
	 */
	public boolean addTitle(int titleId, boolean questReward, int time)
	{
		final TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if (tt == null)
		{
			throw new IllegalArgumentException("Invalid title id " + titleId);
		}
		
		if (owner != null)
		{
			if ((owner.getRace() != tt.getRace()) && (tt.getRace() != Race.PC_ALL))
			{
				PacketSendUtility.sendMessage(owner, "This title is not available for your race.");
				return false;
			}
			
			final Title entry = new Title(tt, titleId, time);
			if (!titles.containsKey(titleId))
			{
				titles.put(titleId, entry);
				if (time != 0)
				{
					ExpireTimerTask.getInstance().addTask(entry, owner);
				}
				
				DAOManager.getDAO(PlayerTitleListDAO.class).storeTitles(owner, entry);
			}
			else
			{
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_TOOLTIP_LEARNED_TITLE);
				return false;
			}
			
			if (questReward)
			{
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_QUEST_GET_REWARD_TITLE(tt.getNameId()));
			}
			else
			{
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_GET_CASH_TITLE(tt.getNameId()));
			}
			
			PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(owner));
			owner.getController().updateNearbyQuests();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Updates the current display title for the player.<br>
	 * This method sends the new {@code titleId} to the owner and broadcasts it to others.<br>
	 * It also updates the {@code titleId} in the player's common data.
	 * @param titleId The unique identifier of the title to display.
	 */
	public void setDisplayTitle(int titleId)
	{
		PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(titleId));
		PacketSendUtility.broadcastPacketAndReceive(owner, (new SM_TITLE_INFO(owner, titleId)));
		owner.getCommonData().setTitleId(titleId);
	}
	
	/**
	 * Updates the bonus title for the player owner.<br>
	 * This method sends a network packet to the {@link Player}.<br>
	 * It also updates the internal data and triggers any necessary listeners.
	 * @param bonusTitleId The unique identifier for the new bonus title.
	 */
	public void setBonusTitle(int bonusTitleId)
	{
		PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(6, bonusTitleId));
		if (owner.getCommonData().getBonusTitleId() > 0)
		{
			if (owner.getGameStats() != null)
			{
				TitleChangeListener.onBonusTitleChange(owner.getGameStats(), owner.getCommonData().getBonusTitleId(), false);
			}
		}
		
		owner.getCommonData().setBonusTitleId(bonusTitleId);
		if ((bonusTitleId > 0) && (owner.getGameStats() != null))
		{
			TitleChangeListener.onBonusTitleChange(owner.getGameStats(), bonusTitleId, true);
		}
	}
	
	/**
	 * Removes a specific title from the player's list.<br>
	 * This method updates the display and bonus titles if they match the removed ID.<br>
	 * It also synchronizes the change with the database and sends a packet to the owner.
	 * @param titleId The unique identifier of the title to remove.
	 */
	public void removeTitle(int titleId)
	{
		if (!titles.containsKey(titleId))
		{
			return;
		}
		
		if (owner.getCommonData().getTitleId() == titleId)
		{
			setDisplayTitle(-1);
		}
		
		if (owner.getCommonData().getBonusTitleId() == titleId)
		{
			setBonusTitle(-1);
		}
		
		titles.remove(titleId);
		PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(owner));
		DAOManager.getDAO(PlayerTitleListDAO.class).removeTitle(owner.getObjectId(), titleId);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return titles.size();
	}
	
	/**
	 * Retrieves all {@link Title} objects associated with this list.<br>
	 * This method returns the values stored in the internal map.
	 * @return a {@code Collection} of {@link Title} objects.
	 */
	public Collection<Title> getTitles()
	{
		return titles.values();
	}
}
