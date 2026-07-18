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
package com.aionemu.gameserver.model.gameobjects.player.equipmentsetting;

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * This class manages the equipment configuration for a player.<br>
 * It stores and handles how items are equipped on a character's model.<br>
 * It acts as a {@link PersistentState} to ensure settings are saved between sessions.
 * @author Falke_34
 */
public class EquipmentSetting
{
	private PersistentState persistentState;
	private int slot;
	private int display;
	private int mHand;
	private int sHand;
	private int helmet;
	private int torso;
	private int glove;
	private int boots;
	private int earringsLeft;
	private int earringsRight;
	private int ringLeft;
	private int ringRight;
	private int necklace;
	private int shoulder;
	private int pants;
	private int powershardLeft;
	private int powershardRight;
	private int wings;
	private int waist;
	private int mOffHand;
	private int sOffHand;
	private int plume;
	private int bracelet;
	
	/**
	 * Creates a new {@link EquipmentSetting} instance with specific equipment data.<br>
	 * This constructor initializes all gear slots for the player character.
	 * @param slot The unique identifier for the equipment slot.
	 * @param display The value used for displaying the equipment.
	 * @param mHand The main hand weapon ID.
	 * @param sHand The secondary hand weapon ID.
	 * @param helmet The helmet item ID.
	 * @param torso The torso armor item ID.
	 * @param glove The glove item ID.
	 * @param boots The boots item ID.
	 * @param earringsLeft The left earring item ID.
	 * @param earringsRight The right earring item ID.
	 * @param ringLeft The left ring item ID.
	 * @param ringRight The right ring item ID.
	 * @param necklace The necklace item ID.
	 * @param shoulder The shoulder armor item ID.
	 * @param pants The pants item ID.
	 * @param powershardLeft The left power shard item ID.
	 * @param powershardRight The right power shard item ID.
	 * @param wings The wings item ID.
	 * @param waist The waist armor item ID.
	 * @param mOffHand
	 * @param sOffHand
	 * @param plume
	 * @param bracelet
	 */
	public EquipmentSetting(int slot, int display, int mHand, int sHand, int helmet, int torso, int glove, int boots, int earringsLeft, int earringsRight, int ringLeft, int ringRight, int necklace, int shoulder, int pants, int powershardLeft, int powershardRight, int wings, int waist, int mOffHand, int sOffHand, int plume, int bracelet)
	{
		this.slot = slot;
		this.display = display;
		this.mHand = mHand;
		this.sHand = sHand;
		this.helmet = helmet;
		this.torso = torso;
		this.glove = glove;
		this.boots = boots;
		this.earringsLeft = earringsLeft;
		this.earringsRight = earringsRight;
		this.ringLeft = ringLeft;
		this.ringRight = ringRight;
		this.necklace = necklace;
		this.shoulder = shoulder;
		this.pants = pants;
		this.powershardLeft = powershardLeft;
		this.powershardRight = powershardRight;
		this.wings = wings;
		this.waist = waist;
		this.mOffHand = mOffHand;
		this.sOffHand = sOffHand;
		this.plume = plume;
		this.bracelet = bracelet;
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this decoration.<br>
	 * This method assigns a new {@link PersistentState} to the object.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
	
	/**
	 * Retrieves the current slot position of this wardrobe entry.<br>
	 * This value is used to identify where the item is located in the inventory.
	 * @return The {@code int} value representing the slot index.
	 */
	public int getSlot()
	{
		return slot;
	}
	
	/**
	 * Updates the equipment slot identifier.<br>
	 * This method sets the {@code slot} value for this object.
	 * @param slot The new integer value for the equipment slot.
	 */
	public void setSlot(int slot)
	{
		this.slot = slot;
	}
	
	/**
	 * Retrieves the current display setting.<br>
	 * This value is stored in the {@code display} field.
	 * @return The integer value of the display setting.
	 */
	public int getDisplay()
	{
		return display;
	}
	
	/**
	 * Sets the display value for this equipment setting.<br>
	 * This updates the {@code display} field with a new integer value.
	 * @param display The new integer value to set for the display.
	 */
	public void setDisplay(int display)
	{
		this.display = display;
	}
	
	/**
	 * Retrieves the main hand equipment value.<br>
	 * This method returns the current {@code mHand} integer.
	 * @return The value of the main hand.
	 */
	public int getmHand()
	{
		return mHand;
	}
	
	/**
	 * Sets the main hand equipment value.<br>
	 * This updates the {@code mHand} field of the current object.
	 * @param mHand The new value to assign to the main hand.
	 */
	public void setmHand(int mHand)
	{
		this.mHand = mHand;
	}
	
	/**
	 * Retrieves the value for the secondary hand.<br>
	 * This method returns the {@code sHand} integer field.
	 * @return The current value of the secondary hand.
	 */
	public int getsHand()
	{
		return sHand;
	}
	
	/**
	 * Updates the secondary hand value.<br>
	 * This method sets the {@code sHand} field of the current object.
	 * @param sHand The new value for the secondary hand.
	 */
	public void setsHand(int sHand)
	{
		this.sHand = sHand;
	}
	
	/**
	 * Retrieves the current helmet value.<br>
	 * This method returns the integer ID of the equipped helmet.
	 * @return the {@code int} value representing the helmet.
	 */
	public int getHelmet()
	{
		return helmet;
	}
	
	/**
	 * Updates the helmet value for this equipment setting.<br>
	 * This method assigns a new {@code int} to the internal helmet field.
	 * @param helmet The new helmet value to set.
	 */
	public void setHelmet(int helmet)
	{
		this.helmet = helmet;
	}
	
	/**
	 * Retrieves the torso value of the player appearance.<br>
	 * This returns the current {@code int} stored in the torso field.
	 * @return the torso value
	 */
	public int getTorso()
	{
		return torso;
	}
	
	/**
	 * Sets the torso value for the player appearance.<br>
	 * This updates the {@code torso} field with a new integer value.
	 * @param torso The new value to set for the torso.
	 */
	public void setTorso(int torso)
	{
		this.torso = torso;
	}
	
	/**
	 * Retrieves the current value for the glove equipment.<br>
	 * This method returns the {@code int} stored in the glove field.
	 * @return The integer value of the glove.
	 */
	public int getGlove()
	{
		return glove;
	}
	
	/**
	 * Updates the {@code glove} value for this equipment setting.<br>
	 * This method sets the internal state of the glove property.
	 * @param glove The new integer value to assign to the glove slot.
	 */
	public void setGlove(int glove)
	{
		this.glove = glove;
	}
	
	/**
	 * Retrieves the current value for the boots equipment.<br>
	 * This method returns the integer ID associated with the boots slot.
	 * @return The {@code int} value of the boots.
	 */
	public int getBoots()
	{
		return boots;
	}
	
	/**
	 * Updates the {@code boots} value for this equipment setting.<br>
	 * This method sets the internal integer state of the boots.
	 * @param boots The new value to assign to the boots field.
	 */
	public void setBoots(int boots)
	{
		this.boots = boots;
	}
	
	/**
	 * Retrieves the ID of the earring in the left ear slot.<br>
	 * This value is stored within the {@link EquipmentSetting} object.
	 * @return The integer ID of the left earring.
	 */
	public int getEarringsLeft()
	{
		return earringsLeft;
	}
	
	/**
	 * Sets the ID for the left earring.<br>
	 * This updates the {@code earringsLeft} field in the current object.
	 * @param earringsLeft The unique identifier for the left earring.
	 */
	public void setEarringsLeft(int earringsLeft)
	{
		this.earringsLeft = earringsLeft;
	}
	
	/**
	 * Retrieves the ID of the earring equipped in the right ear slot.<br>
	 * This value is stored as an {@code int}.
	 * @return The {@code int} value representing the right earring.
	 */
	public int getEarringsRight()
	{
		return earringsRight;
	}
	
	/**
	 * Sets the ID for the right earring.<br>
	 * This updates the {@code earringsRight} field in the current object.
	 * @param earringsRight The unique identifier for the right earring.
	 */
	public void setEarringsRight(int earringsRight)
	{
		this.earringsRight = earringsRight;
	}
	
	/**
	 * Retrieves the ID of the item equipped in the left ring slot.<br>
	 * This value is stored in the {@code ringLeft} field.
	 * @return The integer ID of the left ring.
	 */
	public int getRingLeft()
	{
		return ringLeft;
	}
	
	/**
	 * Sets the value for the left ring slot.<br>
	 * This updates the {@code ringLeft} field in the current object.
	 * @param ringLeft The new value to assign to the left ring slot.
	 */
	public void setRingLeft(int ringLeft)
	{
		this.ringLeft = ringLeft;
	}
	
	/**
	 * Retrieves the ID of the item equipped in the right ring slot.<br>
	 * This value is stored in the {@code ringRight} field.
	 * @return The integer ID of the right ring.
	 */
	public int getRingRight()
	{
		return ringRight;
	}
	
	/**
	 * Sets the value for the right ring equipment slot.<br>
	 * This updates the {@code ringRight} field of this object.
	 * @param ringRight The new value to assign to the right ring slot.
	 */
	public void setRingRight(int ringRight)
	{
		this.ringRight = ringRight;
	}
	
	/**
	 * Retrieves the current necklace value.<br>
	 * This method returns the integer ID of the necklace equipped by the player.
	 * @return The {@code int} value representing the necklace.
	 */
	public int getNecklace()
	{
		return necklace;
	}
	
	/**
	 * Updates the necklace value for this equipment setting.<br>
	 * This method assigns a new {@code int} to the internal necklace field.
	 * @param necklace The new necklace value to set.
	 */
	public void setNecklace(int necklace)
	{
		this.necklace = necklace;
	}
	
	/**
	 * Retrieves the current value for the shoulder equipment slot.<br>
	 * This method returns the {@code int} value stored in the shoulder field.
	 * @return The integer value of the shoulder setting.
	 */
	public int getShoulder()
	{
		return shoulder;
	}
	
	/**
	 * Updates the shoulder equipment value.<br>
	 * This method sets the {@code shoulder} field of the current object.
	 * @param shoulder The new integer value for the shoulder slot.
	 */
	public void setShoulder(int shoulder)
	{
		this.shoulder = shoulder;
	}
	
	/**
	 * Retrieves the current value for the pants equipment slot.<br>
	 * This method returns the integer stored in the {@code pants} field.
	 * @return The integer value of the pants equipment.
	 */
	public int getPants()
	{
		return pants;
	}
	
	/**
	 * Updates the {@code pants} value for this equipment setting.<br>
	 * This method sets the internal state of the pants property.
	 * @param pants The new integer value to assign to the pants field.
	 */
	public void setPants(int pants)
	{
		this.pants = pants;
	}
	
	/**
	 * Retrieves the value of the left powershard.<br>
	 * This method returns the current state of the {@code powershardLeft} field.
	 * @return The integer value of the left powershard.
	 */
	public int getPowershardLeft()
	{
		return powershardLeft;
	}
	
	/**
	 * Sets the value for the left powershard.<br>
	 * This updates the {@code powershardLeft} field in the current object.
	 * @param powershardLeft The new value to assign to the left powershard.
	 */
	public void setPowershardLeft(int powershardLeft)
	{
		this.powershardLeft = powershardLeft;
	}
	
	/**
	 * Retrieves the value of the right powershard.<br>
	 * This method returns the current state of the {@code powershardRight} field.
	 * @return The integer value for the right powershard.
	 */
	public int getPowershardRight()
	{
		return powershardRight;
	}
	
	/**
	 * Sets the value for the right powershard.<br>
	 * This updates the {@code powershardRight} field in the current object.
	 * @param powershardRight The new integer value to assign to the right powershard.
	 */
	public void setPowershardRight(int powershardRight)
	{
		this.powershardRight = powershardRight;
	}
	
	/**
	 * Retrieves the current wing equipment ID.<br>
	 * This value is stored in the {@code wings} field.
	 * @return The integer ID of the equipped wings.
	 */
	public int getWings()
	{
		return wings;
	}
	
	/**
	 * Sets the number of wings for the equipment.<br>
	 * This updates the {@code wings} field in this object.
	 * @param wings The number of wings to set.
	 */
	public void setWings(int wings)
	{
		this.wings = wings;
	}
	
	/**
	 * Retrieves the current waist size of the player.<br>
	 * This value is stored as an {@code int}.
	 * @return the waist size.
	 */
	public int getWaist()
	{
		return waist;
	}
	
	/**
	 * Sets the waist size for the player appearance.<br>
	 * This updates the {@code waist} field in the current object.
	 * @param waist The new value for the waist size.
	 */
	public void setWaist(int waist)
	{
		this.waist = waist;
	}
	
	/**
	 * Retrieves the ID of the main off-hand equipment.<br>
	 * This value is stored in the {@code mOffHand} field.
	 * @return The integer ID for the main off-hand slot.
	 */
	public int getmOffHand()
	{
		return mOffHand;
	}
	
	/**
	 * Sets the item ID for the main off-hand equipment slot.<br>
	 * This updates the {@code mOffHand} field in the current object.
	 * @param mOffHand The integer ID of the off-hand item.
	 */
	public void setmOffHand(int mOffHand)
	{
		this.mOffHand = mOffHand;
	}
	
	/**
	 * Retrieves the off-hand equipment value for the secondary hand.<br>
	 * This method returns the {@code sOffHand} integer from the current setting.
	 * @return The integer value of the secondary off-hand slot.
	 */
	public int getsOffHand()
	{
		return sOffHand;
	}
	
	/**
	 * Updates the secondary off-hand equipment value.<br>
	 * This method sets the {@code sOffHand} field to a new integer value.
	 * @param sOffHand The new value for the secondary off-hand slot.
	 */
	public void setsOffHand(int sOffHand)
	{
		this.sOffHand = sOffHand;
	}
	
	/**
	 * Retrieves the current value of the plume.<br>
	 * This value is part of the {@link EquipmentSetting} data.
	 * @return The integer value of the plume.
	 */
	public int getPlume()
	{
		return plume;
	}
	
	/**
	 * Sets the value for the {@code plume} field.<br>
	 * This updates the current equipment setting for a plume.
	 * @param plume The new integer value to assign to the plume.
	 */
	public void setPlume(int plume)
	{
		this.plume = plume;
	}
	
	/**
	 * Retrieves the current value of the bracelet equipment.<br>
	 * This method returns the integer stored in the {@code bracelet} field.
	 * @return The integer value of the bracelet.
	 */
	public int getBracelet()
	{
		return bracelet;
	}
	
	/**
	 * Updates the {@code bracelet} value for this equipment setting.<br>
	 * This method stores the provided integer in the internal field.
	 * @param bracelet The new value to assign to the bracelet slot.
	 */
	public void setBracelet(int bracelet)
	{
		this.bracelet = bracelet;
	}
}
