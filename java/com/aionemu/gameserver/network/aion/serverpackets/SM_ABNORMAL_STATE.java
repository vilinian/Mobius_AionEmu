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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This packet is sent to the client to notify it of an abnormal state.<br>
 * It handles synchronization when a character enters or leaves a restricted status.
 * @author Avol, ATracer
 */
public class SM_ABNORMAL_STATE extends AionServerPacket
{
	private final Collection<Effect> effects;
	private final int abnormals;
	
	/**
	 * Creates a new {@link SM_ABNORMAL_STATE} packet.<br>
	 * This constructor initializes the state with specific effects and an abnormal count.
	 * @param effects A {@code Collection} of {@link Effect} objects to include.
	 * @param abnormals The number of abnormal states to apply.
	 */
	public SM_ABNORMAL_STATE(Collection<Effect> effects, int abnormals)
	{
		this.effects = effects;
		this.abnormals = abnormals;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(abnormals);
		writeD(0);
		writeD(0); // unk 4.5
		writeC(127); // Should be 127 or we have problems with debuffs
		
		writeH(effects.size());
		
		for (Effect effect : effects)
		{
			writeD(effect.getEffectorId());
			writeD(0); // TODO 6.x
			writeH(effect.getSkillId());
			writeC(effect.getSkillLevel());
			writeC(effect.getTargetSlot());
			writeD(effect.getRemainingTime());
			writeH(0); // unk 5.3
		}
	}
}
