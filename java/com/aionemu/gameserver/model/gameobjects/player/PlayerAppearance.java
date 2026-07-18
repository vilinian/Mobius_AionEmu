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
package com.aionemu.gameserver.model.gameobjects.player;

/**
 * This class represents the visual appearance of a {@link com.aionemu.gameserver.model.gameobjects.player.Player}.<br>
 * It stores data related to the character's physical look, such as costumes and equipment.<br>
 * Use this model to manage how a player is rendered in the game world.
 * @author SoulKeeper, srx47, alexa026
 */
public class PlayerAppearance implements Cloneable
{
	/**
	 * Player's face
	 */
	private int face;
	private int hair;
	private int deco;
	private int tattoo;
	private int faceContour; // 2.5
	private int expression; // 2.5
	private int pupilShape; // 5.0
	private int removeMane; // 5.0
	private int rightEyeRGB; // 5.0
	private int eyeLashshape; // 5.0
	private int jawLine; // 2.5
	private int skinRGB;
	private int hairRGB;
	private int lipRGB;
	private int eyeRGB;
	private int faceShape;
	private int pupilSize; // 5.0
	private int upperTorso; // 5.0
	private int foreArmThickness; // 5.0
	private int handSpan; // 5.0
	private int calfThickness; // 5.0
	private int forehead;
	private int eyeHeight;
	private int eyeSpace;
	private int eyeWidth;
	private int eyeSize;
	private int eyeShape;
	private int eyeAngle;
	private int browHeight;
	private int browAngle;
	private int browShape;
	private int nose;
	private int noseBridge;
	private int noseWidth;
	private int noseTip;
	private int cheek;
	private int lipHeight;
	private int mouthSize;
	private int lipSize;
	private int smile;
	private int lipShape;
	private int jawHeigh;
	private int chinJut;
	private int earShape;
	private int headSize;
	private int neck;
	private int neckLength;
	private int shoulders;
	private int shoulderSize;
	private int torso;
	private int chest;
	private int waist;
	private int hips;
	private int armThickness;
	private int armLength;
	private int handSize;
	private int legThickness;
	private int legLength;
	private int footSize;
	private int facialRate;
	private int voice;
	private float height;
	
	/**
	 * Retrieves the current face value of the player.<br>
	 * This method returns the {@code int} stored in the {@code face} field.
	 * @return The integer value representing the character's face.
	 */
	public int getFace()
	{
		return face;
	}
	
	/**
	 * Sets the character's face ID.<br>
	 * This updates the {@code face} field in the {@link PlayerAppearance} object.
	 * @param face The new face identifier to assign.
	 */
	public void setFace(int face)
	{
		this.face = face;
	}
	
	/**
	 * Retrieves the current hairstyle of the player.<br>
	 * This value is stored as an {@code int}.
	 * @return the player's hair style ID.
	 */
	public int getHair()
	{
		return hair;
	}
	
	/**
	 * Updates the hairstyle of the player character.<br>
	 * This method sets the {@code hair} field to a new value.
	 * @param hair The new hairstyle identifier to apply.
	 */
	public void setHair(int hair)
	{
		this.hair = hair;
	}
	
	/**
	 * Retrieves the decoration value for the player.<br>
	 * This method returns the current {@code deco} integer.
	 * @return the decoration value
	 */
	public int getDeco()
	{
		return deco;
	}
	
	/**
	 * Sets the decoration value for the player.<br>
	 * This updates the {@code deco} field in the {@link PlayerAppearance} object.
	 * @param deco The new decoration value to set.
	 */
	public void setDeco(int deco)
	{
		this.deco = deco;
	}
	
	/**
	 * Retrieves the current tattoo ID of the player.<br>
	 * This value is stored in the {@code tattoo} field.
	 * @return the integer ID of the player's tattoo
	 */
	public int getTattoo()
	{
		return tattoo;
	}
	
	/**
	 * Sets the tattoo ID for the player.<br>
	 * This updates the {@code tattoo} field in the {@link PlayerAppearance} object.
	 * @param tattoo The unique identifier for the tattoo to apply.
	 */
	public void setTattoo(int tattoo)
	{
		this.tattoo = tattoo;
	}
	
	/**
	 * Retrieves the face contour value of the player.<br>
	 * This value defines the shape of the character's facial structure.
	 * @return the {@code int} value representing the face contour.
	 */
	public int getFaceContour()
	{
		return faceContour;
	}
	
