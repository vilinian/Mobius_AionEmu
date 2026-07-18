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

import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.PetEmote;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to trigger a specific emote for a {@link Pet}.<br>
 * It handles the synchronization of pet animations and visual effects.<br>
 * The packet contains data regarding the {@code PetEmote} being performed.
 * @author ATracer
 */
public class SM_PET_EMOTE extends AionServerPacket
{
	private final Pet pet;
	private final PetEmote emote;
	private final float x, y, z, x2, y2, z2;
	private final byte heading;
	private int emotionId, param1;
	
	/**
	 * Creates a new {@code SM_PET_EMOTE} packet.<br>
	 * This constructor initializes the packet with default coordinates and heading.<br>
	 * It uses the {@code PetEmote, float, float, float, byte)} constructor.
	 * @param pet The {@code Pet} object performing the action.
	 * @param emote The {@code PetEmote} type to be displayed.
	 */
	public SM_PET_EMOTE(Pet pet, PetEmote emote)
	{
		this(pet, emote, 0, 0, 0, (byte) 0);
	}
	
	/**
	 * Creates a new {@link SM_PET_EMOTE} packet for a pet performing an action.<br>
	 * This constructor sets the secondary coordinates to {@code 0}.
	 * @param pet The {@code Pet} object that is performing the emote.
	 * @param emote The {@code PetEmote} type being performed.
	 * @param x The X coordinate of the position.
	 * @param y The Y coordinate of the position.
	 * @param z The Z coordinate of the position.
	 * @param h The heading value for the rotation.
	 */
	public SM_PET_EMOTE(Pet pet, PetEmote emote, float x, float y, float z, byte h)
	{
		this(pet, emote, x, y, z, 0, 0, 0, h);
	}
	
	/**
	 * Creates a new {@link SM_PET_EMOTE} packet for a pet performing an emote.<br>
	 * This constructor includes specific 3D coordinates and heading data.
	 * @param pet The {@code Pet} object that is performing the action.
	 * @param emote The {@code PetEmote} type to be displayed.
	 * @param x The first X coordinate of the position.
	 * @param y The first Y coordinate of the position.
	 * @param z The first Z coordinate of the position.
	 * @param x2 The second X coordinate of the position.
	 * @param y2 The second Y coordinate of the position.
	 * @param z2 The second Z coordinate of the position.
	 * @param h The heading value for the emote.
	 */
	public SM_PET_EMOTE(Pet pet, PetEmote emote, float x, float y, float z, float x2, float y2, float z2, byte h)
	{
		this.pet = pet;
		this.emote = emote;
		this.x = x;
		this.y = y;
		this.z = z;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		heading = h;
	}
	
	/**
	 * Creates a new {@link SM_PET_EMOTE} packet with specific emotion data.<br>
	 * This constructor sets the {@code emotionId} and {@code param1} values.
	 * @param pet The {@code Pet} object performing the emote.
	 * @param emote The {@code PetEmote} type being performed.
	 * @param emotionId The unique identifier for the specific emotion.
	 * @param param1 An additional parameter used by the emotion system.
	 */
	public SM_PET_EMOTE(Pet pet, PetEmote emote, int emotionId, int param1)
	{
		this(pet, emote);
		this.emotionId = emotionId;
		this.param1 = param1;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(pet.getObjectId());
		writeC(emote.getEmoteId());
		switch (emote)
		{
			case MOVE_STOP:
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				break;
			case MOVETO:
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				writeF(x2);
				writeF(y2);
				writeF(z2);
				break;
			default:
				writeC(emotionId);
				writeC(param1); // happinessAdded?
				break;
		}
	}
}
