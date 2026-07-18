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
package com.aionemu.gameserver.model.broker.filter;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * This class filters broker listings based on the {@link PlayerClass} of the player.<br>
 * It allows for specific filtering logic when searching for items or services.
 * @author ATracer
 */
public class BrokerPlayerClassFilter extends BrokerFilter
{
	private final PlayerClass playerClass;
	
	/**
	 * Creates a new filter based on a specific {@link PlayerClass}.<br>
	 * This filter is used to check if an item matches the required class.
	 * @param playerClass The {@code PlayerClass} to use for filtering.
	 */
	public BrokerPlayerClassFilter(PlayerClass playerClass)
	{
		super();
		this.playerClass = playerClass;
	}
	
	/**
	 * Checks if an item is allowed for a specific player class.<br>
	 * It verifies the {@link ItemTemplate} against the current {@code playerClass}.
	 * @param template The {@code ItemTemplate} to check.
	 * @return {@code true} if the item is compatible with the class, {@code false} otherwise.
	 */
	@Override
	public boolean accept(ItemTemplate template)
	{
		return template.isClassSpecific(playerClass);
	}
}
