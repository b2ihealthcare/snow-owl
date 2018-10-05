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
package com.b2international.snowowl.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import javax.validation.Validator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.config.ConfigurationFactory;
import com.b2international.commons.config.FileConfigurationSourceProvider;
import com.b2international.commons.extension.ClassPathScanner;
import com.b2international.snowowl.core.ApplicationContext.ServiceRegistryEntry;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.core.setup.Plugins;
import com.b2international.snowowl.hibernate.validator.ValidationUtil;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @since 3.3
 */
public final class SnowOwl {

	private static final String NEW_LINE = "\r\n"; //$NON-NLS-1$
	
	// available configurable paths
	// via System Properties/JVM args
	public static final String SO_PATH_HOME = "so.home"; //$NON-NLS-1$
	public static final String SO_PATH_CONF = "so.path.conf"; //$NON-NLS-1$
	public static final String SO_PATH_DATA = "so.path.data"; //$NON-NLS-1$
	// via ENV variables 
	public static final String SO_PATH_HOME_ENV = "SO_HOME"; //$NON-NLS-1$
	public static final String SO_PATH_CONF_ENV = "SO_PATH_CONF"; //$NON-NLS-1$
	
	// default folders and files
	private static final String OSGI_INSTALL_AREA = "osgi.install.area"; //$NON-NLS-1$
	private static final String DEFAULT_CONF_PATH = "configuration"; //$NON-NLS-1$
	private static final String CONFIGURATION_FILE = "snowowl.yml"; //$NON-NLS-1$
	private static final String DEFAULT_DATA_PATH = "resources"; //$NON-NLS-1$
	
	private static final Logger LOG = LoggerFactory.getLogger("snowowl");
	
	private AtomicBoolean running = new AtomicBoolean(false);
	private AtomicBoolean preRunCompleted = new AtomicBoolean(false);

	private Plugins plugins;
	private Environment environment;
	private SnowOwlConfiguration configuration;

	private SnowOwl(Plugin...additionalPlugins) throws Exception {
		final Path homePath = getHomePath();
		// make sure homePath sysprop is set to the computed path
		System.setProperty(SO_PATH_HOME, homePath.toString());
		final Path confPath = getConfPath(homePath);
		System.setProperty(SO_PATH_CONF, confPath.toString());
		
		List<Plugin> plugins = ImmutableList.<Plugin>builder()
			.addAll(ClassPathScanner.INSTANCE.getComponentsBySuperclass(Plugin.class))
			.add(additionalPlugins != null ? additionalPlugins : new Plugin[]{})
			.build();
		this.plugins = new Plugins(plugins);
		this.configuration = createConfiguration(confPath, this.plugins);
		
		final Path dataPath = getDataPath(homePath, configuration.getPaths().getData());
		this.environment = new Environment(homePath, confPath, dataPath);
		
		this.environment.services().registerService(SnowOwlConfiguration.class, this.configuration);
		this.environment.services().registerService(Plugins.class, this.plugins);
		// log environment and setting info
		logEnvironment();
		this.plugins.getPlugins().forEach(plugin -> LOG.info("loaded plugin [{}]", plugin));
	}
	
