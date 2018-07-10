/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * @since 4.5
 */
public final class SnomedConceptBuilder extends SnomedComponentBuilder<SnomedConceptBuilder, SnomedConceptDocument.Builder, SnomedConceptDocument> {

	private DefinitionStatus definitionStatus = DefinitionStatus.PRIMITIVE;
	private boolean exhaustive = false;

	/**
	 * Specifies the exhaustive flag to use for the new concept.
	 * 
	 * @param exhaustive
	 * @return
	 */
	public final SnomedConceptBuilder withExhaustive(boolean exhaustive) {
		this.exhaustive = exhaustive;
		return getSelf();
	}

	/**
	 * Specifies the {@link DefinitionStatus} to use for the new concept.
	 * 
	 * @param definitionStatus
	 *            - the definition status to use
	 * @return
	 */
	public final SnomedConceptBuilder withDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
		return getSelf();
	}

	@Override
	protected SnomedConceptDocument.Builder create() {
		return SnomedConceptDocument.builder();
	}

	@Override
	public void init(SnomedConceptDocument.Builder component, TransactionContext context) {
		super.init(component, context);
		// check that the definitionStatus concept does exist before using it in this concept
		context.lookup(definitionStatus.getConceptId(), SnomedConceptDocument.class);
		component.primitive(definitionStatus.isPrimitive());
		component.exhaustive(exhaustive);
	}

}
