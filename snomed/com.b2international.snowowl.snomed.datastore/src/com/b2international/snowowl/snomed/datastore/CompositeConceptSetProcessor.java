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

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.services.ConceptSetProcessorFactory;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class CompositeConceptSetProcessor extends ConceptSetProcessor<CompositeConceptSetDefinition> {

	private final SnomedClientTerminologyBrowser terminologyBrowser;

	public CompositeConceptSetProcessor(final CompositeConceptSetDefinition conceptSetDefinition, final SnomedClientTerminologyBrowser terminologyBrowser) {
		super(conceptSetDefinition);
		this.terminologyBrowser = terminologyBrowser;
	}

	@Override
	public boolean contains(final SnomedConceptDocument concept) {
		for (final ConceptSetDefinition child : conceptSetDefinition.getChildren()) {
			if (ConceptSetProcessorFactory.createProcessor(child, terminologyBrowser).contains(concept))
				return true;
		}
		return false;
	}
	
	@Override
	public Iterator<SnomedConceptDocument> getConcepts() {
		if (conceptSetDefinition.getChildren().isEmpty()) {
			return Iterators.emptyIterator();
		}
		final List<Iterator<SnomedConceptDocument>> iterators = Lists.newArrayList();
		for (final ConceptSetDefinition childConceptSetDefinition : conceptSetDefinition.getChildren()) {
			final ConceptSetProcessor<ConceptSetDefinition> conceptSetProcessor = ConceptSetProcessorFactory.createProcessor(
					childConceptSetDefinition, terminologyBrowser);
			iterators.add(conceptSetProcessor.getConcepts());
		}
		
		return Iterators.concat(iterators.iterator());
	}
}