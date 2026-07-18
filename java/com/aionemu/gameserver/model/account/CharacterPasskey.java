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
package com.aionemu.gameserver.model.account;

/**
 * Represents a unique security key associated with a specific character.<br>
 * This class is used to manage and verify access permissions for {@link com.aionemu.gameserver.model.account.Account} entities.
 * @author cura
 */
public class CharacterPasskey
{
	private int objectId;
	private int wrongCount = 0;
	private boolean isPass = false;
	private ConnectType connectType;
	
	/**
	 * Retrieves the unique identifier of this character passkey.<br>
	 * This value is used to identify the specific object in the system.
	 * @return The {@code int} value of the object ID.
	 */
	public int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Sets the unique identifier for this character passkey.<br>
	 * This value is used to identify the specific object in the system.
	 * @param objectId The {@code int} value to assign as the new ID.
	 */
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Retrieves the number of incorrect attempts.<br>
	 * This value tracks how many times a passkey was entered incorrectly.
	 * @return the current {@code int} count of wrong attempts.
	 */
	public int getWrongCount()
	{
		return wrongCount;
	}
	
	/**
	 * Updates the number of incorrect attempts.<br>
	 * This value is used to track failed login or action counts.
	 * @param count The new {@code int} value for the wrong attempt counter.
	 */
	public void setWrongCount(int count)
	{
		wrongCount = count;
	}
	
	/**
	 * Checks if the passkey status is currently active.<br>
	 * This method returns the current value of the {@code isPass} field.
	 * @return {@code true} if the pass is active, {@code false} otherwise.
	 */
	public boolean isPass()
	{
		return isPass;
	}
	
	/**
	 * Updates the pass status of the character.<br>
	 * This method sets the {@code isPass} field to the provided value.
	 * @param isPass The new boolean value to set for the pass status.
	 */
	public void setIsPass(boolean isPass)
	{
		this.isPass = isPass;
	}
	
	/**
	 * Retrieves the current connection type.<br>
	 * This method returns the {@code ConnectType} associated with this character passkey.
	 * @return the {@code ConnectType} of the object.
	 */
	public ConnectType getConnectType()
	{
		return connectType;
	}
	
	/**
	 * Sets the connection type for this character passkey.<br>
	 * This updates the {@code connectType} field with a new value.
	 * @param connectType The new {@code ConnectType} to assign.
	 */
	public void setConnectType(ConnectType connectType)
	{
		this.connectType = connectType;
	}
	
	public enum ConnectType
	{
		ENTER,
		DELETE
	}
}
