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
package com.aionemu.gameserver.services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dataholders.MotionData;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.skillengine.model.Motion;
import com.aionemu.gameserver.skillengine.model.MotionTime;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.model.Times;
import com.aionemu.gameserver.skillengine.model.WeaponTypeWrapper;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logging of character motion data to the database.<br>
 * It processes {@link Motion} information and saves it for analysis or debugging purposes.
 * @author kecimis
 */
public class MotionLoggingService
{
	private static Logger log = LoggerFactory.getLogger(MotionLoggingService.class);
	private final Map<String, MotionLog> motionsMap = new ConcurrentHashMap<>();
	private boolean advancedLog = false;
	private boolean started = false;
	
	/**
	 * Retrieves the singleton instance of the {@link MotionLoggingService}.<br>
	 * This method provides a global access point to the service.
	 * @return The active {@code MotionLoggingService} instance.
	 */
	public static MotionLoggingService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Starts the motion logging service.<br>
	 * This method sets the {@code started} flag to {@code true}.<br>
	 * It then calls {@code loadFromSql} to populate data from the database.
	 */
	public void start()
	{
		if (started)
		{
			return;
		}
		
		started = true;
		
		// load data from sql
		loadFromSql();
	}
	
	/**
	 * Records the execution time of a skill motion for a specific player.<br>
	 * This method calculates the adjusted base time based on client input, ammo speed, and play speed.<br>
	 * It then logs or updates the stored motion data in the {@link MotionLoggingService}.
	 * @param player The {@code Player} who performed the action.
	 * @param sk The {@code SkillTemplate} of the skill being used.
	 * @param clientTime The raw time value received from the game client.
	 * @param distance The distance traveled during the motion to calculate ammo time.
	 */
	public void logTime(Player player, SkillTemplate sk, int clientTime, double distance)
	{
		int currentAttackSpeed = 0;
		if (!started || (sk == null) || (player.getEquipment().getMainHandWeaponType() == null))
		{
			return;
		}
		
		final Motion motion = sk.getMotion();
		if (motion == null)
		{
			return;
		}
		
		currentAttackSpeed = player.getGameStats().getAttackSpeed().getCurrent();
		
		final int skillId = sk.getSkillId();
		final WeaponType mainHandWeapon = player.getEquipment().getMainHandWeaponType();
		final WeaponType offHandWeapon = player.getEquipment().getOffHandWeaponType();
		final String motionName = motion.getName();
		
		// clientTime is send from client
		int baseTime = clientTime; // adjusted time
		
		if (motion.getInstantSkill())
		{
			PacketSendUtility.sendMessage(player, "Skill: " + skillId + " is instant");
			return;
		}
		else if (clientTime == 0)
		{
			PacketSendUtility.sendMessage(player, "ClientTime is 0 for skill: " + skillId);
			return;
		}
		
		if (motion.getName() == null)
		{
			return;
		}
		
		long ammoTime = 0;
		if (sk.getAmmoSpeed() != 0)
		{
			ammoTime = Math.round((distance / sk.getAmmoSpeed()) * 1000); // checked with client
		}
		
		// adjusting with ammospeed
		baseTime -= ammoTime;
		
		// adjust clientTime if play speed is not 100
		if (motion.getSpeed() != 100)
		{
			baseTime /= motion.getSpeed();
			baseTime *= 100;
		}
		
		// logging
		if (advancedLog)
		{
			PacketSendUtility.sendMessage(player, "skillId: " + sk.getSkillId() + " motionName: " + motionName);
			PacketSendUtility.sendMessage(player, "attackSpeed: " + currentAttackSpeed + " mainHand: " + mainHandWeapon.toString() + " isDual: " + (offHandWeapon != null));
			PacketSendUtility.sendMessage(player, "clientTime: " + clientTime + " baseTime: " + baseTime + " playSpeed: " + motion.getSpeed());
			PacketSendUtility.sendMessage(player, "ammoTime: " + ammoTime + " ammoSpeed: " + sk.getAmmoSpeed() + " distance: " + distance);
			PacketSendUtility.sendMessage(player, "-------------------");
		}
		else
		{
			PacketSendUtility.sendMessage(player, "motionName: " + motionName + " clientTime: " + clientTime + " baseTime: " + baseTime);
		}
		
		final Race race = player.getRace();
		final Gender gender = player.getGender();
		
		// create WeaponTypeWrapper
		final WeaponTypeWrapper weapon = new WeaponTypeWrapper(mainHandWeapon, offHandWeapon);
		
		// check if its present
		if (isPresent(motionName, weapon, skillId, currentAttackSpeed, race, gender))
		{
			log.info("[MotionLoggingService] motionName: " + motionName + " weapon: " + (offHandWeapon != null ? "dual" : mainHandWeapon.toString()) + " skillId: " + skillId + " currentAttackSpeed: " + currentAttackSpeed + "baseTime: " + baseTime + " storedTime: " + getTime(motionName, weapon, skillId, currentAttackSpeed, race, gender));
			PacketSendUtility.sendMessage(player, "Its already stored. storedTime: " + getTime(motionName, weapon, skillId, currentAttackSpeed, race, gender));
			return;
		}
		
		// addtime
		if (addTime(motionName, weapon, skillId, currentAttackSpeed, race, gender, baseTime))
		{
			PacketSendUtility.sendMessage(player, "BaseTime: " + baseTime + " for motion: " + motionName + " was added.");
		}
		else
		{
			PacketSendUtility.sendMessage(player, "Couldnt add baseTime: " + baseTime + " for motion: " + motionName + "!");
		}
		
	}
	
