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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client when a target has been selected.<br>
 * It informs the player about the {@code Creature} or {@link Player} they are currently targeting.
 * @author Sweetkr
 * @modified -Enomine- 4.0
 */
public class SM_TARGET_SELECTED extends AionServerPacket
{
	private int level;
	private int maxHp;
	private int currentHp;
	private int maxMp;
	private int currentMp;
	private int targetObjId;
	
	/**
	 * This constructor initializes the packet with data from a target.<br>
	 * It checks if the {@link Player} has a valid target.<br>
	 * It extracts stats like level and health based on the target type.
	 * @param player The {@code Player} who is selecting a target.
	 */
	public SM_TARGET_SELECTED(Player player)
	{
		if (player != null)
		{
			if (player.getTarget() instanceof Player)
			{
				final Player pl = (Player) player.getTarget();
				level = pl.getLevel();
				maxHp = pl.getLifeStats().getMaxHp();
				currentHp = pl.getLifeStats().getCurrentHp();
				maxMp = pl.getLifeStats().getMaxMp();
				currentMp = pl.getLifeStats().getCurrentMp();
			}
			else if (player.getTarget() instanceof Creature)
			{
				final Creature creature = (Creature) player.getTarget();
				level = creature.getLevel();
				maxHp = creature.getLifeStats().getMaxHp();
				currentHp = creature.getLifeStats().getCurrentHp();
				maxMp = 0;
				currentMp = 0;
			}
			else
			{
				level = 0;
				maxHp = 0;
				currentHp = 0;
				maxMp = 0;
				currentMp = 0;
			}
			
			if (player.getTarget() != null)
			{
				targetObjId = player.getTarget().getObjectId();
			}
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(targetObjId);
		writeH(level);
		writeD(maxHp);
		writeD(currentHp);
		writeD(maxMp); // Todo Check on Offi
		writeD(currentMp); // Todo Check on Offi
	}
}
