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

import static com.b2international.commons.collect.LongSets.forEach;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.util.InternalEList;

import com.b2international.collections.longs.LongCollection;
import com.b2international.commons.collect.LongSets;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorClientService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;

/**
 * Reference set member creation strategy for query type reference sets.
 * Initializes a simple type reference set then evaluates the ESCG query tight to the 
 * query reference set member and adds all concepts as referenced components to the 
 * new simple type reference set.
 *
 */
public class SnomedQueryTypeRefSetMemberCreationStrategy implements SnomedRefSetMemberCreationStrategy {

	private final SnomedEditingContext context;
	private final SnomedQueryRefSetMember member;
	private final SnomedRegularRefSet refSet;

	public SnomedQueryTypeRefSetMemberCreationStrategy(final SnomedEditingContext context, final SnomedQueryRefSetMember member) {
		this.context = checkNotNull(context, "context");
		this.member = checkNotNull(member, "member");
		refSet = createSimpleTypeRefSet(this.context, this.member);
		this.member.setReferencedComponentId(refSet.getIdentifierId());
	}
	
	@Override
	public SnomedQueryRefSetMember getRefSetMember() {
		return member;
	}

	@Override
	public SnomedRegularRefSet getRefSet() {
		return refSet;
	}

	@Override
	public SnomedEditingContext getEditingContext() {
		return context;
	}

	@Override
	public void doCreate() {
		final String query = getRefSetMember().getQuery();
		final LongCollection conceptIds = getServiceForClass(IEscgQueryEvaluatorClientService.class).evaluateConceptIds(query);
		final Collection<SnomedRefSetMember> newMembers = newArrayList();
		forEach(conceptIds, new LongSets.LongCollectionProcedure() {
			@Override public void apply(long conceptId) {
				newMembers.add(createMember(conceptId));
			}
		});
		
		final List<SnomedRefSetMember> members = getRefSet().getMembers();
		((InternalEList<SnomedRefSetMember>) members).addAllUnique(newMembers);
	}

	private SnomedRefSetMember createMember(final long conceptId) {
		final SnomedRefSetMember newMember = getMemberFactory().createMember(context, refSet);
		newMember.setReferencedComponentId(Long.toString(conceptId));
		return newMember;
	}
	
	private SnomedRefSetMemberFactory getMemberFactory() {
		return SnomedRefSetMemberFactoryImpl.INSTANCE;
	}
	
	private SnomedRegularRefSet createSimpleTypeRefSet(final SnomedEditingContext context, final SnomedQueryRefSetMember member) {
		final SnomedRefSetEditingContext refSetEditingContext = context.getRefSetEditingContext();
		return refSetEditingContext.createSnomedSimpleTypeRefSet(tryGetRefSetLabel(member), CONCEPT);
	}

	private String tryGetRefSetLabel(SnomedQueryRefSetMember member) {
		final Iterable<SnomedSimpleTypeRefSetLabelAdapter> adapters = // 
				filter(member.eAdapters(), SnomedSimpleTypeRefSetLabelAdapter.class);
		return isEmpty(adapters) ? member.getUuid() : get(adapters, 0).getLabel();
	}
	

}