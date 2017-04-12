/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.core.config;

import java.io.File;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.config.Configuration;
import com.b2international.commons.config.ConfigurationFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Snow Owl Application configuration class. The configuration can be manually
 * constructed or it can be constructed from a JSON file using a
 * {@link ConfigurationFactory}.
 * 
 * @since 3.3
 */
public class SnowOwlConfiguration extends Configuration {

	private transient String installationDirectory = "";
	
	@NotEmpty
	private String configurationDirectory = "configuration";
	
	@NotEmpty
	private String resourceDirectory = "resources";
	
	@NotEmpty
	private String defaultsDirectory = "resources/defaults";
	
	private boolean systemUserNeeded = false;
	
	private String description = "You Know, for Terminologies";

	private boolean gzip = true;

	@JsonProperty
	public String getDescription() {
		return description;
	}
	
	@JsonProperty
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonProperty
	public String getConfigurationDirectory() {
		return configurationDirectory;
	}

	@JsonProperty
	public String getResourceDirectory() {
		return resourceDirectory;
	}

	@JsonProperty
	public String getDefaultsDirectory() {
		return defaultsDirectory;
	}

	@JsonProperty
	public void setConfigurationDirectory(String configurationDirectory) {
		this.configurationDirectory = configurationDirectory;
	}

	@JsonProperty
	public void setDefaultsDirectory(String defaultsDirectory) {
		this.defaultsDirectory = defaultsDirectory;
	}

	@JsonProperty
	public void setResourceDirectory(String resourceDirectory) {
		this.resourceDirectory = resourceDirectory;
	}

	@JsonProperty("systemUser")
	public boolean isSystemUserNeeded() {
		return systemUserNeeded;
	}

	@JsonProperty("systemUser")
	public void setSystemUserNeeded(boolean systemUserNeeded) {
		this.systemUserNeeded = systemUserNeeded;
	}
	
	@JsonProperty
	public boolean isGzip() {
		return gzip;
	}
	
	@JsonProperty
	public void setGzip(boolean gzip) {
		this.gzip = gzip;
	}
	
	/**
	 * @param installationDirectory the installationDirectory to set
	 */
	public void setInstallationDirectory(String installationDirectory) {
		this.installationDirectory = installationDirectory;
	}
	
	/**
	 * Returns the sub directory under the current installation directory.
	 * 
	 * @param subDirectory
	 * @return
	 */
	public File getInstallationSubDirectory(String subDirectory) {
		return new File(installationDirectory, subDirectory);
	}

}