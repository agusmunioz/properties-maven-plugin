package org.softwaredistilled.properties;

import java.util.Collection;
import java.util.TreeSet;

/**
 * Exception thrown when a properties doesn't have an expected key.
 * 
 * @author agusmunioz
 * 
 */
public class MissingKeyException extends Exception {

	private static final long serialVersionUID = 1L;

	private Collection<String> names;

	private Collection<Object> keys;

	/**
	 * Creates an initialized {@link MissingKeyException}.
	 * 
	 * @param names
	 *            the properties names.
	 * @param keys
	 *            the missing keys.
	 */
	public MissingKeyException(Collection<String> names, Collection<Object> keys) {
		this.names = new TreeSet<String>(names);
		this.keys = new TreeSet<Object>(keys);
	}

	/**
	 * The name of properties files with missing keys.
	 * 
	 * @return a sorted list of properties names in a String.
	 */
	public String getNames() {

		return names.toString();
	}

	/**
	 * The list of missing keys.
	 * 
	 * @return a not null sorted list of keys in a String.
	 */
	public String getKeys() {
		return keys.toString();
	}

}
