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
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.emotion.Emotion;

/**
 * This class provides data access methods for managing a list of {@link Emotion} objects associated with a {@link Player}.<br>
 * It handles the persistence and retrieval of player-specific emotion data from the database.
 * @author Mr. Poke
 */
public abstract class PlayerEmotionListDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerEmotionListDAO.class.getName();
	}
	
	/**
	 * @param player
	 */
	public abstract void loadEmotions(Player player);
	
	/**
	 * @param player
	 * @param emotion
	 */
	public abstract void insertEmotion(Player player, Emotion emotion);
	
	public abstract void deleteEmotion(int playerId, int emotionId);
}
