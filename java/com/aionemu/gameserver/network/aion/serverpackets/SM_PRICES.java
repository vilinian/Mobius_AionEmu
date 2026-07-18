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
import com.aionemu.gameserver.services.trade.PricesService;

/**
 * This class handles the {@code SM_PRICES} server packet.<br>
 * It is used to send price information to the client.<br>
 * It interacts with the {@link PricesService} to retrieve relevant data.
 * @author xavier, Sarynth modified by Wakizashi Price/tax in Influence ration dialog
 */
public class SM_PRICES extends AionServerPacket
{
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(PricesService.getGlobalPrices(con.getActivePlayer().getRace())); // Display Buying Price
		
		// %
		writeC(PricesService.getGlobalPricesModifier()); // Buying Modified Price %
		writeC(PricesService.getTaxes(con.getActivePlayer().getRace())); // Tax = -100 + C %
	}
}
