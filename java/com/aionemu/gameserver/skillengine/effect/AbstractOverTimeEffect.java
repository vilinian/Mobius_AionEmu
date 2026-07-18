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
package com.aionemu.gameserver.skillengine.effect;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Provides a base template for effects that occur over a specific duration.<br>
 * It handles the logic for recurring actions or continuous states applied to a {@link Creature}.<br>
 * Subclasses should implement the specific behavior triggered during each tick of the effect.
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractOverTimeEffect")
public abstract class AbstractOverTimeEffect extends EffectTemplate
{
	@XmlAttribute(required = true)
	protected int checktime;
	@XmlAttribute
	protected boolean percent;
	@XmlAttribute
	protected boolean shared;
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	@Override
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Adds the specified {@code Effect} to the controller.<br>
	 * This updates the internal state of the effect's target.
	 * @param effect The {@code Effect} object to be added.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}
	
	/**
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		this.startEffect(effect, null);
	}
	
	/**
	 * Starts a periodic effect on a creature.<br>
	 * This method schedules the {@code onPeriodicAction} task using the {@link ThreadPoolManager}.<br>
	 * It also updates the abnormal state if one is provided.
	 * @param effect The {@code Effect} instance to be started.
	 * @param abnormal The {@link AbnormalState} associated with this effect, or {@code null} if none exists.
	 */
	public void startEffect(Effect effect, AbnormalState abnormal)
	{
		final Creature effected = effect.getEffected();
		
		if (abnormal != null)
		{
			effect.setAbnormal(abnormal.getId());
			effected.getEffectController().setAbnormal(abnormal.getId());
		}
		
		// TODO figure out what to do with such cases
		if (checktime == 0)
		{
			return;
		}
		
		try
		{
			final Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
			{
				@Override
				public void run()
				{
					onPeriodicAction(effect);
				}
			}, checktime, checktime);
			effect.setPeriodicTask(task, position);
		}
		catch (Exception e)
		{
			log.warn("Exception in skillId: " + effect.getSkillId());
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops an active effect for a creature.<br>
	 * This method removes the associated {@code AbnormalState} if one exists.
	 * @param effect The {@link Effect} instance to be ended.
	 * @param abnormal The {@link AbnormalState} to remove from the target.
	 */
	public void endEffect(Effect effect, AbnormalState abnormal)
	{
		if (abnormal != null)
		{
			effect.getEffected().getEffectController().unsetAbnormal(abnormal.getId());
		}
	}
}
