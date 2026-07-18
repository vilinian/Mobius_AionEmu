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
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 * This class manages the configuration settings for game compositions.<br>
 * It provides a structured way to load and access composition-related properties from the server configuration files.
 * @Author FrozenKiller
 */
public class CompositionConfig
{
	/**
	 * Composition Configs
	 */
	@Property(key = "gameserver.composition.speed", defaultValue = "5000")
	public static int COMPOSITION_SPEED;
	@Property(key = "gameserver.composition.rnd.min", defaultValue = "1")
	public static int COMPOSITION_RND_MIN;
	@Property(key = "gameserver.composition.rnd.max", defaultValue = "10")
	public static int COMPOSITION_RND_MAX;
	@Property(key = "gameserver.composition.stone.quantity", defaultValue = "1")
	public static int COMPOSITION_STONE_QUANTITY;
}
