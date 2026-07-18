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
package com.aionemu.gameserver.dataholders;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.stats.CalculatedPlayerStatsTemplate;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class acts as a data holder for player statistics templates.<br>
 * It manages the collection of {@link PlayerStatsTemplate} objects used by the server.<br>
 * These templates are used to define and calculate various stats for players.
 * @author Aquanox
 */
@XmlRootElement(name = "player_stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerStatsData
{
	@XmlElement(name = "player_stats", required = true)
	private List<PlayerStatsType> templatesList = new ArrayList<>();
	private final TIntObjectHashMap<PlayerStatsTemplate> playerTemplates = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It processes the {@code templatesList} to calculate stats and populate the {@code playerTemplates} map.<br>
	 * The list of templates is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PlayerStatsType pt : templatesList)
		{
			final int code = makeHash(pt.getRequiredPlayerClass(), pt.getRequiredLevel());
			final PlayerStatsTemplate template = pt.getTemplate();
			
			// TODO move to DP
			template.setMaxMp(Math.round((template.getMaxMp() * 100f) / template.getWill()));
			template.setMaxHp(Math.round((template.getMaxHp() * 100f) / template.getHealth()));
			int agility = template.getAgility();
			agility = (agility - 100);
			template.setEvasion(Math.round(template.getEvasion() - (template.getEvasion() * agility * 0.003f)));
			template.setBlock(Math.round(template.getBlock() - (template.getBlock() * agility * 0.0025f)));
			template.setParry(Math.round(template.getParry() - (template.getParry() * agility * 0.0025f)));
			template.setStrikeResist(template.getStrikeResist());
			template.setSpellResist(template.getSpellResist());
			playerTemplates.put(code, pt.getTemplate());
		}
		
		/**
		 * for unknown templates *
		 */
		playerTemplates.put(makeHash(PlayerClass.WARRIOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.WARRIOR));
		playerTemplates.put(makeHash(PlayerClass.ASSASSIN, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ASSASSIN));
		playerTemplates.put(makeHash(PlayerClass.CHANTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CHANTER));
		playerTemplates.put(makeHash(PlayerClass.CLERIC, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CLERIC));
		playerTemplates.put(makeHash(PlayerClass.GLADIATOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.GLADIATOR));
		playerTemplates.put(makeHash(PlayerClass.MAGE, 0), new CalculatedPlayerStatsTemplate(PlayerClass.MAGE));
		playerTemplates.put(makeHash(PlayerClass.PRIEST, 0), new CalculatedPlayerStatsTemplate(PlayerClass.PRIEST));
		playerTemplates.put(makeHash(PlayerClass.RANGER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.RANGER));
		playerTemplates.put(makeHash(PlayerClass.SCOUT, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SCOUT));
		playerTemplates.put(makeHash(PlayerClass.SORCERER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SORCERER));
		playerTemplates.put(makeHash(PlayerClass.SPIRIT_MASTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SPIRIT_MASTER));
		playerTemplates.put(makeHash(PlayerClass.TEMPLAR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.TEMPLAR));
		playerTemplates.put(makeHash(PlayerClass.ARTIST, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ARTIST));
		playerTemplates.put(makeHash(PlayerClass.BARD, 0), new CalculatedPlayerStatsTemplate(PlayerClass.BARD)); // 4.3
		playerTemplates.put(makeHash(PlayerClass.ENGINEER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ENGINEER)); // 4.3
		playerTemplates.put(makeHash(PlayerClass.GUNNER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.GUNNER)); // 4.3
		playerTemplates.put(makeHash(PlayerClass.RIDER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.RIDER)); // 4.5
		playerTemplates.put(makeHash(PlayerClass.PAINTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.PAINTER)); // 6.x
		templatesList.clear();
		templatesList = null;
	}
	
	/**
	 * Retrieves the correct {@link PlayerStatsTemplate} for a specific player.<br>
	 * It looks up the template based on the player's class and level.<br>
	 * If no template is found for that level, it defaults to level 0.
	 * @param player The {@code Player} object used to determine the stats.
	 * @return The matching {@code PlayerStatsTemplate} or {@code null}.
	 */
	public PlayerStatsTemplate getTemplate(Player player)
	{
		PlayerStatsTemplate template = getTemplate(player.getCommonData().getPlayerClass(), player.getLevel());
		if (template == null)
		{
			template = getTemplate(player.getCommonData().getPlayerClass(), 0);
		}
		
		return template;
	}
	
	/**
	 * Retrieves the statistics template for a specific class and level.<br>
	 * If no template exists for the given {@code level}, it returns the template for level {@code 0}.
	 * @param playerClass The {@link PlayerClass} of the character.
	 * @param level The current level of the character.
	 * @return The corresponding {@link PlayerStatsTemplate} object.
	 */
	public PlayerStatsTemplate getTemplate(PlayerClass playerClass, int level)
	{
		PlayerStatsTemplate template = playerTemplates.get(makeHash(playerClass, level));
		if (template == null)
		{
			template = getTemplate(playerClass, 0);
		}
		
		return template;
	}
	
	/**
	 * Returns the total number of templates stored in this data holder.<br>
	 * This method calls {@code size} to get the count.
	 * @return The number of items currently in the collection.
	 */
	public int size()
	{
		return playerTemplates.size();
	}
	
	@XmlRootElement(name = "playerStatsTemplateType")
	private static class PlayerStatsType
	{
		@XmlAttribute(name = "class", required = true)
		private PlayerClass requiredPlayerClass;
		@XmlAttribute(name = "level", required = true)
		private int requiredLevel;
		@XmlElement(name = "stats_template")
		private PlayerStatsTemplate template;
		
		public PlayerClass getRequiredPlayerClass()
		{
			return requiredPlayerClass;
		}
		
		public int getRequiredLevel()
		{
			return requiredLevel;
		}
		
		public PlayerStatsTemplate getTemplate()
		{
			return template;
		}
	}
	
	/**
	 * Generates a unique hash for a specific character configuration.<br>
	 * This method combines the {@code level} and the {@link PlayerClass} ordinal.<br>
	 * It is used to quickly identify templates in internal maps.
	 * @param playerClass The class of the player.
	 * @param level The current level of the player.
	 * @return A unique integer hash representing the combination of class and level.
	 */
	private static int makeHash(PlayerClass playerClass, int level)
	{
		return (level << 11) | playerClass.ordinal();
	}
}
