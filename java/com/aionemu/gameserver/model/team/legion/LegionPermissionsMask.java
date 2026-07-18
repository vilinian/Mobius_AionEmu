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
package com.aionemu.gameserver.model.team.legion;

/**
 * Defines the set of permissions available for members within a {@link com.aionemu.gameserver.model.team.legion.Legion}.<br>
 * This enum acts as a bitmask to manage and check specific administrative rights.<br>
 * It helps determine what actions a user can perform based on their assigned role.
 * @author MrPoke
 */
public enum LegionPermissionsMask
{
	EDIT(0x200),
	INVITE(0x8),
	KICK(0x10),
	WH_WITHDRAWAL(0x4),
	WH_DEPOSIT(0x1000),
	ARTIFACT(0x400),
	GUARDIAN_STONE(0x800);
	
	private final int rank;
	
	/**
	 * Creates a new instance of {@link LegionPermissionsMask}.<br>
	 * This constructor assigns the bitmask value to the internal field.
	 * @param rank The integer value representing the specific permission.
	 */
	private LegionPermissionsMask(int rank)
	{
		this.rank = rank;
	}
	
	/**
	 * Checks if the current {@link LegionPermissionsMask} has a specific permission.<br>
	 * It compares the internal rank against the provided bitmask.
	 * @param permission The integer bitmask representing the permission to check.
	 * @return {@code true} if the permission is granted, otherwise {@code false}.
	 */
	public boolean can(int permission)
	{
		return (rank & permission) != 0;
	}
}
