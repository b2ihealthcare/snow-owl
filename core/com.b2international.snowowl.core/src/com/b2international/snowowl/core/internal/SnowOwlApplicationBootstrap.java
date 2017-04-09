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
package com.b2international.snowowl.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.prefs.PreferencesService;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.events.metrics.DefaultMetricsProvider;
import com.b2international.snowowl.core.events.metrics.Metrics;
import com.b2international.snowowl.core.events.metrics.MetricsConfiguration;
import com.b2international.snowowl.core.events.metrics.MetricsProvider;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.core.login.LoginConfiguration;
import com.b2international.snowowl.core.setup.BootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.ModuleConfig;

/**
 * @since 3.3
 */
@ModuleConfig(fieldName = "metrics", type = MetricsConfiguration.class)
public class SnowOwlApplicationBootstrap implements BootstrapFragment {

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) {
		final PreferencesService preferences = env.preferences(); 
		
		final ClientPreferences cdoClientConfiguration = new ClientPreferences(preferences);
		env.services().registerService(ClientPreferences.class, cdoClientConfiguration);

		final LoginConfiguration loginConfiguration = new LoginConfiguration(preferences);
		env.services().registerService(LoginConfiguration.class, loginConfiguration);
		
		env.services().registerService(CoreTerminologyBroker.class, CoreTerminologyBroker.getInstance());
		
		if (configuration.getModuleConfig(MetricsConfiguration.class).isEnabled()) {
			env.services().registerService(MetricsProvider.class, new DefaultMetricsProvider());
		} else {
			env.services().registerService(MetricsProvider.class, MetricsProvider.NOOP);
		}
		env.services().registerService(Metrics.class, Metrics.NOOP);
		
		// TODO support initial values for feature toggles
		env.services().registerService(FeatureToggles.class, new FeatureToggles());
	}

	@Override
	public void run(SnowOwlConfiguration configuration, Environment environment, IProgressMonitor monitor) {
		if (!environment.isEmbedded() && environment.isClient()) {
			PlatformUtil.enableSystemProxies(CoreActivator.getContext());
		}
	}

}