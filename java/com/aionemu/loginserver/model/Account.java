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
package com.aionemu.loginserver.model;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.templates.mail.MailPart;

/**
 * Represents the data model for a user account.<br>
 * It stores essential information required to manage and identify accounts within the system.
 * @author SoulKeeper
 */
public class Account
{
	/**
	 * Id of account, object if assigned, null if not
	 */
	private Integer id;
	/**
	 * Account name
	 */
	private String name;
	/**
	 * Password hash
	 */
	private String passwordHash;
	/**
	 * Access level of account 0 = regular user, > 0 = GM
	 */
	private byte accessLevel;
	/**
	 * Membership of this account (regular, premium etc)
	 */
	private byte membership;
	/**
	 * Account activated
	 */
	private byte activated;
	/**
	 * last server visited by user -1 if none
	 */
	private byte lastServer;
	/**
	 * Last ip of user -1 if none
	 */
	private String lastIp;
	/**
	 * Last mac of user xx-xx-xx-xx-xx-xx if none
	 */
	private String lastMac = "xx-xx-xx-xx-xx-xx";
	/**
	 * The only ip that is allowed to this account
	 */
	private String ipForce;
	/**
	 * AccountTime data
	 */
	private AccountTime accountTime;
	
	private byte isReturn;
	
	private Timestamp returnEnd;
	
	/**
	 * Retrieves the unique identifier for this {@link MailPart}.<br>
	 * This value is used to distinguish different parts of a mail.
	 * @return The {@code Integer} ID of the part, or {@code null} if not set.
	 */
	public Integer getId()
	{
		return id;
	}
	
	/**
	 * Sets the unique identifier for this {@link Account}.<br>
	 * The value can be {@code null} if no ID is assigned.
	 * @param id The unique identifier to assign to the account.
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the unique name for this {@link Account}.<br>
	 * The provided {@code String} is stored as the account name.
	 * @param name The new name to assign to the account.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Retrieves the hashed version of the account password.<br>
	 * This value is used for secure authentication.
	 * @return The {@code String} representing the password hash.
	 */
	public String getPasswordHash()
	{
		return passwordHash;
	}
	
	/**
	 * Updates the hashed password for this account.<br>
	 * This method stores the provided {@code String} in the {@code passwordHash} field.
	 * @param passwordHash The new hash to be stored.
	 */
	public void setPasswordHash(String passwordHash)
	{
		this.passwordHash = passwordHash;
	}
	
	/**
	 * Retrieves the current access level of the account.<br>
	 * This value determines what actions the user is allowed to perform.
	 * @return The {@code byte} value representing the account's access level.
	 */
	public byte getAccessLevel()
	{
		return accessLevel;
	}
	
	/**
	 * Sets the security access level for this account.<br>
	 * This value determines what actions the user can perform.
	 * @param accessLevel The new {@code byte} value to set as the access level.
	 */
	public void setAccessLevel(byte accessLevel)
	{
		this.accessLevel = accessLevel;
	}
	
	/**
	 * Retrieves the membership status of this account.<br>
	 * This value is stored as a {@code byte}.
	 * @return The current membership level.
	 */
	public byte getMembership()
	{
		return membership;
	}
	
	/**
	 * Updates the membership status of the account.<br>
	 * This method sets the {@code membership} field to a new value.
	 * @param membership The new membership level to assign.
	 */
	public void setMembership(byte membership)
	{
		this.membership = membership;
	}
	
	/**
	 * Checks if the account is currently active.<br>
	 * This method returns the status of the {@code activated} field.
	 * @return the activation status as a {@code byte}.
	 */
	public byte getActivated()
	{
		return activated;
	}
	
	/**
	 * Updates the activation status of the account.<br>
	 * This method sets the {@code activated} field to a new value.
	 * @param activated The new activation status as a {@code byte}.
	 */
	public void setActivated(byte activated)
	{
		this.activated = activated;
	}
	
	/**
	 * Retrieves the ID of the last server visited by the user.<br>
	 * Returns -1 if no server has been visited yet.
	 * @return The last server ID as a {@code byte}.
	 */
	public byte getLastServer()
	{
		return lastServer;
	}
	
