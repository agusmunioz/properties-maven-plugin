package org.softwaredistilled.mojo;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.softwaredistilled.properties.MissingKeyException;
import org.softwaredistilled.properties.NamedProperties;
import org.softwaredistilled.test.utils.ReflectionTestUtils;
import org.softwaredistilled.utils.PropertiesUtils;

/**
 * Unit test for {@link CheckPropertiesMojo}.
 * 
 * @author agusmunioz
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PropertiesUtils.class)
public class CheckPropertiesMojoTest {

	private static String[] MATCHERS = new String[] { "i18n_??" };

	private static String RESOURCE_FOLDER = "src/test/resouces";

	/**
	 * Test the Mojo when all properties files have the same keys and the
	 * validation is successful.
	 */
	@Test
	public void sameKeys() {

		CheckPropertiesMojo mojo = this.buildMojo();

		PowerMockito.mockStatic(PropertiesUtils.class);

		NamedProperties propertiesOne = new NamedProperties("ONE");
		propertiesOne.setProperty("key.one", "value one");

		NamedProperties propertiesTwo = new NamedProperties("Two");
		propertiesOne.setProperty("key.two", "value one");

		this.mockFind(RESOURCE_FOLDER, MATCHERS[0],
				Arrays.asList(propertiesOne, propertiesTwo));

		try {

			mojo.execute();

		} catch (MojoExecutionException e) {

			Assert.fail("Mojo execution failed with properties having the same keys.");
		}

		Mockito.verify(mojo.getLog(), Mockito.times(0)).warn(
				Mockito.anyString());

	}

	/**
	 * Test the Mojo when there are missing keys.
	 */
	@Test
	public void differentKeys() {

		CheckPropertiesMojo mojo = this.buildMojo();

		NamedProperties propertiesOne = new NamedProperties(
				"i18n_es.properties");
		propertiesOne.setProperty("say.hello", "Hola");

		NamedProperties propertiesTwo = new NamedProperties(
				"i18n_en.properties");
		propertiesOne.setProperty("say.bye", "Bye");

		PowerMockito.mockStatic(PropertiesUtils.class);

		List<NamedProperties> properties = Arrays.asList(propertiesOne,
				propertiesTwo);

		mockFind(RESOURCE_FOLDER, MATCHERS[0], properties);

		Collection<String> names = Arrays.asList(propertiesOne.getName(),
				propertiesTwo.getName());

		Collection<Object> keys = new LinkedList<Object>();
		keys.add("say.hello");
		keys.add("say.bye");

		try {

			PowerMockito.doThrow(new MissingKeyException(names, keys)).when(
					PropertiesUtils.class, "check", properties);

			mojo.execute();

			Assert.fail("Mojo failure was expected");

		} catch (MojoExecutionException e) {

			Mockito.verify(mojo.getLog(), Mockito.times(0)).warn(
					Mockito.anyString());
			Mockito.verify(mojo.getLog(), Mockito.times(1)).error(
					Mockito.anyString());

		} catch (Exception e) {

			Assert.fail("Mocking failed.");
		}
	}

	/**
	 * Test the Mojo when no properties are found based on the matcher
	 * expressions.
	 */
	@Test
	public void noPropertiesMatched() {

		CheckPropertiesMojo mojo = buildMojo();

		PowerMockito.mockStatic(PropertiesUtils.class);

		List<NamedProperties> properties = Arrays.asList();

		mockFind(RESOURCE_FOLDER, MATCHERS[0], properties);

		try {

			mojo.execute();

			Mockito.verify(mojo.getLog(), Mockito.times(1)).warn(
					Mockito.anyString());

		} catch (MojoExecutionException e) {
			Assert.fail("Mojo execution failed when no properties were matched.");
		}
	}

	/**
	 * Builds the Mojo, with some fields mocked.
	 * 
	 * @return a configure {@link CheckPropertiesMojo} instance.
	 */
	private CheckPropertiesMojo buildMojo() {

		CheckPropertiesMojo mojo = new CheckPropertiesMojo();

		Log log = Mockito.mock(Log.class);

		mojo.setLog(log);

		Resource resource = new Resource();
		resource.setDirectory(RESOURCE_FOLDER);

		MavenProject project = Mockito.mock(MavenProject.class);

		Mockito.when(project.getResources())
				.thenReturn(Arrays.asList(resource));

		ReflectionTestUtils.setField(mojo, "project", project);

		ReflectionTestUtils.setField(mojo, "matchers", MATCHERS);

		return mojo;
	}

	/**
	 * Mocks {@link PropertiesUtils#find(String, String)} method.
	 * 
	 * @param directory
	 *            the directory named passed to find method.
	 * 
	 * @param matcher
	 *            the matcher expression.
	 * 
	 * @param properties
	 *            the list of properties to return.
	 */
	private void mockFind(String directory, String matcher,
			Collection<NamedProperties> properties) {

		try {

			PowerMockito.when(PropertiesUtils.find(directory, matcher))
					.thenReturn(properties);

		} catch (MojoExecutionException e1) {

		}
	}
}
