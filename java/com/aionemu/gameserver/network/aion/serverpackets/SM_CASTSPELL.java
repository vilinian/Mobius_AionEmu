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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is used to notify clients to display a spell casting animation.<br>
 * It handles the visual representation of a character performing a magical ability.
 * @author alexa026
 * @author rhys2002
 * @author Ghostfur (Aion-Unique)
 */
public class SM_CASTSPELL extends AionServerPacket
{
	private final int attackerObjectId;
	private final int spellId;
	private final int level;
	private final int targetType;
	private final int duration;
	private final boolean isCharge;
	private final int targetObjectId;
	private float x;
	private float y;
	private float z;
	private final int skinId;
	
	/**
	 * This constructor initializes a new {@code SM_CASTSPELL} packet.<br>
	 * It sets the data required to show a spell casting animation.
	 * @param attackerObjectId The unique ID of the object casting the spell.
	 * @param spellId The unique identifier for the spell being cast.
	 * @param level The level of the spell.
	 * @param targetType The type of the target.
	 * @param targetObjectId The unique ID of the target object.
	 * @param duration The length of time the spell lasts.
	 * @param isCharge Whether the spell is a charged ability.
	 * @param skinId The identifier for the visual skin of the effect.
	 */
	public SM_CASTSPELL(int attackerObjectId, int spellId, int level, int targetType, int targetObjectId, int duration, boolean isCharge, int skinId)
	{
		this.attackerObjectId = attackerObjectId;
		this.spellId = spellId;
		this.level = level;
		this.targetType = targetType;
		this.targetObjectId = targetObjectId;
		this.duration = duration;
		this.isCharge = isCharge;
		this.skinId = skinId;
	}
	
	/**
	 * This method creates a packet to show a spell casting animation.<br>
	 * It uses coordinates instead of a specific target object ID.
	 * @param attackerObjectId The unique identifier of the player casting the spell.
	 * @param spellId The unique identifier for the spell being cast.
	 * @param level The current level of the spell.
	 * @param targetType The type of entity being targeted.
	 * @param x The X coordinate of the target location.
	 * @param y The Y coordinate of the target location.
	 * @param z The Z coordinate of the target location.
	 * @param duration The length of time the animation or effect lasts.
	 * @param skinId The identifier for the visual appearance of the spell.
	 */
	public SM_CASTSPELL(int attackerObjectId, int spellId, int level, int targetType, float x, float y, float z, int duration, int skinId)
	{
		this(attackerObjectId, spellId, level, targetType, 0, duration, false, skinId);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final Player player = con.getActivePlayer();
		writeD(attackerObjectId);
		writeH(spellId);
		writeC(level);
		
		writeC(targetType);
		switch (targetType)
		{
			case 0:
			case 3:
			case 4:
				writeD(targetObjectId);
				break;
			case 1:
				writeF(x);
				writeF(y);
				writeF(z);
				break;
			case 2:
				writeF(x);
				writeF(y);
				writeF(z);
				writeD(0); // unk1
				writeD(0); // unk2
				writeD(0); // unk3
				writeD(0); // unk4
				writeD(0); // unk5
				writeD(0); // unk6
				writeD(0); // unk7
				writeD(0); // unk8
		}
		
		writeH(duration);
		writeC(0x00); // unk
		writeF(player.getGameStats().getReverseStat(StatEnum.BOOST_CASTING_TIME, 1000).getCurrent() / 1000f); // currentCastingSpeed
		writeC(isCharge ? 0x01 : 0x00); // charge?
		writeH(skinId);
	}
}