	/**
	 * Sets the contour of the player's face.<br>
	 * This updates the {@code faceContour} field in the {@link PlayerAppearance} object.
	 * @param faceContour The new value for the face contour.
	 */
	public void setFaceContour(int faceContour)
	{
		this.faceContour = faceContour;
	}
	
	/**
	 * Retrieves the current facial expression of the player.
	 * @return the {@code int} value representing the expression.
	 */
	public int getExpression()
	{
		return expression;
	}
	
	/**
	 * Sets the facial expression of the player.<br>
	 * This updates the {@code expression} field with a new value.
	 * @param expression The new expression ID to set.
	 */
	public void setExpression(int expression)
	{
		this.expression = expression;
	}
	
	/**
	 * Retrieves the shape of the player's pupils.<br>
	 * This value is used to determine the visual style of the eyes.
	 * @return the {@code int} value representing the pupil shape
	 */
	public int getPupilShape()
	{
		return pupilShape;
	}
	
	/**
	 * Sets the shape of the character's pupils.<br>
	 * This updates the {@code pupilShape} field in the player appearance.
	 * @param pupilShape The new shape value for the pupils.
	 */
	public void setPupilShape(int pupilShape)
	{
		this.pupilShape = pupilShape;
	}
	
	/**
	 * Retrieves the value for removing the mane.<br>
	 * This value determines if the character's mane is hidden.
	 * @return the {@code removeMane} integer value.
	 */
	public int getRemoveMane()
	{
		return removeMane;
	}
	
	/**
	 * Sets the value for removing the mane.<br>
	 * This updates the {@code removeMane} field in the player appearance.
	 * @param removeMane The new value to set for the mane removal.
	 */
	public void setRemoveMane(int removeMane)
	{
		this.removeMane = removeMane;
	}
	
	/**
	 * Retrieves the RGB color value for the player's right eye.<br>
	 * This value is used to determine the specific color of the right eye.
	 * @return the {@code int} representing the right eye RGB color.
	 */
	public int getRightEyeRGB()
	{
		return rightEyeRGB;
	}
	
	/**
	 * Sets the RGB color value for the player's right eye.<br>
	 * This updates the {@code rightEyeRGB} field in the {@link PlayerAppearance} object.
	 * @param rightEyeRGB The new RGB color value to assign.
	 */
	public void setRightEyeRGB(int rightEyeRGB)
	{
		this.rightEyeRGB = rightEyeRGB;
	}
	
	/**
	 * Retrieves the current shape of the player's eyelashes.<br>
	 * This value is used to determine the visual style of the eyes.
	 * @return the {@code int} value representing the eyelash shape
	 */
	public int getEyeLashShape()
	{
		return eyeLashshape;
	}
	
	/**
	 * Sets the shape of the player's eyelashes.<br>
	 * This updates the {@code eyeLashshape} field in the {@link PlayerAppearance} object.
	 * @param eyeLashshape The new shape value for the eyelashes.
	 */
	public void setEyeLashShape(int eyeLashshape)
	{
		this.eyeLashshape = eyeLashshape;
	}
	
	/**
	 * Retrieves the size of the character's pupils.<br>
	 * This value is used to determine how large the eyes appear on the model.
	 * @return the current {@code pupilSize} as an {@code int}
	 */
	public int getPupilSize()
	{
		return pupilSize;
	}
	
	/**
	 * Updates the size of the character's pupils.<br>
	 * This method sets the {@code pupilSize} field to a new value.
	 * @param pupilSize The new size for the pupils.
	 */
	public void setPupilSize(int pupilSize)
	{
		this.pupilSize = pupilSize;
	}
	
	/**
	 * Retrieves the upper torso value of the player.<br>
	 * This value defines a specific physical attribute of the character's body.
	 * @return the {@code int} value representing the upper torso.
	 */
	public int getUpperTorso()
	{
		return upperTorso;
	}
	
	/**
	 * Sets the upper torso value for the player appearance.<br>
	 * This updates the {@code upperTorso} field in this object.
	 * @param upperTorso The new integer value for the upper torso.
	 */
	public void setUpperTorso(int upperTorso)
	{
		this.upperTorso = upperTorso;
	}
	
	/**
	 * Retrieves the thickness of the player's forearm.<br>
	 * This value is used to determine the character's physical appearance.
	 * @return the {@code int} value representing forearm thickness
	 */
	public int getForeArmThickness()
	{
		return foreArmThickness;
	}
	
