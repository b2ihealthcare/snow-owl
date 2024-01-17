/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons.config;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.b2international.commons.ClassUtils;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Abstract base configuration class to use for application configuration.
 * 
 * It supports dynamic configuration object resolution.
 * 
 * @since 3.4
 */
public abstract class Configuration {

	private Map<String, JsonNode> moduleConfigurations = newHashMap();
	private Map<Class<?>, Object> configurationClassToNode = newHashMap();

	/**
	 * Returns the module configuration for the given type.
	 * 
	 * @param configType
	 * @return the desired configuration object from the resolved elements,
	 *         never <code>null</code>.
	 */
	public <T> T getModuleConfig(Class<T> configType) {
		final Object configObject = configurationClassToNode.get(configType);
		if (configObject == null) {
			try {
				return configType.newInstance();
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Failed to create module configuration. Default ctor is required in type " + configType.getName());
			}
		}
		return ClassUtils.checkAndCast(configObject, configType);
	}

	@JsonAnySetter
	/* package */void setModuleConfig(String module, JsonNode config) {
		moduleConfigurations.put(module, config);
	}

	@JsonAnyGetter
	/* package */Map<String, JsonNode> getModuleConfigurations() {
		return moduleConfigurations;
	}

	/**
	 * @return the configurationClassToNode
	 */
	/* package */Map<Class<?>, Object> getConfigurationClassToNode() {
		return configurationClassToNode;
	}

}