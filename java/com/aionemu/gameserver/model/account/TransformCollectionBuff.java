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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.templates.transform_book.CollectionAttr;
import com.aionemu.gameserver.model.templates.transform_book.TransformCollectionTemplate;

/**
 * This class manages the buffs provided by a {@link TransformCollectionTemplate}.<br>
 * It allows for the calculation of various statistics based on collected attributes.<br>
 * It implements {@link StatOwner} to provide data for stat calculations.
 */
public class TransformCollectionBuff implements StatOwner
{
	Logger log = LoggerFactory.getLogger(TransformCollectionBuff.class);
	
	private final List<IStatFunction> functions = new ArrayList<>();
	
	/**
	 * Applies the transformation effects to a specific player.<br>
	 * This method checks if the player is a magical or physical class.<br>
	 * It then adds the correct attributes from the {@code TransformCollectionTemplate} to the player's stats.
	 * @param player The {@link Player} who will receive the buff.
	 * @param template The {@link TransformCollectionTemplate} containing the transformation data.
	 */
	public void apply(Player player, TransformCollectionTemplate template)
	{
		if (template == null)
		{
			return;
		}
		
		CollectionAttr attribute = null;
		if (player.isMagicalTypeClass())
		{
			attribute = template.getMagicalAttr();
		}
		else
		{
			attribute = template.getPhysicalAttr();
		}
		
		functions.add(new StatRateFunction(attribute.getName(), attribute.getValue(), true));
		player.setBonus(true);
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * Removes the buff from the specified {@link Player}.<br>
	 * Clears all associated functions and sets the bonus to {@code false}.<br>
	 * Notifies the game stats to end the effect.
	 * @param player The {@code Player} instance to update.
	 */
	public void end(Player player)
	{
		functions.clear();
		player.setBonus(false);
		player.getGameStats().endEffect(this);
	}
}
