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
package com.b2international.snowowl.snomed.reasoner;

import java.util.concurrent.TimeUnit;

import com.b2international.index.Index;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.PostRunCapableBootstrapFragment;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationTracker;

/**
 * @since 7.0
 */
public final class SnomedReasonerBootstrap extends DefaultBootstrapFragment implements PostRunCapableBootstrapFragment {

	@Override
	public void postRun(SnowOwlConfiguration configuration, Environment env) {
		
		if (env.isServer() || env.isEmbedded()) {
			
			final RepositoryManager repositoryManager = env.service(RepositoryManager.class);
			final Repository repository = repositoryManager.get(SnomedDatastoreActivator.REPOSITORY_UUID);
			final Index repositoryIndex = repository.service(Index.class);
			
			final SnomedCoreConfiguration snomedConfig = configuration.getModuleConfig(SnomedCoreConfiguration.class);
			final int maximumReasonerRuns = snomedConfig.getMaxReasonerRuns();
			final long cleanUpInterval = TimeUnit.MINUTES.toMillis(5L); // TODO: make this configurable
			final ClassificationTracker classificationTracker = new ClassificationTracker(repositoryIndex, maximumReasonerRuns, cleanUpInterval);
			
			env.services().registerService(ClassificationTracker.class, classificationTracker);
		}
		
	}
}
