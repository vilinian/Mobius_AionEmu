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
package system.database.mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerRecipesDAO;
import com.aionemu.gameserver.model.gameobjects.player.RecipeList;

/**
 * This class provides the database access layer for managing player recipes using {@code MySQL5}.<br>
 * It extends {@link PlayerRecipesDAO} to handle specific SQL queries for recipe data.
 * @author lord_rex
 */
public class MySQL5PlayerRecipesDAO extends PlayerRecipesDAO
{
	private static final String SELECT_QUERY = "SELECT `recipe_id` FROM player_recipes WHERE `player_id`=?";
	private static final String ADD_QUERY = "INSERT INTO player_recipes (`player_id`, `recipe_id`) VALUES (?, ?)";
	private static final String DELETE_QUERY = "DELETE FROM player_recipes WHERE `player_id`=? AND `recipe_id`=?";
	
	/**
	 * Retrieves the list of recipes for a specific player.<br>
	 * This method fetches all {@code recipe_id} values from the database.<br>
	 * It returns a new {@link RecipeList} object containing the results.
	 * @param playerId The unique identifier of the player to load.
	 * @return A {@code RecipeList} containing the player's recipes.
	 */
	@Override
	public RecipeList load(int playerId)
	{
		final HashSet<Integer> recipeList = new HashSet<>();
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement ps) throws SQLException
			{
				ps.setInt(1, playerId);
			}
			
			@Override
			public void handleRead(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					recipeList.add(rs.getInt("recipe_id"));
				}
			}
		});
		
		return new RecipeList(recipeList);
	}
	
	/**
	 * Adds a new recipe to a specific player's collection.<br>
	 * This method updates the database records for the given {@code playerId}.
	 * @param playerId The unique identifier of the player.
	 * @param recipeId The unique identifier of the recipe to add.
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	@Override
	public boolean addRecipe(int playerId, int recipeId)
	{
		return DB.insertUpdate(ADD_QUERY, ps ->
		{
			ps.setInt(1, playerId);
			ps.setInt(2, recipeId);
			ps.execute();
		});
	}
	
	/**
	 * Removes a specific recipe from a player's collection.<br>
	 * This method updates the database by deleting the record matching both IDs.
	 * @param playerId The unique identifier of the player.
	 * @param recipeId The unique identifier of the recipe to remove.
	 * @return {@code true} if the deletion was successful, otherwise {@code false}.
	 */
	@Override
	public boolean delRecipe(int playerId, int recipeId)
	{
		return DB.insertUpdate(DELETE_QUERY, ps ->
		{
			ps.setInt(1, playerId);
			ps.setInt(2, recipeId);
			ps.execute();
		});
	}
	
	/**
	 * Checks if the current database system supports a specific feature.<br>
	 * This method delegates the check to {@code int, int)}.
	 * @param s The name of the feature to check.
	 * @param i The first integer parameter for the feature.
	 * @param i1 The second integer parameter for the feature.
	 * @return {@code true} if the feature is supported, {@code false} otherwise.
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
