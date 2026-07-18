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
package com.aionemu.gameserver.model.broker;

/**
 * This class defines the set of message types used for communication between brokers.<br>
 * It serves as a central registry for all {@code BrokerMessages} handled by the system.
 * @author kosyachok
 */
public enum BrokerMessages
{
	CANT_REGISTER_ITEM(2),
	NO_SPACE_AVAIABLE(3),
	NO_ENOUGHT_KINAH(5);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link BrokerMessages}.<br>
	 * This constructor assigns the unique identifier to the message.
	 * @param id The numeric ID assigned to this specific message type.
	 */
	private BrokerMessages(int id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
