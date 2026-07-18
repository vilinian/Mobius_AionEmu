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
package com.aionemu.gameserver.model.templates.pet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class defines the functional behaviors and actions associated with pets.<br>
 * It serves as a template for handling various pet-related mechanics in the game.
 * @author IlBuono
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "petfunction")
public class PetFunction
{
	@XmlAttribute(name = "type")
	private PetFunctionType type;
	@XmlAttribute(name = "id")
	private int id;
	@XmlAttribute(name = "slots")
	private int slots;
	
	/**
	 * Retrieves the specific type of the pet function.<br>
	 * This method returns the {@code type} field associated with this object.
	 * @return The {@link PetFunctionType} of the current pet function.
	 */
	public PetFunctionType getPetFunctionType()
	{
		return type;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the number of available slots for this pet function.<br>
	 * This value is stored in the {@code slots} field.
	 * @return The total count of slots as an {@code int}.
	 */
	public int getSlots()
	{
		return slots;
	}
	
	/**
	 * Creates a new {@link PetFunction} instance with default values.<br>
	 * The type is set to {@code PetFunctionType.NONE}.
	 * @return A new {@code PetFunction} object.
	 */
	public static PetFunction CreateEmpty()
	{
		final PetFunction result = new PetFunction();
		result.type = PetFunctionType.NONE;
		return result;
	}
}
