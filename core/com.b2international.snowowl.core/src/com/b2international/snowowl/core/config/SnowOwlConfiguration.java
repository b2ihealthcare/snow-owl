/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.config.Configuration;
import com.b2international.commons.config.ConfigurationFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Snow Owl Application configuration class. The configuration can be manually
 * constructed or it can be constructed from a YAML file using a
 * {@link ConfigurationFactory}.
 * 
 * @since 3.3
 */
public class SnowOwlConfiguration extends Configuration {

	private boolean systemUserNeeded = false;
	
	private String description = "You Know, for Terminologies";

	private boolean gzip = true;
	
	private Paths paths = new Paths();
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonProperty("systemUser")
	public boolean isSystemUserNeeded() {
		return systemUserNeeded;
	}

	@JsonProperty("systemUser")
	public void setSystemUserNeeded(boolean systemUserNeeded) {
		this.systemUserNeeded = systemUserNeeded;
	}
	
	public boolean isGzip() {
		return gzip;
	}
	
	public void setGzip(boolean gzip) {
		this.gzip = gzip;
	}
	
	@JsonProperty("path")
	public Paths getPaths() {
		return paths;
	}
	
	@JsonProperty("path")
	public void setPaths(Paths paths) {
		this.paths = paths;
	}
	
	public static final class Paths {
		
		private String data;
		
		public String getData() {
			return data;
		}
		
		public void setData(String data) {
			this.data = data;
		}
		
	}
	
}