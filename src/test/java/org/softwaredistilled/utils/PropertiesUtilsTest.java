package org.softwaredistilled.utils;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;
import org.softwaredistilled.properties.MissingKeyException;
import org.softwaredistilled.properties.NamedProperties;

/**
 * Unit test for {@link PropertiesUtils}.
 * 
 * @author agusmunioz
 * 
 */
public class PropertiesUtilsTest {

	/**
	 * Test tree properties files have the same keys.
	 */
	@Test
	public void sameKeys() {

		NamedProperties one = new NamedProperties("ONE");
		one.setProperty("key.one", "A value for one");
		one.setProperty("key.two", "A value for two");

		NamedProperties two = new NamedProperties("TWO");
		two.setProperty("key.one", "Another value for one");
		two.setProperty("key.two", "Another value for two");

		NamedProperties tree = new NamedProperties("TREE");
		tree.setProperty("key.one", "A third value for one");
		tree.setProperty("key.two", "A third value for two");

		try {

			PropertiesUtils.check(Arrays.asList(one, two, tree));

		} catch (MissingKeyException e) {

			Assert.fail("Properties with same keys have missing keys");
		}

	}

	/**
	 * Test for missing keys in properties.
	 */
	@Test
	public void missingKeys() {

		NamedProperties one = new NamedProperties("ONE");
		one.setProperty("key.one", "A value for one");
		one.setProperty("key.two", "A value for two");

		NamedProperties two = new NamedProperties("TWO");
		two.setProperty("key.zero", "A value for zero");
		two.setProperty("key.two", "Another value for two");

		NamedProperties tree = new NamedProperties("TREE");
		tree.setProperty("key.two", "A third value for two");

		try {

			PropertiesUtils.check(Arrays.asList(one, two, tree));
			Assert.fail("Properties with missing keys have the same keys");

		} catch (MissingKeyException e) {

			Assert.assertEquals("Invalid properties names", "[ONE, TREE, TWO]",
					e.getNames());

			Assert.assertEquals("Invalid missing keys", "[key.one, key.zero]",
					e.getKeys());
		}

	}
}