	/**
	 * Sets the thickness of the player's forearm.<br>
	 * This updates the {@code foreArmThickness} value in the appearance model.
	 * @param foreArmThickness The new thickness value for the forearm.
	 */
	public void setForeArmThickness(int foreArmThickness)
	{
		this.foreArmThickness = foreArmThickness;
	}
	
	/**
	 * Retrieves the current hand span of the player.<br>
	 * This value represents a specific physical attribute of the character's appearance.
	 * @return the {@code int} value of the hand span
	 */
	public int getHandSpan()
	{
		return handSpan;
	}
	
	/**
	 * Sets the width of the player's hand span.<br>
	 * This updates the {@code handSpan} field in the {@link PlayerAppearance} object.
	 * @param handSpan The new value for the hand span.
	 */
	public void setHandSpan(int handSpan)
	{
		this.handSpan = handSpan;
	}
	
	/**
	 * Retrieves the thickness of the character's calves.<br>
	 * This value is used to determine the physical appearance of the legs.
	 * @return the {@code int} value representing calf thickness
	 */
	public int getCalfThickness()
	{
		return calfThickness;
	}
	
	/**
	 * Sets the thickness of the player's calves.<br>
	 * This updates the {@code calfThickness} field in the {@link PlayerAppearance} object.
	 * @param calfThickness The new thickness value for the calves.
	 */
	public void setCalfThickness(int calfThickness)
	{
		this.calfThickness = calfThickness;
	}
	
	/**
	 * Retrieves the current jaw line value for the player.<br>
	 * This value defines a specific facial feature of the character.
	 * @return the {@code int} value representing the jaw line.
	 */
	public int getJawLine()
	{
		return jawLine;
	}
	
	/**
	 * Sets the jaw line value for the player appearance.<br>
	 * This updates the {@code jawLine} field in the current object.
	 * @param jawLine The new value to set for the jaw line.
	 */
	public void setJawLine(int jawLine)
	{
		this.jawLine = jawLine;
	}
	
	/**
	 * Retrieves the RGB color value of the player's skin.<br>
	 * This value is used to determine the skin tone of the character.
	 * @return the {@code int} representing the skin RGB color.
	 */
	public int getSkinRGB()
	{
		return skinRGB;
	}
	
	/**
	 * Sets the RGB color value for the player's skin.<br>
	 * This updates the {@code skinRGB} field in the {@link PlayerAppearance} object.
	 * @param skinRGB The new RGB color value to apply.
	 */
	public void setSkinRGB(int skinRGB)
	{
		this.skinRGB = skinRGB;
	}
	
	/**
	 * Retrieves the RGB color value of the player's hair.<br>
	 * This value is used to determine the hair color in the game.
	 * @return the {@code int} representing the hair RGB color.
	 */
	public int getHairRGB()
	{
		return hairRGB;
	}
	
	/**
	 * Sets the RGB color value for the player's hair.<br>
	 * This updates the {@code hairRGB} field in the {@link PlayerAppearance} object.
	 * @param hairRGB The new RGB color value to assign to the hair.
	 */
	public void setHairRGB(int hairRGB)
	{
		this.hairRGB = hairRGB;
	}
	
	/**
	 * Sets the RGB color value for the player's eyes.<br>
	 * This updates the {@code eyeRGB} field in the {@link PlayerAppearance} object.
	 * @param eyeRGB The integer value representing the eye color.
	 */
	public void setEyeRGB(int eyeRGB)
	{
		this.eyeRGB = eyeRGB;
	}
	
	/**
	 * Retrieves the RGB color value of the player's eyes.<br>
	 * This value is used to determine the eye color in the game.
	 * @return the {@code int} representing the eye RGB color.
	 */
	public int getEyeRGB()
	{
		return eyeRGB;
	}
	
	/**
	 * Retrieves the RGB color value of the player's lips.
	 * @return the {@code int} value representing the lip color.
	 */
	public int getLipRGB()
	{
		return lipRGB;
	}
	
	/**
	 * Sets the RGB color value for the player's lips.<br>
	 * This updates the {@code lipRGB} field in the {@link PlayerAppearance} object.
	 * @param lipRGB The new RGB color value to assign.
	 */
	public void setLipRGB(int lipRGB)
	{
		this.lipRGB = lipRGB;
	}
	
