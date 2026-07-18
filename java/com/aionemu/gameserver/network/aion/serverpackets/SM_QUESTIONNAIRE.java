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
 * Sends survey {@code HTML} data to the client.<br>
 * This packet can be split over a maximum of {@code 255} packets.<br>
 * The maximum length for the {@code HTML} content is {@code 255 * 65525} bytes.
 * @author lhw and Kaipo
 */
public class SM_QUESTIONNAIRE extends AionServerPacket
{
	private final int messageId;
	private final byte chunk;
	private final byte count;
	private final String html;
	
	/**
	 * Creates a new {@link SM_QUESTIONNAIRE} packet.<br>
	 * This constructor initializes the survey data for transmission to the client.
	 * @param messageId The unique identifier for the message.
	 * @param chunk The current index of the data chunk.
	 * @param count The total number of chunks.
	 * @param html The HTML content string to be sent.
	 */
	public SM_QUESTIONNAIRE(int messageId, byte chunk, byte count, String html)
	{
		this.messageId = messageId;
		this.chunk = chunk;
		this.count = count;
		this.html = html;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(messageId);
		writeC(chunk);
		writeC(count);
		writeH(html.length() * 2);
		writeS(html);
	}
}
