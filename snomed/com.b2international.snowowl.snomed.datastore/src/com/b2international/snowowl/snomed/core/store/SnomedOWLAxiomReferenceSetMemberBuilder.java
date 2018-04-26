/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.snomedrefset.SnomedAnnotationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;

/**
 * @since 6.1.0
 */
public class SnomedOWLAxiomReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedOWLAxiomReferenceSetMemberBuilder, SnomedAnnotationRefSetMember> {

	private String owlExpression;

	public SnomedOWLAxiomReferenceSetMemberBuilder withOWLExpression(final String owlExpression) {
		this.owlExpression = owlExpression;
		return getSelf();
	}

	@Override
	protected SnomedAnnotationRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedAnnotationRefSetMember();
	}

	@Override
	public void init(final SnomedAnnotationRefSetMember component, final TransactionContext context) {
		super.init(component, context);
		component.setAnnotation(owlExpression);
	}
}
