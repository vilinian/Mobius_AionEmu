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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * This packet is received from the client to signal that a movie playback has finished.<br>
 * It allows the server to proceed with any logic tied to the completion of a cinematic sequence.
 * @author MrPoke
 */
public class CM_PLAY_MOVIE_END extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int type;
	@SuppressWarnings("unused")
	private int targetObjectId;
	@SuppressWarnings("unused")
	private int dialogId;
	private int movieId;
	@SuppressWarnings("unused")
	private int unk;
	
	/**
	 * Handles the end of a movie playback.<br>
	 * This packet is sent to notify the server that a movie has finished.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} associated with the request.
	 * @param restStates Additional {@link State} objects if required.
	 */
	public CM_PLAY_MOVIE_END(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		type = readC();
		targetObjectId = readD();
		dialogId = readD();
		movieId = readH();
		unk = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		QuestEngine.getInstance().onMovieEnd(new QuestEnv(null, player, 0, 0), movieId);
		if (player.getPosition().isInstanceMap())
		{
			player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayMovieEnd(player, movieId);
		}
		else
		{
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onPlayMovieEnd(player, movieId);
		}
	}
}
