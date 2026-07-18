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
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.PacketWriteHelper;

/**
 * This class represents the packet data for item information blobs.<br>
 * It contains detailed entries used to describe specific items within a network packet.
 * @author -Nemesiss-
 * @modified Rolandas - complete rewrite, no trees (blob size must be known in advance!), just sequences
 */
public class ItemInfoBlob extends PacketWriteHelper
{
	protected final Player player;
	protected final Item item;
	private final List<ItemBlobEntry> itemBlobEntries = new ArrayList<>();
	
	/**
	 * Creates a new {@code ItemInfoBlob} for a specific player and item.<br>
	 * This object stores the detailed information needed to display an item's stats.
	 * @param player The {@link Player} who is viewing the item.
	 * @param item The {@link Item} that needs to be processed.
	 */
	public ItemInfoBlob(Player player, Item item)
	{
		this.player = player;
		this.item = item;
	}
	
	/**
	 * Writes the packet data into a {@code ByteBuffer}.<br>
	 * It first writes the total size of the blob.<br>
	 * Then it iterates through all entries and writes each one.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeMe(ByteBuffer buf)
	{
		writeH(buf, size());
		for (ItemBlobEntry ent : itemBlobEntries)
		{
			ent.writeMe(buf);
		}
	}
	
	/**
	 * Adds a new entry to the list of blob entries.<br>
	 * This method creates an {@code ItemBlobEntry} based on the provided type.<br>
	 * It automatically sets the owner using the current player and item.
	 * @param type The {@code ItemBlobType} used to create the new entry.
	 */
	public void addBlobEntry(ItemBlobType type)
	{
		final ItemBlobEntry ent = type.newBlobEntry();
		ent.setOwner(player, item, null);
		itemBlobEntries.add(ent);
	}
	
	/**
	 * Adds a new stat bonus entry to the blob list.<br>
	 * This method creates an {@link ItemBlobEntry} of type {@code STAT_BONUSES}.<br>
	 * It links the provided {@code modifier} to the current player and item.
	 * @param modifier The {@code IStatFunction} that defines the bonus to be added.
	 */
	public void addBonusBlobEntry(IStatFunction modifier)
	{
		final ItemBlobEntry ent = ItemBlobType.STAT_BONUSES.newBlobEntry();
		ent.setOwner(player, item, modifier);
		itemBlobEntries.add(ent);
	}
	
	/**
	 * Creates a new {@link ItemBlobEntry} based on the specified type.<br>
	 * This method initializes the entry with the provided owner data.<br>
	 * It will throw an exception if the type is {@code ItemBlobType.STAT_BONUSES}.
	 * @param type The category of the blob entry to create.
	 * @param player The {@link Player} who owns the item.
	 * @param item The {@link Item} being processed.
	 * @return A new instance of {@link ItemBlobEntry}.
	 */
	public static ItemBlobEntry newBlobEntry(ItemBlobType type, Player player, Item item)
	{
		if (type == ItemBlobType.STAT_BONUSES)
		{
			throw new UnsupportedOperationException();
		}
		
		final ItemBlobEntry ent = type.newBlobEntry();
		ent.setOwner(player, item, null);
		return ent;
	}
	
	/**
	 * Creates and populates a full {@link ItemInfoBlob} for a specific player and item.<br>
	 * This method determines which data entries to include based on the item properties.<br>
	 * It handles logic for weapons, armor types, sockets, and special modifiers.
	 * @param player The {@link Player} who is viewing or interacting with the item.
	 * @param item The {@link Item} object to be processed into a blob.
	 * @return A fully populated {@link ItemInfoBlob} containing all relevant data entries.
	 */
	public static ItemInfoBlob getFullBlob(Player player, Item item)
	{
		final ItemInfoBlob blob = new ItemInfoBlob(player, item);
		
		final ItemTemplate itemTemplate = item.getItemTemplate();
		
		if ((itemTemplate.getWeaponType() != null) && itemTemplate.isTwoHandWeapon())
		{
			blob.addBlobEntry(ItemBlobType.COMPOSITE_ITEM);
		}
		
		if (item.getEquipmentType() != EquipType.NONE)
		{
			// EQUIPPED SLOT
			blob.addBlobEntry(ItemBlobType.EQUIPPED_SLOT);
			
			// SLOT INFO
			if ((itemTemplate.getArmorType() != null) && (itemTemplate.getArmorType() != ArmorType.NO_ARMOR))
			{
				switch (itemTemplate.getArmorType())
				{
					case WING:
						blob.addBlobEntry(ItemBlobType.SLOTS_WING);
						break;
					case SHIELD:
						blob.addBlobEntry(ItemBlobType.SLOTS_SHIELD);
						break;
					default:
						blob.addBlobEntry(ItemBlobType.SLOTS_ARMOR);
						break;
				}
			}
			else if (itemTemplate.isWeapon())
			{
				blob.addBlobEntry(ItemBlobType.SLOTS_WEAPON);
			}
			else if (item.getEquipmentType() == EquipType.ARMOR)
			{
				blob.addBlobEntry(ItemBlobType.SLOTS_ACCESSORY); // power shards, helmets, earrings, rings, belts
			}
			
			// MANA STONES
			blob.addBlobEntry(ItemBlobType.MANA_SOCKETS);
			
			if (item.getConditioningInfo() != null)
			{
				blob.addBlobEntry(ItemBlobType.CONDITIONING_INFO);
			}
			
			// All items with only General
			if (blob.getBlobEntries().size() > 0)
			{
				blob.addBlobEntry(ItemBlobType.PREMIUM_OPTION);
				if (itemTemplate.isCanPolish())
				{
					blob.addBlobEntry(ItemBlobType.POLISH_INFO);
				}
			}
			
			blob.addBlobEntry(ItemBlobType.PACK_INFO);
			
			if (itemTemplate.isPlume())
			{
				blob.addBlobEntry(ItemBlobType.PLUME_INFO);
			}
			
			if (itemTemplate.isBracelet())
			{
				blob.addBlobEntry(ItemBlobType.BRACELET_INFO);
			}
			
			if (itemTemplate.getCategory() == ItemCategory.GLYPH)
			{
				blob.addBlobEntry(ItemBlobType.GLYPH_INFO);
			}
			
			final List<StatFunction> allModifiers = itemTemplate.getModifiers();
			if (allModifiers != null)
			{
				for (IStatFunction modifier : allModifiers)
				{
					if (modifier.isBonus() && !modifier.hasConditions())
					{
						blob.addBonusBlobEntry(modifier);
					}
				}
			}
		}
		else if (itemTemplate.getTemplateId() == 141000001)
		{
			blob.addBlobEntry(ItemBlobType.STIGMA_SHARD);
		}
		
		// GENERAL INFO
		blob.addBlobEntry(ItemBlobType.GENERAL_INFO);
		
		return blob;
	}
	