	/**
	 * Updates the ID of the last server visited by the user.<br>
	 * Use {@code -1} if no server has been visited yet.
	 * @param lastServer The ID of the last server to store.
	 */
	public void setLastServer(byte lastServer)
	{
		this.lastServer = lastServer;
	}
	
	/**
	 * Retrieves the IP address of the last login.<br>
	 * This method returns the value stored in the {@code lastIp} field.<br>
	 * It may return {@code null} if no IP is recorded.
	 * @return The last known IP address as a {@code String}.
	 */
	public String getLastIp()
	{
		return lastIp;
	}
	
	/**
	 * Updates the last known IP address for this account.<br>
	 * This method stores the provided {@code String} value into the {@code lastIp} field.
	 * @param lastIp The IP address to be saved.
	 */
	public void setLastIp(String lastIp)
	{
		this.lastIp = lastIp;
	}
	
	/**
	 * Retrieves the last MAC address associated with this account.<br>
	 * It returns the value stored in the {@code lastMac} field.
	 * @return The last MAC address as a {@code String}.
	 */
	public String getLastMac()
	{
		return lastMac;
	}
	
	/**
	 * Updates the last known MAC address for this account.<br>
	 * The value is stored as a {@code String}.
	 * @param lastMac The new MAC address to assign.
	 */
	public void setLastMac(String lastMac)
	{
		this.lastMac = lastMac;
	}
	
	/**
	 * Retrieves the forced IP address for this account.<br>
	 * This value is used to restrict login access to a specific IP.
	 * @return the forced IP address as a {@code String}
	 */
	public String getIpForce()
	{
		return ipForce;
	}
	
	/**
	 * Sets the specific IP address allowed for this account.<br>
	 * This value is used to restrict access to a single IP.
	 * @param ipForce The {@code String} representing the forced IP address.
	 */
	public void setIpForce(String ipForce)
	{
		this.ipForce = ipForce;
	}
	
	/**
	 * Retrieves the time information for this account.<br>
	 * This method returns the {@code AccountTime} object associated with the current account.
	 * @return the {@link AccountTime} object.
	 */
	public AccountTime getAccountTime()
	{
		return accountTime;
	}
	
	/**
	 * Updates the time information for this account.<br>
	 * This method sets the {@code accountTime} field to the provided value.
	 * @param accountTime The new {@link AccountTime} object to assign.
	 */
	public void setAccountTime(AccountTime accountTime)
	{
		this.accountTime = accountTime;
	}
	
	/**
	 * Retrieves the return status of the account.<br>
	 * This value corresponds to the {@code isReturn} field.
	 * @return the current return status as a {@code byte}.
	 */
	public byte getReturn()
	{
		return isReturn;
	}
	
	/**
	 * Updates the return status of the account.<br>
	 * This method sets the {@code isReturn} field.
	 * @param isReturn The new return status value.
	 */
	public void setReturn(byte isReturn)
	{
		this.isReturn = isReturn;
	}
	
	/**
	 * Retrieves the timestamp for when the account's return period ends.<br>
	 * This value is stored in the {@code returnEnd} field.
	 * @return the end timestamp of the return period.
	 */
	public Timestamp getReturnEnd()
	{
		return returnEnd;
	}
	
	/**
	 * Sets the end time for the account return period.<br>
	 * This updates the {@code returnEnd} field with the provided value.
	 * @param end The {@code Timestamp} representing when the return period ends.
	 */
	public void setReturnEnd(Timestamp end)
	{
		returnEnd = end;
	}
	
	/**
	 * Compares this {@link Account} object with another object for equality.<br>
	 * It checks if both objects have the same name and password hash.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		
		if (!(o instanceof Account))
		{
			return false;
		}
		
		final Account account = (Account) o;
		
		// noinspection SimplifiableIfStatement
		if (name != null ? !name.equals(account.name) : account.name != null)
		{
			return false;
		}
		
		return !(passwordHash != null ? !passwordHash.equals(account.passwordHash) : account.passwordHash != null);
	}
	
	/**
	 * Returns a hash code value for this {@link Account} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code name} and {@code passwordHash} fields.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		
		result = (31 * result) + (passwordHash != null ? passwordHash.hashCode() : 0);
		
		return result;
	}
}
