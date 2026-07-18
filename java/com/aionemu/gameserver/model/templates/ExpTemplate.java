package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the configuration data for experience points.<br>
 * It defines how {@link com.aionemu.gameserver.model.templates.ExpTemplate} values are loaded from XML files.
 * @author Antraxx
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "exp")
public class ExpTemplate
{
	@XmlAttribute(name = "level", required = true)
	private int level;
	
	@XmlAttribute(name = "exp", required = true)
	private long exp;
	
	@XmlAttribute(name = "cp", required = false)
	private int cp;
	
	@XmlAttribute(name = "cp_lvlup", required = false)
	private int cpLevelUp;
	
	@XmlAttribute(name = "feverpoint_boost", required = false)
	private long feverPointBoost;
	
	@XmlAttribute(name = "feverpoint_max", required = false)
	private long feverPointMax;
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the current experience points of the player.<br>
	 * This value represents the total accumulated {@code exp}.
	 * @return The current experience points as a {@code long}.
	 */
	public long getExp()
	{
		return exp;
	}
	
	/**
	 * Retrieves the current CP value.<br>
	 * This value is defined in the {@code ExpTemplate}.
	 * @return The integer value of the CP.
	 */
	public int getCp()
	{
		return cp;
	}
	
	/**
	 * Retrieves the amount of CP required to level up.<br>
	 * This value is stored in the {@code cp_lvlup} attribute.
	 * @return The current CP level up requirement as an {@code int}.
	 */
	public int getCpLevelUp()
	{
		return cpLevelUp;
	}
	
	/**
	 * Retrieves the amount of Fever Points granted as a boost.<br>
	 * This value is defined in the {@code ExpTemplate}.
	 * @return The total number of bonus Fever Points.
	 */
	public long getFeverPointBoost()
	{
		return feverPointBoost;
	}
	
	/**
	 * Retrieves the maximum amount of fever points allowed.<br>
	 * This value is defined in the {@code feverpoint_max} attribute.
	 * @return The maximum fever point value as a {@code long}.
	 */
	public long getFeverPointMax()
	{
		return feverPointMax;
	}
	
}
