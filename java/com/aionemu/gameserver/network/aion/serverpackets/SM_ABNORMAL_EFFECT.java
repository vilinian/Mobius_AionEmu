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

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This packet is sent to the client to notify it of an abnormal effect occurring in the game world.<br>
 * It handles the synchronization of special visual or status effects that do not follow standard mechanics.
 * @author ATracer
 */
public class SM_ABNORMAL_EFFECT extends AionServerPacket
{
	private final int effectedId;
	private int effectType = 1; // 1: creature 2: effected is player
	private final int abnormals;
	private final Collection<Effect> filtered;
	
	/**
	 * Creates a new {@link SM_ABNORMAL_EFFECT} packet.<br>
	 * This packet handles abnormal effects applied to a creature.<br>
	 * It automatically determines the effect type based on the target.
	 * @param effected The {@link Creature} receiving the effect.
	 * @param abnormals The number of abnormal effects.
	 * @param effects A collection of {@link Effect} objects to be applied.
	 */
	public SM_ABNORMAL_EFFECT(Creature effected, int abnormals, Collection<Effect> effects)
	{
		this.abnormals = abnormals;
		effectedId = effected.getObjectId();
		filtered = effects;
		
		if (effected instanceof Player)
		{
			effectType = 2;
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(effectedId);
		writeC(effectType); // unk
		writeD(0); // time
		writeD(abnormals); // unk
		writeD(0); // unk
		writeC(0x7F);
		
		writeH(filtered.size()); // effects size
		
		for (Effect effect : filtered)
		{
			switch (effectType)
			{
				case 1:
					writeH(effect.getSkillId());
					writeC(effect.getSkillLevel());
					writeC(effect.getTargetSlot());
					writeD(effect.getRemainingTime());
					writeH(0);
					break;
				case 2:
					writeD(effect.getEffectorId());
					writeD(0); // TODO 6.x
					writeH(effect.getSkillId());
					writeC(effect.getSkillLevel());
					writeC(effect.getTargetSlot());
					writeD(effect.getRemainingTime());
					writeH(0);
					break;
				default:
					writeH(effect.getSkillId());
					writeC(effect.getSkillLevel());
					break;
			}
		}
	}
}
