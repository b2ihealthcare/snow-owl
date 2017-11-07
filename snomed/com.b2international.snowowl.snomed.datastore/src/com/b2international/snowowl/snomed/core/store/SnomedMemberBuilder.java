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

import java.util.UUID;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;

/**
 * @since 4.5
 */
public abstract class SnomedMemberBuilder<B extends SnomedMemberBuilder<B, T>, T extends SnomedRefSetMember> extends SnomedComponentBuilder<B, T> {

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
	public B withReferencedComponent(String referencedComponent) {
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
	public B withRefSet(String referenceSetId) {
		this.referenceSetId = referenceSetId;
		return getSelf();
	}
	
	public final T addTo(TransactionContext context) {
		final T component = build(context);
		final SnomedRefSet refSet = context.lookup(referenceSetId, SnomedRefSet.class);
		addToList(context, refSet, component);
		return component;
	}
	
	protected void addToList(TransactionContext context, SnomedRefSet refSet, T component) {
		((SnomedRegularRefSet) refSet).getMembers().add(component);
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	public void init(T component, TransactionContext context) {
		super.init(component, context);
		component.setReferencedComponentId(referencedComponent);
		final SnomedRefSet refSet = context.lookup(referenceSetId, SnomedRefSet.class);
		component.setRefSet(refSet);
	}
	
}
