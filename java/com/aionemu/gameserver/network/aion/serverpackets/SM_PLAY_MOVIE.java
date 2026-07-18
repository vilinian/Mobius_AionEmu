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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is used to instruct the client to play a specific movie.<br>
 * It handles the communication for cinematic sequences within the game.
 * @author -orz-, MrPoke
 */
public class SM_PLAY_MOVIE extends AionServerPacket
{
	private int type = 1; // if 1: CutSceneMovies else CutScenes
	private int movieId = 0;
	private int id = 0; // id scene ?
	private int restrictionId;
	private int objectId;
	
	/**
	 * This method creates a new {@code SM_PLAY_MOVIE} packet.<br>
	 * It sets the movie type and the specific movie identifier.
	 * @param type The category of the scene, such as 1 for CutSceneMovies.
	 * @param movieId The unique ID of the movie to be played.
	 */
	public SM_PLAY_MOVIE(int type, int movieId)
	{
		this.type = type;
		this.movieId = movieId;
	}
	
	/**
	 * This method creates a new {@code SM_PLAY_MOVIE} packet.<br>
	 * It is used to trigger movie playback for the player.
	 * @param type The category of the scene, such as cutscenes or movies.
	 * @param id The unique identifier for the specific scene.
	 * @param movieId The unique identifier for the movie file.
	 * @param restrictionId The ID used to check access restrictions.
	 */
	public SM_PLAY_MOVIE(int type, int id, int movieId, int restrictionId)
	{
		this(type, movieId);
		this.id = id;
		this.restrictionId = restrictionId;
	}
	
	/**
	 * This method creates a new {@code SM_PLAY_MOVIE} packet.<br>
	 * It is used to trigger movie playback in the game.<br>
	 * It initializes all required fields including the {@code objectId}.
	 * @param type The category of the scene, such as 1 for CutSceneMovies.
	 * @param id The unique identifier for the scene.
	 * @param movieId The specific ID of the movie to play.
	 * @param restrictionId The ID used for any playback restrictions.
	 * @param objectId The ID of the object associated with this movie.
	 */
	public SM_PLAY_MOVIE(int type, int id, int movieId, int restrictionId, int objectId)
	{
		this(type, id, movieId, restrictionId);
		this.objectId = objectId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(type);
		writeD(objectId);
		writeD(id);
		writeH(movieId);
		writeD(restrictionId);
	}
}
