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

import java.util.Calendar;
import java.util.List;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the transmission of Luna system information to the client.<br>
 * It provides necessary data regarding the current state of the Luna system.
 */
public class SM_LUNA_SYSTEM_INFO extends AionServerPacket
{
	private final int actionId;
	@SuppressWarnings("unused")
	private long points;
	@SuppressWarnings("unused")
	private int keys;
	private int costId;
	@SuppressWarnings("unused")
	private int entryCount;
	private int tableId;
	@SuppressWarnings("unused")
	private List<Integer> idList;
	private List<Integer> randomDailyCraft;
	@SuppressWarnings("unused")
	private List<Integer> lists;
	private final int startTime = ((int) (Calendar.getInstance().getTimeInMillis() / 1000));
	private final int endTime = (startTime + 86400);
	
	/**
	 * Creates a new instance of {@link SM_LUNA_SYSTEM_INFO}.<br>
	 * This constructor initializes the packet with a specific action identifier.
	 * @param actionId The unique ID for the system action.
	 */
	public SM_LUNA_SYSTEM_INFO(int actionId)
	{
		this.actionId = actionId;
	}
	
	/**
	 * Creates a new instance of {@link SM_LUNA_SYSTEM_INFO}.<br>
	 * This constructor initializes the packet with specific system data.
	 * @param actionId The unique identifier for the action.
	 * @param points The number of points associated with this info.
	 */
	public SM_LUNA_SYSTEM_INFO(int actionId, long points)
	{
		this.actionId = actionId;
		this.points = points;
	}
	
	/**
	 * Creates a new instance of {@link SM_LUNA_SYSTEM_INFO}.<br>
	 * This constructor initializes the packet with specific action and key data.
	 * @param actionId The unique identifier for the system action.
	 * @param keys The number of keys associated with this information.
	 */
	public SM_LUNA_SYSTEM_INFO(int actionId, int keys)
	{
		this.actionId = actionId;
		this.keys = keys;
	}
	
	/**
	 * Creates a new {@code SM_LUNA_SYSTEM_INFO} packet.<br>
	 * This constructor initializes the action and table identifiers.<br>
	 * It also assigns the provided list to specific internal fields based on the {@code tableId}.
	 * @param actionId The unique identifier for the action.
	 * @param tableId The identifier used to determine which list field to populate.
	 * @param lists The list of integers to be assigned to the packet.
	 */
	public SM_LUNA_SYSTEM_INFO(int actionId, int tableId, List<Integer> lists)
	{
		this.actionId = actionId;
		this.tableId = tableId;
		switch (tableId)
		{
			case 0:
				idList = lists;
				break;
			case 1:
				randomDailyCraft = lists;
				break;
		}
	}
	
	/**
	 * Creates a new instance of {@code SM_LUNA_SYSTEM_INFO}.<br>
	 * This constructor initializes the packet with specific system identifiers.
	 * @param actionId The unique identifier for the action.
	 * @param tableId The identifier for the data table.
	 * @param costId The identifier for the associated cost.
	 */
	public SM_LUNA_SYSTEM_INFO(int actionId, int tableId, int costId)
	{
		this.actionId = actionId;
		this.tableId = tableId;
		this.costId = costId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(actionId); // actionid
		switch (actionId)
		{
			case 0:// luna point handler id
				writeQ(con.getAccount().getLuna());
				break;
			case 1:// taki advanture update NOTE also send after free box craft (1,1,1,1)
				writeH(tableId); // size? subaction
				writeD(costId);
				if (costId == 79)
				{// First run on Dice game is 78 after that its 79 + last D packet get's +1 for every try
					writeD(con.getActivePlayer().getLunaDiceGameTry());
				}
				else
				{
					writeD(1);
				}
				break;
			case 2:
				writeC(tableId); // tabId
				switch (tableId)
				{
					case 0: // NormalCraft (Disabled from NC / GF) writeD(startTime); // Start time writeD(0); writeD(endTime); // End time writeD(0); writeH(idList.size()); // size for (int i = 0; i < idList.size(); i++) { writeD(idList.get(i)); // luna recipe id }
						
						writeD(startTime); // Start time
						writeD(0);
						writeD(-1);
						writeD(2147483647); // End Time
						writeH(0); // size
						break;
					case 1: // DailyCraft
						writeD(startTime);
						writeD(0); // test
						writeD(endTime);
						writeD(0);
						writeH(randomDailyCraft.size()); // size
						for (Integer daily : randomDailyCraft)
						{
							writeD(daily);
							System.out.println("Daily: " + daily);
						}
						break;
				}
				break;
			case 4:// munirunerk's keys
				writeD(con.getActivePlayer().getMuniKeys());
				break;
			case 5:// luna consume point spent
				writeD(con.getActivePlayer().getLunaConsumePoint());
				break;
			case 6:// update taki's mission?
				break;
			case 7:
				writeC(0);
				writeH(100);
				break;
			case 8:// Updated for 5.8 on 16.05.2018
				writeH(tableId <= 5 ? tableId : (tableId - 1));
				writeD(10 + tableId);
				writeC((tableId + 10) == 16 ? 1 : 0);
				break;
			case 9:
				writeH(-1);
				writeC(0);
		}
	}
}