	/**
	 * Retrieves the current face shape of the player.<br>
	 * This value is stored as an {@code int}.
	 * @return the face shape value.
	 */
	public int getFaceShape()
	{
		return faceShape;
	}
	
	/**
	 * Sets the shape of the player's face.<br>
	 * This updates the {@code faceShape} field in the {@link PlayerAppearance} object.
	 * @param faceShape The new face shape value to assign.
	 */
	public void setFaceShape(int faceShape)
	{
		this.faceShape = faceShape;
	}
	
	/**
	 * Retrieves the current forehead value.<br>
	 * This value is part of the {@link PlayerAppearance} model.
	 * @return the integer value of the forehead
	 */
	public int getForehead()
	{
		return forehead;
	}
	
	/**
	 * Updates the forehead value for the player appearance.<br>
	 * This method sets the {@code forehead} field to a new integer value.
	 * @param forehead The new value to set for the forehead.
	 */
	public void setForehead(int forehead)
	{
		this.forehead = forehead;
	}
	
	/**
	 * Retrieves the height of the character's eyes.<br>
	 * This value is used to determine the vertical position of the eyes on the face.
	 * @return the current {@code eyeHeight} value.
	 */
	public int getEyeHeight()
	{
		return eyeHeight;
	}
	
	/**
	 * Sets the height of the character's eyes.<br>
	 * This updates the {@code eyeHeight} field in the {@link PlayerAppearance} object.
	 * @param eyeHeight The new height value for the eyes.
	 */
	public void setEyeHeight(int eyeHeight)
	{
		this.eyeHeight = eyeHeight;
	}
	
	/**
	 * Retrieves the spacing between the eyes of the character.<br>
	 * This value is used to determine the facial layout.
	 * @return the current {@code eyeSpace} value as an {@code int}
	 */
	public int getEyeSpace()
	{
		return eyeSpace;
	}
	
	/**
	 * Sets the spacing between the eyes for the player character.<br>
	 * This updates the {@code eyeSpace} field in the {@link PlayerAppearance} object.
	 * @param eyeSpace The new value for the distance between the eyes.
	 */
	public void setEyeSpace(int eyeSpace)
	{
		this.eyeSpace = eyeSpace;
	}
	
	/**
	 * Retrieves the width of the player's eyes.<br>
	 * This value is stored as an {@code int}.
	 * @return the current eye width.
	 */
	public int getEyeWidth()
	{
		return eyeWidth;
	}
	
	/**
	 * Sets the width of the player's eyes.<br>
	 * This updates the {@code eyeWidth} field in the {@link PlayerAppearance} object.
	 * @param eyeWidth The new width value for the eyes.
	 */
	public void setEyeWidth(int eyeWidth)
	{
		this.eyeWidth = eyeWidth;
	}
	
	/**
	 * Retrieves the size of the character's eyes.<br>
	 * This value is stored as an {@code int}.
	 * @return the current eye size.
	 */
	public int getEyeSize()
	{
		return eyeSize;
	}
	
	/**
	 * Sets the size of the player's eyes.<br>
	 * This updates the {@code eyeSize} field in the {@link PlayerAppearance} object.
	 * @param eyeSize The new size value for the eyes.
	 */
	public void setEyeSize(int eyeSize)
	{
		this.eyeSize = eyeSize;
	}
	
	/**
	 * Retrieves the current shape of the player's eyes.<br>
	 * This value is stored as an {@code int}.
	 * @return the eye shape value.
	 */
	public int getEyeShape()
	{
		return eyeShape;
	}
	
	/**
	 * Sets the shape of the player's eyes.<br>
	 * This updates the {@code eyeShape} field in the {@link PlayerAppearance} object.
	 * @param eyeShape The new shape value for the eyes.
	 */
	public void setEyeShape(int eyeShape)
	{
		this.eyeShape = eyeShape;
	}
	
	/**
	 * Retrieves the current angle of the character's eyes.<br>
	 * This value is used to determine the tilt of the eyes in the appearance model.
	 * @return the {@code int} value representing the eye angle.
	 */
	public int getEyeAngle()
	{
		return eyeAngle;
	}
	
	/**
	 * Sets the angle of the character's eyes.<br>
	 * This updates the {@code eyeAngle} field in the player appearance.
	 * @param eyeAngle The new angle value for the eyes.
	 */
	public void setEyeAngle(int eyeAngle)
	{
		this.eyeAngle = eyeAngle;
	}
	
