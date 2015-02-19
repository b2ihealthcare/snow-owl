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

import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.b2international.snowowl.datastore.cdo.IMutableAdapter.EMPTY_IMPL;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.isQueryType;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.util.InternalEList;

import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;

/**
 * The default reference set member creation strategy. Initialize the reference set member, then adds it to 
 * the members of the reference set.
 *
 */
public class SnomedRefSetMemberCreationStrategyImpl implements SnomedRefSetMemberCreationStrategy {

	private final SnomedEditingContext context;
	private final SnomedRefSet refSet;
	private final SnomedRefSetMember member;

	public SnomedRefSetMemberCreationStrategyImpl(final SnomedRefSetEditingContext context, final SnomedRefSet refSet, @Nullable final String referencedComponentId) {
		this(checkNotNull(context, "context").snomedEditingContext, check(refSet), referencedComponentId);
	}
	
	public SnomedRefSetMemberCreationStrategyImpl(final SnomedRefSetEditingContext context, final SnomedRefSet refSet) {
		this(checkNotNull(context, "context").snomedEditingContext, check(refSet), null);
	}
	
	public SnomedRefSetMemberCreationStrategyImpl(final SnomedEditingContext context, final SnomedRefSet refSet, @Nullable final String referencedComponentId) {
		this.context = checkNotNull(context, "context");
		this.refSet = check(refSet);
		check(this.context.getTransaction());
		member = createMember(this.context, this.refSet, referencedComponentId);
	}
	
	@Override
	public SnomedRefSetMember getRefSetMember() {
		return member; 
	}

	@Override
	public void doCreate() {
		
		if (refSet instanceof SnomedRegularRefSet) {
			final List<SnomedRefSetMember> members = ((SnomedRegularRefSet) refSet).getMembers();
			((InternalEList<SnomedRefSetMember>) members).addUnique(member);
		}
		
		if (isQueryType(refSet)) {
			new SnomedQueryTypeRefSetMemberCreationStrategy(context, (SnomedQueryRefSetMember) getRefSetMember()).doCreate();
		}
		
		// Send signal for refresh
		refSet.eNotify(EMPTY_IMPL);
	}

	@Override
	public SnomedRefSet getRefSet() {
		return refSet;
	}

	@Override
	public SnomedEditingContext getEditingContext() {
		return context;
	}
	
	protected SnomedRefSetMemberFactory getMemberFactory() {
		return SnomedRefSetMemberFactoryImpl.INSTANCE;
	}
	
	protected SnomedRefSetMember createMember(final SnomedEditingContext context, final SnomedRefSet refSet, final String referencedComponentId) {
		final SnomedRefSetMember newMember = getMemberFactory().createMember(context, refSet);
		newMember.setReferencedComponentId(referencedComponentId);
		return newMember;
	}

}