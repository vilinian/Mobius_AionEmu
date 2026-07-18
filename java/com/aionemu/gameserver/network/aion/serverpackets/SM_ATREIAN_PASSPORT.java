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

import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the {@code ATREIAN_PASSPORT} server message.<br>
 * It is used to manage passport-related data for players in the game world.
 * @author Falke_34
 */
public class SM_ATREIAN_PASSPORT extends AionServerPacket
{
	private final int passportId;
	private final int countCollected;
	private final int lastStampRecived;
	private final boolean hasCollected;
	
	/**
	 * Creates a new {@code SM_ATREIAN_PASSPORT} packet.<br>
	 * This constructor initializes the passport data for the client.
	 * @param passportId The unique identifier for the passport.
	 * @param countCollected The total number of stamps collected.
	 * @param lastStampRecived The ID of the most recent stamp received.
	 * @param hasCollected A boolean indicating if any stamps have been collected.
	 */
	public SM_ATREIAN_PASSPORT(int passportId, int countCollected, int lastStampRecived, boolean hasCollected)
	{
		this.passportId = passportId;
		this.countCollected = countCollected;
		this.lastStampRecived = lastStampRecived;
		this.hasCollected = hasCollected;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final long tStamp = con.getActivePlayer().getPlayerAccount().getPlayerAccountData(con.getActivePlayer().getObjectId()).getPlayerCommonData().getCreationDate().getTime();
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tStamp);
		writeH(cal.get(Calendar.YEAR)); // Year?
		writeH(cal.get(Calendar.MONTH)); // Month?
		writeH(cal.get(Calendar.DAY_OF_MONTH)); // Day?
		writeH(EventsConfig.ENABLE_ATREIAN_PASSPORT); // 1=on, 0=off
		writeD(passportId); // Id
		writeD(lastStampRecived);
		writeD(countCollected);
		writeC(hasCollected ? 0 : 1);
	}
}
