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

import java.util.Map;

import com.aionemu.gameserver.controllers.RVController;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is used to announce the appearance of a Rift to players.<br>
 * It informs clients about new rift locations and details within the game world.
 * @author Sweetkr
 * @modified -Enomine-
 */
public class SM_RIFT_ANNOUNCE extends AionServerPacket
{
	private final int actionId;
	private RVController rift;
	private Map<Integer, Integer> rifts;
	private int objectId;
	private int gelkmaros, inggison;
	
	/**
	 * This method creates a new {@code SM_RIFT_ANNOUNCE} packet.<br>
	 * It initializes the packet with a map of rift data.
	 * @param rifts A {@code Map} containing the rift information.
	 */
	public SM_RIFT_ANNOUNCE(Map<Integer, Integer> rifts)
	{
		actionId = 0;
		this.rifts = rifts;
	}
	
	/**
	 * Creates a new {@code SM_RIFT_ANNOUNCE} packet.<br>
	 * This constructor sets the specific rift flags based on boolean inputs.<br>
	 * It initializes the {@code actionId} to {@code 1}.
	 * @param gelkmaros The status of the Gelkmaros flag.
	 * @param inggison The status of the Inggison flag.
	 */
	public SM_RIFT_ANNOUNCE(boolean gelkmaros, boolean inggison)
	{
		this.gelkmaros = gelkmaros ? 1 : 0;
		this.inggison = inggison ? 1 : 0;
		actionId = 1;
	}
	
	/**
	 * Creates a new {@code SM_RIFT_ANNOUNCE} packet for a specific rift.<br>
	 * It sets the internal {@code actionId} based on whether the rift is a master.
	 * @param rift The {@link RVController} associated with this rift.
	 * @param isMaster A boolean indicating if this is the master rift; use {@code true} for master or {@code false} otherwise.
	 */
	public SM_RIFT_ANNOUNCE(RVController rift, boolean isMaster)
	{
		this.rift = rift;
		actionId = isMaster ? 2 : 3;
	}
	
	/**
	 * Creates a new {@code SM_RIFT_ANNOUNCE} packet for a specific object.<br>
	 * This method sets the internal {@code actionId} to {@code 5}.
	 * @param objectId The unique identifier of the object.
	 */
	public SM_RIFT_ANNOUNCE(int objectId)
	{
		this.objectId = objectId;
		actionId = 5;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		switch (actionId)
		{
			case 0: // announce
				writeH(0x57); // 4.7 // old -->writeH(0x19); // 0x19 // 4.9 = 57
				writeC(actionId);
				for (int value : rifts.values())
				{
					writeD(value);
				}
				break;
			case 1:
				writeH(0x09); // 0x09
				writeC(actionId);
				writeD(gelkmaros);
				writeD(inggison);
				break;
			case 2:
				writeH(0x39); // 0x39
				writeC(actionId);
				writeD(rift.getOwner().getObjectId());
				writeD(rift.getMaxEntries());
				writeD(rift.getRemainTime());
				writeD(rift.getMinLevel());
				writeD(rift.getMaxLevel());
				writeF(rift.getOwner().getX());
				writeF(rift.getOwner().getY());
				writeF(rift.getOwner().getZ());
				writeC(rift.isVortex() ? 1 : 0); // red | blue
				writeC(rift.isMaster() ? 1 : 0); // display | hide
				writeD(rift.getOwner().getWorldId());
				break;
			case 3:
				writeH(0x15);
				writeC(actionId);
				writeD(rift.getOwner().getObjectId());
				writeD(rift.getUsedEntries());
				writeD(rift.getRemainTime());
				writeC(rift.isVortex() ? 1 : 0); // red | blue
				writeC(rift.isMaster() ? 1 : 0); // display | hide
				break;
			case 4:
				writeH(0x07);
				writeC(actionId);
				writeD(objectId);
				writeC(rift.isVortex() ? 1 : 0); // red | blue
				writeC(rift.isMaster() ? 1 : 0); // display | hide
				break;
			case 5:
				writeH(0x05);
				writeC(actionId);
				writeD(0x00); // 0x00
				break;
			
		}
	}
}