	/**
	 * Retrieves the height of the player's eyebrows.<br>
	 * This value is used to determine the facial appearance.
	 * @return the current {@code browHeight} value.
	 */
	public int getBrowHeight()
	{
		return browHeight;
	}
	
	/**
	 * Sets the height of the player's eyebrows.<br>
	 * This updates the {@code browHeight} field in the {@link PlayerAppearance} object.
	 * @param browHeight The new height value for the eyebrows.
	 */
	public void setBrowHeight(int browHeight)
	{
		this.browHeight = browHeight;
	}
	
	/**
	 * Retrieves the current angle of the player's eyebrows.<br>
	 * This value is used to determine the facial expression.
	 * @return the {@code int} value representing the eyebrow angle
	 */
	public int getBrowAngle()
	{
		return browAngle;
	}
	
	/**
	 * Sets the angle of the character's eyebrows.<br>
	 * This updates the {@code browAngle} field in the player appearance.
	 * @param browAngle The new angle value for the eyebrows.
	 */
	public void setBrowAngle(int browAngle)
	{
		this.browAngle = browAngle;
	}
	
	/**
	 * Retrieves the shape of the player's eyebrows.
	 * @return the {@code int} value representing the eyebrow shape.
	 */
	public int getBrowShape()
	{
		return browShape;
	}
	
	/**
	 * Sets the shape of the player's eyebrows.<br>
	 * This updates the {@code browShape} field in the {@link PlayerAppearance} object.
	 * @param browShape The integer value representing the new eyebrow shape.
	 */
	public void setBrowShape(int browShape)
	{
		this.browShape = browShape;
	}
	
	/**
	 * Retrieves the character's nose value.<br>
	 * This method returns the current {@code int} value for the nose property.
	 * @return the nose value
	 */
	public int getNose()
	{
		return nose;
	}
	
	/**
	 * Sets the nose shape for the player.<br>
	 * This updates the {@code nose} field in the appearance model.
	 * @param nose The new nose value to assign.
	 */
	public void setNose(int nose)
	{
		this.nose = nose;
	}
	
	/**
	 * Retrieves the bridge height of the player's nose.<br>
	 * This value is used to determine the facial structure.
	 * @return the {@code int} value representing the nose bridge.
	 */
	public int getNoseBridge()
	{
		return noseBridge;
	}
	
	/**
	 * Sets the bridge of the character's nose.<br>
	 * This updates the {@code noseBridge} field in the player appearance.
	 * @param noseBridge The new value for the nose bridge.
	 */
	public void setNoseBridge(int noseBridge)
	{
		this.noseBridge = noseBridge;
	}
	
	/**
	 * Retrieves the width of the player's nose.<br>
	 * This value is stored as an {@code int}.
	 * @return the current nose width.
	 */
	public int getNoseWidth()
	{
		return noseWidth;
	}
	
	/**
	 * Sets the width of the player's nose.<br>
	 * This updates the {@code noseWidth} field in the character appearance.
	 * @param noseWidth The new width value for the nose.
	 */
	public void setNoseWidth(int noseWidth)
	{
		this.noseWidth = noseWidth;
	}
	
	/**
	 * Retrieves the current value of the player's nose tip.<br>
	 * This value is used to determine the shape of the character's nose.
	 * @return the {@code int} value representing the nose tip.
	 */
	public int getNoseTip()
	{
		return noseTip;
	}
	
	/**
	 * Sets the shape of the character's nose tip.<br>
	 * This updates the {@code noseTip} field in the player appearance.
	 * @param noseTip The new value for the nose tip.
	 */
	public void setNoseTip(int noseTip)
	{
		this.noseTip = noseTip;
	}
	
	/**
	 * Retrieves the current cheek value of the player.<br>
	 * This value is part of the {@link PlayerAppearance} model.
	 * @return the integer value of the cheek
	 */
	public int getCheek()
	{
		return cheek;
	}
	
	/**
	 * Sets the cheek value for the player appearance.<br>
	 * This updates the internal {@code cheek} field.
	 * @param cheek The new value to set for the cheeks.
	 */
	public void setCheek(int cheek)
	{
		this.cheek = cheek;
	}
	
	/**
	 * Retrieves the height of the player's lips.<br>
	 * This value is part of the {@link PlayerAppearance} model.
	 * @return the current lip height as an {@code int}
	 */
	public int getLipHeight()
	{
		return lipHeight;
	}
	
