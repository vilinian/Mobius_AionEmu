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

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.player.MinionCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This class handles the server-to-client packets related to minions.<br>
 * It manages data synchronization for {@link MinionCommonData} and other minion states.
 * @author Falke_34, FrozenKiller
 */
public class SM_MINIONS extends AionServerPacket
{
	int action;
	int subaction;
	int minionObjId;
	int minionId;
	int masterObjId;
	int energy;
	boolean bool;
	private boolean isActing;
	private int lootNpcId;
	String name;
	// private Timestamp expiredTimeMillis;
	private MinionCommonData commonData;
	private Collection<MinionCommonData> minions;
	private int dopeAction;
	private int dopeSlot;
	private int itemObjectId;
	private int code;
	@SuppressWarnings("unused")
	private Player player;
	// private long timeLeft;
	// private int slot;
	// private int slot2;
	
	/**
	 * Creates a new {@link SM_MINIONS} packet.<br>
	 * This constructor initializes the packet with a specific action type.
	 * @param action The integer value representing the action to perform.
	 */
	public SM_MINIONS(int action)
	{
		this.action = action;
	}
	
	/**
	 * Creates a new {@link SM_MINIONS} packet with specific minion states.<br>
	 * This constructor sets the primary action, current energy level, and automation status.
	 * @param action The type of action to perform.
	 * @param energy The current energy value of the minion.
	 * @param auto Set to {@code true} if the minion is on autopilot, or {@code false} otherwise.
	 */
	public SM_MINIONS(int action, int energy, boolean auto)
	{
		this.action = action;
		this.energy = energy;
		bool = auto;
	}
	
	/**
	 * Creates a new {@link SM_MINIONS} packet.<br>
	 * This constructor sets the basic action and object identifier.
	 * @param action The type of action to perform.
	 * @param minionObjId The unique identifier for the minion object.
	 */
	public SM_MINIONS(int action, int minionObjId)
	{
		this.action = action;
		this.minionObjId = minionObjId;
	}
	
	/**
	 * Creates a new {@link SM_MINIONS} packet.<br>
	 * This constructor initializes the packet with specific minion data.
	 * @param action The type of action to perform.
	 * @param commonData The shared data for the minion.
	 */
	public SM_MINIONS(int action, MinionCommonData commonData)
	{
		this.action = action;
		this.commonData = commonData;
	}
	
	/**
	 * Creates a new {@link SM_MINIONS} packet with specific data.<br>
	 * This constructor initializes the action, common data, and status code.
	 * @param action The primary action type for the minion.
	 * @param commonData The shared data object for the minion.
	 * @param code The specific status or result code.
	 */
	public SM_MINIONS(int action, MinionCommonData commonData, int code)
	{
		this.action = action;
		this.commonData = commonData;
		this.code = code;
	}
	
	/**
	 * This constructor initializes a new {@code SM_MINIONS} packet.<br>
	 * It sets the basic identification data for the minion and its owner.
	 * @param action The primary action type to be performed.
	 * @param name The display name of the minion.
	 * @param minionObjId The unique object identifier for the minion.
	 * @param minionId The specific ID assigned to the minion.
	 * @param masterObjId The unique object identifier for the minion's master.
	 */
	public SM_MINIONS(int action, String name, int minionObjId, int minionId, int masterObjId)
	{
		this.action = action;
		this.name = name;
		this.minionObjId = minionObjId;
		this.minionId = minionId;
		this.masterObjId = masterObjId;
	}
	
	/**
	 * Creates a new {@link SM_MINIONS} packet.<br>
	 * This constructor initializes the packet with a specific action and a list of minion data.
	 * @param action The type of action to perform.
	 * @param minions A {@code Collection} of {@link MinionCommonData} objects.
	 */
	public SM_MINIONS(int action, Collection<MinionCommonData> minions)
	{
		this.action = action;
		this.minions = minions;
	}
	
