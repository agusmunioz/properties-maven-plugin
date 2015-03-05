package org.softwaredistilled.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.softwaredistilled.properties.MissingKeyException;
import org.softwaredistilled.properties.NamedProperties;

import com.google.common.collect.Sets;

/**
 * Utility class for handling properties files.
 * 
 * @author agusmunioz
 * 
 */
public class PropertiesUtils {

	private static final String PROPERTIES = ".properties";

	/**
	 * Finds any properties file that matches the provided regular expression in
	 * the specified folder.
	 * 
	 * @param directory
	 *            folder where to search files.
	 * 
	 * @param matcher
	 *            regular expression for matching file names.
	 * 
	 * @return a not null collection of matching files.
	 * 
	 * @throws MojoExecutionException
	 *             if there is an error when loading properties files.
	 */
	public static Collection<NamedProperties> find(String directory,
			String matcher) throws MojoExecutionException {

		Collection<File> files = FileUtils.listFiles(new File(directory),
				new WildcardFileFilter(matcher + PROPERTIES),
				DirectoryFileFilter.DIRECTORY);

		Collection<NamedProperties> found = new LinkedList<NamedProperties>();

		try {

			for (File file : files) {

				NamedProperties properties = new NamedProperties(file.getName());
				properties.load(new FileInputStream(file));
				found.add(properties);

			}

		} catch (Exception e) {
			throw new MojoExecutionException("Error when loading properties.",
					e);
		}

		return found;

	}

	/**
	 * Checks if all properties have the same keys.
	 * 
	 * @param properties
	 *            the list of properties to check.
	 * 
	 * @throws MissingKeyException
	 *             if a properties don't have a key.
	 */
	public static void check(Collection<NamedProperties> properties)
			throws MissingKeyException {

		Collection<String> names = new LinkedList<String>();

		Set<Object> all = new HashSet<Object>();

		Set<Object> missing = new HashSet<Object>();

		for (NamedProperties nproperties : properties) {

			names.add(nproperties.getName());
			all.addAll(nproperties.keySet());
		}

		for (NamedProperties target : properties) {

			Set<Object> difference = Sets.difference(all, target.keySet());

			if (!difference.isEmpty()) {
				missing.addAll(difference);
			}
		}

		if (!missing.isEmpty()) {
			throw new MissingKeyException(names, missing);
		}
	}
}
