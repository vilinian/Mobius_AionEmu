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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.GroupGate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for summoning a {@link GroupGate} as part of a skill effect.<br>
 * It utilizes the {@link SpawnEngine} to create the gate at the specified location.
 * @author LokiReborn
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonGroupGateEffect")
public class SummonGroupGateEffect extends SummonEffect
{
	/**
	 * This method applies a specific {@code Effect} to the game world.<br>
	 * It creates a new {@link GroupGate} at the effector's location.<br>
	 * A task is scheduled to delete the gate after a set duration.
	 * @param effect The {@code Effect} object containing the spawn data.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		final Creature effector = effect.getEffector();
		final float x = effector.getX();
		final float y = effector.getY();
		final float z = effector.getZ();
		final byte heading = effector.getHeading();
		final int worldId = effector.getWorldId();
		final int instanceId = effector.getInstanceId();
		
		final SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
		final GroupGate groupgate = VisibleObjectSpawner.spawnGroupGate(spawn, instanceId, effector);
		
		final Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				groupgate.getController().onDelete();
			}
		}, time * 1000);
		groupgate.getController().addTask(TaskId.DESPAWN, task);
	}
}
