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
package com.b2international.snowowl.snomed.datastore.services;

import com.b2international.snowowl.snomed.datastore.CompositeConceptSetProcessor;
import com.b2international.snowowl.snomed.datastore.ConceptSetProcessor;
import com.b2international.snowowl.snomed.datastore.EnumeratedConceptSetProcessor;
import com.b2international.snowowl.snomed.datastore.HierarchyConceptSetProcessor;
import com.b2international.snowowl.snomed.datastore.ReferenceSetConceptSetProcessor;
import com.b2international.snowowl.snomed.datastore.RelationshipConceptSetProcessor;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;

public class ConceptSetProcessorFactory {

	@SuppressWarnings("unchecked")
	public static <T extends ConceptSetDefinition> ConceptSetProcessor<T> createProcessor(final T conceptSetDefinition,	final SnomedClientTerminologyBrowser terminologyBrowser) {
		
		if (conceptSetDefinition instanceof HierarchyConceptSetDefinition) {
			return (ConceptSetProcessor<T>) new HierarchyConceptSetProcessor((HierarchyConceptSetDefinition) conceptSetDefinition, terminologyBrowser);
		} else if (conceptSetDefinition instanceof ReferenceSetConceptSetDefinition) {
			return (ConceptSetProcessor<T>) new ReferenceSetConceptSetProcessor((ReferenceSetConceptSetDefinition) conceptSetDefinition);
		} else if (conceptSetDefinition instanceof RelationshipConceptSetDefinition) {
			return (ConceptSetProcessor<T>) new RelationshipConceptSetProcessor((RelationshipConceptSetDefinition) conceptSetDefinition);
		} else if (conceptSetDefinition instanceof EnumeratedConceptSetDefinition) {
			return (ConceptSetProcessor<T>) new EnumeratedConceptSetProcessor((EnumeratedConceptSetDefinition) conceptSetDefinition, terminologyBrowser);
		} else if (conceptSetDefinition instanceof CompositeConceptSetDefinition) {
			return (ConceptSetProcessor<T>) new CompositeConceptSetProcessor((CompositeConceptSetDefinition) conceptSetDefinition, terminologyBrowser);
		}
		
		throw new IllegalArgumentException("Unexpected concept set definition: " + conceptSetDefinition);
	}
}