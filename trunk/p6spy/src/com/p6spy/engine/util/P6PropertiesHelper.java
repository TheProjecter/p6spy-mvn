package com.p6spy.engine.util;

import java.util.Properties;

public final class P6PropertiesHelper {

	public P6PropertiesHelper() {

	}
	
	/**
	 * Get a property value as a string.
	 */
	public static String getString(String propertyName, Properties properties, String defaultValue) {
		String value = extractPropertyValue(propertyName, properties);
		return value == null ? defaultValue : value.trim();
	}

	/**
	 * Get a property value as a boolean. Shorthand for calling
	 */
	public static boolean getBoolean(String propertyName, Properties properties) {
		return getBoolean(propertyName, properties, false);
	}
	
	/**
	 * Get a property value as a boolean.
	 */
	public static boolean getBoolean(String propertyName, Properties properties, boolean defaultValue) {
		String value = extractPropertyValue(propertyName, properties);
		return value == null ? defaultValue : Boolean.valueOf(value).booleanValue();
	}
	
	/**
	 * Extract a property value by name from the given properties object.
	 */
	public static String extractPropertyValue(String propertyName, Properties properties) {
		String value = properties.getProperty(propertyName);
		if (value == null) {
			return null;
		}
		value = value.trim();
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		return value;
	}

}
