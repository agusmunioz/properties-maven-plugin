package org.softwaredistilled.mojo;

import java.util.Collection;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.softwaredistilled.properties.MissingKeyException;
import org.softwaredistilled.properties.NamedProperties;
import org.softwaredistilled.utils.PropertiesUtils;

/**
 * Mojo that validates if a set of .properties files have the same keys.
 * 
 * @author agusmunioz
 * 
 */
@Mojo(name = "check-properties", defaultPhase = LifecyclePhase.VALIDATE)
public class CheckPropertiesMojo extends AbstractMojo {

	@Component
	private MavenProject project;

	@Parameter(alias = "check", required = true)
	private String[] matchers;

	@Override
	public void execute() throws MojoExecutionException {

		List<Resource> resources = this.project.getResources();

		for (Resource resource : resources) {
			this.check(resource);
		}
	}

	/**
	 * Checks the resource folder for properties validation.
	 * 
	 * @param resource
	 *            the resource folder.
	 * 
	 * @throws MojoExecutionException
	 *             if there are missing keys in any properties file.
	 */
	private void check(Resource resource) throws MojoExecutionException {

		for (String matcher : this.matchers) {

			Collection<NamedProperties> properties = PropertiesUtils.find(
					resource.getDirectory(), matcher);

			if (properties.isEmpty()) {

				getLog().warn("No properties found with expression: " + matcher);

			} else {

				this.check(properties);
			}
		}
	}

	/**
	 * Checks if all properties have the same keys.
	 * 
	 * @param properties
	 *            the properties to check.
	 * 
	 * @throws MojoExecutionException
	 *             if there are missing keys in any properties file.
	 */
	private void check(Collection<NamedProperties> properties)
			throws MojoExecutionException {

		try {

			PropertiesUtils.check(properties);

		} catch (MissingKeyException e) {

			getLog().error(
					"Missing keys in properties files: " + e.getNames()
							+ ". Keys: " + e.getKeys());

			throw new MojoExecutionException("Missing keys");
		}

	}

}