	/**
	 * Sets the height of the player's lips.<br>
	 * This updates the {@code lipHeight} field in the {@link PlayerAppearance} object.
	 * @param lipHeight The new height value for the lips.
	 */
	public void setLipHeight(int lipHeight)
	{
		this.lipHeight = lipHeight;
	}
	
	/**
	 * Retrieves the size of the player's mouth.<br>
	 * This value is stored as an {@code int}.
	 * @return the current mouth size.
	 */
	public int getMouthSize()
	{
		return mouthSize;
	}
	
	/**
	 * Sets the size of the player's mouth.<br>
	 * This updates the {@code mouthSize} field in the {@link PlayerAppearance} object.
	 * @param mouthSize The new size value for the mouth.
	 */
	public void setMouthSize(int mouthSize)
	{
		this.mouthSize = mouthSize;
	}
	
	/**
	 * Retrieves the size of the player's lips.<br>
	 * This value is stored as an {@code int}.
	 * @return the current lip size.
	 */
	public int getLipSize()
	{
		return lipSize;
	}
	
	/**
	 * Sets the size of the player's lips.<br>
	 * This updates the {@code lipSize} field in the appearance model.
	 * @param lipSize The new size value for the lips.
	 */
	public void setLipSize(int lipSize)
	{
		this.lipSize = lipSize;
	}
	
	/**
	 * Retrieves the current smile value of the player.<br>
	 * This value is used to determine the character's facial expression.
	 * @return the {@code int} value representing the smile.
	 */
	public int getSmile()
	{
		return smile;
	}
	
	/**
	 * Updates the player's smile value.<br>
	 * This method sets the {@code smile} field for the character appearance.
	 * @param smile The new smile value to apply.
	 */
	public void setSmile(int smile)
	{
		this.smile = smile;
	}
	
	/**
	 * Retrieves the current shape of the player's lips.<br>
	 * This value is used to determine the visual appearance of the mouth.
	 * @return the {@code int} value representing the lip shape.
	 */
	public int getLipShape()
	{
		return lipShape;
	}
	
	/**
	 * Sets the shape of the player's lips.<br>
	 * This updates the {@code lipShape} field in the character model.
	 * @param lipShape The new shape value for the lips.
	 */
	public void setLipShape(int lipShape)
	{
		this.lipShape = lipShape;
	}
	
	/**
	 * Retrieves the height of the player's jaw.<br>
	 * This value is used to determine the facial structure.
	 * @return the {@code int} value representing the jaw height.
	 */
	public int getJawHeigh()
	{
		return jawHeigh;
	}
	
	/**
	 * Sets the height of the player's jaw.<br>
	 * This updates the {@code jawHeigh} field in the {@link PlayerAppearance} object.
	 * @param jawHeigh The new value for the jaw height.
	 */
	public void setJawHeigh(int jawHeigh)
	{
		this.jawHeigh = jawHeigh;
	}
	
	/**
	 * Retrieves the chin jut value of the player.<br>
	 * This value defines a specific facial feature for the character appearance.
	 * @return the {@code int} value representing the chin jut.
	 */
	public int getChinJut()
	{
		return chinJut;
	}
	
	/**
	 * Sets the chin jut value for the player appearance.<br>
	 * This updates the {@code chinJut} field with a new integer value.
	 * @param chinJut The new value to set for the chin jut.
	 */
	public void setChinJut(int chinJut)
	{
		this.chinJut = chinJut;
	}
	
	/**
	 * Retrieves the current shape of the player's ears.
	 * @return the {@code int} value representing the ear shape.
	 */
	public int getEarShape()
	{
		return earShape;
	}
	
	/**
	 * Sets the shape of the player's ears.<br>
	 * This updates the {@code earShape} field in the {@link PlayerAppearance} object.
	 * @param earShape The integer value representing the desired ear shape.
	 */
	public void setEarShape(int earShape)
	{
		this.earShape = earShape;
	}
	
	/**
	 * Retrieves the size of the player's head.<br>
	 * This value is stored as an {@code int}.
	 * @return the current head size.
	 */
	public int getHeadSize()
	{
		return headSize;
	}
	
	/**
	 * Sets the size of the player's head.<br>
	 * This updates the {@code headSize} field in the {@link PlayerAppearance} object.
	 * @param headSize The new size value for the head.
	 */
	public void setHeadSize(int headSize)
	{
		this.headSize = headSize;
	}
	
