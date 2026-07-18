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
package com.aionemu.gameserver.skillengine.effect.modifier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Represents a modification applied to an action within the skill engine.<br>
 * This abstract class serves as a base for various effects that alter game behavior.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionModifier")
public abstract class ActionModifier
{
	@XmlAttribute
	protected int delta;
	@XmlAttribute(required = true)
	protected int value;
	@XmlAttribute
	protected Func mode = Func.ADD;
	
	/**
	 * Applies modifier to original value
	 * @param effect
	 * @return int
	 */
	public abstract int analyze(Effect effect);
	
	/**
	 * Performs check of condition
	 * @param effect
	 * @return true or false
	 */
	public abstract boolean check(Effect effect);
	
	/**
	 * Retrieves the {@code Func} associated with this attribute.<br>
	 * This method returns the function used for calculations.
	 * @return The {@code Func} object.
	 */
	public Func getFunc()
	{
		return mode;
	}
}
