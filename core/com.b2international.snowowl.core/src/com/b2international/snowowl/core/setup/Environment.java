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
package com.b2international.snowowl.core.setup;

import java.io.File;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.osgi.service.prefs.PreferencesService;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.preferences.FileBasedPreferencesService;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.google.inject.Provider;

/**
 * @since 3.3
 */
public final class Environment implements ServiceProvider {

	private final ApplicationContext context = ApplicationContext.getInstance();
	private final IManagedContainer container = IPluginContainer.INSTANCE;
	private final File homeDirectory;
	
	private File configDirectory;
	private File resourcesDirectory;
	private File defaultsDirectory;

	public Environment(final Plugins plugins, File homeDirectory, final SnowOwlConfiguration configuration) throws Exception {
		this.homeDirectory = homeDirectory;
		initializeEnvironmentDirectories(configuration);
		final PreferencesService preferences = PlatformUtil.getPreferencesService(plugins.getBundleContext());
		services().registerService(PreferencesService.class, preferences);
		services().registerService(FileBasedPreferencesService.class, new FileBasedPreferencesService(getConfigDirectory()));
		services().registerService(SnowOwlConfiguration.class, configuration);
		final ClientPreferences cdoClientConfiguration = new ClientPreferences(preferences);
		services().registerService(ClientPreferences.class, cdoClientConfiguration);
		services().registerService(Plugins.class, plugins);
	}
	
	private void initializeEnvironmentDirectories(SnowOwlConfiguration configuration) throws Exception {
		// TODO check if the configuration uses an absolute path
		this.configDirectory = createDirectory(homeDirectory, configuration.getConfigurationDirectory());
		this.resourcesDirectory = createDirectory(homeDirectory, configuration.getResourceDirectory());
		this.defaultsDirectory = createDirectory(homeDirectory, configuration.getDefaultsDirectory());
		// set resolved directory paths to configuration
		configuration.setInstallationDirectory(this.homeDirectory.getAbsolutePath());
		configuration.setConfigurationDirectory(this.configDirectory.getAbsolutePath());
		configuration.setResourceDirectory(this.resourcesDirectory.getAbsolutePath());
		configuration.setDefaultsDirectory(this.defaultsDirectory.getAbsolutePath());
	}

	/**
	 * Returns the {@link ApplicationContext} instance to register/retrieve
	 * services.
	 * 
	 * @return
	 */
	public ApplicationContext services() {
		return context;
	}

	/**
	 * Returns the {@link IManagedContainer} to register Net4J and CDO services.
	 * 
	 * @return
	 */
	public IManagedContainer container() {
		return container;
	}

	/**
	 * Returns the global {@link FileBasedPreferencesService}.
	 * 
	 * @return
	 */
	public FileBasedPreferencesService filePreferences() {
		return service(FileBasedPreferencesService.class);
	}

	/**
	 * Returns the current installation directory.
	 * 
	 * @return
	 */
	public File getHomeDirectory() {
		return homeDirectory;
	}

	/**
	 * Returns the config directory location.
	 * 
	 * @return
	 */
	public File getConfigDirectory() {
		return configDirectory;
	}

	/**
	 * Returns the data directory location.
	 * 
	 * @return
	 */
	public File getDataDirectory() {
		return resourcesDirectory;
	}

	/**
	 * Returns the defaults directory location.
	 * 
	 * @return
	 */
	public File getDefaultsDirectory() {
		return defaultsDirectory;
	}

	/**
	 * Returns the {@link PreferencesService} from the
	 * {@link ApplicationContext}.
	 * 
	 * @return
	 */
	public PreferencesService preferences() {
		return services().getServiceChecked(PreferencesService.class);
	}

	@Override
	public <T> T service(final Class<T> type) {
		return services().getServiceChecked(type);
	}
	
	@Override
	public <T> Provider<T> provider(final Class<T> type) {
		return new Provider<T>() {
			@Override
			public T get() {
				return service(type);
			}
		};
	}

	/**
	 * Returns if Snow Owl running in embedded mode or not.
	 * 
	 * @return
	 */
	public boolean isEmbedded() {
		return service(ClientPreferences.class).isClientEmbedded();
	}

	/**
	 * Returns <code>true</code> if Snow Owl is running on a client environment.
	 * 
	 * @return
	 */
	public boolean isClient() {
		return !isServer();
	}

	/**
	 * Returns <code>true</code> if Snow Owl is running on a server environment.
	 * 
	 * @return
	 */
	public boolean isServer() {
		return services().isServerMode();
	}

	private File createDirectory(File parent, String path) throws Exception {
		return createDirectory(new File(parent, path));
	}
	
	private File createDirectory(File directory) {
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return directory;
	}

}