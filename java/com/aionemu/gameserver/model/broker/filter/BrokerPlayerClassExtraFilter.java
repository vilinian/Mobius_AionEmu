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
 * This filter handles additional logic for filtering player classes in the broker system.<br>
 * It extends {@link BrokerPlayerClassFilter} to provide specific requirements based on extra criteria.
 * @author ATracer
 */
public class BrokerPlayerClassExtraFilter extends BrokerPlayerClassFilter
{
	private final int mask;
	
	/**
	 * Creates a new filter for broker items based on specific criteria.<br>
	 * This constructor initializes the filter with a bitwise {@code mask}.<br>
	 * It also sets the required {@link PlayerClass}.
	 * @param mask The integer bitmask used to determine filtering rules.
	 * @param playerClass The specific class of player to filter by.
	 */
	public BrokerPlayerClassExtraFilter(int mask, PlayerClass playerClass)
	{
		super(playerClass);
		this.mask = mask;
	}
	
	/**
	 * Checks if the item matches the required mask.<br>
	 * It validates the {@link ItemTemplate} against a specific ID range.
	 * @param template The {@code ItemTemplate} to check.
	 * @return {@code true} if the item is accepted, otherwise {@code false}.
	 */
	@Override
	public boolean accept(ItemTemplate template)
	{
		return super.accept(template) && (mask == (template.getTemplateId() / 100000));
	}
}