	private Path getHomePath() {
		// check ENV variable
		String homePath = System.getenv(SO_PATH_HOME_ENV);
		if (!Strings.isNullOrEmpty(homePath)) {
			return createPath(SO_PATH_HOME_ENV, homePath);
		}
		
		// check System property
		homePath = System.getProperty(SO_PATH_HOME);
		if (!Strings.isNullOrEmpty(homePath)) {
			return createPath(SO_PATH_HOME, homePath);
		}
		
		// as last resort, use the current install area for homePath
		try {
			final String installArea = CoreActivator.getContext().getProperty(OSGI_INSTALL_AREA);
			return URIUtil.toFile(URIUtil.fromString(installArea)).toPath();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Path getConfPath(Path homePath) {
		// check ENV variable
		String confPath = System.getenv(SO_PATH_CONF_ENV);
		if (!Strings.isNullOrEmpty(confPath)) {
			return createPath(SO_PATH_CONF_ENV, confPath);
		}
		
		// check System property
		confPath = System.getProperty(SO_PATH_CONF);
		if (!Strings.isNullOrEmpty(confPath)) {
			return createPath(SO_PATH_CONF, confPath);
		}
		
		// as last resort, fall back to the default configuration folder SO_HOME/configuration
		return homePath.resolve(DEFAULT_CONF_PATH);
	}

	private Path getDataPath(Path homePath, String configurationDataPath) throws IOException {
		if (!Strings.isNullOrEmpty(configurationDataPath)) {
			return createPath(SO_PATH_DATA, configurationDataPath);
		}
		
		Path defaultDataPath = homePath.resolve(DEFAULT_DATA_PATH);
		if (!Files.exists(defaultDataPath)) {
			Files.createDirectories(defaultDataPath);
		}
		return defaultDataPath;
	}
	
	private static Path createPath(String variable, String path) {
		final Path p = Paths.get(path);
		checkArgument(Files.isDirectory(p), "%s should point to a directory. Got: %s", variable, path);
		return p;
	}

	/**
	 * Creates a new Snow Owl application ready to be initialized via {@link #bootstrap()} and started via {@link #run()}.
	 * 
	 * @param additionalPlugins - additional {@link Plugin} instances to use during initialization
	 * @throws Exception 
	 */
	public static SnowOwl create(Plugin...additionalPlugins) throws Exception {
		return new SnowOwl(additionalPlugins);
	}

	/**
	 * Returns the {@link Environment} of the Snow Owl Application.
	 * 
	 * @return
	 */
	public Environment getEnviroment() {
		return environment;
	}

	/**
	 * Returns the global {@link SnowOwlConfiguration} of this
	 * {@link SnowOwl}.
	 * 
	 * @return
	 */
	public SnowOwlConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Bootstraps the application with a default configuration.
	 * @return 
	 * 
	 * @throws Exception
	 * @see {@link #bootstrap(String)}
	 */
	public SnowOwl bootstrap() throws Exception {
		if (!isRunning()) {
			LOG.info("Initializing...");
			this.plugins.init(this.configuration, this.environment);
		}
		return this;
	}

	private SnowOwlConfiguration createConfiguration(Path configPath, Plugins plugins) throws Exception {
		final Validator validator = ValidationUtil.getValidator();
		final ConfigurationFactory<SnowOwlConfiguration> factory = new ConfigurationFactory<SnowOwlConfiguration>(SnowOwlConfiguration.class, validator);
		factory.setAdditionalModules(plugins.getPluginConfigurations());
		
		final Path configFile = configPath.resolve(CONFIGURATION_FILE);
		return configFile != null ? factory.build(new FileConfigurationSourceProvider(), configFile.toString()) : factory.build();
	}
	
	private void logEnvironment() {
		LOG.info(String.format("Home path: %s", this.environment.getHomePath()));
		LOG.info(String.format("Config path: %s", this.environment.getConfigPath()));
		LOG.info(String.format("Data path: %s", this.environment.getDataPath()));
	}

	/**
	 * Runs the Snow Owl application by performing all necessary initialization
	 * logic. Uses a <code>null</code> {@link PreRunRunnable} and a
	 * {@link NullProgressMonitor} instance.
	 * @return 
	 * 
	 * @throws Exception
	 * @see {@link #run(IProgressMonitor)}
	 * @see #run(PreRunRunnable, IProgressMonitor)
	 */
	public SnowOwl run() throws Exception {
		return run(new NullProgressMonitor());
	}

	/**
	 * Runs the application without any {@link PreRunRunnable} and the given
	 * monitor.
	 * 
	 * @param monitor
	 * @throws Exception
	 * @see #run(PreRunRunnable, IProgressMonitor)
	 */
	public SnowOwl run(IProgressMonitor monitor) throws Exception {
		return run(null, monitor);
	}

	/**
	 * Runs the application by performing all necessary initialization logic
	 * like service registrations, etc.
	 * 
	 * @param preRunRunnable
	 *            - to execute logic between
	 *            {@link Plugins#preRun(SnowOwlConfiguration, Environment)}
	 *            and
	 *            {@link Plugins#run(SnowOwlConfiguration, Environment, IProgressMonitor)}
	 * @param monitor
	 *            - to monitor application startup
	 */
	public SnowOwl run(PreRunRunnable preRunRunnable, IProgressMonitor monitor) throws Exception {
		if (!isRunning()) {
			checkState(plugins != null, "Bootstrap the application first");
			if (preRunCompleted.compareAndSet(false, true)) {
				this.plugins.preRun(configuration, environment);
			}
			if (preRunRunnable != null) {
				preRunRunnable.run();
			}
			LOG.info("Preparing to run Snow Owl...");
			this.plugins.run(configuration, environment);
			checkApplicationState();
			running.set(true);
			this.plugins.postRun(configuration, environment);
			LOG.info("Snow Owl successfully started.");
		} else {
			LOG.info("Snow Owl is already running.");
		}
		return this;
	}

	/**
	 * Checks the current application state and throws exceptions if errors have
	 * been found.
	 * 
	 * @throws InitializationException
	 *             - on application init failures
	 */
	private void checkApplicationState() throws InitializationException {
		// check all registered services
		final Collection<ServiceRegistryEntry<?>> failedServices = this.environment.services().checkStrictServices();
		if (!failedServices.isEmpty()) {
			final String errorMessage = serviceRegistryErrorMessage(failedServices);
			throw new InitializationException(errorMessage);
		}
	}

	private String serviceRegistryErrorMessage(Collection<ServiceRegistryEntry<?>> failedServices) {
		final StringBuilder builder = new StringBuilder();
		for (Iterator<ServiceRegistryEntry<?>> iter = failedServices.iterator(); iter.hasNext();) {
			final ServiceRegistryEntry<?> next = iter.next();
			builder.append("Missing implementation of service: " + next.getServiceInterface());
			if (iter.hasNext()) {
				builder.append(NEW_LINE);
			}
		}
		return builder.toString();
	}

	/**
	 * Shuts down the Snow Owl application by performing shut down logic.
	 */
	public void shutdown() {
		if (isRunning()) {
			LOG.info("Snow Owl is shutting down.");
			this.environment.services().dispose();
			LifecycleUtil.deactivate(environment.container());
			running.set(false);
		}
	}

	/**
	 * Returns whether the Snow Owl application is ready to use or not.
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running.get();
	}

	/**
	 * {@link PreRunRunnable} to run additional logic before the run method is
	 * actually called.
	 * 
	 * @since 3.3
	 */
	public static interface PreRunRunnable {

		public void run() throws Exception;

	}

	/**
	 * Exception to indicate problems with application startup.
	 * 
	 * @since 3.3
	 */
	public static class InitializationException extends RuntimeException {

		private static final long serialVersionUID = 3313953055518425730L;

		public InitializationException(String message) {
			super(message);
		}
	}

	/*
	 * Setting all java loggers levels to SEVERE. This is done here to suppress
	 * the overwhelming INFO logs from the apache CXF stack.
	 */
	static {
		final Enumeration<String> loggerNames = java.util.logging.LogManager.getLogManager().getLoggerNames();
		while (loggerNames.hasMoreElements()) {
			final String name = loggerNames.nextElement();
			final java.util.logging.Logger javaLogger = java.util.logging.LogManager.getLogManager().getLogger(name);
			if (javaLogger != null) {
				javaLogger.setLevel(Level.SEVERE);
			}
		}
	}

}