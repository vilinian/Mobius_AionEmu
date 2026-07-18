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
package system.handlers.admincommands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.ChatUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.WorldMapType;

import ch.qos.logback.classic.Logger;

/**
 * Handles the {@code Bk} admin command to teleport a player back to their previous location.<br>
 * It utilizes {@link TeleportService2} to manage the movement of the target {@link Player}.
 * @author Mrakobes
 * @modified antness
 */
public class Bk extends AdminCommand
{
	ArrayList<Bookmark> bookmarks = new ArrayList<>();
	private static final Logger log = (Logger) LoggerFactory.getLogger(Bk.class);
	private String bookmark_name = "";
	
	/**
	 * Initializes a new instance of the {@link Bk} class.<br>
	 * This constructor sets up the default command name as {@code bk}.
	 */
	public Bk()
	{
		super("bk");
	}
	
	/**
	 * Executes the bookmark command for a player.<br>
	 * It allows adding, deleting, teleporting to, or listing bookmarks.<br>
	 * The method parses parameters to determine which action to perform.
	 * @param player The {@code Player} executing the command.
	 * @param params Variable arguments containing the action and the bookmark name.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "syntax //bk <add|del|tele|list>");
			return;
		}
		
		if (params[0].equals("add"))
		{
			try
			{
				bookmark_name = params[1].toLowerCase();
				if (isBookmarkExists(bookmark_name, player.getObjectId()))
				{
					PacketSendUtility.sendMessage(player, "Bookmark " + bookmark_name + " already exists !");
					return;
				}
				
				final float x = player.getX();
				final float y = player.getY();
				final float z = player.getZ();
				final int char_id = player.getObjectId();
				final int world_id = player.getWorldId();
				
				DB.insertUpdate("INSERT INTO bookmark (" + "`name`,`char_id`, `x`, `y`, `z`,`world_id` )" + " VALUES " + "(?, ?, ?, ?, ?, ?)", ps ->
				{
					ps.setString(1, bookmark_name);
					ps.setInt(2, char_id);
					ps.setFloat(3, x);
					ps.setFloat(4, y);
					ps.setFloat(5, z);
					ps.setInt(6, world_id);
					ps.execute();
				});
				
				PacketSendUtility.sendMessage(player, "Bookmark " + bookmark_name + " sucessfully added to your bookmark list!");
				
				updateInfo(player.getObjectId());
			}
			catch (Exception e)
			{
				PacketSendUtility.sendMessage(player, "syntax //bk <add|del|tele> <bookmark name>");
				return;
			}
		}
		else if (params[0].equals("del"))
		{
			Connection con = null;
			try
			{
				bookmark_name = params[1].toLowerCase();
				con = DatabaseFactory.getConnection();
				
				final PreparedStatement statement = con.prepareStatement("DELETE FROM bookmark WHERE name = ?");
				statement.setString(1, bookmark_name);
				statement.executeUpdate();
				statement.close();
			}
			catch (Exception e)
			{
				PacketSendUtility.sendMessage(player, "syntax //bk <add|del|tele> <bookmark name>");
				return;
			}
			finally
			{
				DatabaseFactory.close(con);
				PacketSendUtility.sendMessage(player, "Bookmark " + bookmark_name + " sucessfully removed from your bookmark list!");
				updateInfo(player.getObjectId());
			}
		}
		else if (params[0].equals("tele"))
		{
			try
			{
				if (params[1].equals("") || (params[1] == null))
				{
					PacketSendUtility.sendMessage(player, "syntax //bk <add|del|tele> <bookmark name>");
					return;
				}
				
				updateInfo(player.getObjectId());
				
				bookmark_name = params[1].toLowerCase();
				Bookmark tele_bk = null;
				try
				{
					tele_bk = selectByName(bookmark_name);
				}
				finally
				{
					if (tele_bk != null)
					{
						TeleportService2.teleportTo(player, tele_bk.getWorld_id(), tele_bk.getX(), tele_bk.getY(), tele_bk.getZ());
						PacketSendUtility.sendMessage(player, "Teleported to bookmark " + tele_bk.getName() + " location");
					}
				}
			}
			catch (Exception e)
			{
				PacketSendUtility.sendMessage(player, "syntax //bk <add|del|tele> <bookmark name>");
				return;
			}
		}
		else if (params[0].equals("list"))
		{
			updateInfo(player.getObjectId());
			PacketSendUtility.sendMessage(player, "=====Bookmark list begin=====");
			for (Bookmark b : bookmarks)
			{
				final String chatLink = ChatUtil.position(b.getName(), b.getWorld_id(), b.getX(), b.getY(), b.getZ());
				PacketSendUtility.sendMessage(player, " = " + chatLink + " =  " + WorldMapType.getWorld(b.getWorld_id()) + "  ( " + b.getX() + " ," + b.getY() + " ," + b.getZ() + " )");
			}
			
			PacketSendUtility.sendMessage(player, "=====Bookmark list end=======");
		}
	}
	
	/**
	 * Refreshes the list of bookmarks for a specific character.<br>
	 * This method clears the current {@code bookmarks} list.<br>
	 * It then fetches new data from the database using the provided {@code objId}.
	 * @param objId The unique identifier of the character to update.
	 */
	public void updateInfo(int objId)
	{
		bookmarks.clear();
		
		DB.select("SELECT * FROM `bookmark` where char_id= ?", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, objId);
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final String name = rset.getString("name");
					final float x = rset.getFloat("x");
					final float y = rset.getFloat("y");
					final float z = rset.getFloat("z");
					final int world_id = rset.getInt("world_id");
					bookmarks.add(new Bookmark(x, y, z, world_id, name));
				}
			}
		});
	}
	
	/**
	 * Finds a {@code Bookmark} object by its name.<br>
	 * It searches through the list of all available bookmarks.<br>
	 * Returns {@code null} if no match is found.
	 * @param bk_name The name of the bookmark to search for.
	 * @return The matching {@code Bookmark} object or {@code null}.
	 */
	public Bookmark selectByName(String bk_name)
	{
		for (Bookmark b : bookmarks)
		{
			if (b.getName().equals(bk_name))
			{
				return b;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if a specific bookmark exists in the database.<br>
	 * It searches using both the name and the character ID.
	 * @param bk_name The name of the bookmark to search for.
	 * @param objId The unique identifier of the character.
	 * @return {@code true} if at least one bookmark is found, otherwise {@code false}.
	 */
	public boolean isBookmarkExists(String bk_name, int objId)
	{
		Connection con = null;
		int bkcount = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement statement = con.prepareStatement("SELECT count(id) as bkcount FROM bookmark WHERE ? = name AND char_id = ?");
			statement.setString(1, bk_name);
			statement.setInt(2, objId);
			final ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				bkcount = rset.getInt("bkcount");
			}
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			log.error("Error in reading db", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return bkcount > 0;
	}
	
	/**
	 * Handles the failure of an {@code execute} command.<br>
	 * It sends a syntax hint to the player.
	 * @param player The {@code Player} who attempted the command.
	 * @param message The error message associated with the failure.
	 */
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "syntax //bk <add|del|tele|list>");
	}
}

class Bookmark
{
	private final String name;
	private final float x;
	private final float y;
	private final float z;
	private final int world_id;
	
	/**
	 * Creates a new {@code Bookmark} object.<br>
	 * This constructor initializes the coordinates and world information.
	 * @param x The X coordinate of the bookmark.
	 * @param y The Y coordinate of the bookmark.
	 * @param z The Z coordinate of the bookmark.
	 * @param world_id The unique identifier for the world.
	 * @param name The display name of the bookmark.
	 */
	public Bookmark(float x, float y, float z, int world_id, String name)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.world_id = world_id;
		this.name = name;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value corresponds to the {@code world_id} of the bookmark.
	 * @return The integer ID of the world.
	 */
	public int getWorld_id()
	{
		return world_id;
	}
}
