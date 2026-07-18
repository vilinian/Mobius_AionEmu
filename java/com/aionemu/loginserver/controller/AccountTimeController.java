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
package com.aionemu.loginserver.controller;

import java.sql.Timestamp;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.loginserver.dao.AccountPlayTimeDAO;
import com.aionemu.loginserver.dao.AccountTimeDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.model.AccountTime;

/**
 * This class manages account time tracking for players.<br>
 * It retrieves the daily online and rest time when a character logs into any server.<br>
 * These values are used to determine various in-game features that depend on player activity.
 * @author EvilSpirit
 */
public class AccountTimeController
{
	/**
	 * Updates the time statistics for an {@link Account} when a character logs in.<br>
	 * This method refreshes the last login timestamp and calculates rest time.<br>
	 * It also handles daily resets and return status logic.
	 * @param account The {@code Account} object to be updated.
	 */
	public static void updateOnLogin(Account account)
	{
		AccountTime accountTime = account.getAccountTime();
		
		/**
		 * It seems the account was just created, so new accountTime should be created too
		 */
		if (accountTime == null)
		{
			accountTime = new AccountTime();
		}
		
		final int lastLoginDay = getDays(accountTime.getLastLoginTime().getTime());
		final int currentDay = getDays(System.currentTimeMillis());
		final int returnday = getDays(accountTime.getLastLoginTime().getTime() + (+30L * 24 * 60 * 60 * 1000));
		
		/**
		 * The character from that account was online not today, so it's account timings should be nulled.
		 */
		if (lastLoginDay < currentDay)
		{
			DAOManager.getDAO(AccountPlayTimeDAO.class).update(account.getId(), accountTime);
			accountTime.setAccumulatedOnlineTime(0);
			accountTime.setAccumulatedRestTime(0);
		}
		else
		{
			final long restTime = System.currentTimeMillis() - accountTime.getLastLoginTime().getTime() - accountTime.getSessionDuration();
			accountTime.setAccumulatedRestTime(accountTime.getAccumulatedRestTime() + restTime);
			
		}
		
		accountTime.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
		
		DAOManager.getDAO(AccountTimeDAO.class).updateAccountTime(account.getId(), accountTime);
		account.setAccountTime(accountTime);
		
		if ((currentDay >= returnday) && (account.getReturn() == 0))
		{
			account.setReturn((byte) 1);
			account.setReturnEnd(new Timestamp(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)));
		}
		
		if (currentDay >= account.getReturnEnd().getTime())
		{
			account.setReturn((byte) 0);
		}
		
	}
	
	/**
	 * Updates the online time for an {@link Account}.<br>
	 * This method calculates the session duration since the last login.<br>
	 * It adds that duration to the total accumulated online time.<br>
	 * The updated data is saved to the database via {@link AccountTimeDAO}.
	 * @param account The {@code Account} object to update.
	 */
	public static void updateOnLogout(Account account)
	{
		final AccountTime accountTime = account.getAccountTime();
		
		accountTime.setSessionDuration(System.currentTimeMillis() - accountTime.getLastLoginTime().getTime());
		accountTime.setAccumulatedOnlineTime(accountTime.getAccumulatedOnlineTime() + accountTime.getSessionDuration());
		DAOManager.getDAO(AccountTimeDAO.class).updateAccountTime(account.getId(), accountTime);
		account.setAccountTime(accountTime);
	}
	
	/**
	 * Checks if the provided {@code Account} has passed its expiration date.<br>
	 * It compares the current system time against the account's expiration timestamp.
	 * @param account The {@link Account} object to check.
	 * @return {@code true} if the account is expired, {@code false} otherwise.
	 */
	public static boolean isAccountExpired(Account account)
	{
		final AccountTime accountTime = account.getAccountTime();
		
		return (accountTime != null) && (accountTime.getExpirationTime() != null) && (accountTime.getExpirationTime().getTime() < System.currentTimeMillis());
	}
	
	/**
	 * Checks if an account currently has an active penalty.<br>
	 * It verifies the {@code AccountTime} for the given {@link Account}.<br>
	 * A penalty is considered active if it has not expired or is set to infinity.
	 * @param account The {@code Account} object to check.
	 * @return {@code true} if a penalty is active, {@code false} otherwise.
	 */
	public static boolean isAccountPenaltyActive(Account account)
	{
		final AccountTime accountTime = account.getAccountTime();
		
		// 1000 is 'infinity' value
		return (accountTime != null) && (accountTime.getPenaltyEnd() != null) && ((accountTime.getPenaltyEnd().getTime() == 1000) || (accountTime.getPenaltyEnd().getTime() >= System.currentTimeMillis()));
	}
	
	/**
	 * Converts a duration in milliseconds into the total number of days.<br>
	 * This method performs integer division to find the full days elapsed.
	 * @param millis The time duration in {@code long} milliseconds.
	 * @return The total number of days as an {@code int}.
	 */
	public static int getDays(long millis)
	{
		return (int) (millis / 1000 / 3600 / 24);
	}
}