	/**
	 * Retrieves the neck value of the player appearance.<br>
	 * This returns the current {@code int} stored for the neck property.
	 * @return the neck value
	 */
	public int getNeck()
	{
		return neck;
	}
	
	/**
	 * Sets the neck value for the player appearance.<br>
	 * This updates the {@code neck} field in this object.
	 * @param neck The new neck value to set.
	 */
	public void setNeck(int neck)
	{
		this.neck = neck;
	}
	
	/**
	 * Retrieves the length of the player's neck.<br>
	 * This value is stored as an {@code int}.
	 * @return the current neck length.
	 */
	public int getNeckLength()
	{
		return neckLength;
	}
	
	/**
	 * Sets the length of the player's neck.<br>
	 * This updates the {@code neckLength} field in the {@link PlayerAppearance} object.
	 * @param neckLength The new length value for the neck.
	 */
	public void setNeckLength(int neckLength)
	{
		this.neckLength = neckLength;
	}
	
	/**
	 * Retrieves the shoulder value of the player appearance.<br>
	 * This value is used to determine the character's physical build.
	 * @return the current {@code int} value for shoulders
	 */
	public int getShoulders()
	{
		return shoulders;
	}
	
	/**
	 * Sets the shoulder width for the player character.<br>
	 * This updates the {@code shoulders} field in the {@link PlayerAppearance} object.
	 * @param shoulders The new value for the shoulder width.
	 */
	public void setShoulders(int shoulders)
	{
		this.shoulders = shoulders;
	}
	
	/**
	 * Retrieves the size of the player's shoulders.<br>
	 * This value is part of the {@link PlayerAppearance} model.
	 * @return the shoulder size as an {@code int}
	 */
	public int getShoulderSize()
	{
		return shoulderSize;
	}
	
	/**
	 * Sets the size of the player's shoulders.<br>
	 * This updates the {@code shoulderSize} field in the {@link PlayerAppearance} object.
	 * @param shoulderSize The new size value for the shoulders.
	 */
	public void setShoulderSize(int shoulderSize)
	{
		this.shoulderSize = shoulderSize;
	}
	
	/**
	 * Retrieves the torso value of the player appearance.<br>
	 * This returns the current {@code int} stored in the torso field.
	 * @return the torso value
	 */
	public int getTorso()
	{
		return torso;
	}
	
	/**
	 * Sets the torso value for the player appearance.<br>
	 * This updates the {@code torso} field with a new integer value.
	 * @param torso The new value to set for the torso.
	 */
	public void setTorso(int torso)
	{
		this.torso = torso;
	}
	
	/**
	 * Retrieves the character's chest value.<br>
	 * This method returns the current {@code chest} property.
	 * @return the integer value of the chest
	 */
	public int getChest()
	{
		return chest;
	}
	
	/**
	 * Sets the character's chest value.<br>
	 * This updates the {@code chest} field in the {@link PlayerAppearance} object.
	 * @param chest The new value for the chest attribute.
	 */
	public void setChest(int chest)
	{
		this.chest = chest;
	}
	
	/**
	 * Retrieves the current waist size of the player.<br>
	 * This value is stored as an {@code int}.
	 * @return the waist size.
	 */
	public int getWaist()
	{
		return waist;
	}
	
	/**
	 * Sets the waist size for the player appearance.<br>
	 * This updates the {@code waist} field in the current object.
	 * @param waist The new value for the waist size.
	 */
	public void setWaist(int waist)
	{
		this.waist = waist;
	}
	
	/**
	 * Retrieves the hip width of the player character.<br>
	 * This value is used to determine the body shape.
	 * @return the current {@code hips} value as an {@code int}.
	 */
	public int getHips()
	{
		return hips;
	}
	
	/**
	 * Sets the hip width for the player character.<br>
	 * This updates the {@code hips} field in the {@link PlayerAppearance} object.
	 * @param hips The new value for the hip size.
	 */
	public void setHips(int hips)
	{
		this.hips = hips;
	}
	
	/**
	 * Retrieves the thickness of the player's arms.<br>
	 * This value is used to determine the character's physical build.
	 * @return the current {@code armThickness} value.
	 */
	public int getArmThickness()
	{
		return armThickness;
	}
	
	/**
	 * Sets the thickness of the player's arms.<br>
	 * This updates the {@code armThickness} field in the {@link PlayerAppearance} object.
	 * @param armThickness The new thickness value for the arms.
	 */
	public void setArmThickness(int armThickness)
	{
		this.armThickness = armThickness;
	}
	
