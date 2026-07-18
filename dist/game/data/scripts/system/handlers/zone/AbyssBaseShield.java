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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;

/**
 * Handles the logic for the base shield in the Abyss zone.<br>
 * This class manages specific behaviors for players and creatures within the {@code ASMODIANS_BASE_400010000} and {@code ELYOS_BASE_400010000} areas.
 * @author MrPoke
 */
@ZoneNameAnnotation("ASMODIANS_BASE_400010000 ELYOS_BASE_400010000")
public class AbyssBaseShield implements ZoneHandler
{
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
		final Creature actingCreature = creature.getActingCreature();
		if ((actingCreature instanceof Player) && !((Player) actingCreature).isGM())
		{
			final ZoneName currZone = zone.getZoneTemplate().getName();
			if (currZone == ZoneName.get("ASMODIANS_BASE_400010000"))
			{
				if (((Player) actingCreature).getRace() == Race.ELYOS)
				{
					creature.getController().die();
				}
			}
			else if (currZone == ZoneName.get("ELYOS_BASE_400010000"))
			{
				if (((Player) actingCreature).getRace() == Race.ASMODIANS)
				{
					creature.getController().die();
				}
			}
		}
	}
	
	/**
	 * This method is called when a {@code Creature} leaves a specific {@link ZoneInstance}.<br>
	 * It handles any logic required to update the game state after the player exits.
	 * @param player The {@code Creature} that is leaving the zone.
	 * @param zone The {@link ZoneInstance} that was just exited.
	 */
	@Override
	public void onLeaveZone(Creature player, ZoneInstance zone)
	{
	}
}
