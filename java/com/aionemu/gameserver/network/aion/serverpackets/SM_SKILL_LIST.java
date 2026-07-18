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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent from the server to the client to provide skill information.<br>
 * It contains a list of skills that the player can use or view.
 * @author modified by ATracer,MrPoke
 */
public class SM_SKILL_LIST extends AionServerPacket
{
	private final PlayerSkillEntry[] skillList;
	private final int messageId;
	private int skillNameId;
	private String skillLvl;
	public static final int YOU_LEARNED_SKILL = 1300050;
	boolean isNew = false;
	
	/**
	 * Creates a new {@code SM_SKILL_LIST} packet for a specific player.<br>
	 * This method retrieves the basic skills from the {@link Player}.<br>
	 * It initializes the packet with a default message ID of {@code 0}.
	 * @param player The {@code Player} object whose skills will be listed.
	 * @param basicSkills An array of {@code PlayerSkillEntry} objects representing the skills.
	 */
	public SM_SKILL_LIST(Player player, PlayerSkillEntry[] basicSkills)
	{
		skillList = player.getSkillList().getBasicSkills();
		messageId = 0;
	}
	
	/**
	 * Creates a new {@code SM_SKILL_LIST} packet for a specific skill.<br>
	 * This constructor wraps a single {@link PlayerSkillEntry} into an array.<br>
	 * It sets the {@code messageId} to {@code 0}.
	 * @param player The {@link Player} who owns the skill.
	 * @param stigmaSkill The specific {@link PlayerSkillEntry} to be included in the list.
	 */
	public SM_SKILL_LIST(Player player, PlayerSkillEntry stigmaSkill)
	{
		skillList = new PlayerSkillEntry[]
		{
			stigmaSkill
		};
		messageId = 0;
	}
	
	/**
	 * Creates a new {@link SM_SKILL_LIST} packet for a specific skill.<br>
	 * This constructor initializes the skill data and determines the level string.
	 * @param skillListEntry The {@code PlayerSkillEntry} containing the skill details.
	 * @param messageId The unique identifier for the message type.
	 * @param isNew A boolean flag indicating if the skill is newly learned.
	 */
	public SM_SKILL_LIST(PlayerSkillEntry skillListEntry, int messageId, boolean isNew)
	{
		skillList = new PlayerSkillEntry[]
		{
			skillListEntry
		};
		this.messageId = messageId;
		skillNameId = DataManager.SKILL_DATA.getSkillTemplate(skillListEntry.getSkillId()).getNameId();
		if ((messageId == 1330053) || (messageId == 1330005) || (messageId == 1300050))
		{
			skillLvl = String.valueOf(skillListEntry.getSkillLevel());
		}
		else
		{
			final String str = skillListEntry.getSkillTemplate().getNamedesc();
			final String str1 = String.valueOf(str.charAt(str.length() - 2));
			final String str2 = String.valueOf(str.charAt(str.length() - 1));
			skillLvl = String.valueOf(str1.replace("G", "") + str2);
		}
		
		this.isNew = isNew;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final int size = skillList.length;
		writeH(size); // skills list size
		if (isNew)
		{
			writeC(0);
		}
		else
		{
			writeC(1);
		}
		
		if (size > 0)
		{
			for (PlayerSkillEntry entry : skillList)
			{
				writeH(entry.getSkillId()); // id
				writeH(entry.getSkillLevel()); // lvl
				writeC(0x00);
				final int extraLevel = entry.getExtraLvl();
				writeC(extraLevel);
				if (isNew && (extraLevel == 0) && !entry.isStigma())
				{
					writeD((int) (System.currentTimeMillis() / 1000)); // Learned date NCSoft......
				}
				else
				{
					writeD(0);
				}
				
				if (entry.isStigma())
				{
					writeC(1);
				}
				else if (entry.isLinked())
				{
					writeC(3);
				}
				else
				{
					writeC(0);
				}
			}
		}
		
		writeD(messageId);
		if (messageId != 0)
		{
			writeH(0x24);
			writeD(skillNameId);
			writeH(0x00);
			writeS(skillLvl);
			writeH(0x00);
		}
	}
}