	/**
	 * Retrieves the length of the player's arms.<br>
	 * This value is stored as an {@code int}.
	 * @return the current arm length.
	 */
	public int getArmLength()
	{
		return armLength;
	}
	
	/**
	 * Sets the length of the player's arms.<br>
	 * This updates the {@code armLength} field in the {@link PlayerAppearance} object.
	 * @param armLength The new length value for the arms.
	 */
	public void setArmLength(int armLength)
	{
		this.armLength = armLength;
	}
	
	/**
	 * Retrieves the size of the player's hand.<br>
	 * This value is stored in the {@code handSize} field.
	 * @return the current hand size as an {@code int}.
	 */
	public int getHandSize()
	{
		return handSize;
	}
	
	/**
	 * Sets the size of the player's hands.<br>
	 * This updates the {@code handSize} field in the {@link PlayerAppearance} object.
	 * @param handSize The new size value for the hands.
	 */
	public void setHandSize(int handSize)
	{
		this.handSize = handSize;
	}
	
	/**
	 * Retrieves the thickness of the player's legs.<br>
	 * This value is used to determine the character's physical build.
	 * @return the {@code int} value representing leg thickness
	 */
	public int getLegThickness()
	{
		return legThickness;
	}
	
	/**
	 * Sets the thickness of the player's legs.<br>
	 * This updates the {@code legThickness} field in the {@link PlayerAppearance} object.
	 * @param legThickness The new thickness value for the legs.
	 */
	public void setLegThickness(int legThickness)
	{
		this.legThickness = legThickness;
	}
	
	/**
	 * Retrieves the length of the player's legs.<br>
	 * This value is stored as an {@code int}.
	 * @return the current leg length.
	 */
	public int getLegLength()
	{
		return legLength;
	}
	
	/**
	 * Sets the length of the player's legs.<br>
	 * This updates the {@code legLength} field in the {@link PlayerAppearance} object.
	 * @param legLength The new length value for the legs.
	 */
	public void setLegLength(int legLength)
	{
		this.legLength = legLength;
	}
	
	/**
	 * Retrieves the current size of the player's feet.<br>
	 * This value is stored as an {@code int}.
	 * @return the foot size of the character.
	 */
	public int getFootSize()
	{
		return footSize;
	}
	
	/**
	 * Sets the size of the player's feet.<br>
	 * This updates the {@code footSize} field in the {@link PlayerAppearance} object.
	 * @param footSize The new size value for the feet.
	 */
	public void setFootSize(int footSize)
	{
		this.footSize = footSize;
	}
	
	/**
	 * Retrieves the current facial rate of the player.<br>
	 * This value is stored in the {@code facialRate} field.
	 * @return the integer value of the facial rate.
	 */
	public int getFacialRate()
	{
		return facialRate;
	}
	
	/**
	 * Sets the facial rate for the player appearance.<br>
	 * This updates the {@code facialRate} field in this object.
	 * @param facialRate The new value to set for the facial rate.
	 */
	public void setFacialRate(int facialRate)
	{
		this.facialRate = facialRate;
	}
	
	/**
	 * Retrieves the current voice type of the player.<br>
	 * This value is stored as an {@code int}.
	 * @return The player's voice identifier.
	 */
	public int getVoice()
	{
		return voice;
	}
	
	/**
	 * Sets the character's voice type.<br>
	 * This updates the {@code voice} field in the {@link PlayerAppearance} object.
	 * @param voice The new voice identifier to assign.
	 */
	public void setVoice(int voice)
	{
		this.voice = voice;
	}
	
	/**
	 * Retrieves the current height of the player.<br>
	 * This value is stored as a {@code float}.
	 * @return The player's height.
	 */
	public float getHeight()
	{
		return height;
	}
	
	/**
	 * Sets the character's height.<br>
	 * This updates the {@code height} field in the {@link PlayerAppearance} object.
	 * @param height The new height value to set.
	 */
	public void setHeight(float height)
	{
		this.height = height;
	}
	
	/**
	 * Creates and returns a copy of this {@link PlayerAppearance} object.<br>
	 * This method uses the default cloning mechanism provided by the {@code Cloneable} interface.
	 * @return A new {@code Object} that is a copy of this instance, or {@code null} if cloning fails.
	 */
	@Override
	public Object clone()
	{
		Object newObject = null;
		
		try
		{
			newObject = super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		
		return newObject;
	}
}
