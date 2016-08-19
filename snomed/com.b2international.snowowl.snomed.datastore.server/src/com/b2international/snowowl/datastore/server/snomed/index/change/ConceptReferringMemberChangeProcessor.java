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
package com.b2international.snowowl.datastore.server.snomed.index.change;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange.MemberChangeKind;
import com.b2international.snowowl.snomed.datastore.index.update.ReferenceSetMembershipUpdater;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
public class ConceptReferringMemberChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	private static final Predicate<SnomedRefSetMember> REFERRING_CONCEPT_MEMBER = new Predicate<SnomedRefSetMember>() {
		@Override
		public boolean apply(SnomedRefSetMember input) {
			return SnomedTerminologyComponentConstants.CONCEPT_NUMBER == input.getReferencedComponentType() && isValidType(input.getRefSet().getType());
		}
	};
	
	private static boolean isValidType(final SnomedRefSetType type) {
		return SnomedRefSetType.SIMPLE.equals(type) || SnomedRefSetType.ATTRIBUTE_VALUE.equals(type) || SnomedRefSetType.SIMPLE_MAP.equals(type);
	}
	
	private Function<CDOID, Document> documentProvider;
	
	public ConceptReferringMemberChangeProcessor(Function<CDOID, Document> documentProvider) {
		super("referring member changes");
		this.documentProvider = documentProvider;
	}
	
	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) {
		final Multimap<String, RefSetMemberChange> memberChanges = HashMultimap.create();
		
		// process active new and dirty
		final Iterable<SnomedRefSetMember> newReferringMembers = FluentIterable
				.from(Iterables.concat(commitChangeSet.getNewComponents(), commitChangeSet.getDirtyComponents())).filter(SnomedRefSetMember.class)
				.filter(REFERRING_CONCEPT_MEMBER).toSet();

		
		for (SnomedRefSetMember member : newReferringMembers) {
			if (member.isActive()) {
				addChange(memberChanges, member, MemberChangeKind.ADDED);
			}
		}
		
		// process dirty inactive members
		final Iterable<SnomedRefSetMember> dirtyReferringMembers = FluentIterable
				.from(getDirtyComponents(commitChangeSet, SnomedRefSetMember.class))
				.filter(REFERRING_CONCEPT_MEMBER).toSet();
		
		for (SnomedRefSetMember member : dirtyReferringMembers) {
			if (!member.isActive()) {
				addChange(memberChanges, member, MemberChangeKind.REMOVED);
			}
		}
		
		// process detached
		final Collection<CDOID> detachedComponents = getDetachedComponents(commitChangeSet, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER);
		for (CDOID cdoid : detachedComponents) {
			final Document doc = documentProvider.apply(cdoid);
			final boolean active = SnomedMappings.active().getValue(doc) == 1;
			final SnomedRefSetType type = SnomedRefSetType.get(SnomedMappings.memberRefSetType().getValue(doc)); 
			if (active && isValidType(type)) {
				final String uuid = SnomedMappings.memberUuid().getValue(doc);
				final String referencedComponentId = SnomedMappings.memberReferencedComponentId().getValueAsString(doc);
				final long refSetId = SnomedMappings.memberRefSetId().getValue(doc);
				memberChanges.put(referencedComponentId, new RefSetMemberChange(uuid, refSetId, MemberChangeKind.REMOVED, type));
			}
		}
		
		for (String conceptId : memberChanges.keySet()) {
			registerUpdate(conceptId, new ReferenceSetMembershipUpdater(conceptId, memberChanges.get(conceptId)));
		}
	}

	private void addChange(final Multimap<String, RefSetMemberChange> memberChanges, SnomedRefSetMember member, MemberChangeKind changeKind) {
		final String uuid = member.getUuid();
		final long refSetId = Long.parseLong(member.getRefSetIdentifierId());
		final SnomedRefSetType refSetType = member.getRefSet().getType();
		memberChanges.put(member.getReferencedComponentId(), new RefSetMemberChange(uuid, refSetId, changeKind, refSetType));
	}
}
