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
package com.b2international.commons.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * A factory class for loading JSON and YAML configuration files, binding them
 * to configuration objects. 
 * <p><i>Important note: only YAML/UNIX style comments are
 * allowed in configuration files, YAML dataformat does not support Java/C++
 * style comments in files.</i></p>
 * 
 * @since 3.3
 * @param <T>
 *            - the type of the configuration object to produce
 */
public class ConfigurationFactory<T> {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFactory.class);
	private Class<T> klass;
	private ObjectMapper mapper;
	private Validator validator;
	private Map<String, Class<?>> additionalConfigurations = newHashMap();

	public ConfigurationFactory(Class<T> klass, Validator validator) {
		this(klass, new ObjectMapper(new YAMLFactory()), validator);
	}
	
	public ConfigurationFactory(Class<T> klass, ObjectMapper mapper, Validator validator) {
		this.klass = checkNotNull(klass, "class");
		this.mapper = checkNotNull(mapper, "mapper");
		this.validator = checkNotNull(validator, "validator");
		this.mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		this.mapper.configure(Feature.ALLOW_COMMENTS, false);
		this.mapper.configure(Feature.ALLOW_YAML_COMMENTS, true);
	}
	
	/**
	 * @param additionalConfigurations the additionalConfigurations to set
	 */
	public ConfigurationFactory<T> setAdditionalModules(Map<String, Class<?>> additionalConfigurations) {
		this.additionalConfigurations = checkNotNull(additionalConfigurations, "additionalConfigurations");
		return this;
	}
	
	/**
     * Loads, parses, binds, then return a configuration object.
     *
     * @param provider - the provider to to use for reading configuration files
     * @param path     - the path of the configuration file
     * @return a configuration object
     * @throws RuntimeException            if there is an error reading or parsing the file
     */
	public T build(ConfigurationSourceProvider provider, final String path) {
		try (InputStream input = provider.open(checkNotNull(path))) {
			final JsonNode node = mapper.readTree(mapper.getFactory().createParser(input));
			if (node == null) {
				LOG.info("No configuration found at {}, falling back to default configurations", path);
				return klass.newInstance();
			}
			return build(node, path);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
     * Loads, parses, binds, then return a configuration object from a file.
     *
     * @param file - the file to parse
     * @return a configuration object
     * @throws RuntimeException            if there is an error reading or parsing the file
     */
	public T build(File file) throws IOException {
		checkNotNull(file, "file");
		return build(new FileConfigurationSourceProvider(), file.toString());
	}
	
	/**
	 * Loads, parses, binds, then return a configuration object from a {@link URL}.
	 * 
	 * @param url - the url to parse
     * @return a configuration object
     * @throws RuntimeException if there is an error reading or parsing the file
	 */
	public T build(URL url) throws IOException {
		checkNotNull(url, "url");
		return build(new URLConfigurationSourceProvider(), url.toString());
	}
	
	/**
     * Loads, parses, binds, then return a configuration object from a file.
     *
     * @param provider - the provider to to use for reading configuration files
     * @param path     - the path of the configuration file
     * @return a configuration object
     * @throws RuntimeException            if there is an error reading or parsing the file
     */
    public T build() throws IOException {
        return build(JsonNodeFactory.instance.objectNode(), "default configuration");
    }

    private T build(JsonParser jp, String path) throws IOException {
    	try {
    		final T config = mapper.readValue(jp, klass);
    		if (config instanceof Configuration) {
    			resolveModuleConfigs((Configuration) config);
    		}
    		validate(config, path);
    		return config;
    	} catch (UnrecognizedPropertyException e) {
    		Collection<Object> knownProperties = e.getKnownPropertyIds();
    		Collection<String> properties = new ArrayList<>(knownProperties.size());
    		for (Object property : knownProperties) {
    			properties.add(property.toString());
    		}
    		throw new RuntimeException(String.format("Unrecognized field '%s' in config file '%s'", e.getPropertyName(), path));
    	} catch (InvalidFormatException e) {
    		String sourceType = e.getValue().getClass().getSimpleName();
    		String targetType = e.getTargetType().getSimpleName();
    		throw new RuntimeException(String.format("Incorrect type of value %s, expected %s in config file '%s'", sourceType, targetType, path));
    	} catch (JsonMappingException e) {
    		throw new RuntimeException("Failed to parse configuration at " + path + " for class: " + klass, e);
    	}
    }
    
	/**
	 * Resolves the currently loaded module configurations based on the biven
	 * fieldName to fieldType map.
	 * 
	 * @param config
	 * @throws IOException
	 */
	private void resolveModuleConfigs(Configuration config) throws IOException {
		for (Entry<String, Class<?>> entry : additionalConfigurations.entrySet()) {
			final String fieldName = entry.getKey();
			final Class<Object> fieldType = (Class<Object>) entry.getValue();
			final ConfigurationFactory<Object> factory = new ConfigurationFactory<Object>(fieldType, mapper, validator);
			final JsonNode moduleConfigNode = config.getModuleConfigurations().get(fieldName);
			Object moduleConfig = null;
			if (moduleConfigNode != null) {
				moduleConfig = factory.build(moduleConfigNode, fieldName);
			} else {
				moduleConfig = factory.build();
			}
			config.getConfigurationClassToNode().put(fieldType, moduleConfig);
		}
	}

	/**
	 * Validates the given instance of configuration class against the
	 * validation constraints defined in the current {@link #klass}.
	 * 
	 * @param config
	 * @param path
	 * @since 3.4
	 */
	private void validate(T config, String path) {
		final Set<ConstraintViolation<T>> violations = validator.validate(config);
		if (!violations.isEmpty()) {
			throw new ConfigurationValidationException(path, violations);
		}
	}

	/**
     * Parses, binds, then returns a configuration object represented as the given {@link JsonNode}.
     * @param node
     * @param path
     * @return
     * @throws IOException
     */
	public T build(JsonNode node, String path) throws IOException {
		return build(new TreeTraversingParser(node), path);
	}

}