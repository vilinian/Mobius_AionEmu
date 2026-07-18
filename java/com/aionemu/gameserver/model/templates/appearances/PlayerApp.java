package com.aionemu.gameserver.model.templates.appearances;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the appearance data for a player character.<br>
 * This class maps to the {@code player} XML element in the configuration files.<br>
 * It stores visual attributes used by the game server to render characters.
 * @author CoolyT
 */
@XmlType(name = "player")
public class PlayerApp
{
	@XmlAttribute(name = "name")
	public String name;
	
	@XmlAttribute(name = "gender")
	public String gender;
	
	@XmlAttribute(name = "race")
	public String race;
	
	@XmlAttribute(name = "level")
	public int level;
	
	@XmlAttribute(name = "class")
	public String playerClass;
	
	@XmlElement(name = "appearance")
	public PlayerAppearanceTemplate appearance = new PlayerAppearanceTemplate();
	
	@XmlElement(name = "items")
	public List<PlayerItem> items = new ArrayList<>();
}
