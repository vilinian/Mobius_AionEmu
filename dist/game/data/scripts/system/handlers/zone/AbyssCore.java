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
package system.handlers.zone;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.controllers.observer.CollisionDieActor;
import com.aionemu.gameserver.geoEngine.GeoWorldLoader;
import com.aionemu.gameserver.geoEngine.math.Matrix3f;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.Node;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;

/**
 * Handles the specific logic and behaviors for the {@code CORE_400010000} zone.<br>
 * This class manages environmental interactions and rules unique to the Abyss Core area.
 * @author MrPoke
 */
@ZoneNameAnnotation("CORE_400010000")
public class AbyssCore implements ZoneHandler
{
	Map<Integer, CollisionDieActor> observed = new HashMap<>();
	private Node geometry;
	
	/**
	 * Initializes the {@code AbyssCore} zone handler.<br>
	 * This constructor loads the required 3D geometry from the file system.<br>
	 * It sets the initial transform and updates the model bounds for the scene.
	 */
	public AbyssCore()
	{
		try
		{
			geometry = (Node) GeoWorldLoader.loadMeshs("data/geo/models/na_ab_lmark_col_01a.mesh").values().toArray()[0];
			geometry.setTransform(new Matrix3f(1.15f, 0, 0, 0, 1.15f, 0, 0, 0, 1.15f), new Vector3f(2140.104f, 1925.5823f, 2303.919f), 1f);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		geometry.updateModelBound();
	}
	
	/**
	 * Handles logic when a {@code Creature} enters a specific {@code ZoneInstance}.<br>
	 * This method checks if the creature is a non-GM {@link Player}.<br>
	 * It kills players who enter the wrong faction base.
	 * @param creature The {@code Creature} entering the zone.
	 * @param zone The {@code ZoneInstance} being entered.
	 */
	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone)
	{
		final Creature acting = creature.getActingCreature();
		if ((acting instanceof Player) && !((Player) acting).isGM())
		{
			final CollisionDieActor observer = new CollisionDieActor(creature, geometry);
			creature.getObserveController().addObserver(observer);
			observed.put(creature.getObjectId(), observer);
		}
	}
	
	/**
	 * This method is called when a {@link Creature} leaves a {@link ZoneInstance}.<br>
	 * It removes the observer from the creature if it is a non-GM player.<br>
	 * The logic ensures that observation effects are cleared correctly.
	 * @param creature The {@link Creature} that is leaving the zone.
	 * @param zone The {@link ZoneInstance} being exited.
	 */
	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone)
	{
		final Creature acting = creature.getActingCreature();
		if ((acting instanceof Player) && !((Player) acting).isGM())
		{
			final CollisionDieActor observer = observed.get(creature.getObjectId());
			if (observer != null)
			{
				creature.getObserveController().removeObserver(observer);
				observed.remove(creature.getObjectId());
			}
		}
	}
}
