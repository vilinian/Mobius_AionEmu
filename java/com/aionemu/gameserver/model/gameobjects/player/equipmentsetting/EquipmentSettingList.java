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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerEquipmentSettingDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class manages a collection of {@link Player} equipment settings.<br>
 * It provides a structured way to store and retrieve specific gear configurations for players.
 * @author Falke_34
 */
public class EquipmentSettingList
{
	private Map<Integer, EquipmentSetting> equipmentSetting;
	private final Player owner;
	
	/**
	 * Creates a new {@link EquipmentSettingList} for a specific player.<br>
	 * This constructor initializes the list with the provided {@code owner}.
	 * @param owner The {@link Player} who owns these equipment settings.
	 */
	public EquipmentSettingList(final Player owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Adds a new {@link EquipmentSetting} to the player's collection.<br>
	 * This method creates a new entry and saves it to the database.
	 * @param slot The unique identifier for the equipment slot.
	 * @param display The display ID of the item.
	 * @param mHand Whether the item is held in the main hand.
	 * @param sHand Whether the item is held in the secondary hand.
	 * @param helmet The helmet item ID.
	 * @param torso The torso item ID.
	 * @param glove The glove item ID.
	 * @param boots The boots item ID.
	 * @param earringsLeft The left earring item ID.
	 * @param earringsRight The right earring item ID.
	 * @param ringLeft The left ring item ID.
	 * @param ringRight The right ring item ID.
	 * @param necklace The necklace item ID.
	 * @param shoulder The shoulder item ID.
	 * @param pants The pants item ID.
	 * @param powershardLeft The left powershard item ID.
	 * @param powershardRight The right powershard item ID.
	 * @param wings The wings item ID.
	 * @param waist The
	 * @param mOffHand
	 * @param sOffHand
	 * @param plume
	 * @param bracelet
	 * @param isNew
	 */
	public void add(int slot, int display, int mHand, int sHand, int helmet, int torso, int glove, int boots, int earringsLeft, int earringsRight, int ringLeft, int ringRight, int necklace, int shoulder, int pants, int powershardLeft, int powershardRight, int wings, int waist, int mOffHand, int sOffHand, int plume, int bracelet, boolean isNew)
	{
		if (equipmentSetting == null)
		{
			equipmentSetting = new HashMap<>();
		}
		
		final EquipmentSetting equipmentSettings = new EquipmentSetting(slot, display, mHand, sHand, helmet, torso, glove, boots, earringsLeft, earringsRight, ringLeft, ringRight, necklace, shoulder, pants, powershardLeft, powershardRight, wings, waist, mOffHand, sOffHand, plume, bracelet);
		equipmentSetting.put(slot, equipmentSettings);
		DAOManager.getDAO(PlayerEquipmentSettingDAO.class).insertEquipmentSetting(owner, equipmentSettings);
	}
	
	/**
	 * Retrieves all the current equipment settings.<br>
	 * This method returns a {@code Collection} of {@link EquipmentSetting} objects.<br>
	 * If no settings exist, it returns an empty list instead of {@code null}.
	 * @return A collection containing all {@code EquipmentSetting} items.
	 */
	public Collection<EquipmentSetting> getEquipmentSetting()
	{
		if (equipmentSetting == null)
		{
			return Collections.emptyList();
		}
		
		return equipmentSetting.values();
	}
}
