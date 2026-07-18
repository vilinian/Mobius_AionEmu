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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the transmission of account access properties.<br>
 * It is used to synchronize specific permissions or settings for a user's account.
 * @author pixfid
 * @modified Magenik , Kev
 */
public class SM_ACCOUNT_ACCESS_PROPERTIES extends AionServerPacket
{
	/**
	 * Creates a new instance of the {@code SM_ACCOUNT_ACCESS_PROPERTIES} packet.<br>
	 * This is used to handle account access information.
	 */
	public SM_ACCOUNT_ACCESS_PROPERTIES()
	{
	}
	
	private boolean isGM;
	private int accountType;
	private int purchaseType;
	private int time;
	private boolean active;
	
	/**
	 * Creates a new instance of {@link SM_ACCOUNT_ACCESS_PROPERTIES}.<br>
	 * This constructor sets the GM status for the account.
	 * @param isGM The boolean value indicating if the user is a Game Master. Set to {@code true} for GM or {@code false} for regular users.
	 */
	public SM_ACCOUNT_ACCESS_PROPERTIES(boolean isGM)
	{
		this.isGM = isGM;
	}
	
	/**
	 * Creates a new {@code SM_ACCOUNT_ACCESS_PROPERTIES} packet.<br>
	 * This constructor initializes all access properties for an account.
	 * @param isGM The status of the Game Master flag.
	 * @param accountType The specific type of the account.
	 * @param purchaseType The category of the purchase.
	 * @param time The timestamp associated with the property.
	 * @param active The current activity status of the account.
	 */
	public SM_ACCOUNT_ACCESS_PROPERTIES(boolean isGM, int accountType, int purchaseType, int time, boolean active)
	{
		this.isGM = isGM;
		this.accountType = accountType;
		this.purchaseType = purchaseType;
		this.time = time;
		this.active = active;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(isGM ? 3 : 0); // 3 = GM-Panel(Shift+F1)
		writeH(0);
		writeD(0);
		writeD(0);
		writeD(isGM ? 32768 : 0); // unk
		writeD(0);
		writeC(0);
		writeD(active ? 31 : 0); // 31 with Active GoldPaket
		writeD(0); // should be always 0
		writeD(purchaseType); // GoldPaket active 8 else 0
		writeD(accountType); // 2 = Starter 4 = Veteran
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(time); // GoldPaket Time
		writeB(new byte[32]); // unk 4.9
	}
}
