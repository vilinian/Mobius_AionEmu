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
package com.aionemu.gameserver.model.templates.cosmeticitems;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * Represents the base data template for cosmetic items in the game.<br>
 * This class stores configuration details such as appearance and requirements for specific cosmetics.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CosmeticItemTemplate")
public class CosmeticItemTemplate
{
	@XmlAttribute(name = "type")
	private String type;
	@XmlAttribute(name = "cosmetic_name")
	private String cosmeticName;
	@XmlAttribute(name = "id")
	private int id;
	@XmlAttribute(name = "race")
	private Race race;
	@XmlAttribute(name = "gender_permitted")
	private String genderPermitted;
	@XmlElement(name = "preset")
	private Preset preset;
	
	/**
	 * Retrieves the type of the task.<br>
	 * This method returns the value stored in the {@code type} field.
	 * @return The string representation of the task type.
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the display name of the cosmetic item.<br>
	 * This value is stored in the {@code cosmetic_name} attribute.
	 * @return The name of the cosmetic as a {@code String}.
	 */
	public String getCosmeticName()
	{
		return cosmeticName;
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
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the allowed genders for this cosmetic item.<br>
	 * This value determines which characters can wear the item.
	 * @return The {@code String} representing permitted genders.
	 */
	public String getGenderPermitted()
	{
		return genderPermitted;
	}
	
	/**
	 * Retrieves the {@code Preset} associated with this cosmetic item.<br>
	 * This method returns the configuration data for the item's appearance.
	 * @return the {@code Preset} object or {@code null} if no preset is defined.
	 */
	public Preset getPreset()
	{
		return preset;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "Preset")
	public static class Preset
	{
		@XmlElement(name = "scale")
		private float scale;
		@XmlElement(name = "hair_type")
		private int hairType;
		@XmlElement(name = "face_type")
		private int faceType;
		@XmlElement(name = "hair_color")
		private int hairColor;
		@XmlElement(name = "lip_color")
		private int lipColor;
		@XmlElement(name = "eye_color")
		private int eyeColor;
		@XmlElement(name = "eye_color2")
		private int eyeColor2;
		@XmlElement(name = "skin_color")
		private int skinColor;
		
		public float getScale()
		{
			return scale;
		}
		
		public int getHairType()
		{
			return hairType;
		}
		
		public int getFaceType()
		{
			return faceType;
		}
		
		public int getHairColor()
		{
			return hairColor;
		}
		
		public int getLipColor()
		{
			return lipColor;
		}
		
		public int getEyeColor()
		{
			return eyeColor;
		}
		
		public int getEyeColor2()
		{
			return eyeColor2;
		}
		
		public int getSkinColor()
		{
			return skinColor;
		}
	}
}