	/**
	 * Generates analysis files for motion data.<br>
	 * This method creates individual XML files for each motion in the {@code motionsMap}.<br>
	 * It also generates a summary file named {@code results.xml} containing aggregated weapon times.<br>
	 * The summary includes calculations for base, cap, and 1000 attack speeds.
	 */
	public void createAnalyzeFiles()
	{
		/*
		 * TreeMap<String, WeaponTime> resultsBase = new TreeMap<String, WeaponTime>(); TreeMap<String, WeaponTime> resultsCap = new TreeMap<String, WeaponTime>(); TreeMap<String, WeaponTime> results1000 = new TreeMap<String, WeaponTime>(); for (Entry<String, MotionLog> entry : motionsMap.entrySet()) { String fileName = "motions/" + entry.getKey() + ".xml"; WeaponTime weaponTimeBase = new WeaponTime(); WeaponTime weaponTimeCap = new WeaponTime(); WeaponTime weaponTime1000 = new WeaponTime(); try { FileWriter fstream = new FileWriter(fileName); BufferedWriter out = new BufferedWriter(fstream); out.append("<motion name=\"" + entry.getKey() + "\">\n"); if (entry.getValue() != null) { // loop through weaponType for (Entry<WeaponTypeWrapper, List<SkillTime>> entry2 : entry.getValue().getMotionLog().entrySet()) { WeaponTypeWrapper weapon = entry2.getKey(); out.append("\t<motionlog "+ weapon.toString() + "\">\n"); if (entry2.getValue() == null) continue; for (SkillTime st : entry2.getValue()) { out.append("\t\t<skilltime skillId=\"" + st.getSkillId() + "\" attackspeed=\"" + st.getAttackSpeed() + "\" clienttime=\"" + st.getClientTime() + "\" race=\"" + st.getRace() + "\" gender=\"" + st.getGender() + "\" baseTime=\"" + this.recalculate("base", weapon, st.getAttackSpeed(), st.getClientTime()) + "\" >\n"); //fill weaponTime weaponTimeBase.add(weapon, this.recalculate("base", weapon, st.getAttackSpeed(), st.getClientTime())); weaponTimeCap.add(weapon, this.recalculate("cap", weapon, st.getAttackSpeed(), st.getClientTime())); weaponTime1000.add(weapon, this.recalculate("1000", weapon, st.getAttackSpeed(), st.getClientTime())); } out.append("\t<motionlog/>\n"); } //fill results resultsBase.put(entry.getKey(), weaponTimeBase); resultsCap.put(entry.getKey(), weaponTimeCap); results1000.put(entry.getKey(), weaponTime1000); } out.append("<motion/>"); out.close(); fstream.close(); } catch (IOException e) { log.error("Error: " + e.getMessage()); } } // create results files String fileName = "motions/results.xml"; try { FileWriter fstream = new FileWriter(fileName); BufferedWriter out = new BufferedWriter(fstream); out.append("<base attack speed>\n"); for (Entry<String, WeaponTime> entry : resultsBase.entrySet()) { out.append("<motion name=\"" + entry.getKey() + "\">\n"); TreeMap<WeaponTypeWrapper, Integer> map = entry.getValue().process(); for(Entry<WeaponTypeWrapper, Integer> entry2 : map.entrySet()) { out.append("\t<weapontime " + entry2.getKey().toString() + "\" time=\"" +entry2.getValue()+ "\" >\n"); } out.append("<motion/>\n"); map.clear(); } out.append("<base attack speed/>\n\n"); out.append("<cap attack speed>\n"); for (Entry<String, WeaponTime> entry : resultsCap.entrySet()) { out.append("<motion name=\"" + entry.getKey() + "\">\n"); TreeMap<WeaponTypeWrapper, Integer> map = entry.getValue().process(); for(Entry<WeaponTypeWrapper, Integer> entry2 : map.entrySet()) { out.append("\t<weapontime " + entry2.getKey().toString() + "\" time=\"" +entry2.getValue()+ "\" >\n"); } out.append("<motion/>\n"); map.clear(); } out.append("<cap attack speed/>\n\n"); out.append("<1000 attack speed>\n"); for (Entry<String, WeaponTime> entry : results1000.entrySet()) { out.append("<motion name=\"" + entry.getKey() + "\">\n"); TreeMap<WeaponTypeWrapper, Integer> map = entry.getValue().process(); for(Entry<WeaponTypeWrapper, Integer> entry2 : map.entrySet()) { out.append("\t<weapontime " + entry2.getKey().toString() + "\" time=\"" +entry2.getValue()+ "\" >\n"); } out.append("<motion/>\n"); map.clear(); } out.append("<1000 attack speed/>\n"); out.close(); fstream.close(); } catch (IOException e) { log.error("Error: " + e.getMessage()); }
		 */
	}
	
