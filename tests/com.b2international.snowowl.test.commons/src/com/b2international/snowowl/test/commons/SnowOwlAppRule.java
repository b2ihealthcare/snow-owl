/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons;

import java.io.File;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.FileUtils;
import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.BootstrapFragment;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * Bootstraps a {@link SnowOwlApplication} and runs it before test method execution. After all test execution finished, shuts the application down.
 * <p>
 * Usage:
 *
 * <pre>
 * &#064;RunWith(Suite.class)
 * public class SnowOwlAppTestSuite {
 *
 * 	&#064;ClassRule
 * 	public static final SnowOwlAppRule appRule = SnowOwlAppRule.snowOwl();
 *
 * }
 * </pre>
 *
 * Usage example with configuration, the configuration file is at the same location at the SnowOwlAppTestSuite class.
 *
 * <pre>
 * &#064;RunWith(Suite.class)
 * public class SnowOwlAppTestSuite {
 *
 * 	&#064;ClassRule
 * 	public static final SnowOwlAppRule appRule = SnowOwlAppRule.snowOwl().config(PlatformUtil.toAbsolutePath(SnowOwlAppTestSuite.class, &quot;test-config.json&quot;));
 *
 * }
 * </pre>
 *
 * @since 3.3
 * @see PlatformUtil#toAbsolutePath(Class, String)
 * @see #snowOwl()
 */
public class SnowOwlAppRule extends ExternalResource {

	private String configPath;
	private boolean clearResources = false;
	private BootstrapFragment[] fragments;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SnowOwlAppRule.class);

	private SnowOwlAppRule() {
		
		String requestLoggerLevelProperty = System.getProperty("request.logger.level");
		LOGGER.info("Using the system property 'request.logger.level' to set the request logger level to {}. Default level is INFO.", requestLoggerLevelProperty);
		Level requestLoggerLevel = Level.toLevel(requestLoggerLevelProperty, Level.INFO);
		
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("request");
		rootLogger.setLevel(requestLoggerLevel);
	}

	/**
	 * Sets the absolute configuration path to the given argument.
	 *
	 * @param configPath
	 */
	public SnowOwlAppRule config(String configPath) {
		this.configPath = configPath;
		return this;
	}

	/**
	 * Set whether to clear the {@link SnowOwlConfiguration#getResourceDirectory()} or not.
	 *
	 * @param clearResources
	 */
	public SnowOwlAppRule clearResources(boolean clearResources) {
		this.clearResources = clearResources;
		return this;
	}

	/**
	 * Defines additional {@link BootstrapFragment} instances to be part of the setup process.
	 * @param fragments
	 * @return
	 */
	public SnowOwlAppRule fragments(BootstrapFragment...fragments) {
		this.fragments = fragments;
		return this;
	}

	@Override
	protected void before() throws Throwable {
		super.before();
		SnowOwlApplication.INSTANCE.bootstrap(configPath, fragments);
		if (clearResources) {
			final SnowOwlConfiguration config = SnowOwlApplication.INSTANCE.getConfiguration();
			final File resourceDirectory = new File(config.getResourceDirectory());
			FileUtils.cleanDirectory(resourceDirectory);
		}
		SnowOwlApplication.INSTANCE.run();
	}

	@Override
	protected void after() {
		super.after();
		SnowOwlApplication.INSTANCE.shutdown();
	}

	/**
	 * Constructs a new {@link SnowOwlAppRule}.
	 *
	 * @return
	 */
	public static SnowOwlAppRule snowOwl() {
		return new SnowOwlAppRule();
	}

}
