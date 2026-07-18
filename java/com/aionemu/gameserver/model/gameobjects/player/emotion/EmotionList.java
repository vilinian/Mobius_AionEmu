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
package com.aionemu.gameserver.model.gameobjects.player.emotion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dao.PlayerEmotionListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the collection of emotions available to a {@link Player}.<br>
 * It handles retrieving and sending emotion data to the client via {@code SM_EMOTION_LIST} packets.
 * @author MrPoke
 */
public class EmotionList
{
	private Map<Integer, Emotion> emotions;
	private final Player owner;
	
	/**
	 * Creates a new {@code EmotionList} for a specific player.<br>
	 * This object manages the emotions owned by the {@link Player}.
	 * @param owner The {@code Player} who owns this list of emotions.
	 */
	public EmotionList(Player owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Adds a new emotion to the player's list.<br>
	 * This method handles both local storage and database updates if {@code isNew} is true.
	 * @param emotionId The unique identifier for the emotion.
	 * @param dispearTime The duration in seconds before the emotion disappears.
	 * @param isNew Set to {@code true} to save to the database and send a network packet.
	 */
	public void add(int emotionId, int dispearTime, boolean isNew)
	{
		if (emotions == null)
		{
			emotions = new HashMap<>();
		}
		
		final Emotion emotion = new Emotion(emotionId, dispearTime);
		emotions.put(emotionId, emotion);
		
		if (isNew)
		{
			if (emotion.getExpireTime() != 0)
			{
				ExpireTimerTask.getInstance().addTask(emotion, owner);
			}
			
			DAOManager.getDAO(PlayerEmotionListDAO.class).insertEmotion(owner, emotion);
			PacketSendUtility.sendPacket(owner, new SM_EMOTION_LIST((byte) 1, Collections.singletonList(emotion)));
		}
	}
	
	/**
	 * Removes a specific emotion from the player's list.<br>
	 * This method updates the internal map and deletes the entry from the database.<br>
	 * It also sends an updated {@link SM_EMOTION_LIST} packet to the owner.
	 * @param emotionId The unique identifier of the emotion to remove.
	 */
	public void remove(int emotionId)
	{
		emotions.remove(emotionId);
		DAOManager.getDAO(PlayerEmotionListDAO.class).deleteEmotion(owner.getObjectId(), emotionId);
		PacketSendUtility.sendPacket(owner, new SM_EMOTION_LIST((byte) 0, getEmotions()));
	}
	
	/**
	 * Checks if a specific emotion exists in the list.<br>
	 * It returns {@code true} if the ID is found.<br>
	 * It returns {@code false} if the ID is missing or the list is null.
	 * @param emotionId The unique identifier of the emotion to check.
	 * @return A boolean indicating whether the emotion exists.
	 */
	public boolean contains(int emotionId)
	{
		if (emotions == null)
		{
			return false;
		}
		
		return emotions.containsKey(emotionId);
	}
	
	/**
	 * Checks if the player is allowed to use a specific emotion.<br>
	 * It verifies the {@code emotionId} against allowed ranges.<br>
	 * It also checks if the owner has the required permissions.
	 * @param emotionId The unique identifier of the emotion to check.
	 * @return {@code true} if the emotion can be used, otherwise {@code false}.
	 */
	public boolean canUse(int emotionId)
	{
		return (emotionId < 64) || (emotionId > 155) || ((emotions != null) && emotions.containsKey(emotionId)) || owner.havePermission(MembershipConfig.EMOTIONS_ALL);
	}
	
	/**
	 * Retrieves all {@link Emotion} objects associated with this list.<br>
	 * Returns an empty collection if no emotions exist.
	 * @return a {@code Collection} of {@code Emotion} objects.
	 */
	public Collection<Emotion> getEmotions()
	{
		if (emotions == null)
		{
			return Collections.emptyList();
		}
		
		return emotions.values();
	}
}