	/**
	 * Generates a final XML file containing processed motion times.<br>
	 * This method calculates weapon times for different races and genders.<br>
	 * It organizes the data into a specific order of {@code WeaponTypeWrapper}.<br>
	 * The result is saved to {@code data/static_data/skills/new_motion_times.xml}.
	 */
	public void createFinalFile()
	{
		final MotionData motionData = new MotionData();
		final List<MotionTime> motionTimes = motionData.getMotionTimes();
		
		// create results
		final TreeMap<String, List<WeaponTime>> results = new TreeMap<>();
		for (Entry<String, MotionLog> entry : motionsMap.entrySet())
		{
			final WeaponTime weaponTimeAm = new WeaponTime(Race.ASMODIANS, Gender.MALE);
			final WeaponTime weaponTimeAf = new WeaponTime(Race.ASMODIANS, Gender.FEMALE);
			final WeaponTime weaponTimeEm = new WeaponTime(Race.ELYOS, Gender.MALE);
			final WeaponTime weaponTimeEf = new WeaponTime(Race.ELYOS, Gender.FEMALE);
			if (entry.getValue() == null)
			{
				continue;
			}
			
			// loop through weaponType
			for (Entry<WeaponTypeWrapper, List<SkillTime>> entry2 : entry.getValue().getMotionLog().entrySet())
			{
				final WeaponTypeWrapper weapon = entry2.getKey();
				
				if (entry2.getValue() == null)
				{
					continue;
				}
				
				for (SkillTime st : entry2.getValue())
				{
					switch (st.getRace())
					{
						case ASMODIANS:
							if (st.getGender() == Gender.MALE)
							{
								weaponTimeAm.add(weapon, recalculate("base", weapon, st.getAttackSpeed(), st.getClientTime()));
							}
							else
							{
								weaponTimeAf.add(weapon, recalculate("base", weapon, st.getAttackSpeed(), st.getClientTime()));
							}
							break;
						case ELYOS:
							if (st.getGender() == Gender.MALE)
							{
								weaponTimeEm.add(weapon, recalculate("base", weapon, st.getAttackSpeed(), st.getClientTime()));
							}
							else
							{
								weaponTimeEf.add(weapon, recalculate("base", weapon, st.getAttackSpeed(), st.getClientTime()));
							}
							break;
						default:
							break;
					}
				}
				
				final List<WeaponTime> weaponTimes = new ArrayList<>(4);
				weaponTimes.add(weaponTimeAm);
				weaponTimes.add(weaponTimeAf);
				weaponTimes.add(weaponTimeEm);
				weaponTimes.add(weaponTimeEf);
				
				// fill results
				results.put(entry.getKey(), weaponTimes);
			}
		}
		
		for (Entry<String, List<WeaponTime>> entry : results.entrySet())
		{
			final Set<WeaponTypeWrapper> listofWeapons = new TreeSet<>();
			
			// Order the weapons and tools as follows: BOOK_2H, BOW, DAGGER_1H, MACE_1H, ORB_2H, POLEARM_2H, STAFF_2H, SWORD_1H, SWORD_2H, dualwield, GUN_1H, GUN_1H, dualwield, TOOLHOE_1H, TOOLHOE_1H, dualwield, TOOLPICK_2H, TOOLROD_2H, CANNON_2H, HARP_2H, GUN_2H, KEYBLADE_2H, and KEYHAMMER_2H.
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.BOOK_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.BOW, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.DAGGER_1H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.MACE_1H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.ORB_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.POLEARM_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.STAFF_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.SWORD_1H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.SWORD_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.SWORD_1H, WeaponType.SWORD_1H));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.DAGGER_1H, WeaponType.DAGGER_1H));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.GUN_1H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.GUN_1H, WeaponType.GUN_1H));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.TOOLHOE_1H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.TOOLHOE_1H, WeaponType.TOOLHOE_1H));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.TOOLPICK_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.TOOLROD_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.CANNON_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.HARP_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.GUN_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.KEYBLADE_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.KEYHAMMER_2H, null));
			listofWeapons.add(new WeaponTypeWrapper(WeaponType.SPRAY_2H, null));
			
			// create MotionTime
			final MotionTime motion = new MotionTime();
			motion.setName(entry.getKey());
			
			for (WeaponTime wt : entry.getValue())
			{
				// process values
				final TreeMap<WeaponTypeWrapper, Integer> map = wt.process();
				
				final StringBuilder sb = new StringBuilder();
				boolean first = true;
				
				// create time
				for (WeaponTypeWrapper weapon : listofWeapons)
				{
					if (first)
					{
						sb.append((map.containsKey(weapon) ? map.get(weapon) : "0"));
						first = false;
					}
					else
					{
						sb.append("," + (map.containsKey(weapon) ? map.get(weapon) : "0"));
					}
				}
				
				final Times times = new Times();
				times.setTimes(sb.toString());
				switch (wt.getRace())
				{
					case ASMODIANS:
						if (wt.getGender() == Gender.MALE)
						{
							motion.setAm(times);
						}
						else
						{
							motion.setAf(times);
						}
						break;
					case ELYOS:
						if (wt.getGender() == Gender.MALE)
						{
							motion.setEm(times);
						}
						else
						{
							motion.setEf(times);
						}
						break;
					default:
						break;
				}
			}
			
			motionTimes.add(motion);
		}
		
		// marshall the final xml file
		marshallFile(motionData, "data/static_data/skills/new_motion_times.xml");
	}
	
	/**
	 * Saves the provided object to a file using JAXB.<br>
	 * The output will be formatted for readability.
	 * @param templates The object to be saved to the file.
	 * @param file The destination path where the file will be created.
	 */
	public static void marshallFile(Object templates, String file)
	{
		try
		{
			final JAXBContext jaxbContext = JAXBContext.newInstance(templates.getClass());
			final Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(templates, new FileOutputStream(file));
		}
		catch (JAXBException | FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates the adjusted motion time based on weapon types and attack speed.<br>
	 * This method determines a speed multiplier from a predefined list of weapons.<br>
	 * It then computes the final rounded time using the provided parameters.
	 * @param method The calculation mode such as "base", "cap", or a numeric string.
	 * @param weapon The {@code WeaponTypeWrapper} containing main and off-hand data.
	 * @param attackSpeed The current speed of the character's attack.
	 * @param time The original motion time to be recalculated.
	 * @return The calculated final motion time as an {@code int}.
	 */
	private int recalculate(String method, WeaponTypeWrapper weapon, int attackSpeed, int time)
	{
		int finalTime = 0;
		
		final TreeMap<WeaponType, float[]> list = new TreeMap<>();
		
		final float dagger[] =
		{
			600f,
			1200f
		};
		list.put(WeaponType.DAGGER_1H, dagger);
		final float mace[] =
		{
			750f,
			1500f
		};
		list.put(WeaponType.MACE_1H, mace);
		list.put(WeaponType.GUN_1H, mace);
		list.put(WeaponType.TOOLHOE_1H, mace);
		final float sword1h[] =
		{
			700f,
			1400f
		};
		list.put(WeaponType.SWORD_1H, sword1h);
		final float bookorb[] =
		{
			1100f,
			2200f
		};
		list.put(WeaponType.BOOK_2H, bookorb);
		list.put(WeaponType.ORB_2H, bookorb);
		final float polearm[] =
		{
			1400f,
			2800f
		};
		list.put(WeaponType.POLEARM_2H, polearm);
		list.put(WeaponType.TOOLPICK_2H, polearm);
		list.put(WeaponType.TOOLROD_2H, polearm);
		list.put(WeaponType.CANNON_2H, polearm);
		list.put(WeaponType.GUN_2H, polearm);
		list.put(WeaponType.KEYBLADE_2H, polearm);
		list.put(WeaponType.KEYHAMMER_2H, polearm);
		list.put(WeaponType.SPRAY_2H, polearm);
		final float staff[] =
		{
			1000f,
			2000f
		};
		list.put(WeaponType.STAFF_2H, staff);
		list.put(WeaponType.HARP_2H, staff);
		final float swordbow[] =
		{
			1200f,
			2400f
		};
		list.put(WeaponType.SWORD_2H, swordbow);
		list.put(WeaponType.BOW, swordbow);
		
		float speed = 0;
		if (method.equalsIgnoreCase("base"))
		{
			speed = list.get(weapon.getMainHand())[1];
			if (weapon.getOffHand() != null)
			{
				speed += (list.get(weapon.getOffHand())[1] * 0.25);
			}
		}
		else if (method.equalsIgnoreCase("cap"))
		{
			speed = list.get(weapon.getMainHand())[0];
			if (weapon.getOffHand() != null)
			{
				speed += (list.get(weapon.getOffHand())[0] * 0.25);
			}
		}
		else
		{
			try
			{
				speed = Float.parseFloat(method);
			}
			catch (Exception e)
			{
				// log
			}
		}
		
		finalTime = Math.round(((float) time / (float) attackSpeed) * speed);
		
		return finalTime;
	}
	
	// save to sql
	/**
	 * Saves all collected motion logs into the database.<br>
	 * This method iterates through the {@code motionsMap}.<br>
	 * It inserts or updates records in the {@code skill_motions} table.
	 */
	public void saveToSql()
	{
		Connection con = null;
		
		final String INSERT_QUERY = "INSERT INTO skill_motions (motion_name, weapon_type, off_weapon_type, skill_id, attack_speed, race, gender, time) VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE motion_name = ?";
		
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			for (Entry<String, MotionLog> entry : motionsMap.entrySet())
			{
				final String motionName = entry.getKey();
				
				// set motion_name
				stmt.setString(1, motionName);
				stmt.setString(9, motionName);
				if (entry.getValue() == null)
				{
					continue;
				}
				
				// loop through weaponType
				for (Entry<WeaponTypeWrapper, List<SkillTime>> entry2 : entry.getValue().getMotionLog().entrySet())
				{
					final String weaponType = (entry2.getKey().getMainHand() != null ? entry2.getKey().getMainHand().toString() : "null");
					final String offWeaponType = (entry2.getKey().getOffHand() != null ? entry2.getKey().getOffHand().toString() : "null");
					
					// set weapon_type
					stmt.setString(2, weaponType);
					stmt.setString(3, offWeaponType);
					
					if (entry2.getValue() == null)
					{
						continue;
					}
					
					// sort by skillId
					Collections.sort(entry2.getValue());
					for (SkillTime st : entry2.getValue())
					{
						stmt.setInt(4, st.getSkillId());
						stmt.setInt(5, st.getAttackSpeed());
						stmt.setString(6, st.getRace().toString());
						stmt.setString(7, st.getGender().toString());
						stmt.setInt(8, st.getClientTime());
						stmt.execute();
					}
				}
			}
			
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("MotionLoggingService", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	// load from sql
	/**
	 * Loads motion data from the database into memory.<br>
	 * This method retrieves all records from the {@code skill_motions} table.<br>
	 * It populates the internal motion logs using the {@code WeaponTypeWrapper, int, int, Race, Gender, int)} method.
	 */
	public void loadFromSql()
	{
		Connection con = null;
		
		final String SELECT_QUERY = "SELECT * FROM skill_motions";
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			
			final ResultSet resultSet = stmt.executeQuery();
			
			while (resultSet.next())
			{
				final String motionName = resultSet.getString("motion_name");
				final WeaponType mainHandWeapon = WeaponType.valueOf(resultSet.getString("weapon_type"));
				final WeaponType offHandWeapon = (resultSet.getString("off_weapon_type").contains("null") ? null : WeaponType.valueOf(resultSet.getString("off_weapon_type")));
				final int skillId = resultSet.getInt("skill_id");
				final int attackSpeed = resultSet.getInt("attack_speed");
				final int time = resultSet.getInt("time");
				final String sRace = resultSet.getString("race");
				final String sGender = resultSet.getString("gender");
				final WeaponTypeWrapper weapon = new WeaponTypeWrapper(mainHandWeapon, offHandWeapon);
				
				Race race = null;
				Gender gender = null;
				try
				{
					race = Race.valueOf(sRace);
					gender = Gender.valueOf(sGender);
				}
				catch (Exception e)
				{
					log.info("[MotionLoggingService] cant load gender or race for motion_name: " + motionName);
				}
				finally
				{
					addTime(motionName, weapon, skillId, attackSpeed, race, gender, time);
				}
			}
			
			resultSet.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("MotionLoggingService", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Refreshes the motion data from the database.<br>
	 * This method clears existing motions and calls {@code loadFromSql}.
	 */
	public void reloadFromSql()
	{
		clearMotions();
		loadFromSql();
	}
	
	/**
	 * Clears all recorded motion data from the system.<br>
	 * It empties every {@code MotionLog} inside the {@code motionsMap}.<br>
	 * Finally, it removes all entries from the {@code motionsMap}.
	 */
	private void clearMotions()
	{
		for (MotionLog mLog : motionsMap.values())
		{
			mLog.getMotionLog().clear();
		}
		
		motionsMap.clear();
	}
	
	/**
	 * Checks if a specific motion exists for the given character attributes.<br>
	 * It verifies the {@code motionName} in the internal map and validates the requirements.
	 * @param motionName The name of the motion to check.
	 * @param weapon The wrapper containing the weapon type information.
	 * @param skillId The unique identifier for the skill.
	 * @param currentAttackSpeed The current attack speed of the character.
	 * @param race The {@link Race} of the character.
	 * @param gender The {@link Gender} of the character.
	 * @return {@code true} if the motion is present and valid, {@code false} otherwise.
	 */
	private boolean isPresent(String motionName, WeaponTypeWrapper weapon, int skillId, int currentAttackSpeed, Race race, Gender gender)
	{
		return motionsMap.containsKey(motionName) && motionsMap.get(motionName).isPresent(weapon, skillId, currentAttackSpeed, race, gender);
	}
	
	/**
	 * Retrieves the motion time for a specific action.<br>
	 * It checks if the {@code motionName} exists in the internal map.<br>
	 * If it does not exist, it returns {@code 0}.
	 * @param motionName The unique name of the motion to look up.
	 * @param weapon The wrapper containing weapon type information.
	 * @param skillId The unique identifier for the skill.
	 * @param currentAttackSpeed The current attack speed value of the character.
	 * @param race The {@link Race} of the character.
	 * @param gender The {@link Gender} of the character.
	 * @return The calculated motion time or {@code 0} if not found.
	 */
	private int getTime(String motionName, WeaponTypeWrapper weapon, int skillId, int currentAttackSpeed, Race race, Gender gender)
	{
		if (motionsMap.containsKey(motionName))
		{
			return motionsMap.get(motionName).getTime(weapon, skillId, currentAttackSpeed, race, gender);
		}
		
		return 0;
	}
	
	/**
	 * Adds a recorded skill time to the motion log.<br>
	 * It creates a new {@code MotionLog} if the motion name does not exist.<br>
	 * Otherwise, it updates the existing entry in the map.
	 * @param motionName The unique identifier for the motion.
	 * @param weapon The wrapper containing weapon type information.
	 * @param skillId The ID of the skill being performed.
	 * @param currentAttackSpeed The current attack speed of the character.
	 * @param race The race of the character.
	 * @param gender The gender of the character.
	 * @param clientTime The time received from the client.
	 * @return {@code true} if the time was successfully added, {@code false} otherwise.
	 */
	public boolean addTime(String motionName, WeaponTypeWrapper weapon, int skillId, int currentAttackSpeed, Race race, Gender gender, int clientTime)
	{
		if (!motionsMap.containsKey(motionName))
		{
			final MotionLog motionLog = new MotionLog();
			final boolean result = motionLog.addSkillTime(weapon, new SkillTime(skillId, currentAttackSpeed, race, gender, clientTime));
			motionsMap.put(motionName, motionLog);
			return result;
		}
		
		return motionsMap.get(motionName).addSkillTime(weapon, new SkillTime(skillId, currentAttackSpeed, race, gender, clientTime));
	}
	
	/**
	 * Enables or disables advanced logging features.<br>
	 * This method updates the {@code advancedLog} state.
	 * @param bol The boolean value to set for advanced logging.
	 */
	public void setAdvancedLog(boolean bol)
	{
		advancedLog = bol;
	}
	
	/**
	 * Checks if the advanced logging feature is currently enabled.<br>
	 * This status determines whether detailed motion data is recorded.
	 * @return {@code true} if advanced logging is active, {@code false} otherwise.
	 */
	public boolean getAdvancedLog()
	{
		return advancedLog;
	}
	
	/**
	 * Private constructor for the {@link MotionLoggingService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * It logs a startup message to the logger.
	 */
	private MotionLoggingService()
	{
		log.info("[MotionLoggingService] started ...");
	}
	
	private static class SingletonHolder
	{
		protected static final MotionLoggingService instance = new MotionLoggingService();
	}
	
	private class MotionLog
	{
		private final Map<WeaponTypeWrapper, List<SkillTime>> motionsForWeapons = new HashMap<>();
		
		public Map<WeaponTypeWrapper, List<SkillTime>> getMotionLog()
		{
			return motionsForWeapons;
		}
		
		public boolean addSkillTime(WeaponTypeWrapper weapon, SkillTime skillTime)
		{
			if (motionsForWeapons.containsKey(weapon))
			{
				if (!motionsForWeapons.get(weapon).contains(skillTime))
				{
					motionsForWeapons.get(weapon).add(skillTime);
					return true;
				}
			}
			else
			{
				final List<SkillTime> list = new ArrayList<>();
				list.add(skillTime);
				motionsForWeapons.put(weapon, list);
				return true;
			}
			
			return false;
		}
		
		public int getTime(WeaponTypeWrapper weapon, int skillId, int currentAttackSpeed, Race race, Gender gender)
		{
			if (motionsForWeapons.containsKey(weapon))
			{
				for (SkillTime st : motionsForWeapons.get(weapon))
				{
					if ((st.getSkillId() == skillId) && (st.getAttackSpeed() == currentAttackSpeed) && (st.getRace() == race) && (st.getGender() == gender))
					{
						return st.getClientTime();
					}
				}
			}
			
			return 0;
		}
		
		public boolean isPresent(WeaponTypeWrapper weapon, int skillId, int currentAttackSpeed, Race race, Gender gender)
		{
			if (motionsForWeapons.containsKey(weapon))
			{
				for (SkillTime st : motionsForWeapons.get(weapon))
				{
					if ((st.getSkillId() == skillId) && (st.getAttackSpeed() == currentAttackSpeed) && (st.getRace() == race) && (st.getGender() == gender))
					{
						return true;
					}
				}
			}
			
			return false;
		}
	}
	
	private class SkillTime implements Comparable<SkillTime>
	{
		private final int skillId;
		private final int attackSpeed;
		private final int clientTime;
		private final Race race;
		private final Gender gender;
		
		public SkillTime(int skillId, int attackSpeed, Race race, Gender gender, int clientTime)
		{
			this.skillId = skillId;
			this.attackSpeed = attackSpeed;
			this.clientTime = clientTime;
			this.race = race;
			this.gender = gender;
		}
		
		@Override
		public int compareTo(SkillTime o)
		{
			if (skillId < o.getSkillId())
			{
				return -1;
			}
			else if (skillId > o.getSkillId())
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = (prime * result) + getOuterType().hashCode();
			result = (prime * result) + attackSpeed;
			result = (prime * result) + clientTime;
			result = (prime * result) + ((gender == null) ? 0 : gender.hashCode());
			result = (prime * result) + ((race == null) ? 0 : race.hashCode());
			result = (prime * result) + skillId;
			return result;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			
			if ((obj == null) || (getClass() != obj.getClass()))
			{
				return false;
			}
			
			final SkillTime other = (SkillTime) obj;
			if (!getOuterType().equals(other.getOuterType()) || (attackSpeed != other.attackSpeed) || (clientTime != other.clientTime) || (gender != other.gender))
			{
				return false;
			}
			
			if (race != other.race)
			{
				return false;
			}
			
			return skillId == other.skillId;
		}
		
		public int getSkillId()
		{
			return skillId;
		}
		
		public int getAttackSpeed()
		{
			return attackSpeed;
		}
		
		public int getClientTime()
		{
			return clientTime;
		}
		
		public Race getRace()
		{
			return race;
		}
		
		public Gender getGender()
		{
			return gender;
		}
		
		private MotionLoggingService getOuterType()
		{
			return MotionLoggingService.this;
		}
	}
	
	private class WeaponTime
	{
		private final TreeMap<WeaponTypeWrapper, List<Integer>> values = new TreeMap<>();
		private final Race race;
		private final Gender gender;
		
		public WeaponTime(Race race, Gender gender)
		{
			this.race = race;
			this.gender = gender;
		}
		
		/**
		 * @return the race
		 */
		public Race getRace()
		{
			return race;
		}
		
		/**
		 * @return the gender
		 */
		public Gender getGender()
		{
			return gender;
		}
		
		public void add(WeaponTypeWrapper weapon, int value)
		{
			if (values.containsKey(weapon))
			{
				values.get(weapon).add(value);
			}
			else
			{
				final List<Integer> list = new ArrayList<>();
				list.add(value);
				values.put(weapon, list);
			}
		}
		
		public TreeMap<WeaponTypeWrapper, Integer> process()
		{
			final TreeMap<WeaponTypeWrapper, Integer> weaponMap = new TreeMap<>();
			
			for (Entry<WeaponTypeWrapper, List<Integer>> entry2 : values.entrySet())
			{
				// Calculate one value per weapon type by counting the element with the most occurrences.
				int finalValue = 0;
				int maxFrequency = 0;
				int value = 0;
				int total = 0;
				for (Integer i : entry2.getValue())
				{
					total += i;
					if (calculateFrequency(entry2.getValue(), i) > maxFrequency)
					{
						maxFrequency = calculateFrequency(entry2.getValue(), i);
						value = i;
					}
				}
				
				log.info("[MotionLoggingService] maxFrequency: " + maxFrequency + " value: " + value + " size: " + entry2.getValue().size());
				
				// if frequency of given value is higher than 70% take it, otherwise do Arithmetic mean
				if (Math.round(entry2.getValue().size() * 0.7f) <= maxFrequency)
				{
					finalValue = value;
				}
				else
				{
					finalValue = total / entry2.getValue().size();
				}
				
				log.info("[MotionLoggingService] weaponTime.process() finalValue: " + finalValue);
				weaponMap.put(entry2.getKey(), finalValue);
			}
			
			return weaponMap;
		}
	}
	
	/**
	 * Counts how many times a specific value appears in a list within a 10% tolerance.<br>
	 * It ignores {@code null} entries in the provided {@code List}.
	 * @param list The {@code List} of {@code Integer} values to check.
	 * @param value The target {@code int} value to compare against.
	 * @return The total count of matches found.
	 */
	private int calculateFrequency(List<Integer> list, int value)
	{
		int frequency = 0;
		
		// 10% tolerance
		final int min = Math.round(value * 0.90f);
		final int max = Math.round(value * 1.1f);
		for (Integer i : list)
		{
			if (i == null)
			{
				continue;
			}
			
			if ((min <= value) && (max >= value))
			{
				frequency++;
			}
		}
		
		return frequency;
	}
}
