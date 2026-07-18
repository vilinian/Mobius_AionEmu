package com.aionemu.gameserver.model.templates.appearances;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents an item that can be equipped or held by a player.<br>
 * This class defines the data structure for items within the {@code appearances} system.
 * @author CoolyT
 */
@XmlType(name = "items")
public class PlayerItem
{
	@XmlAttribute()
	public int itemTemplateId;
	@XmlAttribute()
	public int itemColor;
}
