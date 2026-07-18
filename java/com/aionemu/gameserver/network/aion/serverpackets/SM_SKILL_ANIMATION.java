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
import com.aionemu.gameserver.model.skinskill.SkillSkin;
import com.aionemu.gameserver.model.skinskill.SkillSkinList;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is used to synchronize skill animations between the server and the client.<br>
 * It ensures that players see the correct visual effects when a {@link com.aionemu.gameserver.model.gameobjects.player.Player} uses a skill.
 * @author Ghostfur (Aion-Unique)
 * @rework FrozenKiller
 */
public class SM_SKILL_ANIMATION extends AionServerPacket
{
	private SkillSkinList skillSkinList;
	private final int action;
	private int skillSkinId;
	private int expire;
	private int isActive;
	
	/**
	 * Creates a new {@code SM_SKILL_ANIMATION} packet.<br>
	 * This packet handles the visual animation of a skill skin.<br>
	 * It sets the action to {@code 0} and marks the animation as active.
	 * @param skillSkinId The unique identifier for the skill skin.
	 * @param expire The time in milliseconds before the animation expires.
	 */
	public SM_SKILL_ANIMATION(int skillSkinId, int expire)
	{
		action = 0;
		this.skillSkinId = skillSkinId;
		this.expire = expire;
		isActive = 1;
	}
	
	/**
	 * Creates a new {@code SM_SKILL_ANIMATION} packet for a specific player.<br>
	 * This constructor sets the action to {@code 1}.<br>
	 * It retrieves the skill skin list from the provided {@link Player}.
	 * @param player The {@code Player} object used to fetch the skill skin list.
	 */
	public SM_SKILL_ANIMATION(Player player)
	{
		action = 1;
		skillSkinList = player.getSkillSkinList();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(action);
		switch (action)
		{
			case 0:
				writeH(1);
				writeH(skillSkinId);
				writeD(expire);
				writeC(isActive);
				break;
			case 1:
				if (skillSkinList != null)
				{
					writeH(skillSkinList.size());
					for (SkillSkin skillSkin : skillSkinList.getSkillSkins())
					{
						writeH(skillSkin.getId());
						writeD(skillSkin.getExpireTime());
						writeC(skillSkin.getIsActive());
					}
				}
				break;
			default:
				break;
		}
	}
}
