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

import com.aionemu.gameserver.model.gameobjects.HouseDecoration;
import com.aionemu.gameserver.model.gameobjects.SummonedHouseNpc;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.team.legion.LegionEmblem;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.templates.housing.BuildingType;
import com.aionemu.gameserver.model.templates.housing.PartType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.LegionService;

/**
 * This packet updates the client with information regarding house changes.<br>
 * It synchronizes data for {@link House} objects, including decorations and NPCs.
 * @author Rolandas
 */
public class SM_HOUSE_UPDATE extends AionServerPacket
{
	private final House house;
	
	/**
	 * Creates a new {@code SM_HOUSE_UPDATE} packet.<br>
	 * This packet is used to send house data to the client.<br>
	 * It initializes the internal state with the provided {@link House} object.
	 * @param house The {@code House} object to be included in the update.
	 */
	public SM_HOUSE_UPDATE(House house)
	{
		this.house = house;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(1); // unk
		writeH(0);
		writeH(1); // unk
		
		writeD(0);
		writeD(house.getAddress().getId());
		final int playerObjectId = house.getOwnerId();
		writeD(playerObjectId);
		writeD(house.getBuilding().getType().getId());
		writeC(house.getNoticeState().getPacketValue()); // Hide or Show Owner
		writeD(house.getBuilding().getId());
		writeC(house.getHouseOwnerInfoFlags()); // owner info
		writeC(house.getDoorState().getPacketValue());
		
		int dataSize = 52;
		if (house.getButler() != null)
		{
			final SummonedHouseNpc butler = (SummonedHouseNpc) house.getButler();
			if ((butler.getMasterName() != null) && !butler.getMasterName().isEmpty())
			{
				dataSize -= (butler.getMasterName().length() + 1) * 2;
				writeS(butler.getMasterName()); // owner name
			}
		}
		
		// TODO: various messages, some crypted tags
		for (int i = 0; i < dataSize; i++)
		{
			writeC(0);
		}
		
		final LegionMember member = LegionService.getInstance().getLegionMember(playerObjectId);
		writeD(member == null ? 0 : member.getLegion().getLegionId());
		
		// show/hide owner name
		writeC(house.getNoticeState().getPacketValue());
		
		final byte[] signNotice = house.getSignNotice();
		for (int i = 0; i < signNotice.length; i++)
		{
			writeC(signNotice[i]);
		}
		
		for (int i = signNotice.length; i < House.NOTICE_LENGTH; i++)
		{
			writeC(0);
		}
		
		writePartData(house, PartType.ROOF, 0, true);
		writePartData(house, PartType.OUTWALL, 0, true);
		writePartData(house, PartType.FRAME, 0, true);
		writePartData(house, PartType.DOOR, 0, true);
		writePartData(house, PartType.GARDEN, 0, true);
		writePartData(house, PartType.FENCE, 0, true);
		
		for (int floor = 0; floor < 6; floor++)
		{
			writePartData(house, PartType.INWALL_ANY, floor, floor > 0);
		}
		
		for (int floor = 0; floor < 6; floor++)
		{
			writePartData(house, PartType.INFLOOR_ANY, floor, floor > 0);
		}
		
		writePartData(house, PartType.ADDON, 0, true);
		writeD(0);
		writeD(0);
		writeC(0);
		
		// Emblem and color
		if ((member == null) || (member.getLegion().getLegionEmblem() == null))
		{
			writeC(0);
			writeC(0);
			writeD(0);
		}
		else
		{
			final LegionEmblem emblem = member.getLegion().getLegionEmblem();
			writeC(emblem.getEmblemId());
			writeC(emblem.getEmblemType().getValue());
			writeC(emblem.isDefaultEmblem() ? 0x0 : 0xFF); // Alpha Channel
			writeC(emblem.getColor_r());
			writeC(emblem.getColor_g());
			writeC(emblem.getColor_b());
		}
	}
	
	/**
	 * Writes the data for a specific part of a {@link House} to the packet.<br>
	 * It handles different building types and floors.<br>
	 * The method determines whether to include personal information based on the {@code skipPersonal} flag.
	 * @param house The {@link House} object containing the building data.
	 * @param partType The specific {@link PartType} of the house component to write.
	 * @param floor The floor number where the part is located.
	 * @param skipPersonal A boolean flag that determines if personal information should be omitted.
	 */
	private void writePartData(House house, PartType partType, int floor, boolean skipPersonal)
	{
		final boolean isPersonal = house.getBuilding().getType() == BuildingType.PERSONAL_INS;
		final HouseDecoration deco = house.getRenderPart(partType, floor);
		if (skipPersonal && isPersonal)
		{
			writeD(0);
		}
		else
		{
			writeD(deco != null ? deco.getTemplate().getId() : 0);
		}
	}
}
