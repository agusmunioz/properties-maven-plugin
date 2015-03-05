package org.softwaredistilled.properties;

import java.util.Properties;

/**
 * A {@link Properties} extension that provides the properties file name.
 * 
 * @author agusmunioz
 * 
 */
public class NamedProperties extends Properties {

	private static final long serialVersionUID = 1L;

	private String name;

	/**
	 * Creates an initialized {@link NamedProperties}.
	 * 
	 * @param name
	 *            the properties file name.
	 */
	public NamedProperties(String name) {
		this.name = name;
	}

	/**
	 * The properties name.
	 * 
	 * @return a not null name.
	 */
	public String getName() {
		return name;
	}

	@Override
	public synchronized String toString() {

		return this.getName() + ": " + super.toString();
	}

}
