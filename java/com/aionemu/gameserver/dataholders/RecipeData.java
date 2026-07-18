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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link RecipeTemplate} objects.<br>
 * It manages the collection of all crafting recipes loaded from the game configuration.<br>
 * Use this class to access recipe information throughout the server.
 * @author ATracer, MrPoke, KID
 */
@XmlRootElement(name = "recipe_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecipeData
{
	@XmlElement(name = "recipe_template")
	protected List<RecipeTemplate> list;
	private TIntObjectHashMap<RecipeTemplate> recipeData;
	@XmlTransient
	private List<RecipeTemplate> elyos, asmos, any;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code recipeData}, {@code elyos}, {@code asmos}, and {@code any} collections from the {@code list}.<br>
	 * The {@code list} is set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		recipeData = new TIntObjectHashMap<>();
		elyos = new ArrayList<>();
		asmos = new ArrayList<>();
		any = new ArrayList<>();
		for (RecipeTemplate it : list)
		{
			recipeData.put(it.getId(), it);
			if (it.getAutoLearn() == 0)
			{
				continue;
			}
			
			switch (it.getRace())
			{
				case ASMODIANS:
					asmos.add(it);
					break;
				case ELYOS:
					elyos.add(it);
					break;
				case PC_ALL:
					any.add(it);
					break;
				default:
					break;
			}
		}
		
		list = null;
	}
	
	/**
	 * Retrieves a list of recipes that are automatically learned by a specific race.<br>
	 * It filters templates based on the provided {@code skillId} and {@code maxLevel}.<br>
	 * The method checks race-specific lists and general shared recipes.
	 * @param race The {@link Race} type of the character.
	 * @param skillId The unique identifier for the skill.
	 * @param maxLevel The maximum level threshold for the skill point.
	 * @return A {@code List} containing the matching {@link RecipeTemplate} objects.
	 */
	public List<RecipeTemplate> getAutolearnRecipes(Race race, int skillId, int maxLevel)
	{
		final List<RecipeTemplate> list = new ArrayList<>();
		switch (race)
		{
			case ASMODIANS:
				for (RecipeTemplate recipe : asmos)
				{
					if ((recipe.getSkillid() == skillId) && (recipe.getSkillpoint() <= maxLevel))
					{
						list.add(recipe);
					}
				}
				break;
			case ELYOS:
				for (RecipeTemplate recipe : elyos)
				{
					if ((recipe.getSkillid() == skillId) && (recipe.getSkillpoint() <= maxLevel))
					{
						list.add(recipe);
					}
				}
				break;
			default:
				break;
		}
		
		for (RecipeTemplate recipe : any)
		{
			if ((recipe.getSkillid() == skillId) && (recipe.getSkillpoint() <= maxLevel))
			{
				list.add(recipe);
			}
		}
		
		return list;
	}
	
	/**
	 * Retrieves a specific {@link RecipeTemplate} using its unique identifier.<br>
	 * This method looks up the template in the internal data map.
	 * @param id The unique integer ID of the recipe to find.
	 * @return The {@code RecipeTemplate} associated with the given ID, or {@code null} if not found.
	 */
	public RecipeTemplate getRecipeTemplateById(int id)
	{
		return recipeData.get(id);
	}
	
	/**
	 * Retrieves the collection of all available recipe templates.<br>
	 * This method returns a {@code TIntObjectHashMap} containing the data.
	 * @return A map where the keys are IDs and the values are {@link RecipeTemplate} objects.
	 */
	public TIntObjectHashMap<RecipeTemplate> getRecipeTemplates()
	{
		return recipeData;
	}
	
	/**
	 * Returns the total number of recipes in this collection.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return recipeData.size();
	}
}
