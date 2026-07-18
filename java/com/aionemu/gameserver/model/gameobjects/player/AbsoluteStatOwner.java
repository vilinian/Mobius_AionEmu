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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * This class represents an entity that possesses absolute statistics.<br>
 * It provides a base implementation for objects that own stats without complex calculations.<br>
 * It implements the {@link StatOwner} interface to manage these values.
 * @author Rolandas
 */
public class AbsoluteStatOwner implements StatOwner
{
	
	Player target;
	ModifiersTemplate template;
	boolean isActive = false;
	
	/**
	 * Creates a new instance of {@code AbsoluteStatOwner}.<br>
	 * This constructor links a {@link Player} to a specific stat template.<br>
	 * It initializes the owner with the provided data.
	 * @param player The {@link Player} who will own these stats.
	 * @param templateId The unique identifier for the {@code ModifiersTemplate}.
	 */
	public AbsoluteStatOwner(Player player, int templateId)
	{
		target = player;
		setTemplate(templateId);
	}
	
	/**
	 * Checks if the portal location is currently active.<br>
	 * This method returns the current state of the {@code isActive} field.
	 * @return {@code true} if the location is active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return isActive;
	}
	
	/**
	 * Sets the template for this stat owner.<br>
	 * This method updates the {@code template} field using the provided ID.<br>
	 * It calls {@code cancel} if the current state is active.
	 * @param templateId The unique identifier for the new template.
	 */
	public void setTemplate(int templateId)
	{
		if (isActive)
		{
			cancel();
		}
		
		template = DataManager.ABSOLUTE_STATS_DATA.getTemplate(templateId);
	}
	
	/**
	 * Applies the statistics from the {@code template} to the {@link Player}.<br>
	 * This method updates the target's game stats and sets {@code isActive} to {@code true}.<br>
	 * If the {@code template} is {@code null}, no action is taken.
	 */
	public void apply()
	{
		if (template == null)
		{
			return;
		}
		
		target.getGameStats().addEffect(this, template.getModifiers());
		isActive = true;
	}
	
	/**
	 * Stops the current effect for the {@link Player}.<br>
	 * Sets the {@code isActive} status to {@code false}.<br>
	 * This method does nothing if the {@code template} is {@code null}.
	 */
	public void cancel()
	{
		if (template == null)
		{
			return;
		}
		
		target.getGameStats().endEffect(this);
		isActive = false;
	}
}