	/**
	 * Creates a new {@code SM_MINIONS} packet for a specific player.<br>
	 * This constructor initializes the packet with an action type and a {@link Player}.
	 * @param action The integer code representing the minion action.
	 * @param player The {@link Player} object associated with this packet.
	 */
	public SM_MINIONS(int action, Player player)
	{
		this.action = action;
		this.player = player;
	}
	
	/**
	 * Creates a new {@link SM_MINIONS} packet for looting actions.<br>
	 * This constructor sets the action to {@code 9}.<br>
	 * It also sets the subaction to {@code 1}.
	 * @param isLooting A boolean indicating if the minion is currently looting.
	 */
	public SM_MINIONS(boolean isLooting)
	{
		action = 9;
		isActing = isLooting;
		subaction = 1;
	}
	
	/**
	 * Creates a new {@link SM_MINIONS} packet for looting actions.<br>
	 * This constructor sets the action to {@code 9} and subaction to {@code 1}.
	 * @param isLooting A boolean indicating if the minion is currently looting.
	 * @param npcId The unique identifier of the NPC being looted.
	 */
	public SM_MINIONS(boolean isLooting, int npcId)
	{
		this(isLooting);
		action = 9;
		lootNpcId = npcId;
		subaction = 1;
	}
	
	/**
	 * This constructor initializes a minion packet with specific dope actions.<br>
	 * It sets the {@code action} to {@code 9} and {@code subaction} to {@code 1}.<br>
	 * The internal state is updated based on the provided item and slot data.
	 * @param dopeAction The primary action code for the minion.
	 * @param itemId The unique identifier for the item object.
	 * @param slot The specific inventory or equipment slot index.
	 */
	public SM_MINIONS(int dopeAction, int itemId, int slot)
	{
		this(dopeAction, true);
		itemObjectId = itemId;
		dopeSlot = slot;
		action = 9;
		subaction = 1;
	}
	
	/**
	 * This constructor initializes a {@link SM_MINIONS} packet with specific actions.<br>
	 * It sets the primary action and assigns values for dope actions and item slots.
	 * @param action The main action type to perform.
	 * @param dopeAction The secondary action identifier.
	 * @param itemId The unique identifier for the item object.
	 * @param slot The specific inventory or equipment slot index.
	 */
	public SM_MINIONS(int action, int dopeAction, int itemId, int slot)
	{
		this(dopeAction, true);
		this.action = action;
		itemObjectId = itemId;
		dopeSlot = slot;
		subaction = 0;
	}
	
	/**
	 * Creates a new {@code SM_MINIONS} packet for a specific player.<br>
	 * This constructor initializes the packet with the provided {@code Player} and {@code action}.
	 * @param player The {@link Player} who is performing the minion action.
	 * @param action The type of action being performed on the minions.
	 */
	public SM_MINIONS(Player player, int action)
	{
		this.action = action;
		this.player = player;
	}
	
	/**
	 * Creates a new {@link SM_MINIONS} packet for specific minion actions.<br>
	 * This constructor sets the action type to {@code 14}.<br>
	 * It initializes the internal state based on the provided dope action and buffing status.
	 * @param dopeAction The specific action identifier for the minion.
	 * @param isBuffing A boolean indicating if the minion is currently applying a buff.
	 */
	public SM_MINIONS(int dopeAction, boolean isBuffing)
	{
		action = 14;
		this.dopeAction = dopeAction;
		isActing = isBuffing;
		subaction = 0;
	}
	
	// public SM_MINIONS(Timestamp expire, boolean isAuto)
	// {
	// action = 9;
	// expiredTimeMillis = expire;
	// bool = isAuto;
	// }
	
