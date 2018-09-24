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

import java.nio.file.Path;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.osgi.service.prefs.PreferencesService;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.preferences.FileBasedPreferencesService;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.google.inject.Provider;

/**
 * @since 3.3
 */
public final class Environment implements ServiceProvider {

	private final ApplicationContext context = ApplicationContext.getInstance();
	private final IManagedContainer container = IPluginContainer.INSTANCE;
	
	private final Path homePath;
	private final Path configPath;
	private final Path dataPath;
	
	public Environment(final Path homePath, final Path configPath, final Path dataPath) throws Exception {
		this.homePath = homePath;
		this.configPath = configPath;
		this.dataPath = dataPath; 
		// intialize global services based on the environment
		final PreferencesService preferences = PlatformUtil.getPreferencesService(CoreActivator.getContext());
		services().registerService(PreferencesService.class, preferences);
		services().registerService(FileBasedPreferencesService.class, new FileBasedPreferencesService(configPath));
		final ClientPreferences cdoClientConfiguration = new ClientPreferences(preferences);
		services().registerService(ClientPreferences.class, cdoClientConfiguration);
		services().registerService(Environment.class, this);
	}
	
	/**
	 * @return the currently loaded list of {@link Plugin}s via a {@link Plugins} instance
	 */
	public Plugins plugins() {
		return service(Plugins.class);
	}
	
	/**
	 * @return the {@link ApplicationContext} instance to register/retrieve services.
	 */
	public ApplicationContext services() {
		return context;
	}

	/**
	 * @return the {@link IManagedContainer} to register Net4J and CDO services.
	 */
	public IManagedContainer container() {
		return container;
	}

	/**
	 * @return the home directory path.
	 */
	public Path getHomePath() {
		return homePath;
	}
	
	/**
	 * @return the configuration directory path.
	 */
	public Path getConfigPath() {
		return configPath;
	}

	/**
	 * @return the data directory path.
	 */
	public Path getDataPath() {
		return dataPath;
	}

	/**
	 * @return the {@link PreferencesService} from the {@link ApplicationContext}.
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

}