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

import java.util.Arrays;

import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * This filter checks if a broker listing contains a specific {@link ItemTemplate}.<br>
 * It is used to narrow down results based on the presence of an item in a collection.
 * @author ATracer
 */
public class BrokerContainsFilter extends BrokerFilter
{
	private final int[] masks;
	
	/**
	 * Creates a new filter based on specific bitwise masks.<br>
	 * This filter is used to check if an item matches the provided criteria.
	 * @param masks The array of {@code int} values representing the required filters.
	 */
	public BrokerContainsFilter(int... masks)
	{
		this.masks = masks;
	}
	
	/**
	 * Checks if the item belongs to a specific category.<br>
	 * It compares the {@link ItemTemplate} ID against the allowed masks.
	 * @param template The {@code ItemTemplate} to check.
	 * @return {@code true} if the item is accepted, otherwise {@code false}.
	 */
	@Override
	public boolean accept(ItemTemplate template)
	{
		final int value = template.getTemplateId() / 100000;
		return Arrays.stream(masks).anyMatch(e -> e == value);
	}
}