	// public SM_MINIONS(int action, long timeLeft)
	// {
	// this.action = action;
	// this.timeLeft = timeLeft;
	// }
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(action);
		switch (action)
		{
			case 0:
			{
				writeH(10);
				writeD(2500000);
				writeD(0);
				writeD(2);
				writeD(5000);
				writeD(0);
				writeD(10000);
				writeD(0);
				writeD(5000);
				writeD(0);
				writeD(5000);
				writeD(0);
				writeD(10000);
				writeD(0);
				writeH(4);
				break;
			}
			case 1:
			{
				writeC(0);
				writeH(minions.size());
				for (MinionCommonData mcd : minions)
				{
					writeD(mcd.getObjectId());
					writeD(0);
					writeD(0);
					writeD(mcd.getMasterObjectId());
					writeD(mcd.getMinionId());
					writeS(mcd.getName());
					writeD(mcd.getBirthday());
					writeQ(mcd.getGrowthPoints());
					writeC(mcd.isLocked() ? 0 : 1);
					final int[] scrollBag = mcd.getDopingBag().getScrollsUsed();
					if (scrollBag.length == 0)
					{
						writeB(new byte[28]);
					}
					else
					{
						writeD((scrollBag.length > 1) ? scrollBag[0] : 0);
						writeD((scrollBag.length > 2) ? scrollBag[1] : 0);
						writeD((scrollBag.length > 3) ? scrollBag[2] : 0);
						writeD((scrollBag.length > 4) ? scrollBag[3] : 0);
						writeD((scrollBag.length > 5) ? scrollBag[4] : 0);
						writeD((scrollBag.length > 6) ? scrollBag[5] : 0);
					}
					
					writeC(0);
				}
				break;
			}
			case 2:
			{
				writeD(code);
				writeD(0);
				writeH(0);
				writeD(commonData.getObjectId());
				writeD(0);
				writeD(0);
				writeD(commonData.getMasterObjectId());
				writeD(commonData.getMinionId());
				writeS(commonData.getName());
				writeD(commonData.getBirthday());
				writeQ(commonData.getGrowthPoints());
				writeC(commonData.isLocked() ? 0 : 1);
				writeB(new byte[28]);
				writeC(0);
				break;
			}
			case 3:
			{
				writeH(code);
				writeD(commonData.getObjectId());
				break;
			}
			case 4:
			{
				writeD(commonData.getObjectId());
				writeS(commonData.getName());
				break;
			}
			case 5:
			{
				writeD(commonData.getObjectId());
				writeC(commonData.isLocked() ? 0 : 1);
				break;
			}
			case 6:
			{
				if (commonData == null)
				{
					return;
				}
				
				writeS(commonData.getName());
				writeD(commonData.getObjectId());
				writeD(commonData.getMinionId());
				writeD(commonData.getMasterObjectId());
				break;
			}
			case 7:
			{
				if (commonData == null)
				{
					return;
				}
				
				writeD(commonData.getObjectId());
				writeC(21);
				break;
			}
			case 8:
			{
				if (commonData == null)
				{
					return;
				}
				
				writeD(commonData.getObjectId());
				writeD(commonData.getGrowthPoints());
				break;
			}
			case 9:
			{
				writeC(subaction);
				if (subaction == 1)
				{
					if (lootNpcId > 0)
					{
						writeC(isActing ? 1 : 2);
						writeD(lootNpcId);
						break;
					}
					
					writeC(0);
					writeC(isActing ? 1 : 0);
					break;
				}
				
				if (subaction == 0)
				{
					writeC(dopeAction);
					switch (dopeAction)
					{
						case 0:
						{
							writeD(minionObjId);
							writeD(itemObjectId);
							writeD(dopeSlot);
							break;
						}
						case 1:
						{
							writeD(minionObjId);
							writeD(dopeSlot);
							break;
						}
						case 3:
						{
							writeD(minionObjId);
							writeD(itemObjectId);
							break;
						}
					}
					break;
				}
				break;
			}
			case 11:
			{
				writeD(0); // Function Expire Date
				writeD(0); // unk
				break;
			}
			case 13:
			{
				writeD(con.getActivePlayer().getCommonData().getMinionEnergy());
				writeC(1);
				break;
			}
			case 14:
			{
				writeC(0);
				break;
			}
		}
	}
}
