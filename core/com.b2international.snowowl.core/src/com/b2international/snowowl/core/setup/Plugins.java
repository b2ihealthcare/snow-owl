/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.CompositeClassLoader;
import com.b2international.snowowl.core.SnowOwl;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0
 */
public final class Plugins {

	private final Collection<Plugin> plugins;
	private final CompositeClassLoader compositeClassLoader;

	/**
	 * Constructs a new {@link Plugins} instance with the given set of Plug-ins.
	 * 
	 * @param plugins
	 */
	public Plugins(Collection<Plugin> plugins) {
		this.plugins = ImmutableList.copyOf(plugins);
		final CompositeClassLoader classLoader = new CompositeClassLoader();
		plugins.stream().map(Plugin::getClass).map(Class::getClassLoader).forEach(classLoader::add);
		this.compositeClassLoader = classLoader;
	}

	/**
	 * Initializes all currently existing {@link Plugin}s within the given {@link Environment}.
	 * 
	 * @param configuration
	 * @param environment
	 * @throws Exception
	 * @see Plugin#init(Environment)
	 */
	public void init(SnowOwlConfiguration configuration, Environment environment) throws Exception {
		for (Plugin plugin : plugins) {
			plugin.init(configuration, environment);
		}
	}

	/**
	 * Executes {@link Plugin#run(SnowOwlConfiguration, Environment, IProgressMonitor)} methods.
	 * 
	 * @param configuration
	 * @param environment
	 * @throws Exception
	 * @see Plugin#run(Environment)
	 */
	public void run(SnowOwlConfiguration configuration, Environment environment) throws Exception {
		for (Plugin plugin : plugins) {
			plugin.run(configuration, environment);
		}
	}

	/**
	 * Executes {@link PreRunCapableBootstrapFragment#preRun(SnowOwlConfiguration, Environment)} methods in the currently registered
	 * {@link Plugin}s.
	 * 
	 * @param configuration
	 * @param environment
	 * @throws Exception 
	 */
	public void preRun(SnowOwlConfiguration configuration, Environment environment) throws Exception {
		for (Plugin plugin : plugins) {
			plugin.preRun(configuration, environment);
		}
	}
	
	/**
	 * Executes {@link Plugin#postRun(SnowOwlConfiguration, Environment)} methods in the currently registered
	 * {@link Plugin}s.
	 * 
	 * @param configuration
	 * @param environment
	 * @throws Exception 
	 */
	public void postRun(SnowOwlConfiguration configuration, Environment environment) throws Exception {
		for (Plugin fragment : plugins) {
			fragment.postRun(configuration, environment);
		}
	}

	/**
	 * @return all {@link Plugin}s.
	 */
	public Collection<Plugin> getPlugins() {
		return plugins;
	}

	/**
	 * Collects all plugin configuration contributions and returns them in a {@link Map} where the key is the desired property name of the
	 * configuration node and the value is the {@link Class} of the actual configuration node.
	 * 
	 * @return
	 * @since 3.4
	 */
	public Map<String, Class<?>> getPluginConfigurations() {
		final Map<String, Class<?>> moduleConfigMap = newHashMap();
		for (Plugin plugin : getPlugins()) {
			plugin.addConfigurations(new ConfigurationRegistry() {
				@Override
				public void add(String field, Class<?> configurationType) {
					Class<?> prev = moduleConfigMap.put(field, configurationType);
					if (prev != null) {
						throw new SnowOwl.InitializationException("Configuration node already registered for " + field + " - " + prev + " vs. " + configurationType);
					}
				}
			});
		}
		return moduleConfigMap;
	}

	/**
	 * @return a class loader instance that can load classes from all available {@link Plugin} instances.
	 */
	public ClassLoader getCompositeClassLoader() {
		return compositeClassLoader;
	}

}