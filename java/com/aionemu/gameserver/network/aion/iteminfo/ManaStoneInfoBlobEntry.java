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
package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.IdianStone;
import com.aionemu.gameserver.model.items.ItemStone;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.RealRandomBonusStat;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This class represents a data entry for sending information about {@link ManaStone} objects.<br>
 * It is used to package specific stone details into an {@link ItemInfoBlob}.
 * @author -Nemesiss-
 * @modified Rolandas
 * @modified Alcapwnd
 * @Reworked GiGatR00n
 * @reworked Himiko and FrozenKiller
 */
public class ManaStoneInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new entry for mana stone information.<br>
	 * This object handles the data structure for {@code MANA_SOCKETS}.<br>
	 * It is used to serialize specific details about mana stones into a network buffer.
	 */
	ManaStoneInfoBlobEntry()
	{
		super(ItemBlobType.MANA_SOCKETS);
	}
	
	/**
	 * Writes the mana stone data into a {@code ByteBuffer}.<br>
	 * This method serializes various properties like enchant levels and stones.<br>
	 * It handles specific details such as dye expiration and polish stats.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		final Item item = ownerItem;
		
		writeC(buf, item.isSoulBound() ? 1 : 0);
		writeC(buf, item.getItemTemplate().getMaxAuthorize() > 0 ? 0 : item.getEnchantOrAuthorizeLevel()); // enchant (1-15)
		writeD(buf, item.getItemSkinTemplate().getTemplateId());
		writeC(buf, item.getOptionalSocket());
		writeC(buf, 0); // enchant Bonus
		writeItemStones(buf);
		
		final ItemStone god = item.getGodStone();
		writeD(buf, god == null ? 0 : god.getItemId());
		final int itemColor = item.getItemColor();
		final int dyeExpiration = item.getColorTimeLeft();
		
		// expired dyed items
		if ((((dyeExpiration > 0) && (item.getColorExpireTime() > 0)) || ((dyeExpiration == 0) && (item.getColorExpireTime() == 0))) && item.getItemTemplate().isItemDyePermitted())
		{
			writeC(buf, itemColor == 0 ? 0 : 1);
			writeD(buf, itemColor);
			writeD(buf, 0); // unk 1.5.1.9
			writeD(buf, dyeExpiration); // seconds until dye expires
		}
		else
		{
			writeC(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0); // unk 1.5.1.9
			writeD(buf, 0);
		}
		
		final IdianStone idianStone = item.getIdianStone();
		if ((idianStone != null) && (idianStone.getPolishNumber() > 0))
		{
			writeD(buf, idianStone.getItemId()); // Idian Stone template ID
			writeC(buf, idianStone.getPolishNumber()); // polish statset ID
		}
		else
		{
			writeD(buf, 0); // Idian Stone template ID
			writeC(buf, 0); // polish statset ID
		}
		
		writeC(buf, item.getItemTemplate().getMaxAuthorize() > 0 ? item.getEnchantOrAuthorizeLevel() : 0);
		writeH(buf, 0);
		writePlumeStats(buf); // 64-bytes
		writeB(buf, new byte[36]);
		writeAmplification(buf); // 5-bytes
		writeB(buf, new byte[12]);
		writeSkillBoost(buf); // 8-bytes
		writeD(buf, item.isLunaReskin() ? 1 : 0); // Luna Reskin
		writeC(buf, item.getReductionLevel()); // Level Reduction
		writeRandomBonus(buf); // TODO
		writeD(buf, item.getItemSkinTemplate().getTemplateId());
		writeGrind(buf);
	}
	
	/**
	 * Writes the grind-related data to the buffer.<br>
	 * This method handles socket and color information.<br>
	 * It also records the contamination status of the item.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	private void writeGrind(ByteBuffer buf)
	{
		final Item item = ownerItem;
		writeC(buf, item.getGrindSocket());
		writeC(buf, item.getGrindColor());
		writeQ(buf, 0);
		writeC(buf, item.isContaminated() ? 1 : 0);
		writeC(buf, 0);
		writeC(buf, 0);
		writeD(buf, 0);
	}
	
	/**
	 * Writes the random bonus statistics to the buffer.<br>
	 * It checks if the {@code ownerItem} has any real random bonuses.<br>
	 * If no bonuses exist, it writes a default byte array.<br>
	 * Otherwise, it writes the masks and values for each {@link RealRandomBonusStat}.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	private void writeRandomBonus(ByteBuffer buf)
	{
		// TODO
		final Item item = ownerItem;
		if (item.getRealRndBonus() == null)
		{
			writeB(buf, new byte[40]);
		}
		else
		{
			final int size = (20 - (item.getRealRndBonus().getStats().size() * 2));
			for (RealRandomBonusStat bonus : item.getRealRndBonus().getStats())
			{
				writeH(buf, bonus.getStat().getItemStoneMask());
				// System.out.println("Bonus STAT-NAME: " + bonus.getStat().name() + " STAT-ID: " + bonus.getStat().getItemStoneMask());
			}
			
			writeB(buf, new byte[size]);
			for (RealRandomBonusStat bonus : item.getRealRndBonus().getStats())
			{
				writeH(buf, bonus.getValue());
				// System.out.println("Bonus VAL: " + bonus.getValue());
			}
			
			writeB(buf, new byte[size]);
			// System.out.println("Size: " + size);
		}
	}
	
	/**
	 * Writes the skill boost information to the buffer.<br>
	 * This method saves the {@code enhanceSkillId} and {@code enhanceEnchantLevel} from the owner item.
	 * @param buf The {@link ByteBuffer} used to store the data.
	 */
	private void writeSkillBoost(ByteBuffer buf)
	{
		// TODO
		final Item item = ownerItem;
		writeD(buf, item.getEnhanceSkillId());
		writeD(buf, item.getEnhanceEnchantLevel());
	}
	
	/**
	 * Writes the amplification data to the buffer.<br>
	 * This method checks if the {@code ownerItem} is amplified.<br>
	 * It then writes the amplification skill value.
	 * @param buf The {@link ByteBuffer} where the data will be written.
	 */
	private void writeAmplification(ByteBuffer buf)
	{
		final Item item = ownerItem;
		writeC(buf, item.isAmplified() ? 1 : 0);
		writeD(buf, item.getAmplificationSkill());
	}
	
	/**
	 * Writes the plume statistics to the provided buffer.<br>
	 * This method checks if the item is a plume and writes its specific stats based on its authorize name.<br>
	 * It handles various stat types like Physical Attack, Magic Boost, and others depending on the item template.<br>
	 * If the item is not a plume, it writes a default byte array to the buffer.
	 * @param buf The {@code ByteBuffer} where the statistics will be written.
	 */
	private void writePlumeStats(ByteBuffer buf)
	{
		final Item item = ownerItem;
		final int authorizeName = item.getItemTemplate().getAuthorizeName();
		if (item.getItemTemplate().isPlume())
		{
			writeD(buf, 0); // unk plume stat
			writeD(buf, 0); // value
			writeD(buf, 0); // unk plume stat
			writeD(buf, 0); // value
			writeD(buf, 42);
			writeD(buf, item.getEnchantOrAuthorizeLevel() * 150); // HP Boost for Tempering Solution
			switch (authorizeName)
			{
				case 10051:
				case 10063:
				case 10103:
				case 11003:
					writeD(buf, 30);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 4); // Physical Attack
					writeD(buf, 0); // New Plume Stat 4.7.5.6 (NcSoft will implement it at future)
					writeD(buf, 0); // it's Value
					break;
				case 10052:
				case 10064:
				case 10104:
					writeD(buf, 35);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 20); // Magic Boost
					writeD(buf, 0);
					writeD(buf, 0);
					break;
				case 10056:
				case 10065:
					writeD(buf, 33);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 12); // Physical Critical
					writeD(buf, 0);
					writeD(buf, 0);
					break;
				case 10057:
				case 10066:
					writeD(buf, 36);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 8); // Magical Accuracy
					writeD(buf, 0);
					writeD(buf, 0);
					break;
				case 10105:
				case 10223:
					writeD(buf, 30);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 4); // Physical Attack
					writeD(buf, 32);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 16); // Physical Accuracy
					break;
				case 10106:
				case 10224:
					writeD(buf, 35);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 20); // Magic Boost
					writeD(buf, 34);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 8); // Magic Critical
					break;
				case 11105:
				case 11223:
					writeD(buf, 33);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 12); // Physical Critical
					writeD(buf, 32);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 16); // Physical Accuracy
					break;
				case 11106:
				case 11224:
					writeD(buf, 36);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 8); // Magical Accuracy
					writeD(buf, 34);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 8); // Magic Critical
					break;
				case 10107:
				case 10109:
				case 10225:
				case 10227:
				case 11002:
					writeD(buf, 30);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 4); // Physical Attack
					writeD(buf, 33);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 12); // Physical Critical
					break;
				case 10108:
				case 10110:
				case 10226:
				case 10228:
					writeD(buf, 36);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 8); // Magical Accuracy
					writeD(buf, 35);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 20); // Magic Boost
					break;
				case 11000:
					writeD(buf, 30);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 4); // Physical Attack
					writeD(buf, 33);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 12); // Physical Critical
					// writeD(buf, 0);
					// writeD(buf, 0); // PvE Attack Ratio
					break;
				case 11001:
					writeD(buf, 30);
					writeD(buf, item.getEnchantOrAuthorizeLevel() * 4); // Physical Attack
					writeD(buf, 0);
					writeD(buf, 0);
					
					// writeD(buf, 0);
					// writeD(buf, 0); // PvE Defend Ratio
					break;
				default:
					writeD(buf, 0);
					writeD(buf, 0);
					writeD(buf, 0);
					writeD(buf, 0);
					break;
			}
			
			// Some Padding for future.
			writeD(buf, 0); // unk plume stat
			writeD(buf, 0); // value
			writeD(buf, 0); // unk plume stat
			writeD(buf, 0); // value
			writeD(buf, 0); // unk plume stat
			writeD(buf, 0); // value
		}
		else
		{
			writeB(buf, new byte[64]);
		}
	}
	
	/**
	 * Writes the mana stone information to the buffer.<br>
	 * This method handles items that have stones attached.<br>
	 * It limits the number of stones written to a maximum of 6.<br>
	 * If no stones exist, it skips the required bytes.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	private void writeItemStones(ByteBuffer buf)
	{
		final Item item = ownerItem;
		int count = 0;
		
		if (item.hasManaStones())
		{
			final Set<ManaStone> itemStones = item.getItemStones();
			final ArrayList<ManaStone> basicStones = new ArrayList<>();
			
			for (ManaStone itemStone : itemStones)
			{
				basicStones.add(itemStone);
			}
			
			for (ManaStone basicStone : basicStones)
			{
				if (count == 6)
				{
					break;
				}
				
				writeD(buf, basicStone.getItemId());
				count++;
			}
			
			skip(buf, (6 - count) * 4);
		}
		else
		{
			skip(buf, 24);
		}
	}
	
	/**
	 * Returns the fixed size of this blob entry.<br>
	 * This value is used to determine how many bytes to read from the buffer.
	 * @return The size of the entry as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 248; // 7.5
	}
}
