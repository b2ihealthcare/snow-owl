/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import java.util.Iterator;
import java.util.List;

import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ConceptSetProcessorFactory;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class CompositeConceptSetProcessor extends ConceptSetProcessor<CompositeConceptSetDefinition> {

	private final SnomedClientTerminologyBrowser terminologyBrowser;
	private final SnomedClientRefSetBrowser refSetBrowser;
	private final SnomedClientIndexService indexService;

	public CompositeConceptSetProcessor(final CompositeConceptSetDefinition conceptSetDefinition, final SnomedClientTerminologyBrowser terminologyBrowser, final SnomedClientRefSetBrowser refSetBrowser,
			final SnomedClientIndexService indexService) {
		super(conceptSetDefinition);
		this.terminologyBrowser = terminologyBrowser;
		this.refSetBrowser = refSetBrowser;
		this.indexService = indexService;
	}

	@Override
	public boolean contains(final SnomedConceptIndexEntry concept) {
		for (final ConceptSetDefinition child : conceptSetDefinition.getChildren()) {
			if (ConceptSetProcessorFactory.createProcessor(child, terminologyBrowser, refSetBrowser, indexService).contains(concept))
				return true;
		}
		return false;
	}
	
	@Override
	public Iterator<SnomedConceptIndexEntry> getConcepts() {
		if (conceptSetDefinition.getChildren().isEmpty()) {
			return Iterators.emptyIterator();
		}
		final List<Iterator<SnomedConceptIndexEntry>> iterators = Lists.newArrayList();
		for (final ConceptSetDefinition childConceptSetDefinition : conceptSetDefinition.getChildren()) {
			final ConceptSetProcessor<ConceptSetDefinition> conceptSetProcessor = ConceptSetProcessorFactory.createProcessor(
					childConceptSetDefinition, terminologyBrowser, refSetBrowser, indexService);
			iterators.add(conceptSetProcessor.getConcepts());
		}
		
		return Iterators.concat(iterators.iterator());
	}
}