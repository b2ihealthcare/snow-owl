/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.reasoner;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.b2international.index.Index;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.TerminologyRepositoryConfigurer;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationTracker;
import com.b2international.snowowl.snomed.reasoner.equivalence.IEquivalentConceptMerger;
import com.b2international.snowowl.snomed.reasoner.index.*;

/**
 * @since 7.0
 */
@Component
public final class SnomedReasonerPlugin extends Plugin implements TerminologyRepositoryConfigurer {

	@Override
	public void run(final SnowOwlConfiguration configuration, final Environment env) throws Exception {
		if (env.isServer()) {
			final Index repositoryIndex = env.service(RepositoryManager.class).get(getToolingId()).service(Index.class);
			final SnomedCoreConfiguration snomedConfig = configuration.getModuleConfig(SnomedCoreConfiguration.class);
			final int maximumReasonerRuns = snomedConfig.getMaxReasonerRuns();
			final long classificationCleanUpInterval = snomedConfig.getClassificationCleanUpInterval();
			final ClassificationTracker classificationTracker = new ClassificationTracker(repositoryIndex, maximumReasonerRuns, TimeUnit.MINUTES.toMillis(classificationCleanUpInterval));
			env.services().registerService(ClassificationTracker.class, classificationTracker);
			
			final ClassPathScanner scanner = env.service(ClassPathScanner.class);
			env.services().registerService(IEquivalentConceptMerger.Registry.class, new IEquivalentConceptMerger.Registry(scanner));
		}
	}
	
	@Override
	public Collection<Class<?>> getAdditionalMappings() {
		return List.of(
			ClassificationTaskDocument.class,
			EquivalentConceptSetDocument.class,
			ConceptChangeDocument.class,
			DescriptionChangeDocument.class,
			RelationshipChangeDocument.class,
			ConcreteDomainChangeDocument.class
		);
	}
	
	@Override
	public String getToolingId() {
		return SnomedTerminologyComponentConstants.TOOLING_ID;
	}
	
}
