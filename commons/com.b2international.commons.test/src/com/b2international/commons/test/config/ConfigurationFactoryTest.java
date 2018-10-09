/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.test.config;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.validation.Validator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.b2international.commons.config.ConfigurationFactory;
import com.b2international.commons.config.ConfigurationValidationException;
import com.b2international.commons.test.config.data.DefaultSettings;
import com.b2international.commons.test.config.data.DynamicConfig;
import com.b2international.commons.test.config.data.ModuleConfig1;
import com.b2international.commons.test.config.data.ModuleConfig2;
import com.b2international.commons.test.config.data.NestedConfig;
import com.b2international.commons.test.config.data.TestConfig;
import com.b2international.snowowl.hibernate.validator.ValidationUtil;

/**
 * @since 3.4
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationFactoryTest {
	
	private static final String SOME_ATTRIBUTE_ENVIRONMENT_VARIABLE = "SOME_ATTRIBUTE";
	private static final String SOME_ATTRIBUTE_ENVIRONMENT_VARIABLE_VALUE = "value";

	@Mock
	private Validator validator;
	
	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
	
	@Test
	public void build_WithoutSource_ShouldReturnADefaultInstance() throws Exception {
		final TestConfig config = parse(TestConfig.class);
		assertNotNull(config);
	}

	@Test
	public void build_FromJsonFileNoValidation_ShouldReturnInstanceMatchingFileContent() throws Exception {
		final TestConfig config = parse(TestConfig.class, "config.json");
		assertNotNull(config);
		assertEquals("", config.getAttribute());
		assertTrue(config.isValid());
	}
	
	@Test
	public void build_FromYamlFileNoValidation_ShouldReturnInstanceMatchingFileContent() throws Exception {
		final TestConfig config = parse(TestConfig.class, "config.yml");
		assertNotNull(config);
		assertEquals("", config.getAttribute());
		assertTrue(config.isValid());
	}

	@Test(expected = ConfigurationValidationException.class)
	public void build_WithValidation_ShouldThrowExceptionIfValidationErrorsPresent() throws Exception {
		parse(TestConfig.class, "config.yml", ValidationUtil.getValidator());
	}
	
	@Test
	public void build_NestedConfigNoValidation_ShouldBuildProperly() throws Exception {
		final NestedConfig config = parse(NestedConfig.class, "nestedconfig.yml");
		assertNotNull(config);
		assertEquals("Test", config.getGlobalParameter());
		assertEquals("SomeAttr", config.getNestedConfig().getAttribute());
		assertTrue(config.getNestedConfig().isValid());
	}
	
	@Test
	public void build_DynamicModuleConfigWithEnvVarNoValidation_ShouldExtractEnvVariable() throws Exception {
		environmentVariables.set(SOME_ATTRIBUTE_ENVIRONMENT_VARIABLE, SOME_ATTRIBUTE_ENVIRONMENT_VARIABLE_VALUE);
		
		final Map<String, Class<?>> modules = newHashMap();
		modules.put("moduleConfig1", ModuleConfig1.class);
		modules.put("moduleConfig2", ModuleConfig2.class);
		
		final DynamicConfig config = parseWithModules(DynamicConfig.class, "dynamic-config-envVar.yml", validator, modules);
		final ModuleConfig1 config1 = config.getModuleConfig(ModuleConfig1.class);
		final ModuleConfig2 config2 = config.getModuleConfig(ModuleConfig2.class);
		
		assertEquals("SOME_PARAM", config.getGlobalParameter());
		assertTrue(config1.isAttribute1_1());
		assertTrue(config2.isAttribute2_2());
		
		assertEquals(SOME_ATTRIBUTE_ENVIRONMENT_VARIABLE_VALUE, config1.getAttribute1_2());
		assertEquals("config2", config2.getAttribute2_1());
	}
	
	@Test(expected = ConfigurationValidationException.class)
	public void build_NestedConfigWithValidation_ShouldThrowExceptionInCaseOfErrors() throws Exception {
		parse(NestedConfig.class, "nestedconfig-error.yml", ValidationUtil.getValidator());
	}
	
	@Test(expected = RuntimeException.class)
	public void build_UnrecognizedProperty_ShouldThrowException() throws Exception {
		parse(TestConfig.class, "config-unrecognized.yml");
	}

	@Test(expected = RuntimeException.class)
	public void build_InvalidAttributeType_ShouldThrowException() throws Exception {
		TestConfig parse = parse(TestConfig.class, "config-invalidtype.yml");
		assertNotNull(parse);
	}
	
	@Test
	public void build_DynamicConfigurationWithModules_ShouldDeserializeProperly() throws Exception {
		final Map<String, Class<?>> modules = newHashMap();
		modules.put("moduleConfig1", ModuleConfig1.class);
		modules.put("moduleConfig2", ModuleConfig2.class);
		final DynamicConfig config = parseWithModules(DynamicConfig.class, "dynamic-config.yml", validator, modules);
		final ModuleConfig1 config1 = config.getModuleConfig(ModuleConfig1.class);
		final ModuleConfig2 config2 = config.getModuleConfig(ModuleConfig2.class);
		assertEquals("SOME_PARAM", config.getGlobalParameter());
		assertTrue(config1.isAttribute1_1());
		assertEquals("config1", config1.getAttribute1_2());
		assertEquals("config2", config2.getAttribute2_1());
		assertTrue(config2.isAttribute2_2());
	}
	
	@Test
	public void build_DynamicConfigurationWithEmptyModule_ShouldDeserializeWithDefaults() throws Exception {
		final Map<String, Class<?>> modules = newHashMap();
		modules.put("moduleConfig1", ModuleConfig1.class);
		final DynamicConfig config = parseWithModules(DynamicConfig.class, "empty_module_config.yml", validator, modules);
		assertNotNull(config.getModuleConfig(ModuleConfig1.class));
	}
	
	@Test
	public void build_EmptyConfigurationFile_DefaultSettings() throws Exception {
		assertNotNull(parse(DefaultSettings.class, "emptyconfig.yml"));
	}
	
	private <T> T parse(Class<T> klass) throws IOException {
		return new ConfigurationFactory<T>(klass, validator).build();
	}
	
	private <T> T parse(Class<T> klass, String configFile) throws IOException {
		final URL resource = TestConfig.class.getResource(configFile);
		return new ConfigurationFactory<T>(klass, validator).build(resource);
	}
	
	private <T> T parse(Class<T> klass, String configFile, Validator validator) throws IOException {
		final URL resource = TestConfig.class.getResource(configFile);
		return new ConfigurationFactory<T>(klass, validator).build(resource);
	}
	
	private <T> T parseWithModules(Class<T> klass, String configFile, Validator validator, Map<String, Class<?>> modules) throws IOException {
		final URL resource = TestConfig.class.getResource(configFile);
		return new ConfigurationFactory<T>(klass, validator).setAdditionalModules(modules).build(resource);
	}

}