/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import com.b2international.index.Index;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.config.IndexSettings;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.monitoring.MonitoringConfiguration;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.PathTerminologyResourceResolver;
import com.b2international.snowowl.core.repository.TerminologyComponents;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.uri.DefaultResourceURIPathResolver;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.b2international.snowowl.core.version.VersionDocument;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

/**
 * @since 3.3
 */
@Component
public final class SnowOwlPlugin extends Plugin {

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) {
		env.services().registerService(TerminologyRegistry.class, TerminologyRegistry.INSTANCE);
		env.services().registerService(ResourceURIPathResolver.class, new DefaultResourceURIPathResolver(true));
		env.services().registerService(PathTerminologyResourceResolver.class, new PathTerminologyResourceResolver.Default());
		env.services().registerService(TimestampProvider.class, new TimestampProvider.Default());
		
		// configure monitoring support
		final MonitoringConfiguration monitoringConfig = configuration.getModuleConfig(MonitoringConfiguration.class);
		if (monitoringConfig.isEnabled()) {
			final PrometheusMeterRegistry registry = createRegistry(monitoringConfig);
			env.services().registerService(MeterRegistry.class, registry);
		} else {
			// XXX this works like a NOOP registry if you do NOT register any additional registries to it
			env.services().registerService(MeterRegistry.class, new CompositeMeterRegistry());
		}
	}
	
	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isServer()) {
			final ObjectMapper mapper = env.service(ObjectMapper.class);
			final Map<String, Object> indexSettings = newHashMap(env.service(IndexSettings.class));
			indexSettings.put(IndexClientFactory.NUMBER_OF_SHARDS, 3); // TODO make this configurable
			final Index resourceIndex = Indexes.createIndex(
				"resources", 
				mapper, 
				new Mappings(ResourceDocument.class, VersionDocument.class), 
				env.service(IndexSettings.class)
			);
			
			final RevisionIndex revisionIndex = new DefaultRevisionIndex(resourceIndex, env.service(TimestampProvider.class), mapper);
			env.services().registerService(ResourceRepository.class, new ResourceRepository(revisionIndex));
			
			env.services().registerService(TerminologyComponents.class, new TerminologyComponents(LoggerFactory.getLogger("core")));
		}
	}

	private PrometheusMeterRegistry createRegistry(final MonitoringConfiguration monitoringConfig) {
		final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
		
		Map<String, String> tags = newHashMapWithExpectedSize(1 + monitoringConfig.getTags().size());
		// always set the application tag to snow_owl
		tags.put("application", "snow_owl");
		// override with tags coming from the config file
		tags.putAll(monitoringConfig.getTags());

		// configure the tags
		final List<Tag> commonTags = tags.entrySet()
				.stream()
				.map(entry -> Tag.of(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
		
		registry.config().commonTags(commonTags);
		
		// configure default JVM and node metrics
		new ClassLoaderMetrics().bindTo(registry);
		new JvmGcMetrics().bindTo(registry);
		new JvmMemoryMetrics().bindTo(registry);
		new JvmThreadMetrics().bindTo(registry);
		new UptimeMetrics().bindTo(registry);
		new ProcessorMetrics().bindTo(registry);
		new LogbackMetrics().bindTo(registry);
		new FileDescriptorMetrics().bindTo(registry);
		
		return registry;
	}

	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("monitoring", MonitoringConfiguration.class);
	}

}