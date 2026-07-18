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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.TransformType;

/**
 * This packet handles the transformation of a {@link Creature} into a different form.<br>
 * It communicates changes related to {@link TransformType} to the client.
 * @author Sweetkr, xTz
 */
public class SM_TRANSFORM extends AionServerPacket
{
	private final Creature creature;
	private final int state;
	@SuppressWarnings("unused")
	private int modelId;
	private final boolean applyEffect;
	@SuppressWarnings("unused")
	private int panelId;
	@SuppressWarnings("unused")
	private int itemId;
	@SuppressWarnings("unused")
	private int skillId = 0;
	@SuppressWarnings("unused")
	private int transformId = 0;
	
	/**
	 * Creates a new {@code SM_TRANSFORM} packet for a specific creature.<br>
	 * This method initializes the default values for transformation data.<br>
	 * It sets the state and model ID based on the provided {@link Creature}.
	 * @param creature The {@code Creature} object to transform.
	 * @param applyEffect Set to {@code true} if visual effects should be applied, otherwise {@code false}.
	 */
	public SM_TRANSFORM(Creature creature, boolean applyEffect)
	{
		modelId = 0;
		panelId = 0;
		itemId = 0;
		skillId = 0;
		transformId = 0;
		this.creature = creature;
		state = creature.getState();
		modelId = creature.getTransformModel().getModelId();
		this.applyEffect = applyEffect;
	}
	
	/**
	 * Creates a new {@code SM_TRANSFORM} packet for a specific creature.<br>
	 * This constructor initializes the transformation data based on the provided parameters.
	 * @param creature The {@link Creature} object to be transformed.
	 * @param panelId The unique identifier for the transformation panel.
	 * @param applyEffect Set to {@code true} if visual effects should be triggered, otherwise {@code false}.
	 */
	public SM_TRANSFORM(Creature creature, int panelId, boolean applyEffect)
	{
		modelId = 0;
		panelId = 0;
		itemId = 0;
		skillId = 0;
		transformId = 0;
		this.creature = creature;
		state = creature.getState();
		modelId = creature.getTransformModel().getModelId();
		this.panelId = panelId;
		this.applyEffect = applyEffect;
	}
	
	/**
	 * This method creates a new {@code SM_TRANSFORM} packet.<br>
	 * It handles the transformation of a specific creature.<br>
	 * The packet includes details about the model and effects.
	 * @param creature The {@link Creature} being transformed.
	 * @param panelId The unique identifier for the panel.
	 * @param applyEffect Set to {@code true} to trigger visual effects.
	 * @param itemId The specific item ID associated with the transformation.
	 */
	public SM_TRANSFORM(Creature creature, int panelId, boolean applyEffect, int itemId)
	{
		modelId = 0;
		this.panelId = 0;
		this.itemId = 0;
		skillId = 0;
		transformId = 0;
		this.creature = creature;
		state = creature.getState();
		modelId = creature.getTransformModel().getModelId();
		this.panelId = panelId;
		this.applyEffect = applyEffect;
		this.itemId = itemId;
	}
	
	/**
	 * This method creates a new {@code SM_TRANSFORM} packet.<br>
	 * It handles the transformation of a specific creature.<br>
	 * The packet includes details about effects and item IDs.
	 * @param creature The {@link Creature} being transformed.
	 * @param panelId The ID of the panel associated with the transform.
	 * @param applyEffect Set to {@code true} if an effect should be applied, otherwise {@code false}.
	 * @param itemId The unique identifier for the item used in the transformation.
	 * @param skillId The unique identifier for the skill used in the transformation.
	 */
	public SM_TRANSFORM(Creature creature, int panelId, boolean applyEffect, int itemId, int skillId)
	{
		modelId = 0;
		this.panelId = 0;
		this.itemId = 0;
		this.skillId = 0;
		transformId = 0;
		this.creature = creature;
		state = creature.getState();
		modelId = creature.getTransformModel().getModelId();
		this.panelId = panelId;
		this.applyEffect = applyEffect;
		this.itemId = itemId;
		this.skillId = skillId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(creature.getObjectId());
		writeD(creature.getTransformModel().getModelId());
		writeH(state);
		writeF(0.25f);
		writeF(2.0f);
		writeC(applyEffect && (creature.getTransformModel().getType() == TransformType.NONE) ? 1 : 0);
		writeD(creature.getTransformModel().getType().getId());
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeD(creature.getTransformModel().getPanelId());
		writeD(creature.getTransformModel().getItemId());
		writeC(1); // 1 = normal, 0 = transparent
		writeH(creature.getTransformModel().getSkillId());
		writeD(creature.getTransformModel().getTransformId());
	}
}
