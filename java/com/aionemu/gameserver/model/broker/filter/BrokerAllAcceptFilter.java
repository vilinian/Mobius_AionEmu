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

import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * This filter allows all items to be accepted by the broker.<br>
 * It acts as a universal pass-through for {@link ItemTemplate} processing.
 * @author ATracer
 */
public class BrokerAllAcceptFilter extends BrokerFilter
{
	/**
	 * This filter accepts all items.<br>
	 * It always returns {@code true} for any given {@link ItemTemplate}.
	 * @param template The {@code ItemTemplate} to check.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean accept(ItemTemplate template)
	{
		return true;
	}
}
