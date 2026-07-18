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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.network.PacketWriteHelper;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * Represents an individual entry within an {@link ItemInfoBlob} containing detailed item information.<br>
 * This class serves as a base for various blob types that the client processes in a sequence.<br>
 * It handles the serialization of specific item data into the network packet.
 * @author -Nemesiss-
 * @modified Rolandas
 */
public abstract class ItemBlobEntry extends PacketWriteHelper
{
	private final ItemBlobType type;
	Player owner;
	Item ownerItem;
	IStatFunction modifier;
	
	/**
	 * Creates a new entry for an {@link ItemBlobType}.<br>
	 * This constructor initializes the blob type for the entry.
	 * @param type The specific {@code ItemBlobType} to assign to this entry.
	 */
	ItemBlobEntry(ItemBlobType type)
	{
		this.type = type;
	}
	
	/**
	 * Sets the owner details for this entry.<br>
	 * This method links a {@link Player}, an {@code Item}, and an {@code IStatFunction}.<br>
	 * It updates the internal fields of the current object.
	 * @param owner The {@link Player} who owns the item.
	 * @param item The {@link Item} being referenced.
	 * @param modifier The {@link IStatFunction} applied to this entry.
	 */
	void setOwner(Player owner, Item item, IStatFunction modifier)
	{
		this.owner = owner;
		ownerItem = item;
		this.modifier = modifier;
	}
	
	/**
	 * Writes the current blob entry data into a {@code ByteBuffer}.<br>
	 * This method handles the serialization of the entry ID and specific blob content.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	protected void writeMe(ByteBuffer buf)
	{
		writeC(buf, type.getEntryId());
		writeThisBlob(buf);
	}
	
	public abstract void writeThisBlob(ByteBuffer buf);
	
	public abstract int getSize();
}