	/**
	 * Retrieves the list of blob entries for this packet.<br>
	 * These entries contain detailed information about the {@link Item}.
	 * @return A {@code List} containing all {@code ItemBlobEntry} objects.
	 */
	public List<ItemBlobEntry> getBlobEntries()
	{
		return itemBlobEntries;
	}
	
	/**
	 * Calculates the total size of all entries in this packet.<br>
	 * It sums the size of each {@link ItemBlobEntry} plus one byte for the ID.
	 * @return The total size in bytes required to store the blob data.
	 */
	public int size()
	{
		int totalSize = 0;
		for (ItemBlobEntry ent : itemBlobEntries)
		{
			totalSize += ent.getSize() + 1; // 1 C for blob id
		}
		
		return totalSize;
	}
	
	public enum ItemBlobType
	{
		GENERAL_INFO(0x00)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new GeneralInfoBlobEntry();
			}
		},
		SLOTS_WEAPON(0x01)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new WeaponInfoBlobEntry();
			}
		},
		SLOTS_ARMOR(0x02)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new ArmorInfoBlobEntry();
			}
		},
		SLOTS_SHIELD(0x03)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new ShieldInfoBlobEntry();
			}
		},
		SLOTS_ACCESSORY(0x04)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new AccessoryInfoBlobEntry();
			}
		},
		SLOTS_ARROW(0x05)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new ArrowInfoBlobEntry();
			}
		},
		EQUIPPED_SLOT(0x06)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new EquippedSlotBlobEntry();
			}
		},
		// Removed from 3.5
		STIGMA_INFO(0x07)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new StigmaInfoBlobEntry();
			}
		},
		STIGMA_SHARD(0x08)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new StigmaShardInfoBlobEntry();
			}
		},
		// missing(0x09), //15? [Not handled before]
		PREMIUM_OPTION(0x10)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new PremiumOptionInfoBlobEntry();
			}
		},
		POLISH_INFO(0x11)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new PolishInfoBlobEntry();
			}
		},
		PACK_INFO(0x12)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new PackInfoBlobEntry();
			}
		},
		PLUME_INFO(0x13)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new PlumeInfoBlobEntry();
			}
		},
		BRACELET_INFO(0x14)
		{
			// 5.3
			
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new BraceletInfoBlobEntry();
			}
		},
		GLYPH_INFO(0x15)
		{
			// 7.7
			
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new GlyphInfoBlobEntry();
			}
		},
		STAT_BONUSES(0x0A)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new BonusInfoBlobEntry();
			}
		},
		// [Not handled before] retail send it xx times (smth dynamically changed)
		MANA_SOCKETS(0x0B)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new ManaStoneInfoBlobEntry();
			}
		},
		// 0x0C - not used?
		SLOTS_WING(0x0D)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new WingInfoBlobEntry();
			}
		},
		COMPOSITE_ITEM(0x0E)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new CompositeItemBlobEntry();
			}
		},
		CONDITIONING_INFO(0x0F)
		{
			@Override
			ItemBlobEntry newBlobEntry()
			{
				return new ConditioningInfoBlobEntry();
			}
		};
		
		private final int entryId;
		
		private ItemBlobType(int entryId)
		{
			this.entryId = entryId;
		}
		
		public int getEntryId()
		{
			return entryId;
		}
		
		abstract ItemBlobEntry newBlobEntry();
	}
}
