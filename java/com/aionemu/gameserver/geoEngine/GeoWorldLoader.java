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
package com.aionemu.gameserver.geoEngine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.bounding.BoundingVolume;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Matrix3f;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.models.GeoMap;
import com.aionemu.gameserver.geoEngine.scene.Geometry;
import com.aionemu.gameserver.geoEngine.scene.Mesh;
import com.aionemu.gameserver.geoEngine.scene.Node;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.geoEngine.scene.VertexBuffer;
import com.aionemu.gameserver.geoEngine.scene.mesh.DoorGeometry;
import com.aionemu.gameserver.model.templates.materials.MaterialTemplate;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.ZoneService;

/**
 * This class is responsible for loading world geometry data from files into the game engine.<br>
 * It parses spatial data to construct {@link Mesh} and {@link Geometry} objects for the game world.
 * @author Mr. Poke
 */
public class GeoWorldLoader
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(GeoWorldLoader.class);
	private static String GEO_DIR = "data/geo/";
	private static boolean DEBUG = false;
	
	/**
	 * Enables or disables the debug mode for the geo engine.<br>
	 * This updates the internal {@code DEBUG} flag.
	 * @param debug The value to set for the debug mode. Set to {@code true} to enable it and {@code false} to disable it.
	 */
	public static void setDebugMod(boolean debug)
	{
		DEBUG = debug;
	}
	
	/**
	 * Loads 3D meshes from a specified geometry file.<br>
	 * This method parses the file to create {@link Spatial} objects for each model.<br>
	 * It handles collision data and door geometries during the loading process.
	 * @param fileName The path to the geometry file to be loaded.
	 * @return A map where keys are names and values are the loaded {@link Spatial} objects.
	 * @throws IOException If there is an error reading the file.
	 */
	public static Map<String, Spatial> loadMeshs(String fileName) throws IOException
	{
		final Map<String, Spatial> geoms = new HashMap<>();
		final File geoFile = new File(fileName);
		try (RandomAccessFile geoFileAccess = new RandomAccessFile(geoFile, "r");
			FileChannel roChannel = geoFileAccess.getChannel();
			Arena arena = Arena.ofConfined())
		{
			final int size = (int) roChannel.size();
			final ByteBuffer geo = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, size, arena).asByteBuffer();
			geo.order(ByteOrder.LITTLE_ENDIAN);
			while (geo.hasRemaining())
			{
				final short namelenght = geo.getShort();
				final byte[] nameByte = new byte[namelenght];
				geo.get(nameByte);
				final String name = new String(nameByte).intern();
				final Node node = new Node(DEBUG ? name : null);
				byte intentions = 0;
				byte singleChildMaterialId = -1;
				final int modelCount = geo.getShort();
				for (int c = 0; c < modelCount; c++)
				{
					final Mesh m = new Mesh();
					
					final int vectorCount = (geo.getInt()) * 3;
					final ByteBuffer floatBuffer = ByteBuffer.allocateDirect(vectorCount * 4);
					final FloatBuffer vertices = floatBuffer.asFloatBuffer();
					for (int x = 0; x < vectorCount; x++)
					{
						vertices.put(geo.getFloat());
					}
					
					final int triangles = geo.getInt();
					final ByteBuffer shortBuffer = ByteBuffer.allocateDirect(triangles * 2);
					final ShortBuffer indexes = shortBuffer.asShortBuffer();
					for (int x = 0; x < triangles; x++)
					{
						indexes.put(geo.getShort());
					}
					
					Geometry geom = null;
					m.setCollisionFlags(geo.getShort());
					if ((m.getIntentions() & CollisionIntention.MOVEABLE.getId()) != 0)
					{
						// TODO: skip moveable collisions (ships, shugo boxes), not handled yet
						continue;
					}
					
					intentions |= m.getIntentions();
					m.setBuffer(VertexBuffer.Type.Position, 3, vertices);
					m.setBuffer(VertexBuffer.Type.Index, 3, indexes);
					m.createCollisionData();
					
					if (((intentions & CollisionIntention.DOOR.getId()) != 0) && ((intentions & CollisionIntention.PHYSICAL.getId()) != 0))
					{
						if (!GeoDataConfig.GEO_DOORS_ENABLE)
						{
							continue;
						}
						
						geom = new DoorGeometry(name, m);
						// what if doors have few models ?
					}
					else
					{
						final MaterialTemplate mtl = DataManager.MATERIAL_DATA.getTemplate(m.getMaterialId());
						geom = new Geometry(null, m);
						if ((mtl != null) || (m.getMaterialId() == 11))
						{
							node.setName(name);
						}
						
						if (modelCount == 1)
						{
							geom.setName(name);
							singleChildMaterialId = geom.getMaterialId();
						}
						else
						{
							geom.setName(("child" + c + "_" + name).intern());
						}
						
						node.attachChild(geom);
					}
					
					geoms.put(geom.getName(), geom);
				}
				
				node.setCollisionFlags((short) ((intentions << 8) | (singleChildMaterialId & 0xFF)));
				if (!node.getChildren().isEmpty())
				{
					geoms.put(name, node);
				}
			}
		}
		return geoms;
	}
	
	/**
	 * Loads world data from a file based on the provided ID.<br>
	 * This method populates the {@link GeoMap} with terrain and spatial models.<br>
	 * It handles geometry placement, door creation, and zone generation.
	 * @param worldId The unique identifier for the world file to load.
	 * @param models A map of model names to their corresponding {@link Spatial} objects.
	 * @param map The {@link GeoMap} instance where the loaded data will be stored.
	 * @return {@code true} if the world was loaded successfully.
	 * @throws IOException If there is an error reading the world file.
	 */
	public static boolean loadWorld(int worldId, Map<String, Spatial> models, GeoMap map) throws IOException
	{
		final File geoFile = new File(GEO_DIR + worldId + ".geo");
		try (RandomAccessFile geoFileAccess = new RandomAccessFile(geoFile, "r");
			FileChannel roChannel = geoFileAccess.getChannel();
			Arena arena = Arena.ofConfined())
		{
			final ByteBuffer geo = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) roChannel.size(), arena).asByteBuffer();
			geo.order(ByteOrder.LITTLE_ENDIAN);
			if (geo.get() == 0)
			{
				// no terrain
				map.setTerrainData(new short[]
				{
					geo.getShort()
				});
				/* int cutoutSize = */ geo.getInt();
			}
			else
			{
				final int size = geo.getInt();
				final short[] terrainData = new short[size];
				for (int i = 0; i < size; i++)
				{
					terrainData[i] = geo.getShort();
				}
				
				map.setTerrainData(terrainData);
				
				// read list of terrain indexes to remove.
				final int cutoutSize = geo.getInt();
				if (cutoutSize > 0)
				{
					final int[] cutoutData = new int[cutoutSize];
					for (int i = 0; i < cutoutSize; i++)
					{
						cutoutData[i] = geo.getInt();
					}
					
					map.setTerrainCutouts(cutoutData);
				}
			}
			
			while (geo.hasRemaining())
			{
				final int nameLength = geo.getShort();
				final byte[] nameByte = new byte[nameLength];
				geo.get(nameByte);
				final String name = new String(nameByte);
				final Vector3f loc = new Vector3f(geo.getFloat(), geo.getFloat(), geo.getFloat());
				final float[] matrix = new float[9];
				for (int i = 0; i < 9; i++)
				{
					matrix[i] = geo.getFloat();
				}
				
				final float scale = geo.getFloat();
				geo.get(); // TODO : use the data: EventType eventType = EventType.fromByte(geo.get());
				final Matrix3f matrix3f = new Matrix3f();
				matrix3f.set(matrix);
				final Spatial node = models.get(name.toLowerCase().intern());
				try
				{
					if (node != null)
					{
						Spatial nodeClone = node;
						if (node instanceof DoorGeometry)
						{
							try
							{
								nodeClone = node.clone();
							}
							catch (CloneNotSupportedException e)
							{
								e.printStackTrace();
							}
							
							createDoors(nodeClone, worldId, matrix3f, loc, scale);
							map.attachChild(nodeClone);
						}
						else
						{
							nodeClone = attachChild(map, node, matrix3f, loc, scale);
							final List<Spatial> children = ((Node) node).descendantMatches("child\\d+_" + name.replace("\\", "\\\\"));
							if (children.size() == 0)
							{
								createZone(nodeClone, worldId, 0);
							}
							else
							{
								for (int c = 0; c < children.size(); c++)
								{
									final Spatial child = children.get(c);
									nodeClone = attachChild(map, child, matrix3f, loc, scale);
									createZone(nodeClone, worldId, c + 1);
								}
							}
						}
					}
				}
				catch (Throwable t)
				{
					System.out.println(t);
				}
			}
		}
		map.updateModelBound();
		return true;
	}
	
	/**
	 * Attaches a new child {@link Spatial} to the provided {@link GeoMap}.<br>
	 * This method clones the input node and applies a transformation.<br>
	 * It updates the model bounds before adding it to the map.
	 * @param map The {@link GeoMap} where the child will be attached.
	 * @param node The original {@link Spatial} used as a template for the new child.
	 * @param matrix The rotation and orientation matrix to apply.
	 * @param location The 3D coordinates for the new position.
	 * @param scale The size multiplier for the new child.
	 * @return The newly created and attached {@link Spatial} object.
	 */
	private static Spatial attachChild(GeoMap map, Spatial node, Matrix3f matrix, Vector3f location, float scale)
	{
		Spatial nodeClone = node;
		try
		{
			nodeClone = node.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		
		nodeClone.setTransform(matrix, location, scale);
		nodeClone.updateModelBound();
		map.attachChild(nodeClone);
		return nodeClone;
	}
	
	/**
	 * Creates a material zone template for a specific spatial node.<br>
	 * This method checks if the node has material intentions enabled in the configuration.<br>
	 * It determines the correct zone name based on the node path and child number.<br>
	 * Finally, it registers the new zone via {@code getInstance}.
	 * @param node The {@code Spatial} node to process.
	 * @param worldId The unique identifier for the world.
	 * @param childNumber The index of the child if this is a sub-zone.
	 */
	private static void createZone(Spatial node, int worldId, int childNumber)
	{
		if (GeoDataConfig.GEO_MATERIALS_ENABLE && ((node.getIntentions() & CollisionIntention.MATERIAL.getId()) != 0))
		{
			final BoundingVolume bv = node.getWorldBound();
			final int regionId = getVectorHash(bv.getCenter().x, bv.getCenter().y, bv.getCenter().z);
			final int index = node.getName().lastIndexOf('\\');
			final int dotIndex = node.getName().lastIndexOf('.');
			String zoneName = node.getName().substring(index + 1, dotIndex).toUpperCase();
			if (childNumber > 0)
			{
				zoneName += "_CHILD" + childNumber;
			}
			
			final String existingName = zoneName + "_" + regionId + "_" + worldId;
			if (ZoneName.getId(existingName) != ZoneName.getId(ZoneName.NONE))
			{
				// for override
				zoneName += "_" + regionId;
				node.setName(zoneName);
				ZoneService.getInstance().createMaterialZoneTemplate(node, worldId, node.getMaterialId(), true);
			}
			else
			{
				node.setName(zoneName);
				ZoneService.getInstance().createMaterialZoneTemplate(node, regionId, worldId, node.getMaterialId());
			}
		}
	}
	
	/**
	 * Initializes the transformation and naming for door objects.<br>
	 * This method sets the position, rotation, and scale of a {@code Spatial} node.<br>
	 * It also calculates a unique name based on the world ID and spatial region.
	 * @param node The {@code Spatial} object to modify.
	 * @param worldId The unique identifier for the current world.
	 * @param matrix The rotation matrix for the door.
	 * @param location The 3D coordinates of the door.
	 * @param scale The size multiplier for the door.
	 */
	private static void createDoors(Spatial node, int worldId, Matrix3f matrix, Vector3f location, float scale)
	{
		node.setTransform(matrix, location, scale);
		node.updateModelBound();
		final BoundingVolume bv = node.getWorldBound();
		final int regionId = getVectorHash(bv.getCenter().x, bv.getCenter().y, bv.getCenter().z);
		final int index = node.getName().lastIndexOf('\\');
		final String doorName = worldId + "_" + "DOOR" + "_" + regionId + "_" + node.getName().substring(index + 1).toUpperCase();
		node.setName(doorName);
	}
	
	/**
	 * Generates a unique hash for a 3D coordinate.<br>
	 * This method uses the bit representation of the coordinates to create an integer value.
	 * @param x The x-coordinate of the vector.
	 * @param y The y-coordinate of the vector.
	 * @param z The z-coordinate of the vector.
	 * @return A unique hash value as an {@code int}.
	 */
	private static int getVectorHash(float x, float y, float z)
	{
		final long xIntBits = Float.floatToIntBits(x);
		final long yIntBits = Float.floatToIntBits(y);
		final long zIntBits = Float.floatToIntBits(z);
		return (int) (((xIntBits * 73856093) ^ (yIntBits * 19349663) ^ (zIntBits * 83492791)) % 50000);
	}
	
}
