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
package com.aionemu.gameserver.model.gameobjects;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Represents the different types of emotes that a pet can perform.<br>
 * This enum is used to identify specific animations or actions for pets in the game.
 * @author ATracer
 */
public enum PetEmote
{
	MOVE_STOP(0),
	MOVETO(12),
	ALARM(-114),
	UNK_M110(-110),
	UNK_M111(-111),
	UNK_M123(-123),
	FLY(-125),
	UNK_M128(-128),
	UNKNOWN(255);
	
	private static TIntObjectHashMap<PetEmote> petEmotes;
	
	static
	{
		petEmotes = new TIntObjectHashMap<>();
		for (PetEmote emote : values())
		{
			petEmotes.put(emote.getEmoteId(), emote);
		}
	}
	
	private final int emoteId;
	
	/**
	 * Creates a new instance of {@link PetEmote}.<br>
	 * This constructor assigns the unique identifier to the object.
	 * @param emoteId The integer ID associated with the specific pet emote.
	 */
	private PetEmote(int emoteId)
	{
		this.emoteId = emoteId;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link PetEmote}.<br>
	 * This ID is used to map emotes to their specific values.
	 * @return The integer value of the {@code emoteId}.
	 */
	public int getEmoteId()
	{
		return emoteId;
	}
	
	/**
	 * Retrieves a {@link PetEmote} based on its unique ID.<br>
	 * If the ID is not found, it returns {@code PetEmote#UNKNOWN}.
	 * @param emoteId The integer ID of the emote to find.
	 * @return The matching {@code PetEmote} or {@code UNKNOWN}.
	 */
	public static PetEmote getEmoteById(int emoteId)
	{
		final PetEmote emote = petEmotes.get(emoteId);
		return emote != null ? emote : UNKNOWN;
	}
}
