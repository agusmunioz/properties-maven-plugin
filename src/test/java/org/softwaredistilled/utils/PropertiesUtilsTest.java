package org.softwaredistilled.utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.softwaredistilled.properties.MissingKeyException;
import org.softwaredistilled.properties.NamedProperties;

/**
 * Unit test for {@link PropertiesUtils}.
 * 
 * @author agusmunioz
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
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

	/**
	 * Test {@link PropertiesUtils#find(String, String)} when properties files
	 * are matched.
	 */
	@Test
	public void filesMatched() {

		try {

			File es = new File(getClass().getResource(
					"/properties/i18n_es.properties").toURI());

			File en = new File(getClass().getResource(
					"/properties/i18n_en.properties").toURI());

			Collection<File> files = Arrays.asList(es, en);

			PowerMockito.mockStatic(FileUtils.class);

			PowerMockito.when(
					FileUtils.listFiles(Mockito.isA(File.class),
							Mockito.isA(WildcardFileFilter.class),
							Mockito.isA(DirectoryFileFilter.class)))
					.thenReturn(files);

			Collection<NamedProperties> properties = PropertiesUtils.find("",
					"i18n_??");

			Assert.assertNotNull("Null properties", properties);

			Assert.assertEquals("Unexpected amount of properties.",
					files.size(), properties.size());

		} catch (MojoExecutionException e) {

			Assert.fail("Error when looking for existing properties files");

		} catch (URISyntaxException e) {
			Assert.fail("Error when looking for test properties files");
		}

	}

	/**
	 * Test {@link PropertiesUtils#find(String, String)} when properties files
	 * are found.
	 */
	@Test
	public void filesNotMatched() {

		try {

			Collection<File> files = Arrays.asList();

			PowerMockito.mockStatic(FileUtils.class);

			PowerMockito.when(
					FileUtils.listFiles(Mockito.isA(File.class),
							Mockito.isA(WildcardFileFilter.class),
							Mockito.isA(DirectoryFileFilter.class)))
					.thenReturn(files);

			Collection<NamedProperties> properties = PropertiesUtils.find("",
					"i18n_??");

			Assert.assertNotNull("Null properties", properties);

			Assert.assertTrue("Properties returned when not matched",
					properties.isEmpty());

		} catch (MojoExecutionException e) {

			Assert.fail("Error when looking for non matching properties files");

		}
	}

	/**
	 * Test {@link PropertiesUtils#find(String, String)} when there is an error
	 * loading a properties file.
	 */
	@Test
	public void loadingError() {

		try {

			Collection<File> files = Arrays.asList(new File(
					"/unexistent/file.properties"));

			PowerMockito.mockStatic(FileUtils.class);

			PowerMockito.when(
					FileUtils.listFiles(Mockito.isA(File.class),
							Mockito.isA(WildcardFileFilter.class),
							Mockito.isA(DirectoryFileFilter.class)))
					.thenReturn(files);

			PropertiesUtils.find("", "i18n_??");

			Assert.fail("Expected failure in a loading properties error.");

		} catch (MojoExecutionException e) {

		}
	}
}
