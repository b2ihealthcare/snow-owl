/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;

/**
 * @since 6.0.0
 */
final class Rf2ConceptContentType implements Rf2ContentType<SnomedConcept> {

	@Override
	public SnomedConcept create() {
		return new SnomedConcept();
	}

	@Override
	public void resolve(SnomedConcept component, String[] values) {
		component.setDefinitionStatus(Concepts.PRIMITIVE.equals(values[4]) ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
		component.setSubclassDefinitionStatus(SubclassDefinitionStatus.NON_DISJOINT_SUBCLASSES);
	}

	@Override
	public String getContainerId(String[] values) {
		return IComponent.ROOT_ID;
	}

	@Override
	public String[] getHeaderColumns() {
		return SnomedRf2Headers.CONCEPT_HEADER;
	}

	@Override
	public String getType() {
		return "concept";
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(Long.parseLong(values[3]), Long.parseLong(values[4]));
	}

}