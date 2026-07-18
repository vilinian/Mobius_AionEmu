/*
 * =====================================================================================*
 * This file is part of Aion-Unique (Aion-Unique Home Software Development)             *
 * Aion-Unique Development is a closed Aion Project that use Old Aion Project Base      *
 * Like Aion-Lightning, Aion-Engine, Aion-Core, Aion-Extreme, Aion-NextGen, ArchSoft,   *
 * Aion-Ger, U3J, Encom And other Aion project, All Credit Content                      *
 * That they make is belong to them/Copyright is belong to them. And All new Content    *
 * that Aion-Unique make the copyright is belong to Aion-Unique                         *
 * You may have agreement with Aion-Unique Development, before use this Engine/Source   *
 * You have agree with all of Term of Services agreement with Aion-Unique Development   *
 * =====================================================================================*
 */
package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.SkillSkinTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link SkillSkinTemplate} objects.<br>
 * It manages the collection of skill skins loaded from the game configuration files.
 */
@XmlRootElement(name = "skill_skins")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillSkinData
{
	@XmlElement(name = "skill_skin")
	private List<SkillSkinTemplate> sst;
	private TIntObjectHashMap<SkillSkinTemplate> skillskins;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code skillskins} map using the list of {@link SkillSkinTemplate} templates.<br>
	 * The {@code sst} list is set to {@code null} after the map is built.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		skillskins = new TIntObjectHashMap<>();
		for (SkillSkinTemplate st : sst)
		{
			skillskins.put(st.getId(), st);
		}
		
		sst = null;
	}
	
	/**
	 * Retrieves a specific {@link SkillSkinTemplate} based on its unique ID.<br>
	 * This method looks up the template in the internal data map.
	 * @param skinId The unique integer identifier for the skill skin.
	 * @return The {@code SkillSkinTemplate} associated with the provided ID, or {@code null} if not found.
	 */
	public SkillSkinTemplate getSkillSkinTemplate(int skinId)
	{
		return skillskins.get(skinId);
	}
	
	/**
	 * Returns the total number of skill skins.<br>
	 * This method calls {@code size} to get the count.
	 * @return The number of items currently stored in the collection.
	 */
	public int size()
	{
		return skillskins.size();
	}
}
