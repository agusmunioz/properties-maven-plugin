package org.softwaredistilled.test.utils;

import java.lang.reflect.Field;

/**
 * Utility class for using reflection in tests.
 * 
 * @author agusmunioz
 * 
 */
public class ReflectionTestUtils {

	/**
	 * Sets an object field with a value.
	 * 
	 * @param object
	 *            the object whose field is being set.
	 * @param name
	 *            the field name.
	 * @param value
	 *            the value to be set.
	 */
	public static void setField(Object object, String name, Object value) {

		try {

			Field field = object.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(object, value);

		} catch (Exception e) {

		}

	}

}
