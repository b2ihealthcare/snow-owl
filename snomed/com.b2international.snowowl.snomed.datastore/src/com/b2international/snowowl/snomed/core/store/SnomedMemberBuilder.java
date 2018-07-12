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

import static com.google.common.base.Preconditions.checkState;

import java.util.UUID;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Builder;

/**
 * @since 4.5
 */
public abstract class SnomedMemberBuilder<B extends SnomedMemberBuilder<B>> extends SnomedComponentBuilder<B, SnomedRefSetMemberIndexEntry.Builder, SnomedRefSetMemberIndexEntry> {

	private String referenceSetId;
	private String referencedComponent;
	
	protected SnomedMemberBuilder() {
		withId(UUID.randomUUID().toString());
	}
	
	/**
	 * Specifies the referenced component ID of the new reference set member.
	 * 
	 * @param referencedComponent
	 *            - the referenced component to refer to
	 * @return
	 */
	public final B withReferencedComponent(String referencedComponent) {
		this.referencedComponent = referencedComponent;
		return getSelf();
	}
	
	/**
	 * Specifies the reference set where this reference set member belongs.
	 * 
	 * @param referenceSetId
	 *            - the identifier concept ID of the reference set
	 * @return
	 */
	public final B withRefSet(String referenceSetId) {
		this.referenceSetId = referenceSetId;
		return getSelf();
	}
	
	public final SnomedRefSetMemberIndexEntry addTo(TransactionContext context) {
		final SnomedRefSetMemberIndexEntry member = build(context);
		context.add(member);
		return member;
	}
	
	@Override
	protected final Builder create() {
		return SnomedRefSetMemberIndexEntry.builder();
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	public void init(SnomedRefSetMemberIndexEntry.Builder component, TransactionContext context) {
		super.init(component, context);
		component.referencedComponentId(referencedComponent);
		final SnomedConceptDocument refSet = context.lookup(referenceSetId, SnomedConceptDocument.class);
		checkState(refSet.getRefSetType() != null, "RefSet properties are missing from identifier concept document %s", referenceSetId);
		component
			.referenceSetId(referenceSetId)
			.referencedComponentType(refSet.getReferencedComponentType())
			.referenceSetType(refSet.getRefSetType());
	}
	
}
