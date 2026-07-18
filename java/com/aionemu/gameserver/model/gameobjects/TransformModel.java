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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.TransformType;

/**
 * Represents the data model for a character's transformation state.<br>
 * It stores information regarding {@link TransformType} and associated visual properties.<br>
 * This class is used by {@link Player} objects to manage temporary shape changes.
 * @author Rolandas
 */
public class TransformModel
{
	private int modelId;
	private final int originalModelId;
	private TransformType originalType;
	private TransformType transformType;
	private int panelId;
	private boolean isActive = false;
	private TribeClass transformTribe;
	private TribeClass overrideTribe;
	private int ItemId;
	private int skillId;
	private int transformId;
	
	/**
	 * Creates a new {@link TransformModel} based on an existing {@code Creature}.<br>
	 * This constructor initializes the original model ID and determines the starting transform type.<br>
	 * It sets the initial state to {@code TransformType.NONE}.
	 * @param creature The {@code Creature} used to initialize the transformation data.
	 */
	public TransformModel(Creature creature)
	{
		if (creature instanceof Player)
		{
			originalType = TransformType.PC;
		}
		else
		{
			originalType = TransformType.NONE;
		}
		
		originalModelId = creature.getObjectTemplate().getTemplateId();
		transformType = TransformType.NONE;
	}
	
	/**
	 * Retrieves the current visual model identifier.<br>
	 * It returns {@code modelId} if the transform is active and valid.<br>
	 * Otherwise, it returns the {@code originalModelId}.
	 * @return The integer ID of the model to be displayed.
	 */
	public int getModelId()
	{
		if (isActive && (modelId > 0))
		{
			return modelId;
		}
		
		return originalModelId;
	}
	
	/**
	 * Sets the unique identifier for the visual model.<br>
	 * This method updates the {@code modelId} field based on the provided value.<br>
	 * If the input is {@code 0} or matches the original ID, it reverts to the default state.
	 * @param modelId The new model identifier to assign.
	 */
	public void setModelId(int modelId)
	{
		if ((modelId == 0) || (modelId == originalModelId))
		{
			modelId = originalModelId;
			isActive = false;
		}
		else
		{
			this.modelId = modelId;
			isActive = true;
		}
	}
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		if (ItemId > 0)
		{
			return ItemId;
		}
		
		return 0;
	}
	
	/**
	 * Sets the unique identifier for the item.<br>
	 * This method updates the {@code ItemId} field.
	 * @param itemId The new ID to assign to the item.
	 */
	public void setItemId(int itemId)
	{
		if (itemId == 0)
		{
			ItemId = 0;
		}
		else
		{
			ItemId = itemId;
		}
	}
	
	/**
	 * Retrieves the current {@code TransformType} of this model.<br>
	 * It returns the active type if the transformation is enabled.<br>
	 * Otherwise, it returns the original type.
	 * @return The current {@code TransformType}.
	 */
	public TransformType getType()
	{
		if (isActive)
		{
			return transformType;
		}
		
		return originalType;
	}
	
	/**
	 * Sets the type of transformation for this model.<br>
	 * This updates the {@code transformType} field.
	 * @param transformType The new {@link TransformType} to assign.
	 */
	public void setTransformType(TransformType transformType)
	{
		this.transformType = transformType;
	}
	
	/**
	 * Retrieves the unique identifier for the current panel.<br>
	 * This method checks if the transform is active first.<br>
	 * It returns {@code 0} if the transform is not currently active.
	 * @return The {@code int} value of the panel ID or {@code 0}.
	 */
	public int getPanelId()
	{
		if (isActive)
		{
			return panelId;
		}
		
		return 0;
	}
	
	/**
	 * Sets the unique identifier for the panel.<br>
	 * This updates the {@code panelId} field of the {@link TransformModel}.
	 * @param id The new integer value to assign to the panel.
	 */
	public void setPanelId(int id)
	{
		panelId = id;
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
	 * Updates the active status of this {@link TransformModel}.<br>
	 * Sets the internal state to {@code true} or {@code false}.
	 * @param isActive The new status to set for the transform.
	 */
	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}
	
	/**
	 * Retrieves the {@link TribeClass} for this transformation.<br>
	 * It returns the active tribe if available.<br>
	 * Otherwise, it returns the override tribe.
	 * @return the {@code TribeClass} associated with this entity.
	 */
	public TribeClass getTribe()
	{
		if (isActive && (transformTribe != null))
		{
			return transformTribe;
		}
		
		return overrideTribe;
	}
	
	/**
	 * Sets the tribe for this transformation.<br>
	 * Use {@code true} to set the override tribe.<br>
	 * Use {@code false} to set the standard transformation tribe.
	 * @param transformTribe The {@link TribeClass} to assign.
	 * @param override Whether to apply the value to the override field.
	 */
	public void setTribe(TribeClass transformTribe, boolean override)
	{
		if (override)
		{
			overrideTribe = transformTribe;
		}
		else
		{
			this.transformTribe = transformTribe;
		}
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * Sets the unique identifier for the skill.<br>
	 * This updates the {@code skillId} field of the current {@link TransformModel}.
	 * @param skillId The new ID to assign to the skill.
	 */
	public void setSkillId(int skillId)
	{
		this.skillId = skillId;
	}
	
	/**
	 * Retrieves the unique identifier for this transformation.<br>
	 * This ID is used to identify specific {@link TransformModel} types.
	 * @return The current {@code transformId}.
	 */
	public int getTransformId()
	{
		return transformId;
	}
	
	/**
	 * Sets the unique identifier for this transformation.<br>
	 * This value is used to identify specific types of transforms in the system.
	 * @param transformId The {@code int} ID of the transformation to assign.
	 */
	public void setTransformId(int transformId)
	{
		this.transformId = transformId;
	}
}
