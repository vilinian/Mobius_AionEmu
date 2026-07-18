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

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * Represents a gate object that allows players to move between different game areas.<br>
 * This class extends {@link SummonedObject} and handles the logic for group transitions.
 * @author LokiReborn
 */
public class GroupGate extends SummonedObject<Creature>
{
	/**
	 * Creates a new instance of a {@link GroupGate}.<br>
	 * This constructor initializes the gate with its required data.
	 * @param objId The unique identifier for the object.
	 * @param controller The {@code NpcController} that handles this object's logic.
	 * @param spawnTemplate The {@link SpawnTemplate} defining where it appears.
	 * @param objectTemplate The {@link NpcTemplate} defining its base properties.
	 */
	public GroupGate(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate)
	{
		super(objId, controller, spawnTemplate, objectTemplate, (byte) 1);
	}
	
	/**
	 * Checks if the specified {@code Creature} is an enemy of this object.<br>
	 * This method delegates the check to the {@code isEnemyFrom} method of the provided creature.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is an enemy, {@code false} otherwise.
	 */
	@Override
	public boolean isEnemy(Creature creature)
	{
		return getCreator().isEnemy(creature);
	}
	
	/**
	 * Retrieves the type of the NPC object.<br>
	 * This method returns a constant value representing a standard NPC.
	 * @return the {@code NpcObjectType} of this entity.
	 */
	@Override
	public NpcObjectType getNpcObjectType()
	{
		return NpcObjectType.GROUPGATE;
	}
}
