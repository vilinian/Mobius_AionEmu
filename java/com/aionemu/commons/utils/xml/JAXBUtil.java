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
package com.aionemu.commons.utils.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;

/**
 * This utility class provides helper methods for {@code JAXB} operations.<br>
 * It simplifies the process of marshalling and unmarshalling XML data between Java objects and strings.
 */
public class JAXBUtil
{
	/**
	 * Converts a Java object into an XML string.<br>
	 * This method uses {@link JAXBContext} to perform the serialization.<br>
	 * The output is formatted with UTF-8 encoding and pretty printing.
	 * @param obj The object to be converted into a string.
	 * @return A string representation of the provided object in XML format.
	 */
	public static String serialize(Object obj)
	{
		try
		{
			final JAXBContext jc = JAXBContext.newInstance(obj.getClass());
			final Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			final StringWriter sw = new StringWriter();
			m.marshal(obj, sw);
			return sw.toString();
		}
		catch (JAXBException e)
		{
			throw new RuntimeException("Failed to marshall object of class " + obj.getClass().getName(), e);
		}
	}
	
	/**
	 * Converts a Java object into an {@link Document} object.<br>
	 * This method first calls {@code serialize} to get a string representation.<br>
	 * It then parses that string into a DOM tree.
	 * @param obj The object to be converted.
	 * @return A {@code Document} representing the serialized object.
	 */
	public static Document serializeToDocument(Object obj)
	{
		final String s = serialize(obj);
		return XmlUtils.getDocument(s);
	}
	
	/**
	 * Converts an XML string into a Java object.<br>
	 * This method uses {@code JAXB} to perform the conversion.<br>
	 * It maps the data based on the provided {@code clazz}.
	 * @param <T>
	 * @param s The XML string to be converted.
	 * @param clazz The class type of the object to create.
	 * @return The deserialized object of type {@code T}.
	 */
	public static <T> T deserialize(String s, Class<T> clazz)
	{
		return deserialize(s, clazz, (Schema) null);
	}
	
	/**
	 * Converts an XML string into a Java object.<br>
	 * This method uses a specific {@code URL} to load the validation schema.<br>
	 * It is a helper for the {@code Class, Schema)} method.
	 * @param <T>
	 * @param s The XML string to be parsed.
	 * @param clazz The class type of the object to create.
	 * @param schemaURL The location of the XML schema file.
	 * @return The deserialized object of type {@code T}.
	 */
	public static <T> T deserialize(String s, Class<T> clazz, URL schemaURL)
	{
		final Schema schema = XmlUtils.getSchema(schemaURL);
		return deserialize(s, clazz, schema);
	}
	
	/**
	 * Converts an XML string into a Java object.<br>
	 * This method uses a specific schema for validation during the process.<br>
	 * It is a helper method that calls {@code Class, Schema)}.
	 * @param <T>
	 * @param s The XML string to be converted.
	 * @param clazz The class type of the object to create.
	 * @param schemaString The path or identifier for the validation schema.
	 * @return The deserialized object of type {@code T}.
	 */
	public static <T> T deserialize(String s, Class<T> clazz, String schemaString)
	{
		final Schema schema = XmlUtils.getSchema(schemaString);
		return deserialize(s, clazz, schema);
	}
	
	/**
	 * Converts an XML {@code Document} into a Java object.<br>
	 * This method uses the provided {@code schemaString} for validation.<br>
	 * It internally converts the {@code Document} to a string before parsing.
	 * @param <T>
	 * @param xml The source {@code Document} containing the XML data.
	 * @param clazz The class type to map the XML data into.
	 * @param schemaString The XSD schema used to validate the XML.
	 * @return The deserialized object of type {@code T}.
	 */
	public static <T> T deserialize(Document xml, Class<T> clazz, String schemaString)
	{
		final String xmlAsString = XmlUtils.getString(xml);
		return deserialize(xmlAsString, clazz, schemaString);
	}
	
	/**
	 * Converts an XML string into a Java object.<br>
	 * This method uses the provided {@code Schema} for validation during the process.<br>
	 * It will throw a {@code RuntimeException} if the conversion fails.
	 * @param <T>
	 * @param s The XML string to be converted.
	 * @param clazz The class type of the object to create.
	 * @param schema The {@link Schema} used to validate the XML.
	 * @return The deserialized object of type {@code T}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(String s, Class<T> clazz, Schema schema)
	{
		try
		{
			final JAXBContext jc = JAXBContext.newInstance(clazz);
			final Unmarshaller u = jc.createUnmarshaller();
			u.setSchema(schema);
			return (T) u.unmarshal(new StringReader(s));
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to unmarshall class " + clazz.getName() + " from xml:\n " + s, e);
		}
	}
}
