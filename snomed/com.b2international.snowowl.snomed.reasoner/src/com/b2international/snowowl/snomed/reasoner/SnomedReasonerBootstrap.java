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

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerServerService;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationRepository;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationRunDocument;
import com.b2international.snowowl.snomed.reasoner.index.ConcreteDomainChangeDocument;
import com.b2international.snowowl.snomed.reasoner.index.EquivalentConceptSetDocument;
import com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument;
import com.b2international.snowowl.snomed.reasoner.ontology.SnomedOntologyService;
import com.b2international.snowowl.snomed.reasoner.preferences.IReasonerPreferencesService;
import com.b2international.snowowl.snomed.reasoner.preferences.ReasonerPreferencesService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.0
 */
public final class SnomedReasonerBootstrap extends Plugin {

	@Override
	public void run(final SnowOwlConfiguration configuration, final Environment env) throws Exception {
		if (env.isServer() || env.isEmbedded()) {

			env.services().registerService(SnomedOntologyService.class, new SnomedOntologyService());
			env.services().registerService(IReasonerPreferencesService.class, new ReasonerPreferencesService());

			final SnomedCoreConfiguration snomedConfig = configuration.getModuleConfig(SnomedCoreConfiguration.class);
			final int maximumReasonerCount = snomedConfig.getMaxReasonerCount();
			final int maximumTaxonomiesToKeep = snomedConfig.getMaxReasonerResults();
			final SnomedReasonerServerService reasonerService = new SnomedReasonerServerService(maximumReasonerCount, maximumTaxonomiesToKeep);
			env.services().registerService(SnomedReasonerService.class, reasonerService);
			reasonerService.registerListeners();
			
			final Mappings mappings = new Mappings(ClassificationRunDocument.class, 
					EquivalentConceptSetDocument.class,
					RelationshipChangeDocument.class,
					ConcreteDomainChangeDocument.class);
			final Index classificationIndex = Indexes.createIndex("classifications", 
					env.service(ObjectMapper.class), 
					mappings, 
					env.service(IndexSettings.class));
			final ClassificationRepository classificationRepository = new ClassificationRepository(classificationIndex);
			env.services().registerService(ClassificationRepository.class, classificationRepository);
		}
	}
}
