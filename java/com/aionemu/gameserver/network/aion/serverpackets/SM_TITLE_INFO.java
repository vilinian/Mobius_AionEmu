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
import com.aionemu.gameserver.model.gameobjects.player.title.Title;
import com.aionemu.gameserver.model.gameobjects.player.title.TitleList;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet sends title information to the client.<br>
 * It contains data regarding a player's {@link Title} or their {@link TitleList}.
 * @author cura, xTz
 * @modified -Enomine-
 */
public class SM_TITLE_INFO extends AionServerPacket
{
	private TitleList titleList;
	private final int action; // 0: list, 1: self set, 3: broad set
	private int titleId;
	private int bonusTitleId;
	private int playerObjId;
	
	/**
	 * Creates a new {@code SM_TITLE_INFO} packet for a specific player.<br>
	 * This constructor sets the action to {@code 0}.<br>
	 * It retrieves the title list from the provided {@link Player} object.
	 * @param player The {@code Player} whose titles will be included in the packet.
	 */
	public SM_TITLE_INFO(Player player)
	{
		action = 0;
		titleList = player.getTitleList();
	}
	
	/**
	 * Creates a new {@link SM_TITLE_INFO} packet for setting a self title.<br>
	 * This constructor sets the {@code action} value to {@code 1}.
	 * @param titleId The unique identifier of the title to be set.
	 */
	public SM_TITLE_INFO(int titleId)
	{
		action = 1;
		this.titleId = titleId;
	}
	
	/**
	 * Creates a new {@code SM_TITLE_INFO} packet for a specific player.<br>
	 * This constructor sets the action to {@code 3} for a broad set operation.<br>
	 * It also stores the unique object ID of the {@link Player}.
	 * @param player The {@link Player} instance associated with this packet.
	 * @param titleId The unique identifier for the title being processed.
	 */
	public SM_TITLE_INFO(Player player, int titleId)
	{
		action = 3;
		playerObjId = player.getObjectId();
		this.titleId = titleId;
	}
	
	/**
	 * Initializes a new {@link SM_TITLE_INFO} packet with a specific action.<br>
	 * This constructor sets the {@code action} to {@code 4}.<br>
	 * The {@code titleId} is determined by the provided boolean value.
	 * @param flag Determines if the {@code titleId} should be set to {@code 1} or {@code 0}.
	 */
	public SM_TITLE_INFO(boolean flag)
	{
		action = 4;
		titleId = flag ? 1 : 0;
	}
	
	/**
	 * Creates a new {@link SM_TITLE_INFO} packet for a specific player.<br>
	 * This constructor sets the action to {@code 5}.<br>
	 * It assigns the {@code titleId} based on the provided boolean flag.
	 * @param player The {@link Player} object associated with this packet.
	 * @param flag A boolean used to determine if the {@code titleId} is {@code 1} or {@code 0}.
	 */
	public SM_TITLE_INFO(Player player, boolean flag)
	{
		action = 5;
		playerObjId = player.getObjectId();
		titleId = flag ? 1 : 0;
	}
	
	/**
	 * Creates a new {@link SM_TITLE_INFO} packet with specific action and bonus details.<br>
	 * This constructor is used to initialize the packet data for title updates.
	 * @param action The type of action to perform, such as 0 for list or 1 for self set.
	 * @param bonusTitleId The unique identifier for the bonus title.
	 */
	public SM_TITLE_INFO(int action, int bonusTitleId)
	{
		this.action = action;
		this.bonusTitleId = bonusTitleId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(action);
		switch (action)
		{
			case 0:
				writeC(0x00);
				writeC(0x01); // 4.9
				writeH(titleList.size());
				for (Title title : titleList.getTitles())
				{
					writeD(title.getId());
					writeD(title.getRemainingTime());
				}
				break;
			case 1: // self set
				writeH(titleId);
				break;
			case 2: // unk 4.7
				writeD(bonusTitleId);
				break;
			case 3: // broad set
				writeD(playerObjId);
				writeH(titleId);
				break;
			case 4: // Mentor flag self
				writeH(titleId);
				break;
			case 5: // broad set mentor fleg
				writeD(playerObjId);
				writeH(titleId);
				break;
			case 6:// Title wich will take BonusStats from
				writeH(bonusTitleId);
				break;
			case 7:// Unk 4.7
				writeD(bonusTitleId);
				break;
		}
	}
}
