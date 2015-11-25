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

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange.MemberChangeKind;
import com.b2international.snowowl.snomed.datastore.index.update.AcceptabilityMembershipUpdater;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
// TODO: The process is very similar to ConceptReferringMemberChangeProcessor, maybe the two can be merged by generalizing
public class DescriptionAcceptabilityChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	private Function<CDOID, Document> documentProvider;
	
	public DescriptionAcceptabilityChangeProcessor(Function<CDOID, Document> documentProvider) {
		super("description acceptability changes");
		this.documentProvider = documentProvider;
	}
	
	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) {
		final Multimap<String, RefSetMemberChange> preferredMemberChanges = HashMultimap.create();
		final Multimap<String, RefSetMemberChange> acceptableMemberChanges = HashMultimap.create();
		
		// add active new and active dirty members
		final Iterable<SnomedLanguageRefSetMember> newAndDirtyMembers = Iterables.concat(
				getNewComponents(commitChangeSet, SnomedLanguageRefSetMember.class),
				getDirtyComponents(commitChangeSet, SnomedLanguageRefSetMember.class));

		for (SnomedLanguageRefSetMember member : newAndDirtyMembers) {
			if (member.isActive()) {
				final long refSetId = Long.parseLong(member.getRefSetIdentifierId());
				final RefSetMemberChange change = new RefSetMemberChange(refSetId, MemberChangeKind.ADDED, member.getRefSet().getType());
				registerChange(preferredMemberChanges, acceptableMemberChanges, member.getAcceptabilityId(), member.getReferencedComponentId(), change);
			}
		}
		
		// remove dirty inactive (or changed in acceptability?) members
		final Iterable<SnomedLanguageRefSetMember> dirtyMembers = getDirtyComponents(commitChangeSet, SnomedLanguageRefSetMember.class);
		
		for (SnomedLanguageRefSetMember member : dirtyMembers) {
			final Document beforeDocument = documentProvider.apply(member.cdoID());
			
			final long refSetId = Long.parseLong(member.getRefSetIdentifierId());
			final RefSetMemberChange change = new RefSetMemberChange(refSetId, MemberChangeKind.REMOVED, member.getRefSet().getType());
			
			if (beforeDocument != null) {
				final String beforeAcceptabilityId = SnomedMappings.memberAcceptabilityId().getValueAsString(beforeDocument);
				final boolean acceptabilityChanged = !member.getAcceptabilityId().equals(beforeAcceptabilityId);
				
				if (acceptabilityChanged) {
					registerChange(preferredMemberChanges, acceptableMemberChanges, beforeAcceptabilityId, member.getReferencedComponentId(), change);
				}
			}
			
			if (!member.isActive()) {
				registerChange(preferredMemberChanges, acceptableMemberChanges, member.getAcceptabilityId(), member.getReferencedComponentId(), change);				
			}
		}
		
		// remove earlier active detached members
		final Collection<CDOID> detachedComponents = getDetachedComponents(commitChangeSet, SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER);
		for (CDOID cdoid : detachedComponents) {
			final Document beforeDocument = documentProvider.apply(cdoid);
			final boolean beforeActive = BooleanUtils.valueOf(SnomedMappings.active().getValue(beforeDocument));
			
			if (beforeActive) {
				final long refSetId = SnomedMappings.memberRefSetId().getValue(beforeDocument);
				final String referencedComponentId = SnomedMappings.memberReferencedComponentId().getValueAsString(beforeDocument);
				final String beforeAcceptabilityId = SnomedMappings.memberAcceptabilityId().getValueAsString(beforeDocument);
				final RefSetMemberChange change = new RefSetMemberChange(refSetId, MemberChangeKind.REMOVED, SnomedRefSetType.LANGUAGE);
				
				registerChange(preferredMemberChanges, acceptableMemberChanges, beforeAcceptabilityId, referencedComponentId, change);
			}
		}
		
		for (String descriptionId : Iterables.concat(preferredMemberChanges.keySet(), acceptableMemberChanges.keySet())) {
			registerUpdate(descriptionId, new AcceptabilityMembershipUpdater(descriptionId, preferredMemberChanges.get(descriptionId), acceptableMemberChanges.get(descriptionId)));
		}
	}

	private void registerChange(final Multimap<String, RefSetMemberChange> preferredMemberChanges,
			final Multimap<String, RefSetMemberChange> acceptableMemberChanges, 
			final String acceptabilityId,
			final String referencedComponentId,
			final RefSetMemberChange change) {
		
		if (acceptabilityId.equals(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)) {
			preferredMemberChanges.put(referencedComponentId, change);
		} else {
			acceptableMemberChanges.put(referencedComponentId, change);
		}
	}
}
